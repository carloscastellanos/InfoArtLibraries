//
//  Traceroute.java
//  Traceroute
//
//  Created by Carlos Castellanos on 5/14/05.

package cc.infoart.net.traceroute;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
//import java.util.regex.*;

public class Traceroute implements Runnable, TraceTracker
{
    private String host;
    //private String output = null;
    List listenerList = new Vector();
    private boolean resolveHost = true;
    private Thread runner;
    private boolean stopthread = false;
	
    // constructor
    public Traceroute(String host)
    {
        this.host = host;
        this.resolveHost = true;
    }
	
    public Traceroute(String host, boolean resolve)
    {
        this.host = host;
        this.resolveHost = resolve;
    }
    
	// regex stuff
    //private static String REGEX = "(\\*)";
    //private static Pattern pattern;
    //private static Matcher matcher;
    
    public void run()
    {
        if(stopthread == false) {
            if(resolveHost) {
                doTraceroute();
            } else {
                doTracerouteIP();
            }
        }
    }

    public void startTraceroute()
    {
        // Start the traceroute thread
        runner = new Thread(this);
        runner.start();
        stopthread = false;
    }

    public void stopTraceroute()
    {
        runner = null;
        stopthread = true;
    }
    
    // traceroute with host names method
    private void doTraceroute() {
		
        Runtime runtime = Runtime.getRuntime();
        Process process = null;		
        String command = "traceroute " + host;
		 
        try
        {
            process = runtime.exec(command);
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
				
            //String output = null;
            // Read and return the output
            String line = null;
            String output = null;
            while((line = in.readLine()) != null)
            {
                output += line + "\n";

                //pattern = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
                //matcher = pattern.matcher(output);
        		
                /*
                if(matcher.find()) {
                    try {
                        in.close();
                        output += host + "\n";
                    } catch(IOException ioe) { }
                        break;
                }
                */
				
            }
            sendResults(output);
        } catch (Exception e)
        {
            System.err.println("Problem with traceroute" + e.getMessage());
        }			
    }
	
	
	// traceroute with only ip numbers method
    private void doTracerouteIP() {
		
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        String command = "traceroute -n " +  host;
				
        try
        {
            process = runtime.exec(command);
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
				
            //String output = null;
            // Read and return the output
            String line;
            String output = null;
            while((line = in.readLine()) != null)
            {
                output += line + "\n";
				
                //pattern = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
                //matcher = pattern.matcher(output);
        		
                /*
                if(matcher.find()) {
                try {
                    in.close();
                    output += host + "\n";
                } catch(IOException ioe) { }
                    break;
                }
                */
            }
            sendResults(output);
        }
        catch (Exception e)
        {
            System.err.println("Problem with traceroute" + e.getMessage());
        }
    } 
   
    public synchronized void addTraceOutputListener(TraceOutputListener tol)
    {
        //listenerList.add(tol);
        if(tol != null && listenerList.indexOf(tol) == -1) {
            listenerList.add(tol);
            System.out.println("[+] " + tol);
        }
    }
    
    public synchronized void removeTraceOutputListener(TraceOutputListener tol)
    {
        //listenerList.remove(tol);
        if(listenerList.contains(tol)) {
            listenerList.remove(listenerList.indexOf(tol));
            System.out.println("[-] " + tol);
        }
    }
    
    // let everyone know the traceroute was completed
    private synchronized void sendResults(String out)
    {
        if(listenerList == null) {
            return;
        } else {
            //System.out.println("Notifying all " + listeners.size() + " listeners...");
            ListIterator iter = listenerList.listIterator();
            while(iter.hasNext())
                ((TraceOutputListener) iter.next()).resultsReceived(out);
    		}
	}
    
} // end class Traceroute
