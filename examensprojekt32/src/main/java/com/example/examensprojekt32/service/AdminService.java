package com.example.examensprojekt32.service;

import com.example.examensprojekt32.model.Admin;
import com.example.examensprojekt32.repository.AdminRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository,BCryptPasswordEncoder passwordEncoder){

        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Admin login(String username, String password){
        Admin admin = adminRepository.findByUsername(username).orElseThrow(()-> new IllegalArgumentException("Forkert brugernavn eller adgangskode."));


        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new IllegalArgumentException(
                    "Forkert brugernavn eller adgangskode.");
        }

        return admin;
    }
}

