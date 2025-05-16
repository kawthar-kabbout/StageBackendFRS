package com.stage.services;

import com.stage.dto.DailyWorkTimeDTO;
import com.stage.dto.WorkTimeDTO;
import com.stage.persistans.Activity;
import com.stage.persistans.DailyWorkTime;
import com.stage.persistans.WorkTime;
import com.stage.repositories.WorkTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkTimeService {
    private final WorkTimeRepository workTimeRepository;




    // Convertir entité -> DTO
    private DailyWorkTimeDTO convertToDailyDTO(DailyWorkTime entity) {
        DailyWorkTimeDTO dto = new DailyWorkTimeDTO();
        dto.setId(entity.getId());
        dto.setDay(entity.getDay().name());
        dto.setMorningStart(entity.getMorningStart().toString());
        dto.setMorningEnd(entity.getMorningEnd().toString());
        dto.setAfternoonStart(entity.getAfternoonStart().toString());
        dto.setAfternoonEnd(entity.getAfternoonEnd().toString());
        return dto;
    }

    // Convertir DTO -> entité
    private DailyWorkTime convertToDailyEntity(DailyWorkTimeDTO dto, Long workTimeId) {
        DailyWorkTime entity = new DailyWorkTime();
        entity.setId(dto.getId());
        entity.setDay(DayOfWeek.valueOf(dto.getDay()));
        entity.setMorningStart(LocalTime.parse(dto.getMorningStart()));
        entity.setMorningEnd(LocalTime.parse(dto.getMorningEnd()));
        entity.setAfternoonStart(LocalTime.parse(dto.getAfternoonStart()));
        entity.setAfternoonEnd(LocalTime.parse(dto.getAfternoonEnd()));
        return entity;
    }




    public WorkTime createWorkTime(WorkTime workTime) {
        return workTimeRepository.save(workTime);
    }


    public List<WorkTime> getAllWorkTimes() {
        return workTimeRepository.findAll();
    }



    public WorkTimeDTO getWorkTimeByIdDTO(Long id) {
        WorkTime workTime = workTimeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("WorkTime non trouvé avec l'ID : " + id));

        WorkTimeDTO dto = new WorkTimeDTO();
        dto.setId(workTime.getId());

        List<DailyWorkTimeDTO> dailyDTOs = workTime.getDailyWorkTimes().stream()
                .map(this::convertToDailyDTO)
                .collect(Collectors.toList());

        dto.setDailyWorkTimes(dailyDTOs);

        return dto;
    }


    public WorkTime updateWorkTime(Long id, WorkTimeDTO dto) {
        WorkTime workTime = workTimeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("WorkTime non trouvé avec l'ID : " + id));

        // Mise à jour des horaires
        List<DailyWorkTime> updatedList = dto.getDailyWorkTimes().stream()
                .map(d -> convertToDailyEntity(d, workTime.getId()))
                .collect(Collectors.toList());

        workTime.setDailyWorkTimes(updatedList);
        workTimeRepository.save(workTime);
        return workTime;
    }

    public WorkTime getWorkTimeById(Long id) {
        return workTimeRepository.getById(id);
    }
}
