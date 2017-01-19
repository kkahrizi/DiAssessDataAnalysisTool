/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ToolUI;
import java.awt.image.BufferedImage;

/**
 *
 * @author kamin
 */
public class imagePair {
    private String time;
    private BufferedImage image;
    
    public imagePair(String when, BufferedImage what){
        if(when.contains(".jpg")){
            time = when.substring(0,4);
        } else {
            time = when;
        }
        image = what;
    }
    
    public String getTime(){
        return time;
    }
    
    public int getIntTime(){
        return Integer.parseInt(time);
    }
    
    public BufferedImage getImage(){
        return image;
    }
}
