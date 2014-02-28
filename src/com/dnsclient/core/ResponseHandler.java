package com.dnsclient.core;

import java.util.ArrayList;
import java.util.HashMap;

public class ResponseHandler {

  private static int numQuestions;
  private static int numAnswers;
  private static int numAuthority;
  private static int numAdditional;
  private static HashMap<Integer, String> nameMap;
  private static ArrayList<DNSAnswer> records_answer;
  private static ArrayList<DNSAnswer> records_auth;
  private static ArrayList<DNSAnswer> records_addi;
  private static String targetDomain;
  private static String queryType;
  private DataHelper helper = null;
  private static int dataCursor;
  byte[] data;
  
  
  public ResponseHandler(byte[] responseData, String targetDomain, String queryType) {
    this.data = responseData;
    this.targetDomain = targetDomain;
    this.helper = DataHelper.getInstance();
    this.nameMap = new HashMap();
    this.records_answer = new ArrayList<DNSAnswer>();
    this.records_auth = new ArrayList<DNSAnswer>();
    this.records_addi = new ArrayList<DNSAnswer>();
    this.dataCursor = 0;
    this.queryType = queryType;
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
          " Additional RRs: " + numAdditional + "\n");
      
      System.out.println("QUESTION SECTION:");
      System.out.println(targetDomain + "\t\t\t\t" + "IN\t" + queryType + "\t");
      
      /* QUESTION SECTION */
      // Loop until reaching first zero byte, which is end of QNAME's domain
      dataCursor = 12;
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

      /* ANSWER SECTION */
      if (numAnswers > 0) {
        mainLoop(numAnswers, "answer", records_answer);      

        System.out.println("\nANSWER SECTION:");
        for (DNSAnswer d : records_answer) {
          System.out.println(d.toString());
        }
      }
      if (numAuthority > 0) {
        mainLoop(numAuthority, "authority", records_auth); 
        
        System.out.println("\nAUTHORITY SECTION:");
        for (DNSAnswer d : records_answer) {
          System.out.println(d.toString());
        }      
      }
      if (numAdditional > 0) {
        mainLoop(numAdditional, "additional", records_addi); 
        
        System.out.println("\nADDITIONAL SECTION:");
        for (DNSAnswer d : records_answer) {
          System.out.println(d.toString());
        }       
      }

      
      
      
    } else {
      // Invalid because QR != 1, doesn't match response type
    }
    
  }
  
  
  private void mainLoop(int looper, String recordType, ArrayList<DNSAnswer> records) {
    for (int i = 0; i < looper; i++) {
      DNSAnswer answer = new DNSAnswer();
      /* ANSWER NAME */
      if (helper.getBitFromByte(data[dataCursor], 0) =='1' && 
          helper.getBitFromByte(data[dataCursor], 1) == '1') {
        // This indicates the name field is a pointer
        // Replace first 2 bits of '11', cast to int to find pointer index
        String name = "" + helper.getBitsFromByte(data[dataCursor]) + 
            helper.getBitsFromByte(data[dataCursor+1]);
        int pointer = Integer.parseInt("00" + name.substring(2), 2);
        // Call the recursive parsing function to get full domain name
        answer.setName(retrieveName(pointer));
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
      } else if (helper.getBitsFromByte(data[dataCursor+1]).
          equalsIgnoreCase("00000110")) { // 0x06, SOA
        answer.setType("SOA");
      } else if (helper.getBitsFromByte(data[dataCursor+1]).
          equalsIgnoreCase("00001011")) { // 0x0B, WKS
        answer.setType("WKS");
      } else if (helper.getBitsFromByte(data[dataCursor+1]).
          equalsIgnoreCase("00001100")) { // 0x0C, PTR
        answer.setType("PTR");
      } else if (helper.getBitsFromByte(data[dataCursor+1]).
          equalsIgnoreCase("00001111")) { // 0x0F, MX
        answer.setType("MX");
      } else if (helper.getBitsFromByte(data[dataCursor+1]).
          equalsIgnoreCase("00100001")) { // 0x21, SRV
        answer.setType("SRV");
      } else if (helper.getBitsFromByte(data[dataCursor+1]).
          equalsIgnoreCase("00100110")) { // 0x26, A6
        answer.setType("A6");
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
      dataCursor += 2; /* 4 bytes for TTL */
      
      /* ANSWER RDATA */
      String address = "";
      if (rlength_int == 4) {
        // It's IP Address
        for (i = 0; i < 4; i++) {
          String add = "" + helper.getBitsFromByte(data[dataCursor+i]);
          int add_int = Integer.parseInt(add, 2);
          if (i != 3) {
            address += String.valueOf(add_int) + ".";
          } else {
            address += String.valueOf(add_int);
          }
        }
      } else {
        // It's name / url
        if (nameMap.get(dataCursor) != null) {
          // This name already existed, just grab it
          address = nameMap.get(dataCursor);
        } else {
          // Call the recursive parsing function to get full domain name
          address = retrieveName(dataCursor);
        }
      }
      answer.setAddress(address);
      dataCursor += rlength_int;

//      System.out.println(answer.toString());
      answer.setRecordType(recordType);
      records.add(answer);
    }
  }
  
  
  private String retrieveName(int pointerIndex) {
    if (nameMap.get(pointerIndex) == null) {
      parseName(pointerIndex, pointerIndex, 0, "");
//    System.out.println("### Complete Name #### " + nameMap.get(pointerIndex));
    }
    return nameMap.get(pointerIndex);
  }
  
  private void parseName(int p_copy, int p, int count, String name) {
    if (helper.getBitsFromByte(data[p]).equalsIgnoreCase("00000000")) {
      // Ending condition, because you reached end of the name
      // I gave up. I'm storing in HashMap because of a strange error
      // preventing me from passing the return value.
      nameMap.put(p_copy, name);
//      System.out.println("###### CHECKING: p=" + p + " p_copy=" + p_copy +
//          " count=" + count + " name=" + name);
    } else {
      // First time entering, data[p] must be segment length indicator
      // Parse first byte (the length)
      String s = "" + helper.getBitsFromByte(data[p]);
      int segLen = Integer.parseInt(s, 2);
      
      // Base on the length, parse the next [length] bytes and save it
      String subname = storeSubname(p, segLen);
      
      // Increment the counter, move it to the latest location
      p += (segLen+1);
      count++;
      name += subname;
      parseName(p_copy, p, count, name);
    }
  }
  
  private String storeSubname(int base, int offset) {
    String subname = "";
    for (int i = 1; i <= offset; i++) {
      char c = (char) data[base+i];
      subname += c;
    }
    subname += ".";
    return subname;
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
