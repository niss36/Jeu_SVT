package com.svt.gui;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class Frame extends JFrame {

    public static final int DIALOGUE = 0, TRANSITION = 1;
    public static final String[] cards = {"DIALOGUE", "TRANSITION"};
    private final CardLayout cl = new CardLayout();
    private final PanelDialogue dialogue;
    private final PanelTransition transition;

    public Frame() {

        super("Les cycles sexuels féminins");

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        Container content = getContentPane();
        content.setPreferredSize(new Dimension(240 * 4, 135 * 4));

        pack();
        setLocationRelativeTo(null);

        Font font;

        try (InputStream stream = ClassLoader.getSystemResourceAsStream("font.ttf")) {
            font = Font.createFont(Font.TRUETYPE_FONT, stream);
        } catch (IOException | FontFormatException e) {
            throw new RuntimeException(e);
        }

        Element dialoguesRoot;

        try (InputStream stream = ClassLoader.getSystemResourceAsStream("dialogues.xml")) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(stream);
            dialoguesRoot = document.getDocumentElement();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }

        content.setLayout(cl);

        dialogue = new PanelDialogue(this, dialoguesRoot, font);
        transition = new PanelTransition(this, dialoguesRoot, font);

        content.add(dialogue, cards[DIALOGUE]);
        content.add(transition, cards[TRANSITION]);
    }

    public void showDialogue(int index) {

        cl.show(getContentPane(), cards[DIALOGUE]);
        dialogue.showDialogue(index);
    }

    public void showTransition(int index) {

        cl.show(getContentPane(), cards[TRANSITION]);
        transition.showTransition(index);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);

        if (b)
            showDialogue(0);
    }
}
