/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.ListIterator;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import tools.Tool;

/**
 * Class that can be used in future to display a loading.
 * @author night
 */
public class Loading extends javax.swing.JDialog {

    /**
     * Creates new form Loading
     */
    public Loading() {
        setTitle("loading");
        initComponents();
        //Icon imgIcon = new ImageIcon(this.getClass().getResource(File.separator+"gui"+File.separator+"images"+File.separator+"spinner.gif"));
        //spinner.setIcon(imgIcon);
        pack();
        setVisible(true);
    }
    /**
    * Method used to understand if tools ended the analyzing phase
    * @param tools ArrayList of tools 
    * @see ArrayList
    * @return string that tells which tools failed or not
    * @author night
    */
    public static String checkEnd(ArrayList<Tool> tools){
        String end = "<HTML>" ;
        ListIterator<Tool> i=tools.listIterator();
        while(i.hasNext()){
            Tool tmp=i.next();
            if(tmp.getExitCode() != 0){
                end+=tmp.getClass().getSimpleName()+": tool failed<BR>";
                i.remove();
            }
            else
                end+= tmp.getClass().getSimpleName()+": tool successful<BR>";
        }

        return end+="</HTML>";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	// Generated using JFormDesigner Evaluation license - Giovanni
	private void initComponents() {
		jPanel1 = new JPanel();
		spinner = new JLabel();

		//======== this ========
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		var contentPane = getContentPane();

		//======== jPanel1 ========
		{
			jPanel1.setBackground(Color.black);
			jPanel1.setToolTipText("");
			jPanel1.setBorder (new javax. swing. border. CompoundBorder( new javax .swing .border .TitledBorder (new javax.
			swing. border. EmptyBorder( 0, 0, 0, 0) , "JF\u006frm\u0044es\u0069gn\u0065r \u0045va\u006cua\u0074io\u006e", javax. swing. border
			. TitledBorder. CENTER, javax. swing. border. TitledBorder. BOTTOM, new java .awt .Font ("D\u0069al\u006fg"
			,java .awt .Font .BOLD ,12 ), java. awt. Color. red) ,jPanel1. getBorder
			( )) ); jPanel1. addPropertyChangeListener (new java. beans. PropertyChangeListener( ){ @Override public void propertyChange (java
			.beans .PropertyChangeEvent e) {if ("\u0062or\u0064er" .equals (e .getPropertyName () )) throw new RuntimeException
			( ); }} );

			GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
			jPanel1.setLayout(jPanel1Layout);
			jPanel1Layout.setHorizontalGroup(
				jPanel1Layout.createParallelGroup()
					.addGroup(jPanel1Layout.createSequentialGroup()
						.addGap(40, 40, 40)
						.addComponent(spinner, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(40, Short.MAX_VALUE))
			);
			jPanel1Layout.setVerticalGroup(
				jPanel1Layout.createParallelGroup()
					.addGroup(jPanel1Layout.createSequentialGroup()
						.addGap(40, 40, 40)
						.addComponent(spinner, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(40, Short.MAX_VALUE))
			);
		}

		GroupLayout contentPaneLayout = new GroupLayout(contentPane);
		contentPane.setLayout(contentPaneLayout);
		contentPaneLayout.setHorizontalGroup(
			contentPaneLayout.createParallelGroup()
				.addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);
		contentPaneLayout.setVerticalGroup(
			contentPaneLayout.createParallelGroup()
				.addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);
		pack();
		setLocationRelativeTo(getOwner());
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Giovanni
	private JPanel jPanel1;
	private JLabel spinner;
    // End of variables declaration//GEN-END:variables
}