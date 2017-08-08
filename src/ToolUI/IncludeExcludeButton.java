/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ToolUI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JToggleButton;

/**
 *
 * @author kamin
 */
public class IncludeExcludeButton extends JToggleButton {
    public IncludeExcludeButton(){
       
        this.setText("INCLUDE");
        this.setSelected(true);
        this.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JToggleButton sourceButton = (JToggleButton) e.getSource();
                if (sourceButton.isSelected()) {
                    sourceButton.setText("INCLUDE");
                } else {
                    sourceButton.setText("EXCLUDE");
                }
        }
    });
    }
}
