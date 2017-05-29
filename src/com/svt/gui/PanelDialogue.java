package com.svt.gui;

import com.svt.dialogues.Choix;
import com.svt.dialogues.Dialogue;
import com.svt.dialogues.Replique;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class PanelDialogue extends JPanel implements Runnable {

    private final Object waitLock = new Object();
    private final Frame frame;
    private final Font choiceFont;
    private final Font bubbleFont;
    private final Dialogue[] dialogues;
    private final BufferedImage background;
    private final BufferedImage ovule;
    private final BufferedImage bubble;
    private int currentDialogue;
    private int currentReplique;
    private int currentPhrase;
    private int selectionIndex;

    public PanelDialogue(Frame frame, Element dialoguesRoot, Font font) {

        this.frame = frame;
        choiceFont = font.deriveFont(48f);
        bubbleFont = font.deriveFont(32f);
        setForeground(Color.black);
        setBackground(Color.gray);

        try (InputStream stream = ClassLoader.getSystemResourceAsStream("background_dialogue.png")) {
            background = ImageIO.read(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (InputStream stream = ClassLoader.getSystemResourceAsStream("bubble.png")) {
            bubble = ImageIO.read(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (InputStream stream = ClassLoader.getSystemResourceAsStream("ovule.png")) {
            ovule = ImageIO.read(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {

                if (e.getID() == KeyEvent.KEY_RELEASED) {

                    switch (e.getKeyCode()) {

                        case KeyEvent.VK_ENTER:
                            Replique replique = dialogues[currentDialogue].repliques[currentReplique];
                            if (replique.choix != null) {
                                Choix choix = replique.choix[selectionIndex];
                                System.out.println("Selected " + choix.texte);
                                selectionIndex = 0;
                                if (choix.next == -1) {
                                    PanelDialogue.this.frame.showTransition(currentDialogue);
                                } else {
                                    currentReplique = choix.next;
                                    synchronized (waitLock) {
                                        waitLock.notify();
                                    }
                                    repaint();
                                }
                            }
                            return true;

                        case KeyEvent.VK_UP:
                            if (selectionIndex > 0) {
                                selectionIndex--;
                                repaint();
                            }
                            return true;

                        case KeyEvent.VK_DOWN:
                            if (selectionIndex < 1) {
                                selectionIndex++;
                                repaint();
                            }
                            return true;
                    }
                }

                return false;
            }
        });

        NodeList dialogueNodes = dialoguesRoot.getElementsByTagName("dialogue");

        dialogues = new Dialogue[dialogueNodes.getLength()];

        for (int i = 0; i < dialogues.length; i++) {

            Element dialogue = (Element) dialogueNodes.item(i);

            dialogues[i] = Dialogue.parseXML(dialogue);
        }

        new Thread(this).start();
    }

    public void showDialogue(int index) {
        currentDialogue = index;
        currentReplique = 0;
        currentPhrase = 0;
        synchronized (waitLock) {
            waitLock.notify();
        }
    }

    @Override
    public void run() {

        int delay = 2000;

        try {

            synchronized (waitLock) {
                waitLock.wait();
            }

            while (true) {

                if (currentDialogue < dialogues.length) {

                    Dialogue dialogue = dialogues[currentDialogue];
                    Replique[] repliques = dialogue.repliques;

                    if (currentReplique < repliques.length) {

                        Replique replique = repliques[currentReplique];

                        if (replique.choix == null) {

                            if (currentPhrase < replique.phrases.length) {
                                System.out.println(replique.phrases[currentPhrase]);
                                repaint();
                                Thread.sleep(delay);
                                currentPhrase++;
                            } else {
                                currentPhrase = 0;
                                if (replique.next == -1) {
                                    frame.showTransition(currentDialogue);
                                    synchronized (waitLock) {
                                        waitLock.wait();
                                    }
                                } else {
                                    Thread.sleep(delay / 2);
                                    currentReplique = replique.next;
                                    repaint();
                                }
                            }
                        } else {
                            currentPhrase = 0;
                            synchronized (waitLock) {
                                waitLock.wait();
                            }
                        }
                    }

                } else break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int scale = 4;

        g.drawImage(background, 0, 0, getWidth(), getHeight(), null);

        g.drawImage(ovule, 726, 196, ovule.getWidth() * scale, ovule.getHeight() * scale, null);

        Replique replique = dialogues[currentDialogue].repliques[currentReplique];

        if (replique.choix != null) {

            g.setFont(choiceFont);

            int length = replique.choix.length;

            if (selectionIndex >= length)
                selectionIndex = length - 1;

            g.setColor(selectionIndex == 0 ? getForeground() : getBackground());

            g.drawString(">", 7 * scale, 107 * scale);

            g.drawString(replique.choix[0].texte, 11 * scale, 107 * scale);

            if (length == 2) {

                g.setColor(selectionIndex == 1 ? getForeground() : getBackground());

                g.drawString(">", 7 * scale, 125 * scale);

                g.drawString(replique.choix[1].texte, 11 * scale, 125 * scale);
            }
        } else {

            g.setFont(bubbleFont);

            String phrase1;
            String phrase2;
            String phrase3;

            if (currentPhrase == 0) {

                phrase1 = replique.phrases[0];
                phrase2 = "";
                phrase3 = "";
            } else if (currentPhrase == 1) {
                phrase1 = replique.phrases[0];
                phrase2 = replique.phrases[1];
                phrase3 = "";
            } else {
                phrase1 = replique.phrases[currentPhrase - 2];
                phrase2 = replique.phrases[currentPhrase - 1];
                phrase3 = replique.phrases[currentPhrase];
            }

            g.drawImage(bubble, 96 * scale, 7 * scale, bubble.getWidth() * scale, bubble.getHeight() * scale, null);

            g.drawString(phrase1, 103 * scale, 14 * scale);
            g.drawString(phrase2, 103 * scale, 20 * scale);
            g.drawString(phrase3, 103 * scale, 26 * scale);
        }
    }
}
