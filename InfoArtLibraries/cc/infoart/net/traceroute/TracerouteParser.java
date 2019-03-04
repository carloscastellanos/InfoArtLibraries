//
//  TracerouteParser.java
//  Traceroute
//
//  Created by Carlos Castellanos on 5/14/05.

package cc.infoart.net.traceroute;

//import java.net.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;
import java.util.regex.*;

import cc.infoart.net.LookupHost;

public class TracerouteParser implements Runnable, TraceOutputListener, TraceParserTracker
{    
    
    // regex stuff
    /*
    private static String REGEX = "([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}){1}?\1([0-9]{1,3}\\.{0,1}[0-9]{0,3})\2([0-9]{1,3}\\.{0,1}[0-9]{0,3})\3([0-9]{1,3}\\.{0,1}[0-9]{0,3})\4)";
    */
    private static final String REGEX_IP = "([01]?[0-9][0-9]?|2[0-4][0-9]|25[0-5])\\.([01]?[0-9][0-9]?|2[0-4][0-9]|25[0-5])\\.([01]?[0-9][0-9]?|2[0-4][0-9]|25[0-5])\\.([012]?[0-9][0-9])";
    private static final String REGEX_DELAYS = "([0-9]*\\.[0-9]*(?= ms))";
    private Pattern patternIP;
    private Matcher matcherIP;
    private Pattern patternDelay;
    private Matcher matcherDelay;
    private boolean found;
        
    private Integer[] b = null;  
    private int count = 0;  
    private String host = null;
    
    // data structures to hold parsed traceroute results
    private List probeDelays = new Vector(3);
    private Map[] resultsHashArray = new Hashtable[30]; // default is 30 hops
    private List ipInts = new Vector(4);
    
    private List parseListeners = new Vector();
        
    //int[] ipInt = new int[4];
    
    protected Traceroute tr;
    private boolean resolveHost = true;
    
    private Thread runner;
    private boolean stopthread = false;
        
    // Constructor
    public TracerouteParser(String host)
    {
        this.host = host;
    }
    
    public TracerouteParser(String host, boolean resolve)
    {
        this.host = host;
        this.resolveHost = resolve;
    }
    
    public void resultsReceived(String s)
    {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new StringReader(s));
            String line = null;
            while((line = br.readLine()) != null)
            {	
                //pout.println(line);
                // set-up regex pattern & matcher objects
                // and call method to process regex
                patternIP = Pattern.compile(REGEX_IP, Pattern.CASE_INSENSITIVE);
                matcherIP = patternIP.matcher(line);
                patternDelay = Pattern.compile(REGEX_DELAYS, Pattern.CASE_INSENSITIVE);
                matcherDelay = patternDelay.matcher(line);
                processRegex();
                count++;
            }
                
        } catch(IOException ioe) {
           System.out.println("Problem reading tr results " + ioe.getMessage());
        }
		
        try {
            if(br != null) {
                br.close();
            }
        } catch(IOException ioe) { }
    
        this.stopParser();
    }
    
    public void startParser()
    {
        // Start the traceroute thread
        runner = new Thread(this);
        runner.start();
        stopthread = false;
    }
    
    public void stopParser()
    {
        if (tr != null) {
            // remove ourselves from Traceroute's list of listeners
            tr.removeTraceOutputListener(this);
        }
        runner = null;
        stopthread = true;
    }
    
    public void run()
    {
        if (host != null) {
		
            try {
				
                // do traceroute
                String trResult = null;
                if(resolveHost == false) {
                    // lookup host
                    String lookupResult = LookupHost.lookup(host);
                    System.out.println("host " + host +"=" + lookupResult + "\n");
                    tr = new Traceroute(lookupResult, false);
                    //trResult = tr.doTracerouteIP();
                } else {
                    tr = new Traceroute(host, true);
                    //trResult = tr.doTraceroute();
                }
                tr.startTraceroute();
                // add ourselves to Traceroute's list of listeners
                tr.addTraceOutputListener(this);
			
                } catch(Exception e) {
                   System.out.println("Problem looking starting traceroute " + e.getMessage());
                }
        } else {
           System.out.println("You must enter a host name!");
        }

                
    } // end run() method

	
    // process regex
    private void processRegex()
    {
        while(matcherIP.find()) {
            String ip = matcherIP.group();
            b = convertBytes(ip.getBytes());
            resultsHashArray[count] = new Hashtable(3);
            resultsHashArray[count].put("ip", ip);
            System.out.println("IP=" + ip);

            String probe1Delay = matcherDelay.group(1);
            String probe2Delay = matcherDelay.group(2);
            String probe3Delay = matcherDelay.group(3);
            probeDelays.add(new Integer(Integer.parseInt(probe1Delay)));
            probeDelays.add(new Integer(Integer.parseInt(probe2Delay)));
            probeDelays.add(new Integer(Integer.parseInt(probe3Delay)));
            resultsHashArray[count].put("probeDelays", probeDelays);
            System.out.println("probe delays: " +  probe1Delay + " " + probe2Delay + " " + probe3Delay);
                
            //resultsHashArray[count].put("ipInts", b);
            
            for(int i = 0; i < b.length; i++) {
                ipInts.set(i, b[i]);
            }
            resultsHashArray[count].put("ipInts", ipInts);
                
            found = true;
        }

        if(!found)
           System.out.println("No match found!");
    }
    
    private Integer[] convertBytes(byte[] b)
    {
        Integer[] temp = new Integer[b.length];
        for (int i=0;i<b.length;i++) {
            if (b[i]<0) 
                temp[i]=new Integer(b[i]+256);
            else
                temp[i]=new Integer(b[i]);
            }
        return temp;
    }
    
    public synchronized void addTPOutputListener(TPOutputListener tpol)
    {
        //listenerList.add(tpol);
        if(tpol != null && parseListeners.indexOf(tpol) == -1) {
            parseListeners.add(tpol);
            System.out.println("[+] " + tpol);
        }
    }
    
    public synchronized void removeTPOutputListener(TPOutputListener tpol)
    {
        //listenerList.remove(tpol);
        if(parseListeners.contains(tpol)) {
            parseListeners.remove(parseListeners.indexOf(tpol));
            System.out.println("[-] " + tpol);
        }
    }
    
    // let everyone know the traceroute results were parsed
    private synchronized void sendResults(Map m)
    {
        if(parseListeners == null) {
            return;
        } else {
            //System.out.println("Notifying all " + listeners.size() + " listeners...");
            ListIterator iter = parseListeners.listIterator();
            while(iter.hasNext())
                ((TPOutputListener) iter.next()).resultsReceived(resultsHashArray);
    		}
	}

}
