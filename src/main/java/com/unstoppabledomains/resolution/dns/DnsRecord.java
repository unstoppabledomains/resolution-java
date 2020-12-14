package com.unstoppabledomains.resolution.dns;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class DnsRecord {
  private DnsRecordsType type;
  private Number ttl;
  private String data;

  @Override
  public boolean equals(Object obj) {
    DnsRecord that = (DnsRecord) obj;

    return that.type.equals(type) && that.ttl.equals(ttl) && that.data.equals(data);
  }
}
