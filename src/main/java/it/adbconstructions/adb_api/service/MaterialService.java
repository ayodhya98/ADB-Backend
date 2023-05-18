package it.adbconstructions.adb_api.service;

import it.adbconstructions.adb_api.exception.*;
import it.adbconstructions.adb_api.model.Material;
import it.adbconstructions.adb_api.model.User;
import it.adbconstructions.adb_api.repository.MaterialRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static it.adbconstructions.adb_api.common.constant.UserImplementation.*;
import static it.adbconstructions.adb_api.common.constant.UserImplementation.EMAIL_ALREADY_EXISTS;

@Service
@Transactional
public class MaterialService {

    @Autowired
    private MaterialRepository materialRepository;

    public List<Material> getMaterials()
    {
        return materialRepository.findAll();
    }

    public Material findByCode(String code) throws MaterialNotFoundException {
        Material material = materialRepository.findByCode(code);
        if (material != null)
        {
            return material;
        }
        else {
            throw new MaterialNotFoundException("Material already exist by the code: " + code);
        }
    }

    public void deleteMaterial(String code) throws MaterialNotFoundException {
        Material material = findByCode(code);
        materialRepository.delete(material);
    }

    public Material createMaterial(String code,String name,String currency, Double currentPrice) throws MaterialAlreadyExistException {
        Material materialByCode = materialRepository.findByCode(code);
        Material materialByName = materialRepository.findByName(name);

        if (materialByName == null && materialByCode == null)
        {
            Material newMaterial = new Material();
            newMaterial.setCode(code);
            newMaterial.setName(name);
            newMaterial.setLastUpdateOn(new Date());
            newMaterial.setCurrency(currency);
            newMaterial.setCurrentPrice(currentPrice);
            newMaterial.setAnalysisAvailability(Arrays.asList(ANALYSIS_AVAILABLE_MATERIALS).contains(code));
            return materialRepository.save(newMaterial);
        }
        else
        {
            if (materialByCode != null)
            {
                throw new MaterialAlreadyExistException("Material already exist by the code: " + code);
            }
            else {
                throw new MaterialAlreadyExistException("Material already exist by the name: " + name);
            }
        }
    }

    public Material updateMaterial(String code,String newCode,String newName,String currency, Double currentPrice) throws MaterialNotFoundException, MaterialAlreadyExistException {
        Material material = validateNewCodeAndName(code,newCode,newName);
        material.setCode(newCode);
        material.setName(newName);
        material.setLastUpdateOn(new Date());
        material.setCurrency(currency);
        material.setCurrentPrice(currentPrice);
        if (Arrays.asList(ANALYSIS_AVAILABLE_MATERIALS).contains(code))
               {
                   material.setAnalysisAvailability(true);
               }
        return materialRepository.save(material);
    }

    private Material validateNewCodeAndName(String currentCode, String newCode, String newName) throws MaterialNotFoundException, MaterialAlreadyExistException {
        Material materialByCode = materialRepository.findByCode(newCode);
        Material materialByName = materialRepository.findByName(newName);
        if (StringUtils.isNotBlank(currentCode)) {
            Material current = materialRepository.findByCode(currentCode);
            if (current == null) {
                throw new MaterialNotFoundException("Material not found by code: " + currentCode);
            }
            if (materialByCode != null && !current.getId().equals(materialByCode.getId())) {
                throw new MaterialAlreadyExistException("Material already exist by the code: " + newCode);
            }
            if (materialByName != null && !current.getId().equals(materialByName.getId())) {
                throw new MaterialAlreadyExistException("Material already exist by the name: " + newName);
            }
            return current;
        } else {
            if (materialByCode != null) {
                throw new MaterialAlreadyExistException("Material already exist by the code: " + newCode);
            }
            if (materialByName != null) {
                throw new MaterialAlreadyExistException("Material already exist by the name: " + newName);
            }
            return null;
        }
    }
}
