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
public class ThermoSample {
    public ArrayList<ThermoReplicate> allReplicates;
    String label;
    
    public ThermoSample(ArrayList<ThermoReplicate> replicates, String name){
        allReplicates = replicates;
        label = name;
    }
    
    public ThermoSample(ArrayList<ThermoReplicate> replicates){
        allReplicates = replicates;
        label = null;
    }
    
    public ThermoSample(String name){
        allReplicates = new ArrayList<ThermoReplicate>();
        label = name;
    }
    
    public void label(String name){
        label = name;
    }
    
    public ArrayList<ThermoReplicate> getReplicates(){
        return allReplicates;
    }
    
    public void addReplicate(ThermoReplicate newRep){
        allReplicates.add(newRep);
    }
    
    public String getLabel(){
        return label;
    }
    
}
