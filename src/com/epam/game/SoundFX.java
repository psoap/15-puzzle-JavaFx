package com.epam.game;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.media.AudioClip;

public final class SoundFX{
    //names in file, not enum
    public enum SOUND_NAMES{
        BUMP, SWAP, WIN
    };
    private static final String PATH = "com/epam/game/resources/sounds/";
    private static final Map<SOUND_NAMES, AudioClip> SOUNDS = new HashMap<>();
    
    static {
        ClassLoader cl = SoundFX.class.getClassLoader();
        for (SOUND_NAMES value : SOUND_NAMES.values()) {
            URL u = cl.getResource(PATH + value + ".mp3");
            SOUNDS.put(value, new AudioClip(u.toString()));
        }
    }
    
    public static void playSound(SOUND_NAMES soundName){
        SOUNDS.get(soundName).play(0.3);
    }
}
