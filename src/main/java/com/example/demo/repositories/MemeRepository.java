package com.example.demo.repositories;

import com.example.demo.models.Meme;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by student on 7/10/17.
 */
public interface MemeRepository extends CrudRepository<Meme,Long> {

    public Iterable<Meme> findAllByUserId(int userId);

    public List<Meme> findTop1ByUserIdOrderByIdDesc(int userId);

}

