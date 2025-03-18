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
    private final ActivityService activityService;


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


    public Project cloneProject(Project projectTemplate  ) {
        Project newProject = null;
        if (projectRepository.findById(projectTemplate.getId()).isPresent()) {
            Project project = projectRepository.findById(projectTemplate.getId()).get();
            newProject = new Project();
            newProject.setName(project.getName() +"new");
            if (projectRepository.save(newProject) != null) {
                activityService.cloneActivityProjectRootTree(projectTemplate, projectRepository.save(newProject));
            }

        }
        return newProject;
    }
}
