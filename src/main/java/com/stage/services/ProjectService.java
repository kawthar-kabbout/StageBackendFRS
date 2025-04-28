package com.stage.services;

import com.stage.dto.ActiviteFrontDTO;
import com.stage.dto.ProjetDTO;
import com.stage.persistans.Activity;
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
    public Project getProjectByName(String name) {

        if (projectRepository.findByName(name).isPresent()) {
            return projectRepository.findByName(name).get();
        }return null;

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


    public Project cloneProject(Project projectTemplate  , String newName,List<ActiviteFrontDTO> activitesFrontDTO) {
        Project newProject = null;
        if (projectRepository.findById(projectTemplate.getId()).isPresent()) {
            Project project = projectRepository.findById(projectTemplate.getId()).get();
            newProject = new Project();
            newProject.setName(newName);
            newProject.setProjectTemplateId(projectTemplate.getId());
            if (projectRepository.save(newProject) != null) {
                activityService.cloneActivityProjectRootTree(projectTemplate, projectRepository.save(newProject),activitesFrontDTO);
            }

        }
        return newProject;
    }


    public boolean existingProjects(List<Project> projects) {


        for (Project project : projects) {
            if (projectRepository.findById(project.getId()).isEmpty()) {
                return false;
            }
        }return true;
    }


    public ProjetDTO getProjetDTOPalnification(Project project) {
        ProjetDTO projetDTO = new ProjetDTO();
        projetDTO.setId(project.getId());
        projetDTO.setName(project.getName());
        projetDTO.setProjectTemplateId(project.getProjectTemplateId());
        projetDTO.setActivites(activityService.getActivitiesByProjectId(project.getId()));
    return projetDTO;

    }

}
