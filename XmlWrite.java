import javax.xml.stream.*;
import javax.xml.stream.events.Characters;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by sam on 2/6/17.
 * This Code will break a Wiki History Dump into seperate pages and save individual pages.
 * Run using following flags to reset secure processing
 * java -DentityExpansionLimit=2147480000 -DtotalEntitySizeLimit=2147480000 -Djdk.xml.totalEntitySizeLimit=2147480000 XmlWrite
 *
 */
public class XmlWrite {
    public static void main(String[] args) {
    try {
        //FileWriter fw = null;
	StringBuilder text=null;
	long outer = System.nanoTime();
	String pg = "pages_";
	FileReader page = new FileReader("enwiki-1");
        XMLInputFactory factory = XMLInputFactory.newInstance();
	XMLOutputFactory factory1  = XMLOutputFactory.newInstance();
	XMLStreamWriter fw = null;
	FileWriter fr = null;
	//factory.setProperty(XMLConstants.FEATURE_SECURE_PROCESSING,false);
        XMLEventReader eventReader = factory.createXMLEventReader(page);
        boolean flag = false;
        int no = 1;
        while(eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
	    //System.out.println(event.toString());
	    //System.out.println(event.getEventType());
            switch (event.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    StartElement startElement = event.asStartElement();
                    String qname = startElement.getName().getLocalPart().toString();
		    //System.out.println(qname);
                    if (qname.equalsIgnoreCase("page") && flag==false)
                    {
			//text = new StringBuilder("");
                        flag = true;
			fr = new FileWriter(pg+no+".xml");
			fw = factory1.createXMLStreamWriter(fr);
			fw.writeStartDocument();
			fw.writeStartElement(qname);
			//fr.write('\n');
                        //fw = new FileWriter(pg+no);
                        //event.writeAsEncodedUnicode(fw);
                    }
                    else if(flag == true)
                    {
                        //event.writeAsEncodedUnicode(fr);
			fw.writeStartElement(qname);
			//fr.write('\n');
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    if (flag==true)
                    {
                        //event.writeAsEncodedUnicode(fr);
			Characters characters = event.asCharacters();
			//text.append(characters.getData());
			fw.writeCharacters(characters.getData());
			//fr.write(characters.getData());
			
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    EndElement endElement = event.asEndElement();
		    String qname1 = endElement.getName().getLocalPart().toString();
                    if (qname1.equalsIgnoreCase("page") && (flag==true))
                    {
                        //event.writeAsEncodedUnicode(fw);
                        flag = false;
			fw.writeEndElement();
			//fr.write('\n');
			fw.writeEndDocument();
			//fw.flush();
                        fw.close();
                        no=no+1;
			System.out.println(no);
                    }
                    else if(flag==true){
                        //event.writeAsEncodedUnicode(fw);
			//fw.writeCharacters(new String(text));
			fw.writeEndElement();
			//fr.write('\n');
                    }
                    break;
            }
        }
        System.out.println(no);
	long elap2 = System.nanoTime() - outer;
	System.out.println(elap2 / 1000000000.0);
	} catch (Exception e)
	{
	   e.printStackTrace();
	}
    }
}
