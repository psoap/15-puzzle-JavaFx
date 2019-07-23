package com.epam.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GameLogicUtil {
    public static Integer[][] getSolvedNumbers(final int size){
        List<Integer> buffNubers = new ArrayList<>();
        for(Integer i = 0; i<size*size; i++){
            buffNubers.add(i);
        }
        do{
            Collections.shuffle(buffNubers);
        } while (!isSolved(buffNubers));
        Integer numbers[][] = new Integer[size][size];
        for(int i = 0; i<size; i++){
            for(int j = 0; j<size; j++){
                numbers[i][j] = buffNubers.get(i*size+j);
            }
        }
        return numbers;
    }
    
    private static boolean isSolved(List<Integer> randomNumbers){
        int sum = 0;
        final int SIZE = randomNumbers.size();
        for (int i = 0; i < SIZE; i++) {
            if (randomNumbers.get(i) == 0) {
                sum += i / Math.sqrt(SIZE);
                continue;
            }
            for (int j = i + 1; j < SIZE; j++) {
                if (randomNumbers.get(j) < randomNumbers.get(i))
                    sum++;
            }
        }
        return sum % 2 == 0;
    }
}
