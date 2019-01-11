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
public class CupHolder implements Serializable{
    
    private ArrayList<DieCup> cups = new ArrayList<>(); 
    
    public void addCup(DieCup dc) {
        cups.add(dc);
    }
    
    public DieCup getCup(int i) {
        return cups.get(i);
    }
    
    public ArrayList<DieCup> getCups() {
        return cups;
    }
    
    public void removeCup(int i) {
        cups.remove(i);
    }
    
    public void removeCup(DieCup dc) {
        cups.remove(dc);
    }
    
}
