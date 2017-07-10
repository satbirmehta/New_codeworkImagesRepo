package com.example.demo.repositories;

import com.example.demo.models.Meme;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by student on 7/10/17.
 */
public interface MemeRepository extends CrudRepository<Meme,Long> {

    public Iterable<Meme> findAllByUserId(int userId);

}

