package com.agentdid127.resourcepack.MCPatcher;

import com.agentdid127.converter.util.Logger;
import com.agentdid127.resourcepack.library.PackConverter;
import com.agentdid127.resourcepack.library.RPPlugin;
import com.agentdid127.resourcepack.library.Util;

public class MCPatcherPlugin extends RPPlugin {

  public MCPatcherPlugin() {
    super("MCPatcherPlugin");
  }

  @Override
  public void onLoad() {
    Logger.log("MCPatcher Plugin Loaded.");
  }

  @Override
  public void onInit() {
    PackConverter pc = this.getPackConverter();
    if (Util.getVersionProtocol(pc.getGson(), this.getFrom()) <= Util.getVersionProtocol(pc.getGson(),
        this.getTo())) {
      if (Util.getVersionProtocol(pc.getGson(), this.getFrom()) <= Util.getVersionProtocol(pc.getGson(),
          "1.12.2")
          && Util.getVersionProtocol(pc.getGson(), this.getTo()) >= Util.getVersionProtocol(
          pc.getGson(), "1.13")) {
        this.getRunners().add(new MCPatcherConverter(pc));
      }
    } else {
      if (Util.getVersionProtocol(pc.getGson(), this.getFrom()) >= Util.getVersionProtocol(pc.getGson(), "1.12.2")
          && Util.getVersionProtocol(pc.getGson(), this.getTo()) <= Util.getVersionProtocol(pc.getGson(), "1.13")) {
        this.getRunners().add(new MCPatcherBackwardsConverter(pc));
      }
    }
  }

  @Override
  public void onUnload() {
    this.getRunners().clear();
    Logger.log("MCPatcher Plugin Unloaded.");
  }
}
