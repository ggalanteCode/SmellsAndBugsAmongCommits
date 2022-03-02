package models;

import java.util.ArrayList;

/**
 * an instance of this class represents a single change object in the JSON output file returned by
 * CodeShovel
 * @author Giovanni Galante
 *
 */
public class ChangeObject {
	private String type;
	private String commitMessage;
	private String commitDate;
	private String commitName;
	private String commitAuthor;
	private String diff;
	private String actualSource;
	private String path;
	private String functionStartLine;
	private String functionName;
	private ArrayList<ChangeObject> subchanges;
	private String commitDateOld;
	private String commitNameOld;
	private String commitAuthorOld;
	private String daysBetweenCommits;
	private String commitsBetweenForRepo;
	private String commitsBetweenForFile;
	private ExtendedDetails extendedDetails;
	
	public ExtendedDetails getExtendedDetails() {
		return extendedDetails;
	}
	public void setExtendedDetails(ExtendedDetails extendedDetails) {
		this.extendedDetails = extendedDetails;
	}
	public String getCommitsBetweenForFile() {
		return commitsBetweenForFile;
	}
	public void setCommitsBetweenForFile(String commitsBetweenForFile) {
		this.commitsBetweenForFile = commitsBetweenForFile;
	}
	public String getCommitsBetweenForRepo() {
		return commitsBetweenForRepo;
	}
	public void setCommitsBetweenForRepo(String commitsBetweenForRepo) {
		this.commitsBetweenForRepo = commitsBetweenForRepo;
	}
	public String getDaysBetweenCommits() {
		return daysBetweenCommits;
	}
	public void setDaysBetweenCommits(String daysBetweenCommits) {
		this.daysBetweenCommits = daysBetweenCommits;
	}
	public String getCommitAuthorOld() {
		return commitAuthorOld;
	}
	public void setCommitAuthorOld(String commitAuthorOld) {
		this.commitAuthorOld = commitAuthorOld;
	}
	public String getCommitNameOld() {
		return commitNameOld;
	}
	public void setCommitNameOld(String commitNameOld) {
		this.commitNameOld = commitNameOld;
	}
	public String getCommitDateOld() {
		return commitDateOld;
	}
	public void setCommitDateOld(String commitDateOld) {
		this.commitDateOld = commitDateOld;
	}
	public ArrayList<ChangeObject> getSubchanges() {
		return subchanges;
	}
	public void setSubchanges(ArrayList<ChangeObject> subchanges) {
		this.subchanges = subchanges;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	public String getFunctionStartLine() {
		return functionStartLine;
	}
	public void setFunctionStartLine(String functionStartLine) {
		this.functionStartLine = functionStartLine;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getActualSource() {
		return actualSource;
	}
	public void setActualSource(String actualSource) {
		this.actualSource = actualSource;
	}
	public String getDiff() {
		return diff;
	}
	public void setDiff(String diff) {
		this.diff = diff;
	}
	public String getCommitAuthor() {
		return commitAuthor;
	}
	public void setCommitAuthor(String commitAuthor) {
		this.commitAuthor = commitAuthor;
	}
	public String getCommitName() {
		return commitName;
	}
	public void setCommitName(String commitName) {
		this.commitName = commitName;
	}
	public String getCommitDate() {
		return commitDate;
	}
	public void setCommitDate(String commitDate) {
		this.commitDate = commitDate;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCommitMessage() {
		return commitMessage;
	}
	public void setCommitMessage(String commitMessage) {
		this.commitMessage = commitMessage;
	}
}
