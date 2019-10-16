package com.backend.helpdesk.controller;

import com.backend.helpdesk.DTO.ProjectDTO;
import com.backend.helpdesk.entity.Project;
import com.backend.helpdesk.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Secured("ROLE_MANAGE")
    @GetMapping
    public List<ProjectDTO> getAllProject() {
        return projectService.getAllProject();
    }

    @Secured("ROLE_MANAGE")
    @PostMapping
    public Project addProject(@Valid @RequestBody ProjectDTO projectDTO) {
        return projectService.addProject(projectDTO);
    }

    @Secured("ROLE_MANAGE")
    @PutMapping("/{id}")
    public Project editProject(@PathVariable("id") int id, @Valid @RequestBody ProjectDTO projectDTO) {
        return projectService.editProject(id, projectDTO);
    }

    @Secured("ROLE_MANAGE")
    @DeleteMapping("/{id}")
    public Project deleteProject(@PathVariable("id") int id) {
        return projectService.deleteProject(id);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}")
    public Project acceptProject(@PathVariable("id") int id) {
        return projectService.acceptProject(id);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/add_user/{id}")
    public Project addUserForProject(@PathVariable("id") int id,@RequestParam(value = "email", required = false) String email){
        return projectService.addUserForProject(id,email);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/remove_user/{id}")
    public Project removeUserForProject(@PathVariable("id") int id,@RequestParam(value = "email", required = false) String email){
        return projectService.removeUserInProject(id,email);
    }

}