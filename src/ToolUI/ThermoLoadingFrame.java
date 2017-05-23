/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ToolUI;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
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
    public final int DEFAULT_AMPLITUDE_THRESHOLD = 2000;
    public final String SAMPLE_HEADER = "Sample";
    public final String WELL_HEADER = "Well";
    public final String SIGNAL_FILE_TOKEN = "Quantification Amplification Results";
    public final String SAMPLE_FILE_TOKEN = "Quantification Summary_0.csv";
    public final String MELTCURVE_SIGNAL_TOKEN = "Melt Curve Derivative Results";
    public final String MELTCURVE_SAMPLE_TOKEN = "Melt Curve Summary_0.csv";
    public final String ROOT_SPLITTER = " - ";
    public final int ORGANIZE = 2;
    public final int PLOTWIDTH=500;
    public final int PLOTHEIGHT=600;
    public final int MINY = -500;
    public final int MAXY = 30000;
    public final int MINY_MELT = -500;
    public final int MAXY_MELT = MAXY/10;
    public final int NUMBACK = 3; //How many replicates back to look at when smoothing derivatives (for inflection point TTR)
    public final String[] AXISLABELS = {"Minutes", "RFU"};
    public final String[] MELTAXISLABELS = {"Temperature (C)", "RFU"};
    public final Color[] colors = {Color.BLUE,Color.GREEN,Color.CYAN,Color.GRAY, Color.BLACK,Color.ORANGE,Color.MAGENTA};
    
    public Font official_font;
    public boolean useMidpointMethod;
    public final int TTRLINELENGTH = 10;
    public ArrayList<SignalSampleCombo> allCombos;
    public LabelManager myManager;
    /**
     * Creates new form loadingFrame
     */
    public ThermoLoadingFrame(File folder, Font officialFont) {
        
        
        initComponents();
        automateIndividuallyButton.setSelected(true);
        minTextField.setText(Integer.toString(MINY));
        maxTextField.setText(Integer.toString(MAXY));
        minTextField.setEditable(false);
        maxTextField.setEditable(false);
        this.setResizable(true);
        official_font = officialFont;
        DiAssessDataAnalysisToolUI.applyFont(this, officialFont);
        labelTTRButton.setSelected(true);
        midpointButton.setSelected(true);
        amplitudeThresholdField.setEditable(true);
        amplitudeThresholdField.setText(Integer.toString(DEFAULT_AMPLITUDE_THRESHOLD));
        useMidpointMethod = true;
        
        
        this.getRootPane().setDefaultButton(waitContinueButton);
      
        myTextField.setEditable(false);
        myTextField.setFont(officialFont);
        currentText = "";
        passedFolder = folder;
        allCombos = new ArrayList<SignalSampleCombo>();
        int success = loadFiles(folder);
        if(success == FILES_FOUND){
            waitContinueButton.setText("Continue!");
            waitContinueButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    int amplitudeThreshold = DEFAULT_AMPLITUDE_THRESHOLD;
                    if(!midpointButton.isSelected()){
                        useMidpointMethod = false;
                    } else {
                         try{
                            amplitudeThreshold = Integer.parseInt(amplitudeThresholdField.getText());
                         } catch (NumberFormatException error){
                             JOptionPane.showMessageDialog(null, "Please make sure amplitude threshold is an integer");
                             return;
                         }
                    }
                    
                    double secondsPerCycle;
                    try{
                        secondsPerCycle = Double.parseDouble(secondsPerCycleField.getText());
                    } catch (NumberFormatException error){
                        JOptionPane.showMessageDialog(null, "Please make sure seconds per cycle is a number");
                        return;
                    }
                    
                    double minY_option = 0;
                    double maxY_option = 0;
                    if (fixedBoundsButton.isSelected()){
                        try{
                            minY_option = Double.parseDouble(minTextField.getText());
                            maxY_option = Double.parseDouble(maxTextField.getText());
                        } catch(NumberFormatException error){
                            JOptionPane.showMessageDialog(null, "Please make sure min and max y values are valid numbers");
                        return;
                        }
                    }
                    boolean autoYAxis = automateIndividuallyButton.isSelected();
                    
                    
                    int plotsPerRow;
                    try {
                        plotsPerRow = Integer.parseInt(jTextField1.getText());
                        
                    } catch (NumberFormatException error ) {
                        JOptionPane.showMessageDialog(null, "Please make sure number of plots per row is an integer");
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

                        boolean successful = makeAndSavePlots(allCombos.get(i), useMidpointMethod, amplitudeThreshold, secondsPerCycle, 
                                labelTTR, plotsPerRow, autoYAxis, minY_option, maxY_option);
                        if (!successful) {
                            addText("Something went wrong. Ask Kamin to investigate");
                        } else {
                            addText("Plots were successful. You should see a window of plots with buttons to save");
                        }
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
        ArrayList<File> meltCurveSignalFiles = new ArrayList<File>();
        ArrayList<File> meltCurveSampleFiles = new ArrayList<File>();
        for (File aFolder : allSubFolders){
            String fileName = aFolder.getName();
            
            if (fileName.contains(MELTCURVE_SAMPLE_TOKEN)){
                File meltCurve_sample = aFolder;
                addText("Found " + meltCurve_sample.getName());
                meltCurveSampleFiles.add(meltCurve_sample);
            }
            if (fileName.contains(MELTCURVE_SIGNAL_TOKEN)){
                File meltCurve_signal = aFolder;
                addText("Found " + meltCurve_signal.getName());
                meltCurveSignalFiles.add(meltCurve_signal);
            }
            
            
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
        
        
        
        
        
        
        //Loop through all melt curve signal Files and see if there is a matching file;
        while(!meltCurveSignalFiles.isEmpty()){
            File aSignalFile = meltCurveSignalFiles.remove(0);
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
                for (int i = 0; i < meltCurveSampleFiles.size(); i++){
                    foundSample = false;
                    File aSampleFile = meltCurveSampleFiles.get(i);
                    String sampleFileName = aSampleFile.getName();
                    String[] sampleSplitName = sampleFileName.split(ROOT_SPLITTER);
                    if (sampleSplitName.length > 0 ){
                        String sampleRootName = sampleSplitName[0];
                        if(sampleRootName.equals(signalRootName)){
                            addText("Matched two files: ");
                            addText("For " + signalRootName + ", found:");
                            addText("\tRFU Signal file: " + signalFileName);
                            addText("\tSample name file: " + sampleFileName);
                            SignalSampleCombo thisCombo = new SignalSampleCombo(aSignalFile, aSampleFile,true);
                            allCombos.add(thisCombo);
                            foundSample = true;
                            break;
                        }
                    } else {
                        addText("There is a melt curve sample file without a root name: " + sampleFileName + " removing from list and skipping.");
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
    public boolean makeAndSavePlots(SignalSampleCombo thisCombo, boolean isMidpoint, int amplitudeThreshold,
            double secondsPerCycle, boolean labelTTR, int plotsPerRow, boolean autoYAxis, double minY, double maxY){
        ArrayList<TTRTuple> TTRData = new ArrayList<TTRTuple>();
        int OFFSET = -1; //Offset for empty spaces and "Cycle" column
        File signalFile = thisCombo.getSignal();
        File sampleFile = thisCombo.getSample();
        boolean isMeltCurve = thisCombo.isMelt();
        ThermoReplicate meltCurveTemperatures = null;
        if (isMeltCurve) {
            meltCurveTemperatures = new ThermoReplicate("Temperature");
        }
        ArrayList<String[]> signalFileData = readFile(signalFile); 
        ArrayList<String[]> sampleFileData = null;
        boolean hasSamples = false;
        if (sampleFile != null){
            hasSamples = true;
            sampleFileData = readFile(sampleFile);
        } else {
            JOptionPane.showMessageDialog(null," Error. There is no sample information file \"Summary_0.csv\"");
        }
        System.out.println("Signal file: " + signalFile.getName());
        System.out.println("Sample file: " + sampleFile.getName());
        ArrayList<ThermoReplicate> allUnlabeledReplicates = new ArrayList<ThermoReplicate>();
        
        
        int temperatureCol = -1; //This will keep track of which column the temperature data is found for melt curves
        for (int i = 0; i < signalFileData.size(); i++){
            String[] row = signalFileData.get(i);
            
            if (isMeltCurve && temperatureCol != -1){
                meltCurveTemperatures.addDataPoint(Double.parseDouble(row[temperatureCol]));
            }
            
            //This for loop finds the column of where temperature data is found and stores that column index in 
            //temperatureCol
            if (temperatureCol == -1){
                for (int k = 0; k < row.length; k++) {
                    if (isMeltCurve && row[k].equals("Temperature")) {
                        temperatureCol = k;
                    }
                }
            }
            
            
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
                return false;
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
        int sampleIndex = -1; //Variable to store the sample info index
        int wellIndex = -1; //Variable to store the well info index
        if(hasSamples){
            ArrayList<ThermoReplicate> labeledReplicates = new ArrayList<ThermoReplicate>();
            while(!allUnlabeledReplicates.isEmpty()){
                ThermoReplicate thisReplicate = allUnlabeledReplicates.remove(0);
                for (int i = 0; i < sampleFileData.size(); i++){
                    String[] sampleRow = sampleFileData.get(i);
                    
                    //This for loop finds the sampleIndex and wellIndex by looking for the first instance where the header is "Sample" or Well
                    for (int headerIndex = 0; headerIndex < sampleRow.length && (sampleIndex == -1 || wellIndex == -1); headerIndex++) {
                        String header = sampleRow[headerIndex];
                        
                        if (header.equalsIgnoreCase(SAMPLE_HEADER) && sampleIndex == -1) {
                            sampleIndex = headerIndex;
                            continue;
                        }
                        if (header.equalsIgnoreCase(WELL_HEADER) && wellIndex == -1){
                            wellIndex = headerIndex;
                            continue;
                        }
                    }
                   
                    if (sampleIndex == -1){
                        JOptionPane.showMessageDialog(this, "In the file " + sampleFile.getAbsolutePath() +
                                ", could not find a header called \"" + SAMPLE_HEADER + "\"");
                        return false;
                    } 
                    if (wellIndex == -1){
                        JOptionPane.showMessageDialog(this, "In the file " + sampleFile.getAbsolutePath() +
                                ", could not find a header called \"" + WELL_HEADER + "\"");
                        return false;
                    }
                    
                    String wellCoordinates = "";
                    String sampleName = "";
                    
                    
                    wellCoordinates = sampleRow[wellIndex];
                    
                    if(wellCoordinates.equalsIgnoreCase(WELL_HEADER)){
                        continue;
                    }
                   
                    if (thisReplicate.checkCoordinate(wellCoordinates)) {
                        sampleName = sampleRow[sampleIndex];
                        thisReplicate.label(sampleName);
                        labeledReplicates.add(thisReplicate);
                    } else {
                        continue;
                    }

                }
            }
            allData = organizeReplicates(labeledReplicates);
        } else {
           allData = organizeReplicates(allUnlabeledReplicates);
        }
        
       BufferedImage allPlots = null;
       BufferedImage thisRowOfPlots = null;

        for (int i = 0; i < allData.size(); i++){     
        
            ThermoSample thisSample = allData.get(i);
            String label = thisSample.getLabel();
            ArrayList<ThermoReplicate> allReplicates = thisSample.getReplicates();
            Plot2DPanel thisPanel = null;
            ArrayList<double[]> toPlot = new ArrayList<double[]>();
            
            for (int j = 0; j < allReplicates.size(); j++){
                toPlot.add(allReplicates.get(j).getSignal());
                String coordinates = allReplicates.get(j).getCoordinates();
                double TTR = 0;
                if(isMidpoint && !isMeltCurve){
                    TTR = allReplicates.get(j).getMidpointTTR(secondsPerCycle, amplitudeThreshold);
                } else if (!isMeltCurve){
                    TTR = allReplicates.get(j).getInflectionPointTTR(secondsPerCycle, 0, NUMBACK);
                }
                if (isMeltCurve){
                    TTR = meltCurveTemperatures.getSignal()[allReplicates.get(j).getPeakIndex()];
                }
                TTRData.add(new TTRTuple(label,TTR,coordinates));
            }
            BufferedImage plotImage = plotGraph(thisSample.getLabel(),toPlot,
                    true,thisPanel, allReplicates, secondsPerCycle, isMidpoint, amplitudeThreshold, 
                    labelTTR, isMeltCurve, meltCurveTemperatures, autoYAxis, minY, maxY);
            BufferedImage labeledImage = null;
            if (isMeltCurve){
                labeledImage = addText(plotImage, label + " Melt", 40, true, 0);
            } else {
                labeledImage = addText(plotImage, label, 40, true, 0);
            }
            if ( (i + 1)% plotsPerRow == 0 || i == allData.size()-1){
                if (thisRowOfPlots == null){
                    thisRowOfPlots = labeledImage;
                } else {
                    if (allPlots == null ){
                        thisRowOfPlots = joinImageHorz(thisRowOfPlots, labeledImage,0);
                        allPlots = deepCopy(thisRowOfPlots);
                        thisRowOfPlots = null;
                    } else {
                        thisRowOfPlots = joinImageHorz(thisRowOfPlots, labeledImage,0);
                        allPlots = joinImageVert(allPlots,thisRowOfPlots,0);
                        thisRowOfPlots = null;
                    }
                }
            } else {
                if (thisRowOfPlots == null){
                    thisRowOfPlots = labeledImage;
                } else {
                    thisRowOfPlots = joinImageHorz(thisRowOfPlots, labeledImage,0);
                }                
            }
            
        }
        if ( thisRowOfPlots != null ) {
           allPlots = joinImageVert(allPlots,thisRowOfPlots,0);
        }
        

        OutputFrame thisExperiment = new OutputFrame(allPlots,OutputFrame.THERMOCYCLER, TTRData, official_font, passedFolder, autoSaveButton.isSelected(), isMeltCurve);
        return true;
    }
    
    static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

   
     public static BufferedImage joinImageHorz(BufferedImage img1,BufferedImage img2, int separation) {

        //do some calculate first
        int offset  = 5;
        int wid = img1.getWidth()+img2.getWidth()+offset + separation;
        int height = Math.max(img1.getHeight(),img2.getHeight())+offset;
        //create a new buffer and draw two image into the new image
        BufferedImage newImage = new BufferedImage(wid,height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();
        //fill background
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, wid, height);
        //draw image
        g2.setColor(oldColor);
        g2.drawImage(img1, null, 0, 0);
        g2.drawImage(img2, null, img1.getWidth()+offset + separation, 0);
        g2.dispose();
        return newImage;
    }
    
    public static BufferedImage joinImageVert(BufferedImage img1, BufferedImage img2, int separation){
        //do some calculate first
        int offset  = 5;
        int height = img1.getHeight()+img2.getHeight()+offset + separation;
        int wid = Math.max(img1.getWidth(),img2.getWidth())+offset;
        //create a new buffer and draw two image into the new image
        BufferedImage newImage = new BufferedImage(wid,height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();
        //fill background
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, wid, height);
        //draw image
        g2.setColor(oldColor);
        g2.drawImage(img1, null, 0, 0);
        g2.drawImage(img2, null, 0, img1.getHeight()+offset + separation);
        g2.dispose();
        return newImage;
    }
    
    
    public BufferedImage addText(BufferedImage picture, String text, int textSize, boolean above, int distance){
        int width = picture.getWidth();
        int height = picture.getHeight();
        int textWidth = Math.floorDiv(text.length() * textSize,3);
       
        if (above) {
            height = height + distance;
        } else {
            width = width + distance;
        }
        BufferedImage newImage = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);   
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();
        g2.setPaint(Color.WHITE);
        g2.fillRect(0,0, width,height);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
        Font font = new Font("Serif", Font.PLAIN, textSize);
        g2.setFont(font);
        g2.setColor(Color.BLACK);
        if (above){
            g2.drawImage(picture,null,0,distance);
            g2.drawString(text,width/2-textWidth,textSize);  
        } else {
            g2.drawImage(picture,null,0,0);
            g2.drawString(text, width - distance, height/2-textSize);
        }
        return newImage;
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
    
  
    
    
    
 
    
    
    
    public BufferedImage plotGraph(String source, ArrayList<double[]> yValues, 
            boolean newPlot, Plot2DPanel toPlot, ArrayList<ThermoReplicate> allReplicates, 
            double secondsPerCycle, boolean isMidpoint, int amplitudeThreshold, boolean plotTTR, boolean isMelt, ThermoReplicate meltTemperatures,
            boolean autoYAxis, double minY, double maxY){
        
              
        Plot2DPanel panel = null;
        JFrame frame = null;
        if(newPlot){
            frame = new JFrame(source);
            panel = new Plot2DPanel();
            frame.setContentPane(panel);
            frame.setSize(PLOTWIDTH,PLOTHEIGHT);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        } else {
            panel = toPlot;
        }
        double maxXvalue = 0;
        double minXvalue = 100;
        for (int plotIndex = 0; plotIndex < allReplicates.size(); plotIndex++){
            ThermoReplicate plotReplicate = allReplicates.get(plotIndex);
            double [] yValueArray = plotReplicate.getSignal();
            double [] xValueArray = plotReplicate.getMinutes(secondsPerCycle);
           
            if (isMelt){
                xValueArray = meltTemperatures.getSignal();
            }
            if(plotTTR){
                int TTR = plotReplicate.getMidpointTTRIndex(amplitudeThreshold);
                if (!isMidpoint){
                    TTR = plotReplicate.getInflectionPointTTRIndex(secondsPerCycle, 0, NUMBACK);
                }
                if (isMelt){
                    TTR = plotReplicate.getPeakIndex();
                }
                double[] TTRXValues = new double[TTRLINELENGTH];
                double[] TTRYValues = new double[TTRLINELENGTH];
                double yValueAtTTR = yValueArray[TTR];
                int SCALE = 20;
                if (isMelt) {
                    for (int TTRPoint = 0; TTRPoint < TTRLINELENGTH; TTRPoint++) {
                        TTRXValues[TTRPoint] = xValueArray[TTR];
                        TTRYValues[TTRPoint] = yValueArray[TTR] - (int) Math.round(TTRLINELENGTH / 2.0) * SCALE + TTRPoint * SCALE;
                    }
                } else {
                    for (int TTRPoint = 0; TTRPoint < TTRLINELENGTH; TTRPoint++) {
                        int xPoint = (TTR - (int) Math.round(TTRLINELENGTH / 2.0) + TTRPoint);
                        if (xPoint < 1) {
                            xPoint = 0;
                        } else if (xPoint > xValueArray.length - 1) {
                            xPoint = xValueArray.length - 1;
                        }
                        TTRXValues[TTRPoint] = xValueArray[xPoint];
                        TTRYValues[TTRPoint] = yValueAtTTR;
                    }
                }
                panel.addLinePlot(source, Color.red, TTRXValues, TTRYValues);
            }
            if (maxXvalue < xValueArray[xValueArray.length-1]){
                maxXvalue = xValueArray[xValueArray.length-1];
            }
            if (minXvalue > xValueArray[0]){
                minXvalue = xValueArray[0];
            }
            panel.addLinePlot(source, colors[plotIndex%colors.length], xValueArray, yValueArray);
        }
//        for (int plotIndex = 0; plotIndex < yValues.size(); plotIndex++){
//            double[] yValueArray = yValues.get(plotIndex);
//            double[] xValueArray = new double[yValueArray.length];
//            for (int i = 0; i < yValueArray.length; i++){
//                xValueArray[i] = i;
//            }
//            
//            panel.addLinePlot(source, Color.blue, yValueArray);
//            
//        }
        panel.setFixedBounds(0, 0, Math.ceil(maxXvalue/10.0)*10);
        if (!autoYAxis) {
            if (!isMelt) {
                panel.setFixedBounds(1, minY, maxY);

            } else {
                panel.setFixedBounds(1, MINY_MELT, MAXY_MELT);
            }
        }
        if (isMelt){
            panel.setFixedBounds(0,minXvalue,Math.ceil(maxXvalue/10.0)*10);
        }
        if (isMelt){
            panel.setAxisLabels(MELTAXISLABELS);
        } else {
            panel.setAxisLabels(AXISLABELS);
        }
        panel.removePlotToolBar();
//        for (int j =)
//        double[] xAxis = new double[yAxis.length];
//        for (int i = 1; i < xAxis.length; i++){
//            xAxis[i] = i;
//        }
//        panel.addLinePlot(source,xAxis, yAxis);
        BufferedImage plotImage = new BufferedImage(PLOTWIDTH, PLOTHEIGHT,
                                    BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = plotImage.createGraphics();
        panel.paint(g2);
        if (frame != null){
            frame.dispose();
        }
        return plotImage;
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
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        AxisButtonGroup = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        myTextField = new javax.swing.JTextArea();
        cancelButton = new javax.swing.JButton();
        waitContinueButton = new javax.swing.JButton();
        inflectionButton = new javax.swing.JRadioButton();
        midpointButton = new javax.swing.JRadioButton();
        secondsPerCycleField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        labelTTRButton = new javax.swing.JToggleButton();
        jTextField1 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        autoSaveButton = new javax.swing.JToggleButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        automateIndividuallyButton = new javax.swing.JRadioButton();
        fixedBoundsButton = new javax.swing.JRadioButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        minTextField = new javax.swing.JTextField();
        maxTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        amplitudeThresholdField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        myTextField.setColumns(20);
        myTextField.setFont(new java.awt.Font("Monospaced", 0, 24)); // NOI18N
        myTextField.setRows(5);
        jScrollPane1.setViewportView(myTextField);

        cancelButton.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        waitContinueButton.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        waitContinueButton.setText("(Wait...)");

        ttrMethodButtonGroup.add(inflectionButton);
        inflectionButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        inflectionButton.setText("Inflection Point");
        inflectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inflectionButtonActionPerformed(evt);
            }
        });

        ttrMethodButtonGroup.add(midpointButton);
        midpointButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        midpointButton.setText("Midpoint of Linear Phase");
        midpointButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                midpointButtonActionPerformed(evt);
            }
        });

        secondsPerCycleField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        secondsPerCycleField.setText("40");
        secondsPerCycleField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                secondsPerCycleFieldActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setText("Seconds per cycle:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Label TTR on Plot:");

        labelTTRButton.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        labelTTRButton.setText("ON");
        labelTTRButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                labelTTRButtonActionPerformed(evt);
            }
        });

        jTextField1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jTextField1.setText("4");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Plots Per Row:");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Automatically save tables/plots:");

        autoSaveButton.setText("OFF");
        autoSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoSaveButtonActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("TTR Method:");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setText("Y-Axis Labels:");

        AxisButtonGroup.add(automateIndividuallyButton);
        automateIndividuallyButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        automateIndividuallyButton.setText("Automate individually");
        automateIndividuallyButton.setToolTipText("Each plot will have axes that are optimized for that plot in particular.");
        automateIndividuallyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                automateIndividuallyButtonActionPerformed(evt);
            }
        });

        AxisButtonGroup.add(fixedBoundsButton);
        fixedBoundsButton.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        fixedBoundsButton.setText("Set fixed bounds:");
        fixedBoundsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fixedBoundsButtonActionPerformed(evt);
            }
        });

        minTextField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        minTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minTextFieldActionPerformed(evt);
            }
        });

        maxTextField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("MIN");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("MAX");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("Amplitude Threshold:");

        amplitudeThresholdField.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        amplitudeThresholdField.setText("4000");
        amplitudeThresholdField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                amplitudeThresholdFieldActionPerformed(evt);
            }
        });

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ToolUI/icons/smallQuestionMark.png"))); // NOI18N
        jLabel10.setToolTipText("A difference in ampiltude between baseline and midpoint value smaller than this threshold will result in TTR being the maximum possible for each plot.");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(waitContinueButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelTTRButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(autoSaveButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(44, 44, 44))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(773, 773, 773)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(amplitudeThresholdField, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 591, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator3)
                            .addComponent(jSeparator1)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 611, Short.MAX_VALUE))))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(597, 597, 597)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(inflectionButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(midpointButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(automateIndividuallyButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(fixedBoundsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(82, 82, 82)
                                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(103, 103, 103)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(secondsPerCycleField)
                            .addComponent(minTextField)
                            .addComponent(maxTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE))
                        .addGap(44, 44, 44))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {amplitudeThresholdField, jTextField1, maxTextField, minTextField, secondsPerCycleField});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel7});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(secondsPerCycleField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(8, 8, 8)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(13, 13, 13)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(midpointButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(78, 78, 78)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(amplitudeThresholdField, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                        .addComponent(inflectionButton)
                        .addGap(18, 18, 18)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(automateIndividuallyButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fixedBoundsButton))
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(minTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(maxTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelTTRButton, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(autoSaveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cancelButton)
                            .addComponent(waitContinueButton)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {amplitudeThresholdField, jTextField1, maxTextField, minTextField, secondsPerCycleField});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jSeparator1, jSeparator2, jSeparator3});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        //Cancel means close this window but also pause whatever is going on above it
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void midpointButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_midpointButtonActionPerformed
        // TODO add your handling code here:

        amplitudeThresholdField.setText(Integer.toString(DEFAULT_AMPLITUDE_THRESHOLD));
        amplitudeThresholdField.setEditable(true);

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

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void autoSaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoSaveButtonActionPerformed
        // TODO add your handling code here:
        if (autoSaveButton.isSelected()){
            autoSaveButton.setText("ON");
        } else {
            autoSaveButton.setText("OFF");
        }
    }//GEN-LAST:event_autoSaveButtonActionPerformed

    private void automateIndividuallyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_automateIndividuallyButtonActionPerformed
        // TODO add your handling code here:
        minTextField.setEditable(false);
        maxTextField.setEditable(false);
        minTextField.setText(Integer.toString(MINY));
        maxTextField.setText(Integer.toString(MAXY));
        
        
    }//GEN-LAST:event_automateIndividuallyButtonActionPerformed

    private void fixedBoundsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fixedBoundsButtonActionPerformed
        // TODO add your handling code here:
        minTextField.setEditable(true);
        maxTextField.setEditable(true);
    }//GEN-LAST:event_fixedBoundsButtonActionPerformed

    private void minTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_minTextFieldActionPerformed

    private void inflectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inflectionButtonActionPerformed
        // TODO add your handling code here:
        amplitudeThresholdField.setEditable(false);
    }//GEN-LAST:event_inflectionButtonActionPerformed

    private void amplitudeThresholdFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_amplitudeThresholdFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_amplitudeThresholdFieldActionPerformed

    /**
     * @param args the command line arguments
     */
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup AxisButtonGroup;
    private javax.swing.JTextField amplitudeThresholdField;
    private javax.swing.JToggleButton autoSaveButton;
    private javax.swing.JRadioButton automateIndividuallyButton;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton cancelButton;
    private javax.swing.JRadioButton fixedBoundsButton;
    private javax.swing.JRadioButton inflectionButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollBar jScrollBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JToggleButton labelTTRButton;
    private javax.swing.JTextField maxTextField;
    private javax.swing.JRadioButton midpointButton;
    private javax.swing.JTextField minTextField;
    private javax.swing.JTextArea myTextField;
    private javax.swing.JTextField secondsPerCycleField;
    private javax.swing.ButtonGroup ttrMethodButtonGroup;
    private javax.swing.JButton waitContinueButton;
    // End of variables declaration//GEN-END:variables
}


