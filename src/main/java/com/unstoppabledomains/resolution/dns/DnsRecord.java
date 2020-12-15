package com.unstoppabledomains.resolution.dns;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class DnsRecord {
  private DnsRecordsType type;
  private int ttl;
  private String data;
}
