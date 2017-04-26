/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ToolUI;

import javax.swing.JComboBox;
import javax.swing.JTextField;

/**
 *
 * @author kamin
 */
public class FocusComboBox extends JComboBox{
    
    LabelManager theManager;
    
    public FocusComboBox(LabelManager myManager, String val){
        theManager = myManager;
        this.setEditable(true);
        this.addItem(val);
        JTextField thisField = (JTextField)getEditor().getEditorComponent();
        thisField.setText(val);
        FocusBoxListener myListener = new FocusBoxListener(this,theManager);
        thisField.addFocusListener(myListener);
        this.addFocusListener(myListener);
        //this.addFocusListener(myListener);
            
    }
    
    
}
