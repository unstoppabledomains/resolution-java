package com.unstoppabledomains.resolution;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class TokenUriMetadata {
  @Data
  public class TokenUriMetadataAttribute {
    private String displayType;
    private String traitType;
    private String value;
  }

  @Data
  public class TokenUriMetadataProperties {
    private Map<String, String> records;
  }

  private String name;
  private String namehash;
  private String tokenId;
  private String description;
  private String image;
  private String externalUrl;
  private String externalLink;
  private String imageData;
  private List<TokenUriMetadataAttribute> attributes;
  private String backgroundColor;
  private String animationUrl;
  private String youtubeUrl;
}
