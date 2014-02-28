package com.dnsclient.core;

public class ResponseGenerator {

  private DataHelper helper = null;
  byte[] data;
  
  public ResponseGenerator(byte[] responseData) {
    this.data = responseData;
    this.helper = DataHelper.getInstance();
    parseResponse();
  }
  
  protected void parseResponse() {
    System.out.println("======= RESPONSE ====== ");
//    helper.printBitsFromByteArray(data, 12);
//    helper.printBitsFromByteArray(data, 12, 20);
    helper.printBitFromByte(data[3], 0);
    if (helper.getBitFromByte(data[3],0) == '1') {
      // Valid DNS response, keep parsing
      
      // Loop until reaching first zero byte, which is end of QNAME
      int i = 12;
      while (!helper.isZeroByte(data[i])) {
        System.out.print((char) data[i]);
//        System.out.println(helper.getContentFromByte(data[i]));
        i++;
      }
      System.out.println();
      System.out.println(i);
    helper.printBitsFromByteArray(data, 23, 25); // byte[24] is where it ends (endblock)
      
    } else {
      // Invalid because QR != 1, doesn't match response type
    }
    
  }
  
}
