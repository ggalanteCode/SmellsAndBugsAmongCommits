package gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Adv extends JDialog {
    private boolean decision;

    public Adv(String msg) {
        initComponents();
		setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        messageLable.setText(msg);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onYes();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onNo();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onNo();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onNo();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        pack();
        setVisible(true);
    }

    private void onYes() {
        decision = true;
        dispose();
    }

    private void onNo() {
        decision = false;
        dispose();
    }

    public boolean getDecision(){
        return decision;
    }

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Giovanni
		contentPane = new JPanel();
		var panel1 = new JPanel();
		var hSpacer1 = new JPanel(null);
		var panel2 = new JPanel();
		buttonOK = new JButton();
		buttonCancel = new JButton();
		var panel3 = new JPanel();
		messageLable = new JLabel();

		//======== contentPane ========
		{
			contentPane.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(new javax
			.swing.border.EmptyBorder(0,0,0,0), "JF\u006frmDesi\u0067ner Ev\u0061luatio\u006e",javax.swing
			.border.TitledBorder.CENTER,javax.swing.border.TitledBorder.BOTTOM,new java.awt.
			Font("Dialo\u0067",java.awt.Font.BOLD,12),java.awt.Color.red
			),contentPane. getBorder()));contentPane. addPropertyChangeListener(new java.beans.PropertyChangeListener(){@Override
			public void propertyChange(java.beans.PropertyChangeEvent e){if("borde\u0072".equals(e.getPropertyName(
			)))throw new RuntimeException();}});

			//======== panel1 ========
			{

				//======== panel2 ========
				{

					//---- buttonOK ----
					buttonOK.setText("Yes");

					//---- buttonCancel ----
					buttonCancel.setText("No");

					GroupLayout panel2Layout = new GroupLayout(panel2);
					panel2.setLayout(panel2Layout);
					panel2Layout.setHorizontalGroup(
						panel2Layout.createParallelGroup()
							.addGroup(panel2Layout.createSequentialGroup()
								.addComponent(buttonOK)
								.addGap(10, 10, 10)
								.addComponent(buttonCancel, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE))
					);
					panel2Layout.setVerticalGroup(
						panel2Layout.createParallelGroup()
							.addComponent(buttonOK)
							.addComponent(buttonCancel)
					);
				}

				GroupLayout panel1Layout = new GroupLayout(panel1);
				panel1.setLayout(panel1Layout);
				panel1Layout.setHorizontalGroup(
					panel1Layout.createParallelGroup()
						.addGroup(panel1Layout.createSequentialGroup()
							.addComponent(hSpacer1, GroupLayout.PREFERRED_SIZE, 298, GroupLayout.PREFERRED_SIZE)
							.addGap(10, 10, 10)
							.addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				);
				panel1Layout.setVerticalGroup(
					panel1Layout.createParallelGroup()
						.addGroup(panel1Layout.createSequentialGroup()
							.addGap(5, 5, 5)
							.addComponent(hSpacer1, GroupLayout.PREFERRED_SIZE, 12, GroupLayout.PREFERRED_SIZE))
						.addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				);
			}

			//======== panel3 ========
			{

				//---- messageLable ----
				messageLable.setText("");

				GroupLayout panel3Layout = new GroupLayout(panel3);
				panel3.setLayout(panel3Layout);
				panel3Layout.setHorizontalGroup(
					panel3Layout.createParallelGroup()
						.addGroup(panel3Layout.createSequentialGroup()
							.addComponent(messageLable, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addContainerGap())
				);
				panel3Layout.setVerticalGroup(
					panel3Layout.createParallelGroup()
						.addGroup(panel3Layout.createSequentialGroup()
							.addGap(66, 66, 66)
							.addComponent(messageLable, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE)
							.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);
			}

			GroupLayout contentPaneLayout = new GroupLayout(contentPane);
			contentPane.setLayout(contentPaneLayout);
			contentPaneLayout.setHorizontalGroup(
				contentPaneLayout.createParallelGroup()
					.addGroup(contentPaneLayout.createSequentialGroup()
						.addGap(10, 10, 10)
						.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
							.addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(panel3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
			);
			contentPaneLayout.setVerticalGroup(
				contentPaneLayout.createParallelGroup()
					.addGroup(contentPaneLayout.createSequentialGroup()
						.addGap(10, 10, 10)
						.addComponent(panel3, GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE)
						.addGap(5, 5, 5)
						.addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			);
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Giovanni
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JLabel messageLable;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
