/*
// Copyright (C) 2005 by Carlos Castellanos
// 
// This program is free software; you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by the
// Free Software Foundation; either version 2 of the License, or (at your
// option) any later version.
// 
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
// Public License for more details.
 */

package cc.infoart.net.carnivore;

import java.io.*;
import java.net.*;
import java.util.ListIterator;
import java.util.StringTokenizer;
import java.util.Vector;

/*
============================================================================
==  CarnivoreListener
==
==  A Stream Listener that reads packets from Carnivore server, file, or URL.
== 
== portions of this code Copyright (C) 2003 by Mark Napier
== 
== 
============================================================================
*/

public class CarnivoreListener implements Runnable {

// Private data //

    // 3 ways to get data: "socket|url|file"
    private static final String DEFAULT_HOW = "socket";
    private String how;

    // For connecting to socket
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 6667;
    private String host;
    private int port;

    // For reading file
    private String filename = null;
    //String Filename = "carnivore_packets.txt";

    // For connecting to URL (ie. a CGI script)
    private String url = null;
    //String Url = "http://www.somedomain.com/cgi-bin/carnivore_client_zero.pl";

    // Stream stuff
    private Socket s;
    private URL dataURL = null;
    private URLConnection uc = null;
    private BufferedReader in;
    //private DataInputStream in;
    private PrintStream out;
    private boolean connected = false;
    private boolean stopthread = false;

    //private int bit = 0;
    // By default process hex values in packets
    // Set to true to process plain bytes
    protected boolean processHexBytes = false;

    // Runs the listener
    private Thread runner;
    
    // list of carni output listeners
    private static Vector listeners = new Vector();

    // ============================= //
    // CONSTRUCTORS
	// Set the host and port
	// Start the Carni listener.
    // Set the connection method.
    // ============================= //
	
    public CarnivoreListener() {
        setSocket(DEFAULT_HOST, DEFAULT_PORT);
    }
    
    public CarnivoreListener(String theHost, int thePort) {
        setSocket(theHost, thePort);
    }

    public CarnivoreListener(int thePort) {
        setSocket(DEFAULT_HOST, thePort);
    }
	
    public CarnivoreListener(String theHost) {
        setSocket(theHost, DEFAULT_PORT);
    }


    // ===================================================================== //
    // Process text lines from Carnivore Server
    // Customize this to change what happens with each line.
    // If you set processHexBytes = true, then the processLineHex() function
    // will break packet data into bytes, and will call processByte()
    // for each byte.
    //  ==================================================================== //

    private void processLine(String line)
    {
        if (processHexBytes) {
            processLineHex(line);
        }
        else {
            processLineRaw(line);
        }
    }

    // Send a line
    private void processLineRaw(String line)
    {
        showLine(line);
    }

    // Send a byte to the panel
    private void processByte(byte b)
    {
        showByte(b);
    }

    // Break packet into bytes and call processByte() for each
    private boolean processLineHex(String packet)
    {
        // Packet looks like:   "some header data, blah == 010f 2213 0020 bd01 00ae ..."
        // split line on "==",  right side of line has hex values
        int pos1 = packet.indexOf("==");
        if (pos1 < 0) {
            System.out.println("processLine: Never found '==' line=" + packet);
        }
        else {
            // parse list of hexvals.  each hexval is two bytes together (4 hex digits)
            String hexvals = packet.substring(pos1+2);
            StringTokenizer st = new StringTokenizer(hexvals," ");
            Integer i = null;
            String hexval="", hexval1="", hexval2;
            //short s = 0;
            byte b = 0;
            //System.out.println("ProcessLine: " + hexvals);
            while (st.hasMoreTokens()) {
                hexval = st.nextToken();
                if (hexval.length() >= 4 && !hexval.equals("(DF)")) {  // check valid token
                    hexval1 = "0x" + hexval.substring(0, 2);        // left byte
                    hexval2 = "0x" + hexval.substring(2, 4);        // rite byte
                    // byte 1
                    try {
                        i = Integer.decode(hexval1);
                        b = i.byteValue();
                        processByte(b);
                    }
                    catch (Exception e) {
                        message("Exception: HEX=" + hexval1 + " " + e);
                    }
                    // byte 2
                    try {
                        i = Integer.decode(hexval2);
                        b = i.byteValue();
                        processByte(b);
                    }
                    catch (Exception e) {
                        message("Exception: HEX=" + hexval2 + " " + e);
                    }
                }   // end of if token length >= 4
            }
        }
        return true;
    }


    private String currentLine = null;
    // This will be called if CarnivoreListener.processHexBytes = true
    private synchronized void showByte(byte b)
    {
        currentLine = "" + ((int)(b & 0xFF));
        notifyCarniListeners(currentLine);
    }

    // This is called if processing line by line (default)
    private synchronized void showLine(String line)
    {
        currentLine = line;
		//System.out.println("CarnivoreListener: " + currentLine);
        notifyCarniListeners(currentLine);
    }


    // accessor method
    // This is the method that will actually return carnivore data to the user
    //public synchronized String getOutput() {
    //    return currentLine;
    //}


    // accessor method return this carni listener's port
    public int getPort() {
        return port;
    }
    
    // accessor method return this carni listener's host
    public String getHost() {
        return host;
    }
    
    /////////////////////////////////////////////////////
    // Set connection type and params

    public void setURL(String sURL) {
        if (sURL != null && sURL != "") {
            url = sURL;
        }
        message("CarnivoreListener: Use URL " + url);
        how = "url";
    }

    public void setFile(String fname) {
        if (fname != null && fname != "") {
            filename = fname;
        }
        message("CarnivoreListener: Use FILE " + filename);
        how = "file";
    }

    public void setSocket(String h, int p) {
        if (h != null && h != "") {
            host = h;
        }
        if (p > 0) {
            port = p;
        }
        message("CarnivoreListener: Use Socket " + host + ":" + port);
        how = DEFAULT_HOW;
    }


    /////////////////////////////////////////////////////
    // Start/stop the listening loop

    public void startListening() {
        runner = new Thread(this);
        runner.start();
        stopthread = false;
    }


    public void stopListening() {
        runner = null;
        stopthread = true;
        disconnectFromServer();
    }


    /////////////////////////////////////////////////////
    // connect to the data source
    // may be a URL, file, or socket

    private boolean connectToServer() {
        connected = false;

        if (how.equals("url")) {
            // open URL
            message("CONNECT TO CGI URL: " + url);
            try {
                dataURL = new URL(url);
                uc = dataURL.openConnection();
                uc.connect();
                in = new BufferedReader(new InputStreamReader(uc.getInputStream()) );
                //in = new DataInputStream(s.getInputStream());
                connected = true;
            }
            catch (Exception e) {
                message("connect() (URL): " + e);
                connected = false;
            }
        }
        else if (how.equals("file")) {
            // open File
            message("CONNECT TO FILE: " + filename);
            try {
                FileInputStream FileIn = new FileInputStream(filename);
                in = new BufferedReader(new InputStreamReader(FileIn) );
                //in = new DataInputStream(s.getInputStream());
                connected = true;
            }
            catch (Exception e) {
                message("connect() (FILE): " + e);
                connected = false;
                return false;
            }
        }
        else {
            // default: open Socket
            message("Connecting to host " + host + ":" + port + "... ");
            try {
                // connect to server socket
                s = new Socket(host,port);
                // to read from server
                in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                //in = new DataInputStream(s.getInputStream());
                // to send to server
                out = new PrintStream(s.getOutputStream());
                message("Connection to " + host + ":" + port + " successful. ");
                connected = true;
            }
            catch (Exception e) {
                message("connect(): " + e);
                connected = false;
            }
        }
        // Got the connection.  If Socket, do hand-shake
        if (connected) {
            if (how.equals("url")) {
                System.out.println("CONNECTED TO CGI URL: " + dataURL);
            }
            else if (how.equals("file")) {
                System.out.println("CONNECTED TO FILE: " + filename);
            }
            else {
                int rand = (int)(Math.random()*999999);
                message("send user");
                sendToServer("USER bw" +rand+ "\n");
                message("send nick");
                sendToServer("NICK bw" +rand+ "\n");
                String ping = readFromServer();
                String pong = "PONG" + ping.substring(4)+"\n";
                message("read  " + ping);
                message("write " + pong);
                sendToServer(pong);
                message("write JOIN");
                //sendToServer("JOIN #hexivore\n");
                sendToServer("JOIN\n");
            }
        }
        return connected;
    }


    private String readFromServer() {
        String line = "nada";
        try {
            // get a line from the server
            line = in.readLine();
            message("from server: "+ line);
        }
        catch (Exception e) {
            message("Error when reading  " + host + " " + port + ": " + e);
        }
        return line;
    }


    private void sendToServer(String s) {
        if (out != null) {
            try {
                out.print(s);
                Thread.sleep(500);
            }
            catch (Exception e) {
                message("Error when sending: " + e);
            }
        }
    }


    private void disconnectFromServer() {
        message("disconnect");
        try {
            if (out != null) {
                out.println("QUIT");
                message("QUIT");
                out.close();
            }
            if (in != null) {
                in.close();
            }
        }
        catch (IOException e) {
            message("Error when closing inputStream: " + e);
        }
        connected = false;
    }


    //  ================================================ //
    // Thread runs in two states
    // 1) loop until connected to server
    // 2) read lines until thread stops or socket fails
    //    if socket fails, return to state 1.
    //  ================================================ //
    public void run() {
        String line;
        connected = false;
        message("run()");
        while (stopthread==false) {
            // Get connection
            while (connected==false && stopthread==false) {
                if (connectToServer() == false) {
                    try {
                        Thread.sleep(60000);  // retry in 60 secs
                    } catch (InterruptedException e) {
                        message("Interrupted: " + e);
                    }
                }
            }
            // Read lines from Carni Server
            try {
                //byte bb = 0;
                while (connected == true && stopthread == false && !Thread.interrupted()) {
                    line = in.readLine();
                    System.out.println("line=" + line + " " + line.length());
                    if (line == null) {
                    	   message("run() - line == null");
                        break;
                    }
                    processLine(line);
                    // Yield a little time for any display operations to work
                    Thread.sleep(10);
                }
            }
            catch (Exception e) {
                message("run() - Error when listening to " + host + " " + port + ": " + e);
                e.printStackTrace();
            }
            finally {
                message("run() Connection closed by server.");
                disconnectFromServer();
                // will loop back to connectToServer() to retry connection every 10 secs
            }
        }
    }


    private void message(String msg) {
        System.out.println("CarnivoreListener: " + msg);
    }

    public synchronized void addCarniOutputListener(CarniOutputListener col) {
        if(col != null && listeners.indexOf(col) == -1) {
            listeners.add(col);
            System.out.println("[+] " + col + " # of listeners=" + listeners.size());
        }
    }

    public synchronized void removeCarniOutputListener(CarniOutputListener col) {
        if(listeners.contains(col)) {
            listeners.remove(listeners.indexOf(col));
            System.out.println("[-] " + col + " # of listeners=" + listeners.size());
        }
    }

// let everyone know message was received
    private synchronized void notifyCarniListeners(String str) {
        if(listeners == null) {
            //System.out.println("CarnivoreListener: no listeners");
            return;
        } else {
            ListIterator iter = listeners.listIterator();
            System.out.println("Notifying all " + listeners.size() + " listeners..." + str);
            while(iter.hasNext()) {
                	((CarniOutputListener) iter.next()).messageReceived(this, str);
            	}
        }
    	}  
        
}

