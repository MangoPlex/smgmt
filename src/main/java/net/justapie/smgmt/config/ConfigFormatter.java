package net.justapie.smgmt.config;

import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;

public class ConfigFormatter {
  private final String s;
  private final HashMap<String, String> map;

  public ConfigFormatter(String s) {
    this.s = s;
    this.map = new HashMap<>();
  }

  public ConfigFormatter putKV(String k, String v) {
    map.put(k, v);
    return this;
  }

  public String build() {
    return StringSubstitutor.replace(this.s, this.map, "{", "}");
  }
}
