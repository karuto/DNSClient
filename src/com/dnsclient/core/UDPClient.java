package com.dnsclient.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPClient {
  static DatagramSocket clientSocket;
  static InetAddress IPAddress;
  static byte[] responseData;
  
  public static String host = "0.0.0.0";
  public static String targetDomain = "www.example.com";
  public static String queryType;
  public static int port;
  
  public UDPClient(String host, int port, String targetDomain, String queryType) {
    try {
      clientSocket = new DatagramSocket();
    } catch (SocketException e) {
      e.printStackTrace();
    }
    this.host = host;
    this.port = port;
    this.targetDomain = targetDomain;
    this.queryType = queryType;
    
    this.responseData = new byte[1024];
    
    try {
      IPAddress = InetAddress.getByName(host);
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
  }
  
  /*
   * The skeleton of this method is inspired from:
   * http://systembash.com/content/a-simple-java-udp-server-and-udp-client/
   */
  public byte[] connect(byte[] sendData) throws Exception {
     
     byte[] receiveData = new byte[1024];   
     
     DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
     clientSocket.send(sendPacket);
     
     DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
     clientSocket.receive(receivePacket);
     
     responseData = receivePacket.getData();
     clientSocket.close();
     
     return responseData;
     
  }
}
