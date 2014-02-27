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
    
    try {
      IPAddress = InetAddress.getByName(host);
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
  }
  
  public void connect(String userData) throws Exception {
     
     byte[] sendData = new byte[1024];
     byte[] receiveData = new byte[1024];     
     sendData = userData.getBytes();
     
     DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
     clientSocket.send(sendPacket);
     System.out.println("TO SERVER:" + sendPacket.getData());
     
     DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
     clientSocket.receive(receivePacket);
     
     String modifiedSentence = new String(receivePacket.getData());
     System.out.println("FROM SERVER:" + modifiedSentence);
     
     clientSocket.close();
  }
}
