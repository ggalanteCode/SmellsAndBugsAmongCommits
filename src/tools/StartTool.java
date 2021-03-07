/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

/**
 * class that handles the execution of tools based on the instance received
 * @author alessio
 */
public class StartTool extends Thread {
    
    private Tool t;
    
    /**
     * @param t tool instance
     * Constructor
     */
    public StartTool(Tool t){
        this.t=t;
        start();
    }
    
    
    /**
     * executes the runWin or runLin method based on the value associated with the generic instance t.
     */
    @Override
    public void run(){
        try{
            System.out.println("Starting tool...");
            Thread.sleep(50);
            if(System.getProperty("os.name").startsWith("Windows"))
                t.runWin();
            else
                t.runLin();
        }catch(InterruptedException e){
            System.out.println(e);
        }
    }
}
