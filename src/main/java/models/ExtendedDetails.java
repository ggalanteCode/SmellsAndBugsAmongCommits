package models;

/**
 * an instance of this class represents the extended details of a change object
 * @author Giovanni Galante
 *
 */
public class ExtendedDetails {
	private String oldValue;
	private String newValue;
	private String oldPath;
	private String newPath;
	private String oldMethodName;
	private String newMethodName;

	public String getOldMethodName() {
		return oldMethodName;
	}

	public void setOldMethodName(String oldMethodName) {
		this.oldMethodName = oldMethodName;
	}

	public String getNewMethodName() {
		return newMethodName;
	}

	public void setNewMethodName(String newMethodName) {
		this.newMethodName = newMethodName;
	}

	public String getNewPath() {
		return newPath;
	}

	public void setNewPath(String newPath) {
		this.newPath = newPath;
	}

	public String getOldPath() {
		return oldPath;
	}

	public void setOldPath(String oldPath) {
		this.oldPath = oldPath;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	
}
