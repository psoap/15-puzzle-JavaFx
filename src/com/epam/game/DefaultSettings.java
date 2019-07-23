package com.epam.game;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public final class DefaultSettings {
    private final String PATH = "com/epam/game/resources/settings.xml";
    private Integer size;
    private Locale lang;
    
    public void load(){
        Locale buffLocale = new Locale("en");
        int buffSize = 4;
        try {
            InputStream defaultXML = DefaultSettings.class.getClassLoader()
                                        .getResourceAsStream(PATH);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(defaultXML);
            Node nodeXML = doc.getDocumentElement();
            Element el = (Element) nodeXML;
            buffSize = Integer.parseInt(el.getElementsByTagName("size").item(0).getTextContent());
            buffLocale = new Locale(el.getElementsByTagName("lang").item(0).getTextContent());
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            ex.printStackTrace();
        } finally {
            size = buffSize;
            lang = buffLocale;
        }
    }

    public Integer getSize() {
        return size;
    }

    public Locale getLang() {
        return lang;
    }
}
