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

//
//  CarnivoreWrapper.java
//  CarnivoreWrapper
//
//  Created by Carlos Castellanos on 10/17/04.
//

package cc.infoart.net.carnivore;

//import java.util.*;
//import java.io.*;

public class CarnivoreWrapper {
    // Default connection info
    private static final String DEFAULT_IP = "127.0.0.1";
    private static final int DEFAULT_PORT = 6667;
    private String carnivoreIP;
    private int carnivorePort;
    //private String carnivoreDataFile = "carnivore_data.txt";
    //private String carnivoreCGIsource = "http://www.somedomain.com/cgi-bin/carnivore_client_zero.pl";
    //private CarniToOSC carni;
    //private int oscPort = 57000;
    //private String oscHost = "localhost";
    protected static CarnivoreListener carnivore;
    protected CarniOutputListener client;
    
    // Contructors
    public CarnivoreWrapper(CarniOutputListener client) {
        this.client = client;
        this.carnivoreIP = DEFAULT_IP;
        this.carnivorePort = DEFAULT_PORT;
    }
    
    public CarnivoreWrapper(String host, CarniOutputListener client) {
        this.carnivoreIP = host;
        this.carnivorePort = DEFAULT_PORT;
        this.client = client;
    }
    
    public CarnivoreWrapper(int port, CarniOutputListener client) {
        this.carnivoreIP = DEFAULT_IP;
        this.carnivorePort = port;
        this.client = client;
    }
    
    public CarnivoreWrapper(String host, int port, CarniOutputListener client) {
        this.carnivoreIP = host;
        this.carnivorePort = port;
        this.client = client;
    }
    

    // ===================================== //
	// Start and stop the Carnivore Listener

    public void startCarnivore() throws Exception
    {
        startCarnivore(false);
    }

    public void startCarnivore(boolean hex) throws Exception
    {
    
        System.out.print("Establishing connection to Carnivore server at " +  carnivoreIP + " on port " + carnivorePort + "... ");
        // init Carnivore Listner
        carnivore = new CarnivoreListener(carnivoreIP, carnivorePort);
        
        // process packet byte-by-byte if hex is true
        carnivore.processHexBytes = hex;
            
        try {
            carnivore.startListening();
            carnivore.addCarniOutputListener(this.client);
            System.out.println("adding CarniOutputListener...");
        } catch (Exception e) {
             System.out.println("Failed to establish a connection to the Carnivore server! " + e.getMessage());
        }
    }


    public void startCarnivore(String fileOrURL) throws Exception {
        // init Carnivore Listener
        carnivore = new CarnivoreListener();
        
        // check to see if it's a url or filename
        if(fileOrURL.startsWith("http://") || fileOrURL.startsWith("HTTP://")) {
            System.out.print("Establishing connection to Carnivore.  Data coming via url: " + fileOrURL);
            carnivore.setURL(fileOrURL);
        } else {
            System.out.print("Establishing connection to Carnivore.  Data coming from local file: " + fileOrURL);
            carnivore.setFile(fileOrURL);
        }
        
        try {
            carnivore.startListening();
            carnivore.addCarniOutputListener(this.client);
            System.out.println("adding CarniOutputListener...");
        } catch (Exception e) {
             System.out.println("Failed to establish a connection to the Carnivore server! " + e.getMessage());
        }
    }
    
    public void startCarnivore(String fileOrURL, boolean hex) throws Exception {
        // init Carnivore Listener
        carnivore = new CarnivoreListener();
        
        // check to see if it's a url or filename
        if(fileOrURL.startsWith("http://") || fileOrURL.startsWith("HTTP://")) {
            System.out.print("Establishing connection to Carnivore.  Data coming via url: " + fileOrURL);
            carnivore.setURL(fileOrURL);
        } else {
            System.out.print("Establishing connection to Carnivore.  Data coming from local file : " + fileOrURL);
            carnivore.setFile(fileOrURL);
        }
        
        // process packet byte-by-byte if hex is true
        carnivore.processHexBytes = hex;
            
        try {
            carnivore.startListening();
            carnivore.addCarniOutputListener(this.client);
            System.out.println("adding CarniOutputListener...");
        } catch (Exception e) {
             System.out.println("Failed to establish a connection to the Carnivore server! " + e.getMessage());
        }
    }

    
    public void stopCarnivore()
    {
        if (carnivore != null) {
            carnivore.stopListening();
            carnivore.removeCarniOutputListener(this.client);
            System.out.println("removing CarniOutputListener...");
        }
    }
   
   
   
}


