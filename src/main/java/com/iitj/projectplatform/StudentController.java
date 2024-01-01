package com.iitj.projectplatform;

import com.iitj.projectplatform.Repositories.*;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class StudentController {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ProjectRepo projectRepo;
    @Autowired
    private ProjectApplyRepo projectApplyRepo;
    @Autowired
    private ProjectCreateRepo projectCreateRepo;
    @Autowired
    private RejectedRepo rejectedRepo;

    @Autowired
    private ApprovedRepo approvedRepo;
    @Autowired
    private TagMappingRepo tagMappingRepo;

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private CourseCodeRepository courseCodeRepository;


    @GetMapping("/welcome")
    public String greeting(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Optional<User>currentUser = userRepo.findUserByUsername(username);
//        Role currentRole ;
        // add project list

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority: authorities){
            if (authority.getAuthority().equals("ROLE_STUDENT")){
                System.out.println(
                        ">> adding student to model"
                );

                String role = currentUser.get().getRole().name();
                String user_name = currentUser.get().getUsername();
                String name = currentUser.get().getName();
                String email = currentUser.get().getEmail();


                model.addAttribute("role", role);
                model.addAttribute("user_name", user_name);
                model.addAttribute("name", name);
                model.addAttribute("email", email);



                model.addAttribute("isStudent", true);
                model.addAttribute("isProfessor", false);
//                model.addAttribute("isCoordinator", false);

            }
//            else if (authority.getAuthority().equals("ROLE_COORDINATOR")){
//                System.out.println(
//                        ">> adding coordinator to model"
//                );
//
//                String role = currentUser.get().getRole().name();
//                String user_name = currentUser.get().getUsername();
//                String name = currentUser.get().getName();
//                String email = currentUser.get().getEmail();
//
//                model.addAttribute("role", role);
//                model.addAttribute("user_name", user_name);
//                model.addAttribute("name", name);
//                model.addAttribute("email", email);
//
//
//                model.addAttribute("isCoordinator", true);
//                model.addAttribute("isStudent", false);
//                model.addAttribute("isProfessor", false);
//            }
            else if (authority.getAuthority().equals("ROLE_SUPERADMIN")){
                System.out.println("Redirecting to superadmin");
                return "redirect:/welcomeSuperAdmin";
            }
            else {
                System.out.println(
                        ">> adding prof to model"
                );
                String role = currentUser.get().getRole().name();
                String user_name = currentUser.get().getUsername();
                String name = currentUser.get().getName();
                String email = currentUser.get().getEmail();

                model.addAttribute("role", role);
                model.addAttribute("user_name", user_name);
                model.addAttribute("name", name);
                model.addAttribute("email", email);
//                model.addAttribute("isCoordinator", false);
                model.addAttribute("isStudent", false);
                model.addAttribute("isProfessor", true);
            }
            break;
        }

        //if user is coordinator also, set it
        if (currentUser.get().getRole().equals(Role.Professor)
                && currentUser.get().getCoordinator().equals(Boolean.TRUE)){
            model.addAttribute("isCoordinator",true);
        }
        else {
            model.addAttribute("isCoordinator",false);
        }

        //running projects
        List<ProjectCreate> myProjectsCreated = new ArrayList<>();
        List<Approved> myApprovedProjects = new ArrayList<>();

        if (currentUser.get().getRole().equals(Role.Professor))  myProjectsCreated = projectCreateRepo.findProjectCreateByUserId(username);
        else if (currentUser.get().getRole().equals(Role.Student)){
            myApprovedProjects = approvedRepo.findApprovedByUserId(username);
        }

        List<Project> myProjectsRunning = new ArrayList<>();
        HashMap<Project,CourseCode> mapOfProjectAndCourseCode = new HashMap<>();
        HashMap<Project,User> mapOfProjectAndProf = new HashMap<>();
        HashMap<Project,List<User>> mapOfProjectAndStudents = new HashMap<>();


        if (currentUser.get().getRole().equals(Role.Professor)){
            for (ProjectCreate projectCreate: myProjectsCreated){
                if (projectRepo.findProjectById(projectCreate.getProjectId()).getStatus().equals(ProjectStatus.IN_PROGRESS.toString())){
                    myProjectsRunning.add(projectRepo.findProjectById(projectCreate.getProjectId()));
                    myProjectsRunning.add(projectRepo.findProjectById(projectCreate.getProjectId()));
                    List<User> studentList = new ArrayList<>();
                    List<Approved> approvedList = approvedRepo.findApprovedByProjectId(projectCreate.getProjectId());
                    for (Approved approvedIt: approvedList){
                        studentList.add(userRepo.findUserByUsername(approvedIt.getUserId()).get());
                    }
                    mapOfProjectAndStudents.put(
                            projectRepo.findProjectById((projectCreate.getProjectId())),
                            studentList
                    );
                }

            }
        }
        else if (currentUser.get().getRole().equals(Role.Student)){
            for (Approved projectApproved: myApprovedProjects){
                if (projectRepo.findProjectById(projectApproved.getProjectId()).getStatus().equals(ProjectStatus.IN_PROGRESS.toString())){
                    mapOfProjectAndCourseCode.put(
                            projectRepo.findProjectById((projectApproved.getProjectId())),
                            projectApproved.getCourseCode()
                    );
                    mapOfProjectAndProf.put(
                            projectRepo.findProjectById((projectApproved.getProjectId())),
                            userRepo.findUserByUsername(projectCreateRepo.findProjectCreateByProjectId(projectApproved.getProjectId()).getUserId()).get()
                    );
                    myProjectsRunning.add(projectRepo.findProjectById(projectApproved.getProjectId()));
                }

            }
        }

        model.addAttribute("myProjectsRunning", myProjectsRunning);
        model.addAttribute("mapOfProjectAndCourseCode", mapOfProjectAndCourseCode);
        model.addAttribute("mapOfProjectAndProf", mapOfProjectAndProf);
        System.out.println(mapOfProjectAndProf);
        model.addAttribute("mapOfProjectAndStudents", mapOfProjectAndStudents);




        return "welcome";
    }

    @GetMapping("/login")
    public String showLogin(){
        System.out.println(
                ">>  login (GET) called"
        );
        return "login";
    }

    @PostMapping("/login")
    public void postLogin(){

    }

    @GetMapping("/register")
    public String showRegister(Model model){
        User user = new User();
        model.addAttribute("user",user);
        model.addAttribute("roleList",Role.values());
        return "register";
    }

    @PostMapping("/register/save")
    public String saveRegister(@ModelAttribute("user") User user){
        Optional<User> exisitingUser = userRepo.findUserByUsername(user.getUsername());
        if (!exisitingUser.isEmpty()){
            System.out.println(
                    ">> User already exists."
            );
            return "redirect:/register";
        }
        else {
            User toSave = new User();
            toSave.setName(user.getName());
            toSave.setUsername(user.getUsername());
            toSave.setPassword(user.getPassword());
            toSave.setEmail(user.getEmail());
            if (user.getRole().equals(Role.Student)){
                toSave.setRole(Role.Student);
            }
//            else if (user.getRole().equals(Role.Coordinator)){
//                toSave.setRole(Role.Coordinator);
//            }
            else {
                toSave.setRole(Role.Professor);
            }
            toSave.setAccountNonLocked(true);
            userRepo.save(toSave);
            return "redirect:/login";
        }
    }

    @GetMapping("/create")
    public String createProject(Model model, Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Optional<User> user = userRepo.findUserByUsername(username);
        Role role;


        model.addAttribute("stipendOptionsList", StipendOption.values());
//        model.addAttribute("departmentList", Departments.values());
        model.addAttribute("courseCodeList", courseCodeRepository.findAll());
        model.addAttribute("projectTypeList", ProjectType.values());

        Boolean isStudent=false, isProfessor=false, isCoordinator = false;
        if (user.isPresent()){
            role = user.get().getRole();
            if (role.equals(Role.Student)){
                isStudent = true;
                isProfessor = false;
                isCoordinator = false;
            } else if (role.equals(Role.Coordinator)) {
                isStudent=false;
                isProfessor = false;
                isCoordinator = true;
            } else {
                isStudent = false;
                isProfessor = true;
                isCoordinator = false;
            }
        }
        else {
            System.out.println("User not found");
        }

        model.addAttribute("isStudent", isStudent);
        model.addAttribute("isProf", isProfessor);
        model.addAttribute("isCoordinator", isCoordinator);


        if (user.get().getRole().equals(Role.Professor)
                && user.get().getCoordinator().equals(Boolean.TRUE)){
            model.addAttribute("isCoordinator",true);
        }



        Project project = new Project();
        model.addAttribute("project",project);
        model.addAttribute("isEdit", false);

        return "createProject";
    }

    @PostMapping("/create/save")
    public String saveProject(@ModelAttribute("project") Project project,
                              @ModelAttribute("projectId") Long projectId,
                              @ModelAttribute("tagsGiven") String tagsGiven,
                              @RequestParam("projectTypesAllowed") String[] projectTypesAllowed,
                              @ModelAttribute("isEdit") Boolean isEdit,
                              Authentication authentication){

        System.out.println("alloweed pt "+ projectTypesAllowed.length);
        List<ProjectType> projectTypes = new ArrayList<>();
        if (projectTypesAllowed!=null){
            for (String it: projectTypesAllowed){
                projectTypes.add(ProjectType.valueOf(it));
            }
        }

        System.out.println("Editing Project " + projectId);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        List<String> tagList = Arrays.asList(tagsGiven.split(","));
        for (String tag: tagList){
            tagList.set(tagList.indexOf(tag), tag.toLowerCase());
        }
        List<Tag> tagObjectList = new ArrayList<>();
        for (String tag: tagList){
            try {
                tagRepository.findTagByName(tag);
                tagObjectList.add(
                        tagRepository.findTagByName(tag).get()
                );

            }
            catch (NoSuchElementException e){
                Tag newTag = new Tag(tag);
                newTag = tagRepository.save(newTag);
                tagObjectList.add(newTag);
            }
        }

        //Delete older tagMappings if some exist

        List<TagMapping> tagMappingsToBeDeleted = tagMappingRepo.findTagMappingByProjectId(projectId);
        for (TagMapping tmIt: tagMappingsToBeDeleted){
            tagMappingRepo.deleteById(tmIt.getId());
        }




//        Project toSave = new Project();
//        if (projectId == -1){
//            toSave = new Project();
//        }
//        else toSave = projectRepo.findProjectById(projectId);
//
//        toSave.setTitle(project.getTitle());
////        toSave.setDepartment(project.getDepartment());
//        toSave.setDeadline(project.getDeadline());
//        toSave.setDescription(project.getDescription());
//        toSave.setPreReq(project.getPreReq());
//        toSave.setMaxLim(project.getMaxLim());
////        toSave.setStatus(project.getStatus());
//        toSave.setProjectType(project.getProjectType());
//        toSave.setStipendOption(project.getStipendOption());
//        toSave.setStipendAmount(project.getStipendAmount());




        List<Project> projectsFormed = new ArrayList<>();
        //set status
        for (ProjectType projectType: projectTypes){
            Project toSave = new Project();
            if (projectId == -1){
                toSave = new Project();
            }
            else toSave = projectRepo.findProjectById(projectId);

            toSave.setTitle(project.getTitle());
//        toSave.setDepartment(project.getDepartment());
            toSave.setDeadline(project.getDeadline());
            toSave.setDescription(project.getDescription());
            toSave.setPreReq(project.getPreReq());
            toSave.setMaxLim(project.getMaxLim());
            toSave.setStatus(project.getStatus());
            toSave.setProjectType(projectType);
            toSave.setStipendOption(project.getStipendOption());
            toSave.setStipendAmount(project.getStipendAmount());
            toSave = projectRepo.save(toSave);
            projectsFormed.add(toSave);

        }



        for (Tag tag: tagObjectList){
            for (Project toSave: projectsFormed){
                TagMapping tagMapping = new TagMapping(toSave.getId(),tag.getId());
                tagMappingRepo.save(tagMapping);
            }
        }

        //cascade add
        if (!isEdit){
            for (Project toSave: projectsFormed){
                ProjectCreate projectCreate = new ProjectCreate(username,toSave.getId());
                projectCreateRepo.save(projectCreate);
            }
        }

        return "redirect:/myProjects";
    }


    @GetMapping("/myProjects")
    public String getMyProjects(Model model, Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Role currentRole = Role.Student;
        for (GrantedAuthority authority: authorities){
            if (authority.getAuthority().equals("ROLE_STUDENT")){
                System.out.println(
                        ">> (myProjects) adding student to model"
                );


                model.addAttribute("isStudent", true);
                model.addAttribute("isProfessor", false);
                model.addAttribute("isCoordinator", false);

                currentRole = Role.Student;
            }
            else {
                System.out.println(
                        ">> (myProjects) adding prof to model"
                );
                model.addAttribute("isStudent", false);
                model.addAttribute("isProfessor", true);
                currentRole = Role.Professor;
                if (userRepo.findUserByUsername(username).get().getCoordinator()){
                    model.addAttribute("isCcordinator",true);
                }
                else {
                    model.addAttribute("isCoordinator", false);

                }
            }
            break;
        }

        List<Project> myProjectList = new ArrayList<>();
        List<ProjectCreate> createdProjects = new ArrayList<>();
        if (currentRole.equals(Role.Professor)) createdProjects = projectCreateRepo.findProjectCreateByUserId(username);

        List<Optional<ProjectApply>> applyProjects = new ArrayList<>();
        List<Project> approvedProjects = new ArrayList<>();
        List<Project> rejectedProjects = new ArrayList<>();

        if (currentRole.equals(Role.Student)){
            applyProjects = projectApplyRepo.findProjectApplyByUserId(username);
            List<Approved> approvedObjectList = approvedRepo.findApprovedByUserId(username);
            for (Approved it: approvedObjectList){

                Project currProject = projectRepo.findProjectById(it.getProjectId());
                if (!(currProject==null) && currProject.getDeleted().equals(Boolean.FALSE)){
                    approvedProjects.add(currProject);
                }

            }
            List<Rejected> rejectedObjectList = rejectedRepo.findRejectedByUserId(username);
            for (Rejected it: rejectedObjectList){
                Project currProject = projectRepo.findProjectById(it.getProjectId());
                if (!(currProject==null) && currProject.getDeleted().equals(Boolean.FALSE)){
                    rejectedProjects.add(currProject);
                }
            }

            model.addAttribute("approvedList", approvedProjects);
            model.addAttribute("rejectedList", rejectedProjects);
        }


        List<Project> projectList = new ArrayList<>();

        if (currentRole.equals(Role.Professor)){
            for (ProjectCreate projectCreateIterator: createdProjects){
                if (projectCreateIterator!=null){
                    Optional<Project> foundProject = projectRepo.findById(projectCreateIterator.getProjectId());
                    if (foundProject.isPresent() && !(foundProject == null) && foundProject.get().getDeleted().equals(Boolean.FALSE)){
                        projectList.add(foundProject.get());
                    }
                }
            }
        }
        //else: all other than professor, might modify to else if in future
        else {
            for (Optional<ProjectApply> projectApplyIterator: applyProjects){
                if (projectApplyIterator.isPresent() && projectApplyIterator.get().getDeleted().equals(Boolean.FALSE)){
                    Optional<Project> foundProject = projectRepo.findById(projectApplyIterator.get().getProjectId());
                    if (foundProject.isPresent() && !approvedProjects.contains(foundProject)
                            && !rejectedProjects.contains(foundProject) && !(foundProject==null) && foundProject.get().getDeleted().equals(Boolean.FALSE)){
                        projectList.add(foundProject.get());
                    }
                }
            }
        }           
//        applyProjects.sort(Comparator.comparing(Project::getDeadline));
        approvedProjects.sort(Comparator.comparing(Project::getDeadline));
        rejectedProjects.sort(Comparator.comparing(Project::getDeadline));
        projectList.sort(Comparator.comparing(Project::getDeadline));

        model.addAttribute("listOfProjects", projectList);
        return "myProjects";
    }

    @PostMapping("/delete")
    public String deleteProject(@ModelAttribute("projectId") Long projectId){

        //cascade deletes
//        List<Optional<ProjectCreate>> projectCreated = projectCreateRepo.findProjectCreateByProjectId(projectId);
//        for (Optional<ProjectCreate> projectCreatedIterator: projectCreated){
//            projectCreateRepo.deleteById(projectCreatedIterator.get().getId());
//        }
//
//        List<ProjectApply> projectApplied = projectApplyRepo.findProjectApplyByProjectId(projectId);
//        for (ProjectApply projectApplyIterator: projectApplied){
//            projectApplyRepo.deleteById(projectApplyIterator.getId());
//        }

        Project projectToDelete = projectRepo.findProjectById(projectId);
        projectToDelete.setDeleted(true);
        projectRepo.save(projectToDelete);

        System.out.println(">> Deleted id: " + projectId);
        return "redirect:/myProjects";
    }

    @RequestMapping(value = "/edit", method = {RequestMethod.GET,RequestMethod.POST})
    public String editProject(Model model, @ModelAttribute("projectId") Long projectId, Authentication authentication){

        Project project = projectRepo.findProjectById(projectId);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Optional<User> user = userRepo.findUserByUsername(username);
        Role role;
        Boolean isStudent=false, isProfessor=false, isCoordinator = false;
        if (project == null){
            System.out.println(">> Project does not exist.");
        }
        else {
            role = user.get().getRole();
            if (role.equals(Role.Student)){
                isStudent = true;
                isProfessor = false;
                isCoordinator = false;
            }
            else {
                isStudent = false;
                isProfessor = true;
                isCoordinator = false;
            }
            model.addAttribute("isStudent", isStudent);
            model.addAttribute("isProf", isProfessor);
            model.addAttribute("isCoordinator", isCoordinator);

            model.addAttribute("project", project);
            model.addAttribute("isEdit", true);
            model.addAttribute("projectId", projectId);
            model.addAttribute("stipendOptionsList", StipendOption.values());
//            model.addAttribute("departmentList", Departments.values());
            model.addAttribute("courseCodeList", courseCodeRepository);
            model.addAttribute("projectTypeList", ProjectType.values());
            List<TagMapping> tagMappings = tagMappingRepo.findTagMappingByProjectId(projectId);
            String projectTags =  "";
            for (TagMapping tagMapping: tagMappings){
                if (tagMapping == tagMappings.get(tagMappings.size()-1)){
                    projectTags += (tagRepository.findById(tagMapping.getTagId()).get().getName());

                }
                else projectTags += (tagRepository.findById(tagMapping.getTagId()).get().getName()+",");
            }
            model.addAttribute("projectTags", projectTags);

        }

        return "createProject";
    }

    @PostMapping("/apply")
    public String showFilteredProjects(Model model, Authentication authentication,
                                       @RequestParam("title") String title,
                                       @RequestParam("deadline") String deadline,
                                       @RequestParam("keywords") String keywords,
                                       @RequestParam("stipendOption") String stipendOption) throws ParseException {
//        System.out.println("here>>>>>>>>>>>>>>>>>>>" + projectFilter.getDeadline());


        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Optional<User> user = userRepo.findUserByUsername(username);
        Role role;
        Boolean isStudent=false, isProfessor=false;
        if (user.isPresent()){
            role = user.get().getRole();
            if (role.equals(Role.Student)){
                isStudent = true;
                isProfessor = false;
            }
            else {
                isStudent = false;
                isProfessor = true;
            }
        }
        else {
            System.out.println("User not found");
        }

        model.addAttribute("isStudent", isStudent);
        model.addAttribute("isProf", isProfessor);

        model.addAttribute("stipendOptionsList",StipendOption.values());
        model.addAttribute("courseCodeList", courseCodeRepository.findAll());
        if (keywords==""){
            keywords = null;
        }
        if (stipendOption==""){
            stipendOption = null;
        }

        ProjectFilter projectFilter1 = new ProjectFilter(projectRepo,tagRepository,tagMappingRepo);
        projectFilter1.setDeadline(deadline==""?null: Date.valueOf(deadline));
        if (keywords!=null) {
            List<String> listOfKeywords = new ArrayList<>();
            List<String> temp = Arrays.stream(keywords.split(",")).toList();
            for (String key: temp){
                listOfKeywords.add(key.toLowerCase());
            }
            System.out.println("list of Keys: " + listOfKeywords);

            projectFilter1.setKeywords(listOfKeywords);
        }
        if (stipendOption!=null) projectFilter1.setStipendOption(StipendOption.valueOf(stipendOption));
        projectFilter1.setTitle(title);

        List<Project> listProjects = projectFilter1.getFilteredResults();

        List<Project> finalListProjects = new ArrayList<>();
        for (Project projectIt: listProjects){
            List<ProjectApply> projectApplyForThis = projectApplyRepo.findProjectApplyByProjectId(projectIt.getId());
            List<Approved> approvedForThis = approvedRepo.findApprovedByProjectId(projectIt.getId());
            List<Rejected> rejectedForThis = rejectedRepo.findRejecteddByProjectId(projectIt.getId());

            boolean toAdd = true;
            for (ProjectApply projectApplyIt: projectApplyForThis){
                if (projectApplyIt.getUserId().equals(username)){
                    toAdd=false;
                    break;
                }
            }
            if (!toAdd){
                continue;
            }
            for (Approved approvedIt: approvedForThis){
                if (approvedIt.getUserId().equals(username)){
                    toAdd=false;
                    break;
                }
            }
            if (!toAdd){
                continue;
            }
            for (Rejected rejectedIt: rejectedForThis){
                if (rejectedIt.getUserId().equals(username)){
                    toAdd=false;
                    break;
                }
            }
            if (toAdd){
                finalListProjects.add(projectIt);
            }
        }
        Map<Long,String> projectToProf = new HashMap<>();
        List<Project> floatedProjectsNotAppliedOrApprovedOrRejected = new ArrayList<>();
        for (Project project: finalListProjects){
            boolean addProjectToFinalList = true;
            List<ProjectApply> projectApplies = projectApplyRepo.findProjectApplyByProjectId(project.getId());
            for (ProjectApply projectApply: projectApplies){
                if (projectApply.getUserId().equals(username)){
                    addProjectToFinalList = false;
                    break;
                }
            }
            List<Approved> approvedList = approvedRepo.findApprovedByProjectId(project.getId());
            for (Approved approved: approvedList){
                if (approved.getUserId().equals(username)){
                    addProjectToFinalList = false;
                    break;
                }
            }
            List<Rejected> rejectedList = rejectedRepo.findRejecteddByProjectId(project.getId());
            for (Rejected rejected: rejectedList){
                if (rejected.getUserId().equals(username)){
                    addProjectToFinalList = false;
                    break;
                }
            }

            if (addProjectToFinalList == false){
                continue;
            }
            Long projectID = project.getId();
            String userID = projectCreateRepo.findProjectCreateByProjectId(projectID).getUserId();
            projectToProf.put(projectID, userRepo.findUserByUsername(userID).get().getName());
        }

        model.addAttribute("floatedProjects", finalListProjects );
        model.addAttribute("projectToProf", projectToProf);


        return "applyProject";
    }

    @GetMapping("/apply")
    public String applyProject(Model model, Authentication authentication){
        model.addAttribute("stipendOptionsList",StipendOption.values());
        model.addAttribute("courseCodeList",courseCodeRepository.findAll());
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Optional<User> user = userRepo.findUserByUsername(username);
        Role role;
        Boolean isStudent=false, isProfessor=false;
        if (user.isPresent()){
            role = user.get().getRole();
            if (role.equals(Role.Student)){
                isStudent = true;
                isProfessor = false;
            }
            else {
                isStudent = false;
                isProfessor = true;
            }
        }
        else {
            System.out.println("User not found");
        }

        model.addAttribute("isStudent", isStudent);
        model.addAttribute("isProf", isProfessor);
        model.addAttribute("courseCodeList", courseCodeRepository.findAll());




        List<Project> floatedProjects = projectRepo.findProjectByStatus("FLOATED");
        List<Project> toRemove = new ArrayList<>();
        for (Project project: floatedProjects){
            if (rejectedRepo.findRejectedByUserIdAndProjectId(username,project.getId()).isPresent()
                || approvedRepo.findApprovedByUserIdAndProjectId(username,project.getId()).isPresent()
                    || projectApplyRepo.findProjectByUserIdAndProjectId(username,project.getId()).isPresent()
                    || project.getDeleted().equals(Boolean.TRUE)  //added condition for checking isDeleted
            ){
                toRemove.add(project);
            }
        }
        floatedProjects.removeAll(toRemove);

        Map<Long,String> projectToProf = new HashMap<>();
        for (Project project: floatedProjects){
            Long projectID = project.getId();
            String userID = projectCreateRepo.findProjectCreateByProjectId(projectID).getUserId();
            projectToProf.put(projectID, userRepo.findUserByUsername(userID).get().getName());
        }

        model.addAttribute("floatedProjects", floatedProjects);
        model.addAttribute("projectToProf", projectToProf);
        floatedProjects.sort(Comparator.comparing(Project::getDeadline));

        return "applyProject";
    }

    @PostMapping("/apply/save")
    public String saveAppliedProject(
            @RequestParam("projectId") Long projectId,
            @RequestParam("courseCode") String courseCodeString,
            @RequestParam("resumeLink") String resumeLink,
            Authentication authentication
    ){
//        System.out.println("CourseCode: " + courseCode);
        CourseCode courseCode;
        if (courseCodeString.equals("")){
            courseCode = courseCodeRepository.findCourseCodeByCode("NA").get();
        }
        else {
            courseCode = courseCodeRepository.findCourseCodeByCode(courseCodeString).get();
        }

        Project project = projectRepo.findProjectById(projectId);
        if (project == null){
            System.out.println(">> Project does not exist");
            return "redirect:/myProjects";
        }
        else {

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            System.out.println(
                    "User " + userDetails.getUsername() + " applied for project " + projectId
            );
            ProjectApply projectApply = new ProjectApply();
            projectApply.setProjectId(projectId);
            projectApply.setUserId(userDetails.getUsername());
            projectApply.setResumeLink(resumeLink);
            projectApply.setCourseCode(courseCode);

            projectApplyRepo.save(projectApply);
            return "redirect:/apply";
        }
    }


    @GetMapping("/approve")
    public String approvePage(Model model, Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        Optional<User> user = userRepo.findUserByUsername(username);
        Role role;
        List<ProjectCreate> myprojects = projectCreateRepo.findProjectCreateByUserId(username);
        List<Long> projectIds = new ArrayList<>();
        List<ProjectApply> myProjectApplicants = new ArrayList<>();

        Boolean isStudent=false, isProfessor=false, isCoordinator = false;
        if (user.isPresent()){
            role = user.get().getRole();
            if (role.equals(Role.Student)){
                isStudent = true;
                isProfessor = false;
                isCoordinator = false;
            } else if (role.equals(Role.Coordinator)) {
                isStudent = false;
                isProfessor = false;
                isCoordinator=true;
            } else {
                isStudent = false;
                isProfessor = true;
                isCoordinator = false;
            }
        }
        else {
            System.out.println("User not found");
        }

        model.addAttribute("isStudent", isStudent);
        model.addAttribute("isProf", isProfessor);
        model.addAttribute("isCoordinator", isCoordinator);


        if (user.get().getRole().equals(Role.Professor)
                && user.get().getCoordinator().equals(Boolean.TRUE)){
            model.addAttribute("isCoordinator",true);
        }



        for (ProjectCreate projectCreate: myprojects){
            if (projectCreate!=null){
                if (projectCreate.getUserId().equals(username) && projectRepo.findProjectById(projectCreate.getProjectId()).getDeleted().equals(Boolean.FALSE)){
                    projectIds.add(projectCreate.getProjectId());
                    List<ProjectApply> projectApplicants = projectApplyRepo.findProjectApplyByProjectId(projectCreate.getProjectId());
//                    myProjectApplicants.addAll(projectApplicants);
                    for (ProjectApply projectApplyIterator: projectApplicants){
                        if (projectApplyIterator.getDeleted().equals(Boolean.FALSE)){
                            myProjectApplicants.add(projectApplyIterator);
                        }
                    }
                }
            }
        }



        Map<Long, List<ProjectApply>> groupedByProjectId = myProjectApplicants.stream()
                .collect(Collectors.groupingBy(ProjectApply::getProjectId));
        Map<String, List<ProjectApply>> groupedByProjectName = new HashMap<>();
        for (Long projectId: groupedByProjectId.keySet()){
            groupedByProjectName.put(
                    projectRepo.findProjectById(projectId).getTitle(),
                    groupedByProjectId.get(projectId)
            );
        }

        model.addAttribute("MapOfProjectApplicants", groupedByProjectName);
        return "approve";
    }


    @PostMapping("/approve/save")
    public String approveRequest(Model model,Authentication authentication,
                               @ModelAttribute("userId") String username,
                               @ModelAttribute("projectId") Long projectId){
//        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        String currentUser = userDetails.getUsername();

//        System.out.println("here*** " + username + projectId);
        Approved newApproved = new Approved();
        newApproved.setProjectId(projectId);
        newApproved.setUserId(username);
        newApproved.setCourseCode(projectApplyRepo.findProjectApplyByProjectId(projectId).get(0).getCourseCode());
        newApproved = approvedRepo.save(newApproved);

        //delete this approved user from applied list of that project
        Optional<ProjectApply> projectApply = projectApplyRepo.findProjectByUserIdAndProjectId(username,projectId);

        if (projectApply.isPresent()){
            projectApply.get().setDeleted(true);
            projectApplyRepo.save(projectApply.get());
        }



        return "redirect:/approve";
    }

    @PostMapping("/reject/save")
    public String rejectSaveMapping(Model model,Authentication authentication,
                                    @ModelAttribute("userId") String username,
                                    @ModelAttribute("projectId") Long projectId){

        System.out.println("Rejected a student");

        Rejected newRejected = new Rejected();
        newRejected.setProjectId(projectId);
        newRejected.setUserId(username);
        newRejected = rejectedRepo.save(newRejected);
        //delete this the approved user from applied list of that project
        Optional<ProjectApply> projectApply = projectApplyRepo.findProjectByUserIdAndProjectId(username,projectId);

        if (projectApply.isPresent()){
            projectApply.get().setDeleted(true);
            projectApplyRepo.save(projectApply.get());
        }
        return "redirect:/approve";
    }


    @GetMapping("/filter")
    public String getCourseCode(Model model, Authentication authentication){
        model.addAttribute("isCoordinator", userRepo.findUserByUsername(((UserDetails)authentication.getPrincipal()).getUsername()).get().getCoordinator());
        model.addAttribute("courseCodeList",courseCodeRepository.findAll());
        return "ListCourseCode";
    }

    @PostMapping("/filter/get")
    public String getListOfStudentWithCourseCode(@RequestParam("courseCode") String courseCode, Model model, Authentication authentication){
        model.addAttribute("isCoordinator", userRepo.findUserByUsername(((UserDetails)authentication.getPrincipal()).getUsername()).get().getCoordinator());
        List<Approved> approvedList =  approvedRepo.findApprovedByCourseCode(courseCodeRepository.findCourseCodeByCode(courseCode).get());
//        System.out.println("the size of list: " + approvedList.size());
//        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>HERE " + approvedList.size());

        List<List<Object>> mapOfApproved = new ArrayList<>();
        for (Approved approved: approvedList){
            System.out.println(approved.toString());
            Project currProject = projectRepo.findProjectById(approved.getProjectId());
            User professor = userRepo.findUserByUsername(projectCreateRepo.findProjectCreateByProjectId(approved.getProjectId()).getUserId()).orElse(null);

            if (
                    currProject!=null && currProject.getDeleted().equals(Boolean.FALSE)
                            && (currProject.getStatus().equals(ProjectStatus.FLOATED.toString()) || currProject.getStatus().equals(ProjectStatus.IN_PROGRESS.toString()))
                    && userRepo.findUserByUsername(approved.getUserId())!=null && professor != null ){
                mapOfApproved.add(
                        List.of(
                                userRepo.findUserByUsername(approved.getUserId()).get(),
//                                approved.getUserId(),
                                projectRepo.findProjectById(approved.getProjectId()),
                                professor
                        )


                );
            }

        }
        System.out.println(mapOfApproved);
        model.addAttribute("PairsOfApproved", mapOfApproved);
        return "courseCodeApproved";
    }

//    @PostMapping("/filterProject")
//    public String filteredProjects(Model model, @ModelAttribute("searchKeyword") String searchKeyword){
//        Optional<Tag> getTag = tagRepository.findTagByName(searchKeyword);
//        List<TagMapping> getAllTagMappingsForThisTag = tagMappingRepo.findTagMappingByTagId(getTag.get().getId());
//        List<Project> listProjects = new ArrayList<>();
//        for (TagMapping tagMapping: getAllTagMappingsForThisTag){
//            listProjects.add(projectRepo.findProjectById(tagMapping.getProjectId()));
//        }
//        Map<Long,String> projectToProf = new HashMap<>();
//        for (Project project: listProjects){
//            Long projectID = project.getId();
//            String userID = projectCreateRepo.findProjectCreateByProjectId(projectID).get(0).get().getUserId();
//            projectToProf.put(projectID, userRepo.findUserByUsername(userID).get().getName());
//        }
//
//        model.addAttribute("floatedProjects", listProjects);
//        model.addAttribute("projectToProf", projectToProf);
//        return "redirect://apply";
//    }

    @GetMapping("/filterProjects")
    public String filterProjects(Model model){
        model.addAttribute("projectFilter", new ProjectFilter());
        model.addAttribute("stipendOptionsList",StipendOption.values());
        return "filterProjects";
    }

//    @PostMapping("/filterProjects/get")
//    public String showFilteredProjects(Model model,
//                                       @RequestParam("title") String title,
//                                       @RequestParam("deadline") String deadline,
//                                       @RequestParam("keywords") String keywords,
//                                       @RequestParam("stipendOption") String stipendOption) throws ParseException {
////        System.out.println("here>>>>>>>>>>>>>>>>>>>" + projectFilter.getDeadline());
//
//        if (keywords==""){
//            keywords = null;
//        }
//        if (stipendOption==""){
//            stipendOption = null;
//        }
//
//        ProjectFilter projectFilter1 = new ProjectFilter(projectRepo,tagRepository,tagMappingRepo);
//        projectFilter1.setDeadline(deadline==""?null: Date.valueOf(deadline));
//        if (keywords!=null) {
//            List<String> listOfKeywords = new ArrayList<>();
//            List<String> temp = Arrays.stream(keywords.split(",")).toList();
//            for (String key: temp){
//                listOfKeywords.add(key.toLowerCase());
//            }
//            System.out.println("list of Keys: " + listOfKeywords);
//
//            projectFilter1.setKeywords(listOfKeywords);
//        }
//        if (stipendOption!=null) projectFilter1.setStipendOption(StipendOption.valueOf(stipendOption));
//        projectFilter1.setTitle(title);
//
//        List<Project> listProjects = projectFilter1.getFilteredResults();
//        Map<Long,String> projectToProf = new HashMap<>();
//        for (Project project: listProjects){
//            Long projectID = project.getId();
//            String userID = projectCreateRepo.findProjectCreateByProjectId(projectID).get(0).get().getUserId();
//            projectToProf.put(projectID, userRepo.findUserByUsername(userID).get().getName());
//        }
//        model.addAttribute("floatedProjects", listProjects);
//        model.addAttribute("projectToProf", projectToProf);
//
//        return "applyProject";
//    }

//    @GetMapping("/gotApproved")
//    public String gotApprovedList(@ModelAttribute("mapOfApproved") Map<User,Project> mapOfApproved){
//        return "courseCodeApproved";
//    }



    @GetMapping("/welcomeSuperAdmin")
    public String superAdminWelcome(){
        return "welcomeSuperAdmin";
    }

    @PostMapping("/superadmin/save")
    public String superAdminSave(@RequestParam("file") MultipartFile file){

        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0); // Assuming the first sheet is being read

            // Iterating through rows and cells
            List<String> listUsernames = new ArrayList<>();
            for (Row row : sheet) {
                for (Cell cell : row) {
                    // Process cell content (example: printing cell value)
                    //System.out.print(cell.getStringCellValue() + "\t");
                    listUsernames.add(row.getCell(1).getStringCellValue());
                }
//                System.out.println(); // Move to the next line after each row
            }

            System.out.println("File Received>>>>>>>>>");
            userRepo.setAllCoordinatorsFalse();
            for (String username: listUsernames){
//                User currUser = userRepo.findUserByUsername(username).orElse(null);
//                if (currUser != null){
//                    currUser.setCoordinator(true);
//                    userRepo.save(currUser);
//                }
//                else continue;
                userRepo.setAsCoordinator(username);
            }

            workbook.close();
//            return "redirect:/welcomeSuperAdmin";
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }
        finally {
            return "redirect:/welcomeSuperAdmin";
        }

    }


    @PostMapping("/addCourseCode")
    String addCourseCode(@RequestParam("courseCodeToAdd") String courseCode){
        CourseCode courseCodeObject = new CourseCode();
        Optional<CourseCode> courseCodeOptional = courseCodeRepository.findCourseCodeByCode(courseCode);
        if (courseCodeOptional.isPresent()){
            courseCodeObject = courseCodeOptional.get();
            courseCodeObject.setCode(courseCode);
        }
        else {
            courseCodeObject.setCode(courseCode);
        }
        courseCodeObject = courseCodeRepository.save(courseCodeObject);
        return "redirect:/welcomeSuperAdmin";
    }


}
