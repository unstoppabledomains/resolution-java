package com.unstoppabledomains.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.unstoppabledomains.exceptions.NSExceptionCode;
import com.unstoppabledomains.exceptions.NSExceptionParams;
import com.unstoppabledomains.exceptions.NamingServiceException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DnsUtils {
  
  static Number DEFAULT_TTL = 300;
  public List<DnsRecord> toList(Map<String, String> rawRecords) throws NamingServiceException {
    List<DnsRecord> dnsRecords = new ArrayList<DnsRecord>();
    List<DnsRecordsType> types = getAllDnsTypes(rawRecords);
    for (DnsRecordsType type: types) {
      List<DnsRecord> dnsType = constructDnsRecords(rawRecords, type);
      dnsRecords.addAll(dnsType);
    }
    return dnsRecords;
  }

  public Map<String, String> toMap(List<DnsRecord> records) throws NamingServiceException {
    Map<String, String> map = new HashMap<String, String>();
    for (DnsRecord record: records) {
      DnsRecordsType type = record.getType();
      String ttlInRecord = map.get("dns." + type + ".ttl");
      JsonArray dnsInRecord = getJsonArray(map.get("dns." + type), type);
      if (dnsInRecord != null) {
        dnsInRecord.add(record.getData());
        map.put("dns." + type, dnsInRecord.toString());
      } else {
        map.put("dns." + type, "["+record.getData()+"]");
        map.put("dns." + type + ".ttl", record.getTtl().toString());
      }

      if (ttlInRecord != null && !ttlInRecord.isEmpty() && !ttlInRecord.equals(record.getTtl().toString())) {
        throw new NamingServiceException(NSExceptionCode.InconsistentTtl, new NSExceptionParams("r", type.toString()));
      }
    }
    return map;
  }

  private JsonArray getJsonArray(String jsonArray, DnsRecordsType type) throws NamingServiceException {
    try {
      return JsonParser.parseString(jsonArray).getAsJsonArray();
    } catch(NullPointerException exception) {
      // this is possible in toMap function only
      return null;
    } catch(JsonSyntaxException exception) {
      throw new NamingServiceException(NSExceptionCode.DnsRecordCorrupted,
        new NSExceptionParams("r", type.toString()));
    }
  }

  private List<DnsRecordsType> getAllDnsTypes(Map<String, String> rawRecords) {
    Set<DnsRecordsType> dnsTypes = new HashSet<DnsRecordsType>();
    for (Map.Entry<String, String> entry : rawRecords.entrySet()) {
      String[] chunks = entry.getKey().split("\\.");
      Boolean isDnsType = chunks[0].equals("dns") && !chunks[1].equals("ttl");
      if (isDnsType.equals(Boolean.TRUE)) {
        dnsTypes.add(DnsRecordsType.valueOf(chunks[1]));
      }
    }
    return new ArrayList<DnsRecordsType>(dnsTypes);
  }

  private List<DnsRecord> constructDnsRecords(Map<String, String> rawRecords, DnsRecordsType type) throws NamingServiceException {
    Number ttl = parseTtl(rawRecords, type);
    String jsonValueString = rawRecords.get("dns." + type.toString());
    if (jsonValueString.isEmpty()) {
        return null;
    }
    List<DnsRecord> data = new ArrayList<DnsRecord>();
      JsonArray arr = getJsonArray(jsonValueString, type);
      for (JsonElement elem: arr) {
        String value = elem.getAsString();
        if (!value.isBlank()) {
          data.add(new DnsRecord(type, ttl, value));
        }
      }
      return data;
  }

  private Number parseTtl(Map<String, String> rawRecords, DnsRecordsType type) {
    Number recordTtl = parseIfNumber(rawRecords.get("dns." + type.toString() + ".ttl"));
    if (recordTtl != null) {
      return recordTtl;
    }
    Number defaultRecordTtl = parseIfNumber(rawRecords.get("dns.ttl"));
    if (defaultRecordTtl != null) {
      return defaultRecordTtl;
    }

    return DEFAULT_TTL;
  }

  private Number parseIfNumber(String str) {
    if (str.isEmpty()) {
      return null;
    }
    try {
      return(Integer.parseInt(str));
    } catch(NumberFormatException exception) {
      return null;
    }
  }
}
