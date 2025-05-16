package com.stage.controller;

import com.stage.dto.WorkTimeDTO;
import com.stage.persistans.WorkTime;
import com.stage.services.WorkTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/workTime")
@RequiredArgsConstructor
public class WorkTimeController {


    private final WorkTimeService workTimeService;

    @GetMapping("/{id}")
    public ResponseEntity<WorkTimeDTO> getWorkTime(@PathVariable Long id) {
        WorkTimeDTO workTimeDTO = workTimeService.getWorkTimeByIdDTO(id);
        if (workTimeDTO == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(workTimeDTO, HttpStatus.OK);
    }
    @PutMapping("/{id}")
    public ResponseEntity<WorkTime> updateWorkTime(@PathVariable Long id, @RequestBody WorkTimeDTO dto) {
      WorkTime workTime=   workTimeService.updateWorkTime(id, dto);
        if (workTime == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(workTime, HttpStatus.OK);
    }
}
