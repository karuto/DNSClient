package com.dnsclient.core;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DNSClient {
  
  public static String DNSIP = "";
  public static String targetDomain = "";
  public static String queryType;
  public static int port = 53; /* port for sending DNS requests */
  
  private static Pattern pattern;
  private static Matcher matcher;
  
  /*
   * The regular expression below is borrowed from:
   * http://examples.javacodegeeks.com/core-java/util/regex/matcher/
   * validate-ip-address-with-java-regular-expression-example/
   */
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
      
      // Validate IP address, quit if invalid
      pattern = Pattern.compile(IPADDRESS_PATTERN);
      matcher = pattern.matcher(DNSIP);
      if (!matcher.matches()) {
        System.out.println("The IP address of the DNS server is invalid.");
        System.exit(1);
      } 
      
      // If everything else checks out to be correct, init the UDP socket
      UDPClient client = new UDPClient(DNSIP, port, targetDomain, queryType);
      RequestGenerator request = new RequestGenerator(targetDomain, queryType);
      
      try {
        byte[] responseData = client.connect(request.build());
        ResponseGenerator response = new ResponseGenerator(responseData);
      } catch (Exception e) {
        e.printStackTrace();
      }
      
    } else {
      System.out.println("Usage: java -cp *.jar DNSClient 8.8.8.8 www.cnn.com A");
    }
}
  

  
  
  
  
  
}
