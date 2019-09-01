/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diceroller;

import filewriter.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.Duration;

/**
 *
 * @author rewil
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    TextField addCount;
    @FXML
    TextField addSides;
    @FXML
    TextField addModifier;
    @FXML
    TextField dropLowest;
    
    @FXML
    CheckBox rerollOnes;
    @FXML
    CheckBox explode;
    
    private CupHolder ch = new CupHolder();
    
    @FXML
    ListView list;
    
    public void rollAll() {
        for(int i = 0; i < ch.getCups().size(); ++i) {
            list.getSelectionModel().selectIndices(i);
            extendedRollSelected();
        }
    }
    
    public void extendedRollSelected() {
        
        if(list.getSelectionModel().getSelectedItem() != null) {
        
            int delay = 50;
            int cycles = 51; // Total Possible rolls as defined in Die.java:rollExtended

            DieCup dc = (DieCup) list.getSelectionModel().getSelectedItem();
            list.getSelectionModel().clearSelection();
            Timeline tl = new Timeline(new KeyFrame(Duration.millis(delay), (ActionEvent event) -> {
                dc.roll(false);
                updateList();
            })); 
            tl.setCycleCount(cycles);
            tl.play();
            Timeline finish = new Timeline(new KeyFrame(tl.getTotalDuration(), (ActionEvent event) -> {
                dc.roll(true);
                dc.resetRead();
                updateList();
            }));
            finish.setCycleCount(1);
            finish.play();
            Timeline rollcheck = new Timeline(new KeyFrame(Duration.millis(delay), (ActionEvent event) -> {
                if(!dc.getRolling()) {
                    tl.stop();
                    finish.jumpTo(finish.getTotalDuration().subtract(Duration.millis(5)));
                }
            }));
            rollcheck.setCycleCount(cycles);
            rollcheck.play();
        }
    }
    
    @Deprecated
    public void rollSelected() {
        Random rand = new Random();
        int count = rand.nextInt(50) + 1;
        rollSelected(count);
    }
    
    @Deprecated
    private void rollSelected(int ct) {
        
        //<editor-fold>
        
        int delay = 50;
        
        DieCup dc = (DieCup) list.getSelectionModel().getSelectedItem();
        list.getSelectionModel().clearSelection();
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(delay), (ActionEvent event) -> {
            dc.roll(false);
            updateList();
        })); 
        tl.setCycleCount(ct);
        tl.play();
        Timeline finish = new Timeline(new KeyFrame(tl.getTotalDuration(), (ActionEvent event) -> {
            dc.roll(true);
            updateList();
        }));
        finish.setCycleCount(1);
        finish.play();
        
        //</editor-fold>
    }
    
    public void removeAll() {
        ch = new CupHolder();
        updateList();
    }
    
    public void removeSelected() {
        try {
            DieCup dc = (DieCup) list.getSelectionModel().getSelectedItem();
            list.getSelectionModel().clearSelection();
                dc.setNote("Blank");
                updateList();
            ch.removeCup(dc);
            updateList();
        } catch(Exception e) {}
    }
    
    public void duplicateSelected() {
        try {
            DieCup dc = (DieCup) list.getSelectionModel().getSelectedItem();
            list.getSelectionModel().clearSelection();
            
            DieCup newDC = new DieCup();
            for(int i : dc.contents()) {
                newDC.add(i);
            }
            newDC.setDropLowest(dc.getDropLowest());
            newDC.setExplode(dc.getExplode());
            newDC.setModifier(dc.getModifier());
            newDC.setRerollOnes(dc.getRerollOnes());
            ch.addCup(newDC);
            
            updateList();
        } catch(Exception e) {}
    }
    
    public void addCup() {
        try{
            if(!addSides.getText().isEmpty() && Integer.parseInt(addSides.getText()) > 0 && !((rerollOnes.isSelected() || explode.isSelected()) && Integer.parseInt(addSides.getText()) < 2) && !(rerollOnes.isSelected() && explode.isSelected() && Integer.parseInt(addSides.getText()) < 3)) {
                if(addCount.getText().isEmpty()) {
                    addCount.setText("1");
                } if(addModifier.getText().isEmpty()) {
                    addModifier.setText("0");
                } if(dropLowest.getText().isEmpty() || Integer.parseInt(dropLowest.getText()) < 0) {
                    dropLowest.setText("0");
                } 
                DieCup dc = new DieCup(Integer.parseInt(addSides.getText()), Integer.parseInt(addCount.getText()));
                dc.setModifier(Integer.parseInt(addModifier.getText()));
                dc.setDropLowest(Integer.parseInt(dropLowest.getText()));
                dc.setRerollOnes(rerollOnes.isSelected());
                dc.setExplode(explode.isSelected());
                ch.addCup(dc);
                addSides.setText("");
                addCount.setText("");
                addModifier.setText("");
                dropLowest.setText("");
                rerollOnes.setSelected(false);
                explode.setSelected(false);
                updateList();
            }
        } catch(Exception e) {}
    }
    
    public void updateList() {
        ObservableList<DieCup> noList = FXCollections.observableArrayList(new ArrayList<>());
        list.setItems(noList);
        ObservableList<DieCup> cupsList = FXCollections.observableArrayList(ch.getCups());
        list.setItems(cupsList);
    }
    
  //-------------------------------------------------------------------------------
    
    @FXML
    TextField saveName;
    @FXML
    ComboBox saveList;
    
    Writer io = new Writer("Dice Roller");
    
    public void saveDieCups() {
        if(io.writeObject(ch, saveName.getText())) {
            saveName.setText("");
        } else {
            saveName.setText("An Error Occurred");
        }
        updateSaveList();
    }
    
    public void loadDieCups() {
        String s = (String) saveList.valueProperty().getValue();
        if(s != null) {
            ch = (CupHolder) io.readObject(s);
            saveName.setText(s);
            saveList.setValue(null);
            updateList();
        }
    }
    
    public void deleteSave() {
        String s = (String) saveList.valueProperty().getValue();
        if(s != null) {
            io.deleteFile(s);
            updateList();
            updateSaveList();
        }
    }
    
    public void updateSaveList() {
        ArrayList<String> files = io.listFiles();
        ObservableList<String> options = FXCollections.observableArrayList(files);
        saveList.setItems(options);
    }
    
  //-------------------------------------------------------------------------------
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        list.setCellFactory(TextFieldListCell.forListView());
        list.setCellFactory(new Callback<ListView<DieCup>, ListCell<DieCup>>(){
 
            @Override
            public ListCell<DieCup> call(ListView<DieCup> p) {
                 
                ListCell<DieCup> cell = new ListCell<DieCup>(){
 
                    @Override
                    protected void updateItem(DieCup t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t.toString());
                        } else {
                            setText("");
                        }
                    }
 
                };
                cell.setWrapText(true);
                cell.setPrefWidth(list.getPrefWidth());
                return cell;
            }
        });
        updateList();
        
        list.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                switch(event.getButton()) {
                    case PRIMARY: extendedRollSelected();
                        break;
                    case SECONDARY: 
                }
            }
        });
        
        list.setTooltip(new Tooltip("Left Click to Roll, Right Click to Select"));  
        updateSaveList();
    }    
}
