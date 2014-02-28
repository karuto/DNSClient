package com.dnsclient.core;

public class DataHelper {
  private static DataHelper instance = null;
  
  protected DataHelper() {
    /* Null for singleton protection */
  }
  
  public static DataHelper getInstance() {
    if (instance == null) {
      instance = new DataHelper();
    }
    return instance;
  }
  
  /* Helper function to print bits from a byte array */
  protected void printBitsFromByteArray(byte[] bytes, Boolean hasCounter) {
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
  protected void printBitsFromByte(byte b) {
    String bits = String.format("%8s", 
        Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    System.out.println(bits);
  }

}
