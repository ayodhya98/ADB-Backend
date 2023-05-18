package it.adbconstructions.adb_api.controller;

import it.adbconstructions.adb_api.exception.ExceptionHandling;
import it.adbconstructions.adb_api.exception.MaterialAlreadyExistException;
import it.adbconstructions.adb_api.exception.MaterialNotFoundException;
import it.adbconstructions.adb_api.model.HttpResponse;
import it.adbconstructions.adb_api.model.Material;
import it.adbconstructions.adb_api.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = {"/", "/materials"})
public class MaterialController extends ExceptionHandling {

    @Autowired
    private MaterialService materialService;

    @PostMapping("/create")
    public ResponseEntity<Material> create(@RequestBody Material material) throws MaterialAlreadyExistException {
        Material newMaterial = materialService.createMaterial(material.getCode(),material.getName(),material.getCurrency(),material.getCurrentPrice());
        return new ResponseEntity<>(newMaterial, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Material> update(@RequestParam("currentCode") String currentCode,
                                           @RequestParam("newCode") String newCode,
                                           @RequestParam("newName") String newName,
                                           @RequestParam("currency") String currency,
                                           @RequestParam("newPrice") Double newPrice
                               ) throws MaterialAlreadyExistException, MaterialNotFoundException {
        Material newMaterial = materialService.updateMaterial(currentCode,newCode,newName,currency,newPrice);
        return new ResponseEntity<>(newMaterial, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Material>> getAll()  {
        List<Material> newMaterials = materialService.getMaterials();
        return new ResponseEntity<>(newMaterials, HttpStatus.OK);
    }

    @GetMapping("/{code}")
    public ResponseEntity<Material> getByCode(@PathVariable("code") String code) throws MaterialNotFoundException {
        Material newMaterial = materialService.findByCode(code);
        return new ResponseEntity<>(newMaterial, HttpStatus.OK);
    }

    @GetMapping("/delete/{code}")
    public ResponseEntity<HttpResponse> delete(@PathVariable("code") String code) throws MaterialNotFoundException {
        materialService.deleteMaterial(code);
        return response(HttpStatus.OK, "Item Successfully Deleted.");
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        HttpResponse body = new HttpResponse(httpStatus.value(),httpStatus,httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
        return new ResponseEntity<>(body, httpStatus);
    }
}
