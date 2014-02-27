package com.dnsclient.core;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DNSClient {
  
  public static String DNSIP = "0.0.0.0";
  public static String targetDomain = "www.example.com";
  public static String queryType;
  
  private static Pattern pattern;
  private static Matcher matcher;
  private static final String IPADDRESS_PATTERN = 
      "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
      "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
      "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
      "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
  
  public static void main (String[] args) {
    
    if (args.length == 3) {
      DNSIP = args[0]; // the IP address of the DNS server
      targetDomain = args[1]; // the domain name to look up
      queryType = args[2]; // the type of query (e.g., [A, NS, MX])

      System.out.println("======= " + DNSIP);
      System.out.println("======= " + targetDomain);
      System.out.println("======= " + queryType);
      
      // Validate IP address, quit if invalid
      pattern = Pattern.compile(IPADDRESS_PATTERN);
      matcher = pattern.matcher(DNSIP);
      if (!matcher.matches()) {
        System.out.println("The IP address of the DNS server is invalid.");
        System.exit(1);
      } 
      
    } else {
      System.out.println("Usage: java -cp *.jar DNSClient 8.8.8.8 www.cnn.com A");
    }
}
  

  
  
  
  
  
}
