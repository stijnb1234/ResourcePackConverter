package com.agentdid127.resourcepack.forwards.impl;

import com.agentdid127.converter.util.Logger;
import com.agentdid127.resourcepack.library.PackConverter;
import com.agentdid127.resourcepack.library.RPConverter;
import com.agentdid127.resourcepack.library.Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

public class BlockStateConverter extends RPConverter {
    private boolean anyChanges;
    private final int from;
    private final int to;

    public BlockStateConverter(PackConverter packConverter, int from, int to) {
        super(packConverter, "BlockStateConverter", 1);
        this.from = from;
        this.to = to;
    }

    /**
     * Updates blockstates in blockstates folder
     *
     * @throws IOException
     */
    @Override
    public void convert() throws IOException {
        Path states = this.pack.getWorkingPath()
                .resolve("assets" + File.separator + "minecraft" + File.separator + "blockstates");
        if (!states.toFile().exists())
            return;
        Files.list(states).filter(file -> file.toString().endsWith(".json")).forEach(file -> {
            try {
                JsonObject json = Util.readJson(this.packConverter.getGson(), file);
                this.anyChanges = false;

                // process multipart
                JsonArray multipartArray = json.getAsJsonArray("multipart");
                if (multipartArray != null) {
                    for (int i = 0; i < multipartArray.size(); i++) {
                        JsonObject multipartObject = multipartArray.get(i).getAsJsonObject();
                        for (Map.Entry<String, JsonElement> entry : multipartObject.entrySet())
                            this.updateModelPath(entry);
                    }
                }

                // process variants
                JsonObject variantsObject = json.getAsJsonObject("variants");
                if (variantsObject != null) {
                    // change "normal" key to ""
                    if (this.from <= Util.getVersionProtocol(this.packConverter.getGson(), "1.12.2")
                            && this.to >= Util.getVersionProtocol(this.packConverter.getGson(), "1.13")) {
                        JsonElement normal = variantsObject.get("normal");
                        if (normal instanceof JsonObject || normal instanceof JsonArray) {
                            variantsObject.add("", normal);
                            variantsObject.remove("normal");
                            this.anyChanges = true;
                        }
                    }

                    // update model paths to prepend block
                    for (Map.Entry<String, JsonElement> entry : variantsObject.entrySet())
                        this.updateModelPath(entry);
                }
                if (this.anyChanges) {
                    Files.write(file, Collections.singleton(
                            this.packConverter.getGson().toJson(json)),
                        StandardCharsets.UTF_8);
                    if (PackConverter.DEBUG)
                        Logger.log("      Converted " + file.getFileName());
                }
            } catch (IOException e) {
                Util.propagate(e);
            }
        });
    }

    /**
     * Updates Model paths
     * 
     * @param entry
     */
    private void updateModelPath(Map.Entry<String, JsonElement> entry) {
        NameConverter nameConverter = this.packConverter.getConverter(NameConverter.class);
        if (entry.getValue() instanceof JsonObject) {
            JsonObject value = (JsonObject) entry.getValue();
            if (value.has("model")) {
                String[] split = value.get("model").getAsString().split("/");
                String val = split[split.length - 1];
                String prefix = value.get("model").getAsString().substring(0,
                        value.get("model").getAsString().length() - val.length());

                if (this.from < Util.getVersionProtocol(this.packConverter.getGson(), "1.13")
                        && this.to >= Util.getVersionProtocol(this.packConverter.getGson(), "1.13")) {
                    val = nameConverter.getBlockMapping().remap(val);
                    prefix = prefix.replaceAll("blocks", "block");
                    this.anyChanges = true;
                }

                if (this.from < Util.getVersionProtocol(this.packConverter.getGson(), "1.14")
                        && this.to >= Util.getVersionProtocol(this.packConverter.getGson(), "1.14")) {
                    val = nameConverter.getNewBlockMapping().remap(val);
                    this.anyChanges = true;
                }

                if (this.from < Util.getVersionProtocol(this.packConverter.getGson(), "1.17")
                        && this.to >= Util.getVersionProtocol(this.packConverter.getGson(), "1.17")) {
                    val = nameConverter.getBlockMapping17().remap(val);
                    this.anyChanges = true;
                }

                if (this.from < Util.getVersionProtocol(this.packConverter.getGson(), "1.19")
                        && this.to >= Util.getVersionProtocol(this.packConverter.getGson(), "1.19")) {
                    val = nameConverter.getBlockMapping19().remap(val);
                    this.anyChanges = true;
                }

                if (this.from < Util.getVersionProtocol(this.packConverter.getGson(), "1.19.3")
                        && this.to >= Util.getVersionProtocol(this.packConverter.getGson(), "1.19.3")) {
                    prefix = "minecraft:" + prefix;
                    this.anyChanges = true;
                }

                if (this.anyChanges)
                    value.addProperty("model", prefix + val);
            }
        } else if (entry.getValue() instanceof JsonArray) { // some states have arrays
            for (JsonElement jsonElement : ((JsonArray) entry.getValue())) {
                if (jsonElement instanceof JsonObject) {
                    JsonObject value = (JsonObject) jsonElement;
                    if (value.has("model")) {
                        String[] split = value.get("model").getAsString().split("/");
                        String val = split[split.length - 1];
                        String prefix = value.get("model").getAsString().substring(0,
                                value.get("model").getAsString().length() - val.length());

                        if (this.from < Util.getVersionProtocol(this.packConverter.getGson(), "1.13")
                                && this.to >= Util.getVersionProtocol(this.packConverter.getGson(), "1.13")) {
                            val = nameConverter.getBlockMapping().remap(val);
                            prefix = prefix.replaceAll("blocks", "block");
                            this.anyChanges = true;
                        }

                        if (this.from < Util.getVersionProtocol(this.packConverter.getGson(), "1.14")
                                && this.to >= Util.getVersionProtocol(this.packConverter.getGson(), "1.14")) {
                            val = nameConverter.getNewBlockMapping().remap(val);
                            this.anyChanges = true;
                        }

                        if (this.from < Util.getVersionProtocol(this.packConverter.getGson(), "1.17")
                                && this.to >= Util.getVersionProtocol(this.packConverter.getGson(), "1.17")) {
                            val = nameConverter.getBlockMapping17().remap(val);
                            this.anyChanges = true;
                        }

                        if (this.from < Util.getVersionProtocol(this.packConverter.getGson(), "1.19")
                                && this.to >= Util.getVersionProtocol(this.packConverter.getGson(), "1.19")) {
                            val = nameConverter.getBlockMapping19().remap(val);
                            this.anyChanges = true;
                        }

                        if (this.from < Util.getVersionProtocol(this.packConverter.getGson(), "1.19.3")
                                && this.to >= Util.getVersionProtocol(this.packConverter.getGson(), "1.19.3")) {
                            prefix = "minecraft:" + prefix;
                            this.anyChanges = true;
                        }

                        if (this.anyChanges)
                            value.addProperty("model", prefix + val);
                    }
                }
            }
        }
    }
}