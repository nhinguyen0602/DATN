package com.backend.helpdesk.service;

import com.backend.helpdesk.DTO.ProjectDTO;
import com.backend.helpdesk.common.Constants;
import com.backend.helpdesk.converter.ConvertProject.ProjectDTOToProjectConvert;
import com.backend.helpdesk.converter.ConvertProject.ProjectToProjectDTOConvert;
import com.backend.helpdesk.entity.Project;
import com.backend.helpdesk.entity.Status;
import com.backend.helpdesk.entity.UserEntity;
import com.backend.helpdesk.exception.UserException.BadRequestException;
import com.backend.helpdesk.exception.UserException.NotFoundException;
import com.backend.helpdesk.exception.UserException.UserAccessDeniedException;
import com.backend.helpdesk.repository.ProjectRepository;
import com.backend.helpdesk.repository.RoleRepository;
import com.backend.helpdesk.repository.StatusRepository;
import com.backend.helpdesk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectToProjectDTOConvert projectToProjectDTOConvert;

    @Autowired
    private ProjectDTOToProjectConvert projectDTOToProjectConvert;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public int getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        UserEntity userEntity = userRepository.findByEmail(email);
        return userEntity.getId();
    }

    public List<ProjectDTO> getAllProject() {
        Status status = statusRepository.findByName(Constants.APPROVED);
        return projectToProjectDTOConvert.convert(projectRepository.findByStatus(status));
    }

    public Project addProject(ProjectDTO projectDTO) {
        Calendar calendar = Calendar.getInstance();
        projectDTO.setCreateAt(calendar);
        projectDTO.setUpdateAt(calendar);
        projectDTO.setUserIdCreate(getUserId());
        projectDTO.setStatus(Constants.PENDING);
        Project project = projectDTOToProjectConvert.convert(projectDTO);
        UserEntity userEntity=userRepository.findById(getUserId()).get();
        List<Project> projects=new ArrayList<>();
        projects.add(project);
        userEntity.setProjects(projects);
        userRepository.save(userEntity);
        return projectRepository.save(project);
    }

    public Project editProject(int id, ProjectDTO projectDTO) {
        Optional<Project> project = projectRepository.findById(id);
        if (!project.isPresent()) {
            throw new NotFoundException("project not found!");
        }
        if (project.get().getUserCreate() == null) {
            project.get().setUserCreate(userRepository.findById(getUserId()).get());
        }
        if (project.get().getUserCreate().getId() != getUserId() || !userRepository.findById(project.get().getUserCreate().getId()).get().getRoleEntities().contains(roleRepository.findByName(Constants.ADMIN))) {
            throw new BadRequestException("Not have access");
        }
        Calendar calendar = Calendar.getInstance();
        project.get().setName(projectDTO.getName());
        project.get().setDescription(projectDTO.getDescription());
        project.get().setUpdateAt(calendar);
        project.get().setStatus(statusRepository.findByName(Constants.PENDING));
        return projectRepository.save(project.get());
    }

    public Project deleteProject(int id) {
        Optional<Project> project = projectRepository.findById(id);
        if (!project.isPresent()) {
            throw new NotFoundException("project not found!");
        }
        project.get().setStatus(statusRepository.findByName(Constants.REJECTED));
        return projectRepository.save(project.get());
    }

    public Project acceptProject(int id) {
        Optional<Project> project = projectRepository.findById(id);
        if (!project.isPresent()) {
            throw new NotFoundException("project not found!");
        }
        project.get().setStatus(statusRepository.findByName(Constants.APPROVED));
        return projectRepository.save(project.get());
    }

    public Project addUserForProject(int id, String email) {
        Optional<Project> project = projectRepository.findById(id);
        if (!project.isPresent()) {
            throw new NotFoundException("project not found!");
        }
        UserEntity userEntity = userRepository.findByEmail(email);
        if(userEntity==null){
            throw new NotFoundException("User not found!");
        }
        List<Project> projects = new ArrayList<>();
        projects.add(project.get());
        userEntity.setProjects(projects);
        userRepository.save(userEntity);
        return project.get();
    }

    public Project removeUserInProject(int id, String email){
        Optional<Project> project = projectRepository.findById(id);
        if (!project.isPresent()) {
            throw new NotFoundException("project not found!");
        }
        UserEntity userEntity = userRepository.findByEmail(email);
        if(userEntity==null){
            throw new NotFoundException("User not found!");
        }
        List<Project> projects = userEntity.getProjects();
        projects.remove(project.get());
        userEntity.setProjects(projects);
        userRepository.save(userEntity);
        return project.get();
    }
}
