package com.stage.services;

import com.stage.persistans.Activity;
import com.stage.persistans.WorkTime;
import com.stage.repositories.WorkTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkTimeService {
    private final WorkTimeRepository workTimeRepository;

    public WorkTime findById( Long id) {
       return workTimeRepository.findById(id).get();
    }
}
