package org.example.eksamenbackend.controller;

import org.example.eksamenbackend.dto.DroneRequestDTO;
import org.example.eksamenbackend.dto.DroneResponseDTO;
import org.example.eksamenbackend.model.DroneStatus;
import org.example.eksamenbackend.service.DroneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:63342")
@RequestMapping("/drones")
public class DroneController {

    private final DroneService droneService;

    public DroneController(DroneService droneService) {
        this.droneService = droneService;
    }


    @GetMapping
    public List<DroneResponseDTO> getAllDrones() {
        return droneService.getAllDrones();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DroneResponseDTO> getDroneById(@PathVariable int id) {
        try {
            DroneResponseDTO drone = droneService.getDroneById(id);
            return new ResponseEntity<>(drone, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<DroneResponseDTO> addDrone(@RequestBody DroneRequestDTO requestDTO) {
        try {
            DroneResponseDTO newDrone = droneService.addDrone(requestDTO);
            return new ResponseEntity<>(newDrone, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<DroneResponseDTO> updateDrone(@PathVariable int id, @RequestBody DroneRequestDTO droneRequestDTO) {
        try {
            DroneResponseDTO updatedDrone = droneService.updateDrone(id, droneRequestDTO);
            return new ResponseEntity<>(updatedDrone, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrone(@PathVariable int id) {
        try {
            droneService.deleteDrone(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/enable/{id}")
    public ResponseEntity<DroneResponseDTO> enableDrone(@PathVariable int id) {
        try {
            DroneResponseDTO updatedDrone = droneService.changeDroneStatus(id, DroneStatus.I_DRIFT);
            return new ResponseEntity<>(updatedDrone, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/disable/{id}")
    public ResponseEntity<DroneResponseDTO> disableDrone(@PathVariable int id) {
        try {
            DroneResponseDTO updatedDrone = droneService.changeDroneStatus(id, DroneStatus.UDE_AF_DRIFT);
            return new ResponseEntity<>(updatedDrone, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/retire/{id}")
    public ResponseEntity<DroneResponseDTO> retireDrone(@PathVariable int id) {
        try {
            DroneResponseDTO updatedDrone = droneService.changeDroneStatus(id, DroneStatus.UDFASET);
            return new ResponseEntity<>(updatedDrone, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
