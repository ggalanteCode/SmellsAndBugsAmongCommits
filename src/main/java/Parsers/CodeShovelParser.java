package Parsers;

import models.ChangeObject;
import models.CodeShovelClass;
import models.CodeShovelMethod;
import models.CodeShovelOutputFileRoot;
import models.Commit;
import repository.RepositoryHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.errors.GitAPIException;

import com.github.antlrjavaparser.api.CompilationUnit;
import com.github.antlrjavaparser.JavaParser;
import com.github.antlrjavaparser.ParseException;
import com.github.antlrjavaparser.api.TypeParameter;
import com.github.antlrjavaparser.api.body.BodyDeclaration;
import com.github.antlrjavaparser.api.body.ConstructorDeclaration;
import com.github.antlrjavaparser.api.body.MethodDeclaration;
import com.github.antlrjavaparser.api.body.ModifierSet;
import com.github.antlrjavaparser.api.body.Parameter;
import com.github.antlrjavaparser.api.body.TypeDeclaration;
import com.github.antlrjavaparser.api.expr.AnnotationExpr;
import com.github.antlrjavaparser.api.expr.BinaryExpr;
import com.github.antlrjavaparser.api.stmt.ExpressionStmt;
import com.github.antlrjavaparser.api.type.ClassOrInterfaceType;

import java.util.Vector;

import gui.Download;

/**
 *
 * this class parses the results in the output file produced by the tool for calculating
 * differences among successive releases (CodeShovel) and writes the parsed results on the 
 * 'sbac' database
 * @author Giovanni Galante
 */
public class CodeShovelParser {

    public String getAnalyzableFile() {
        return analyzableFile;
    }

    private String analyzableFile = "results.json";
    private String urlProject;

    public void setAnalyzableFile(String analyzableFile) {
        this.analyzableFile = analyzableFile;
    }

    public String getUrlProject() {
        return urlProject;
    }

    public void setUrlProject(String urlProject) {
        this.urlProject = urlProject;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    private String commitId;

    public JSONReader getJsonReader() {
        return jsonReader;
    }

    public void setJsonReader(JSONReader jsonReader) {
        this.jsonReader = jsonReader;
    }

    private JSONReader jsonReader;
    
    //private String projectPath;
    
    //private RepositoryHandler repoHandler;

    public CodeShovelParser(String urlProject, String commitId, JSONReader jsonReader) {
        setJsonReader(jsonReader);
        this.urlProject = urlProject;
        this.commitId = commitId;
    }

    /**
     * reads the output file and creates the CSMethod and CSClass objects containing 
     * @throws IOException 
     */
    public void execute(File resultFile) throws IOException {
    	    CodeShovelOutputFileRoot outputFileRoot = jsonReader.readFromJSONFile(resultFile);
            CodeShovelMethod method = getCodeShovelMethodFromJsonObject(outputFileRoot);
            CodeShovelClass csClass = getCodeShovelClassFromJsonObject(outputFileRoot,method);
            if(Download.isCodeShovelPrintLines()) {
            	this.printCodeShovelParserPrintLines(method, csClass);
            }
            writeinSBACDataBase(method, csClass);
            
    }

    private CodeShovelMethod getCodeShovelMethodFromJsonObject(CodeShovelOutputFileRoot outputFileRoot) throws IOException {
        CodeShovelMethod method = new CodeShovelMethod();
        try {
			method.setSourceFileName(getFullyQualifiedName(outputFileRoot));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        HashMap<String,ChangeObject> changeHistoryDetails = outputFileRoot.getChangeHistoryDetails();
        String mostRecentCommitString = outputFileRoot.getChangeHistory().get(0);
        ChangeObject currentVersion = changeHistoryDetails.get(mostRecentCommitString);
        String changeType = outputFileRoot.getChangeHistoryShort().get(mostRecentCommitString);
        method.setFunctionName(getMethodName(currentVersion));
        method.setFunctionSignature(getMethodSignature(currentVersion));
        method.setStartCommitName(outputFileRoot.getChangeHistory().get(0));
        int[] diff = new int[3];
        if(currentVersion.getType().equals("Yfilerename")) {
        	diff = null;
        } else if (currentVersion.getType().startsWith("Ymultichange")) {
        	ArrayList<ChangeObject> subChanges = currentVersion.getSubchanges();
        	ChangeObject firstSubChange = subChanges.get(0);
            diff = countLinesFromDiff(firstSubChange.getDiff());
        } else {
        	diff = countLinesFromDiff(currentVersion.getDiff());
        }
        if (diff==null) {
            method.setChangeType(newOldRenamedOrModified(changeType));
            method.setPreviousCommitName(outputFileRoot.getChangeHistory().get(1));
        } else {
        		method.setLinesOfCodeModified(diff[2]);
        		method.setLinesOfCodeAdded(diff[0]);
           	 	method.setLinesOfCodeDeleted(diff[1]);
        	}
        method.setChangeType(newOldRenamedOrModified(changeType));
        if(previousCommitExists(method.getChangeType())) {
        	method.setPreviousCommitName(outputFileRoot.getChangeHistory().get(1));
        }
        ArrayList<String> methodModifiers = getModifiers(currentVersion);
        for(String s : methodModifiers) {
        	switch(s) {
        		case "public":
        			method.setPublic(true);
        			break;
        		case "private":
        			method.setPrivate(true);
        			break;
        		case "protected":
        			method.setProtected(true);
        			break;
        		case "final":
        			method.setFinal(true);
        			break;
        		case "static":
        			method.setStatic(true);
        			break;
        		case "abstract":
        			method.setAbstract(true);
        			break;
        		case "transient":
        			method.setTransient(true);
        			break;
        		case "synchronized":
        			method.setSynchronized(true);
        			break;
        		case "volatile":
        			method.setVolatile(true);
        			break;
        		default:
        			break;
        	}
        }
        return method;
        }
    
    private boolean previousCommitExists(String changeType) {
    	if(changeType.equals("NEW")) {
    		return false;
    	} else return true;
    }

    private String newOldRenamedOrModified(String changeType) {
        if (changeType.equals("Yintroduced")) {
            return "NEW";
        } else if (changeType.equals("Ynochange")) {
            return "OLD";
        } else if(changeType.equals("Yfilerename")) {
        	return "RENAMED";
        } else {
        	return "MODIFIED(" + changeType + ")";
        }
    }

    private CodeShovelClass getCodeShovelClassFromJsonObject(CodeShovelOutputFileRoot outputFileRoot, CodeShovelMethod method) {
        CodeShovelClass csClass = new CodeShovelClass();
        csClass.setSourceFileName(outputFileRoot.getSourceFileName().replaceFirst(".java", ""));
        if(method.getChangeType().startsWith("MODIFIED")) {
        	csClass.setChangeType("MODIFIED");
        } else {
        	csClass.setChangeType(method.getChangeType());
        }
        csClass.setStartCommitName(method.getStartCommitName());
        if(previousCommitExists(csClass.getChangeType())) {
        	csClass.setPreviousCommitName(method.getPreviousCommitName());
        }
        try {
			csClass.setCompleteSourceFileName(getFullyQualifiedName(outputFileRoot));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return csClass;
    }

    private int[] countLinesFromDiff(String diffString) {
        ArrayList<String> linesModified = new ArrayList<String>();
        ArrayList<ArrayList<String>> linesGroups = new ArrayList<ArrayList<String>>();
        ArrayList<String> lineGroup = null;
        String string = ""; int[] diff = new int[3];
        int plusLines = 0, minusLines = 0;
        try {
        	Stream<String> lines = diffString.lines();
        	Object[] linesArray = lines.toArray();
        	for(int i = 0; i < linesArray.length; i++) {
        		string = (String) linesArray[i];
        		if(string.startsWith("+")||string.startsWith("-")) {
        			lineGroup = new ArrayList<String>();
        			lineGroup.add(string);
        			if(string.startsWith("+")) {
        				plusLines++;
        				//System.out.println("+ LINE ADDED");
        			} else if(string.startsWith("-")) {
        				minusLines++;
        				//System.out.println("- LINE ADDED");
        			}
        			i++;
        			if(i == linesArray.length) {
        				break;
        			}
        			string = (String)linesArray[i];
        			while(string.startsWith("+")||string.startsWith("-") && i <linesArray.length) {
        				lineGroup.add(string);
        				if(string.startsWith("+")) {
        					plusLines++;
        					//System.out.println("+ LINE ADDED");
            			} else if(string.startsWith("-")) {
            				minusLines++;
            				//System.out.println("- LINE ADDED");
            			}
        				i++;
        				if(i == linesArray.length) {
            				break;
            			}
        				string = (String) linesArray[i];
        			}
        			linesGroups.add(lineGroup);
        			//System.out.println("LINE GROUP ADDED");
        			if(checkIfLinesAreModified(lineGroup)) {
        				minusLines = 0; plusLines = 0;
        				linesModified.addAll(lineGroup);
        				diff[2] = countLinesModified(linesModified.size());
        				//System.out.println("+ LINES: " + diff[0]);
        				//System.out.println("- LINES: " + diff[1]);
        				//System.out.println("MOD LINES: " + diff[2]);
        			} else {
        				for(int k = 0; k<lineGroup.size(); k++) {
        		    		if(lineGroup.get(k).startsWith("+")) {diff[0]+=1;}
        		    		else {diff[1]+=1;}
        		    	}
        				//System.out.println("+ LINES: " + diff[0]);
        				//System.out.println("- LINES: " + diff[1]);
        				//System.out.println("MOD LINES: " + diff[2]);
        			}
        			
        		}
        	}
        } catch (IllegalStateException | NullPointerException e) {
        	e.printStackTrace();
        }
        return diff;
    }
    
    private boolean checkIfLinesAreModified(ArrayList<String> lGroup) {
    	int plus = 0, minus = 0;
    	for(int i = 0; i<lGroup.size(); i++) {
    		if(lGroup.get(i).startsWith("+")) {plus++;}
    		else {minus++;}
    	}
    	return plus == minus;
    }
    
    private int countLinesModified(int numLinesModified) {
    	return numLinesModified/2;
    }
    
    private String getFullyQualifiedName(CodeShovelOutputFileRoot outputFileRoot) throws IOException {
    	String absPath = outputFileRoot.getRepositoryPath().replaceAll("/.git", "").concat("\\").concat(outputFileRoot.getSourceFilePath());
		BufferedReader reader =new BufferedReader(new FileReader(new File(absPath)));
		String line = reader.readLine();
		while(line != null) {
			if (line.stripLeading().startsWith("package")) {
				String classNameWithoutDotJava = outputFileRoot.getSourceFileName().replaceAll(".java", "");
				line = line.replaceFirst("package *", "").replaceAll(";", "").concat(".").concat(classNameWithoutDotJava);
				reader.close();
				return line;
			} else {
				line = reader.readLine();
			}
		}
		reader.close();
		return outputFileRoot.getSourceFileName();
    }
    
    private String getMethodName(ChangeObject currentVersion) {
    	String methodName = "";
    	try {
        	if(currentVersion.getType().startsWith("Ymultichange")) {
        		ChangeObject firstSubChange = currentVersion.getSubchanges().get(0);
        		methodName = firstSubChange.getFunctionName();
        	} else {
        		methodName = currentVersion.getFunctionName();
        	}
    	} catch(NullPointerException e) {
    		e.printStackTrace();
    	}
    	return methodName;
    }
    
    private String getMethodSignature(ChangeObject currentVersion) {
    	String methodSignature = "";
    	if(currentVersion.getType().startsWith("Ymultichange")) {
    		ChangeObject firstSubChange = currentVersion.getSubchanges().get(0);
    		try {
    			Iterator<String> iterator = firstSubChange.getActualSource().lines().iterator();
        		String nextString = iterator.next();
        		methodSignature = methodSignature.concat(nextString);
        		while(!methodSignature.contains("{")) {
        			nextString = iterator.next();
        			methodSignature = methodSignature.concat(nextString);
        		}
    		}catch(NoSuchElementException e) {
    			
    		}
    		methodSignature = methodSignature.replaceFirst(" *\\u007b *", "").replaceAll("(public|private|protected|final|static|abstract|transient|synchronized|volatile)", "").replaceFirst("throws.*", "").stripLeading();
    		//System.out.println("CodeShovelParser: METHOD SIGNATURE:" + methodSignature);
    		// *** PARSE METHOD SIGNATURE ***
    		String signatureInDebugClass = "public class Debug{\n" + methodSignature +"{}\n}";
    		try {
    			CompilationUnit compUnit = JavaParser.parse(signatureInDebugClass);
    			List<TypeDeclaration> listTypes = compUnit.getTypes();
    			List<BodyDeclaration> bodyDeclarations = listTypes.get(0).getMembers();
    			BodyDeclaration body = bodyDeclarations.get(0);
    			MethodDeclaration myMethod = (MethodDeclaration) body;
    			//System.out.println("CodeShovelParser: MODIFIERS : "+myMethod.getModifiers());
    			//System.out.println("CodeShovelParser: METHOD BODY : "+body);
    			//System.out.println("CodeShovelParser: NAME : " + myMethod.getName());
    			//System.out.println("CodeShovelParser: PARAMS : " + myMethod.getParameters());
    			//System.out.println("CodeShovelParser: TYPE PARAMETERS : " + myMethod.getTypeParameters());
    			//System.out.println("CodeShovelParser: RETURN TYPE : " + myMethod.getType());
    			List<TypeParameter> typeParameters = myMethod.getTypeParameters();
    			List<Parameter> parameters = myMethod.getParameters();
    			if(typeParameters != null) {
    				for (TypeParameter parameter : typeParameters) {
                        //System.out.println("--- TYPE PARAMETER ---");
                        //System.out.println("PARAMETER : " + parameter.toString());
                        if(!parameter.toString().contains(" extends ")) {
                        	 //System.out.println("TRASFORMO LA STRINGA IN :");
                             parameter.setName(parameter.getName() + " extends Object");
                             //System.out.println(parameter.getName());
                        }
                    }
    			}
    			if(parameters != null) {
    				for(Parameter param : parameters) {
    					//System.out.println("CodeShovelParser: MODIFIERS : "+param.getModifiers());
    					//System.out.println("--- PARAM ---");
    					//System.out.println("PARAMETER : " + param.toString());
    					//System.out.println("data:"+param.getData());
    					//System.out.println("modifiers:"+param.getModifiers());
    					//System.out.println("annotations:"+param.getAnnotations());
    					//System.out.println("begin comments:"+param.getBeginComments());
    					//System.out.println("end comments:"+param.getEndComments());
    					//System.out.println("internal commnets:"+param.getInternalComments());
    					//System.out.println("type:"+param.getType());
    					//System.out.println("is var args?:"+param.isVarArgs());
    					if(param.isVarArgs()) {
    						param.setVarArgs(false);
    						//System.out.println("is var args?:"+param.isVarArgs());
    						param.setType(new ClassOrInterfaceType(param.getType()+"[]"));
    					}
    					if(param.getAnnotations() != null) {
    						param.setAnnotations(null);
    						//System.out.println("annotations:"+param.getAnnotations());
    					}
    				}
    			}
    			//System.out.println("CodeShovelParser: METHOD BODY : "+body);
    			methodSignature = body.toString().replaceFirst(" *\\u007b(\\n|\\r|\\r\\n) *\\u007d", "");
    		} catch(Exception e) {
    			e.printStackTrace();
    		}
    	} else {
    		try {
    			Iterator<String> iterator = currentVersion.getActualSource().lines().iterator();
        		String nextString = iterator.next();
        		methodSignature = methodSignature.concat(nextString);
        		while(!methodSignature.contains("{")) {
        			nextString = iterator.next();
        			methodSignature = methodSignature.concat(nextString);
        		}
    		}catch(NoSuchElementException e) {
    			
    		}
    		methodSignature = methodSignature.replaceFirst(" *\\u007b *", "").replaceAll("(public|private|protected|final|static|abstract|transient|synchronized|volatile)", "").replaceFirst("throws.*", "").stripLeading();
    		//System.out.println("CodeShovelParser: METHOD SIGNATURE:" + methodSignature);
    		// *** PARSE METHOD SIGNATURE ***
    		String signatureInDebugClass = "public class Debug{\n" + methodSignature +" {}\n}";
    		try {
    			CompilationUnit compUnit = JavaParser.parse(signatureInDebugClass);
    			List<TypeDeclaration> listTypes = compUnit.getTypes();
    			List<BodyDeclaration> bodyDeclarations = listTypes.get(0).getMembers();
    			BodyDeclaration body = bodyDeclarations.get(0);
    			MethodDeclaration myMethod = (MethodDeclaration) body;
    			//System.out.println("CodeShovelParser: MODIFIERS : "+myMethod.getModifiers());
    			//System.out.println("CodeShovelParser: METHOD BODY : "+body);
    			//System.out.println("CodeShovelParser: NAME : " + myMethod.getName());
    			//System.out.println("CodeShovelParser: PARAMS : " + myMethod.getParameters());
    			//System.out.println("CodeShovelParser: TYPE PARAMETERS : " + myMethod.getTypeParameters());
    			//System.out.println("CodeShovelParser: RETURN TYPE : " + myMethod.getType());
    			List<TypeParameter> typeParameters = myMethod.getTypeParameters();
    			List<Parameter> parameters = myMethod.getParameters();
    			if(typeParameters != null) {
    				for (TypeParameter parameter : typeParameters) {
                        //System.out.println("--- TYPE PARAMETER ---");
                        //System.out.println("PARAMETER : " + parameter);
                        if(!parameter.toString().contains(" extends ")) {
                       	 //System.out.println("TRASFORMO LA STRINGA IN :");
                            parameter.setName(parameter.getName() + " extends Object");
                            //System.out.println(parameter.getName());
                       }
                    }
    			}
    			if(parameters != null) {
    				for(Parameter param : parameters) {
    					//System.out.println("--- PARAM ---");
    					//System.out.println("PARAMETER : " + param.toString());
    					//System.out.println("data:"+param.getData());
    					//System.out.println("modifiers:"+param.getModifiers());
    					//System.out.println("annotations:"+param.getAnnotations());
    					//System.out.println("begin comments:"+param.getBeginComments());
    					//System.out.println("end comments:"+param.getEndComments());
    					//System.out.println("internal commnets:"+param.getInternalComments());
    					//System.out.println("type:"+param.getType());
    					//System.out.println("is var args?:"+param.isVarArgs());
    					if(param.isVarArgs()) {
    						param.setVarArgs(false);
    						//System.out.println("is var args?:"+param.isVarArgs());
    						param.setType(new ClassOrInterfaceType(param.getType()+"[]"));
    					}
    					if(param.getAnnotations() != null) {
    						param.setAnnotations(null);
    						//System.out.println("annotations:"+param.getAnnotations());
    					}
    				}
    			}
    			//System.out.println("CodeShovelParser: METHOD BODY : "+body);
    			methodSignature = body.toString().replaceFirst(" *\\u007b(\\n|\\r|\\r\\n) *\\u007d", "");
    		} catch(Exception e) {
    			e.printStackTrace();
    		}
    	}
    	return methodSignature;
    }
    
    private ArrayList<String> getModifiers(ChangeObject currentVersion) {
    	String methodSignature = "";
    	ArrayList<String> modifiersList = new ArrayList<String>();
    	String[] modifiers = {"public", "private", "protected", "static", "abstract", "transient", "synchronized", "volatile"};
    	if(currentVersion.getType().startsWith("Ymultichange")) {
    		ChangeObject firstSubChange = currentVersion.getSubchanges().get(0);
    		try {
    			Iterator<String> iterator = firstSubChange.getActualSource().lines().iterator();
        		String nextString = iterator.next();
        		methodSignature = methodSignature.concat(nextString);
        		while(!methodSignature.contains("{")) {
        			nextString = iterator.next();
        			methodSignature = methodSignature.concat(nextString);
        		}
        		methodSignature = methodSignature.replaceFirst(" *\\u007b *", "").stripLeading();
    		}catch(NoSuchElementException e) {
    			
    		}
    		//methodSignature = firstSubChange.getActualSource().lines().findFirst().get().stripLeading().replaceFirst(" *\\u007b *", "");
    		String signatureInDebugClass = "public class Debug{\n" + methodSignature +" {}\n}";
    		try {
				CompilationUnit compUnit = JavaParser.parse(signatureInDebugClass);
				List<TypeDeclaration> listTypes = compUnit.getTypes();
    			List<BodyDeclaration> bodyDeclarations = listTypes.get(0).getMembers();
    			BodyDeclaration body = bodyDeclarations.get(0);
    			MethodDeclaration myMethod = (MethodDeclaration) body;
    			for(String s : modifiers) {
        			if(ModifierSet.isFinal(myMethod.getModifiers()) ) {
        				modifiersList.add("final");
        			}
        			if(methodSignature.contains(s)) {
        				modifiersList.add(s);
        			}
        		}
    			
    		} catch (Exception e ) {
				e.printStackTrace();
			}
    		return modifiersList;
    	} else {
    		try {
    			Iterator<String> iterator = currentVersion.getActualSource().lines().iterator();
        		String nextString = iterator.next();
        		methodSignature = methodSignature.concat(nextString);
        		while(!methodSignature.contains("{")) {
        			nextString = iterator.next();
        			methodSignature = methodSignature.concat(nextString);
        		}
        		methodSignature = methodSignature.replaceFirst(" *\\u007b *", "").stripLeading();
    		}catch(NoSuchElementException e) {
    			
    		}
    		//methodSignature = currentVersion.getActualSource().lines().findFirst().get().stripLeading().replaceFirst(" *\\u007b *", "");
    		String signatureInDebugClass = "public class Debug{\n" + methodSignature +" {}\n}";
    		try {
				CompilationUnit compUnit = JavaParser.parse(signatureInDebugClass);
				List<TypeDeclaration> listTypes = compUnit.getTypes();
    			List<BodyDeclaration> bodyDeclarations = listTypes.get(0).getMembers();
    			BodyDeclaration body = bodyDeclarations.get(0);
    			MethodDeclaration myMethod = (MethodDeclaration) body;
    			for(String s : modifiers) {
        			if(ModifierSet.isFinal(myMethod.getModifiers()) ) {
        				modifiersList.add("final");
        			}
        			if(methodSignature.contains(s)) {
        				modifiersList.add(s);
        			}
        		}
			} catch(Exception e) {
				e.printStackTrace();
			}
    		return modifiersList;
    	}
    }
    
    private void printCodeShovelParserPrintLines(CodeShovelMethod method, CodeShovelClass csClass) {
    	System.out.println("CodeShovelParser: EXECUTE METHOD STARTED FOR COMMIT " + this.getCommitId());
    	System.out.println("CodeShovelParser: *** CODESHOVEL METHOD ***");
    	System.out.println("CodeShovelParser: GET FULLY QUALIFIED NAME STARTED");
    	System.out.println("CodeShovelParser: SOURCE FILE NAME " + method.getSourceFileName());
    	System.out.println("CodeShovelParser: GET METHOD NAME STARTED");
    	System.out.println("CodeShovelParser: FUNCTION NAME " + method.getFunctionName());
    	System.out.println("CodeShovelParser: GET METHOD SIGNATURE STARTED");
    	System.out.println("CodeShovelParser: FUNCTION SIGNATURE " + method.getFunctionSignature());
    	System.out.println("CodeShovelParser: START COMMIT NAME " + method.getStartCommitName());
    	System.out.println("CodeShovelParser: THE CHANGE TYPE IS " + method.getChangeType());
    	System.out.println("CodeShovelParser: PREVIOUS COMMIT NAME " + method.getPreviousCommitName());
    	System.out.println("CodeShovelParser: LOC MODIFIED " + method.getLinesOfCodeModified());
    	System.out.println("CodeShovelParser: LOC ADDED " + method.getLinesOfCodeAdded());
    	System.out.println("CodeShovelParser: LOC DELETED " + method.getLinesOfCodeDeleted());
    	System.out.println("CodeShovelParser: *** METHOD MODIFIERS ***");
    	System.out.println("CodeShovelParser: PUBLIC = " + method.isPublic());
    	System.out.println("CodeShovelParser: PRIVATE = " + method.isPrivate());
    	System.out.println("CodeShovelParser: PROTECTED = " + method.isProtected());
    	System.out.println("CodeShovelParser: FINAL = " + method.isFinal());
    	System.out.println("CodeShovelParser: STATIC = " + method.isStatic());
    	System.out.println("CodeShovelParser: ABSTRACT = " + method.isAbstract());
    	System.out.println("CodeShovelParser: TRANSIENT = " + method.isTransient());
    	System.out.println("CodeShovelParser: SYNCHRONIZED = " + method.isSynchronized());
    	System.out.println("CodeShovelParser: VOLATILE = " + method.isVolatile());
    	System.out.println("CodeShovelParser: GET CODESHOVEL METHOD RETURNED");
    	System.out.println("CodeShovelParser: *** CODESHOVEL CLASS ***");
    	System.out.println("CodeShovelParser: GET FULLY QUALIFIED NAME STARTED");
    	System.out.println("CodeShovelParser: SOURCE FILE NAME " + csClass.getSourceFileName());
    	System.out.println("CodeShovelParser: CHANGE TYPE " + csClass.getChangeType());
    	System.out.println("CodeShovelParser: START COMMIT NAME " + csClass.getStartCommitName());
    	System.out.println("CodeShovelParser: PREVIOUS COMMIT NAME " + csClass.getPreviousCommitName());
    }

    /**
     * analyze and write the output file's results in the database
     * @param method
     * @param csClass
     */
    public void writeinSBACDataBase(CodeShovelMethod method, CodeShovelClass csClass) {
        jsonReader.writeInDB(method, csClass);
    }
}
