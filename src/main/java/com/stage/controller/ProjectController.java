package com.stage.controller;

import com.stage.dto.ActiviteFrontDTO;
import com.stage.dto.ActivityDTO;
import com.stage.dto.ProjetDTO;
import com.stage.persistans.Activity;
import com.stage.persistans.Project;
import com.stage.repositories.ProjectRepository;
import com.stage.services.ActivityService;
import com.stage.services.ChocosolverService;
import com.stage.services.DependanceActivityService;
import com.stage.services.ProjectService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/projects")

@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final ActivityService activityService;
    private final DependanceActivityService dependanceActivityService;
    private final ChocosolverService chocosolverService;

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


    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody Project project) {
        if (projectService.getProjectByName(project.getName()) != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Un projet avec ce nom existe déjà.");
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(response);
        }

        Project savedProject = projectService.save(project);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Projet créé avec succès.");
        response.put("project", savedProject);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id, @RequestBody Project project) {
        Optional<Project> savedProject = projectService.getProjectById(id);
        if (savedProject.isPresent()) {
            savedProject.get().setName(project.getName());
            return ResponseEntity.ok(projectService.save(savedProject.get()));
        }return ResponseEntity.notFound().build();


        }




    @GetMapping("/{projectId}/wbs-structure")
    public ResponseEntity<List<ActivityDTO>> getProjectWBSStructure(@PathVariable Long projectId) {
        // Récupérer la structure WBS depuis le service
        List<ActivityDTO> wbsStructure = activityService.getProjectWBSStructure(projectId);


        return ResponseEntity.ok(wbsStructure);
    }

    @PostMapping("cloneProject/{id}/{name}")
    public ResponseEntity<?> cloneProject(@PathVariable Long id,@PathVariable String name,
                                          @RequestBody List<ActiviteFrontDTO> activitesFrontDTO) {

        Optional<Project> oldProject = projectService.getProjectById(id);
        if (oldProject.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Project newProject = projectService.cloneProject(oldProject.get(),name,activitesFrontDTO);
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

//    activityService.updateActivitiesCloningRoot(activitesFrontDTO, newProject.getId());


        return ResponseEntity.ok(newActivitiesDTO);
    }

    @PostMapping("/solve/projects/{startPlanning}")
    public ResponseEntity<List<Activity>> solveNewProject(
            @PathVariable Long startPlanning ,
            @RequestBody List<Project> projects) {


        System.out.println("");
        System.out.println("");
        System.out.println("LocalDateTime: " + startPlanning);
      Long   startPlanning2=startPlanning*1000;
        LocalDateTime localDateTimePlannig  = Instant.ofEpochMilli(startPlanning2)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        System.out.println("LocalDateTime: " + localDateTimePlannig);

        // Validate that the projects exist
        if (!projectService.existingProjects(projects)) {
            return ResponseEntity.notFound().build();
        }


        // Solve the projects using the Choco solver
        List<Activity> result = chocosolverService.chocosolver(projects,localDateTimePlannig);

        // Check if the result is valid
        if (result == null || result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }

        @GetMapping("/project/planning/{id}")
    public ResponseEntity<ProjetDTO> getProjectPlanning(@PathVariable Long id) {
        Optional<Project> project = projectService.getProjectById(id);
        if (project.isPresent()) {
            ProjetDTO projetDTO = projectService.getProjetDTOPalnification(project.get());
            return ResponseEntity.ok(projetDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        Optional<Project> projectOpt = projectService.getProjectById(id);

        if (projectOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        boolean deleted = projectService.deleteProject(projectOpt.get());

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
