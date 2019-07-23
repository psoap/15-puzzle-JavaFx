package com.epam.game;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class GameWindow extends Application{
    private final BorderPane MAIN_BORDER_PANE = new BorderPane();
    private final IOPlayerData IOPD = new IOPlayerData();
    private final Label LBL_TIME_COUNTER = new Label();
    private final Label LBL_STEPS_COUNTER = new Label();
    private final Label LBL_WIN = new Label();
    private final String SAVE_FILE_NAME = "save";
    private final DefaultSettings DEFAULT_SETTINGS = new DefaultSettings();
    private ResourceBundle rb;
    private Stage primaryStage;
    private GridPane gamePane;
    private Button buttons[][];
    private PlayerData currenPlayerData;
    private Integer currentPlayerNumbers[][];
    private int currentSize;
    private static Locale currentLocale;
    private boolean isWin = false;
    private Timer timer;
    
    @Override
    public void start(Stage primaryStage){
        this.primaryStage = primaryStage;
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        DEFAULT_SETTINGS.load();
        initGUIElements();
        initPlayerData(getParameters().getUnnamed().toArray(new String[getParameters().getUnnamed().size()]));
        initGame();

        File saveFolder = new File(IOPlayerData.SAVES_FOLDER);
        if(!saveFolder.exists()) {
            saveFolder.mkdir();
        }
                
        MAIN_BORDER_PANE.setStyle("-fx-border-color: darkgray;");
        Scene scene = new Scene(MAIN_BORDER_PANE);
        primaryStage.setTitle("Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    public void initPlayerData(String args[]){
        if(args.length<2){
            args = new String[2];
            args[0] = IOPlayerData.SIZE;
            args[1] = DEFAULT_SETTINGS.getSize().toString();
        }
        updateCurrentPlayerData(IOPD.inputTxtData(args));
    }
    
    private void updateCurrentPlayerData(PlayerData pd){
        currenPlayerData = pd;
        currentPlayerNumbers = currenPlayerData.getNumbers();
        currentSize = currenPlayerData.getSize();
        updateStepsCounter();
        updateTimeCounter();
        isWin = false;
    }
    
    public void startTimer(){
        if(timer!=null) timer.cancel();
        timer = new Timer(true);
        TimerTask task = new TimerTask(){
           @Override
           public void run() {
               if(isWin) timer.cancel();
               currenPlayerData.incrementTimeCounter();
               Platform.runLater(() -> {
                   updateTimeCounter();
               });
           }
        };
        timer.schedule(task, 0, 1000);
    }
    
    public static void setCurrentLocale(Locale locale){
        currentLocale = locale;
    }
    
    //fix repeats
    public void initGUIElements(){
        if(currentLocale==null) currentLocale = DEFAULT_SETTINGS.getLang(); 
        rb = ResourceBundle.getBundle("com.epam.game.resources.locale.lang", currentLocale);
        
        MenuItem btnNewGame = new MenuItem(rb.getString("menu.game.new"));
        btnNewGame.setOnAction((event) -> {
                    initPlayerData(new String[]{IOPlayerData.SIZE, DEFAULT_SETTINGS.getSize().toString()});
                    initGame();
        });
        
        MenuItem btnSaveGameTxt = new MenuItem(rb.getString("menu.game.saveTxt"));
        btnSaveGameTxt.setOnAction((event) -> {
            try {
                IOPD.outputTxtData(currenPlayerData, SAVE_FILE_NAME);
            } catch (IOException ex) {
                Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        MenuItem btnSaveGamePd = new MenuItem(rb.getString("menu.game.savePd"));
        btnSaveGamePd.setOnAction((event) -> {
            try {
                IOPD.outputSerializableData(currenPlayerData, SAVE_FILE_NAME);
            } catch (IOException ex) {
                Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        MenuItem btnLoadGameTxt = new MenuItem(rb.getString("menu.game.loadTxt"));
        btnLoadGameTxt.setOnAction((event) -> {
            try {
                updateCurrentPlayerData(IOPD.inputTxtData(SAVE_FILE_NAME));
                initGame();
            } catch (IOException ex) {
                Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        MenuItem btnLoadGamePd = new MenuItem(rb.getString("menu.game.loadPd"));
        btnLoadGamePd.setOnAction((event) -> {
            try {
                updateCurrentPlayerData(IOPD.inputSerializableData(SAVE_FILE_NAME));
                initGame();
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        MenuItem btnExitGame = new MenuItem(rb.getString("menu.game.exit"));
        btnExitGame.setOnAction((event) -> {
            System.exit(1);
        });
        
        Menu menu = new Menu(rb.getString("menu.game"), null, btnNewGame, 
                            btnSaveGameTxt, btnSaveGamePd, btnLoadGameTxt, 
                            btnLoadGamePd, btnExitGame);
        MenuBar menuBar = new MenuBar(menu);
        MAIN_BORDER_PANE.setTop(menuBar);
        
        StackPane  bottomPane = new StackPane(LBL_TIME_COUNTER,LBL_STEPS_COUNTER);
        StackPane.setAlignment(LBL_TIME_COUNTER, Pos.BOTTOM_LEFT);
        StackPane.setAlignment(LBL_STEPS_COUNTER, Pos.BOTTOM_RIGHT);
        bottomPane.setStyle("-fx-font: 13px \"Arial\";");
        MAIN_BORDER_PANE.setBottom(bottomPane);
        
        LBL_WIN.setText(rb.getString("lblWin"));
        LBL_WIN.setStyle("-fx-font: 30px \"Arial\";-fx-text-fill: red;");
    }
    
    public void initGame(){
       gamePane = new GridPane();
       buttons = new Button[currentSize][currentSize];
       for(int i = 0; i<currentSize; i++){
           for(int j = 0; j<currentSize; j++){
               String n = String.valueOf(currentPlayerNumbers[i][j]);
               buttons[i][j] = new Button(n);
               buttons[i][j].setMinSize(50d, 50d);
               buttons[i][j].setOnMouseClicked((event) -> {
                   if(event.getSource() instanceof Button){
                       shiftEvent(Integer.parseInt(((Button)event.getSource()).getText()));
                   }
               });
               if(currentPlayerNumbers[i][j] == 0) buttons[i][j].setVisible(false);
               gamePane.add(buttons[i][j], j, i);
           }
       }
       gamePane.setStyle("-fx-focus-color: silver;-fx-faint-focus-color: transparent;"
                            + "-fx-font: 15px \"Arial\";");
       StackPane sp = new StackPane(gamePane,LBL_WIN);
       MAIN_BORDER_PANE.setCenter(sp);
       LBL_WIN.setVisible(false);
       
       resizePrimaryStage();
       startTimer();
   }
    
    private void resizePrimaryStage(){
        primaryStage.setWidth(currentSize*buttons[0][0].getMinWidth() + 2);
        primaryStage.setHeight(currentSize*buttons[0][0].getMinHeight() + 50);
    }
   
    public void shiftEvent(int clicketButtonNumber){
       boolean flagFoundIndex = false;
       int rowIndxClickedBtn=0;
       int colIndxClickedBtn=0;
       for(int i = 0; i<currentSize; i++){
           for(int j = 0; j<currentSize; j++){
                if(currentPlayerNumbers[i][j] == clicketButtonNumber){
                    rowIndxClickedBtn=i;
                    colIndxClickedBtn=j;
                    flagFoundIndex = true;
                    break;
                }
                if(flagFoundIndex) {
                    break;
                }
           }
        }
       boolean flag = false;
        if (rowIndxClickedBtn > 0) {
            if (currentPlayerNumbers[rowIndxClickedBtn - 1][colIndxClickedBtn] == 0) {
                swapEmptyBtnWithClickedBtn(rowIndxClickedBtn, colIndxClickedBtn, rowIndxClickedBtn-1,
                                            colIndxClickedBtn, clicketButtonNumber);
                flag = true;
            }
        }
        if (rowIndxClickedBtn < currentSize-1) {
            if (currentPlayerNumbers[rowIndxClickedBtn + 1][colIndxClickedBtn] == 0) {
                swapEmptyBtnWithClickedBtn(rowIndxClickedBtn ,colIndxClickedBtn ,rowIndxClickedBtn+1,
                                            colIndxClickedBtn, clicketButtonNumber);
                flag = true;
            }
        }
        if (colIndxClickedBtn > 0) {
            if (currentPlayerNumbers[rowIndxClickedBtn][colIndxClickedBtn - 1] == 0) {
                swapEmptyBtnWithClickedBtn(rowIndxClickedBtn, colIndxClickedBtn, rowIndxClickedBtn,
                                            colIndxClickedBtn-1, clicketButtonNumber);
                flag = true;
            }
        }
        if (colIndxClickedBtn < currentSize-1) {
            if (currentPlayerNumbers[rowIndxClickedBtn][colIndxClickedBtn + 1] == 0) {
                swapEmptyBtnWithClickedBtn(rowIndxClickedBtn, colIndxClickedBtn, rowIndxClickedBtn,
                                            colIndxClickedBtn+1, clicketButtonNumber);
                flag = true;
            } 
        }
        if(!flag) SoundFX.playSound(SoundFX.SOUND_NAMES.BUMP); 
        checkWin();
   }
   
    private void swapEmptyBtnWithClickedBtn(int rowIndxClickedBtn, int colIndxClickedBtn, 
                                           int rowIndxEmptyBtn, int colIndxEmptyBtn, 
                                           int clicketButtonValue){
                SoundFX.playSound(SoundFX.SOUND_NAMES.SWAP);
                currentPlayerNumbers[rowIndxEmptyBtn][colIndxEmptyBtn] = clicketButtonValue;
                currentPlayerNumbers[rowIndxClickedBtn][colIndxClickedBtn] = 0;
                currenPlayerData.addStepToHistory(rowIndxEmptyBtn, colIndxEmptyBtn);
                currenPlayerData.incrementStepsCounter();
                buttons[rowIndxEmptyBtn][colIndxEmptyBtn].setText(clicketButtonValue+"");
                buttons[rowIndxEmptyBtn][colIndxEmptyBtn].setVisible(true);
                buttons[rowIndxClickedBtn][colIndxClickedBtn].setText("0");
                buttons[rowIndxClickedBtn][colIndxClickedBtn].setVisible(false);
                updateStepsCounter();
   }
   
    private void updateStepsCounter(){
        LBL_STEPS_COUNTER.setText(rb.getString("bottomLbl.steps")+
                                String.valueOf(currenPlayerData.getStepsCounter()));
   }
   
    private void updateTimeCounter(){
        LBL_TIME_COUNTER.setText(rb.getString("bottomLbl.time")+
                                String.valueOf(currenPlayerData.getTimeCounter()));
   }
   
    private void checkWin(){
       if(currentPlayerNumbers[currentSize-1][currentSize-1]!=0) return;
       for(int i = 0; i<currentSize; i++){
           for(int j = 0; j<currentSize; j++){
               if(i==currentSize-1 && j==currentSize-1) break;
               if(currentPlayerNumbers[i][j] != i*currentSize+j+1) return;
           }
       }
       SoundFX.playSound(SoundFX.SOUND_NAMES.WIN); 
       isWin = true;
       LBL_WIN.setVisible(true);
       gamePane.setDisable(true);
   }
      
    
    public static void main(String[] args) {
        launch(args);
    }
}