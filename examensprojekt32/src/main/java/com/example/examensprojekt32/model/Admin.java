package com.example.examensprojekt32.model;

import jakarta.persistence.*;

@Entity
public class Admin {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   @Column(unique = true, nullable = false)
    private String username;

   @Column(nullable = false)
    private String password;

   public Admin(){}

    public Admin(String username, String password){
       this.username = username;
       this.password = password;
    }

    private Long getId(){
       return id;
    }

    public String getUsername(){
       return username;
    }

    public String getPassword(){
       return password;
    }


}
