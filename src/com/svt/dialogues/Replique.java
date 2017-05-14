package com.svt.dialogues;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Replique {

    public final String[] phrases;
    public final Choix[] choix;
    public final int next;

    private Replique(Choix[] choix) {

        phrases = null;
        this.choix = choix;
        next = 0;
    }

    private Replique(String[] phrases, int next) {

        this.phrases = phrases;
        choix = null;
        this.next = next;
    }

    public static Replique parseXML(Element element) {

        if (element.hasAttribute("next")) {

            int next = Integer.parseInt(element.getAttribute("next"));

            NodeList phraseNodes = element.getElementsByTagName("phrase");

            String[] phrases = new String[phraseNodes.getLength()];

            for (int i = 0; i < phrases.length; i++) {

                Element phrase = (Element) phraseNodes.item(i);
                phrases[i] = phrase.getAttribute("texte");
            }

            return new Replique(phrases, next);
        } else {

            NodeList choixNodes = element.getElementsByTagName("choix");

            Choix[] choix = new Choix[choixNodes.getLength()];

            for (int i = 0; i < choix.length; i++) {

                Element choixNode = (Element) choixNodes.item(i);
                choix[i] = Choix.parseXML(choixNode);
            }

            return new Replique(choix);
        }
    }
}
