package com.uit.flowerstore.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.uit.flowerstore.domain.Flower;

import com.uit.flowerstore.repository.FlowerRepository;
import com.uit.flowerstore.services.FlowerService;
import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
@RequestMapping("/api")

@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")


public class FlowerController {
	
	private final FlowerService flowerService;
	@Autowired
	FlowerRepository flowerRepository;
    
    public FlowerController(FlowerService flowerService) {
        this.flowerService= flowerService; 
    }
    
    @PostMapping("/flowers")
    public ResponseEntity<Flower> createFlower(@RequestBody Flower flower) {
        Flower createdFlower = flowerService.createFlower(flower);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFlower);
    }
    


    
    @GetMapping("/flowers")
    public List<Flower> getAllFlowers() {
		return flowerRepository.findAll();
	}
    


	
	
	

}
