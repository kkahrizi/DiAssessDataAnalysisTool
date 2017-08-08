/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ToolUI;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author kamin
 */
public class qPCRSampleGUI {
   
    public ArrayList <qPCRSample> allSamples;
    public HashMap<Integer, JLabel> allDilutionLabels;
    public HashMap<Integer, IncludeExcludeButton> allIncludeExcludeButton;
    public HashMap <Integer, JLabel> allSampleLabels;
    public HashMap <Integer, FocusTextField> allSampleTextFields;
    public HashMap<Integer, JLabel> r2Labels;
    public HashMap<Integer, Double> r2Values;
    public HashMap<Integer, JLabel> effLabels;
    public HashMap<Integer, Double> effValues;
    public HashMap<Integer, JSeparator> separators;

    public final int LARGENUMBER = 10; //Assumes fewer than this many dilutions in all samples
   
    
    public qPCRSampleGUI(ArrayList<qPCRSample> samplePoints){
        allSamples = samplePoints;
        allDilutionLabels = new HashMap<>();
        allSampleLabels = new HashMap <>();
        allSampleTextFields = new HashMap < >();
        allIncludeExcludeButton = new HashMap <>();
        r2Labels = new HashMap<>();
        r2Values = new HashMap<>();
        effLabels = new HashMap<>();
        effValues = new HashMap<>();
        separators = new HashMap<>();
        
        Collections.sort(allSamples);
        for (int i = 0; i < allSamples.size(); i++) {
            ArrayList<qPCRDataPoint> dataPointsInSample = allSamples.get(i).getDilutions();
            allSampleLabels.put(i,new JLabel(allSamples.get(i).getLabel()));
            allSampleTextFields.put(i,new FocusTextField());
            r2Labels.put(i,new JLabel("R^2="));
            effLabels.put(i, new JLabel("Effic.="));
            
            if (i != allSamples.size()-1 ){ //no separator after the last column
                separators.put(i, new JSeparator(SwingConstants.VERTICAL));
            }
             
            Collections.sort(dataPointsInSample);
            for (int j = 0; j < dataPointsInSample.size(); j++) {
                allDilutionLabels.put(i * LARGENUMBER + j, new JLabel(dataPointsInSample.get(j).getLabel()));
                IncludeExcludeButton decisionButton = new IncludeExcludeButton();
             
                allIncludeExcludeButton.put(i * LARGENUMBER + j, decisionButton);
            }
        }
        
    }
    
    public JPanel producePanel(JScrollPane destination){
        JPanel contentPanel = new JPanel();
        contentPanel.setMaximumSize(destination.getPreferredSize());
        
        GroupLayout layout = new GroupLayout(contentPanel);
        contentPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        GroupLayout.SequentialGroup horizontalGroups = layout.createSequentialGroup();
        GroupLayout.ParallelGroup outerVerticalGroup  = layout.createParallelGroup(); //One level outer for separators
        GroupLayout.SequentialGroup verticalGroups = layout.createSequentialGroup();
        
        
        //Create horizontal group
        for (int i = 0; i < allSamples.size(); i++) {
            ArrayList<qPCRDataPoint> dataPointsInSample = allSamples.get(i).getDilutions();
            GroupLayout.ParallelGroup thisVerticalSampleGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
            
            thisVerticalSampleGroup.addGroup(layout.createSequentialGroup()
                    .addComponent(allSampleLabels.get(i))
                    .addComponent(allSampleTextFields.get(i)));
            
            thisVerticalSampleGroup.addComponent(r2Labels.get(i));
            thisVerticalSampleGroup.addComponent(effLabels.get(i));
            for (int j = 0; j < dataPointsInSample.size(); j++) {
                thisVerticalSampleGroup.addGroup(layout.createSequentialGroup()
                        .addComponent(allDilutionLabels.get(i * LARGENUMBER + j))
                        .addComponent(allIncludeExcludeButton.get(i * LARGENUMBER + j)));
            }
            horizontalGroups.addGroup(thisVerticalSampleGroup);
            if (i != allSamples.size() - 1) {
                horizontalGroups.addComponent(separators.get(i));
            }
        }
        
        //Create vertical group
        //First just for first row
        GroupLayout.ParallelGroup allFirstRow = layout.createParallelGroup(GroupLayout.Alignment.CENTER,false);
        for (int i = 0; i < allSamples.size(); i++){
            JLabel sampleLabel = allSampleLabels.get(i);
            FocusTextField thisField = allSampleTextFields.get(i);
            allFirstRow.addComponent(sampleLabel)
                    .addComponent(thisField);
            if (i != (allSamples.size()-1) ){
                outerVerticalGroup.addComponent(separators.get(i));
            }
        
        }
        verticalGroups.addGroup(allFirstRow);
        
        GroupLayout.ParallelGroup r2Rows = layout.createParallelGroup(GroupLayout.Alignment.LEADING, true);
        GroupLayout.ParallelGroup effRows = layout.createParallelGroup(GroupLayout.Alignment.LEADING, true);
        for (int i = 0; i < allSamples.size(); i++){
            r2Rows.addComponent(r2Labels.get(i));
            effRows.addComponent(effLabels.get(i));
        }
        verticalGroups.addGroup(r2Rows);
        verticalGroups.addGroup(effRows);
        GroupLayout.SequentialGroup dilutionGroups = layout.createSequentialGroup();
        //Now for all other possible rows 
        for (int j = 0; j < LARGENUMBER; j++ ){
            GroupLayout.ParallelGroup allThisRow = layout.createParallelGroup(GroupLayout.Alignment.LEADING, true);
            boolean foundOneThisRow = false;
            for (int i = 0; i < allSamples.size(); i++){
                if (allDilutionLabels.containsKey(i * LARGENUMBER + j)){
                    if (allIncludeExcludeButton.containsKey(i*LARGENUMBER + j)){
                        JLabel dilutionLabel = allDilutionLabels.get(i*LARGENUMBER + j);
                      
                        allThisRow.addComponent(dilutionLabel);
                        IncludeExcludeButton thisButton = allIncludeExcludeButton.get(i*LARGENUMBER + j);
                     
                        allThisRow.addComponent(thisButton);
                        foundOneThisRow = true;
                    } else {
                        System.out.println("The dilution label exists but the button doesn't");
                        System.exit(0);
                    }
                } else if (allIncludeExcludeButton.containsKey(i*LARGENUMBER + j)) {
                    System.out.println("The button exists but the dilution label doesn't");
                        System.exit(0);
                } 
            }
            if (foundOneThisRow){
                verticalGroups.addGroup(allThisRow);
            }
      
        }
        
     
        
        outerVerticalGroup.addGroup(verticalGroups);

        layout.setHorizontalGroup(horizontalGroups);
        layout.setVerticalGroup(outerVerticalGroup);
                
        JFrame justTest = new JFrame();
        justTest.getContentPane().add(contentPanel);
        justTest.pack();
        justTest.setVisible(true);
        return contentPanel;

    }
    
  
    
    static class FocusTextField extends JTextField {
        {
            addFocusListener(new FocusListener() {

                @Override
                public void focusGained(FocusEvent e) {
                    FocusTextField.this.select(0, getText().length());
                }

                @Override
                public void focusLost(FocusEvent e) {
                    FocusTextField.this.select(0, 0);
                }
            });
        }
    }
    
   
}
