package com.unstoppabledomains.resolution.contracts.cns;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProxyData {
  private String resolver;
  private String owner;
  private String[] values;
}
