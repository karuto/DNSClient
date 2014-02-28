package com.dnsclient.core;

public class ResponseHandler {

  private DataHelper helper = null;
  byte[] data;
  
  public ResponseHandler(byte[] responseData) {
    this.data = responseData;
    this.helper = DataHelper.getInstance();
    parseResponse();
  }
  
  protected void parseResponse() {
    if (helper.getBitFromByte(data[3],0) == '1') {
      // Valid DNS response, keep parsing
      
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
      
      
      
      /* ANSWER SECTION */
      
      /* TODO: For now, just parse 1 single answering query */
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
      answer.setDataLength(String.valueOf(Integer.parseInt(rlength, 2)));
      System.out.println(answer.toString());
      answerCursor += 4; /* 4 bytes for TTL */
      
      
      /* ANSEWR RDATA */
      
        
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
