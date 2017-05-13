package com.svt.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class PanelDialogue extends JPanel {

    private final Frame frame;
    private final BufferedImage background;
    private int selectionIndex = 0;

    public PanelDialogue(Frame frame, Font font) {

        this.frame = frame;

        setFont(font);
        setForeground(Color.black);
        setBackground(Color.gray);

        BufferedImage background = null;

        try (InputStream stream = ClassLoader.getSystemResourceAsStream("background_dialogue.png")) {
            background = ImageIO.read(stream);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        this.background = background;

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {

                if (e.getID() == KeyEvent.KEY_RELEASED) {

                    switch (e.getKeyCode()) {

                        case KeyEvent.VK_ENTER:
                            System.out.println("Selected option " + selectionIndex);
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
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(background, 0, 0, getWidth(), getHeight(), null);

        g.setColor(selectionIndex == 0 ? getForeground() : getBackground());

        g.drawString(">", 14, 214);

        g.drawString("Option A", 22, 214);

        g.setColor(selectionIndex == 1 ? getForeground() : getBackground());

        g.drawString(">", 14, 250);

        g.drawString("Option B", 22, 250);
    }
}
