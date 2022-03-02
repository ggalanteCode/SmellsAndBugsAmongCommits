package models;

import java.util.ArrayList;
import java.util.List;

public class CodeShovelClass {

    @Override
	public String toString() {
		return "CodeShovelClass [identifier=" + identifier + ", sourceFileName=" + sourceFileName + ", changeType="
				+ changeType + ", startCommitName=" + startCommitName + ", previousCommitName=" + previousCommitName
				+ ", completeSourceFileName=" + completeSourceFileName + "]";
	}

	public long getIdentifier() {
        return identifier;
    }

    public void setIdentifier(long identifier) {
        this.identifier = identifier;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getStartCommitName() {
        return startCommitName;
    }

    public void setStartCommitName(String startCommitName) {
        this.startCommitName = startCommitName;
    }

    public String getPreviousCommitName() {
        return previousCommitName;
    }

    public void setPreviousCommitName(String previousCommitName) {
        this.previousCommitName = previousCommitName;
    }

    private long identifier;
    private String sourceFileName;
    private String changeType;
    private String startCommitName;
    private String previousCommitName = null;
    private String completeSourceFileName;

    public String getCompleteSourceFileName() {
		return completeSourceFileName;
	}

	public void setCompleteSourceFileName(String completeSourceFileName) {
		this.completeSourceFileName = completeSourceFileName;
	}

	public CodeShovelClass() {

    }

    
}
