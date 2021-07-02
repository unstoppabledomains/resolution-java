package com.unstoppabledomains.resolution.contracts;

import java.util.List;

import lombok.Data;

@Data
public class ContractLogs {
  private String logIndex;
  private String blockNumber;
  private String blockHash;
  private String transactionHash;
  private String transactionIndex;
  private String address;
  private String data;
  private List<String> topics;
}
