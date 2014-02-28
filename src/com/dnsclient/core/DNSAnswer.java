package com.dnsclient.core;

public class DNSAnswer {

  String name;
  String type;
  String dnsClass;
  String ttl;
  String address;
  
  public DNSAnswer(String name, String type, String dnsClass, String ttl,
      String address) {
    super();
    this.name = name;
    this.type = type;
    this.dnsClass = dnsClass;
    this.ttl = ttl;
    this.address = address;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getDnsClass() {
    return dnsClass;
  }

  public void setDnsClass(String dnsClass) {
    this.dnsClass = dnsClass;
  }

  public String getTtl() {
    return ttl;
  }

  public void setTtl(String ttl) {
    this.ttl = ttl;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }
  
  
  
  
}
