package com.epam.game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;

public class IOPlayerData {
//    public enum Commands{
//        HISTORY{
//            @Override
//            public String toString() {
//                return "--history";  
//            }
//        },
//        NUMBERS{
//            @Override
//            public String toString() {
//                return "--numbers";  
//            }
//        },
//        STEPS{
//            @Override
//            public String toString() {
//                return "--steps";  
//            }
//        },
//        SIZE{
//            @Override
//            public String toString() {
//                return "--size";  
//            }
//        },
//        TIME{
//            @Override
//            public String toString() {
//                return "--time";  
//            }
//        },
//        LANG{
//            @Override
//            public String toString() {
//                return "--lang";  
//            }
//        }
//    }
    
    //fix new line exists
    public static final String HISTORY = "--history",
                                NUMBERS = "--numbers",
                                STEPS = "--steps",
                                SIZE = "--size",
                                TIME = "--time",
                                LANG = "--lang";
    public static final String SAVES_FOLDER = "saves";
    
    public PlayerData inputTxtData(String fileName) throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader (new FileReader(SAVES_FOLDER+"/"+fileName+".txt"));
        String dataFromFile[] = br.readLine().split(" ");
        br.close();
        return inputTxtData(dataFromFile);
    }
    
    public PlayerData inputTxtData(String args[]){
        boolean isNewGame = true;
        int size = 0; //mb default value
        PlayerData pd = null;
        
        //fix if in switch
        for(int i=0;i<=args.length/2;i+=2){
            switch(args[i]){
                case SIZE: isNewGame = true; size=Integer.parseInt(args[i+1]);break;
                case NUMBERS: isNewGame = false; pd = new PlayerData(parseNumbers(args[i+1], size)); break;
                case STEPS: if(!isNewGame){pd.setStepsCounter(Integer.parseInt(args[i+1]));} break;
                case TIME: if(!isNewGame){pd.setTimeCounter(Integer.parseInt(args[i+1]));} break;
                case HISTORY: if(!isNewGame){pd.setStepsHistory(args[i+1]);} break;
                case LANG: GameWindow.setCurrentLocale(new Locale(args[i+1]));break; //doest work
                default: System.out.println("Invalid command");
            }
        }
        return isNewGame? new PlayerData(GameLogicUtil.getSolvedNumbers(size)): pd;
    }
    
    //need check data
    private Integer[][] parseNumbers(String numbers, final int size){
        String buffNumsStr[] = numbers.split(",");
        Integer nums[][] = new Integer[size][size];
        for(int i = 0; i<size; i++){
            for(int j = 0; j<size; j++){
                nums[i][j] = Integer.valueOf(buffNumsStr[i*size+j]);
            }
        }
        return nums;
    }
    
    public void outputTxtData(PlayerData playerData, String fileName) throws IOException{
        BufferedWriter bw = new BufferedWriter(new FileWriter((SAVES_FOLDER+"/"+fileName+".txt")));
        bw.write(generateOutputString(playerData));
        bw.close();
    }
    
    //fix empty spaces
    private String generateOutputString(PlayerData playerData){
        StringBuilder data = new StringBuilder();
        data.append(SIZE).append(" ").append(String.valueOf(playerData.getSize())).append(" ");
        
        data.append(NUMBERS).append(" ");
        Integer nums[][] = playerData.getNumbers();
        for(int i = 0; i<playerData.getSize(); i++){
            for(int j = 0; j<playerData.getSize(); j++){
                data.append(nums[i][j].toString()).append(",");
            }
        }
        data.replace(data.length()-1, data.length(), " ");
        
        data.append(STEPS).append(" ").append(String.valueOf(playerData.getStepsCounter())).append(" ");
        data.append(TIME).append(" ").append(String.valueOf(playerData.getTimeCounter())).append(" ");
        data.append(HISTORY).append(" ").append(playerData.getStepsHistory());
        
        return data.toString();
    }
    
    //try with resouces, catch here
    public PlayerData inputSerializableData(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException{
        PlayerData pd = null;
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVES_FOLDER+"/"+fileName+".pd"));
        Object bufObj = ois.readObject();
        if(bufObj instanceof PlayerData){
            pd = (PlayerData) bufObj;
        }
        ois.close();
        return pd;
    }
    
    public void outputSerializableData(PlayerData playerData, String fileName) throws FileNotFoundException, IOException{
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVES_FOLDER+"/"+fileName+".pd"));
        oos.writeObject(playerData);
        oos.close();
    }
}
