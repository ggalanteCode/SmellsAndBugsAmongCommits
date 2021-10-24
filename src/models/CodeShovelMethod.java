package models;

public class CodeShovelMethod {

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

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
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

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public int getLinesOfCodeAdded() {
        return linesOfCodeAdded;
    }

    public void setLinesOfCodeAdded(int linesOfCodeAdded) {
        this.linesOfCodeAdded = linesOfCodeAdded;
    }

    public int getLinesOfCodeDeleted() {
        return linesOfCodeDeleted;
    }

    public void setLinesOfCodeDeleted(int linesOfCodeDeleted) {
        this.linesOfCodeDeleted = linesOfCodeDeleted;
    }

    public int getLinesOfCodeModified() {
        return linesOfCodeModified;
    }

    public void setLinesOfCodeModified(int linesOfCodeModified) {
        this.linesOfCodeModified = linesOfCodeModified;
    }

    private long identifier;
    private String sourceFileName;
    private String functionName;
    private String startCommitName;
    private String previousCommitName;
    private String changeType;
    private int linesOfCodeAdded = 0;
    private int linesOfCodeDeleted = 0;
    private int linesOfCodeModified = 0;

    public CodeShovelMethod() {

    }

    @Override
    public String toString() {
        return "CodeShovelMethod{" +
                "identifier=" + identifier +
                ", sourceFileName='" + sourceFileName + '\'' +
                ", functionName='" + functionName + '\'' +
                ", startCommitName='" + startCommitName + '\'' +
                ", previousCommitName='" + previousCommitName + '\'' +
                ", changeType='" + changeType + '\'' +
                ", linesOfCodeAdded=" + linesOfCodeAdded +
                ", linesOfCodeDeleted=" + linesOfCodeDeleted +
                ", linesOfCodeModified=" + linesOfCodeModified +
                '}';
    }
}
