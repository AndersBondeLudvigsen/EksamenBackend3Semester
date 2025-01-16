package org.example.eksamenbackend.controller;

import org.example.eksamenbackend.dto.LeveringsRequestDTO;
import org.example.eksamenbackend.dto.LeveringsResponseDTO;
import org.example.eksamenbackend.service.LeveringsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:63342")

@RequestMapping("/deliveries")
public class LeveringsController {

    private final LeveringsService leveringsService;

    public LeveringsController(LeveringsService leveringsService) {
        this.leveringsService = leveringsService;
    }

    @GetMapping
    public List<LeveringsResponseDTO> getAllPendingDeliveries() {
        return leveringsService.getAllPendingDeliveries();
    }

    @PostMapping
    public ResponseEntity<LeveringsResponseDTO> addDelivery(@RequestBody LeveringsRequestDTO requestDTO) {
        try {
            LeveringsResponseDTO newDelivery = leveringsService.addDelivery(requestDTO);
            return new ResponseEntity<>(newDelivery, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/queue")
    public List<LeveringsResponseDTO> getQueuedDeliveries() {
        return leveringsService.getQueuedDeliveries();
    }

    @PutMapping("/schedule/{leveringId}")
    public ResponseEntity<LeveringsResponseDTO> scheduleDelivery(@PathVariable int leveringId, @RequestParam(required = false) Integer droneId) {
        try {
            LeveringsResponseDTO scheduledDelivery = leveringsService.scheduleDelivery(leveringId, droneId);
            return new ResponseEntity<>(scheduledDelivery, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @PutMapping("/finish/{leveringId}")
    public ResponseEntity<LeveringsResponseDTO> finishDelivery(@PathVariable int leveringId) {
        try {
            LeveringsResponseDTO finishedDelivery = leveringsService.finishDelivery(leveringId);
            return new ResponseEntity<>(finishedDelivery, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/simulate/add")
    public ResponseEntity<LeveringsResponseDTO> simulateAddDelivery(@RequestBody LeveringsRequestDTO requestDTO) {
        try {
            LeveringsResponseDTO newDelivery = leveringsService.simulateAddDelivery(requestDTO);
            return new ResponseEntity<>(newDelivery, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
