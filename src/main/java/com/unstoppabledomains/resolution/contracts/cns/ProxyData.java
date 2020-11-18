package com.unstoppabledomains.resolution.contracts.cns;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProxyData {
  String resolver;
  String owner;
  String[] values;
}
