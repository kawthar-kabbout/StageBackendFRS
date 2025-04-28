package com.stage.services;

import com.stage.persistans.CapabilityMachine;
import com.stage.repositories.CapabilityMachineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryTypeService {
    private final CapabilityMachineRepository categoryTypeRepository;


    public List<CapabilityMachine> findAll() {
        return categoryTypeRepository.findAll();
    }
    public CapabilityMachine findById(Long id) {
        return categoryTypeRepository.findById(id).orElse(null);
    }
    public CapabilityMachine save(CapabilityMachine categoryType) {
        return categoryTypeRepository.save(categoryType);
    }

    public void delete(CapabilityMachine categoryType) {
        categoryTypeRepository.delete(categoryType);
    }

    public CapabilityMachine update(CapabilityMachine categoryType) {
        return categoryTypeRepository.save(categoryType);
    }
    public Boolean delete (Long id) {
        CapabilityMachine cap = categoryTypeRepository.findById(id).orElse(null);
        if (cap != null) {
            cap.setArchived(1);
            categoryTypeRepository.save(cap);
            return true;
        }return  false;


    }
}
