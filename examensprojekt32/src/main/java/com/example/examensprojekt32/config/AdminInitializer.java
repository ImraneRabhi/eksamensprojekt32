package com.example.examensprojekt32.config;

import com.example.examensprojekt32.model.Admin;
import com.example.examensprojekt32.repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminInitializer(AdminRepository adminRepository, BCryptPasswordEncoder passwordEncoder){

        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args){
        if(adminRepository.findByUsername("admin").isEmpty()){
            Admin admin = new Admin("admin", passwordEncoder.encode("admin123"));
            adminRepository.save(admin);
        }
    }
}
