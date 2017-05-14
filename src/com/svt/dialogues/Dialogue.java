package com.svt.dialogues;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Dialogue {

    public final Replique[] repliques;

    private Dialogue(Replique[] repliques) {
        this.repliques = repliques;
    }

    public static Dialogue parseXML(Element element) {

        NodeList repliqueNodes = element.getElementsByTagName("replique");

        Replique[] repliques = new Replique[repliqueNodes.getLength()];

        for (int i = 0; i < repliques.length; i++) {
            Element replique = (Element) repliqueNodes.item(i);

            repliques[i] = Replique.parseXML(replique);
        }

        return new Dialogue(repliques);
    }
}
