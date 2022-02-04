package gui;

import repository.RepositoryHandler;
import javax.swing.*;
import java.awt.event.*;

public class GitTokenRequest extends JDialog {
    private RepositoryHandler repoH;

    public GitTokenRequest(RepositoryHandler repoH) {
    	initComponents();
		this.repoH = repoH;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        pack();
        setVisible(true);
    }

    private void onOK() {
        // add your code here
        String token = tokenField.getText();
        if (token.isBlank()) {
            JOptionPane.showMessageDialog(null, "Inserisci un token!", "WARNING", JOptionPane.WARNING_MESSAGE);
        } else {
            repoH.token = token;
            dispose();
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Giovanni
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTextField tokenField;
	// JFormDesigner - End of variables declaration  //GEN-END:variables


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
		var label1 = new JLabel();
		var hSpacer2 = new JPanel(null);
		tokenField = new JTextField();

		//======== contentPane ========
		{
			contentPane.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder
			(0,0,0,0), "JF\u006frmDesi\u0067ner Ev\u0061luatio\u006e",javax.swing.border.TitledBorder.CENTER,javax.swing.border
			.TitledBorder.BOTTOM,new java.awt.Font("Dialo\u0067",java.awt.Font.BOLD,12),java.awt
			.Color.red),contentPane. getBorder()));contentPane. addPropertyChangeListener(new java.beans.PropertyChangeListener(){@Override public void
			propertyChange(java.beans.PropertyChangeEvent e){if("borde\u0072".equals(e.getPropertyName()))throw new RuntimeException()
			;}});

			//======== panel1 ========
			{

				//======== panel2 ========
				{

					//---- buttonOK ----
					buttonOK.setText("OK");

					//---- buttonCancel ----
					buttonCancel.setText("Cancel");

					GroupLayout panel2Layout = new GroupLayout(panel2);
					panel2.setLayout(panel2Layout);
					panel2Layout.setHorizontalGroup(
						panel2Layout.createParallelGroup()
							.addGroup(panel2Layout.createSequentialGroup()
								.addComponent(buttonOK, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
								.addGap(10, 10, 10)
								.addComponent(buttonCancel))
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
							.addComponent(hSpacer1, GroupLayout.PREFERRED_SIZE, 262, GroupLayout.PREFERRED_SIZE)
							.addGap(10, 10, 10)
							.addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				);
				panel1Layout.setVerticalGroup(
					panel1Layout.createParallelGroup()
						.addGroup(panel1Layout.createSequentialGroup()
							.addGap(8, 8, 8)
							.addComponent(hSpacer1, GroupLayout.PREFERRED_SIZE, 12, GroupLayout.PREFERRED_SIZE))
						.addComponent(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				);
			}

			//======== panel3 ========
			{

				//---- label1 ----
				label1.setText("Insert personal token to authenticate into github: ");

				GroupLayout panel3Layout = new GroupLayout(panel3);
				panel3.setLayout(panel3Layout);
				panel3Layout.setHorizontalGroup(
					panel3Layout.createParallelGroup()
						.addComponent(label1)
						.addGroup(panel3Layout.createSequentialGroup()
							.addGap(336, 336, 336)
							.addComponent(hSpacer2, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE))
						.addComponent(tokenField, GroupLayout.PREFERRED_SIZE, 326, GroupLayout.PREFERRED_SIZE)
				);
				panel3Layout.setVerticalGroup(
					panel3Layout.createParallelGroup()
						.addGroup(panel3Layout.createSequentialGroup()
							.addGap(40, 40, 40)
							.addComponent(label1)
							.addGap(40, 40, 40)
							.addComponent(hSpacer2, GroupLayout.PREFERRED_SIZE, 12, GroupLayout.PREFERRED_SIZE)
							.addGap(30, 30, 30)
							.addComponent(tokenField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				);
			}

			GroupLayout contentPaneLayout = new GroupLayout(contentPane);
			contentPane.setLayout(contentPaneLayout);
			contentPaneLayout.setHorizontalGroup(
				contentPaneLayout.createParallelGroup()
					.addGroup(contentPaneLayout.createSequentialGroup()
						.addGap(10, 10, 10)
						.addGroup(contentPaneLayout.createParallelGroup()
							.addComponent(panel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
			);
			contentPaneLayout.setVerticalGroup(
				contentPaneLayout.createParallelGroup()
					.addGroup(contentPaneLayout.createSequentialGroup()
						.addGap(10, 10, 10)
						.addComponent(panel3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(5, 5, 5)
						.addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
			);
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}
}
