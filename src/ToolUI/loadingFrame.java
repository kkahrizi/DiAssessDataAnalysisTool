/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ToolUI;
import java.io.*;
import javax.swing.*;
/**
 *
 * @author kamin
 */
public class loadingFrame extends javax.swing.JFrame implements FileFilter {
    public String currentText;
    public File passedFolder;
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
        } else {
            waitContinueButton.setText("Select another folder.");
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
        for (File subFolder : allSubFolders){
            addText(Integer.toString(index) + ". Looking in " + subFolder.getAbsolutePath());
            File[] allFiles = subFolder.listFiles();
            addText("Found " + allFiles.length + ": ");
            if (allFiles.length == 0){ 
                JOptionPane.showMessageDialog(this,"One of the subfolders of the folder you selected is empty. Either delete that subfolder, or pick another folder)");
                return false;
            }
            numFilesDict[index - 1] = allFiles.length;
            
            int subindex = 1;
            for (File file : allFiles){
                addText(Integer.toString(index) + "." + Integer.toString(subindex) + ". " + file.getName());
                subindex++;
            }
            
            
            index++;
        } 
        boolean organize = verifyNumFiles(numFilesDict);
        return true;
    }
            
    public boolean verifyNumFiles(int[] numbers){
        int firstNumber = numbers[0];
        for (int num : numbers){
            if (num != firstNumber){
                Object[] myOptions = {"Organize by name with padding", "Stitch together"};
                Object selectedValue = JOptionPane.showInputDialog(this, "All of your subfolders do not have the same number of images. \nWe can either organize images by their name and include padding images, or we can just stitch them together in order. What would you like to do?"
                        ,"Input",JOptionPane.INFORMATION_MESSAGE,null,myOptions,myOptions[0]);
                if (selectedValue.equals(myOptions[0])){
                    return true;
                }
                else {
                    return false;
                }
            }
        }
        return true;
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
