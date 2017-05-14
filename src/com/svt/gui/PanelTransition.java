package com.svt.gui;

import org.w3c.dom.Element;

import javax.swing.*;
import java.awt.*;

public class PanelTransition extends JPanel {

    private final Frame frame;

    public PanelTransition(Frame frame, Element dialoguesRoot, Font font) {

        this.frame = frame;

        setFont(font);
    }
}
