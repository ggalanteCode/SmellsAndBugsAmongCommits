package repository;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import database.DbHandler;
import gui.GitTokenRequest;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.revwalk.RevCommit;
import models.Commit;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevWalk;
import com.jcabi.github.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import models.IssueCollection;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHRateLimit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.PagedIterable;

import javax.swing.*;


/**
 * Everything you need to work with a Git-based source code repository.
 * @author Francesco Florio
 */
public class RepositoryHandler {
    
    public static final String URL_SUFFIX = ".git";
    public static final String URL_PREFIX=".com/";
    private String remoteURL;
    private Path localPath; 
    private static Git gitRepo;
    private TreeSet<Commit> commits;
    private GHRepository repository; // utilizzato per il repository
    private Issues issues; //iteratore di tutte le issue
    private GitHub github;
    public String token;
    

    /**
    * Constructor taking the URL of a Git remote repository
    * Instantiating a RepositoryHandler will clone the specified repository in a temporary directory
    * @param URL URL of the Git remote repository.
    * @throws IOException
    * @throws GitAPIException
    */
    public RepositoryHandler(String URL) throws IOException, GitAPIException {

        try {
            if(Files.notExists(Paths.get(URL)))
                throw new IOException();
            this.gitRepo=Git.open(new File(URL));
            this.localPath=Paths.get(URL);
            this.remoteURL= this.gitRepo.getRepository().getConfig().getString("remote", "origin", "url");
            repository=this.entryGitApi();//restituisce il repository che sto considerando
            //issues=this.getIterateIssues();
        } catch (java.nio.file.InvalidPathException | IOException e) {
                // il path non esite: è un url remoto?
                this.remoteURL = URL; 
                this.localPath = Files.createTempDirectory("SBAC-" + this.repoNameFromURI());
                cloneGitRepository();
        }
        this.commits = new TreeSet<>();
        this.findAllCommits();
    }

    /**
     * Delete a folder given its path.
     * @param url
     */
    public static void deleter(String url) {

        try {

            File projectDirectory = new File(DbHandler.projectDeleter(url));
            deleteUtils(projectDirectory);
            projectDirectory.delete();
            System.out.println("Old repo deleted.\n");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Recursively delete a folder and its contents.
     * @param folder
     */
    private static void deleteUtils (File folder) {

        for (File f : Objects.requireNonNull(folder.listFiles())) {
            if (f.isDirectory()) {
                deleteUtils(f);
                f.delete();
            } else {
                if (f.getPath().endsWith(".pack")){
                    gitRepo.getRepository().close();
                }
               if (f.delete()) {
                   System.out.println(f.getPath() + ":  deleted");
               } else {
                   System.err.println(f.getPath() + ":  NOT deleted");
               }
            }
        }
        System.out.println();
    }
    
    /**
     * Get the remote URL of the Git repository
     * @return String
     */
    public String getRemoteURL() {
        return remoteURL;
    }

    /**
     * Get the path of the local copy of the Git Repository
     * @return Path
     */
    public Path getLocalPath() {
        return localPath;
    }

    /**
     * Get all the commits of this Git repository
     * @return TreSet&lt;Commit&gt;
     */
    public TreeSet<Commit> getCommits() {
        return commits;
    }  
    
    /**
     * Get the commit identified by its hash
     * @param String hash id of the commit
     * @return Commit
     */
    private Commit getCommit(String version){
        for(Commit c: this.commits)
            if(c.getVersion().equals(version))
                return c;
        return null;
    }
    
    /**
     * Get the total number of commits of this Git repository
     * @return long
     */
    public long totalCommits() {
        return this.commits.size();
    }
    
    /**
     * Get the last commit of this repository in chronological order
     * @return Commit
     */
    public Commit getLastCommit(){
        return commits.last();
    }
    
    /**
     * Get the first commit of this repository in chronological order
     * @return Commit
     */
    public Commit getFirstCommit(){
        return commits.first();
    }
    
    /**
     * Get all the commit since the specified date
     * @param start start date to filter
     * @return TreSet&lt;Commit&gt;
     */    
    public TreeSet<Commit> getCommitsSince(Date start){
        return commits.stream().filter(c-> c.getCommitDate().after(Date.from(start.toInstant().minus(1, ChronoUnit.DAYS)))).collect(Collectors.toCollection(()->new TreeSet<>()));
    }
    
    /**
     * Get all the commit between the specified dates
     * @param start start date to filter
     * @param end end date to filter
     * @return TreSet&lt;Commit&gt;
     */     
    public TreeSet<Commit> getCommitsBetweenDates(Date start, Date end){
        return this.getCommitsSince(start).stream().filter(c-> c.getCommitDate().before(Date.from(end.toInstant().plus(1, ChronoUnit.DAYS)))).collect(Collectors.toCollection(()->new TreeSet<>()));
    }
    
    /**
     * Get all the commits that contains in the messagge a reference of fixing code
     * @return TreSet&lt;Commit&gt;
     */
    public TreeSet<Commit> getBugFixerCommits(){
        return commits.stream().filter(c-> c.getMsg().toLowerCase().replace("\n", " ").matches("(.*)(refactor|bug|fix|close|(#\\d*))(.*)")).collect(Collectors.toCollection(()->new TreeSet<>()));
    }
    
    /**
     * Get all the commits mark as release
     * @return TreSet&lt;Commit&gt;
     */
    public TreeSet<Commit> getRealeaseCommits(){
        return commits.stream().filter(c->c.getRelease()!=null).collect(Collectors.toCollection(()->new TreeSet<>()));
    }
    
    /**
     * 
     * @param date date to filter
     * @return TreSet&lt;Commit&gt;
     */
    public TreeSet<Commit> getCommitForDate(Date date){
        return commits.stream().filter(c-> c.getCommitDate().after(Date.from(date.toInstant()))).collect(Collectors.toCollection(()->new TreeSet<>())).stream().filter(c-> c.getCommitDate().before(Date.from(date.toInstant().plus(22, ChronoUnit.HOURS).plus(59, ChronoUnit.MINUTES)))).collect(Collectors.toCollection(()->new TreeSet<>()));
    }
    
    /**
     * Get all the commits that contains in the messagge a reference of fixing code
     * @param version hash id of the commit to check
     * @throws GitAPIException
     */
    public void checkoutCommit(String version) {
        try {
            this.gitRepo.checkout().setName(version).setStartPoint(version).call();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Questa release potrebbe non essere analizzabile",
                    "Attenzione", JOptionPane.INFORMATION_MESSAGE);
            e.printStackTrace();

        }
    }

    
    /**
     * Get all the commits that contains in the messagge a reference of fixing code
     * @throws GitAPIException
     */
    public void reset() throws GitAPIException {
        try {
            this.gitRepo.checkout().setName("master").call();
        } catch(RefNotFoundException e) {
            this.gitRepo.branchCreate().setName("master").call();
        }
    }
    
    /**
     * Create a local copy of main branch of the Git remote repository
     * @throws GitAPIException
     * @throws IOException
     */
    private void cloneGitRepository() throws GitAPIException, IOException {
        try (Git result = Git.cloneRepository()
                .setURI(remoteURL)
                .setDirectory(localPath.toFile())
                .call()) {
            this.gitRepo=result;
        } catch (Exception e) {
            System.err.println("Invalid repo? RepositoryHandler 219.\n");
            e.printStackTrace();
        }
    }
    
    /**
     * Extract all the commits of the Git repository including release commits
     * @throws GitAPIException
     * @throws IOException 
     */
    public void findAllCommits() throws GitAPIException, IOException {
        //search commit in main branch
        for (RevCommit c : this.gitRepo.log().call()) {
            commits.add(new Commit(c.getId().getName(),remoteURL.replace(".git", "") +File.separator+"tree"+File.separator+c.getId().getName() , c.getFullMessage().trim() , c.getCommitterIdent().getWhen()));
        }
        
        //search release commit (annotated tag)
              
        List<Ref> tmp= gitRepo.tagList().call();
        for(Ref r : tmp){
            if(r.getName().toUpperCase().matches("(.*)(\\d.\\d)(.*)")){ //version
                if(r.getPeeledObjectId()!=null){
                    boolean trovato=false;
                    Commit c = getCommit(r.getPeeledObjectId().name());
                    if(c!=null) //commit main branch
                        c.setRelease(r.getName().replace("refs"+File.separator+"tags"+File.separator, ""));
                    else { //commit other branch
                        RevWalk walk = new RevWalk(gitRepo.getRepository());
                        RevCommit rc = walk.parseCommit(r.getPeeledObjectId());
                        this.commits.add(new Commit(rc.getId().getName(),remoteURL.replace(".git", "") +File.separator+"tree"+File.separator+ rc.getId().getName() , rc.getFullMessage().trim() , r.getName().replace("refs"+File.separator+"tags"+File.separator, ""), rc.getCommitterIdent().getWhen()));
                    }
                }
                else {
                    if(r.getObjectId()!=null){
                        Commit c = getCommit(r.getObjectId().name());
                        if (c!=null)
                            c.setRelease(r.getName().replace("refs"+File.separator+"tags"+File.separator, ""));
                    }
                }
            }
        }
    }
    /**
    * Extract a Git repository name from its URL.
    * @return String 
    */
    public String repoNameFromURI() {
        int lastSlashIx = remoteURL.lastIndexOf("/");

        int lastSuffIx = remoteURL.lastIndexOf(URL_SUFFIX);
        if (lastSuffIx < 0)
                lastSuffIx = remoteURL.length();

        if (lastSlashIx < 0 || lastSuffIx <= lastSlashIx)
                throw new RuntimeException("Error, ill-formed url: " + remoteURL);

        return remoteURL.substring(lastSlashIx + 1, lastSuffIx);
    }
    
    /**
     * extracts the repository name to the form developname/repository name
     * @return String
     */
    public String coordinatesFromURI(){
        int gitHubIndex=remoteURL.lastIndexOf(URL_PREFIX);
        int indexSlash=URL_PREFIX.indexOf("/");
        //System.out.println(gitHubIndex);
        int lastSuffix=remoteURL.lastIndexOf(URL_SUFFIX);
        
       if (lastSuffix < 0)
                lastSuffix = remoteURL.length();

        if (gitHubIndex < 0 || lastSuffix <= gitHubIndex)
                throw new RuntimeException("Error, ill-formed url: " + remoteURL);
        
        return remoteURL.substring(gitHubIndex+indexSlash+1, lastSuffix);
    }
   
    /**
     * method that authenticates by personal token to use the github library
     * @return GHRepository
     */
    public GHRepository entryGitApi(){
        new GitTokenRequest(this);
        if (token == null) {
            entryGitApi();
        }
        String coordinates=this.coordinatesFromURI();
        try{
        System.out.println(coordinates);
        github = new GitHubBuilder().withOAuthToken(token).build();
        GHRepository repo=github.getRepository(coordinates);
        return repo;
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }
     
    /**
     * method that collects all the bugs reported within the project that you are analyzing
     * @param bugInDb bugs already entered in the database
     * @return ArrayList
     */
    public ArrayList<IssueCollection> getAllIssue(ArrayList<IssueCollection> bugInDb){
        System.out.println("Raccolta bug");
        ArrayList<IssueCollection>allIssues=new ArrayList<>();
        PagedIterable<GHIssue>iterableIssue=repository.listIssues(GHIssueState.ALL);
        try{
            List<GHIssue>list=iterableIssue.toList();
            if(list!=null && !(list.isEmpty())){
                for(int i=0;i<list.size();i++){
                    if(!(list.get(i).isPullRequest()) && !(researchNewIssue(list.get(i),bugInDb))){
                        IssueCollection ic=new IssueCollection(list.get(i).getTitle(),list.get(i).getBody(),list.get(i).getNumber());
                        ic.setState(list.get(i).getState());
                        if(!(ic.bugIsAlreadyPresent(allIssues))){
                            ic.setLabel(this.getLabelIssue(list.get(i).getLabels()));
                            allIssues.add(ic);
                        }
                    }
                }
            }
        }catch(IOException e){
            System.err.println(e);
            return null;
        }
        
        if(!(allIssues.isEmpty()))
            this.getFileNameCommitModified(allIssues);
        return allIssues;
    }
    
    /**
     * method for finding new bugs
     * @param issue issue that you're analyzing
     * @param bugAlreadyPresent bugs already collected
     * @return boolean
     */
    public boolean researchNewIssue(GHIssue issue,ArrayList<IssueCollection>bugAlreadyPresent){
        GHIssueState ghState=issue.getState();
        if(bugAlreadyPresent!=null && !(bugAlreadyPresent.isEmpty())){
            for(int i=0;i<bugAlreadyPresent.size();i++){
                if(bugAlreadyPresent.get(i).getTitle().equals(issue.getTitle()) && bugAlreadyPresent.get(i).getState().toUpperCase().equals(ghState.name()))
                    return true;
            }
        }
        return false;
    }
    
    /**
     * displays the remaining limit rate
     */
    public void getRateLimit(){
        try{
            GHRateLimit limit=github.getRateLimit();
            System.out.println(limit.getRemaining());
        }catch(IOException e){
            System.out.println(e);
        }
    }
    
    /**
     * search if there are references to bugs in the list within the commit message. 
     * If present collect modified file name and number of modified lines
     * @param allIssues list containing all the bugs collected
     */
    public void getFileNameCommitModified(ArrayList<IssueCollection> allIssues){
        ArrayList<String> list;
        
        for(Commit c:commits){
            list=c.getNumberIssue();
            if(list!=null && list.size()>0){
                for(int i=0;i<list.size();i++){
                    if(list.get(i).matches("\\d*") && !(list.get(i).equals(""))){
                        c.verifyNumberIssue(list.get(i), allIssues,repository);
                           
                    }
                }
            }
        }
    }
    
    /**
     * returns all labels associated with the bug
     * @param label labels associated with the bug
     * @return ArrayList
     * @throws AssertionError 
     */
    public ArrayList<String> getLabelIssue(Collection<GHLabel> label) throws AssertionError{
        ArrayList<String> labelName=new ArrayList<>();
        if(label!=null){
            for(GHLabel l:label){
                labelName.add(l.getName());
            }
            return labelName;
        }else
            return null;
    }
  
}

