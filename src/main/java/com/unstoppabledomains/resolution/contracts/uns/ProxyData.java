package com.unstoppabledomains.resolution.contracts.uns;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProxyData {
  private String resolver;
  private String owner;
  private List<String> values;
}
