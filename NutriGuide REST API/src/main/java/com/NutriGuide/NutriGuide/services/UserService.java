package com.NutriGuide.NutriGuide.services;

import java.util.List;

import com.NutriGuide.NutriGuide.entities.Status;
import com.NutriGuide.NutriGuide.entities.User;


public interface UserService {

	public List<User> getUser();

	public User registerUser(User newUser);

	public User loginUser(User user);
}
