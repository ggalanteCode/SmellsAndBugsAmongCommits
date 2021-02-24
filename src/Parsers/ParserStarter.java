package Parsers;


import java.util.ArrayList;
import tools.Tool;

/**
 * class used to create threads that will run parsers
 */
public class ParserStarter {
    private ArrayList<Tool> tools;
    private String idCommit;
    private String projectUrl;
    
    /**
     * Costructor
     * @param tools tool used for the analysis
     * @param idCommit commit analyzed
     * @param projectUrl project analyzed
     */
    public ParserStarter(ArrayList<Tool> tools,String idCommit, String projectUrl) {
        this.tools=tools;
        this.idCommit=idCommit;
        this.projectUrl=projectUrl;
        ArrayList<ParserToolThread> parserTool=new ArrayList<>();
        ParserToolThread pth;
        try{
            for(int i=0;i<tools.size();i++){
                pth=new ParserToolThread(tools.get(i),idCommit,projectUrl);
                parserTool.add(pth);
                Thread.sleep(50);
            }
            if(!(parserTool.isEmpty()))
              for(int i=0;i<parserTool.size();i++)
                parserTool.get(i).join();
        }catch(InterruptedException ie){
            System.out.println(ie);
        }
    }
    
}
