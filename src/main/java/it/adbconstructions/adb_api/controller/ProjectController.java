package it.adbconstructions.adb_api.controller;

import it.adbconstructions.adb_api.exception.ExceptionHandling;
import it.adbconstructions.adb_api.exception.MaterialAlreadyExistException;
import it.adbconstructions.adb_api.exception.ProjectAlreadyExistException;
import it.adbconstructions.adb_api.exception.ProjectNotFoundException;
import it.adbconstructions.adb_api.model.HttpResponse;
import it.adbconstructions.adb_api.model.Project;
import it.adbconstructions.adb_api.model.User;
import it.adbconstructions.adb_api.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = {"/", "/projects"})
public class ProjectController extends ExceptionHandling {

    @Autowired
    private ProjectService projectService;

    @PostMapping("/create")
    public ResponseEntity<Project> create(@RequestParam("projectCode") String projectCode,
                                           @RequestParam("title") String title,
                                           @RequestParam("description") String description,
                                           @RequestParam("category") String category,
                                           @RequestParam("country") String country,
                                           @RequestParam("location") String location,
                                           @RequestParam("duration") int duration,
                                           @RequestParam("projectManager") String projectManager,
                                           @RequestParam("projectEngineer") String projectEngineer,
                                           @RequestParam(value = "img1", required = false) MultipartFile img1,
                                           @RequestParam(value = "img2", required = false) MultipartFile img2,
                                           @RequestParam(value = "img3", required = false) MultipartFile img3) throws ProjectAlreadyExistException, IOException {
        Project newProject = projectService.createProject(projectCode,title,description,category,country,location,projectManager,projectEngineer,img1,img2,img3,duration);
        return new ResponseEntity<>(newProject, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<Project> update(
                                        @RequestParam("currentProjectCode") String currentProjectCode,
                                        @RequestParam("projectCode") String projectCode,
                                        @RequestParam("title") String title,
                                        @RequestParam("description") String description,
                                        @RequestParam("category") String category,
                                        @RequestParam("country") String country,
                                        @RequestParam("location") String location,
                                        @RequestParam("duration") int duration,
                                        @RequestParam("projectManager") String projectManager,
                                        @RequestParam("projectEngineer") String projectEngineer,
                                        @RequestParam(value = "img1", required = false) MultipartFile img1,
                                        @RequestParam(value = "img2", required = false) MultipartFile img2,
                                        @RequestParam(value = "img3", required = false) MultipartFile img3) throws IOException, ProjectNotFoundException {
        Project newProject = projectService.updateProject(currentProjectCode,projectCode,title,description,category,country,location,duration,projectManager,projectEngineer,img1,img2,img3);
        return new ResponseEntity<>(newProject, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Project>> getAllProjects (){
        List<Project> allProjects = projectService.getAllProjects();
        return new ResponseEntity<>(allProjects, HttpStatus.OK);
    }

    @GetMapping("/find/{projectCategory}")
    public ResponseEntity<List<Project>> getAllByCategory(@PathVariable("projectCategory") String projectCategory){
        List<Project> projectListByCategory = projectService.findAllByCategory(projectCategory);
        return new ResponseEntity<>(projectListByCategory, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{projectCode}")
    public ResponseEntity<HttpResponse> deleteByProjectCode(@PathVariable("projectCode") String projectCode) throws ProjectNotFoundException {
        projectService.deleteByProjectCode(projectCode);
        return response(HttpStatus.OK, "Project successfully deleted.");
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        HttpResponse body = new HttpResponse(httpStatus.value(),httpStatus,httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase());
        return new ResponseEntity<>(body, httpStatus);
    }
}
