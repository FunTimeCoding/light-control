/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.funtimecoding.light.control;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author shiin
 */
class DropdownRenderer extends JButton implements ListCellRenderer {

    LightControlMainFrame mc;

    public DropdownRenderer(LightControlMainFrame mc) {
        this.mc = mc;
        setOpaque(true);

    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(this.getContrastColor(fg)); //To change body of generated methods, choose Tools | Templates.
    }
    
    

    private void setBackground(String farbName) {
        int[] farbwerte = this.mc.ladeFarbeFürNamen(farbName);
        Color c = new Color(farbwerte[0], farbwerte[1], farbwerte[2]);
        this.setBackground(c);
        this.setForeground(c);
    }

    @Override
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        setText((String) value);
        setBackground((String) value);
        return this;
    }

    private Color getContrastColor(Color color) {
        double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
        return y >= 128 ? Color.black : Color.white;
    }
}
