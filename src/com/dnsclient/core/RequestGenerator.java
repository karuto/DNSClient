package com.dnsclient.core;
import java.util.Random;

public class RequestGenerator {
  
  public static String targetDomain = "www.example.com";
  public static String queryType;
  
  private byte[] data;
  /* 8 bits = 1 byte; header is 12 bytes; */
  
  public RequestGenerator(String targetDomain, String queryType) {
    this.targetDomain = targetDomain;
    this.queryType = queryType;
    data = new byte[64];
    build();
  }
  
  
  private void build() {
    System.out.println("====== STARTING REQUEST BUILDER ======");
    System.out.println(this.targetDomain);
    System.out.println(this.queryType);
    
    /* HEADER */
    /* 16 bit Message ID */
    byte[] messageID = new byte[2];
    new Random().nextBytes(messageID);
    
    System.out.println("====== HEADER ======");
    printBitsFromByteArray(messageID);
    
  }
  
  
  /* Helper function to print bits from a byte array */
  private void printBitsFromByteArray(byte[] bytes) {
    for (byte b : bytes)
    {
      printBitsFromByte(b);
    }
  }
  
  
  /* Helper function to print bits from a single byte 
   * Credit: http://stackoverflow.com/questions/12310017/
   * how-to-convert-a-byte-to-its-binary-string-representation/12310078
   */
  private void printBitsFromByte(byte b) {
    String bits = String.format("%8s", 
        Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    System.out.println(bits);
  }
  
  
  
  
}

/*
 * 
  
  4. MESSAGES
  
  4.1. Format
  
  All communications inside of the domain protocol are carried in a single
  format called a message.  The top level format of message is divided
  into 5 sections (some of which are empty in certain cases) shown below:
  
      +---------------------+
      |        Header       |
      +---------------------+
      |       Question      | the question for the name server
      +---------------------+
      |        Answer       | RRs answering the question 
      +---------------------+ (not needed for request)
      |      Authority      | RRs pointing toward an authority
      +---------------------+ (not needed for request)
      |      Additional     | RRs holding additional information
      +---------------------+ (not needed for request)
   
 * 
 */