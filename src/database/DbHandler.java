/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import com.jcabi.github.*;
import java.io.IOException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import gui.Download;
import models.BugCollection;
import models.BugIstance;
import models.Commit;
import models.Error;
import models.Method;
import models.Project;
import models.Variable;
import models.IssueCollection;

/**
    * Class used to manage all the interactions with database
    * @author mattia
    */
public class DbHandler {
    private static final String URLPOSTGRES = "jdbc:postgresql://localhost:5432/postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/sbac";
    private static final String USER = "postgres";
    private static final String PSW = "Password";
    private static Connection connection;
    
    public DbHandler(){}
    /**
    * Method used to access or initialize the database in case never did before
    * @author mattia
    */
    public static void initDb(){
        boolean access = accessDb();
        if(!access){
            createDb();
            access = accessDb();
            if(access)
                createTables();
        }
    }
    /**
    * Method used to access to the database 
    * @return boolean true if access succesfull
    * @author mattia
    */
    private static boolean accessDb(){
        //accedo al DB  creato in precedenza
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL, USER, PSW );
            System.err.println("Sbac : Connected to "+ connection.getCatalog());
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Sbac : Error the database doesn't exist");
            return false;
        }
    }
    /**
    * Method used to create the database
    * @author mattia
    */
    private static void createDb(){
         try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection(URLPOSTGRES, USER, PSW );
            Statement sqlState = con.createStatement();
            String dbCreation ="create database sbac";
            sqlState.executeUpdate(dbCreation);
            System.err.println("Sbac : Created Db  and connected to it ");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Sbac : Failed to create Db\n"+e);
        }
    }
    /**
    * Method used to create tables
    * @author mattia
    */
    private static void createTables(){
        try {
            Statement sqlState = connection.createStatement();
            sqlState.executeUpdate( PreparedSQL.PROJECT+
                                    PreparedSQL.COMMIT+
                                    PreparedSQL.PACKAGE+
                                    PreparedSQL.COMMPAC+
                                    PreparedSQL.CLASS+
                                    PreparedSQL.CLASSPAC+
                                    PreparedSQL.METHOD+
                                    PreparedSQL.VARIABLE+
                                    PreparedSQL.BUGCOLLECTION+
                                    PreparedSQL.BUGISTANCE+
                                    PreparedSQL.ERROR+
                                    PreparedSQL.INFORMATION+
                                    PreparedSQL.CLONE+
                                    PreparedSQL.PMD+
                                    PreparedSQL.METRICHUNTER+
                                    PreparedSQL.METRIC+
                                    PreparedSQL.SMELL+
                                    PreparedSQL.DATACLUMPS+
                                    PreparedSQL.CLONEISTANCE+
                                    PreparedSQL.ISSUEISTANCE+
                                    PreparedSQL.METRICCLASSCOLLECTION+
                                    PreparedSQL.METRICMETHODCOLLECTION+
                                    PreparedSQL.METRICPACKAGECOLLECTION
                                    );
            System.err.println("Sbac : Created tables on db");
        } catch (SQLException ex) {
            System.err.println("Sbac : Failed to create tables on db \n"+ex);
        }
    }
    
    // QUERY COMUNI
    
    /**
    * Method used to insert into COMMPAC 
    * @param idp id package
    * @param idc id commit
    * @throws SQLException incorrect insertion
    * @author mattia
    */
    public static void insertCommPac(int idp,String idc) throws SQLException {
        PreparedStatement stmt=null;
        try {
            stmt = connection.prepareStatement(PreparedSQL.INSERTCOMMPAC);
                stmt.setInt(1, idp);
                stmt.setString(2,idc);
                stmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
        }
        finally{
            if(stmt!=null)
            stmt.close();
        }
    }
    
    /**
    * Method used to insert into LASSPAC 
    * @param idp id package
    * @param idc id class
    * @throws SQLException incorrect insertion
    * @author mattia
    */
    public static void insertClassPac(int idp,int idc) throws SQLException {
       PreparedStatement stmt=null;
       try {
           stmt = connection.prepareStatement(PreparedSQL.INSERTCLASSPAC);
               stmt.setInt(1, idp);
               stmt.setInt(2,idc);
               stmt.executeUpdate();
       } catch (SQLException ex) {
           Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
           printSQLException(ex);
       }
       finally{
           if(stmt!=null)
           stmt.close();
       }
    }
    
    /**
    * Method used to insert a project with all the commits associated
    * @param p project
    * @param commits ArrayList of commits
    * @see ArrayList
    * @see Project
    * @throws SQLException incorrect insertion / duplicate
    */
    public static void insertProjectCommit(models.Project p,ArrayList<models.Commit> commits) throws SQLException{
        PreparedStatement stmt=null;
        try {
            
            stmt = connection.prepareStatement(PreparedSQL.INSERTPROJECT);
            stmt.setString(1, p.getUrl());
            stmt.setString(2, p.getName());
            stmt.setString(3, p.getPath());
            stmt.executeUpdate();
            
            for(Commit c : commits){
                stmt = connection.prepareStatement(PreparedSQL.INSERTCOMMIT);
                stmt.setString(1, c.getVersion());
                stmt.setTimestamp(2, new Timestamp(c.getCommitDate().getTime()));
                stmt.setString(3, c.getMsg());
                if(c.getRelease()==null)
                    stmt.setNull(4,java.sql.Types.NULL);
                else
                    stmt.setString(4, c.getRelease());
                stmt.setString(5, p.getUrl());
                stmt.execute();
            }

        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                Download.updater(p.getUrl());
            } else {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            if(stmt!=null)
                stmt.close();
        }
    }
    
    /**
     * inserts the collected bugs into the database
     * @param issue list containing all issues
     * @param url project url
     */
    public static void insertBugComm(ArrayList<IssueCollection> issue, String url){
        PreparedStatement stmt=null;
        try{
            for(IssueCollection ic: issue){
                stmt=connection.prepareStatement(PreparedSQL.INSERTISSUEISTANCE);
                stmt.setString(1, ic.getTitle());
                stmt.setString(2, ic.getState());
                Array mc = connection.createArrayOf("text", ic.getLabel().toArray(new String[ic.getLabel().size()]));
                if(mc!=null)
                    stmt.setArray(3, mc);
                else
                    stmt.setNull(3,java.sql.Types.NULL);
                stmt.setString(4, ic.getDescription());
                stmt.setLong(5, ic.getId());
                stmt.setString(6, url);
                Array ms = connection.createArrayOf("text", ic.getFileName().toArray(new String[ic.getFileName().size()]));
                if(ms!=null)
                    stmt.setArray(7, ms);
                else
                    stmt.setNull(7, java.sql.Types.NULL);
                if(ic.getNumberLineschange()>0)
                    stmt.setInt(8, ic.getNumberLineschange());
                else
                    stmt.setInt(8, 0);
                stmt.execute();
            }
        }catch(SQLException ex){
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
        }  
    }
    
    /**
     * inserts all class-level metrics collected with sourcemeters into the database
     * @param list list containing all collected metrics
     * @param version commit version
     * @param url project url
     */
    public static void insertMetricClass(ArrayList<String>list,String version,String url){
        PreparedStatement stmt=null;
        int numCol=0;
        try{
            stmt=connection.prepareStatement(PreparedSQL.INSERTMETRICCLASSCOLLECTION);
            for(int i=0;i<list.size();i++){
                if(!(list.get(i).equals("")) && numCol<111){
                   numCol++; 
                   stmt.setString(numCol,list.get(i));
                }
            }
            stmt.setString(112, version);
            stmt.setString(113, url);
            stmt.execute();
        }catch(SQLException ex){
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
        }
        
    }
    
    /**
     * inserts all method-level metrics collected with sourcemeters into the database
     * @param list list containing all collected metrics
     * @param version commit version
     * @param url project url
     */
    public static void insertMetricMethod(ArrayList<String>list,String version,String url){
        PreparedStatement stmt=null;
        int numCol=0;
        try{
            stmt=connection.prepareStatement(PreparedSQL.INSERTMETRICMETHODCOLLECTION);
            for(int i=0;i<list.size();i++){
                if(!(list.get(i).equals("")) && numCol<86){
                    numCol++;
                    stmt.setString(numCol, list.get(i));
                }
            }
            stmt.setString(87, version);
            stmt.setString(88, url);
            stmt.execute();
        }catch(SQLException ex){
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
        }
    }
    
    /**
     * inserts all package-level metrics collected with sourcemeters into the database
     * @param list list containing all collected metrics
     * @param version commit version
     * @param url project url
     */
    public static void insertMetricPackage(ArrayList<String>list,String version,String url){
        PreparedStatement stmt=null;
        int numCol=0;
        try{
            stmt=connection.prepareStatement(PreparedSQL.INSERTMETRICPACKAGECOLLECTION);
            for(int i=0;i<list.size();i++){
                if(!(list.get(i).equals("")) && numCol<87){
                    numCol++;
                    stmt.setString(numCol, list.get(i));
                }
            }
            stmt.setString(88, version);
            stmt.setString(89, url);
            stmt.execute();
        }catch(SQLException ex){
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
        }
    }
    
    /**
    * Method used to insert into CLASS 
    * @param c class
    * @throws SQLException incorrect insertion
    * @return -1 if unsuccessfull else returning the id
    * @author mattia
    */
    public static int insertClass(models.Class c) throws SQLException{
        PreparedStatement stmt=null;
        try {
            stmt = connection.prepareStatement(PreparedSQL.INSERTCLASS);
            if(c.getPath()!=null){
                stmt.setString(1, c.getName() );
                stmt.setString(2, c.getPath());
                stmt.execute();
            }
            else{
                stmt.setString(1, c.getName() );
                stmt.setNull(2,java.sql.Types.NULL);
                stmt.execute();
            }
            //obtaining id of the class
            ResultSet  rs = stmt.getResultSet();
            rs.next();
            c.setId(rs.getInt(1));
            return rs.getInt(1);
                
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
            return -1; 
        }
        finally{
            if(stmt!=null)
            stmt.close();
        }
    }
    /**
    * Method used to insert into PACKAGE
    * @param p package
    * @throws SQLException incorrect insertion
    * @return -1 if unsuccessfull else returning the id
    * @author mattia
    */    
    public static  int insertPackage(models.Package p) throws SQLException{
        PreparedStatement stmt=null;
        try {
                stmt = connection.prepareStatement(PreparedSQL.INSERTPACKAGE);
                stmt.setString(1, p.getName() );
                if(p.getPath()!=null)
                    stmt.setString(2, p.getPath());
                else
                    stmt.setString(2, null);
                stmt.execute();
                ResultSet  rs = stmt.getResultSet();
                rs.next();
                p.setId(rs.getInt(1));
                return rs.getInt(1);
                
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
            return -1;
        }
        finally{
            if(stmt!=null)
            stmt.close();
        }
    }
    /**
    * Method used to insert into METHOD
    * @param m method 
    * @param idc id class
    * @throws SQLException incorrect insertion
    * @return -1 if unsuccessfull else returning the id
    * @author mattia
    */    
    public static int insertMethod(models.Method m,int idc) throws SQLException{
        PreparedStatement stmt=null;
        try {
            if(m != null){
                stmt = connection.prepareStatement(PreparedSQL.INSERTMETHOD);
                stmt.setString(1, m.getName());
                stmt.setBoolean(2, m.getIsStatic());
                if(m.getRole()!=null )
                    stmt.setString(3, m.getRole());
                else
                    stmt.setNull(3,java.sql.Types.NULL);
                stmt.setString(4, m.getSignature());
                if(idc!=-1)
                    stmt.setInt(5, idc);
                else
                    stmt.setNull(5,java.sql.Types.NULL);
                stmt.execute();
                ResultSet  rs = stmt.getResultSet();
                rs.next();
                m.setId(rs.getInt(1));
                return rs.getInt(1);
            }
            else
                return -1;
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
            return -1;
        }
        finally{
            if(stmt!=null)
                stmt.close();
        }
    }

    public static int insertVariable(models.Variable v,int idc) throws SQLException{
        PreparedStatement stmt=null;
        try {
            if(v != null){
                stmt = connection.prepareStatement(PreparedSQL.INSERTVARIABLE);
                stmt.setString(1, v.getName());
                stmt.setBoolean(2, v.getIsStatic());
                if(v.getRole()!=null )
                    stmt.setString(3, v.getRole());
                else
                    stmt.setNull(3,java.sql.Types.NULL);
                stmt.setString(4, v.getValue());
                if(idc!=-1)
                    stmt.setInt(5, idc);
                else
                    stmt.setNull(5,java.sql.Types.NULL);
                stmt.execute();
                ResultSet  rs = stmt.getResultSet();
                rs.next();
                v.setId(rs.getInt(1));
                return rs.getInt(1);
            }
            else
                return -1;
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
            return -1;
        }
        finally{
            if(stmt!=null)
                stmt.close();
        }
    }
    
    /*
    public static ArrayList<QueryResult> classExist(String classname) throws SQLException{
        PreparedStatement stmt=null;
        ArrayList<QueryResult> output = new ArrayList<>();
        try {
            stmt=connection.prepareStatement(PreparedSQL.CLASSINCOMMIT);
            stmt.setString(1, classname);
            ResultSet rs = stmt.executeQuery();
            while(rs.next())
                        output.add(new QueryResult(rs.getString("commitid") , rs.getInt("classid") , rs.getString("packagename"), rs.getInt("packageid") ));
            return output;
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
            return null;
        }finally{
            if(stmt!=null)
                stmt.close();
        }
    }  AGGIUNGI CLASSNAME EMETHODNAME
    
    public static ArrayList<QueryResult> methodExist(String methodname) throws SQLException{
        Statement stmt=null;
        ArrayList<QueryResult> output = new ArrayList<>();
        try {
            stmt=connection.createStatement();
            ResultSet rs = stmt.executeQuery(PreparedSQL.CLASSINCOMMIT);
            while(rs.next())
                        output.add(new QueryResult(rs.getString("commitid") , rs.getInt("classid") , rs.getString("packagename"), rs.getInt("packageid") ));
            return output;
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
            return null;
        }finally{
            if(stmt!=null)
                stmt.close();
        }
    } */
    
    /**
    * Method used to get a list of all the projects stored in the database
    * @throws SQLException incorrect query
    * @return ArrayList list of projects or null if unsuccesfull
    * @author mattia
    */    
    public static ArrayList<Project> getProjects() throws SQLException{
        Statement stmt=null;
        ArrayList<Project> projects = new ArrayList<>();
        try {
            stmt=connection.createStatement();
            ResultSet rs = stmt.executeQuery(PreparedSQL.GETPROJECTS);
            while(rs.next())
                        projects.add(new Project( rs.getString("url") , rs.getString("name") , rs.getString("path") ));
            return projects;
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
            return null;
        }finally{
            if(stmt!=null)
                stmt.close();
        }
    }
    
    /**
    * Method used to get a list of all the commits stored in the database
    * @param url URL of the project
    * @throws SQLException incorrect query
    * @return ArrayList list of commits or null if unsuccesfull
    * @author mattia
    */    
    public static ArrayList<Commit> getCommits(String url) throws SQLException{
        Statement stmt=null;
        ArrayList<Commit> commits = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(PreparedSQL.GETCOMMITS);
            ps.setString(1, url);
            ResultSet rs = ps.executeQuery();
            while(rs.next())
                      commits.add(new Commit( rs.getString("version"), rs.getString("url") , rs.getString("msg") , rs.getString("release"), rs.getTimestamp("data")));
            return commits;
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
            return null;
        }finally{
            if(stmt!=null)
                stmt.close();
        }
    }
    
    /**
     * returns all bugs stored in the database for the specified project
     * @param url project url
     * @return ArrayList
     */
    public static ArrayList<IssueCollection> getAllBug(String url){
        Statement stmt=null;
        ArrayList<IssueCollection> allBug=new ArrayList<>();
        try{
            PreparedStatement ps=connection.prepareStatement(PreparedSQL.GETALLBUG);
            ps.setString(1, url);
            ResultSet rs=ps.executeQuery();
            while(rs.next()){
                    allBug.add(new IssueCollection(rs.getString("title"),rs.getString("state"),rs.getString("description"),rs.getLong("issuenumber")));
            }
            return allBug;
        }catch(SQLException ex){
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
            return null;
        }
    }
    
    /**
    * Method used to get a list of all the commits stored in the database
    * @param url URL of the project
    * @throws SQLException incorrect query
    * @return Project or null if unsuccesfull
    * @author mattia
    */   
    public static Project urlExist(String url) throws SQLException{
        Statement stmt=null;
        try {
            PreparedStatement ps = connection.prepareStatement(PreparedSQL.URLEXIST);
            ps.setString(1, url);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
                return new Project(rs.getString("url"),rs.getString("name"),rs.getString("path"));
            else
                return null;
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
            return null;
        }finally{
            if(stmt!=null)
                stmt.close();
        }
    }
    /**
    * Method used to print informations about SQLException
    * @param ex SQLException
    * @author mattia
    */   
    public static void printSQLException(SQLException ex){
		System.err.println("SQLState:"+ ex.getSQLState());
		System.err.println("Error code:"+ ex.getErrorCode());
		System.err.println("Message:"+ ex.getMessage());
	}
    
    //QUERY SPOTBUGS
    
    /**
    * Method used to insert into BUGCOLLECTION
    * @param bc bugcollection
    * @param commit id commit
    * @throws SQLException incorrect insertion
    * @return id of bugcollection if succesfull, else -1
    * @author mattia
    */     
    public static int insertBugCollection(BugCollection bc,String commit) throws SQLException{
        PreparedStatement stmt=null;
        try {
            stmt = connection.prepareStatement(PreparedSQL.INSERTBUGCOLLECTION);
            stmt.setDate(1, bc.getTime());
            stmt.setString(2, bc.getVersion());
            stmt.setInt(3, bc.getReferencedclasses());
            stmt.setInt(4, bc.getTotalclasses());
            stmt.setInt(5, bc.getTotalbugs());
            Array mc = connection.createArrayOf("text", bc.getMissingclasses().toArray(new String[bc.getMissingclasses().size()]));
            stmt.setArray(6, mc);
            stmt.setString(7, commit );
            stmt.execute();
            //obtaining id of bugcollection
            ResultSet  rs = stmt.getResultSet();
            rs.next();
            bc.setId(rs.getInt(1));
            return rs.getInt(1);
                
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
            return -1; 
        }
        finally{
            if(stmt!=null)
            stmt.close();
        }
    }
    
    /**
    * Method used to insert into BUGISTANCE (can lead to an insert of a methor or variable too)
    * @param bi bugistance
    * @param bcid bugcollection id
    * @throws SQLException incorrect insertion
    * @author mattia
    */     
    public static void insertBugIstance(BugIstance bi,int bcid) throws SQLException{
        PreparedStatement stmt=null;
        try {
            long mid=-1,vid=-1;
            Method m = bi.getM();
            if(m != null){
                stmt = connection.prepareStatement(PreparedSQL.INSERTMETHOD);
                stmt.setString(1, m.getName());
                stmt.setBoolean(2, m.getIsStatic());
                stmt.setString(3, m.getRole());
                stmt.setString(4, m.getSignature());
                stmt.setNull(5,java.sql.Types.NULL);
                stmt.execute();
                ResultSet  rs = stmt.getResultSet();
                rs.next();
                mid=rs.getInt(1);
            }
            Variable v = bi.getV();
            if(v != null){
                stmt = connection.prepareStatement(PreparedSQL.INSERTVARIABLE);
                stmt.setString(1, v.getName());
                stmt.setBoolean(2, v.getIsStatic());
                stmt.setString(3, v.getRole());
                stmt.setString(4, v.getValue());
                stmt.setNull(5,java.sql.Types.NULL);
                stmt.execute();
                ResultSet  rs = stmt.getResultSet();
                rs.next();
                vid=rs.getInt(1);
            }
            stmt = connection.prepareStatement(PreparedSQL.INSERTBUGISTANCE);
            stmt.setString(1, bi.getType());
            stmt.setInt(2, bi.getPriority());
            stmt.setInt(3, bi.getRank());
            stmt.setString(4, bi.getAbbrev());
            stmt.setString(5, bi.getCategory());
            stmt.setString(6, bi.getClassname());
            stmt.setInt(7, bi.getStartsourceline());
            stmt.setInt(8, bi.getEndsourceline());
            stmt.setInt(9, bi.getStartbytecode());
            stmt.setInt(10, bi.getEndsbytecode());
            stmt.setString(11, bi.getSourcepath());
            if(mid!=-1)
                stmt.setLong(12, mid);
            else
                stmt.setNull(12,java.sql.Types.NULL);
            if(vid!=-1)
                stmt.setLong(13, vid);
            else
                stmt.setNull(13,java.sql.Types.NULL);
            if(bcid!=-1)
                stmt.setLong(14, bcid);
            else
                stmt.setNull(14,java.sql.Types.NULL);
            
            stmt.executeUpdate();
                
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
        }
        finally{
            if(stmt!=null)
                stmt.close();
        }
    }
    
    /**
    * Method used to insert into ERROR
    * @param error error
    * @param bcid bugcollection id
    * @throws SQLException incorrect insertion
    * @author mattia
    */   
    public static void insertError(Error error, int bcid) throws SQLException {
        PreparedStatement stmt=null;
        try {
            stmt = connection.prepareStatement(PreparedSQL.INSERTERROR);
            if(bcid!=-1){
                stmt.setInt(1, bcid);
                stmt.setString(2, error.getErrormsg());
                stmt.setString(3, error.getExceptionmsg());
                stmt.executeUpdate();
            }
            else
                System.err.println("invalide id bugcollection while inserting error");
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
        }
        finally{
            if(stmt!=null)
            stmt.close();
        }
    }
    
    // QUERY CLONE
    
    /**
    * Method used to insert into CLONE
    * @param c clone
    * @param commit bugcollection id
    * @throws SQLException incorrect insertion
    * @return id of clone if succesfull,else -1
    */   
    public static int insertClone(models.Clone c,String commit) throws SQLException {
        PreparedStatement stmt=null;
        try {
            stmt =connection.prepareStatement(PreparedSQL.CLONERECORDEXISTS);
            stmt.setInt(1, c.getOccurrencies());
            stmt.setInt(2, c.getLengthlines());
            stmt.setString(3, commit);
            ResultSet rs=stmt.executeQuery();
            if(!rs.next()){
                stmt = connection.prepareStatement(PreparedSQL.INSERTCLONE);
                stmt.setInt(1, c.getOccurrencies());
                stmt.setInt(2, c.getLengthlines());
                stmt.setString(3, commit);
                stmt.execute();
                rs = stmt.getResultSet();
                rs.next();
            }
            c.setId(rs.getInt(1));
            return rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
            return -1;
        }
        finally{
            if(stmt!=null)
            stmt.close();
        }
    }
    
    /**
    * Method used to insert into CLONEISTANCE
    * @param ci CloneIstance
    * @param idclone  id clone
    * @param idclass  id class
    * @throws SQLException incorrect insertion
    * @return id of clone if succesfull,else -1
    */   
    public static int insertCloneIstance(models.CloneIstance ci,int idclone,int idclass) throws SQLException {
        PreparedStatement stmt=null;
        try {
            stmt =connection.prepareStatement(PreparedSQL.CLONEISTANCERECORDEXISTS);
            stmt.setInt(1, ci.getLinestart());
            stmt.setInt(2, ci.getLineend());
            stmt.setInt(3, ci.getColumnstart());
            stmt.setInt(4, ci.getColumnend());
            stmt.setInt(5, idclone);
            stmt.setInt(6, idclass);
            ResultSet rs=stmt.executeQuery();
            if(!rs.next()){
                stmt = connection.prepareStatement(PreparedSQL.INSERTCLONEISTANCE);
                stmt.setInt(1, ci.getLinestart());
                stmt.setInt(2, ci.getLineend());
                stmt.setInt(3, ci.getColumnstart());
                stmt.setInt(4, ci.getColumnend());
                stmt.setInt(5, idclone);
                stmt.setInt(6, idclass);
                stmt.execute();
                rs = stmt.getResultSet();
                rs.next();
            }
            ci.setId(rs.getInt(1));
            return rs.getInt(1);
            
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
            return -1;
        }
        finally{
            if(stmt!=null)
            stmt.close();
        }
    }
    
    // QUERY METRIC
    
    /**
    * Method used to insert into METRIC
    * @param m Metric
    * @param idcommit  id commit
    * @param idm  id method
    * @param idcl  id class
    * @param idp  id package
    * @throws SQLException incorrect insertion
    * @return boolean true if succesfull
    */   
    public static boolean insertMetric(models.Metric m,String idcommit,int idm, int idcl, int idp) throws SQLException {
        PreparedStatement stmt=null;
        try {
            if(idm!=0){
                stmt =connection.prepareStatement(PreparedSQL.METRICRECORDEXISTS+" idm=?;");
                stmt.setInt(4,idm);
            }
            else if (idcl!=0){
                stmt =connection.prepareStatement(PreparedSQL.METRICRECORDEXISTS+" idcl=?;");
                stmt.setInt(4,idcl);}
            else {
                stmt =connection.prepareStatement(PreparedSQL.METRICRECORDEXISTS+" idp=?;");
                stmt.setInt(4,idp);
            }
            stmt.setString(1, m.getKey());
            stmt.setDouble(2, m.getValue());
            stmt.setString(3, idcommit);
            ResultSet rs=stmt.executeQuery();
            if(!rs.next()){
                stmt = connection.prepareStatement(PreparedSQL.INSERTMETRIC);
                stmt.setString(1, m.getKey());
                stmt.setDouble(2, m.getValue());
                stmt.setString(3, idcommit);
                if(idm!=0)
                    stmt.setInt(4,idm);
                else 
                   stmt.setNull(4,java.sql.Types.NULL); 
                if(idcl!=0)
                    stmt.setInt(5,idcl);
                else 
                   stmt.setNull(5,java.sql.Types.NULL);
                if(idp!=0)
                    stmt.setInt(6,idp);
                else 
                   stmt.setNull(6,java.sql.Types.NULL);

                stmt.executeUpdate();
                return true;
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
            return false;
        }
        finally{
            if(stmt!=null)
            stmt.close();
        }
    }
    
    // QUERY SMELL
    
    /**
    * Method used to insert into SMELL
    * @param s Smell
    * @param idcommit  id commit
    * @param idm  id method
    * @param idcl  id class
    * @param idp  id package
    * @throws SQLException incorrect insertion
    * @return boolean true if succesfull
    */  
    public static boolean insertSmell(models.Smell s,String idcommit,int idv, int idm, int idcl, int idp, int linenumber) throws SQLException {
        PreparedStatement stmt=null;
        try {
            if(idv!=0) {
                stmt =connection.prepareStatement(PreparedSQL.SMELLRECORDEXISTS+" idv=?;");
                stmt.setInt(4,idv);
            }
            else if(idm!=0){
                stmt =connection.prepareStatement(PreparedSQL.SMELLRECORDEXISTS+" idm=?;");
                stmt.setInt(4,idm);
            }
            else if (idcl!=0){
                stmt =connection.prepareStatement(PreparedSQL.SMELLRECORDEXISTS+" idcl=?;");
                stmt.setInt(4,idcl);}
            else {
                stmt =connection.prepareStatement(PreparedSQL.SMELLRECORDEXISTS+" idp=?;");
                stmt.setInt(4,idp);
            }
            stmt.setString(1, s.getKey());
            stmt.setDouble(2, s.getValue());
            stmt.setString(3, idcommit);
            ResultSet rs=stmt.executeQuery();
            if(!rs.next()){
                stmt = connection.prepareStatement(PreparedSQL.INSERTSMELL);
                stmt.setString(1, s.getKey());
                stmt.setDouble(2, s.getValue());
                stmt.setString(3, idcommit);
                if(idm!=0)
                    stmt.setInt(4,idm);
                else 
                   stmt.setNull(4,java.sql.Types.NULL); 
                if(idcl!=0)
                    stmt.setInt(5,idcl);
                else 
                   stmt.setNull(5,java.sql.Types.NULL);
                if(idp!=0)
                    stmt.setInt(6,idp);
                else 
                   stmt.setNull(6,java.sql.Types.NULL);
                if(idv!=0)
                    stmt.setInt(7, idv);
                else
                    stmt.setNull(7, java.sql.Types.NULL);
                if(linenumber!=0)
                    stmt.setInt(8, linenumber);
                else
                    stmt.setNull(8, java.sql.Types.NULL);

                stmt.executeUpdate();
                return true;
            }
            return false;
            
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
            return false;
        }
        finally{
            if(stmt!=null)
            stmt.close();
        }
    }

    /**
     * Method used to insert into DATACLUMPS
     * @param d DataClumps
     * @throws SQLException incorrect insertion
     * @return boolean true if succesfull
     */
    public static boolean insertDataClumps(models.DataClumps d) throws SQLException {
        PreparedStatement stmt=null;
        try {
            stmt = connection.prepareStatement(PreparedSQL.DATACLUMPSRECORDEXISTS);
            stmt.setString(1, d.getIdm());
            stmt.setString(2, d.getIdv());
            ResultSet rs=stmt.executeQuery();
            if(!rs.next()) {
                stmt = connection.prepareStatement(PreparedSQL.INSERTDATACLUMPS);
                stmt.setString(1, d.getMethods());
                stmt.setString(2, d.getVariables());
                stmt.setString(3, d.getIdCommit());
                stmt.setString(4, d.getIdp());
                stmt.setString(5, d.getIdcl());
                stmt.setString(6, d.getIdm());
                stmt.setString(7, d.getIdv());
                stmt.executeUpdate();
                return true;
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
            return false;
        }
        finally{
            if(stmt!=null)
                stmt.close();
        }
    }
    
    // QUERY PMD
    
    /**
    * Method used to insert into PMD
    * @param p PMD
    * @param idcommit  id commit
    * @param idclass  id class
    * @throws SQLException incorrect insertion
    * @return boolean true if succesfull
    */  
    public static boolean insertPMD(models.Pmd p,String idcommit,int idclass) throws SQLException {
        PreparedStatement stmt=null;
        try {
            stmt =connection.prepareStatement(PreparedSQL.PMDRECORDEXISTS);
            stmt.setInt(1, p.getLine() );
            stmt.setString(2, p.getSolution());
            stmt.setString(3, p.getType());
            stmt.setString(4, idcommit);
            stmt.setInt(5, idclass);
            ResultSet rs=stmt.executeQuery();
            if(!rs.next()){
                stmt = connection.prepareStatement(PreparedSQL.INSERTPMD);
                stmt.setInt(1, p.getLine() );
                stmt.setString(2, p.getSolution());
                stmt.setString(3, p.getType());
                stmt.setString(4, idcommit);
                stmt.setInt(5, idclass);
                stmt.executeUpdate();
                return true;
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
            return false;
        }
        finally{
            if(stmt!=null)
            stmt.close();
        }
    }
    
    // QUERY METRICHUNTER
    
    
    /**
    * Method used to insert into METRICHUNTER
    * @param m MetricHunter
    * @param idcommit  id commit
    * @param idclass  id class
    * @param idmethod id method
    * @throws SQLException incorrect insertion
    * @return boolean true if succesfull
    */  
    public static boolean insertMetricHunter(models.MetricHunter m,String idcommit,int idclass, int idmethod) throws SQLException {
        PreparedStatement stmt=null;
        try { 
            if(idmethod!=0){
                stmt =connection.prepareStatement(PreparedSQL.METRICHUNTERRECORDEXISTS+" idm=?;");
                stmt.setInt(5,idmethod);
            }
            else{
                stmt =connection.prepareStatement(PreparedSQL.METRICHUNTERRECORDEXISTS+" idcl=?;");
                stmt.setInt(5, idclass);
            }
            stmt.setInt(1, m.getLine() );
            stmt.setDouble(2, m.getValue());
            stmt.setString(3, m.getType());
            stmt.setString(4, idcommit);
            
            ResultSet rs=stmt.executeQuery();
            if(!rs.next()){
                stmt = connection.prepareStatement(PreparedSQL.INSERTMETRICHUNTER);
                stmt.setInt(1, m.getLine() );
                stmt.setDouble(2, m.getValue());
                stmt.setString(3, m.getType());
                stmt.setString(4, idcommit);
                if(idmethod==0)
                    stmt.setNull(5,java.sql.Types.NULL);
                else
                    stmt.setInt(5,idmethod);
                if(idclass==0)
                    stmt.setNull(6,java.sql.Types.NULL);
                else
                    stmt.setInt(6, idclass);
                stmt.executeUpdate();
                return true;
            }
            return false;
            
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
            return false;
        }
        finally{
            if(stmt!=null)
            stmt.close();
        }
    }
    
    //QUERY CONTROLLO
    
    public static int packageExist(String name) throws SQLException{
        Statement stmt=null;
        try {
            PreparedStatement ps = connection.prepareStatement(PreparedSQL.PACKAGEEXIST);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
                return rs.getInt("id");
            else
                return 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
            return 0;
        }finally{
            if(stmt!=null)
                stmt.close();
        }
    }
    
    /*public static boolean metricClassExists(String id)throws SQLException{
        Statement stmt=null;
        try{
            PreparedStatement ps=connection.prepareStatement(PreparedSQL.METRICCLASSEXISTS);
            ps.setString(1, id);
            ResultSet rs=ps.executeQuery();
            if(rs.next())
                return true;
            else
                return false;
        }catch(SQLException ex){
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
            return false;
        }finally{
            if(stmt!=null)
                stmt.close();
        }
    }*/
    
    /*public static boolean metricMethodExists(String id)throws SQLException{
        Statement stmt=null;
        try{
            PreparedStatement ps=connection.prepareStatement(PreparedSQL.METRICMETHODEXISTS);
            ps.setString(1, id);
            ResultSet rs=ps.executeQuery();
            if(rs.next())
                return true;
            else
                return false;
        }catch(SQLException ex){
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
            return false;
        }finally{
            if(stmt!=null)
                stmt.close();
        }
    }*/
    
    /*public static boolean metricPackageExists(String id)throws SQLException{
        Statement stmt=null;
        try{
            PreparedStatement ps=connection.prepareStatement(PreparedSQL.METRICPACKAGEEXISTS);
            ps.setString(1, id);
            ResultSet rs=ps.executeQuery();
            if(rs.next())
                return true;
            else
                return false;
        }catch(SQLException ex){
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
            return false;
        }finally{
            if(stmt!=null)
                stmt.close();
        }
    }*/
    public static int classInProject(String className, String projectName) throws SQLException{
        Statement stmt=null;
        try {
            PreparedStatement ps = connection.prepareStatement(PreparedSQL.CLASSINPROJECT);
            ps.setString(1, className);
            ps.setString(2, projectName);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
                return rs.getInt("id");
            else
                return 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
            return 0;
        }finally{
            if(stmt!=null)
                stmt.close();
        }
    }
    
    public static int MethodInClass(String name, int classId) throws SQLException{
        Statement stmt=null;
        try {
            PreparedStatement ps = connection.prepareStatement(PreparedSQL.METHODINCLASS);
            ps.setString(1, name);
            ps.setInt(2, classId);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
                return rs.getInt("id");
            else
                return 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            return 0;
        }finally{
            if(stmt!=null)
                stmt.close();
        }
    }

    public static int VariableInClass(String name, int classId) throws SQLException {
        Statement stmt = null;
        try {
            PreparedStatement ps = connection.prepareStatement(PreparedSQL.VARIABLEINCLASS);
            ps.setString(1, name);
            ps.setInt(2, classId);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
                return rs.getInt("id");
            else
                return 0;
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            return 0;
        }finally{
            if(stmt!=null)
                stmt.close();
        }
    }
    
    public static String getClassPath(int classId) throws SQLException{
        Statement stmt=null;
        try {
            PreparedStatement ps = connection.prepareStatement(PreparedSQL.CLASSPATH);
            ps.setInt(1, classId);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
                return rs.getString("path");
            else
                return null;
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
            return null;
        }finally{
            if(stmt!=null)
                stmt.close();
        }
    }
    
    public static void updateClassPath (String path, int classId) throws SQLException{
        Statement stmt=null;
        try {
            PreparedStatement ps = connection.prepareStatement(PreparedSQL.UPDATECLASSPATH);
            ps.setString(1, path);
            ps.setInt(2, classId);
            ps.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(DbHandler.class.getName()).log(Level.SEVERE, null, ex);
            printSQLException(ex);
        }finally{
            if(stmt!=null)
                stmt.close();
        }
    }

    /**
     * Deletes a project given its URL address and returns the path of the repository on the pc.
     * @param url
     * @return
     */
    public static String projectDeleter (String url) {
        String returnPath = null;
        try {

            Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sqlVerifyAccess = "SELECT path FROM project WHERE url = '" + url + "';";
            ResultSet rs = stmt.executeQuery(sqlVerifyAccess);
            rs.next();
            returnPath = rs.getString("path");
            rs.close();

            Statement stmt2 = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.executeUpdate("DELETE FROM project WHERE url = '" + url + "';");
            stmt2.close();

            System.out.println("Old DB project-data deleted.");
            System.out.println("PATH: "+returnPath);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnPath;
    }
}
