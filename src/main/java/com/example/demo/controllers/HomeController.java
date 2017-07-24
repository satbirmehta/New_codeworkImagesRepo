package com.example.demo.controllers;

import com.cloudinary.utils.ObjectUtils;
import com.example.demo.configs.CloudinaryConfig;
import com.example.demo.models.*;
import com.example.demo.repositories.ImageRepository;
import com.example.demo.repositories.MemeRepository;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.UserService;
import com.example.demo.validators.UserValidator;
import com.google.common.collect.Lists;
import it.ozimov.springboot.mail.model.Email;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import it.ozimov.springboot.mail.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.internet.InternetAddress;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by student on 7/10/17.
 */
@Controller
public class HomeController {

    @Autowired
    private UserValidator userValidator;
    @Autowired
    private UserService userService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    MemeRepository memeRepository;


    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String home(Model model){

        return "home";
    }

    @GetMapping("/upload")
    public String uploadForm(Model model){
        model.addAttribute("image", new Image());
        return "upload";
    }
    @PostMapping("/upload")
    public String singleImageUpload(@RequestParam("file") MultipartFile file,  RedirectAttributes redirectAttributes,@ModelAttribute Image image, Model model){
        if (file.isEmpty()){
            model.addAttribute("message","Please select a file to upload");
            return "upload";
        }
        try {
            Map uploadResult =  cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
            model.addAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'");
            model.addAttribute("imageurl", uploadResult.get("url"));
            String filename = uploadResult.get("public_id").toString() + "." + uploadResult.get("format").toString();
            model.addAttribute("sizedimageurl", cloudc.createUrl(filename,300,400, "scale"));
            image.setImgname(filename);
            image.setImgsrc((String)  cloudc.createUrl(filename,300,400, "scale"));
            imageRepository.save(image);
            model.addAttribute("imageList", imageRepository.findAll());
        } catch (IOException e){
            e.printStackTrace();
            model.addAttribute("message", "Sorry I can't upload that!");
        }
        return "upload";
    }

    @RequestMapping("/login")
    public String login(){
        return "login";

    }

    @RequestMapping(value="/register", method = RequestMethod.GET)
    public String showRegistrationPage(Model model){
        model.addAttribute("user", new User());
        return "registration";
    }
    @RequestMapping(value="/register", method = RequestMethod.POST)
    public String processRegistrationPage(@Valid @ModelAttribute("user") User user, BindingResult result, Model model){
        model.addAttribute("user", user);
        userValidator.validate(user, result);
        if (result.hasErrors()) {
            return "registration";
        }
        if(user.getRoleName().equals("user"))
        {
            userService.saveUser(user);
            model.addAttribute("message", "User Account Successfully Created");
        }
        if(user.getRoleName().equals("admin"))
        {
            userService.saveAdmin(user);
            model.addAttribute("message", "Admin Account Successfully Created");
        }
        return "home";
    }
    public UserValidator getUserValidator() {
        return userValidator;
    }
    public void setUserValidator(UserValidator userValidator) {
        this.userValidator = userValidator;
    }

    @GetMapping("/meme_maker")
    public String memeMaker(Model model) {
        model.addAttribute("meme", new Meme());
        model.addAttribute("images", imageRepository.findAll());
        return "meme_maker";
    }
    @RequestMapping("/makememe/{id}")
    public String memeform(@PathVariable("id") int id, Model model)
    {
        Image img=imageRepository.findOne(id);
        Meme meme=new Meme();
        meme.setImageUrl(img.getImgsrc());
        model.addAttribute("meme", meme);


        return "meme_maker_maker";
    }


    @PostMapping("/new_meme")
    public String addMeme(@Valid @ModelAttribute("meme")Meme meme, BindingResult result, Model model) {
        model.addAttribute("meme", meme);
        userValidator.validateCaptions(meme, result);
        if (result.hasErrors()) {
            return "meme_maker_maker";
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user=userRepository.findByUsername(username);
        Long id = user.getId();
        meme.setUserId(id.intValue());
        memeRepository.save(meme);
        meme=memeRepository.findTop1ByUserIdOrderByIdDesc((int) user.getId()).get(0);
        sendEmailWithoutTemplating(user,meme);
        return "redirect:/memes";
    }

    @RequestMapping("/memes")
    public String viewMemes(Model model) {
        //Find all by username
        /*String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer id =  userRepository.findByUsername(username).getId();
        model.addAttribute("memeList", memeRepository.findAllByUserId(id.intValue()));
        */
        //Find all
        model.addAttribute("memeList", memeRepository.findAll());
        return "viewmemes";
    }
    @RequestMapping("/showmemes/{id}")
    public String showMeme(@PathVariable("id") long id, Model model)
    {
        Meme meme=memeRepository.findOne(id);
        model.addAttribute("meme",meme);

        return "memez";
    }

    @Autowired
    public EmailService emailService;
    public void sendEmailWithoutTemplating(User user, Meme meme){
        final Email email;
        try {
            email = DefaultEmail.builder()
                    .from(new InternetAddress("dolly9979@gmail.com", "The MemeLord"))
                    .to(Lists.newArrayList(new InternetAddress(user.getEmail(),user.getUsername())))
                    .subject("Your Meme, Your Way")
                    .body("You have created a new meme. Here is the link: memez-memez.herokuapp.com/showmemes/"+meme.getId())
                    .encoding("UTF-8").build();
            emailService.send(email);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
