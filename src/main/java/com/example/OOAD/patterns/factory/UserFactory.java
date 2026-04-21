package com.example.OOAD.patterns.factory;

import com.example.OOAD.Instructor;
import com.example.OOAD.Users;

public class UserFactory {

    public static Users createStudent(String fname, String lname,
                                       String email, String password,
                                       String role) {
        Users user = new Users();
        user.setFname(fname);
        user.setLname(lname);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role.toLowerCase());
        return user;
    }

    public static Instructor createInstructor(String fname, String lname,
                                               String email, String password) {
        Instructor instructor = new Instructor();
        instructor.setFname(fname);
        instructor.setLname(lname);
        instructor.setEmail(email);
        instructor.setPassword(password);
        return instructor;
    }
    
    public static Object create(String fname, String lname,
                                 String email, String password,
                                 String role) {
        if ("instructor".equalsIgnoreCase(role)) {
            return createInstructor(fname, lname, email, password);
        } else {
            return createStudent(fname, lname, email, password, role);
        }
    }



}
