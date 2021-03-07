package tools;

import java.util.ArrayList;

/**
 * Class responsible for starting all analisys 
 */
public class Analyze {
    private static Analyze analyzer;
    
    /**
     * Costruct a unique Analize object. Use Singleton Pattern
     * @return 
     */
    private static Analyze Analyze(){
        if(analyzer != null)
            return analyzer;
        else
            return new Analyze();
    }
    
    public static Analyze getAnalyzer(){
        return Analyze();
    }
    
    /**
     * Start the analysis of the tools 
     * @param tools tools to run
     * @return boolean 
     */
    public boolean startAnalyze(ArrayList<Tool> tools){

        ArrayList<StartTool> t = new ArrayList<>();
        try{
            if(tools!=null){
                for(int i=0;i<tools.size();i++){
                    StartTool st=new StartTool(tools.get(i));
                    t.add(st);
                    Thread.sleep(50);
                   }
                }
            if(!(t.isEmpty()))
                for(int i=0;i<t.size();i++)
                    t.get(i).join();
         }catch(InterruptedException e){
            System.out.println(e);
            return false;
         }
        return true;
    }
}
