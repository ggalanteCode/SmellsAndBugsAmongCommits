/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

/**
 *
 * @author mefi
 */
public class PreparedSQL {
    
    //create tables
    
    public static final String PROJECT = "create table project (\n" +
                                         "	url text not null primary key ,\n" +
                                         "	name text not null ,\n" +
                                         "	path text\n" +
                                         ") ;";
   
    public static final String COMMIT = "create table commit(\n" +
                                        "	version text not null primary key ,\n" +
                                        "	data  timestamp,\n" +
                                        "	msg text ,\n" +
                                        "	release text ,\n" +
                                        "	url text not null references project on delete cascade on update cascade\n" +
                                        ");";
    
    public static final String PACKAGE = "create table package(\n" +
                                      "	id serial not null primary key ,\n" +
                                      "	name text not null ,\n" +
                                      "	path text\n" +
                                      ");";
    
    public static final String COMMPAC = "create table commpac(\n" +
                                         "	idp integer not null references package on delete cascade on update cascade ,\n" +
                                         "	idc text not null references commit on delete cascade on update cascade ,\n" +
                                         "	primary key (idp,idc)\n" +
                                         ");";
    
    public static final String CLASSPAC = "create table classpac(\n" +
                                          "	idp integer not null references package on delete cascade on update cascade ,\n" +
                                          "	idc integer not null references class on delete cascade on update cascade ,\n" +
                                          "	primary key (idp,idc)\n" +
                                          ");";
    
    public static final String CLASS = "create table class (\n" +
                                      "	id serial not null primary key ,\n" +
                                      "	name text not null ,\n" +
                                      "	path text ) ;";
    
    public static final String METHOD = "create table method (\n" +
                                        "	id serial not null primary key ,\n" +
                                        "	name text not null ,\n" +
                                        "	isStatic boolean ,\n" +
                                        "	role text ,\n" +
                                        "	signature text ,\n" +
                                        "	idc integer references class on delete cascade on update cascade ) ;"; 
    
    public static final String VARIABLE = "create table variable (\n" +
                                          "	id serial not null primary key ,\n" +
                                          "	name text not null ,\n" +
                                          "	isStatic boolean not null default false,\n" +
                                          "	role text ,\n" +
                                          "	value text ,\n" +
                                          "	idc integer references class on delete cascade on update cascade ) ;"; 
    
    public static final String BUGISTANCE = "create table bugistance (\n" +
                                            "	id serial not null primary key ,\n" +
                                            "	type text not null ,\n" +
                                            "	priority integer not null ,\n" +
                                            "	rank integer not null ,\n" +
                                            "	abbrev text not null ,\n" +
                                            "	category text ,\n" +
                                            "	classname text ,\n" +
                                            "	startsourceline integer ,\n" +
                                            "	endsourceline integer ,\n" +
                                            "	startbytecode integer ,\n" +
                                            "	endsbytecode integer ,\n" +
                                            "	sourcepath text ,\n" +
                                            "	idp integer references package on delete cascade on update cascade ,\n" +
                                            "	idcl integer references class on delete cascade on update cascade ,\n" +
                                            "	idm integer references method on delete cascade on update cascade ,\n" +
                                            "	idv integer references variable on delete cascade on update cascade ,\n" +
                                            "	idbc integer references bugcollection on delete cascade on update cascade \n" +
                                            ") ;";

    /*

    public static final String SMELLISTANCE = "create table bugistance (\n" +
            "	id serial not null primary key ,\n" +
            "   type text not null, \n" +
            "	startsourceline integer ,\n" +
            "	endsourceline integer ,\n" +
            "	sourcepath text ,\n" +
            "   method text not null \n" +
            "   variable text \n" +
            "   idm integer references method on delete cascade on update cascade ,\n" +
            "	idv integer references variable on delete cascade on update cascade ,\n" +
            ") ;";

    */

    
    public static final String BUGCOLLECTION = "create table bugcollection (\n" +
                                                "	id serial not null primary key ,\n" +
                                                "	time timestamp not null ,\n" +
                                                "	version text not null ,\n" +
                                                "	referencedclasses integer not null default 0,\n" +
                                                "	totalclasses integer not null default 0,\n" +
                                                "	totalbugs integer not null default 0,\n" +
                                                "	missingclass text array ,\n" +
                                                "	idc text references commit on delete cascade on update cascade\n" +
                                                ") ;";
    
    public static final String ERROR = "create table error(\n" +
                                        "	id integer references bugcollection on delete cascade on update cascade ,\n" +
                                        "	errormsg text ,\n" +
                                        "	exceptionmsg text ,\n" +
                                        "	primary key (errormsg,exceptionmsg,id)\n" +
                                        ");";
    
    public static final String INFORMATION = "create table information (\n" +
                                            "	id serial not null primary key ,\n" +
                                            "	title text not null ,\n" +
                                            "	value text not null ,\n" +
                                            "	idc text references commit on delete cascade on update cascade\n" +
                                            ") ;"; 
    
    public static final String CLONE = "create table clone (\n" +
                                        "	id serial not null primary key ,\n" +
                                        "	occurrencies integer not null ,\n" +
                                        "	lengthlines integer not null ,\n" +
                                        "	idc text references commit on delete cascade on update cascade\n" +
                                        ") ;";
    
    public static final String PMD = "create table pmd (\n" +
                                        "	id serial not null primary key ,\n" +
                                        "	line integer not null ,\n" +
                                        "	solution text not null ,\n" +
                                        "	type text ,\n" +
                                        "	idc text not null references commit on delete cascade on update cascade ,\n" +
                                        "	idp integer references package on delete cascade on update cascade  ,\n" +
                                        "	idcl integer references class on delete cascade on update cascade\n" +
                                        ") ;";
    
    public static final String METRICHUNTER = "create table metrichunter (\n" +
                                                "	id serial not null primary key ,\n" +
                                                "	line integer not null ,\n" +
                                                "	value double precision not null ,  \n" +
                                                "	type text not null ,\n" +
                                                "	idc text not null references commit on delete cascade on update cascade ,\n" +
                                                "	idm integer references method on delete cascade on update cascade  ,\n" +
                                                "	idcl integer references class on delete cascade on update cascade\n" +
                                                ") ;";
    
    public static final String METRIC = "create table metric (\n" +
                                        "	id serial not null primary key ,\n" +
                                        "	key text not null ,\n" +
                                        "	value double precision not null ,  \n" +
                                        "	idc text not null references commit on delete cascade on update cascade ,\n" +
                                        "	idp integer references package on delete cascade on update cascade  ,\n" +
                                        "	idm integer references method on delete cascade on update cascade  ,\n" +
                                        "	idcl integer references class on delete cascade on update cascade\n" +
                                        ") ;";
    
    public static final String SMELL = "create table smell (\n" +
                                        "	id serial not null primary key ,\n" +
                                        "	key text not null ,\n" +
                                        "	value double precision not null ,  \n" +
                                        "	idc text not null references commit on delete cascade on update cascade ,\n" +
                                        "	idp integer references package on delete cascade on update cascade  ,\n" +
                                        "	idcl integer references class on delete cascade on update cascade ,\n" +
                                        "	idm integer references method on delete cascade on update cascade  ,\n" +
                                        "	idv integer references variable on delete cascade on update cascade ,\n" +
                                        "	linenumber integer \n" +
                                        ") ;";

    public static final String MULTICODESMELL = "create table multicodesmell (\n" +
                                            "    id integer not null ,\n" +
                                            "    smelltype text not null ,\n" +
                                            "    methodname text not null ,\n" +
                                            "    variablename text not null ,\n" +
                                            "    idc text not null references commit on delete cascade on update cascade ,\n" +
                                            "    idp integer references package on delete cascade on update cascade,\n" +
                                            "    idcl integer references class on delete cascade on update cascade ,\n" +
                                            "    idm integer references method on delete cascade on update cascade ,\n" +
                                            "    idv integer references variable on delete cascade on update cascade ,\n" +
                                            "    primary key(id, methodname, variablename, idcl) \n" +
                                            ") ;";
    
    public static final String CLONEISTANCE = "create table cloneistance (\n" +
                                                "	id serial not null primary key ,\n" +
                                                "	linestart integer not null ,\n" +
                                                "	lineend integer not null ,\n" +
                                                "	columnstart integer not null ,\n" +
                                                "	columnend integer not null ,\n" +
                                                "	idc integer not null references clone on delete cascade on update cascade ,\n" +
                                                "	idcl integer references class on delete cascade on update cascade\n" +
                                                ") ;";
    
    public static final String ISSUEISTANCE = "create table issueistance (\n"+
                                            "       id serial not null primary key ,\n"+
                                            "       title text not null ,\n"+
                                            "       state text not null ,\n"+
                                            "       label text array, \n"+
                                            "       description text, \n"+
                                            "       issuenumber text not null, \n"+
                                            "       url text not null references project on delete cascade on update cascade, \n"+
                                            "       filesnamemodified text array, \n"+
                                            "       numberlineschanged integer\n"+
                                            ") ;";
    
    public static final String METRICCLASSCOLLECTION = "create table metricclasscollection (\n"+
                                                  "       id serial not null primary key, \n"+
                                                  "       metricnumber text not null, \n"+
                                                  "       name text not null, \n"+
                                                  "       longname text not null, \n"+
                                                  "       parent text not null, \n"+
                                                  "       component text not null, \n"+
                                                  "       path text not null, \n"+
                                                  "       line text not null, \n"+
                                                  "       startcolumn text not null, \n"+
                                                  "       endline text not null, \n"+
                                                  "       endcolumn text not null, \n"+
                                                  "       cc text not null, \n"+
                                                  "       ccl text not null, \n"+
                                                  "       cco text not null, \n"+
                                                  "       ci text not null, \n"+
                                                  "       clc text not null, \n"+
                                                  "       cllc text not null, \n"+
                                                  "       ldc text not null, \n"+
                                                  "       lldc text not null, \n"+
                                                  "       lcom5 text not null, \n"+
                                                  "       nl text not null, \n"+
                                                  "       nle text not null, \n"+
                                                  "       wmc text not null, \n"+
                                                  "       cbo text not null, \n"+
                                                  "       cboi text not null, \n"+
                                                  "       nii text not null, \n"+
                                                  "       noi text not null, \n"+
                                                  "       rfc text not null, \n"+
                                                  "       ad text not null, \n"+
                                                  "       cd text not null, \n"+
                                                  "       cloc text not null, \n"+
                                                  "       dloc text not null, \n"+
                                                  "       pda text not null, \n"+
                                                  "       pua text not null, \n"+
                                                  "       tcd text not null, \n"+
                                                  "       tcloc text not null, \n"+
                                                  "       dit text not null, \n"+
                                                  "       noa text not null, \n"+
                                                  "       noc text not null, \n"+
                                                  "       nod text not null, \n"+
                                                  "       nop text not null, \n"+
                                                  "       lloc text not null, \n"+
                                                  "       loc text not null, \n"+
                                                  "       na text not null, \n"+
                                                  "       ng text not null, \n"+
                                                  "       nla text not null, \n"+
                                                  "       nlg text not null, \n"+
                                                  "       nlm text not null, \n"+
                                                  "       nlpa text not null, \n"+
                                                  "       nlpm text not null, \n"+
                                                  "       nls text not null, \n"+
                                                  "       nm text not null, \n"+
                                                  "       nos text not null, \n"+
                                                  "       npa text not null, \n"+
                                                  "       npm text not null, \n"+
                                                  "       ns text not null, \n"+
                                                  "       tlloc text not null, \n"+
                                                  "       tloc text not null, \n"+
                                                  "       tna text not null, \n"+
                                                  "       tng text not null, \n"+
                                                  "       tnla text not null, \n"+
                                                  "       tnlg text not null, \n"+
                                                  "       tnlm text not null, \n"+
                                                  "       tnlpa text not null, \n"+
                                                  "       tnlpm text not null, \n"+
                                                  "       tnls text not null, \n"+
                                                  "       tnm text not null, \n"+
                                                  "       tnos text not null, \n"+
                                                  "       tnpa text not null, \n"+
                                                  "       tnpm text not null, \n"+
                                                  "       tns text not null, \n"+
                                                  "       warningblocker text not null, \n"+
                                                  "       warningcritical text not null, \n"+
                                                  "       warninginfo text not null, \n"+
                                                  "       warningmajor text not null, \n"+
                                                  "       warningminor text not null, \n"+
                                                  "       bestpracticerules text not null, \n"+
                                                  "       clonemetricrules text not null, \n"+
                                                  "       codestylerules text not null, \n"+
                                                  "       cohesionmetricrules text not null, \n"+
                                                  "       complexitymetricrules text not null, \n"+
                                                  "       couplingmetricrules text not null, \n"+
                                                  "       designrules text not null, \n"+
                                                  "       documentationmetricrules text not null, \n"+
                                                  "       documentationrules text not null, \n"+
                                                  "       errorpronerules text not null, \n"+
                                                  "       inheritancemetricrules text not null, \n"+
                                                  "       multithreadingrules text not null, \n"+
                                                  "       performancerules text not null, \n"+
                                                  "       securityrules text not null, \n"+
                                                  "       sizemetricrules text not null, \n"+
                                                  "       version text not null references commit on delete cascade on update cascade, \n"+
                                                  "       url text not null references project on delete cascade on update cascade\n"+
                                                  ") ;";
    
    public static final String METRICMETHODCOLLECTION= "create table metricmethodcollection (\n"+
                                                       "       id serial not null primary key, \n"+
                                                       "       metricnumber text not null,\n"+
                                                       "       name text not null, \n"+
                                                       "       longname text not null, \n"+
                                                       "       parent text not null, \n"+
                                                       "       component text not null, \n"+
                                                       "       path text not null, \n"+
                                                       "       line text not null, \n"+
                                                       "       startcolumn text not null, \n"+
                                                       "       endline text not null, \n"+
                                                       "       endcolumn text not null, \n"+
                                                       "       cc text not null, \n"+
                                                       "       ccl text not null, \n"+
                                                       "       cco text not null, \n"+
                                                       "       ci text not null, \n"+
                                                       "       clc text not null, \n"+
                                                       "       cllc text not null, \n"+
                                                       "       ldc text not null, \n"+
                                                       "       lldc text not null, \n"+
                                                       "       hcpl text not null, \n"+
                                                       "       hdif text not null, \n"+
                                                       "       heff text not null, \n"+
                                                       "       hndb text not null, \n"+
                                                       "       hpl text not null, \n"+
                                                       "       hpv text not null, \n"+
                                                       "       htrp text not null, \n"+
                                                       "       hvol text not null, \n"+
                                                       "       mi text not null, \n"+
                                                       "       mims text not null, \n"+
                                                       "       misei text not null, \n"+
                                                       "       mism text not null, \n"+
                                                       "       mccc text not null, \n"+
                                                       "       nl text not null, \n"+
                                                       "       nle text not null, \n"+
                                                       "       nii text not null, \n"+
                                                       "       noi text not null, \n"+
                                                       "       cd text not null, \n"+
                                                       "       cloc text not null, \n"+
                                                       "       dloc text not null, \n"+
                                                       "       tcd text not null, \n"+
                                                       "       tcloc text not null, \n"+
                                                       "       lloc text not null, \n"+
                                                       "       loc text not null, \n"+
                                                       "       nos text not null, \n"+
                                                       "       numpar text not null, \n"+
                                                       "       tlloc text not null, \n"+
                                                       "       tloc text not null, \n"+
                                                       "       tnos text not null, \n"+
                                                       "       warningblocker text not null, \n"+
                                                       "       warningcritical text not null, \n"+
                                                       "       warninginfo text not null, \n"+
                                                       "       warningmajor text not null, \n"+
                                                       "       warningminor text not null, \n"+
                                                       "       bestpracticerules text not null, \n"+
                                                       "       clonemetricrules text not null, \n"+
                                                       "       codestylerules text not null, \n"+
                                                       "       complexitymetricrules text not null, \n"+
                                                       "       couplingmetricrules text not null, \n"+
                                                       "       designrules text not null, \n"+
                                                       "       documentationmetricrules text not null, \n"+
                                                       "       documentationrules text not null, \n"+
                                                       "       errorpronerules text not null, \n"+
                                                       "       multithreadingrules text not null, \n"+
                                                       "       performancerules text not null, \n"+
                                                       "       securityrules text not null, \n"+
                                                       "       sizemetricrules text not null, \n"+
                                                       "       version text not null references commit on delete cascade on update cascade, \n"+
                                                       "       url text not null references project on delete cascade on update cascade\n"+
                                                       ") ;";
                                                       
    public static final String METRICPACKAGECOLLECTION = "create table metricpackagecollection (\n"+
                                                         "       id serial not null primary key, \n"+
                                                         "       metricnumber text not null,\n"+
                                                         "       name text not null, \n"+
                                                         "       longname text not null, \n"+
                                                         "       parent text not null, \n"+
                                                         "       component text not null, \n"+
                                                         "       cc text not null, \n"+
                                                         "       ccl text not null, \n"+
                                                         "       cco text not null, \n"+
                                                         "       ci text not null, \n"+
                                                         "       clc text not null, \n"+
                                                         "       cllc text not null, \n"+
                                                         "       ldc text not null, \n"+
                                                         "       lldc text not null, \n"+
                                                         "       ad text not null, \n"+
                                                         "       cd text not null, \n"+
                                                         "       cloc text not null, \n"+
                                                         "       pda text not null, \n"+
                                                         "       pua text not null, \n"+
                                                         "       tad text not null, \n"+
                                                         "       tcd text not null, \n"+
                                                         "       tcloc text not null, \n"+
                                                         "       tpda text not null, \n"+
                                                         "       tpua text not null, \n"+
                                                         "       lloc text not null, \n"+
                                                         "       loc text not null, \n"+
                                                         "       na text not null, \n"+
                                                         "       ncl text not null, \n"+
                                                         "       nen text not null, \n"+
                                                         "       ng text not null, \n"+
                                                         "       nin text not null, \n"+
                                                         "       nm text not null, \n"+
                                                         "       npa text not null, \n"+
                                                         "       npkg text not null, \n"+
                                                         "       npm text not null, \n"+
                                                         "       ns text not null, \n"+
                                                         "       tlloc text not null, \n"+
                                                         "       tloc text not null, \n"+
                                                         "       tna text not null, \n"+
                                                         "       tncl text not null, \n"+
                                                         "       tndi text not null, \n"+
                                                         "       tnen text not null, \n"+
                                                         "       tnfi text not null, \n"+
                                                         "       tng text not null, \n"+
                                                         "       tnin text not null, \n"+
                                                         "       tnm text not null, \n"+
                                                         "       tnos text not null, \n"+
                                                         "       tnpa text not null, \n"+
                                                         "       tnpcl text not null, \n"+
                                                         "       tnpen text not null, \n"+
                                                         "       tnpin text not null, \n"+
                                                         "       tnpkg text not null, \n"+
                                                         "       tnpm text not null, \n"+
                                                         "       tns text not null, \n"+
                                                         "       warningblocker text not null, \n"+
                                                         "       warningcritical text not null, \n"+
                                                         "       warninginfo text not null, \n"+
                                                         "       warningmajor text not null, \n"+
                                                         "       warningminor text not null, \n"+
                                                         "       bestpracticerules text not null, \n"+
                                                         "       codestylerules text not null, \n"+
                                                         "       designrules text not null, \n"+
                                                         "       documentationrules text not null, \n"+
                                                         "       errorpronerules text not null, \n"+
                                                         "       multithreadingrules text not null, \n"+
                                                         "       performancerules text not null, \n"+
                                                         "       securityrules text not null, \n"+
                                                         "       version text not null references commit on delete cascade on update cascade, \n"+
                                                         "       url text not null references project on delete cascade on update cascade\n"+
                                                         ") ;";
      
   
    
    // insert in tables
    
    public static final String INSERTPROJECT = "insert into project (url,name,path) values (?,?,?);";
    
    public static final String INSERTCOMMIT = "insert into commit (version,data,msg,release,url) values (?,?,?,?,?) returning version;";
    
    public static final String INSERTPACKAGE = "insert into package (name,path) values (?,?) returning id;";
    
    public static final String INSERTBUGCOLLECTION = "insert into bugcollection (time,version,referencedclasses,totalclasses,totalbugs,missingclass,idc) values (?,?,?,?,?,?,?) returning id;";
    
    public static final String INSERTBUGISTANCE = "insert into bugistance (type,priority,rank,abbrev,category,classname,startsourceline,endsourceline,startbytecode,endsbytecode,sourcepath,idp, idcl, idm,idv,idbc) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
    
    public static final String INSERTMETHOD = "insert into method (name,isStatic,role,signature,idc) values (?,?,?,?,?) returning id;";
    
    public static final String INSERTVARIABLE = "insert into variable (name,isStatic,role,value,idc) values (?,?,?,?,?) returning id;";
  
    public static final String INSERTERROR = "insert into error (id,errormsg,exceptionmsg) values (?,?,?);";
    
    public static final String INSERTCLASS = "insert into class (name, path) values (?,?) returning id;";
    
    public static final String INSERTCLONE = "insert into clone (occurrencies, lengthlines, idc) values (?,?,?) returning id;";

    public static final String INSERTCLONEISTANCE = "insert into cloneistance (linestart, lineend , columnstart, columnend, idc, idcl) values (?,?,?,?,?,?) returning id;";
    
    public static final String INSERTMETRIC = "insert into metric (key, value , idc, idm, idcl, idp) values (?,?,?,?,?,?);";

    public static final String INSERTSMELL = "insert into smell (key, value , idc, idm, idcl, idp, idv, linenumber) values (?,?,?,?,?,?,?,?);";
    
    public static final String INSERTMULTICODESMELL = "insert into multicodesmell (id, smelltype, methodname, variablename, idc, idp, idcl, idm, idv) values (?,?,?,?,?,?,?,?,?)";

    public static final String INSERTPMD = "insert into pmd (line, solution, type, idc, idcl) values (?,?,?,?,?);";
    
    public static final String INSERTMETRICHUNTER = "insert into metrichunter (line, value, type, idc, idm, idcl) values (?,?,?,?,?,?);";
    
    public static final String INSERTCOMMPAC = "insert into commpac (idp,idc) values (?,?);";
    
    public static final String INSERTCLASSPAC = "insert into classpac (idp,idc) values (?,?);";
    
    public static final String INSERTISSUEISTANCE= "insert into issueistance (title,state,label,description,issuenumber,url,filesnamemodified,numberlineschanged) values (?,?,?,?,?,?,?,?) returning id;";
    
    public static final String INSERTMETRICCLASSCOLLECTION = "insert into metricclasscollection (metricnumber,name,longname,parent,component,path,line,startcolumn,endline,endcolumn,cc,ccl,cco,ci,clc,cllc,ldc,lldc,lcom5,nl,nle,wmc,cbo,cboi,nii,noi,rfc,ad,cd,cloc,dloc,pda,pua,tcd,tcloc,dit,noa,noc,nod,nop,lloc,loc,na,ng,nla,nlg,nlm,nlpa,nlpm,nls,nm,nos,npa,npm,ns,tlloc,tloc,tna,tng,tnla,tnlg,tnlm,tnlpa,tnlpm,tnls,tnm,tnos,tnpa,tnpm,tns,warningblocker,warningcritical,warninginfo,warningmajor,warningminor,bestpracticerules,clonemetricrules,codestylerules,cohesionmetricrules,complexitymetricrules,couplingmetricrules,designrules,documentationmetricrules,documentationrules,errorpronerules,inheritancemetricrules,multithreadingrules,performancerules,securityrules,sizemetricrules,version,url) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); ";
                                                                            
    public static final String INSERTMETRICMETHODCOLLECTION= "insert into metricmethodcollection (metricnumber,name,longname,parent,component,path,line,startcolumn,endline,endcolumn,cc,ccl,cco,ci,clc,cllc,ldc,lldc,hcpl,hdif,heff,hndb,hpl,hpv,htrp,hvol,mi,mims,misei,mism,mccc,nl,nle,nii,noi,cd,cloc,dloc,tcd,tcloc,lloc,loc,nos,numpar,tlloc,tloc,tnos,warningblocker,warningcritical,warninginfo,warningmajor,warningminor,bestpracticerules,clonemetricrules,codestylerules,complexitymetricrules,couplingmetricrules,designrules,documentationmetricrules,documentationrules,errorpronerules,multithreadingrules,performancerules,securityrules,sizemetricrules,version,url) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); ";
    
    public static final String INSERTMETRICPACKAGECOLLECTION= "insert into metricpackagecollection (metricnumber,name,longname,parent,component,cc,ccl,cco,ci,clc,cllc,ldc,lldc,ad,cd,cloc,pda,pua,tad,tcd,tcloc,tpda,tpua,lloc,loc,na,ncl,nen,ng,nin,nm,npa,npkg,npm,ns,tlloc,tloc,tna,tncl,tndi,tnen,tnfi,tng,tnin,tnm,tnos,tnpa,tnpcl,tnpen,tnpin,tnpkg,tnpm,tns,warningblocker,warningcritical,warninginfo,warningmajor,warningminor,bestpracticerules,codestylerules,designrules,documentationrules,errorpronerules,multithreadingrules,performancerules,securityrules,version,url) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ;";
    
    // query tables
    
    public static final String GETPROJECTS = "select * from project;";
    
    public static final String GETCOMMITS = "select * from commit where url = ? ;";
    
    public static final String URLEXIST = "select * from project where url = ? ;";
    
    public static final String CLASSINCOMMIT = "select commpac.idc as commitid , class.id  as classid , package.name as packagename , package.id as packageid \n" +
                                               "from class , classpac ,commpac , package \n" +
                                               "where class.name = ? and class.id = classpac.idc and classpac.idp=commpac.idp and package.id = classpac.idp ;" ;   
    
    public static final String METHODINCOMMIT = "select commpac.idc as commitid, class.name  as classname, class.id  as classid, package.name as packagename, package.id as packageid , method.id as methodid \n" +
                                                "from class , classpac ,commpac , package , method \n" +
                                                "where method.name = ? and class.id = classpac.idc and classpac.idp=commpac.idp and package.id = classpac.idp and method.idc = class.id ;";
    
    public static final String VARIABLEINCOMMIT = "select commpac.idc as commitid, class.name  as classname, class.id  as classid, package.name as packagename, package.id as packageid , variable.id as variableid \n" +
                                                  "from class , classpac ,commpac , package , variable \n" +
                                                  "where variable.name = ? and class.id = classpac.idc and classpac.idp=commpac.idp and package.id = classpac.idp and variable.idc = class.id ;";
    
    public static final String GETALLBUG= "select * from issueistance where url= ? ;";
    
    
    
    
    
    // QUERY CONTROLLO JCODE,SOURCEMETER
    public static final String PACKAGEEXIST = "select distinct package.id from package where package.name=?;";
    
    public static final String CLASSINPROJECT = "select distinct class.id from class where class.name = ? and class.path = ?;";

    public static final String METHODINCLASS = "select distinct method.id from method where method.name = ? and method.idc = ?;";

    public static final String METHODWITHSIGNATURE = "select distinct method.id from method where method.name = ? and method.signature = ?;";

    public static final String VARIABLEWITHROLE = "select distinct variable.id from variable where variable.name = ? and variable.role = ?;";

    public static final String VARIABLEINCLASS = "select distinct variable.id from variable where variable.name = ? and variable.idc = ?;";

    public static final String CLASSPATH = "select class.path from class where class.id = ?;";
    
    public static final String UPDATECLASSPATH = "update class set path =? where class.id = ?;";
    
    public static final String PMDRECORDEXISTS= "select pmd.id from pmd where line=? and solution =? and type =? and idc=? and idcl=?;";
    
    public static final String METRICHUNTERRECORDEXISTS= "select metrichunter.id from metrichunter where line =? and value=? and type =? and idc=? and";
    
    public static final String CLONERECORDEXISTS= "select clone.id from clone where occurrencies =? and lengthlines=? and idc=?;";
    
    public static final String CLONEISTANCERECORDEXISTS= "select cloneistance.id from cloneistance where linestart =? and lineend=? and columnstart=? and columnend=? and idc=? and idcl=?;";
    
    public static final String METRICRECORDEXISTS= "select metric.id from metric where key =? and value=? and idc=? and";
  
    public static final String SMELLRECORDEXISTS= "select smell.id from smell where key =? and value=? and idc=? and";

    public static final String MULTICODESMELLRECORDEXISTS= "select multicodesmell.id from multicodesmell where smelltype=? and idcl=? and";

    public static final String LASTIDBLOCK= "select max(id) as max_id from multicodesmell";
    
   // public static final String METRICCLASSEXISTS= "select id from metricclasscollection where id=? ;";
    
  //  public static final String METRICMETHODEXISTS= "select id from metricmethodcollection where id=? ;";
    
  //  public static final String METRICPACKAGEEXISTS= "select id from metricpackagecollection where id=? ;";
    
}
