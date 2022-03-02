package models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * an instance of this class represents the root of the JSON output file returned by CodeShovel
 * @author Giovanni Galante
 *
 */
public class CodeShovelOutputFileRoot {
	
	private String origin;
	private String repositoryName;
	private String repositoryPath;
	private String startCommitName;
	private String sourceFileName;
	private String functionName;
	private String functionId;
	private String sourceFilePath;
	private String functionStartLine;
	private String functionEndLine;
	private String numCommitsSeen;
	private String timeTaken;
	private ArrayList<String> changeHistory;
	private HashMap<String,String> changeHistoryShort;
	private HashMap<String,ChangeObject> changeHistoryDetails;

	public HashMap<String, ChangeObject> getChangeHistoryDetails() {
		return changeHistoryDetails;
	}

	public void setChangeHistoryDetails(HashMap<String, ChangeObject> changeHistoryDetails) {
		this.changeHistoryDetails = changeHistoryDetails;
	}

	public HashMap<String, String> getChangeHistoryShort() {
		return changeHistoryShort;
	}

	public void setChangeHistoryShort(HashMap<String, String> changeHistoryShort) {
		this.changeHistoryShort = changeHistoryShort;
	}

	public ArrayList<String> getChangeHistory() {
		return changeHistory;
	}

	public void setChangeHistory(ArrayList<String> changeHistory) {
		this.changeHistory = changeHistory;
	}

	public String getTimeTaken() {
		return timeTaken;
	}

	public void setTimeTaken(String timeTaken) {
		this.timeTaken = timeTaken;
	}

	public String getNumCommitsSeen() {
		return numCommitsSeen;
	}

	public void setNumCommitsSeen(String numCommitsSeen) {
		this.numCommitsSeen = numCommitsSeen;
	}

	public String getFunctionEndLine() {
		return functionEndLine;
	}

	public void setFunctionEndLine(String functionEndLine) {
		this.functionEndLine = functionEndLine;
	}

	public String getFunctionStartLine() {
		return functionStartLine;
	}

	public void setFunctionStartLine(String functionStartLine) {
		this.functionStartLine = functionStartLine;
	}

	public String getSourceFilePath() {
		return sourceFilePath;
	}

	public void setSourceFilePath(String sourceFilePath) {
		this.sourceFilePath = sourceFilePath;
	}

	public String getFunctionId() {
		return functionId;
	}

	public void setFunctionId(String functionId) {
		this.functionId = functionId;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}

	public String getStartCommitName() {
		return startCommitName;
	}

	public void setStartCommitName(String startCommitName) {
		this.startCommitName = startCommitName;
	}

	public String getRepositoryPath() {
		return repositoryPath;
	}

	public void setRepositoryPath(String repositoryPath) {
		this.repositoryPath = repositoryPath;
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}
	
	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	
}
