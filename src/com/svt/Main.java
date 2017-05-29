package com.svt;

import com.svt.gui.Frame;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        try (AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(ClassLoader.getSystemResourceAsStream("music.wav")))) {

            Clip clip = AudioSystem.getClip();
            clip.open(stream);

            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            float volume = 0.25f;
            gainControl.setValue((float) (Math.log(volume) / Math.log(10.0) * 20.0));

            clip.loop(Clip.LOOP_CONTINUOUSLY);

        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }

        new Frame().setVisible(true);
    }
}
