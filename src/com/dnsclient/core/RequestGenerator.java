package com.dnsclient.core;
import java.nio.ByteBuffer;
import java.util.Random;

public class RequestGenerator {
  
  public static String targetDomain = "www.example.com";
  public static String queryType;
  
  private byte[] data;
  /* 8 bits = 1 byte; header is 12 bytes; */
  
  public RequestGenerator(String targetDomain, String queryType) {
    this.targetDomain = targetDomain;
    this.queryType = queryType;
    data = new byte[1024];
    
  }
  
  
  public byte[] build() {
    System.out.println("====== STARTING REQUEST BUILDER ======");
    System.out.println(this.targetDomain);
    System.out.println(this.queryType);
    
    /* HEADER SECTION */
    /* 16 bit Message ID, randomly generated */
    // TODO: record this message ID so that it can be parsed at response
    byte[] messageID = new byte[2];
    new Random().nextBytes(messageID);
    data[0] = messageID[0];
    data[1] = messageID[1];
    
    /* 16 bit second row in HEADER:
     * QR-1 OPCODE-4  AA-1  TC-1  RD-1  RA-1  0 0 0 RCODE-4
     * For request:
     * QR = 0
     * OPCODE = 0000
     * AA = 0
     * TC = 0
     * RD = 1
     * RA = 0
     * RCODE = 0000
     * Row data: 0 0000 000 | 0 000 0000 
     */
    data[2] = 0x01;
    data[3] = 0x00;

    /* 16 bit unsigned int QDCOUNT */
    data[4] = 0x00;
    data[5] = 0x01;
    
    /* 16 bit unsigned int ANCOUNT */
    data[6] = 0x00;
    data[7] = 0x00;
    
    /* 16 bit unsigned int NSCOUNT */
    data[8] = 0x00;
    data[9] = 0x00;
    
    /* 16 bit unsigned int ARCOUNT */
    data[10] = 0x00;
    data[11] = 0x00;
    
    /* QUESTION SECTION */
    /* QNAME */
    String[] domainParts = targetDomain.split("\\.");
    int k = 0;
    
    /* The size of this buffer is domain (before split) +2 because:
     * +1 for the length block (no. of chars) at beginning
     * +1 for the 0x00 end block
     */
    byte[] domainBuffer = new byte[targetDomain.length()+2];

    /* Read domain name content into QNAME buffer */
    for (int i = 0; i < domainParts.length; i++) {
      String part = domainParts[i];
      // Store the length (no. of chars) of domain segment first
      domainBuffer[i+k] = (byte) part.length();
      
//      int sum = i+k;
//      System.out.println("=== " + sum + " | " + part.length());
      
      for (char c : part.toCharArray()) {
        k++;
        // Store the hex representation of each character
        domainBuffer[i+k] = (byte) c;
        
//        System.out.println(String.format("%04x", (int) c));
//        sum = i+k;
//        System.out.println("===~~~ " + sum + " | " + i+k + " | " + (byte) c);
        
      }
    }
//    printBitsFromByteArray(domainBuffer, true);
    int bytesBuffer = domainBuffer.length;
    System.out.println(bytesBuffer);
    
    /* Transfer bytes in buffer to the data byte array */
    for (int i = 0; i < domainBuffer.length-1; i++) {
      data[12+i] = domainBuffer[i];
    }

    /* 16 bit unsigned int QTYPE */
    if (queryType.equalsIgnoreCase("A")) {
      data[12+bytesBuffer] = 0x00;
      data[12+bytesBuffer+1] = 0x01;
    } else {
      //TODO: Handle 8 other record types
    }
    
    
    /* QCLASS */
    data[12+bytesBuffer+2] = 0x00;
    data[12+bytesBuffer+3] = 0x01;
    
    
    System.out.println("====== HEADER ======");
    printBitsFromByteArray(data, false);
    return data;
  }
  
  
  /* Helper function to print bits from a byte array */
  private void printBitsFromByteArray(byte[] bytes, Boolean hasCounter) {
    for (int i = 0; i < bytes.length; i++) {
      if (hasCounter) {
        System.out.println("Byte #" + i);        
      }
      printBitsFromByte(bytes[i]);
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