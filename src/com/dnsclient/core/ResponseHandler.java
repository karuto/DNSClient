package com.dnsclient.core;

import java.util.ArrayList;
import java.util.HashMap;

public class ResponseHandler {

  private static int numQuestions;
  private static int numAnswers;
  private static int numAuthority;
  private static int numAdditional;
  private static HashMap nameMap;
  private static String targetDomain;
  private DataHelper helper = null;
  byte[] data;
  
  public ResponseHandler(byte[] responseData, String targetDomain) {
    this.data = responseData;
    this.targetDomain = targetDomain;
    this.helper = DataHelper.getInstance();
    this.nameMap = new HashMap();
    parseResponse();
  }
  
  protected void parseResponse() {
    if (helper.getBitFromByte(data[3],0) == '1') {
      // Valid DNS response, keep parsing
      
      // Store the number of questions and responses records
      String nQue = "" + helper.getBitsFromByte(data[4]) + 
          helper.getBitsFromByte(data[5]);
      numQuestions = Integer.parseInt(nQue, 2);
      
      String nAns = "" + helper.getBitsFromByte(data[6]) + 
          helper.getBitsFromByte(data[7]);
      numAnswers = Integer.parseInt(nAns, 2);
      
      String nAut = "" + helper.getBitsFromByte(data[8]) + 
          helper.getBitsFromByte(data[9]);
      numAuthority = Integer.parseInt(nAut, 2);
      
      String nAdd = "" + helper.getBitsFromByte(data[10]) + 
          helper.getBitsFromByte(data[11]);
      numAdditional = Integer.parseInt(nAdd, 2);
      
      System.out.println("Questions: " + numQuestions + " Answers RRs: " + 
          numAnswers + " Authority RRs: " + numAuthority + 
          " Additional RRs: " + numAdditional);
      
      
      /* QUESTION SECTION */
      // Loop until reaching first zero byte, which is end of QNAME's domain
      int dataCursor = 12;
      while (!helper.isZeroByte(data[dataCursor])) {
//        System.out.print((char) data[dataCursor]);
        dataCursor++;
      }
      dataCursor++; // +1 for empty end block
      dataCursor += 2; // +2 for QTYPE
      dataCursor += 2; // +2 for QCLASS
      /* Cursor now points at the first byte (beginning) of answer section */
      
      /*
      System.out.println();
      System.out.println(dataCursor); // i represents the last byte of domain name
      helper.printBitsFromByteArray(data, 23, 40); // byte[24] is where it ends (endblock)
      */
      
      ArrayList<DNSAnswer> results = new ArrayList<DNSAnswer>();
      
      
      /* ANSWER SECTION */
      int answerCursor = 0;
      int answerCursorTotal = 0;
      
      for (int i = 4; i < numAnswers; i++) {
        DNSAnswer answer = new DNSAnswer();
        /* ANSWER NAME */
        if (helper.getBitFromByte(data[dataCursor], 0) =='1' && 
            helper.getBitFromByte(data[dataCursor], 1) == '1') {
          // This indicates the name field is a pointer
          // Replace first 2 bits of '11', cast to int to find pointer index
          String name = "" + helper.getBitsFromByte(data[dataCursor]) + 
              helper.getBitsFromByte(data[dataCursor+1]);
          int pointer = Integer.parseInt("00" + name.substring(2), 2);
          System.out.println(pointer);
          System.out.println(retrieveName(pointer));
          // TODO CHANGE THIS TO REAL
          answer.setName(String.valueOf(pointer));
        } else {
          // This indicates the name field is not pointer
          
        }
        dataCursor += 2; /* 2 bytes for NAME */

        /* ANSWER TYPE */
        String aClass = "" + helper.getBitsFromByte(data[dataCursor]) + 
            helper.getBitsFromByte(data[dataCursor+1]);
        int aClass_int = Integer.parseInt(aClass, 2); // I guess I don't have to
        
        if (helper.getBitsFromByte(data[dataCursor+1]).
            equalsIgnoreCase("00000001")) { // 0x01, A
          answer.setType("A");
        } else if (helper.getBitsFromByte(data[dataCursor+1]).
            equalsIgnoreCase("00000010")) { // 0x02, NS
          answer.setType("NS");
        } else if (helper.getBitsFromByte(data[dataCursor+1]).
            equalsIgnoreCase("00000101")) { // 0x05, CNAME
          answer.setType("CNAME");
        }
        dataCursor += 2; /* 2 bytes for TYPE */
        
        /* ANSWER CLASS */
        if (helper.getBitsFromByte(data[dataCursor+1]).
            equalsIgnoreCase("00000001")) { // 0x01, IN
          answer.setDnsClass("IN");
        }
        dataCursor += 2; /* 2 bytes for CLASS */
        
        /* ANSWER TTL */
        String ttl = "" + helper.getBitsFromByte(data[dataCursor]) + 
            helper.getBitsFromByte(data[dataCursor+1]) + 
            helper.getBitsFromByte(data[dataCursor+2]) + 
            helper.getBitsFromByte(data[dataCursor+3]);
        answer.setTtl(String.valueOf(Integer.parseInt(ttl, 2)));
        dataCursor += 4; /* 4 bytes for TTL */
        
        /* ANSWER RLENGTH */
        String rlength = "" + helper.getBitsFromByte(data[dataCursor]) + 
            helper.getBitsFromByte(data[dataCursor+1]);
        int rlength_int = Integer.parseInt(rlength, 2);
        answer.setDataLength(String.valueOf(rlength_int));
        System.out.println(answer.toString());
        dataCursor += 2; /* 4 bytes for TTL */
        
        /* ANSWER RDATA */
        dataCursor += rlength_int;
        
        results.add(answer);
      }

      
      
      
    } else {
      // Invalid because QR != 1, doesn't match response type
    }
    
  }
  
  
  private String retrieveName(int pointerIndex) {
    String completeName = "";
    parseName(pointerIndex, 0, completeName);
    return completeName;
  }
  
  private void parseName(int p, int count, String name) {
    if (count == 0) {
      // First time entering, data[p] must be segment length indicator
      String s = "" + helper.getBitsFromByte(data[p]);
      int segLen = Integer.parseInt(s, 2);
      System.out.println("###### " + segLen);
      
    } 
  }
  
  private void storeName(int p, String subname) {
    for (int i = 0; i < p; i++) {
      
    }
  }
  
}

/*
 *
Resource record format

The answer, authority, and additional sections all share the same
format: a variable number of resource records, where the number of
records is specified in the corresponding count field in the header.
Each resource record has the following format:
                                    1  1  1  1  1  1
      0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                                               |
    /                                               /
    /                      NAME                     / 16 bit (pointer: '11' + 14 bits)
    |                                               |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                      TYPE                     | unsigned 16 bit
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                     CLASS                     | unsigned 16 bit
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                      TTL                      | unsigned 32 bit
    |                                               |
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    |                   RDLENGTH                    | unsigned 16 bit
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--|
    /                     RDATA                     / 
    /                                               /
    +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
 * 
 */
