package com.agentdid127.resourcepack.library;

import com.agentdid127.converter.Plugin;

public abstract class RPPlugin extends Plugin<RPConverter> {

  private String from;
  private String to;

  private PackConverter packConverter;

  public RPPlugin(String name) {
    super(name, "RPPlugin");
  }

  public String getFrom() {
    return this.from;
  }

  public String getTo() {
    return this.to;
  }

  public PackConverter getPackConverter() {
    return this.packConverter;
  }

  void setFrom(String from) {
    this.from = from;
  }

  void setTo(String to) {
    this.to = to;
  }

  void setPackConverter(PackConverter pc) {
    packConverter = pc;
  }
}
