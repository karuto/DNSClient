package com.dnsclient.core;

public class ResponseGenerator {

  private DataHelper helper = null;
  byte[] data;
  
  public ResponseGenerator(byte[] responseData) {
    this.data = responseData;
    this.helper = DataHelper.getInstance();
  }
  

  
}
