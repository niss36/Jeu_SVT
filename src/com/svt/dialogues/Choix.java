package com.svt.dialogues;

import org.w3c.dom.Element;

public class Choix {

    public final String texte;
    public final int next;

    private Choix(String texte, int next) {
        this.texte = texte;
        this.next = next;
    }

    public static Choix parseXML(Element element) {

        return new Choix(element.getAttribute("texte"), Integer.parseInt(element.getAttribute("next")));
    }
}
