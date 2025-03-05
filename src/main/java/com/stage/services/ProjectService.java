package com.stage.services;

import com.stage.persistans.Project;
import com.stage.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<Project>  getALlProject() {
        return projectRepository.findAll();
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }
    public Optional<Project> getProjectByName(String name) {
        return projectRepository.findByName(name);

    }
    public Project save(Project project) {
        return projectRepository.save(project);
    }
    public Project update(Project project) {
        return projectRepository.save(project);
    }
    public void delete(Long id) {
        projectRepository.deleteById(id);
    }
}
