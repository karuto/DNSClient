package com.dnsclient.core;

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
      helper.printBitsFromByteArray(data, 4, 5);
      
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
      
      DNSAnswer[] results;
      
      
      /* ANSWER SECTION */
      
      int answerCursor = 0;
      DNSAnswer answer = new DNSAnswer();
      /* ANSWER NAME */
      if (helper.getBitFromByte(data[dataCursor], 0) =='1' && 
          helper.getBitFromByte(data[dataCursor], 1) == '1') {
        // This indicates the name field is a pointer
        String name = "" + (char) data[dataCursor] + (char) data[dataCursor+1];
        answer.setName(name);
      } else {
        // This indicates the name field is not pointer
        
      }
      answerCursor += 2; /* 2 bytes for NAME */

      /* ANSEWR TYPE */
      if (helper.getBitsFromByte(data[dataCursor+3]).
          equalsIgnoreCase("00000001")) { // 0x01, A
        answer.setType("A");
      } else if (helper.getBitsFromByte(data[dataCursor+3]).
          equalsIgnoreCase("00000010")) { // 0x02, NS
        answer.setType("NS");
      } else if (helper.getBitsFromByte(data[dataCursor+3]).
          equalsIgnoreCase("00000101")) { // 0x05, CNAME
        answer.setType("CNAME");
      }
      answerCursor += 2; /* 2 bytes for TYPE */
      
      /* ANSEWR CLASS */
      if (helper.getBitsFromByte(data[dataCursor+5]).
          equalsIgnoreCase("00000001")) { // 0x01, IN
        answer.setDnsClass("IN");
      }
      answerCursor += 2; /* 2 bytes for CLASS */
      
      /* ANSEWR TTL */
      String ttl = "" + helper.getBitsFromByte(data[dataCursor+6]) + 
          helper.getBitsFromByte(data[dataCursor+7]) + 
          helper.getBitsFromByte(data[dataCursor+8]) + 
          helper.getBitsFromByte(data[dataCursor+9]);
      answer.setTtl(String.valueOf(Integer.parseInt(ttl, 2)));
      answerCursor += 4; /* 4 bytes for TTL */
      
      /* ANSEWR RLENGTH */
      String rlength = "" + helper.getBitsFromByte(data[dataCursor+10]) + 
          helper.getBitsFromByte(data[dataCursor+11]);
      int rlength_int = Integer.parseInt(rlength, 2);
      answer.setDataLength(String.valueOf(rlength_int));
      System.out.println(answer.toString());
      answerCursor += 2; /* 4 bytes for TTL */
      
      /* ANSEWR RDATA */
      answerCursor += rlength_int;
      
        
      for (int i = dataCursor; i < data.length; i++) {
//        System.out.print((char) data[i]);
      }
      
      
    } else {
      // Invalid because QR != 1, doesn't match response type
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
