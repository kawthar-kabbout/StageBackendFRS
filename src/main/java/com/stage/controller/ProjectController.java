package com.stage.controller;

import com.stage.dto.ActivityDTO;
import com.stage.persistans.Project;
import com.stage.repositories.ProjectRepository;
import com.stage.services.ActivityService;
import com.stage.services.ProjectService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")

@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final ActivityService activityService;

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

    //@PostMapping("cloneProject/{id}/{nameNewProject}")
    @GetMapping("cloneProject/{id}")
    public ResponseEntity<Project> cloneProject(@PathVariable Long id) {
        Optional<Project> oldProject = projectService.getProjectById(id);
        if (oldProject.isPresent()) {
            projectService.cloneProject(oldProject.get());
            return ResponseEntity.ok(oldProject.get());
        }
else
    return ResponseEntity.notFound().build();

    }
}
