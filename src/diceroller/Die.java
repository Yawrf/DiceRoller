/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diceroller;

import java.io.Serializable;
import java.util.Random;

/**
 *
 * @author rewil
 */
public class Die implements Serializable{
    
    private final int sides;
    private int lastRoll;
    private boolean read = true;
    private boolean rolling = false;
    private final int cap = 50;
    private int rolls = 0;
    
    private Random rand = new Random();
    
    public Die(int sides) {
        this.sides = sides;
    }
    
    public void roll() {
        lastRoll = rand.nextInt(sides) + 1;
    }
    
    public void roll(boolean reroll, boolean explode) {
        lastRoll = roll(reroll, explode, 0);
    }
    
    private int roll(boolean reroll, boolean explode, int current) {
        int temp = rand.nextInt(sides) + 1;
        
        if(reroll && temp == 1) {
            temp = roll(reroll, explode, current);
        }
        if(explode && temp == sides) {
            temp += roll(reroll, explode, current);
        }
        
        return temp;
    }
    
    public boolean rollExtended(boolean reroll, boolean explode) {
        int temp = roll(reroll, explode, 0);
        if(read) {
            if(!rolling) {
                rolling = true;
                rolls = rand.nextInt(cap) + 1;
            }
            if(rolling && (rolls > 0)) {
                lastRoll = temp;
                --rolls;
            } else if(rolling && rolls == 0) {
                read = false;
                rolling = false;
            }
        }
        return rolling;
    }
    
    public void resetRead() {
        read = true;
    }
    
    public boolean getRolling() {
        return rolling;
    }
    
    public int read() {
        return lastRoll;
    }
    
    public int size() {
        return sides;
    }
    
    @Override
    public String toString() {
        return "" + lastRoll;
    }
    
}
