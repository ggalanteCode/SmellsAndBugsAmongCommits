package models;

public class CodeShovelMethod {

    @Override
	public String toString() {
		return "CodeShovelMethod [identifier=" + identifier + ", sourceFileName=" + sourceFileName + ", functionName="
				+ functionName + ", functionSignature=" + functionSignature + ", startCommitName=" + startCommitName
				+ ", previousCommitName=" + previousCommitName + ", changeType=" + changeType + ", linesOfCodeAdded="
				+ linesOfCodeAdded + ", linesOfCodeDeleted=" + linesOfCodeDeleted + ", linesOfCodeModified="
				+ linesOfCodeModified + ", isStatic=" + isStatic + ", isPublic=" + isPublic + ", isPrivate=" + isPrivate
				+ ", isProtected=" + isProtected + ", isFinal=" + isFinal + ", isAbstract=" + isAbstract
				+ ", isTransient=" + isTransient + ", isSynchronized=" + isSynchronized + ", isVolatile=" + isVolatile
				+ "]";
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
    private String functionSignature;
    public String getFunctionSignature() {
		return functionSignature;
	}

	public void setFunctionSignature(String functionSignature) {
		this.functionSignature = functionSignature;
	}

	private String startCommitName;
    private String previousCommitName = null;
    private String changeType;
    private int linesOfCodeAdded = 0;
    private int linesOfCodeDeleted = 0;
    private int linesOfCodeModified = 0;
    private boolean isStatic = false;
    private boolean isPublic = false;
    private boolean isPrivate = false;
    private boolean isProtected = false;
    private boolean isFinal = false;
    private boolean isAbstract = false;
    private boolean isTransient = false;
    private boolean isSynchronized = false;
    private boolean isVolatile = false;

    public boolean isVolatile() {
		return isVolatile;
	}

	public void setVolatile(boolean isVolatile) {
		this.isVolatile = isVolatile;
	}

	public boolean isSynchronized() {
		return isSynchronized;
	}

	public void setSynchronized(boolean isSynchronized) {
		this.isSynchronized = isSynchronized;
	}

	public boolean isTransient() {
		return isTransient;
	}

	public void setTransient(boolean isTransient) {
		this.isTransient = isTransient;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}

	public boolean isProtected() {
		return isProtected;
	}

	public void setProtected(boolean isProtected) {
		this.isProtected = isProtected;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

	public CodeShovelMethod() {

    }

    
}
