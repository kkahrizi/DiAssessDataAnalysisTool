/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ToolUI;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author kamin
 */
public class TubeLabeler extends javax.swing.JFrame {
    LabelManager theManager;
    JLabel[] allCroppedImages;
    FocusTextField[] textArray;
    FocusComboBox[] comboxArray;
    String[] allCurrentRacks;
    ImagePair[] croppedImages;
    public static final String SAME_TOKEN = "Same as tube to the left";
    public static final String FIRST_STRING_TOKEN = "Enter a label for this tube";
    /**
     * Creates new form TubeLabeler
     */
    public TubeLabeler(int[] tubeLocationsX, int tubeLocationY, BufferedImage image, int rackNumber, String time, LabelManager myManager) {
        theManager = myManager;
        initComponents();
        this.getRootPane().setDefaultButton(jButton1);
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        int yRange = 70;
                 
               
        int numTubes = tubeLocationsX.length;
        croppedImages = new ImagePair[numTubes];
        GroupLayout myLayout = new GroupLayout(tubePanel);
        tubePanel.setLayout(myLayout);
        this.setTitle("Rack " + rackNumber + ". Time: " + time);
        myLayout.setAutoCreateGaps(true);
        myLayout.setAutoCreateContainerGaps(true);


        comboxArray = new FocusComboBox[numTubes];
        textArray = new FocusTextField[numTubes];
        allCroppedImages = new JLabel[numTubes];

        GroupLayout.SequentialGroup horGroup = myLayout.createSequentialGroup();
        GroupLayout.ParallelGroup pictureGroup = myLayout.createParallelGroup();
        GroupLayout.ParallelGroup textGroup = myLayout.createParallelGroup();

        for (int x = 0; x < tubeLocationsX.length; x++){
            int xcor = tubeLocationsX[x]-77;
            int xHalfWidth = 80;
            int xStart = xcor - xHalfWidth;
            int xEnd = xcor + xHalfWidth;
            if (xStart < 0){
                xStart = 0;
            }
            if (xEnd > image.getWidth()) {
                xEnd = image.getWidth();
            }
                   
            
            
            int xExtra = 20; //Width of each image is a little longer than 2*xHalfWidth to avoid cropping tubes
            //System.out.println("X start:"  + xStart);
            //System.out.println("X end:" + xEnd);
            this.setPreferredSize(new Dimension(numTubes*2*xHalfWidth + 100,this.getContentPane().getSize().height+100));
           
            BufferedImage crop = image.getSubimage(xStart,tubeLocationY-4*yRange,xEnd-xStart,yRange*5);
            croppedImages[x] = new ImagePair(rackNumber,time,image.getSubimage(xStart,tubeLocationY-3*yRange,xEnd-xStart,yRange*5));
            textArray[x] = new FocusTextField();

            if(x != 0){
                textArray[x].setText(SAME_TOKEN);
                comboxArray[x] = new FocusComboBox(theManager,SAME_TOKEN);

            } else {
                textArray[x].setText(FIRST_STRING_TOKEN);
                comboxArray[x] = new FocusComboBox(theManager,FIRST_STRING_TOKEN);
            }
            allCroppedImages[x] = new JLabel(new ImageIcon(crop));
            horGroup.addGroup(myLayout.createParallelGroup()
                    .addComponent(allCroppedImages[x])
                    //.addComponent(textArray[x]));
                    .addComponent(comboxArray[x]));

            pictureGroup.addComponent(allCroppedImages[x]);
            textGroup.addComponent(comboxArray[x]);
            //textGroup.addComponent(textArray[x]);
        }

        myLayout.setHorizontalGroup(horGroup);
        myLayout.setVerticalGroup(myLayout.createSequentialGroup()
                .addGroup(pictureGroup)
                .addGroup(textGroup));
    }
    
    ImagePair[] getCroppedImages(){
        return croppedImages;
    }

    public void showData(LabelManager myManager){      
        theManager = myManager;
        this.pack();
        this.setVisible(true);
        
                
    }
    
    public Tube[] getTubes(){
        Tube[] myTubes = new Tube[croppedImages.length];
        for (int i = 0; i < croppedImages.length; i++){
            ImagePair thisCrop = croppedImages[i];
            Tube newTube = new Tube("",thisCrop.getImage(),thisCrop.getRackNumber(),thisCrop.getTime(),i);
            myTubes[i] = newTube;
        }
        return myTubes;
    }
    
   // public JTextField[] getLabels(){
   //     return textArray;
   // }
    
    public FocusComboBox[] getLabels(){
        return comboxArray;
    }
    
    public JButton getButton(){
        return jButton1;
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
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tubePanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("Save this rack's data");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tubePanelLayout = new javax.swing.GroupLayout(tubePanel);
        tubePanel.setLayout(tubePanelLayout);
        tubePanelLayout.setHorizontalGroup(
            tubePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        tubePanelLayout.setVerticalGroup(
            tubePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 371, Short.MAX_VALUE)
        );

        jScrollPane3.setViewportView(tubePanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 1359, Short.MAX_VALUE)
                    .addComponent(jScrollPane3))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        int rackNumber = croppedImages[0].getRackNumber();
        String time = croppedImages[0].getTime();
        String previousTubeName = "";
        LabeledTime newTime = new LabeledTime();
        
        newTime.setRack(rackNumber);
        newTime.setTime(time);
        //newTime.label(textArray[0].getText());
        newTime.label(((JTextField)comboxArray[0].getEditor().getEditorComponent()).getText());
        ArrayList<LabeledTime> allTimesInThisRack = new ArrayList<LabeledTime>();
        System.out.println("There are " + comboxArray.length + " tubes in this rack");
       // for (int i = 0; i < textArray.length; i++) {
       //String thisTubeName = textArray[i].getText();

        for (int i = 0; i < comboxArray.length; i++ ){    
            String thisTubeName = (String)((JTextField)comboxArray[i].getEditor().getEditorComponent()).getText();
            System.out.println(thisTubeName);
            if (thisTubeName.equalsIgnoreCase(SAME_TOKEN)) {
                thisTubeName = previousTubeName;
            } else if (!previousTubeName.equals("")) {
                //store the previous time and start a new time
                allTimesInThisRack.add(newTime);
                newTime = new LabeledTime();
                newTime.setRack(rackNumber);
                newTime.setTime(time);
                newTime.label(thisTubeName);
            }
            previousTubeName = thisTubeName;
            ImagePair thisCrop = croppedImages[i];
            BufferedImage thisImage = thisCrop.getImage();
            Tube newTube = new Tube(thisTubeName, thisImage, thisCrop.getRackNumber(), time, i);
            System.out.println("Storing tube with label " + thisTubeName + " which is at time " + time + " rack "
                    + thisCrop.getRackNumber() + " at position " + i);
            newTime.addTube(newTube);

        }
        allTimesInThisRack.add(newTime);
        System.out.println("There are " + allTimesInThisRack.size() + " samples in this rack" );
        while (!allTimesInThisRack.isEmpty()){
            System.out.println("Sending " + allTimesInThisRack.get(0).getLabel() + " to be converted.");
            theManager.convertRackToSample(allTimesInThisRack.remove(0));
        }
        int numFrames = theManager.decrementNumFrames();
        
        if (numFrames < 1){
            
            saveImagesToFile();
        }
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

  public void saveImagesToFile(){
      theManager.saveImages();
  }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel tubePanel;
    // End of variables declaration//GEN-END:variables
}
