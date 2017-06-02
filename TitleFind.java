import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileReader;

/**
 * Created by sam on 25/5/17.
 * This code will extract tiles of all pages extracted and saved by XmlWrite and test3.py
 */


public class TitleFind {
    public static void main(String[] args)
    {
        boolean title = false;
        try {
            String p = "pages";
            for (int i = 2; i < 10; i++) {
                boolean next = true;
                FileReader page = new FileReader(p+i);
                //System.out.println(p+i);
                XMLInputFactory factory = XMLInputFactory.newInstance();
                XMLEventReader eventReader =
                        factory.createXMLEventReader(page);
                while(eventReader.hasNext()) {
                    XMLEvent event = eventReader.nextEvent();
                    switch (event.getEventType()) {
                        case XMLStreamConstants.START_ELEMENT:
                            //System.out.println("Here");
                            StartElement startElement = event.asStartElement();
                            String qName = startElement.getName().toString();
                           // System.out.println(qName);
                            if (qName.equalsIgnoreCase("title"))

                            {
                                //System.out.println(qName);
                              title=true;
                            }
                            break;
                        case XMLStreamConstants.CHARACTERS:
                            //System.out.println("Here");
                            Characters characters = event.asCharacters();
                            if(title)
                                System.out.println(characters.getData());
                            break;
                        case  XMLStreamConstants.END_ELEMENT:
                            //System.out.println("Here");
                            EndElement endElement = event.asEndElement();
                            if(endElement.getName().getLocalPart().equalsIgnoreCase("title") && title==true){
                                  next = false;
                                  title=false;


                            }
                            break;
                    }
                    if (!next) {
                        page.close();
                        break;
                    }
                }



            }
        }
        catch(Exception e){
                System.out.println(e.getMessage());
            }

    }
}
