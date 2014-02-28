package com.dnsclient.core;

public class ResponseGenerator {

  private DataHelper helper = null;
  byte[] data;
  
  public ResponseGenerator(byte[] responseData) {
    this.data = responseData;
    this.helper = DataHelper.getInstance();
    parse();
  }
  
  protected void parse() {
    System.out.println("======= RESPONSE ====== ");
    helper.printBitsFromByteArray(data, 3);
    helper.printBitFromByte(data[3], 0);
    if (helper.getBitFromByte(data[3],0) == '1') {
      
    } else {
      // Invalid because QR != 1, doesn't match response type
    }
    
  }
  
}
