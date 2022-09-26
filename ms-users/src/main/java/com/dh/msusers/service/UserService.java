package com.dh.msusers.service;

import com.dh.msusers.model.Bills;
import com.dh.msusers.model.User;
import com.dh.msusers.repository.IBillsFeignRepository;
import com.dh.msusers.repository.IUserRepository;
import com.dh.msusers.repository.KeycloakUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private KeycloakUserRepository userRepository;

    @Autowired
    private IBillsFeignRepository billsFeignRepository;

    public User findBillByUserId(String userId) {
        User user = userRepository.findById(userId);
        user.setBills(billsFeignRepository.getBills(user.getId()));
        return user;
    }

    public List<Bills> getAllBills() {
        return billsFeignRepository.getAll();
    }


}
