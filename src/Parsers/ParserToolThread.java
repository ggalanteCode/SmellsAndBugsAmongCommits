/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Comparator;

import tools.*;

/**
 * class that handles the execution of parsers associated with tools
 * @author alessio
 */
public class ParserToolThread extends Thread {
    
    private Tool tool;
    private String idCommit;
    private String projectUrl;
    
    /**
     * Constructor
     * @param t tool instance
     * @param idCommit commit analyzed
     * @param projectUrl url project
     */
    public ParserToolThread(Tool t, String idCommit, String projectUrl){
        this.tool=t;
        this.idCommit=idCommit;
        this.projectUrl=projectUrl;
        start();
    }
    
    /**
     * method that runs sourcemeter parsers
     * @param t tool instance
     */
    public void startSourceMeter(Tool t){
        SourceMeter sm=(SourceMeter)t;
        TxtParser txtParser;
        CSVParser csvParser;
        String path;
                
        File dir = new File(sm.getOutputPath());
        File[] files = dir.listFiles();
        File lastModified = Arrays.stream(files).filter(File::isDirectory).max(Comparator.comparing(File::lastModified)).orElse(null);
        txtParser= new TxtParser(new PMDReader(idCommit, projectUrl));
        txtParser.parse(lastModified.getAbsolutePath()+File.separator+sm.getProjectName()+"-PMD.txt");
        txtParser= new TxtParser(new ClonesReader(idCommit, projectUrl));
        txtParser.parse(lastModified.getAbsolutePath()+File.separator+sm.getProjectName()+"-clones.txt");
        txtParser= new TxtParser(new MetricHunterReader(idCommit, projectUrl));
        txtParser.parse(lastModified.getAbsolutePath()+File.separator+sm.getProjectName()+"-MetricHunter.txt");
                
        path=lastModified.getAbsolutePath()+File.separator+sm.getProjectName()+"-Class.csv";
        csvParser=new CSVParser(new MetricCollectionReader(idCommit,projectUrl,path));
        csvParser.parse(path);
                
        path=lastModified.getAbsolutePath()+File.separator+sm.getProjectName()+"-Method.csv";
        csvParser=new CSVParser(new MetricCollectionReader(idCommit,projectUrl,path));
        csvParser.parse(path);
                
        path=lastModified.getAbsolutePath()+File.separator+sm.getProjectName()+"-Package.csv";
        csvParser=new CSVParser(new MetricCollectionReader(idCommit,projectUrl,path));
        csvParser.parse(path);
            
        System.out.println("Parser SourceMeter terminato");
    }
    
    /**
     * method that runs spotbugs parsers
     * @param t tool istance
     * @throws Exception outputPath
     */
    public void startSpotBugs(Tool t)throws Exception{
        SpotBugs sb=(SpotBugs)t;
        String outputPath = "test"+File.separator+"analisi";
        XMLparser xmLparser = new XMLparser(new SpotBugsExtrapolator(idCommit));
        outputPath = sb.getOutputPath();
        xmLparser.parse(outputPath);
        System.out.println("Parser SpotBugs terminato");
    }
    
    /**
     * method that runs jcodeodor parsers
     * @param t tool instance
     * @throws Exception outputPath
     */
    public void startJcodeOdor(Tool t) throws Exception{
        JcodeOdor jo=(JcodeOdor)t;
        XMLparser xmlParser = new XMLparser(new JCodeOdorExtrapolator(idCommit, projectUrl));
        xmlParser.parse(jo.getOutputPath());
        System.out.println("Parser JcodeOdor terminato");
    }

    /**
     * method that runs PhDProjectScriptsParsers.
     * @param t
     */
    public void startPhDSmellsParser(Tool t) {
        try {

            PhDSmellsParser parser = new PhDSmellsParser(idCommit, projectUrl);
            PhDSmells ps = (PhDSmells) t;
            String resultFileAbsPath = ps.getToolPath() + File.separator + parser.getAnalyzableFileName();
            File result = new File(resultFileAbsPath);
            parser.execute(new BufferedReader(new FileReader(result)));
            result.delete();

        } catch (Exception e) {
            System.err.println("Nessun file 'result.txt' trovato\n");
            e.printStackTrace();
        }
    }
    
    /**
     *run method executed by threads to use parsers 
     */
    @Override
    public void run(){
        try{
            if(tool instanceof SourceMeter){
                System.out.println("Partiti parser di SourceMeter");
                Thread.sleep(50);
                this.startSourceMeter(tool);

            } else if(tool instanceof SpotBugs){
                System.out.println("Partito parser di SpotBugs");
                Thread.sleep(70);
                this.startSpotBugs(tool);

            } else if (tool instanceof JcodeOdor){
                System.out.println("Partito parser di JcodeOdor");
                Thread.sleep(50);
                this.startJcodeOdor(tool);
            } else if (tool instanceof PhDSmells) {
                System.out.println("Partito parser di PhdProjectScripts");
                Thread.sleep(50);
                this.startPhDSmellsParser(tool);
            }
        }catch(InterruptedException e){
            System.out.println(e);
        }catch(Exception e){
            System.out.println(e);
        }
           
    }
    
}
