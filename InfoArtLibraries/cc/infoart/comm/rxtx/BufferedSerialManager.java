package cc.infoart.comm.rxtx;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.ListIterator;
import java.util.TooManyListenersException;

/**
 * 
 */

/**
 * @author carlos
 *
 */
public class BufferedSerialManager implements SerialPortEventListener
{
	 private SerialPort mySerialPort = null;    
	 private BufferedInputStream in;	// input data from port
	 private OutputStream out; // output data to port
	 private boolean portOpen;	// status of the port
	 //  holds the list of SerialDataEventListener objects
	 private static ArrayList listeners = new ArrayList();
	 
		/*
	  * 	Constructors.  Set status and speed of port and initlialize list of listeners
	  */
	 
	 
	 /**
	  * Initializes a SerialManager
	  */
	 public BufferedSerialManager()
	 {
	     portOpen = false;
	 }
	 
	 /**
	  * Opens a serial port with default data bits (8), stop bits(1)
	  * and parity (none)
	  * @param whichPort the serial port to open
	  * @param whichSpeed the baud rate
	  * @return true if a port was successfully opened, false otherwise
	  */ 
	 public boolean openPort(String whichPort, int whichSpeed)
	 {
	     try {
	         //find the port
	         CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(whichPort);
	         //try to open the port
	         mySerialPort = (SerialPort) portId.open("Serial Port" + whichPort, 2000);
	         //configure the port
	         try {
	             mySerialPort.setSerialPortParams(whichSpeed,
	                     SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
	                     SerialPort.PARITY_NONE);
	         } catch (UnsupportedCommOperationException e) {
	             System.out.println("Comm parameter error: Probably an unsupported speed");
	         }
	         //establish streams for reading and writing to the port
	         try {
	             in = new BufferedInputStream(mySerialPort.getInputStream());
	             out = mySerialPort.getOutputStream();
	             System.out.println("Input and output streams established for serial I/O");
	         } catch (IOException e) {
	             System.out.println("couldn't establish streams for serial I/O");
	         }
	         // add a listener to trap serial events:
	         try {
	             mySerialPort.addEventListener(this);
	             mySerialPort.notifyOnDataAvailable(true);
	             System.out.println("SerialManager: listening for serial events at a " +
	             		"baud rate of " + mySerialPort.getBaudRate() +  "...");
	         } catch (TooManyListenersException e) {
	             System.out.println("couldn't add serial I/O listener");
	         }

	         // port successfully opened; set portOpen to true
	         portOpen = true;
	         return portOpen;
	         
	     } catch (Exception e) {
	         // if we couldn't open the port, assume it's in use:
	         System.out.println("Port in use or does not exist");
	         System.out.println(e.getMessage());
	         e.printStackTrace();
	         return portOpen;
	     }
	 }
	 
	 /**
	  * Opens a serial port with the specified parameters
	  * @param whichPort the serial port to open
	  * @param whichSpeed the baud rate
	  * @param dataBits the number of data bits
	  * @param stopBits the number of stop bits
	  * @param parity the parity
	  * @return true if a port was successfully opened, false otherwise
	  */
	 public boolean openPort(String whichPort, int whichSpeed, int dataBits, int stopBits, int parity)
	 {
	     try {
	         //find the port
	         CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(whichPort);
	         //try to open the port
	         mySerialPort = (SerialPort) portId.open("Serial Port" + whichPort, 2000);
	         //configure the port
	         try {
	             mySerialPort.setSerialPortParams(whichSpeed, dataBits, stopBits, parity);
	         } catch (UnsupportedCommOperationException e) {
	             System.out.println("Comm parameter error: Probably an unsupported speed");
	         }
	         //establish streams for reading and writing to the port
	         try {
	        	 	in = new BufferedInputStream(mySerialPort.getInputStream());
	        	 	out = mySerialPort.getOutputStream();
	             System.out.println("Input and output streams established for serial I/O");
	         } catch (IOException e) {
	             System.out.println("couldn't establish streams for serial I/O");
	         }
	         // add a listener to trap serial events:
	         try {
	             mySerialPort.addEventListener(this);
	             mySerialPort.notifyOnDataAvailable(true);
	             System.out.println("SerialManager: listening for serial events at a " +
	             		"baud rate of " + mySerialPort.getBaudRate() +  "...");
	         } catch (TooManyListenersException e) {
	             System.out.println("couldn't add serial I/O listener");
	         }

	         // port successfully opened; set portOpen to true
	         portOpen = true;
	         return portOpen;
	         
	     } catch (Exception e) {
	         // if we couldn't open the port, assume it's in use:
	         System.out.println("Port in use or does not exist");
	         System.out.println(e.getMessage());
	         e.printStackTrace();
	         return portOpen;
	     }
	 }
	 
		/**
		 * Closes the port and streams, and disposes
		 * @return true if the port was closed.
		 *  
		 */
		public boolean closePort()
		{
		    in = null;
		    out = null;
		    mySerialPort.close();
		    portOpen = false;
		    return !portOpen;
		}

		/**
		 * @return an Arraylist of the serial ports 
		 */
		public ArrayList getPorts()
		{
		    // Set up an ArrayList to store the ports:
	     ArrayList portsAvailable = new ArrayList();
	     Enumeration portList = CommPortIdentifier.getPortIdentifiers();
	     
	     // count the ports:
	     int numberOfPorts = 0;
	     
	     // print out a header for the list of ports:
	     System.out.println("No.\tSerial port\t\tOwner");
	     
	     // print out the list of ports:
	     while (portList.hasMoreElements()) {
	         
	         // get all the ports available:
	         CommPortIdentifier portId = (CommPortIdentifier) portList.nextElement();
	         
	         // if they're serial ports, add them to the Arraylist
	         // and print them out:
	         if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
	             portsAvailable.add(portId.getName());
	             System.out.println(numberOfPorts + "\t" +portId.getName() + "\t\t"
	                     + portId.getCurrentOwner());
	             // increment number of ports:
	             numberOfPorts++;
	         }
	     }
	     // return the ArrayList:
	     return portsAvailable;
	 }
	 
		
		/*
		 * ==================================================================
		 * These two send() methods need to be updated to reflect the changes
		 * that my be made at runtime to the writeBufferSize field
		 * ==================================================================
		 */

	 /**
	  * Sends a byte out the serial port: 
	  */   
		public void send(int outputByte)
		{
		    try {
	         out.write(outputByte);
		    } catch (IOException e) {
	         System.out.println("error: couldn't send byte");
	     }
	 }
		
	 /**
	  * Sends a String out the serial port: 
	  */   
	 public void send(String stringToSend)
	 {
	     try {
	         out.write(stringToSend.getBytes());
	     } catch (IOException e) {
	         System.out.println("error: couldn't send string");
	     }       
	 }
	 
	 /**
	  * @return a boolean indicating whether or not the port is open.
	  */
	 public boolean isPortOpen()
	 {
	     return portOpen;
	 }
	 
	 // SerialEventTracker interface implementations
	 /**
	  * adds a listener to trap serial data events from this SerialManagaer
	  */
	 public void addSerialDataEventListener(SerialDataEventListener sdel)
	 {
	     if(sdel != null && listeners.indexOf(sdel) == -1) {
	         listeners.add(sdel);
	         System.out.println("[+ SerialDataEventListener] " + sdel);
	     }
	 }

	 /**  
	  * removes a listener from this SerialManagaer
	  */
	 public void removeSerialDataEventListener(SerialDataEventListener sdel)
	 {
	     if(listeners.contains(sdel)) {
	         listeners.remove(listeners.indexOf(sdel));
	         System.out.println("[- SerialDataEventListener] " + sdel);
	     }
	 }
	 
		/**
		 *  Reads the latest byte off the input buffer when it comes in 
	  */
		public void serialEvent(SerialPortEvent event)
		{
		    if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
		    		try {
		    			int avail = in.available();
		    			int[] intsarr = new int[avail];
		    			// read all available data from buffer
		    			for(int x=0; x < avail; x++) {
		    				intsarr[x] = in.read();
		    			}
		             // send out the data
		             notifySerialDataEventListeners(intsarr);
		        } catch (IOException e) {
		            System.out.println(e);
		        }          
		    }
		}
		
		/**
		 * let everyone know a serial data event was received
		 */
		private void notifySerialDataEventListeners(int[] data)
		{
		    if(listeners == null) {
		        return;
	     } else {
	         ListIterator iter = listeners.listIterator();
	         while(iter.hasNext()) {
	             	((SerialDataEventListener) iter.next()).serialDataEvent(data);
	         	}
	     }
	 	}  


}
