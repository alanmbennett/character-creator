// Program Title: CharacterCreator 
// Author: Alan Bennett
// Descroption: Application that allows creation of a customizable fictional character
// Creation date: 11/19/2015
// Last modification date: 12/9/2015

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Slider;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

public class CharacterCreator extends Application 
{
    private static GridPane rightPane, leftPane;
    private static Character character;
    private static ImageView display;
    
    public static void main(String[] args) 
    {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage)
    {
        leftPane = new GridPane();
        rightPane = new GridPane();
        character = new Character();
        
        /* rightPane */
        rightPane.setPrefWidth(335);
        rightPane.setHgap(5);
        rightPane.setVgap(10);
        
        sep(Orientation.VERTICAL, 0, 1, 18);
        
        TextField name = new TextField(); 
        rightPane.add(new Text("Name: "), 1, 1);
        rightPane.add(name, 2, 1);
        
        ChoiceBox[] boxArray = new ChoiceBox[2];
        
        setChoiceBox(boxArray[0] = new ChoiceBox(FXCollections.observableArrayList(
            "Human", "Dwarf", "High Elf", "Gnome", "Orc", "Buffalauren")), "Race :", 1, 2);
        
        setChoiceBox(boxArray[1] = new ChoiceBox(FXCollections.observableArrayList(
            "Templar", "Summoner", "Bishop", "Thief", "Performer")), "Class: ", 1, 3);
        
        ToggleGroup group = new ToggleGroup();
        
        RadioButton maleButton = new RadioButton("M");
        maleButton.setToggleGroup(group);
        maleButton.setSelected(true);
        character.setGender("M");
        
        RadioButton femaleButton = new RadioButton("F");
        femaleButton.setToggleGroup(group);
        
       group.selectedToggleProperty().addListener(
               (ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) -> 
        {
           RadioButton check = (RadioButton)t1.getToggleGroup().getSelectedToggle();
           character.setGender(check.getText());
           setImage();
        });
        
        GridPane genderPane = new GridPane();
        genderPane.add(maleButton, 0, 0);
        genderPane.add(femaleButton, 1, 0);
        genderPane.setHgap(5);
        
        rightPane.add(new Text("Gender: "), 1, 4);
        rightPane.add(genderPane, 2, 4);
        
        sep(Orientation.HORIZONTAL, 1, 6, 3);
        
        Text pointsSpent = new Text("0");
        Text pointsRemain = new Text("100");
        rightPane.add(new Text("Points Remaining: "), 1, 8);
        rightPane.add(pointsRemain, 2, 8);
        rightPane.add(new Text("Points Spent: "), 1, 9);
        rightPane.add(pointsSpent, 2, 9);
       
        Slider[] sliders = new Slider[4];
        int[] prevVals = new int[4];
        
        setSlider("Strength: ", sliders[0] = new Slider(0,100,0), 1, 11);
        setSlider("Intelligence: ", sliders[1] = new Slider(0,100,0), 1, 12);
        setSlider("Dexterity: ", sliders[2] = new Slider(0,100,0), 1, 13);
        setSlider("Wisdom: ", sliders[3] = new Slider(0,100,0), 1, 14);
        
        for (Slider i : sliders)
        {
           i.valueProperty().addListener(new ChangeListener() 
           {
                @Override
                public void changed(ObservableValue arg0, Object arg1, Object arg2) 
                {
                    pointsSpent.setText(String.valueOf(0 + (int) sliders[0].getValue() 
                                + (int) sliders[1].getValue() + (int) sliders[2].getValue()
                                + (int) sliders[3].getValue()));
                    pointsRemain.setText(String.valueOf(100 - (int) sliders[0].getValue() 
                                - (int) sliders[1].getValue() - (int) sliders[2].getValue()
                                - (int) sliders[3].getValue())); 
                        
                    if (Integer.valueOf(pointsSpent.getText()) <= 100 && 
                            Integer.valueOf(pointsSpent.getText()) >= 0)
                    {
                        for (int i = 0; i < 4; i++)
                            prevVals[i] = (int) sliders[i].getValue();
                    }
                    
                    else
                    {
                        for (int i = 0; i < 4; i++)
                            sliders[i].setValue(prevVals[i]);
                    }
                }
            });
        }
        
        sep(Orientation.HORIZONTAL, 1, 16, 3);
        
        Button saveButton = new Button("Save"), loadButton = new Button("Load");
        
        saveButton.setOnMouseClicked(e -> 
        { 
            character.setName(name.getText());
            character.setRace((String) boxArray[0].getValue());
            character.setStrength(sliders[0].getValue());
            character.setIntel(sliders[1].getValue());
            character.setDext(sliders[2].getValue());
            character.setWisdom(sliders[3].getValue());
            
            if (character.getName().length() >= 2)
            {   
                new Thread(() -> 
                {  
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                        "UMDCHAR files (*.umdchar)", "*.umdchar"));
                    fileChooser.setTitle("Save Your Character");
                    fileChooser.setInitialFileName(character.getName());
                  
                    Platform.runLater(() -> {
                        Stage saveStage = new Stage();
                        File file = fileChooser.showSaveDialog(saveStage);
                        
                        if(file != null)
                        {
                            try
                            {
                                FileWriter writer = new FileWriter(file);
                                writer.write(character.getName() + "\n" +
                                             character.getRace() + "\n" +
                                             character.getClassType() + "\n" +
                                             character.getGender() + "\n" +
                                             character.getStrength() + "\n" +
                                             character.getIntel() + "\n" +
                                             character.getDext() + "\n" +
                                             character.getWisdom());
                                writer.close();
                            }
                            catch (IOException ex)
                            {
                                Logger.getLogger(CharacterCreator.class.getName()).log(
                                    Level.SEVERE, null, ex);
                            }
                        }

                    });
                }).start();
            }
            
            else 
            {
                Stage errWindow = new Stage();
                HBox errPane = new HBox();
                Text errText = new Text("Error: Character name must be at "
                        + "least 2 characters long");
                errPane.getChildren().add(errText);
                Scene errScene = new Scene(errPane, 350, 20);
                errWindow.setScene(errScene);
                errWindow.setTitle("Error: Not Enough Characters in Name");
                errWindow.setResizable(false);
                errWindow.show();
            }
        });
        
        loadButton.setOnMouseClicked(e-> 
        {
            
            new Thread(() -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                        "UMDCHAR files (*.umdchar)", "*.umdchar"));
                fileChooser.setTitle("Load Your Character");
                
                Platform.runLater(() -> 
                {
                    Stage openStage = new Stage();
                    File file = fileChooser.showOpenDialog(openStage);
                    
                    if (file != null)
                    {
                        String[] contents = new String[8];
                        BufferedReader input = null;
                        try
                        {
                           input = new BufferedReader(
                                new FileReader(file));
                           
                           String text;
                           int i = 0;
                           while((text = input.readLine()) != null)
                           {
                               contents[i] = text;
                               i++;
                           }
                        }
                        catch (FileNotFoundException ex)
                        {
                            Logger.getLogger(CharacterCreator.class.getName()).log
                                    (Level.SEVERE, null, ex);
                        }
                        catch(IOException ex)
                        {
                            Logger.getLogger(CharacterCreator.class.getName()).log
                                    (Level.SEVERE, null, ex);
                        }
                        finally
                        {
                            try
                            {
                                input.close();
                            }
                            catch(IOException ex)
                            {
                                Logger.getLogger(CharacterCreator.class.getName()).log
                                        (Level.SEVERE, null, ex);
                            }
                        }
                 
                        name.setText(contents[0]);
                        boxArray[0].setValue(contents[1]);
                        boxArray[1].setValue(contents[2]);
                        character.setRace(contents[2]);
                        
                        if ("M".equals(contents[3]))
                            maleButton.setSelected(true);
                        else
                            femaleButton.setSelected(true);
                        
                        character.setGender(contents[3]);
                        setImage();
                        
                        sliders[0].setValue(Double.parseDouble(contents[4]));
                        sliders[1].setValue(Double.parseDouble(contents[5]));
                        sliders[2].setValue(Double.parseDouble(contents[6]));
                        sliders[3].setValue(Double.parseDouble(contents[7]));
                        
                        pointsSpent.setText(String.valueOf(0 + (int) sliders[0].getValue() 
                                + (int) sliders[1].getValue() + (int) sliders[2].getValue()
                                + (int) sliders[3].getValue()));
                        pointsRemain.setText(String.valueOf(100 - (int) sliders[0].getValue() 
                                - (int) sliders[1].getValue() - (int) sliders[2].getValue()
                                - (int) sliders[3].getValue())); 
                    }
                });
                
            }).start();
            
        });
        
        GridPane buttonPane = new GridPane();
        buttonPane.add(saveButton, 0, 0);
        buttonPane.add(loadButton, 1, 0);
        buttonPane.setHgap(10);
        rightPane.add(buttonPane, 1, 18);
        
        /* leftPane */
        leftPane.setPrefWidth(150);
        leftPane.setHgap(5);
        leftPane.setVgap(10);
        
        Text previewMsg = new Text("PREVIEW: ");
        previewMsg.setFont(new Font(20));
 
        leftPane.add(previewMsg, 1, 1);
        GridPane.setColumnSpan(previewMsg, 7);
        
        /* Copyright */
        HBox copyrightBox = new HBox();
        copyrightBox.getChildren().add(new Text(" Program \u00A9 2015 Alan Bennett."
                + " | Pictures \u00A9 Square Enix. "));
        
        /* bodyPane */
        BorderPane bodyPane = new BorderPane();
        
        bodyPane.setRight(rightPane);
        bodyPane.setLeft(leftPane);
        bodyPane.setBottom(copyrightBox);
        
        Scene mainScene = new Scene(bodyPane, 550, 430);
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Character Creator");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    public static void setSlider(String text, Slider slider, int c, int r)
    {
       Text valueDisplay = new Text("0");
       
       slider.valueProperty().addListener(new ChangeListener() 
       {
            @Override
            public void changed(ObservableValue arg0, Object arg1, Object arg2) 
            {
                valueDisplay.textProperty().setValue(
                        String.valueOf((int) slider.getValue()));
            }
        });
       
       rightPane.add(new Text(text), c, r);
       rightPane.add(slider, c + 1, r); 
       rightPane.add(valueDisplay, c + 2, r);
    }
    
    public void sep(Orientation orientation, int c, int r, int span)
    {
        Separator s = new Separator(orientation);
        rightPane.add(s, c, r);
        if (orientation != Orientation.HORIZONTAL) { GridPane.setRowSpan(s,span); }
        else { GridPane.setColumnSpan(s,span); }
    }
    
    public static void setChoiceBox(ChoiceBox box, String text, int c, int r)
    {
        box.getSelectionModel().selectFirst();
        rightPane.add(new Text(text), c, r);
        rightPane.add(box, c + 1, r);
        
        display = new ImageView();
        leftPane.add(display, 7, 8);
        character.setClassType(box.getValue().toString());
        setImage();
        
        box.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue obs, Object oldVal, Object newVal) ->
        {
            character.setClassType(box.getValue().toString());
            setImage();
        });
    }
    
    public static void setImage()
    {
       Image image;
       
       if (null != character.getClassType()) 
        switch (character.getClassType()) 
        {
            case "Templar":
                if ("F".equals(character.getGender()))
                {
                    image = new Image("BDFF_Edea_Paladin.png");
                    display.setFitHeight(239);
                    display.setFitWidth(144);
                }
                else
                {
                    image = new Image("BDFF_Tiz_Paladin.png");
                    display.setFitHeight(239);
                    display.setFitWidth(155);
                }
                display.setImage(image);
                break;
            case "Summoner":
                if ("F".equals(character.getGender()))
                {
                    image = new Image("BDFF_Agnes_Summoner.png");
                    display.setFitHeight(230);
                    display.setFitWidth(112);
                }   
                else
                {
                    image = new Image("BDFF_Ringabel_Summoner.png");
                    display.setFitHeight(230);
                    display.setFitWidth(113); 
                }
                display.setImage(image);
                break;
            case "Thief":
                if ("F".equals(character.getGender()))
                {
                    image = new Image("BDFF_Agnes_Thief.png");
                    display.setFitHeight(239);
                    display.setFitWidth(138);
                }
                else
                {
                    image = new Image("BDFF_Tiz_Thief.png");
                    display.setFitHeight(239);
                    display.setFitWidth(143);
                }
                display.setImage(image);
                break;
            case "Bishop":
                if ("F".equals(character.getGender()))
                {
                    image = new Image("BSEL_Edea_Bishop.png");
                    display.setFitHeight(226);
                    display.setFitWidth(133);
                }
                else
                {
                    image = new Image("BSEL_Yew_Bishop.png");
                    display.setFitHeight(223);
                    display.setFitWidth(116);
                }
                display.setImage(image);
                break;
            case "Performer":
                if ("F".equals(character.getGender()))
                {
                    image = new Image("BSEL_Agnes_Performer.png");
                    display.setFitHeight(253);
                    display.setFitWidth(129);
                }
                else
                {
                    image = new Image("BDFF_Ringabel_Performer.png");
                    display.setFitHeight(216);
                    display.setFitWidth(147);
                }
                display.setImage(image);
                break;
        } 
    }
}
