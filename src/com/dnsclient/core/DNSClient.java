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
  
  /*
   * The regular expression below is borrowed from:
   * http://www.mkyong.com/regular-expressions/
   * domain-name-regular-expression-example/
   */
  private static final String DOMAIN_NAME_PATTERN = 
      "^[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
  
  public static void main (String[] args) {
    System.out.println(args.length);
    
    if (args.length == 4) {
      DNSIP = args[1]; // the IP address of the DNS server
      targetDomain = args[2]; // the domain name to look up
      queryType = args[3]; // the type of query (e.g., [A, NS, MX])
      
      // Validate IP address, quit if invalid
      pattern = Pattern.compile(IPADDRESS_PATTERN);
      matcher = pattern.matcher(DNSIP);
      if (!matcher.matches()) {
        System.out.println("The IP address of the DNS server is invalid.");
        System.exit(1);
      } 
      

      // Validate domain name, quit if invalid
      pattern = Pattern.compile(DOMAIN_NAME_PATTERN);
      matcher = pattern.matcher(targetDomain);
      if (!matcher.matches()) {
        System.out.println("The domain name of the DNS server is invalid.");
        System.exit(1);
      } 
      
      if (queryType.equalsIgnoreCase("A") || 
          queryType.equalsIgnoreCase("MX") ||
          queryType.equalsIgnoreCase("NS")) {
      } else {
        System.out.println("The type of query is invalid.");
        System.exit(1);
      }
      
      
      // If everything else checks out to be correct, init the UDP socket
      UDPClient client = new UDPClient(DNSIP, port, targetDomain, queryType);
      RequestGenerator request = new RequestGenerator(targetDomain, queryType);
      
      try {
        byte[] responseData = client.connect(request.build());
        ResponseHandler response = new ResponseHandler(responseData, targetDomain, queryType);
      } catch (Exception e) {
        e.printStackTrace();
      }
      
    } else {
      System.out.println("Usage: java -jar *.jar DNSClient 8.8.8.8 www.cnn.com A");
    }
}
  

  
  
  
  
  
}
