package com.safeher.controller;

import com.safeher.model.EmergencyContact;
import com.safeher.repository.EmergencyContactRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin
public class EmergencyContactController {

    @Autowired
    private EmergencyContactRepository repository;


    // Add contact
    @PostMapping("/add")
    public EmergencyContact addContact(@RequestBody EmergencyContact contact){

        return repository.save(contact);

    }


    // Get all contacts
    @GetMapping("/all")
    public List<EmergencyContact> getAllContacts(){

        return repository.findAll();

    }


    // Delete contact
    @DeleteMapping("/delete/{id}")
public void deleteContact(@PathVariable Long id){
    repository.deleteById(id);
}

}