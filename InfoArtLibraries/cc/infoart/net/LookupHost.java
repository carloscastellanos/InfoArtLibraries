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

package cc.infoart.net;

import java.net.*;
import java.io.*;

public class LookupHost {


  public static void main (String[] args) {

    if (args.length > 0) { // use command line
      for (int i = 0; i < args.length; i++) {
        System.out.println(lookup(args[i]));
      }
    }
    else {
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      System.out.println("Enter names and IP addresses. Enter \"exit\" to quit.");
      try {
        while (true) {
          String host = in.readLine();
          if (host.equalsIgnoreCase("exit") || host.equalsIgnoreCase("quit")) {
            break;
          }
          System.out.println(lookup(host));
        }
      }
      catch (IOException e) {
        System.err.println(e);
      }

   }

  } // end main //


  public static String lookup(String host) {

    InetAddress thisComputer;
    byte[] address;

    // get the bytes of the IP address
    try {
      thisComputer = InetAddress.getByName(host);
      address = thisComputer.getAddress();
    }
    catch (UnknownHostException e) {
      return "Cannot find host " + host;
    }

    if (isHostname(host)) {
      // Print the IP address
      String dottedQuad = "";
      for (int i = 0; i < address.length; i++) {
        int unsignedByte = address[i] < 0 ? address[i] + 256 : address[i];
        dottedQuad += unsignedByte;
        if (i != address.length-1) dottedQuad += ".";
      }
      return dottedQuad;
    }
    else {  // this is an IP address so print the domain name
      return thisComputer.getHostName();
    }

  }  // end lookup

  private static boolean isHostname(String host) {

    char[] ca = host.toCharArray();
    // if we see a character that is neither a digit nor a period
    // then host is probably a hostname
    for (int i = 0; i < ca.length; i++) {
      if (!Character.isDigit(ca[i])) {
        if (ca[i] != '.') return true;
      }
    }

    // Everything was either a digit or a period
    // so host looks like an IP address in dotted quad format
    return false;

   }  // end isHostName

 } // end HostLookup
