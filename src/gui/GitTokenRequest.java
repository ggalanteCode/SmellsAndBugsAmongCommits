package gui;

import repository.RepositoryHandler;

import javax.swing.*;
import java.awt.event.*;

public class GitTokenRequest extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField tokenField;
    private RepositoryHandler repoH;

    public GitTokenRequest(RepositoryHandler repoH) {
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
        System.exit(0);
        dispose();
    }
}
