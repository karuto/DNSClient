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

  /* Helper function to print a single bit at given index from a byte */
  protected void printBitFromByte(byte b, int index) {
    assert(index >= 0);
    String bits = getBitsFromByte(b);
    if (index < bits.length()) {
      System.out.println("The bit at index " + index + " is " + bits.charAt(index));
    } else {
      System.out.println("Requested index out of bound");
    }
  }

  /* Helper function to retrieve a single bit at given index from a byte */
  protected char getBitFromByte(byte b, int index) {
    assert(index >= 0);
    String bits = getBitsFromByte(b);
    if (index < bits.length()) {
      return bits.charAt(index);
    } else {
      System.out.println("Requested index out of bound");
    }
    return 0;
  }
  
  /* Helper function to print bits from a byte array without any options */
  protected void printBitsFromByteArray(byte[] bytes) {
    for (int i = 0; i < bytes.length; i++) {
      System.out.println("Byte #" + i);   
      printBitsFromByte(bytes[i]);
    }
  }
    
  /* Helper function to print bits from a byte array, with segments */
  protected void printBitsFromByteArray(byte[] bytes, int byteCounts) {
    assert (byteCounts < bytes.length);
    for (int i = 0; i < byteCounts; i++) {
      System.out.println("Byte #" + i);   
      printBitsFromByte(bytes[i]);
    }
  }
  
  /* Helper function to print bits from a byte array, with options */
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
  
  /* Helper function to retrieve a bit from a single byte 
   * Credit: http://stackoverflow.com/questions/12310017/
   * how-to-convert-a-byte-to-its-binary-string-representation/12310078
   */
  protected String getBitsFromByte(byte b) {
    String bits = String.format("%8s", 
        Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    return bits;
  }

}
