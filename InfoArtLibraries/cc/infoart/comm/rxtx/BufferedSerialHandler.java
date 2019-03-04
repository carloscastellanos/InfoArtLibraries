/**
 * 
 */
package cc.infoart.comm.rxtx;

import gnu.io.SerialPort;

/**
 * @author carlos
 *
 */
public class BufferedSerialHandler extends BufferedSerialManager
{
    private boolean portOpen;
    private SerialDataEventListener serialClient;
    
    /**
     * Constructor
     */
    public BufferedSerialHandler()
    {
        super();
        //this.serialClient = serialClient;
        portOpen = false;
    }
    
    /**
     * opens a serial connection with the default databits (8), stop bits(1) and parity (none)
     * @param sPort the serial port
     * @param baud baud date
     * @param serial listener to trap serial events
     * @return true if the port was opened successfully.
     * @throws Exception
     */
    public boolean openStream(String sPort, int baud, SerialDataEventListener serial) throws Exception
    {
        // init serial port connection
        if(!(super.isPortOpen())) {
        		System.out.println("Establishing connection to serial port: " + sPort);
            try {
                if((portOpen = super.openPort(sPort, baud)) == true) {
                		System.out.println("Serial port "+ sPort + " is open");
                		this.serialClient = serial;
                		super.addSerialDataEventListener(serialClient);
            			// send an initial character in case the serial device is waiting
            			super.send("A");
            		}
            } catch (Exception e) {
                System.out.println("Failed to establish a connection to the serial port!\n" + e);
            }
        }
        return portOpen;
    }
    
    /**
     * opens a serial connection with the specified parameters
     * @param sPort the serial port
     * @param baud baud date
     * @param dataBits data bits
     * @param stopBits stop bits
     * @param parity parity
     * @param serial listener to trap serial events
     * @return true if the port was opened successfully.
     * @throws Exception
     */
    public boolean openStream(String sPort, int baud, int dataBits,
    					int stopBits, String parity, SerialDataEventListener serial) 
    			throws Exception
    {
        // data bits
        if(dataBits >= 8) {
            dataBits = SerialPort.DATABITS_8;
        } else {
            if(dataBits <= 5) {
                dataBits = SerialPort.DATABITS_5;
            } else {
                if(dataBits == 6) {
                    dataBits = SerialPort.DATABITS_6;
                } else {
                    dataBits = SerialPort.DATABITS_7;
                }
            }
        }
        
        // stop bits
        if(stopBits >= 2)
            stopBits = SerialPort.STOPBITS_2;
        else
            stopBits = SerialPort.STOPBITS_1;
        
        // parity
        int iparity;
        if(parity.equalsIgnoreCase("EVEN"))
            iparity = SerialPort.PARITY_EVEN;
        else if(parity.equalsIgnoreCase("MARK"))
            iparity = SerialPort.PARITY_MARK;
        else if(parity.equalsIgnoreCase("NONE"))
            iparity = SerialPort.PARITY_NONE;
        else if(parity.equalsIgnoreCase("ODD"))
            iparity = SerialPort.PARITY_ODD;
        else if(parity.equalsIgnoreCase("SPACE"))
            iparity = SerialPort.PARITY_SPACE;
        else
            iparity = SerialPort.PARITY_NONE;
        
        // init serial port connection
        if(!(super.isPortOpen())) {
        		System.out.println("Establishing connection to serial port: " + sPort);
            try {
                if((portOpen = super.openPort(sPort, baud, dataBits, stopBits, iparity)) == true) {
                		System.out.println("Serial port "+ sPort + " is open");
                		this.serialClient = serial;
                		super.addSerialDataEventListener(serialClient);
            			// send an initial character in case the microcontroller is waiting
            			super.send("A");
            		}
            } catch (Exception e) {
                System.out.println("Failed to establish a connection to the serial port!\n" + e);
            }
        }
        return portOpen;
    }
    
    /**
     * closes the serial connection
     * @return true if the port was closed.
     * @throws Exception
     */
    public boolean closeStream() throws Exception
    {
       if(super.isPortOpen()) {
           
           if(super.closePort()) {
               super.removeSerialDataEventListener(this.serialClient);
               portOpen = false;
           }
       }
       return !portOpen;
    }
    
}
