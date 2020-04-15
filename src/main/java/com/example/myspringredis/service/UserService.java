package com.example.myspringredis.service;

import com.example.myspringredis.entity.User;

public interface UserService {

    User save(User user);

    void delete(int id);

    User get(Integer id);
}