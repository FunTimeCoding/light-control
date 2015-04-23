/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.funtimecoding.light.control;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.TimerTask;
import javax.swing.JComboBox;
import javax.swing.JSlider;

/**
 *
 * @author K. Sauter
 */
public class CommandPoller extends TimerTask{
    
    private Connection c;
    private final JSlider redSlider;
    private final JSlider greenSlider;
    private final JSlider blueSlider;
    private final JComboBox dropdown;

    public CommandPoller(Connection c, JSlider redSlider, JSlider greenSlider, JSlider blueSlider, JComboBox dropdown) {
        this.c = c;
        this.redSlider = redSlider;
        this.greenSlider = greenSlider;
        this.blueSlider = blueSlider;
        this.dropdown = dropdown;
    }
    
    @Override
    public void run() {
        Statement stmt = null;
        try {
            stmt = this.c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM rgb_control WHERE status = 0 ORDER BY id DESC LIMIT 1");
            while (rs.next()) {
                //bissl updaten :D
                String daten = LightControlMainFrame.dropdownItems.get(rs.getString("requestName"));
                String[] daten2 = daten.split(";");
                int red = Integer.parseInt(daten2[0]);
                int green = Integer.parseInt(daten2[1]);
                int blue = Integer.parseInt(daten2[2]);
                this.redSlider.setValue(red);
                this.greenSlider.setValue(green);
                this.blueSlider.setValue(blue);
                String msg = "C " + red + " " + green + " " + blue + "\n";
                System.out.print(msg);
                this.dropdown.setSelectedItem(rs.getString("requestName"));
                try {
                    OutputStream out = LightControlMainFrame.control.getOut();
                    int len = msg.length();
                    for (int i = 0; i < len; i++) {
                        out.write(msg.charAt(i));
                    }
                } catch (Exception e) {
                    System.err.println("Error in stateChanged: " + e.getMessage());
                }
            }
            stmt.executeUpdate("UPDATE rgb_control SET status = '1'");
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        
    }
    
}
