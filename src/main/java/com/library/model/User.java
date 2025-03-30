package com.library.model;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String role;
    private double fine;

    // Constructor
    public User(int id, String name, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.fine = 0.0;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public double getFine() { return fine; }
    public void setFine(double fine) { this.fine = fine; }

    public boolean isAdmin(){
        return this.role.equals("admin");
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "', role='" + role + "', fine=" + fine + "}";
    }
}
