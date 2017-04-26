/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ToolUI;
import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.math.plot.*;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author kamin
 */
public class ThermoLoadingFrame extends javax.swing.JFrame implements FileFilter {
    public String currentText;
    public File passedFolder;
    public int organize;
    public final int NO_FILES_FOUND = 0;
    public final int FILES_FOUND = 1;
    public final int EQUAL_NUMBER = 0;
    public final int STITCH_TOGETHER = 1;
    public final String SIGNAL_FILE_TOKEN = "Quantification Amplification Results_SYBR.csv";
    public final String SAMPLE_FILE_TOKEN = "Quantification Summary_0.csv";
    public final String ROOT_SPLITTER = " - ";
    public final int ORGANIZE = 2;

    public boolean useMidpointMethod;
    public ArrayList<SignalSampleCombo> allCombos;
    public LabelManager myManager;
    /**
     * Creates new form loadingFrame
     */
    public ThermoLoadingFrame(File folder) {
        
        setUndecorated(true);
        initComponents();
        labelTTRButton.setSelected(true);
        midpointButton.setSelected(true);
        useMidpointMethod = true;
        this.getRootPane().setDefaultButton(waitContinueButton);
        myTextField.setEditable(false);
        currentText = "";
        passedFolder = folder;
        allCombos = new ArrayList<SignalSampleCombo>();
        int success = loadFiles(folder);
        
        if(success == FILES_FOUND){
            waitContinueButton.setText("Continue!");
            waitContinueButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    if(!midpointButton.isSelected()){
                        useMidpointMethod = false;
                    } 
                    double secondsPerCycle;
                    try{
                        secondsPerCycle = Double.parseDouble(secondsPerCycleField.getText());
                    } catch (NumberFormatException error){
                        JOptionPane.showMessageDialog(null, "Please make sure seconds per cycle is a number");
                        return;
                    }
                    boolean labelTTR = false;
                    if(labelTTRButton.isSelected()){
                        labelTTR = true;
                    }
                    addText("Seconds per cycle: " + Double.toString(secondsPerCycle));
                    if (useMidpointMethod) {
                        addText("We will use midpoint method to call TTR");
                    } else {
                        addText("We will use inflection point to call TTR");
                    }
                    if (labelTTR) {
                        addText("We will label TTR on plots.");
                    } else {
                        addText("We will not label TTR on plots.");
                    }
                    for (int i = 0; i < allCombos.size(); i++) {

                        makeAndSavePlots(allCombos.get(i), useMidpointMethod, secondsPerCycle, labelTTR);

                    }
                }
            });
            
        } else {
            waitContinueButton.setText("Select another folder.");
            waitContinueButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    dispose();
                }
            });
        }
        
    }
    
    
    //Loads files in folder that match the following patterns:
    //Signal files: - "<SOME STRING>_- Quantification Amplification Results_SYBR.csv"
    //
    //returns true if successful, false if there is an error
    public int loadFiles(File folder)
    {
        addText("Okay, looking for all folders inside: " + folder.getAbsolutePath());
        File[] allSubFolders = folder.listFiles(this);
        
        addText("Found " + allSubFolders.length + " files. ");
        if (allSubFolders.length == 0){
            JOptionPane.showMessageDialog(this,"Error, the folder you selected is empty.");
            return NO_FILES_FOUND;
        }
       
        ArrayList<File> sampleFiles = new ArrayList<File>();
        ArrayList<File> signalFiles = new ArrayList<File>();
        
        for (File aFolder : allSubFolders){
            String fileName = aFolder.getName();
            
            if(fileName.contains(SIGNAL_FILE_TOKEN)){
                File signalFile = aFolder;
                addText("Found " + signalFile.getName());
                signalFiles.add(signalFile);
            }
            if(fileName.contains(SAMPLE_FILE_TOKEN)){
                File sampleFile = aFolder;
                addText("Found " + sampleFile.getName());
                sampleFiles.add(sampleFile);
            }
        } 
        
        boolean sampleWarningGiven = false; //True if user was warned that there is no sample file
        if(signalFiles.size()<1){
            JOptionPane.showMessageDialog(this,"Error, we were not able to find any files containing the phrase \"" + SIGNAL_FILE_TOKEN + "\". "
                    + " Please check your files and revert them to default nomenclatures.");
            return NO_FILES_FOUND;
        }
        
        if (sampleFiles.size()<1){
            addText("We were unable to find any files containing the phrase \"" + SAMPLE_FILE_TOKEN + "\". \nWe will assume column headers are the correct sample names.");
            sampleWarningGiven = true;
        }
        //Loop through all sample Files and see if there is a matching file;
        while(!signalFiles.isEmpty()){
            File aSignalFile = signalFiles.remove(0);
            String signalFileName = aSignalFile.getName();
            String[] signalSplitName = signalFileName.split(ROOT_SPLITTER);
            String signalRootName = null;
            if(signalSplitName.length > 0){
                signalRootName = signalSplitName[0];
            } else {
                addText("Could not get root name for " + signalFileName + ". There is no instance of \"" + ROOT_SPLITTER + "\".\n Did someone rename this file? Skipping...");
                continue;
            }
            boolean foundSample = false;

            if (!sampleWarningGiven){
                for (int i = 0; i < sampleFiles.size(); i++){
                    foundSample = false;
                    File aSampleFile = sampleFiles.get(i);
                    String sampleFileName = aSampleFile.getName();
                    String[] sampleSplitName = sampleFileName.split(ROOT_SPLITTER);
                    if (sampleSplitName.length > 0 ){
                        String sampleRootName = sampleSplitName[0];
                        if(sampleRootName.equals(signalRootName)){
                            addText("Matched two files: ");
                            addText("For " + signalRootName + ", found:");
                            addText("\tRFU Signal file: " + signalFileName);
                            addText("\tSample name file: " + sampleFileName);
                            SignalSampleCombo thisCombo = new SignalSampleCombo(aSignalFile, aSampleFile);
                            allCombos.add(thisCombo);
                            foundSample = true;
                            break;
                        }
                    } else {
                        addText("There is a sample file without a root name: " + sampleFileName + " removing from list and skipping.");
                        sampleFiles.remove(i);                        
                    }
                } 
            }
            
            if(!foundSample){
                addText("Unable to find matching file for: " + signalFileName + ".\n We will treat column headers as sample names.");
                
                allCombos.add(new SignalSampleCombo(aSignalFile));
            }
        }
        return FILES_FOUND;
    }
        
    
    //
    public void makeAndSavePlots(SignalSampleCombo thisCombo, boolean isMidpoint, double secondsPerCycle, boolean labelTTR){
        int OFFSET = -1; //Offset for empty spaces and "Cycle" column
        File signalFile = thisCombo.getSignal();
        File sampleFile = thisCombo.getSample();
        ArrayList<String[]> signalFileData = readFile(signalFile); 
        ArrayList<String[]> sampleFileData = null;
        boolean hasSamples = false;
        if (sampleFile != null){
            hasSamples = true;
            sampleFileData = readFile(sampleFile);
        }
        System.out.println("Signal file: " + signalFile.getName());
        System.out.println("Sample file: " + sampleFile.getName());
        ArrayList<ThermoReplicate> allUnlabeledReplicates = new ArrayList<ThermoReplicate>();
        for (int i = 0; i < signalFileData.size(); i++){
            String[] row = signalFileData.get(i);
            int offsetAttempt = 0;
            while (OFFSET < 0 && offsetAttempt < row.length){
                System.out.println(row[offsetAttempt]);
                if(!row[offsetAttempt].equals("") && !row[offsetAttempt].equals("Cycle")){
                    OFFSET = offsetAttempt;
                    break;
                } else {
                    offsetAttempt++;
                }
            }
            if (OFFSET < 0){
                JOptionPane.showMessageDialog(this,"Error, nothing found in the signal file " + signalFile.getName());
                return;
            }
            for (int j = OFFSET; j < row.length; j++){
                
                if( i == 0 ){
                    allUnlabeledReplicates.add(new ThermoReplicate(row[j]));
                } else {
                    allUnlabeledReplicates.get(j-OFFSET).addDataPoint(Double.parseDouble(row[j]));
                }
            }            
        }
        ArrayList<ThermoSample> allData = new ArrayList<ThermoSample>();

        if(hasSamples){
            ArrayList<ThermoReplicate> labeledReplicates = new ArrayList<ThermoReplicate>();
            while(!allUnlabeledReplicates.isEmpty()){
                ThermoReplicate thisReplicate = allUnlabeledReplicates.remove(0);
                for (int i = 0; i < sampleFileData.size(); i++){
                    String[] sampleRow = sampleFileData.get(i);
                    String wellCoordinates = "";
                    String sampleName = "";
                    int j = 0;
                    while(wellCoordinates.equals("")){
                        wellCoordinates = sampleRow[j];
                        j++;
                    }
                    if (thisReplicate.checkCoordinate(wellCoordinates)) {
                        j = sampleRow.length - 1;
                        boolean foundSample = false;
                        while (!foundSample) {
                            if (j < 0) {
                                break;
                            }
                            sampleName = sampleRow[j];
                            try {
                                Double.parseDouble(sampleName);
                                j--;
                                continue;
                            } catch (NumberFormatException notGood) {
                                if (!sampleName.equals("NaN")) {
                                    foundSample = true;
                                    break;
                                }
                                j--;
                                continue;
                            }
                        }
                        if(foundSample){
                            thisReplicate.label(sampleName);
                            labeledReplicates.add(thisReplicate);
                            break;
                        } else {
                            addText("WARNING: For " + sampleFile.getName() + ", "
                                    + "could not find a sample name for " + wellCoordinates + ". Skipping...");
                            labeledReplicates.add(thisReplicate);
                            break;
                        }
                    }
                }
            }
            allData = organizeReplicates(labeledReplicates);
        } else {
           allData = organizeReplicates(allUnlabeledReplicates);
        }
        
        for (int i = 0; i < allData.size(); i++){     
            ThermoSample thisSample = allData.get(i);
            ArrayList<ThermoReplicate> allReplicates = thisSample.getReplicates();
            Plot2DPanel thisPanel = null;
            ArrayList<double[]> toPlot = new ArrayList<double[]>();
            
            for (int j = 0; j < allReplicates.size(); j++){
                toPlot.add(allReplicates.get(j).getSignal());
                int TTR = 0;
                if(isMidpoint){
                    TTR = allReplicates.get(j).getMidpointTTR(secondsPerCycle);
                }
            }
            plotGraph(thisSample.getLabel(),toPlot,true,thisPanel);
        }
        
        
    }
    
    public ArrayList<ThermoSample> organizeReplicates(ArrayList<ThermoReplicate> theseReplicates) {
        Collections.sort(theseReplicates);
        ArrayList<ThermoSample> allData = new ArrayList<ThermoSample>();
        int i = 0;
        while (i < theseReplicates.size()) {
            ThermoSample newSample = new ThermoSample(theseReplicates.get(i).getLabel());
            allData.add(newSample);
            newSample.addReplicate(theseReplicates.get(i));
            int j = i + 1;
            while (j < theseReplicates.size() && theseReplicates.get(i).getLabel().equalsIgnoreCase(theseReplicates.get(j).getLabel())) {
                newSample.addReplicate(theseReplicates.get(j));
                j++;
            }
            i = j;
        }
        return allData;
    }
    
    
    
    
    
    public ArrayList<String[]> readFile(File thisFile){
        ArrayList<String[]> data = new ArrayList<String[]>(); 
        try {
            Scanner myScanner = new Scanner(thisFile);
            while(myScanner.hasNextLine()){
                String thisLine = myScanner.nextLine();
                String[] lineSplit = thisLine.split(",");
                data.add(lineSplit);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ThermoLoadingFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }
       
    //Returns the largest value in an array
    public double findMax(double[] array){
        double max = 0;
        for (int i = 0; i < array.length; i++){
            if (array[i] > max){
                max = array[i];
            }
        }
        return max;
    }
    
  
    
    
    
 
    
    
    
    public Plot2DPanel plotGraph(String source, ArrayList<double[]> yValues, boolean newPlot, Plot2DPanel toPlot){
        Plot2DPanel panel = null;
        if(newPlot){
            JFrame frame = new JFrame(source);
            panel = new Plot2DPanel();
            frame.setContentPane(panel);
            frame.setSize(500,600);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        } else {
            panel = toPlot;
        }
        for (int plotIndex = 0; plotIndex < yValues.size(); plotIndex++){
            double[] yValueArray = yValues.get(plotIndex);
            double[] xValueArray = new double[yValueArray.length];
            for (int i = 0; i < yValueArray.length; i++){
                xValueArray[i] = i;
            }
            panel.addLinePlot(source, xValueArray, yValueArray);
            
        }
//        for (int j =)
//        double[] xAxis = new double[yAxis.length];
//        for (int i = 1; i < xAxis.length; i++){
//            xAxis[i] = i;
//        }
//        panel.addLinePlot(source,xAxis, yAxis);
//        
        return panel;
    }
    
   
   
    
    
    
    public int verifyNumFiles(int[] numbers){
        if(numbers == null)
            JOptionPane.showMessageDialog(this, "Numbers is null in verifyNumFile");
        int firstNumber = numbers[0];
        for (int num : numbers){
            if (num != firstNumber){
                Object[] myOptions = {"Organize by name with padding", "Stitch together"};
                Object selectedValue = JOptionPane.showInputDialog(this, "All of your subfolders do not have the same number of images. \nWe can either organize images by their name and include padding images, or we can just stitch them together in order. What would you like to do?"
                        ,"Input",JOptionPane.INFORMATION_MESSAGE,null,myOptions,myOptions[0]);
                if (selectedValue.equals(myOptions[0])){
                    return ORGANIZE;
                }
                else {
                    return STITCH_TOGETHER;
                }
            }
        }
        return EQUAL_NUMBER;
    }
    
    public boolean accept(File fileName){
        return fileName.isFile();
    }
    
    public void addText(String newText){
        if (currentText.equals("")){
            currentText = newText;
            myTextField.setText(currentText);
        }
        else {
            currentText = currentText + "\n" + newText;
            myTextField.setText(currentText);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollBar1 = new javax.swing.JScrollBar();
        ttrMethodButtonGroup = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        myTextField = new javax.swing.JTextArea();
        cancelButton = new javax.swing.JButton();
        waitContinueButton = new javax.swing.JButton();
        inflectionButton = new javax.swing.JRadioButton();
        midpointButton = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        secondsPerCycleField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        labelTTRButton = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        myTextField.setColumns(20);
        myTextField.setRows(5);
        jScrollPane1.setViewportView(myTextField);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        waitContinueButton.setText("(Wait...)");

        ttrMethodButtonGroup.add(inflectionButton);
        inflectionButton.setText("Inflection Point");

        ttrMethodButtonGroup.add(midpointButton);
        midpointButton.setText("Midpoint of Linear Phase");
        midpointButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                midpointButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("TTR Method:");

        secondsPerCycleField.setText("40");
        secondsPerCycleField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                secondsPerCycleFieldActionPerformed(evt);
            }
        });

        jLabel2.setText("Seconds per cycle:");

        jLabel3.setText("Label TTR on Plot:");

        labelTTRButton.setText("ON");
        labelTTRButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                labelTTRButtonActionPerformed(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(waitContinueButton, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(secondsPerCycleField, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelTTRButton, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(52, 52, 52)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(midpointButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(inflectionButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(inflectionButton)
                        .addComponent(jLabel1)
                        .addComponent(midpointButton)
                        .addComponent(secondsPerCycleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(labelTTRButton)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(waitContinueButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        //Cancel means close this window but also pause whatever is going on above it
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void midpointButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_midpointButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_midpointButtonActionPerformed

    private void secondsPerCycleFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_secondsPerCycleFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_secondsPerCycleFieldActionPerformed

    private void labelTTRButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_labelTTRButtonActionPerformed
        // TODO add your handling code here:
        if(labelTTRButton.isSelected()){
            labelTTRButton.setText("ON");
        } else {
            labelTTRButton.setText("OFF");
        }
    }//GEN-LAST:event_labelTTRButtonActionPerformed

    /**
     * @param args the command line arguments
     */
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton cancelButton;
    private javax.swing.JRadioButton inflectionButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollBar jScrollBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JToggleButton labelTTRButton;
    private javax.swing.JRadioButton midpointButton;
    private javax.swing.JTextArea myTextField;
    private javax.swing.JTextField secondsPerCycleField;
    private javax.swing.ButtonGroup ttrMethodButtonGroup;
    private javax.swing.JButton waitContinueButton;
    // End of variables declaration//GEN-END:variables
}

