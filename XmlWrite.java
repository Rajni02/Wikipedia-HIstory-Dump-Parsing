import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by sam on 2/6/17.
 * This Code will break a Wiki History Dump into seperate pages and save individual pages.
 */
public class XmlWrite {
    public static void main(String[] args) throws IOException,XMLStreamException {
        FileWriter fw = null;
        FileReader page = new FileReader("pages142");
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = factory.createXMLEventReader(page);
        boolean flag = false;
        int no = 1;
        while(eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            switch (event.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    StartElement startElement = event.asStartElement();
                    String qName = startElement.getName().toString();
                    if (qName.equalsIgnoreCase("page") && flag==false)
                    {
                        flag = true;
                        fw = new FileWriter("pages_"+no);
                        event.writeAsEncodedUnicode(fw);
                    }
                    else if(flag == true)
                    {
                        event.writeAsEncodedUnicode(fw);
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    if (flag==true)
                    {
                        event.writeAsEncodedUnicode(fw);
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    EndElement endElement = event.asEndElement();
                    if (endElement.getName().getLocalPart().equalsIgnoreCase("page") && (flag==true))
                    {
                        event.writeAsEncodedUnicode(fw);
                        flag = false;
                        fw.close();
                        no=no+1;
                    }
                    else if(flag==true){
                        event.writeAsEncodedUnicode(fw);
                    }
                    break;
            }
        }
        System.out.println(no);

    }
}
