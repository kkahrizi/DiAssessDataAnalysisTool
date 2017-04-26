/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ToolUI;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JTextField;

/**
 *
 * @author kamin
 */
public class FocusBoxListener implements FocusListener {
    
    FocusComboBox focusBox;
    LabelManager theManager;
    
    public FocusBoxListener(FocusComboBox listenToMe, LabelManager toManage){
        focusBox = listenToMe;
        theManager = toManage;
    }
    
    public void focusGained(FocusEvent e) {
        JTextField myField = (JTextField) focusBox.getEditor().getEditorComponent();
        myField.select(0,myField.getText().length());
        
        
        if (theManager == null){
            System.out.println("Flag");
        }
            
        if(theManager.getOtherLabels() != null ){
            String[] allLabels = theManager.getOtherLabels();
            for(int i = 0; i < allLabels.length; i++){
                focusBox.addItem(allLabels[i]);
            }
        }
        
//        String currentValue = (String)focusBox.getSelectedItem();
//        if(currentValue != null) {    
//            theManager.removeALabel(currentValue);
//        }
//        if(theManager == null) {
//            System.out.println("Flag 1");
//        }
//        String[] labels = theManager.getOtherLabels();
//        for(int i = 0; i < labels.length; i++){
//            focusBox.addItem(labels[i]);
//        }
//        
//        JTextField myField = (JTextField) focusBox.getEditor().getEditorComponent();
//        focusBox.getEditor().selectAll();
//        myField.select(0,myField.getText().length());
    }

    public void focusLost(FocusEvent e) {
        String enteredText = ((JTextField) focusBox.getEditor().getEditorComponent()).getText();
        theManager.addALabel(enteredText);
    }
    
}
