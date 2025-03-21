package com.stage.controller;

import com.stage.dto.ActivityDTO;
import com.stage.persistans.Activity;
import com.stage.persistans.Project;
import com.stage.repositories.ProjectRepository;
import com.stage.services.ActivityService;
import com.stage.services.DependanceActivityService;
import com.stage.services.ProjectService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")

@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final ActivityService activityService;
    private final DependanceActivityService dependanceActivityService;

    @GetMapping
    public ResponseEntity<List<Project>> getProjects() {
        List<Project> projects = projectService.getALlProject();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable Long id) {
        Optional<Project> project = projectService.getProjectById(id);
        if (project.isPresent()) {
            return ResponseEntity.ok(project.get());

        }else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/name/{name}")
public ResponseEntity<Project> getProjectByName(@PathVariable String name) {
        Optional<Project> project = projectService.getProjectByName(name);
        if (project.isPresent()) {
            return ResponseEntity.ok(project.get());
        }return ResponseEntity.notFound().build();
    }


    @PostMapping
    public ResponseEntity<Project> createProject(@RequestBody Project project) {
        Project savedProject = projectService.save(project);
        return ResponseEntity.ok(savedProject);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project project) {
        Optional<Project> savedProject = projectService.getProjectById(id);
        if (savedProject.isPresent()) {
            savedProject.get().setName(project.getName());
            return ResponseEntity.ok(projectService.save(savedProject.get()));
        }return ResponseEntity.notFound().build();


        }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {

        projectService.delete(id);
        return ResponseEntity.noContent().build();

    }



    @GetMapping("/{projectId}/wbs-structure")
    public ResponseEntity<List<ActivityDTO>> getProjectWBSStructure(@PathVariable Long projectId) {
        // Récupérer la structure WBS depuis le service
        List<ActivityDTO> wbsStructure = activityService.getProjectWBSStructure(projectId);

        // Retourner la réponse HTTP avec la structure WBS
        return ResponseEntity.ok(wbsStructure);
    }

    @GetMapping("cloneProject/{id}/{name}")
    public ResponseEntity<?> cloneProject(@PathVariable Long id,@PathVariable String name) {

        Optional<Project> oldProject = projectService.getProjectById(id);
        if (oldProject.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Project newProject = projectService.cloneProject(oldProject.get(),name);
        if (newProject == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors du clonage du projet.");
        }


        List<Activity> newActivitiesList = dependanceActivityService.cloneDependanceActivityRoot(newProject,oldProject.get());
        if (newActivitiesList == null || newActivitiesList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors du clonage des activités dépendantes.");
        }


        List<ActivityDTO> newActivitiesDTO = this.activityService.getProjectWBSStructure(newProject.getId());
        if (newActivitiesDTO == null || newActivitiesDTO.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération de la structure WBS.");
        }


        return ResponseEntity.ok(newActivitiesDTO);
    }
}
