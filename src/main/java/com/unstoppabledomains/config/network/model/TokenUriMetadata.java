package com.unstoppabledomains.config.network.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Data
public class TokenUriMetadata {

  @Getter
  @AllArgsConstructor
  public enum DisplayType {
    UNDEFINED(""),
    NUMBER("number"),
    DATE("date"),
    BOOST_NUMBER("boost_number"),
    BOOST_PERCENTAGE("boost_percentage"),
    RANKING("ranking");

    private final String name;
  }
  
  @Data
  class TokenUriMetadataAttribute {
    private DisplayType display_type;
    private String trait_type;
    private String value;
  }

  private String name;
  private String description;
  private String image;
  private String external_url;
  private String external_link;
  private String image_data;
  private List<TokenUriMetadataAttribute> attributes;
  private String background_color;
  private String animation_url;
  private String youtube_url;
}
