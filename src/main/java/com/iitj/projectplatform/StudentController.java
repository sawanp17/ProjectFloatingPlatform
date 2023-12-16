package com.iitj.projectplatform;

import com.iitj.projectplatform.Repositories.*;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
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



    @GetMapping("/welcome")
    public String greeting(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String username = userDetails.getUsername();
        Optional<User>currentUser = userRepo.findUserByUsername(username);

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
                model.addAttribute("isCoordinator", false);

            }
            else if (authority.getAuthority().equals("ROLE_COORDINATOR")){
                System.out.println(
                        ">> adding coordinator to model"
                );

                String role = currentUser.get().getRole().name();
                String user_name = currentUser.get().getUsername();
                String name = currentUser.get().getName();
                String email = currentUser.get().getEmail();

                model.addAttribute("role", role);
                model.addAttribute("user_name", user_name);
                model.addAttribute("name", name);
                model.addAttribute("email", email);


                model.addAttribute("isCoordinator", true);
                model.addAttribute("isStudent", false);
                model.addAttribute("isProfessor", false);
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
                model.addAttribute("isCoordinator", false);
                model.addAttribute("isStudent", false);
                model.addAttribute("isProfessor", true);
            }
            break;
        }

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
            else if (user.getRole().equals(Role.Coordinator)){
                toSave.setRole(Role.Coordinator);
            }
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
        model.addAttribute("departmentList", Departments.values());
        model.addAttribute("courseCodeList", CourseCode.values());
        model.addAttribute("projectTypeList", ProjectType.values());

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



        Project project = new Project();
        model.addAttribute("project",project);
        model.addAttribute("isEdit", false);

        return "createProject";
    }

    @PostMapping("/create/save")
    public String saveProject(@ModelAttribute("project") Project project,
                              @ModelAttribute("projectId") Long projectId,
                              @ModelAttribute("isEdit") Boolean isEdit,
                              Authentication authentication){

        System.out.println("Editing Project " + projectId);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();


        Project toSave ;
        if (projectId == -1){
            toSave = new Project();
        }
        else toSave = projectRepo.findProjectById(projectId);

        toSave.setTitle(project.getTitle());
        toSave.setDepartment(project.getDepartment());
        toSave.setDeadline(project.getDeadline());
        toSave.setDescription(project.getDescription());
        toSave.setPreReq(project.getPreReq());
        toSave.setMaxLim(project.getMaxLim());
        toSave.setStatus(project.getStatus());
        toSave.setProjectType(project.getProjectType());
        toSave.setStipendOption(project.getStipendOption());
        toSave.setStipendAmount(project.getStipendAmount());

        toSave = projectRepo.save(toSave);

        //cascade add
        if (!isEdit){
            ProjectCreate projectCreate = new ProjectCreate(username,toSave.getId());
            projectCreateRepo.save(projectCreate);
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
                currentRole = Role.Student;
            }
            else {
                System.out.println(
                        ">> (myProjects) adding prof to model"
                );
                model.addAttribute("isStudent", false);
                model.addAttribute("isProfessor", true);
                currentRole = Role.Professor;
            }
            break;
        }

        List<Project> myProjectList = new ArrayList<>();
        List<Optional<ProjectCreate>> createdProjects = new ArrayList<>();
        if (currentRole.equals(Role.Professor)) createdProjects = projectCreateRepo.findProjectCreateByUserId(username);

        List<Optional<ProjectApply>> applyProjects = new ArrayList<>();
        List<Project> approvedProjects = new ArrayList<>();
        List<Project> rejectedProjects = new ArrayList<>();

        if (currentRole.equals(Role.Student)){
            applyProjects = projectApplyRepo.findProjectApplyByUserId(username);
            List<Approved> approvedObjectList = approvedRepo.findApprovedByUserId(username);
            for (Approved it: approvedObjectList){
                approvedProjects.add(projectRepo.findProjectById(it.getProjectId()));
            }
            List<Rejected> rejectedObjectList = rejectedRepo.findRejectedByUserId(username);
            for (Rejected it: rejectedObjectList){
                rejectedProjects.add(projectRepo.findProjectById(it.getProjectId()));
            }

            model.addAttribute("approvedList", approvedProjects);
            model.addAttribute("rejectedList", rejectedProjects);
        }


        List<Project> projectList = new ArrayList<>();

        if (currentRole.equals(Role.Professor)){
            for (Optional<ProjectCreate> projectCreateIterator: createdProjects){
                if (projectCreateIterator.isPresent()){
                    Optional<Project> foundProject = projectRepo.findById(projectCreateIterator.get().getProjectId());
                    if (foundProject.isPresent()){
                        projectList.add(foundProject.get());
                    }
                }
            }
        }
        //else: all other than professor, might modify to else if in future
        else {
            for (Optional<ProjectApply> projectApplyIterator: applyProjects){
                if (projectApplyIterator.isPresent()){
                    Optional<Project> foundProject = projectRepo.findById(projectApplyIterator.get().getProjectId());
                    if (foundProject.isPresent() && !approvedProjects.contains(foundProject) && !rejectedProjects.contains(foundProject)){
                        projectList.add(foundProject.get());
                    }
                }
            }
        }


        model.addAttribute("listOfProjects", projectList);
        return "myProjects";
    }

    @PostMapping("/delete")
    public String deleteProject(@ModelAttribute("projectId") Long projectId){

        //cascade deletes
        List<Optional<ProjectCreate>> projectCreated = projectCreateRepo.findProjectCreateByProjectId(projectId);
        for (Optional<ProjectCreate> projectCreatedIterator: projectCreated){
            projectCreateRepo.deleteById(projectCreatedIterator.get().getId());
        }

        List<ProjectApply> projectApplied = projectApplyRepo.findProjectApplyByProjectId(projectId);
        for (ProjectApply projectApplyIterator: projectApplied){
            projectApplyRepo.deleteById(projectApplyIterator.getId());
        }

        projectRepo.deleteById(projectId);

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
        Boolean isStudent=false, isProfessor=false;
        if (project == null){
            System.out.println(">> Project does not exist.");
        }
        else {
            role = user.get().getRole();
            if (role.equals(Role.Student)){
                isStudent = true;
                isProfessor = false;
            }
            else {
                isStudent = false;
                isProfessor = true;
            }
            model.addAttribute("isStudent", isStudent);
            model.addAttribute("isProf", isProfessor);
            model.addAttribute("listProjectTypes", ProjectType.values());
            model.addAttribute("listStipendOptions", StipendOption.values());
            model.addAttribute("listDepartments", Departments.values());
            model.addAttribute("project", project);
            model.addAttribute("isEdit", true);
            model.addAttribute("projectId", projectId);
        }

        return "createProject";
    }


    @GetMapping("/apply")
    public String applyProject(Model model, Authentication authentication){
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
        model.addAttribute("courseCodeList", CourseCode.values());




        List<Project> floatedProjects = projectRepo.findProjectByStatus("FLOATED");
        List<Project> toRemove = new ArrayList<>();
        for (Project project: floatedProjects){
            if (rejectedRepo.findRejectedByUserIdAndProjectId(username,project.getId()).isPresent()
                || approvedRepo.findApprovedByUserIdAndProjectId(username,project.getId()).isPresent()
                    || projectApplyRepo.findProjectByUserIdAndProjectId(username,project.getId()).isPresent()
            ){
                toRemove.add(project);
            }
        }
        floatedProjects.removeAll(toRemove);

        Map<Long,String> projectToProf = new HashMap<>();
        for (Project project: floatedProjects){
            Long projectID = project.getId();
            String userID = projectCreateRepo.findProjectCreateByProjectId(projectID).get(0).get().getUserId();
            projectToProf.put(projectID, userRepo.findUserByUsername(userID).get().getName());
        }

        model.addAttribute("floatedProjects", floatedProjects);
        model.addAttribute("projectToProf", projectToProf);
        return "applyProject";
    }

    @PostMapping("apply/save")
    public String saveAppliedProject(
            @RequestParam("projectId") Long projectId,
            @RequestParam("courseCode") CourseCode courseCode,
            @RequestParam("resumeLink") String resumeLink,
            Authentication authentication
    ){
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

        List<Optional<ProjectCreate>> myprojects = projectCreateRepo.findProjectCreateByUserId(username);
        List<Long> projectIds = new ArrayList<>();
        List<ProjectApply> myProjectApplicants = new ArrayList<>();



        for (Optional<ProjectCreate> projectCreate: myprojects){
            if (projectCreate.isPresent()){
                if (projectCreate.get().getUserId().equals(username)){
                    projectIds.add(projectCreate.get().getProjectId());
                    List<ProjectApply> projectApplicants = projectApplyRepo.findProjectApplyByProjectId(projectCreate.get().getProjectId());
                    myProjectApplicants.addAll(projectApplicants);
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

        System.out.println("here*** " + username + projectId);
        Approved newApproved = new Approved();
        newApproved.setProjectId(projectId);
        newApproved.setUserId(username);
        newApproved.setCourseCode(projectApplyRepo.findProjectApplyByProjectId(projectId).get(0).getCourseCode());
        newApproved = approvedRepo.save(newApproved);

        //delete this approved user from applied list of that project
        Optional<ProjectApply> projectApply = projectApplyRepo.findProjectByUserIdAndProjectId(username,projectId);

        if (projectApply.isPresent()){
            projectApplyRepo.delete(projectApply.get());
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
            projectApplyRepo.delete(projectApply.get());
        }
        return "redirect:/approve";
    }


    @GetMapping("/filter")
    public String getCourseCode(Model model){
        model.addAttribute(CourseCode.values());
        return "ListCourseCode";
    }

    @PostMapping("/filter/get")
    public String getListOfStudentWithCourseCode(@RequestParam("courseCode") CourseCode courseCode, Model model){
        List<Approved> approvedList =  approvedRepo.findApprovedByCourseCode(courseCode);
        Map<User,Project> mapOfApproved = new HashMap<>();
        for (Approved approved: approvedList){
            mapOfApproved.put(
                    userRepo.findUserByUsername(approved.getUserId()).get(),
                    projectRepo.findProjectById(approved.getProjectId())
            );
        }
        model.addAttribute("mapOfApproved", mapOfApproved);
        return "courseCodeApproved";
    }

//    @GetMapping("/gotApproved")
//    public String gotApprovedList(@ModelAttribute("mapOfApproved") Map<User,Project> mapOfApproved){
//        return "courseCodeApproved";
//    }

}
