import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;

/**
 * Created by sam on 25/5/17.
 * This code will read each page and extact relevant field like revision id revision parentid text time and convert it into a ordered list and store the list in the form of object stream using serialization
 */
public class MakeData {
    private static final String regex ="\n\n";
    private static final String regex1 = "\\[\\[([\\w\\s\\-\\|])*\\]\\]";
    public static void main(String args[]) {

        boolean rev = false;
        boolean id = false;
        boolean tme = false;
        boolean pid = false;
        boolean txt = false;
        boolean flag = true;
        Pattern pat = Pattern.compile(regex);
        Pattern pat1 = Pattern.compile(regex1);
        long previous=0,current=0;
        int rid=0;
        int rpid=0;
        String time=null;
        StringBuilder text= null;
        List<List<String>>  all_links = null;
        Map<Integer,String> context = null;
        BufferedWriter wr=null;
        long epochs = -1;
        ArrayList<Revisions> revs = null;
        long outer = System.nanoTime();
        try {
             wr = new BufferedWriter(new FileWriter("testing.txt",true));
            String p = "../pages";
            for (int i = 88; i < 89; i++) {
                long inner = System.nanoTime();
                int links_count = 0;
                revs = new ArrayList<Revisions>();
                FileReader page = new FileReader(p+i);
                //System.out.println(p+i);
                XMLInputFactory factory = XMLInputFactory.newInstance();
                XMLEventReader eventReader =
                        factory.createXMLEventReader(page);
                while(eventReader.hasNext()) {
                    XMLEvent event = eventReader.nextEvent();
                    switch (event.getEventType()) {
                        case XMLStreamConstants.START_ELEMENT:
                            StartElement startElement = event.asStartElement();
                            String qName = startElement.getName().toString();
                            if (qName.equalsIgnoreCase("revision") && rev==false)

                            {
                                rev=true;
                                text = new StringBuilder("");
                                context = new TreeMap<Integer,String>();
                                all_links = new ArrayList<List<String>>();

                            }
                            else if(rev==true && qName.equalsIgnoreCase("id") && id==false)
                            {
                                if(flag) {
                                    id = true;
                                }
                                flag = false;
                            }
                            else if(rev==true && qName.equalsIgnoreCase("parentid") && pid==false)
                            {
                                pid = true;
                            }
                            else if(rev==true && qName.equalsIgnoreCase("timestamp") && tme==false)
                            {
                                tme = true;
                            }
                            else if(rev==true && qName.equalsIgnoreCase("text") && txt==false)
                            {
                                txt = true;
                            }
                            break;
                        case XMLStreamConstants.CHARACTERS:
                            //System.out.println("Here");
                            Characters characters = event.asCharacters();
                            if(id)
                            {
                                rid = Integer.parseInt(characters.getData());
                                id = false;
                            }
                            else if(pid)
                            {
                                rpid = Integer.parseInt(characters.getData());
                                pid = false;
                            }
                            else if(tme)
                            {
                                epochs = (new MyDate(characters.getData())).getEpoch();
                                tme = false;
                                current = epochs;
                            }

                            else if(txt)
                            {
                                text.append(characters.getData());
                                //text.append(event.toString());
                                //System.out.println(characters.getData());

                            }
                                break;
                        case  XMLStreamConstants.END_ELEMENT:
                            //System.out.println("Here");
                            EndElement endElement = event.asEndElement();
                            if (endElement.getName().getLocalPart().equalsIgnoreCase("text")) {
                                txt = false;
                                //System.out.println(txt);
                            } else if (endElement.getName().getLocalPart().equalsIgnoreCase("revision") && rev == true) {
                                rev = false;
                                flag = true;

                                List<String> links = null;

                                String[] items = pat.split(text);
                                for (int j = 0; j < items.length; j++) {
                                    context.put(j,items[j]);
                                    links = new ArrayList<String>();
                                    Matcher m = pat1.matcher(items[j]);
                                    while (m.find()) {

                                        links_count = links_count+1;
                                        String link = m.group();
                                        if (link.substring(2,link.length()-2).indexOf('|')==-1)
                                            links.add(link.substring(2,link.length()-2));
                                        else
                                        {
                                            String[] temp = link.substring(2,link.length()-2).split("\\|");
                                            links.add(temp[0]);
                                        }
                                    }
                                    all_links.add(links);
                                }
                                Revisions r = new Revisions(rid,rpid,epochs,context,all_links);
                                if (current>previous)
                                  {
                                    revs.add(r);
				    //System.out.println(r.getId()+ "  " + r.getParentid());

                                 }
                                previous = current;

                            }
                            break;

                    }


                }

                try {
                    FileOutputStream fr = new FileOutputStream(p+i+".ser");
                    ObjectOutputStream ob = new ObjectOutputStream(fr);
                    ob.writeObject(revs);
                    ob.close();
                    fr.close();

                } catch (IOException io){
                        io.printStackTrace();
                }
                Double elap1 = (System.nanoTime() - inner)/1000000000.0;
                wr.write(elap1.toString()+"\t"+Integer.toString(revs.size())+"\t"+Integer.toString(links_count));

                //System.out.println();
            }
            wr.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        long elap2 = System.nanoTime() - outer;
        //wr.write(new String("elap2 / 1000000000.0"+"\n"));
        System.out.println(elap2 / 1000000000.0);
    }
}
