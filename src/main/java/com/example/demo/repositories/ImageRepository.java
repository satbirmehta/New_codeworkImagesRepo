package com.example.demo.repositories;

import com.example.demo.models.Image;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by student on 7/10/17.
 */
public interface ImageRepository extends CrudRepository<Image,Integer> {

}
