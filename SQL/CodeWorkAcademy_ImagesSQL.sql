create database codeworkimagesDB;
use codeworkimagesDB;
select * from user_data;
select * from meme;
select * from image;

delete from image where caption = 'flower_cap';
insert into image ( caption, imgname, imgsrc)
values ( 'flower_cap', ' flower-name' ,
 'https://static.pexels.com/photos/36753/flower-purple-lical-blosso.jpg');
 
 
insert into meme (bottom, image_url, top, user_id)
values('bot_img', 'https://static.pexels.com/photos/36753/flower-purple-lical-blosso.jpg', 'top_img', 1);
