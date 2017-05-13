package com.svt.gui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class Frame extends JFrame {

    public static final int DIALOGUE = 0, DEPLACEMENT = 1;
    public static final String[] cards = {"DIALOGUE", "DEPLACEMENT"};
    private final CardLayout cl = new CardLayout();

    public Frame() {

        super("Les cycles sexuels féminins");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        Container content = getContentPane();
        content.setPreferredSize(new Dimension(480, 270));

        pack();
        setLocationRelativeTo(null);

        Font font = null;

        try (InputStream stream = ClassLoader.getSystemResourceAsStream("font.ttf")) {
            font = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(32f);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        content.setLayout(cl);

        PanelDeplacement deplacement = new PanelDeplacement(this, font);
        PanelDialogue dialogue = new PanelDialogue(this, font);

        content.add(dialogue, cards[DIALOGUE]);
        content.add(deplacement, cards[DEPLACEMENT]);
    }

    public void show(int index) {

        cl.show(getContentPane(), cards[index]);
    }
}
