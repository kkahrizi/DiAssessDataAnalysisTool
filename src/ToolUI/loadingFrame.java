/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ToolUI;
import java.io.*;
import javax.swing.*;
import javax.imageio.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.ListIterator;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.util.stream.*;
import javax.swing.JOptionPane;
import org.math.plot.*;
/**
 *
 * @author kamin
 */
public class loadingFrame extends javax.swing.JFrame implements FileFilter {
    public String currentText;
    public File passedFolder;
    public int organize;
    public final int EQUAL_NUMBER = 0;
    public final int STITCH_TOGETHER = 1;
    public final int ORGANIZE = 2;
    public ArrayList<imagePair>[] allImages;
    /**
     * Creates new form loadingFrame
     */
    public loadingFrame(File folder) {
        setUndecorated(true);
        initComponents();
        currentText = "";
        passedFolder = folder;
        boolean success = loadImages(folder);
        if(success){
            waitContinueButton.setText("Continue!");
            waitContinueButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    analyzeImages();
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
    
    
    //Loads images in folder and sends them for processing
    //returns true if successful, false if there is an error
    public boolean loadImages(File folder)
    {
        addText("Okay, looking for all folders inside: " + folder.getAbsolutePath());
        File[] allSubFolders = folder.listFiles(this);
        addText("Found " + allSubFolders.length + " subfolders: ");
        if (allSubFolders.length == 0){
            JOptionPane.showMessageDialog(this,"Error, the folder you selected is empty.");
            return false;
        }
        int index = 1;
        int numFiles = 0;
        int[] numFilesDict = new int[allSubFolders.length];
        allImages = new ArrayList[allSubFolders.length];
        
        for (File subFolder : allSubFolders){
            addText(Integer.toString(index) + ". Looking in " + subFolder.getAbsolutePath());
            String[] splitStr = subFolder.getName().split("\\s");
            System.out.println(subFolder.getName());
            System.out.println(splitStr[splitStr.length-1]);
            System.out.println(Integer.parseInt(splitStr[splitStr.length-1]));
            int rackNumber = Integer.parseInt(splitStr[splitStr.length -1]);
            File[] allFiles = subFolder.listFiles();
            addText("Found " + allFiles.length + ": ");
            if (allFiles.length == 0){ 
                JOptionPane.showMessageDialog(this,"One of the subfolders of the folder you selected is empty. Either delete that subfolder, or pick another folder)");
                return false;
            }
            numFilesDict[index - 1] = allFiles.length;
            
            int subindex = 1;
            for (File file : allFiles){
                
                if(allImages[index-1] == null){
                    allImages[index-1] = new ArrayList();
                }
                try{
                    allImages[index-1].add(new imagePair(rackNumber, file.getName(),ImageIO.read(file)));
                } catch (IOException e) {
                    addText("An error occurred while trying to open image " + file.getName());
                }
                addText(Integer.toString(index) + "." + Integer.toString(subindex) + ". " + file.getName() + " loaded successfully.");
                subindex++;
            }
            
            
            index++;
        } 
        organize = verifyNumFiles(numFilesDict);
        if (organize == EQUAL_NUMBER)
            addText("Each folder has an equal number of images");
        else if (organize == ORGANIZE)
            addText("When joining images, we will create padding for missing images");
        else if (organize == STITCH_TOGETHER)
            addText("When joining images, we will simply skip over missing images");
        return true;
    }
    
    //After all file number checks pass and user input is considered, analyze images reads the images,
    //Finds the number of tubes in each image. If two images in the same rack have different number of tubes, throws an error
    //Returns true if successful, false if failed
    public boolean analyzeImages(){
        addText("Ok, starting to analyze images.");
        
        //First create an arraylist<String> of all unique image times, and also
        //create an arraylist of all tubelocations
        ArrayList<String> uniqueTimes = new ArrayList();
        for(int i = 0; i < allImages.length; i++){
            ArrayList thisRacksImages = allImages[i];
            ListIterator<imagePair> imageIterator = thisRacksImages.listIterator();
            while (imageIterator.hasNext()){
                imagePair thisImage = imageIterator.next();
                BufferedImage image = thisImage.getImage();
                int width = image.getWidth();
                int height = image.getHeight();
                int[][] blueChannel = new int[width][height];
                for (int x = 0; x < width; x++){
                    for (int y = 0; y < height; y++){
                        blueChannel[x][y] = new Color(image.getRGB(x, y)).getBlue();
                    }
                }
                int[] tubeLocationsX = analyzeImageX(blueChannel, "Rack: " + thisImage.getRackNumber() + 
                        " Time:  " + thisImage.getTime());
                int tubeLocationY = analyzeImageY(blueChannel);
                String newTime = thisImage.getTime();
                ListIterator<String> timeIterator = uniqueTimes.listIterator();
                boolean isNew = true;
                while (timeIterator.hasNext()){
                    String storedTime = timeIterator.next();
                    if (storedTime.equals(newTime)){
                        isNew = false;
                    }
                }
                if (isNew){
                    uniqueTimes.add(newTime);
                }
            }
        }
        return true;
    }
    
    /**
     *
     * @param blueChannel
     * @return
     */
    public int[] analyzeImageX(int[][] blueChannel, String source){
        int[] collapsedX = new int[blueChannel.length];
        for (int x = 0; x < collapsedX.length; x++){
            collapsedX[x] = IntStream.of(blueChannel[x]).sum();
        }
        double[] xAxis = new double[collapsedX.length];
        double[] yAxis = new double[collapsedX.length];
        for (int x = 0; x < xAxis.length; x++){
            xAxis[x] = x;
            yAxis[x] = collapsedX[x];
        }
        
        double[] filtered = hanningWindow(yAxis);
        
        plotGraph(source,xAxis,yAxis);
        plotGraph(source + " Hanning ", xAxis, filtered);
        return collapsedX;
        
        
    }
    
    public double[] hanningWindow(double[] recordedData) {
        double[] filtered = recordedData.clone();
        // iterate until the last line of the data buffer
        for (int n = 1; n < recordedData.length; n++) {
        // reduce unnecessarily performed frequency part of each and every frequency
        filtered[n] *= 0.5 * (1 - Math.cos((2 * Math.PI * n)
                / (filtered.length - 1)));
        }
        // return modified buffer to the FFT function
        return filtered;
    }
    
    public void plotGraph(String source, double[] xAxis, double[] yAxis){
        JFrame frame = new JFrame(source);
        Plot2DPanel panel = new Plot2DPanel();
        panel.addLinePlot("Source",xAxis, yAxis);
        frame.setContentPane(panel);
        frame.setSize(500,600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
    
    public int analyzeImageY(int[][] blueChannel){
        return 0;
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
        return fileName.isDirectory();
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
        jScrollPane1 = new javax.swing.JScrollPane();
        myTextField = new javax.swing.JTextArea();
        cancelButton = new javax.swing.JButton();
        waitContinueButton = new javax.swing.JButton();

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(waitContinueButton, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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

    /**
     * @param args the command line arguments
     */
   
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JScrollBar jScrollBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea myTextField;
    private javax.swing.JButton waitContinueButton;
    // End of variables declaration//GEN-END:variables
}
