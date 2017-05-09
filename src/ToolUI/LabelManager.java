/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ToolUI;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author kamin
 */
public class LabelManager {
    private int numberOfOpenLabelingFrames;
    public LabeledImage officialImage;
    public Rack[] allRacks;
    public LabeledSample[] allSamples;
    private File outputFolder;
    public ArrayList<String> allLabelsSoFar;
    
    public LabelManager(File outputLocation){
        allLabelsSoFar = new ArrayList<String>();
        numberOfOpenLabelingFrames = 0;
        outputFolder = outputLocation;
        officialImage = new LabeledImage();
        allRacks = new Rack[0];
    }
    
    public int getNumFrames(){
        return numberOfOpenLabelingFrames;
    }
    
    public int incrementNumFrames(){
        numberOfOpenLabelingFrames++;
        return numberOfOpenLabelingFrames;
    }
    
    public int decrementNumFrames(){
        numberOfOpenLabelingFrames--;
        return numberOfOpenLabelingFrames;
    }
    
    public void addSample(LabeledSample thisSample){
        officialImage.addSample(thisSample);
    }
    
    public void addTime(LabeledTime thisTime){
        if (allRacks.length == 0){
            allRacks = new Rack[1];
            allRacks[0] = new Rack();
            allRacks[0].addTime(thisTime);
            System.out.println("Making new rack " + thisTime.getRack() + " from time "
                    + thisTime.getTime());
        } else {
            Rack[] newRacks = new Rack[allRacks.length+1];
            for (int i = 0; i < allRacks.length; i++){
                newRacks[i] = allRacks[i];
                if(allRacks[i].doesBelong(thisTime)){
                    allRacks[i].addTime(thisTime);
                    System.out.println("Adding time " + thisTime.getTime() + " of rack "
                            + thisTime.getRack());
                    System.out.println(" To rack " + allRacks[i].getRackNumber());
                    LabeledTime[] otherTimesInRack = allRacks[i].getTimes();
                    for (int k = 0; k < otherTimesInRack.length; k++) {
                        System.out.println(" The other time in this rack is " + otherTimesInRack[k].getTime());

                    }
                    return;
                }
            }
            System.out.println("Couldn't find a matching time. Making a new rack "
                    + "for rack " + thisTime.getRack()  + " and time " + thisTime.getTime());
            Rack newRack = new Rack();
            newRack.addTime(thisTime);
            newRacks[allRacks.length] = newRack;
            allRacks = newRacks;
            System.out.println("Adding time " + thisTime.getTime() + " of rack " + 
                    thisTime.getRack());
        }
        
    }
    
    public void addRack(Rack thisRack,LabeledTime thisTime){
        thisRack.addTime(thisTime);
        
    }
    
    public void storeTubes(Tube[] tubes, int rack, String time){
        LabeledTime newTime = new LabeledTime(tubes,time,rack);
    
        
        addTime(newTime);
        
    }
    
    public void convertRackToSample(LabeledTime time){
        int rackNumberToLabel = time.getRack();
        System.out.println("Converting rack " + rackNumberToLabel);
        LabeledSample[] myConvertedSample = new LabeledSample[0];
        Rack rackToLabel = null;
        boolean foundATime = false;
//        System.out.println("There are " + allRacks.length + " racks. ");
//        System.out.println("They are: ");
//        for (int i = 0; i < allRacks.length; i++){
//            Rack rackToPrint = allRacks[i];
//            System.out.println("Rack " + rackToPrint.getRackNumber());
//            LabeledTime[] timesToPrint = rackToPrint.getTimes();
//            System.out.println("With " + timesToPrint.length + " times. Which are: ");
//            for (int j = 0; j < timesToPrint.length; j++){
//                LabeledTime printThis = timesToPrint[j];
//                
//                System.out.println(printThis.getTime());
//            }
//        }
//        
        for (int i = 0; i < allRacks.length; i++){
            rackToLabel = allRacks[i];
            if (rackToLabel.getRackNumber() == rackNumberToLabel){
                foundATime = true;
                LabeledTime[] timesToConvert = rackToLabel.getTimes();
                for (int j = 0; j < timesToConvert.length; j++){
                    System.out.println("Labeling time " + timesToConvert[j].getTime() + " at rack "
                    + timesToConvert[j].getRack());
                }
                LabeledSample converted = rackToLabel.labelRack(time);
                officialImage.addSample(converted);
            }
           
        }
        if (!foundATime){
            System.out.println("There is only one rack.");
            LabeledSample mySample = new LabeledSample();
            mySample.addTime(time);
            officialImage.addSample(mySample);
        }         
    }
    
    private LabeledSample[] appendValue(LabeledSample[] currentSample, LabeledSample newSample){
        ArrayList<LabeledSample> temp = new ArrayList<LabeledSample>(Arrays.asList(currentSample));
        temp.add(newSample);
        return (LabeledSample[])temp.toArray();
        
    }
    
    public void consolidateImage(LabeledImage theImage){
        LabeledSample[] allSamples = officialImage.getSamples();
        LabeledImage newOfficial = new LabeledImage();
        for (int i = 0; i < allSamples.length; i++){
            LabeledSample thisSample = allSamples[i];
            for (int j = i+1; j < allSamples.length; j++){
                LabeledSample thatSample = allSamples[j];
                if (thisSample != null && thatSample != null && thisSample.getSampleString().equalsIgnoreCase(thatSample.getSampleString())){
                    thisSample.mergeSample(thatSample);  
                    allSamples[j] = null;
                }
            }
            if(thisSample!=null){
                newOfficial.addSample(thisSample);
            }
            
        }
        officialImage = newOfficial;
        
    }
    
    public void saveImages(){
        System.out.println("We are gonna save");
        
        consolidateImage(officialImage);
        
        
        LabeledSample[] allSamples = officialImage.getSamples();
        BufferedImage allConcat = null;
        for (int i = 0; i < allSamples.length; i++){
            LabeledSample aSample = allSamples[i];
            System.out.println(aSample.getSampleString());
            LabeledTime[] ourTimes = aSample.getTimes();
            Arrays.sort(ourTimes);
            
            BufferedImage vertConcat = null;
            boolean first = true;
            for (int j = 0; j < ourTimes.length; j++){
                Tube[] ourTubes = ourTimes[j].getTubes();
                BufferedImage horzConcat = null;
                for (int k = 0; k < ourTubes.length; k ++){
                    Tube tube1 = ourTubes[k];
                    if(horzConcat == null){
                        horzConcat = tube1.getImage();
                    } else {
                        horzConcat = joinImageHorz(horzConcat,tube1.getImage(),10);
                    }                    
                }
                int time_in_minutes = Integer.parseInt(ourTimes[j].getTime())/60;
                horzConcat = addText(horzConcat,Integer.toString(time_in_minutes)+" min",30,false,130);
                if (vertConcat == null) {
                    vertConcat = horzConcat;
                } else {
                    vertConcat = joinImageVert(vertConcat,horzConcat, -1);
                }
                if (first){
                    vertConcat = addText(vertConcat,ourTimes[j].getLabel(),40,true,100);
                    first = false;
                }
          
            }
            
            if (allConcat == null) {
                allConcat = vertConcat;
            } else {
                allConcat = joinImageHorz(allConcat, vertConcat, 20);
            }

        }
        OutputFrame outputView = new OutputFrame(allConcat, OutputFrame.PCRTUBES, null, new Font("Tahoma",Font.PLAIN,36));

        
         
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
    
    
    //Adds a label when a FocusComboBox loses focus
    
    public void addALabel(String newLabel){
        allLabelsSoFar.add(newLabel);
        for (int i = 0; i < allLabelsSoFar.size(); i++){
            String thisLabel = allLabelsSoFar.get(i);
            for (int j = i + 1; j < allLabelsSoFar.size(); j++){
                String thatLabel = allLabelsSoFar.get(j);
                if(thisLabel.equalsIgnoreCase(thatLabel)){
                    allLabelsSoFar.remove(j);
                }
            }
        }
    }
    
    public void removeALabel(String thisLabel){
        for (int i = 0; i < allLabelsSoFar.size(); i++){
            if(allLabelsSoFar.get(i).equalsIgnoreCase(thisLabel)){
                allLabelsSoFar.remove(i);
            }
        }
    }
    
    
    public String[] getOtherLabels(){
        String[] options = allLabelsSoFar.toArray(new String[allLabelsSoFar.size()]);
        return options;
    }
    
  
}
    

