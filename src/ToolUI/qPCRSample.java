/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ToolUI;

import java.util.ArrayList;

/**
 *
 * @author kamin
 */
public class qPCRSample implements Comparable {
    public String alfaLabel;
    public String properName;
    public ArrayList<qPCRDataPoint> allDilutions;
    
    public qPCRSample(String letter){
        alfaLabel = letter;
        properName = "";
        allDilutions = new ArrayList<qPCRDataPoint>();
    }
    
    public void addDataPoint(qPCRDataPoint thisDilution){
        allDilutions.add(thisDilution);
    }
    
    public ArrayList<qPCRDataPoint> getDilutions(){
        return allDilutions;
    }

    public void setName(String name) {
        properName = name;
    }

    public boolean hasName() {
        if (properName == null || properName.equals("")) {
            return false;
        }
        return true;
    }
    
    public String getLabel(){
        return alfaLabel;
    }
    
    public int compareTo(Object other){
        qPCRSample otherSample = (qPCRSample) other;
        String otherLabel = otherSample.getLabel();
        if (otherLabel.length() != 1 || alfaLabel.length() != 1){
            System.out.println("NON CONFORMING LABEL");
            System.exit(0);
            return -1;
        } else {
            return alfaLabel.compareTo(otherLabel);
        }
    }
}
