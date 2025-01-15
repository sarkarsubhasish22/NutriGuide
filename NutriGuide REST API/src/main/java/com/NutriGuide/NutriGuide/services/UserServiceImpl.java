package com.NutriGuide.NutriGuide.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.NutriGuide.NutriGuide.dao.UserDao;
import com.NutriGuide.NutriGuide.entities.Status;
import com.NutriGuide.NutriGuide.entities.User;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserDao userDao;
//	private User nullloginuser = null;
//	private User loginuser = null;
	private User nullobject = null;

	@Override
	public List<User> getUser() {
		
		return userDao.findAll();
	}

	@Override
	public User registerUser(User newUser) {
		List<User> users = userDao.findAll();
        System.out.println("New user: " + newUser.toString());
        for (User user : users) {
            System.out.println("Registered user: " + newUser.toString());
            if (user.equals(newUser)) {
                System.out.println("User Already exists!");
                return newUser;
            }
        }
        userDao.save(newUser);
        System.out.println(newUser);
        return newUser;
	}

	@Override
	public User loginUser(User user) {
		
		List<User> users = userDao.findAll();
        for (User other : users) {
            if (other.equals(user)) {
                ((User) user).setLoggedIn(true);

//                long userId =  userDao.getUserIdByEmail(user.getEmail());
//                User loginuser = userDao.findById(userId);
                return other;
            }
        }
        return null;
	}


	}
