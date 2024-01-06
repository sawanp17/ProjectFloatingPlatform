package com.iitj.projectplatform;

import com.iitj.projectplatform.Repositories.ProjectRepo;
import com.iitj.projectplatform.Repositories.TagMappingRepo;
import com.iitj.projectplatform.Repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.*;
@Service
public class ProjectFilter {
    String title;
    Date deadline=null;
    List<String> keywords;
    StipendOption stipendOption=null;


    @Autowired
    private ProjectRepo projectRepo;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private TagMappingRepo tagMappingRepo;

    public ProjectFilter(String title, Date deadline, List<String> keywords, StipendOption stipendOption) {
        this.title = title;
        this.deadline = deadline;
        this.keywords = keywords;
        this.stipendOption = stipendOption;
    }

    public ProjectFilter(ProjectRepo projectRepo, TagRepository tagRepository, TagMappingRepo tagMappingRepo) {
        this.projectRepo = projectRepo;
        this.tagRepository = tagRepository;
        this.tagMappingRepo = tagMappingRepo;
    }

    List<Project> getFilteredResults(){
        List<Project> filteredByTitle = projectRepo.findProjectByIsDeleted(Boolean.FALSE);
        List<Project> filteredByDate = filteredByTitle;
        List<Project> filteredByKeywords = filteredByTitle;
        List<Project> filteredByStipendOption = filteredByTitle;


        if (!title.isEmpty()){
            System.out.println("title not empty"+title);
//            String query = "SELECT p FROM Project WHERE p.title LIKE '%" + title + "%'";
//            System.out.println("getQuery " + query);
            filteredByTitle = projectRepo.findByTitleIsContainingIgnoreCase(title);
        }
        if (! (deadline==null)){
            System.out.println(deadline);
            filteredByDate = projectRepo.searchProjectByDeadline(deadline);
        }


        //Sorted projects along with ranking algorithm
//        System.out.println(keywords.get(0) + " " + keywords.size());
        if (!(keywords==null)){
//            List<Tag> getTagsEntered = tagRepository.findTagsByName(keywords);
            for (String it: keywords){
                keywords.set(keywords.indexOf(it),it.toLowerCase());
            }

            List<Tag> getTagsEntered = new ArrayList<>();
            System.out.println("keys: " + keywords.toString());

            for (String it: keywords){
                try{
                    tagRepository.findTagByName(it);
                    getTagsEntered.add(tagRepository.findTagByName(it).get());
                }
                catch (NoSuchElementException e){
                    getTagsEntered = new ArrayList<>();
                    System.out.println("No such tag exists: " + it);
                    break;

                }
            }

            List<Long> getTagIds = new ArrayList<>();
            getTagsEntered.forEach(it->getTagIds.add(it.getId()));

            System.out.println("gettagIds " + getTagIds.toString());
            List<TagMapping> getAllTagMaps = new ArrayList<>();
            getTagIds.forEach(it-> getAllTagMaps.addAll(tagMappingRepo.findTagMappingByTagId(it)));
            System.out.println("getAllTagMaps " + getAllTagMaps.toString());

            //            getAllTagMaps.tagMappingRepo.findTagMappingsByTagId(getTagIds);
            HashMap<Project, Integer> projectCounts = new HashMap<>();
            for (TagMapping tagMapping: getAllTagMaps){
                System.out.println("loop and tagMapping " + tagMapping.toString());

                if (projectCounts.containsKey(projectRepo.findProjectById(tagMapping.getProjectId()))){
                    projectCounts.put(
                            projectRepo.findProjectById(tagMapping.getProjectId()),
                            projectCounts.get(projectRepo.findProjectById(tagMapping.getProjectId())) + 1
                    );
                }
                else {
                    projectCounts.put(
                            projectRepo.findProjectById(tagMapping.getProjectId()),
                            1
                    );
                }
            }
            System.out.println("hashmap " + projectCounts.toString() );

            List<Map.Entry<Project,Integer>> entryList = new ArrayList<>();
            for (Map.Entry<Project,Integer> it: projectCounts.entrySet()){
                entryList.add(it);
            }

            entryList.sort(Map.Entry.comparingByValue());
            System.out.println("entryList" + entryList.toString());

            filteredByKeywords = new ArrayList<>();   //SORTED PROJECTS
            for (Map.Entry<Project,Integer> it: entryList){
                filteredByKeywords.add(it.getKey());
            }
        }


        if (!(stipendOption==null)) filteredByStipendOption = projectRepo.findProjectsByStipendOption(stipendOption);
        System.out.println("Lists: ");
        System.out.println(filteredByDate.toString());
        System.out.println(filteredByKeywords.toString());
        System.out.println(filteredByTitle.toString());
        System.out.println(filteredByStipendOption.toString());

        List<Project> filteredAll = filteredByTitle.stream()
                .filter(filteredByDate::contains).filter(filteredByKeywords::contains).filter(filteredByStipendOption::contains).toList();
        System.out.println(">>>>>>>> Filtered: " + filteredAll.toString());


        return filteredAll;

    }

    public ProjectFilter() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public StipendOption getStipendOption() {
        return stipendOption;
    }

    public void setStipendOption(StipendOption stipendOption) {
        this.stipendOption = stipendOption;
    }
}
