/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ToolUI;

import ToolUI.qPCRSampleGUI.FocusTextField;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author kamin
 */
public class AnalyzeqPCRFrame extends JFrame {
    public ArrayList<qPCRSample> organizedPCRSamples;
    Hashtable<Integer, FocusTextField> allControlTextFields;
    Hashtable<Integer, JLabel> allControlLabels;
    public final String CONTROLTOKEN = "c";
    public final String NTCToken = "NTC";
    /**
     * Creates new form AnalyzeqPCRFrame
     */
    public AnalyzeqPCRFrame(ArrayList<qPCRDataPoint> theseDataPoints) {
        initComponents();
        organizedPCRSamples = organizeDataPoints(theseDataPoints);
        qPCRSample controlSample = getControlSample();
        JPanel controlsPanel = produceControlsPanel(controlSample);
        qPCRSampleGUI buildaGUI = new qPCRSampleGUI(organizedPCRSamples);
        JPanel samplesPanel = buildaGUI.producePanel(dilutionsPane);
       
        controlPane.setViewportView(controlsPanel);
        controlPane.setBorder(BorderFactory.createTitledBorder("Controls"));
       
        
        dilutionsPane.setViewportView(samplesPanel);
        dilutionsPane.setBorder(BorderFactory.createTitledBorder("Samples"));
       
        this.pack();
        Component[] allComponents = samplesPanel.getComponents();
        

        for (int i = 0; i < allComponents.length; i++ ){
            Component attempt = allComponents[i];
            System.out.println(attempt);
            if (attempt instanceof JPanel){
                System.out.println("Worked");
                Component[] subComponents = ((JPanel)attempt).getComponents();
                for (int j = 0; j < subComponents.length; j++){
                    System.out.println(subComponents[j]);
                }
            }
            
        }
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    
    /**
     * Returns a panel that contains the controls in a row, underneath each is a
     * FocusTextField for entering concentration data
     * @param controlData
     * @return JPanel controls panel
     */
    public JPanel produceControlsPanel(qPCRSample controlData){
        JPanel controlPanel = new JPanel();
        GroupLayout layout = new GroupLayout(controlPanel);
        controlPanel.setLayout(layout);
        ArrayList<qPCRDataPoint> allControlDilutions = controlData.getDilutions();
        allControlTextFields = new Hashtable<Integer,FocusTextField>();
        allControlLabels = new Hashtable<Integer, JLabel>();
        for (int i = 0; i < allControlDilutions.size(); i++) {
            allControlTextFields.put(i, new FocusTextField());
            allControlLabels.put(i, new JLabel(allControlDilutions.get(i).getLabel()));
        }
        GroupLayout.SequentialGroup horizontalGroup = layout.createSequentialGroup();
        GroupLayout.SequentialGroup verticalGroup = layout.createSequentialGroup();
        int width  = 10;
        int height = 2;
        for (int i = 0; i < allControlDilutions.size(); i++) {
            horizontalGroup.addGroup(layout.createParallelGroup()
                    .addComponent(allControlLabels.get(i))
                    .addComponent(allControlTextFields.get(i)));
            
            
        }
        
        GroupLayout.ParallelGroup allLabelsGroup = layout.createParallelGroup();
        for (int i = 0; i < allControlDilutions.size(); i++ ){
           allLabelsGroup.addComponent(allControlLabels.get(i));
        }
        verticalGroup.addGroup(allLabelsGroup);
        GroupLayout.ParallelGroup allFocusTextFieldsGroup = layout.createParallelGroup();
        for (int i = 0; i < allControlDilutions.size(); i++) {
            allFocusTextFieldsGroup.addComponent(allControlTextFields.get(i));
        }
        verticalGroup.addGroup(allFocusTextFieldsGroup);
        layout.setVerticalGroup(verticalGroup);
        layout.setHorizontalGroup(horizontalGroup);
        return controlPanel;
    }
    
    
    
    
    
    
    /*
       Organizes the data points into samples using the label
       If the first letter of the label for two datapoints are the same, then the datapoints belong 
         to the same sample
    */
    public ArrayList<qPCRSample> organizeDataPoints(ArrayList<qPCRDataPoint> messyDataPoints){
        ArrayList<qPCRSample> orgSamples = new ArrayList<qPCRSample>();
        for (int i = 0; i < messyDataPoints.size(); i++ ){
            qPCRDataPoint pointToOrganize = messyDataPoints.get(i);
            if(pointToOrganize.getLabel().toLowerCase().contains(NTCToken.toLowerCase())){
                continue;
            }
            System.out.println(pointToOrganize.getLabel());
            int matchingIndex = getIndexOfSample(pointToOrganize, orgSamples);
            if ( matchingIndex != -1 ) {
                orgSamples.get(matchingIndex).addDataPoint(pointToOrganize);
            } else {
                qPCRSample newSample = new qPCRSample(pointToOrganize.getLabel().substring(0,1));
                newSample.addDataPoint(pointToOrganize);
                orgSamples.add(newSample);
            }
        }
        return orgSamples;
    }
    
    /* 
         Helper method for organizeDataPoints: checks if sample already exists and returns the index if it does
         If it doesn't exist, returns -1
    */
    public int getIndexOfSample(qPCRDataPoint thisPoint, ArrayList<qPCRSample> orgSamples){
        for (int i  = 0; i < orgSamples.size(); i++){
            qPCRSample thisSample = orgSamples.get(i);
            if ((thisSample.getLabel().substring(0,1)).equalsIgnoreCase((thisPoint.getLabel().substring(0,1)))){
                return i;
            }
        }
        return -1;
    }
    
    /* 
        Finds the control sample based on whether it matches the control token 
        Removes and returns the control sample
        If no samples are found that match, returns null
    */
    public qPCRSample getControlSample(){
        for (int i = 0; i < organizedPCRSamples.size(); i++ ){ 
            qPCRSample check = organizedPCRSamples.get(i);
            System.out.println(check.getLabel());
            if (check.getLabel().substring(0,1).equalsIgnoreCase(CONTROLTOKEN)){
                return organizedPCRSamples.remove(i);
            }
        }
        return null;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        dilutionsPane = new javax.swing.JScrollPane();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        controlPane = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("qPCR Analysis");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 11, 970, 20));
        getContentPane().add(dilutionsPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 166, 970, 330));

        jButton1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton1.setText("Analyze");
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 530, 100, 50));

        jButton2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton2.setText("CANCEL");
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 530, 100, 50));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel2.setText("Dilution-fold:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 500, 70, 20));

        jTextField1.setText("10");
        getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 500, 62, -1));
        getContentPane().add(controlPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 970, 70));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane controlPane;
    private javax.swing.JScrollPane dilutionsPane;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
