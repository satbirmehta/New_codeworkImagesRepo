package com.example.demo.repositories;

/**
 * Created by student on 7/5/17.
 */

import com.example.demo.models.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role,Long>
{
    public Role findByRole(String role);
}
