/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diceroller;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author rewil
 */
public class DieCup implements Serializable{
    
    private final ArrayList<Die> contents = new ArrayList<>();
    private int modifier = 0;
    private int dropLowest = 0;
    
    boolean rerollOnes = false;
    boolean explode = false;
    
    private String note = "";
    
    public DieCup() {};
    public DieCup(int sides) {
        add(sides);
    }
    public DieCup(int sides, int count) {
        add(sides, count);
    }
    
    public void add(int sides) {
        contents.add(new Die(sides));
    }
    
    public void add(int sides, int count) {
        for(int i = 0; i < count; ++i) {
            contents.add(new Die(sides));
        }
    }
    
    public void setModifier(int i) {
        modifier = i;
    }
    
    public int getModifier() {
        return modifier;
    }
    
    public void setDropLowest(int i) {
        dropLowest = i;
    }
    
    public int getDropLowest() {
        return dropLowest;
    }
    
    public void setRerollOnes(boolean b) {
        rerollOnes = b;
    }
    
    public boolean getRerollOnes() {
        return rerollOnes;
    }
    
    public void setExplode(boolean b) {
        explode = b;
    }
    
    public boolean getExplode() {
        return explode;
    }
    
    public void setNote(String s) {
        note = s;
    }
    
    public String getNote() {
        return note;
    }
    
    public void empty() {
        contents.clear();
    }
    
    public int[] roll() {
        ArrayList<Integer> results = new ArrayList<>();
        for(Die d : contents) {
            d.roll();
            int temp = 0;
            while((rerollOnes || explode) && (d.read() == 1 || d.read() == d.size())) {
                if(rerollOnes && d.read() == 1) {
                    d.roll();
                } else if(explode && d.read() == d.size()) {
                    temp += d.read();
                    d.roll();
                } else {
                    break;
                }
            }
            temp += d.read();
            results.add(temp);
        }
        
        int[] output = new int[results.size()];
        for(int i = 0; i < output.length; ++i) {
            int temp = Integer.MAX_VALUE;
            for(Integer j : results) {
                temp = j < temp ? j : temp;
            }
            output[i] = temp;
            results.remove((Integer)temp);
        }
        
        return output;
    }
    
    public int[] contents() {
        int[] output = new int[contents.size()];
        for(int i = 0; i < contents.size(); ++i) {
            output[i] = contents.get(i).size();
        }
        return output;
    }
    
    @Override
    public String toString() {
        String output = "";
        int count = 0;
        ArrayList<Integer> values = new ArrayList<>();
        for(Die d : contents) {
            if(!values.contains(d.size())) {
                values.add(d.size());
            }
        }
        boolean hold = false;
        for(Integer i : values) {
            if(hold) {
                output += ", ";
            }
            hold = true;
            count = 0;
            for(Die d : contents) {
                if(i == d.size()) {
                    ++count;
                }
            }
            output += count + "d" + i;
        }
        if(modifier > 0) {
            output += " : +" + modifier;
        } else if(modifier < 0) {
            output += " : " + modifier;
        }
        if(dropLowest > 0) {
            output += " | Drop " + dropLowest;
        }
        if(rerollOnes) {
            output += " | Reroll Ones";
        }
        if(explode) {
            output += " | Explode";
        }
        if(!note.isEmpty()) {
            output += " | " + note;
        }
        
        if(note.matches("Blank")){
            output = "";
        }
        
        return output;
    }
    
}
