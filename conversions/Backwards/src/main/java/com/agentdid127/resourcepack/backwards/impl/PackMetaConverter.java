package com.agentdid127.resourcepack.backwards.impl;

import com.agentdid127.resourcepack.library.PackConverter;
import com.agentdid127.resourcepack.library.RPConverter;
import com.agentdid127.resourcepack.library.Util;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class PackMetaConverter extends RPConverter {
    private final int version;
    private int versionInt = 4;

    public PackMetaConverter(PackConverter packConverter, int versionIn) {
        super(packConverter, "PackMetaConverter" ,1);
      this.version = versionIn;
    }

    /**
     * Converts MCMeta to newer version
     *
     * @throws IOException
     */
    @Override
    public void convert() throws IOException {
        Path file = this.pack.getWorkingPath().resolve("pack.mcmeta");
        if (!file.toFile().exists())
            return;
        if (this.version >= Util.getVersionProtocol(this.packConverter.getGson(), "1.20"))
          this.versionInt = 15;
        else if (this.version >= Util.getVersionProtocol(this.packConverter.getGson(), "1.19.4") &&
            this.version < Util.getVersionProtocol(
            this.packConverter.getGson(), "1.20"))
          this.versionInt = 13;
        else if (this.version >= Util.getVersionProtocol(this.packConverter.getGson(), "1.19.3") &&
            this.version < Util.getVersionProtocol(
            this.packConverter.getGson(), "1.19.4"))
          this.versionInt = 12;
        else if (this.version >= Util.getVersionProtocol(this.packConverter.getGson(), "1.19") && this.version
            < Util.getVersionProtocol(
            this.packConverter.getGson(), "1.19.3"))
          this.versionInt = 9;
        else if (this.version >= Util.getVersionProtocol(this.packConverter.getGson(), "1.18") && this.version
            < Util.getVersionProtocol(
            this.packConverter.getGson(), "1.19"))
          this.versionInt = 8;
        else if (this.version >= Util.getVersionProtocol(this.packConverter.getGson(), "1.17") && this.version
            < Util.getVersionProtocol(
            this.packConverter.getGson(), "1.18"))
          this.versionInt = 7;
        else if (this.version >= Util.getVersionProtocol(this.packConverter.getGson(), "1.16.2") &&
            this.version < Util.getVersionProtocol(
            this.packConverter.getGson(), "1.17"))
          this.versionInt = 6;
        else if (this.version >= Util.getVersionProtocol(this.packConverter.getGson(), "1.15") && this.version
            < Util.getVersionProtocol(
            this.packConverter.getGson(), "1.16.2"))
          this.versionInt = 5;
        else if (this.version >= Util.getVersionProtocol(this.packConverter.getGson(), "1.13") && this.version
            < Util.getVersionProtocol(
            this.packConverter.getGson(), "1.15"))
          this.versionInt = 4;
        else if (this.version >= Util.getVersionProtocol(this.packConverter.getGson(), "1.11") && this.version
            < Util.getVersionProtocol(
            this.packConverter.getGson(), "1.13"))
          this.versionInt = 3;
        else if (this.version >= Util.getVersionProtocol(this.packConverter.getGson(), "1.9") && this.version
            < Util.getVersionProtocol(
            this.packConverter.getGson(), "1.11"))
          this.versionInt = 2;
        else if (this.version >= Util.getVersionProtocol(this.packConverter.getGson(), "1.7.2") &&
            this.version < Util.getVersionProtocol(
            this.packConverter.getGson(), "1.9"))
          this.versionInt = 1;
        else
          this.versionInt = 0;

        JsonObject json = Util.readJson(this.packConverter.getGson(), file);
        {
            JsonObject meta = json.getAsJsonObject("meta");
            if (meta == null)
                meta = new JsonObject();
            meta.addProperty("game_version", Util.getVersionFromProtocol(this.packConverter.getGson(),
                this.version));
            json.add("meta", meta);
        }
        
        {
            JsonObject packObject = json.getAsJsonObject("pack");
            if (packObject == null)
                packObject = new JsonObject();
            packObject.addProperty("pack_format", this.versionInt);
            json.add("pack", packObject);
        }

        Files.write(file, Collections.singleton(this.packConverter.getGson().toJson(json)),
            StandardCharsets.UTF_8);
    }
}