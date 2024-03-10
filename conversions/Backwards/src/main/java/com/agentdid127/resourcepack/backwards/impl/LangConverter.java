package com.agentdid127.resourcepack.backwards.impl;

import com.agentdid127.converter.util.Logger;
import com.agentdid127.resourcepack.library.PackConverter;
import com.agentdid127.resourcepack.library.RPConverter;
import com.agentdid127.resourcepack.library.Util;
import com.agentdid127.resourcepack.library.utilities.PropertiesEx;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

public class LangConverter extends RPConverter {
    private final String version;
    private final String from;

    public LangConverter(PackConverter packConverter, String fromIn, String versionIn) {
        super(packConverter, "LangConverter", 1);
        this.version = versionIn;
        this.from = fromIn;
    }

    /**
     * Moves Lang (properties) to JSON
     *
     * @throws IOException
     */
    @Override
    public void convert() throws IOException {
        Path path = this.pack.getWorkingPath().resolve("assets" + File.separator + "minecraft" + File.separator + "lang");
        if (!path.toFile().exists())
            return;
        ArrayList<String> models = new ArrayList<String>();
        Files.list(path)
                .filter(path1 -> path1.toString().endsWith(".json"))
                .forEach(model -> {
                    PropertiesEx out = new PropertiesEx();
                    try (InputStream input = new FileInputStream(model.toString())) {
                        JsonObject object = Util.readJson(this.packConverter.getGson(), model, JsonObject.class);

                        if (Util.getVersionProtocol(this.packConverter.getGson(), this.from) > Util
                                .getVersionProtocol(this.packConverter.getGson(), "1.12")
                                && ((Util.getVersionProtocol(this.packConverter.getGson(), this.version) < Util
                                        .getVersionProtocol(this.packConverter.getGson(), "1.13"))
                                        && (Util.getVersionProtocol(this.packConverter.getGson(),
                            this.version) > Util
                                                .getVersionProtocol(this.packConverter.getGson(), "1.13.2")))) {
                            JsonObject id = Util.readJsonResource(this.packConverter.getGson(), "/backwards/lang.json")
                                    .getAsJsonObject("1_13");
                            object.keySet().forEach(key -> {
                                String value = object.get(key).getAsString();
                                for (Map.Entry<String, JsonElement> id2 : id.entrySet()) {
                                    if (key.equals(id2.getKey())) {
                                        out.setProperty(id2.getValue().getAsString(), value);
                                    }
                                }
                            });
                        }

                        if (Util.getVersionProtocol(this.packConverter.getGson(), this.version) <= Util
                                .getVersionProtocol(this.packConverter.getGson(), "1.14")) {
                            JsonObject id = Util.readJsonResource(this.packConverter.getGson(), "/backwards/lang.json")
                                    .getAsJsonObject("1_14");
                            object.keySet().forEach(key -> {
                                String value = object.get(key).getAsString();
                                for (Map.Entry<String, JsonElement> id2 : id.entrySet())
                                    if (key.equals(id2.getKey()))
                                        out.setProperty(id2.getValue().getAsString(), value);
                            });
                        }

                        input.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        int modelNoJson = model.getFileName().toString().indexOf(".json");
                        String file2 = model.getFileName().toString().substring(0, modelNoJson);
                        Logger.log("Saving: " + file2 + ".lang");
                        out.store(
                                new FileOutputStream(
                                    this.pack.getWorkingPath().resolve("assets" + File.separator + "minecraft"
                                                + File.separator + "lang" + File.separator + file2 + ".lang").toFile()),
                                "");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    models.add(model.getFileName().toString());
                });
        for (int i = 0; i < models.size(); i++) {
            Logger.log("Deleting: " + this.pack.getWorkingPath().resolve("assets" + File.separator + "minecraft"
                    + File.separator + "lang" + File.separator + models.get(i)));
            Files.delete(
                this.pack.getWorkingPath().resolve("assets" + File.separator + "minecraft" + File.separator + "lang"
                    + File.separator + models.get(i)));
        }
    }
}
