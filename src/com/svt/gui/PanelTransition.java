package com.svt.gui;

import com.svt.transitions.Transition;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class PanelTransition extends JPanel implements Runnable {

    private final Object waitLock = new Object();
    private final Frame frame;
    private final Transition[] transitions;
    private final BufferedImage background;
    private final BufferedImage ovule;
    private int currentTransition;
    private int currentPoint;
    private int currentInterpolation;

    public PanelTransition(Frame frame, Element dialoguesRoot, Font font) {

        this.frame = frame;

        setFont(font);

        try (InputStream stream = ClassLoader.getSystemResourceAsStream("uterus.png")) {
            background = ImageIO.read(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (InputStream stream = ClassLoader.getSystemResourceAsStream("ovule_transition.png")) {
            ovule = ImageIO.read(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        NodeList transitionNodes = dialoguesRoot.getElementsByTagName("transition");

        transitions = new Transition[transitionNodes.getLength()];

        for (int i = 0; i < transitions.length; i++) {

            Element transition = (Element) transitionNodes.item(i);

            transitions[i] = Transition.parseXML(transition);
        }

        new Thread(this).start();
    }

    public void showTransition(int index) {
        currentTransition = index;
        currentPoint = 0;
        synchronized (waitLock) {
            waitLock.notify();
        }
    }

    @Override
    public void run() {

        int delay = 40;

        try {

            synchronized (waitLock) {
                waitLock.wait();
            }

            while (true) {

                if (currentTransition < transitions.length) {

                    Transition transition = transitions[currentTransition];
                    Point[] path = transition.path;

                    if (currentPoint < path.length) {

                        for (int i = 0; i < 15; i++) {

                            currentInterpolation = i;
                            repaint();
                            Thread.sleep(delay);
                        }

                        currentPoint++;
                    } else {

                        Thread.sleep(5000);
                        frame.showDialogue(currentTransition + 1);
                        synchronized (waitLock) {
                            waitLock.wait();
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

        Transition transition = transitions[currentTransition];

        Point current = transition.path[currentPoint];

        Point interpolated;

        if (currentPoint + 1 < transition.path.length) {

            Point next = transition.path[currentPoint + 1];

            int ix = current.x + ((next.x - current.x) * currentInterpolation) / 15;
            int iy = current.y + ((next.y - current.y) * currentInterpolation) / 15;

            interpolated = new Point(ix, iy);
        } else interpolated = current;

        System.out.println(interpolated);

        int scale = 2;

        int width = background.getWidth() * scale;
        int height = background.getHeight() * scale;

        Point origin = new Point(interpolated.x - getWidth() / 2, interpolated.y - getHeight() / 2);

        System.out.println(origin);

        if (origin.x < 0)
            origin.x = 0;
        if (origin.y < 0)
            origin.y = 0;
        if (origin.x + getWidth() > width)
            origin.x = width - getWidth();
        if (origin.y + getHeight() > height)
            origin.y = height - getHeight();

        System.out.println(origin);

        g.drawImage(background, -origin.x, -origin.y, width, height, null);

        int ovuleHeight = ovule.getHeight() * scale;
        int ovuleWidth = ovule.getWidth() * scale;
        g.drawImage(ovule, interpolated.x - origin.x - ovuleWidth / 2, interpolated.y - origin.y - ovuleHeight / 2, ovuleWidth, ovuleHeight, null);
    }
}
