package gui;

import javax.swing.*;

public class WorkingAdv extends Thread {

    private final JFrame f;
    private final String lableText = "Working";
    private JPanel panel;
    private JLabel label;
    private boolean stop = false;
    int counter = 0;

    public WorkingAdv() {
        f = new JFrame("SBAC");

        label.setText(lableText);
        f.setSize(250,120);
        f.setContentPane(panel);
        f.setVisible(true);

        start();
    }

    public void run() {
        working();
    }


    public synchronized void halt() {
        stop = true;
    }

    private synchronized boolean check() {
        try {
            wait(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stop;
    }

    private void working() {
        while (!check()) {
            if (counter%4 == 0) {
                label.setText(lableText);
                counter = 0;
            } else {
                label.setText(label.getText()+".");
            }
            System.out.println(label.getText());
            counter++;
        }
        f.dispose();
    }
}