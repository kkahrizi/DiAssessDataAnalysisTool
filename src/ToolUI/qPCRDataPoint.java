/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ToolUI;

/**
 *
 * @author kamin
 */
public class qPCRDataPoint implements Comparable{
    String label;
    double AverageCqValue;
    double CqValueStdev;
    
    public qPCRDataPoint(String name, double average, double stdev ){
        label = name;
        AverageCqValue = average;
        CqValueStdev = stdev;        
    }
    
    public String getLabel(){
        return label;
    }
    
    public double getCq(){
        return AverageCqValue;
    }
    
    public double getStdev(){
        return CqValueStdev;
    }
    
    public int compareTo(Object other){
        qPCRDataPoint otherPoint = (qPCRDataPoint) other;
        String otherLabel = otherPoint.getLabel();
        if (otherLabel.length() != 2 || label.length() != 2){
            System.out.println("NON CONFORMING LABEL");
            System.exit(0);
            return -1;
        } else {
            return label.compareTo(otherLabel);
        }
    }
    
}
