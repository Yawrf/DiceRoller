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
    
    private Random rand = new Random();
    
    public Die(int sides) {
        this.sides = sides;
    }
    
    public void roll() {
        lastRoll = rand.nextInt(sides) + 1;
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
