package com.epam.game;

import java.io.Serializable;

public class PlayerData implements Serializable{
    private final Integer SIZE;
    private final Integer NUMBERS[][];
    private final StringBuilder STEPS_HISTORY = new StringBuilder("[start]");
    private int timeCounter = -1;
    private int stepsCounter = 0;
    
    public PlayerData(Integer NUMBERS[][]) {
        this.SIZE = NUMBERS.length;
        this.NUMBERS = NUMBERS;
    }
    
    public Integer getSize() {
        return SIZE;
    }

//    public void setNumbers(Integer[][] NUMBERS) {
//        this.NUMBERS = NUMBERS;
//    }

    public Integer[][] getNumbers() {
        return NUMBERS;
    }

    public int getStepsCounter() {
        return stepsCounter;
    }
    
    public void setStepsCounter(int stepsCounter) {
        this.stepsCounter = stepsCounter;
    }
    
    public void incrementStepsCounter(){ stepsCounter++;}

    public int getTimeCounter() {
        return timeCounter;
    }

    public void setTimeCounter(int timeCounter) {
        this.timeCounter = timeCounter;
    }
    
    public void incrementTimeCounter(){ timeCounter++;}

    public String getStepsHistory() {
        return STEPS_HISTORY.toString();
    }
        
    public void setStepsHistory(String history) {
        STEPS_HISTORY.append(history);
    }    
    
    public void addStepToHistory(int rowIndex, int colIndex){
        STEPS_HISTORY.append("->[").append(rowIndex).append(":").append(colIndex).append("]");
    }
}
