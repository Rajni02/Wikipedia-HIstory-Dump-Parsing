import javax.xml.stream.*;
import javax.xml.stream.events.Characters;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by sam on 2/6/17.
 * This Code will break a Wiki History Dump into seperate pages and save individual pages based on whether it is present
 * in a Target dictionary.
 */
public class XmlWrite {
    public static void main(String[] args) {
    try {
        //FileWriter fw = null;
        FileWriter tar = new FileWriter("Junk.txt");
        BufferedWriter br1 = new BufferedWriter(tar);
        StringBuilder text=null;
	HashMap<String,Integer> pgs = new HashMap<String, Integer>();
	long outer = System.nanoTime();
	String pg = "pages_";
	FileReader page = new FileReader("enwiki");
        XMLInputFactory factory = XMLInputFactory.newInstance();
	XMLOutputFactory factory1  = XMLOutputFactory.newInstance();
	XMLStreamWriter fw = null;
	FileWriter fr = null;
	boolean writes = true;
	try {
        FileReader target = new FileReader("pages.txt");  // Database of CS Pages
        BufferedReader br = new BufferedReader(target);
        String tempo = null;
        while((tempo=br.readLine())!=null)
        {
            tempo = tempo.replaceAll("_"," ");  // Because in the pages space is replaced by _ character
            pgs.put(tempo,1);
        }

    } catch (Exception e)
    {
        e.printStackTrace();
    }
	//factory.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING,false);
	//factory1.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING,false);
        XMLEventReader eventReader = factory.createXMLEventReader(page);
        boolean flag = false;
        boolean flag1 = false;
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
			//fw.writeStartDocument();
			fw.writeStartElement(qname);
			//fr.write('\n');
                        //fw = new FileWriter(pg+no);
                        //event.writeAsEncodedUnicode(fw);
                    }
                    else if(qname.equalsIgnoreCase("title") && flag1==false && flag == true)
                    {
                        flag1 = true;
                        fw.writeStartElement(qname);
                    }

                    else if(flag == true && writes == true)
                    {
                        //event.writeAsEncodedUnicode(fr);
			            fw.writeStartElement(qname);
			//fr.write('\n');
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    if (flag1 == true)
                    {
                        Characters characters = event.asCharacters();
                        if (pgs.containsKey(characters.getData()) == false)
                        {
                            writes = false;
                            //fw.writeCharacters(characters.getData());
                            br1.write(Integer.toString(no));
                            br1.newLine();
                            br1.flush();
                        }
                        fw.writeCharacters(characters.getData());
                        System.out.println(no+"\t"+characters.getData());
                    }
                    else if (flag==true) {
                        //event.writeAsEncodedUnicode(fr);
                        Characters characters = event.asCharacters();
                        if (writes) {
                            //text.append(characters.getData());
                            fw.writeCharacters(characters.getData());
                        }
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
                        writes = true;
			    fw.writeEndElement();
			    //fr.write('\n');
			    //fw.writeEndDocument();
			//fw.flush();
                        fw.close();
                        no=no+1;
			//System.out.println(no);
                    }
                    else if(qname1.equalsIgnoreCase("title") && (flag1 == true))
                    {
                            flag1 = false;
                            fw.writeEndElement();

                    }

                    else if (flag==true && writes == true){
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
	br1.close();
	tar.close();
	//System.out.println(elap2 / 1000000000.0);
	} catch (Exception e)
	{
	   e.printStackTrace();
	}	
    }
}
