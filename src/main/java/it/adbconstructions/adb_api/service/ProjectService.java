package it.adbconstructions.adb_api.service;

import it.adbconstructions.adb_api.exception.ProjectAlreadyExistException;
import it.adbconstructions.adb_api.exception.ProjectNotFoundException;
import it.adbconstructions.adb_api.model.Project;
import it.adbconstructions.adb_api.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static it.adbconstructions.adb_api.common.constant.ErrorUtil.PROJECT_ALREADY_EXIST;
import static it.adbconstructions.adb_api.common.constant.ErrorUtil.PROJECT_NOT_FOUND;

@Service
@Transactional
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private StorageService storageService;

    public Project createProject(String projectCode, String title, String description, String category, String country,
                                 String location, String projectManager, String projectEngineer, MultipartFile img1,MultipartFile img2,MultipartFile img3,int duration ) throws ProjectAlreadyExistException, IOException {
        Project existProject = projectRepository.findByProjectCode(projectCode);

        if (existProject == null)
        {
            Project newProject = new Project();
            newProject.setProjectCode(projectCode);
            newProject.setTitle(title);
            newProject.setDescription(description);
            newProject.setCategory(category);
            newProject.setCountry(country);
            newProject.setLocation(location);
            newProject.setDuration(duration);
            newProject.setProjectManager(projectManager);
            newProject.setProjectEngineer(projectEngineer);
            newProject.setPublishedDate(new Date());
            if (img1 != null && !img1.isEmpty())
            {
                String url1 = storageService.uploadProjectImage(projectCode,img1,"1");
                newProject.setUrl1(url1);
            }
            if (img2 != null && !img2.isEmpty())
            {
                String url2 = storageService.uploadProjectImage(projectCode,img2,"2");
                newProject.setUrl2(url2);
            }
            if (img3 != null && !img3.isEmpty())
            {
                String url3 = storageService.uploadProjectImage(projectCode,img3,"3");
                newProject.setUrl3(url3);
            }
            return projectRepository.save(newProject);

        }
        else
        {
            throw new ProjectAlreadyExistException(PROJECT_ALREADY_EXIST);
        }
    }

    public Project updateProject(String currentProjectCode, String projectCode, String title, String description, String category, String country,
                                 String location, int duration, String projectManager, String projectEngineer, MultipartFile img1,MultipartFile img2,MultipartFile img3 ) throws IOException, ProjectNotFoundException {
        Project existProject = projectRepository.findByProjectCode(currentProjectCode);

        if (existProject != null)
        {
            existProject.setProjectCode(projectCode);
            existProject.setTitle(title);
            existProject.setDescription(description);
            existProject.setCategory(category);
            existProject.setCountry(country);
            existProject.setLocation(location);
            existProject.setDuration(duration);
            existProject.setProjectManager(projectManager);
            existProject.setProjectEngineer(projectEngineer);
            existProject.setPublishedDate(new Date());
            if (img1 != null && !img1.isEmpty())
            {
                storageService.deleteProjectFile(currentProjectCode,"1");
                String url1 = storageService.uploadProjectImage(projectCode,img1,"1");
                existProject.setUrl1(url1);
            }
            if (img2 != null && !img2.isEmpty())
            {
                storageService.deleteProjectFile(currentProjectCode,"2");
                String url2 = storageService.uploadProjectImage(projectCode,img2,"2");
                existProject.setUrl2(url2);
            }
            if (img3 != null && !img3.isEmpty())
            {
                storageService.deleteProjectFile(currentProjectCode,"3");
                String url3 = storageService.uploadProjectImage(projectCode,img3,"3");
                existProject.setUrl3(url3);
            }
            return projectRepository.save(existProject);

        }
        else
        {
            throw new ProjectNotFoundException(PROJECT_NOT_FOUND);
        }
    }

    public List<Project> getAllProjects()
    {
        return projectRepository.findAll();
    }

    public List<Project> findAllByCategory(String projectCategory)
    {
        return projectRepository.findByCategory(projectCategory);
    }

    public void deleteByProjectCode(String projectCode) throws ProjectNotFoundException {
        Project existProject = projectRepository.findByProjectCode(projectCode);
        if (existProject != null)
        {
            projectRepository.delete(existProject);
        }
        else
        {
            throw new ProjectNotFoundException(PROJECT_NOT_FOUND);
        }
    }
}
