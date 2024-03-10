package com.agentdid127.resourcepack.library;

/**
 * Sets Specific version data for a Plugin.
 */
public class RPPluginVersionSetter {

  /**
   * Set data for a plugin
   * @param plugin Plugin to set data for.
   * @param from Version we are converting from
   * @param to Version we are converting to.
   * @param pc Pack Converter.
   */
  public static void setData(RPPlugin plugin, String from, String to, PackConverter pc) {
    plugin.setFrom(from);
    plugin.setTo(to);
    plugin.setPackConverter(pc);
  } // setData


} // RPPluginVersionSetter
