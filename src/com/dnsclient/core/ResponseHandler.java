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
    System.out.println("======= RESPONSE ====== ");
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
      
      /*
      System.out.println();
      System.out.println(dataCursor); // i represents the last byte of domain name
      helper.printBitsFromByteArray(data, 23, 40); // byte[24] is where it ends (endblock)
      */
      
      
      /* ANSWERING SECTION */
      // Put the cursor at the beginning of Answer section
      helper.printBitsFromByte(data[dataCursor+4]); // +2 for QTYPE, +2 for QCLASS
      helper.printBitsFromByte(data[dataCursor+5]);
    

      System.out.println("=========================");
      
      for (int i = dataCursor+4; i < data.length; i++) {
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
