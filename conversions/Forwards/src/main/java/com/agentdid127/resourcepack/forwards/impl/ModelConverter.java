package com.agentdid127.resourcepack.forwards.impl;

import com.agentdid127.converter.util.Logger;
import com.agentdid127.resourcepack.library.PackConverter;
import com.agentdid127.resourcepack.library.RPConverter;
import com.agentdid127.resourcepack.library.Util;

import com.agentdid127.resourcepack.library.utilities.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

public class ModelConverter extends RPConverter {
    private final int version;
    private final int from;
    protected String light = "none";

    public ModelConverter(PackConverter packConverter, String lightIn, int versionIn, int fromIn) {
        super(packConverter, "ModelConverter", 1);
        this.light = lightIn;
        this.version = versionIn;
        this.from = fromIn;
    }

    /**
     * Runs findfiles with the directory Models
     *
     * @throws IOException
     */
    @Override
    public void convert() throws IOException {
        Path models = this.pack.getWorkingPath()
            .resolve("assets" + File.separator + "minecraft" + File.separator + "models");
        if (!models.toFile().exists())
            return;
        this.findFiles(models);
    }

    /**
     * Recursively finds files with Path path and runs remapModelJson
     *
     * @param path
     * @throws IOException
     */
    protected void findFiles(Path path) throws IOException {
        File directory = new File(path.toString());
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isDirectory()) {
                this.remapModelJson(Paths.get(file.getPath()));
                this.findFiles(Paths.get(file.getPath()));
            }
        }
    }

    /**
     * Updates model Json to newer versions
     *
     * @param path
     * @throws IOException
     */
    protected void remapModelJson(Path path) throws IOException {
        if (!path.toFile().exists())
            return;
        Files.list(path)
            .filter(path1 -> path1.toString().endsWith(".json"))
            .forEach(model -> {
                try {
                    JsonObject jsonObject;
                    if (Util.readJson(this.packConverter.getGson(), model) != null
                        && Util.readJson(this.packConverter.getGson(), model).isJsonObject())
                        jsonObject = Util.readJson(this.packConverter.getGson(), model);
                    else {
                        if (PackConverter.DEBUG) {
                            Logger.log("Could not convert model: " + model.getFileName());
                            if (Util.readJson(this.packConverter.getGson(), model) == null)
                                Logger.log("Check for Syntax Errors in file.");
                            else
                                Logger.log("File is not JSON Object.");
                        }
                        return;
                    }

                    // GUI light system for 1.15.2
                    if (!this.light.equals("none") && (this.light.equals("front") || this.light.equals("side")))
                        jsonObject.addProperty("gui_light", this.light);

                    // minify the json so we can replace spaces in paths easily
                    // TODO Improvement: handle this in a cleaner way?
                    String content = jsonObject.toString();
                    content = content.replaceAll("items/", "item/");
                    content = content.replaceAll("blocks/", "block/");
                    content = content.replaceAll(" ", "_");

                    // handle the remapping of textures, for models that use default texture names
                    jsonObject = this.packConverter.getGson().fromJson(content, JsonObject.class);
                    if (jsonObject.keySet().isEmpty() || jsonObject.entrySet().isEmpty()) {
                        Logger.log("Model '" + model.getFileName() + "' was empty, skipping...");
                        return;
                    }

                    if (jsonObject.has("textures") && jsonObject.get("textures").isJsonObject()) {
                        NameConverter nameConverter = this.packConverter.getConverter(NameConverter.class);

                        JsonObject initialTextureObject = jsonObject.getAsJsonObject("textures");
                        JsonObject textureObject = initialTextureObject.deepCopy();
                        for (Map.Entry<String, JsonElement> entry : initialTextureObject.entrySet()) {
                            String value = entry.getValue().getAsString();
                            textureObject.remove(entry.getKey());

                            // 1.8 mappings
                            if (this.version >= Util.getVersionProtocol(this.packConverter.getGson(), "1.9")) {
                                if (entry.getKey().equals("layer0")
                                    && (!value.startsWith(path.getFileName().toString())
                                    && !value.startsWith("minecraft:" + path.getFileName())))
                                    value = path.getFileName() + "/" + value;
                            }

                            // 1.13 Mappings
                            if (this.version >= Util.getVersionProtocol(this.packConverter.getGson(), "1.13")) {
                                if (value.startsWith("block/")) {
                                    value = "block/" + nameConverter.getBlockMapping()
                                        .remap(value.substring("block/".length())).toLowerCase()
                                        .replaceAll("[()]", "");
                                    if (PackConverter.DEBUG) {
                                        Logger.log(value.substring("block/".length()).toLowerCase()
                                            .replaceAll("[()]", ""));
                                        Logger.log(nameConverter.getBlockMapping()
                                            .remap(value.substring("block/".length())).toLowerCase()
                                            .replaceAll("[()]", ""));
                                    }
                                } else if (value.startsWith("item/")) {
                                    value = "item/" + nameConverter.getItemMapping()
                                        .remap(value.substring("item/".length())).toLowerCase()
                                        .replaceAll("[()]", "");
                                } else
                                    value = value.toLowerCase().replaceAll("[()]", "");
                            }

                            // 1.14 Mappings
                            if (this.version >= Util.getVersionProtocol(this.packConverter.getGson(), "1.14")) {
                                if (value.startsWith("block/"))
                                    value = "block/" + nameConverter.getNewBlockMapping()
                                        .remap(value.substring("block/".length()));
                            }

                            // 1.17 Mappings
                            if (this.version >= Util.getVersionProtocol(this.packConverter.getGson(), "1.17")) {
                                if (value.startsWith("block/")) {
                                    value = "block/" + nameConverter.getBlockMapping17()
                                        .remap(value.substring("block/".length())).toLowerCase()
                                        .replaceAll("[()]", "");
                                } else if (value.startsWith("item/")) {
                                    value = "item/" + nameConverter.getItemMapping17()
                                        .remap(value.substring("item/".length())).toLowerCase()
                                        .replaceAll("[()]", "");
                                }
                                value = value.toLowerCase().replaceAll("[()]", "");
                            }

                            // 1.19 Mappings
                            if (this.version >= Util.getVersionProtocol(this.packConverter.getGson(), "1.19")) {
                                if (value.startsWith("block/")) {
                                    value = "block/" + nameConverter.getBlockMapping19()
                                        .remap(value.substring("block/".length())).toLowerCase()
                                        .replaceAll("[()]", "");
                                }
                                value = value.toLowerCase().replaceAll("[()]", "");
                            }

                            // Dyes
                            if (value.startsWith("item/") && value.contains("dye")) {
                                if (this.version > Util.getVersionProtocol(this.packConverter.getGson(), "1.13"))
                                    value = "item/" + nameConverter.getNewItemMapping()
                                        .remap(value.substring("item/".length()));
                            }

                            // 1.19.3 Mappings
                            if (this.version >= Util.getVersionProtocol(this.packConverter.getGson(), "1.19.3")
                                && this.from < Util.getVersionProtocol(this.packConverter.getGson(), "1.19.3")) {
                                if (!value.startsWith("minecraft:") && !value.startsWith("#"))
                                    value = "minecraft:" + value;
                            }

                            if (!textureObject.has(entry.getKey()))
                                textureObject.addProperty(entry.getKey(), value);
                        }

                        jsonObject.remove("textures");
                        jsonObject.add("textures", textureObject);
                    }

                    // Fix Display Model For Packs (<= 1.8.9)
                    if (jsonObject.has("display")
                        && this.from <= Util.getVersionProtocol(this.packConverter.getGson(), "1.8")) {
                        JsonObject display = ModelConverter.updateDisplay(
                            this.packConverter.getGson(), jsonObject.remove("display").getAsJsonObject());
                        jsonObject.add("display", display);
                    }

                    if (jsonObject.has("overrides")) {
                        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                            if (entry.getKey().equals("overrides")) {
                                JsonArray overrides = jsonObject.get("overrides").getAsJsonArray();
                                JsonArray overrides2 = new JsonArray();
                                for (int i = 0; i < overrides.size(); i++) {
                                    JsonObject object = overrides.get(i).getAsJsonObject();
                                    for (Map.Entry<String, JsonElement> json : object.entrySet()) {
                                        if (json.getKey().equals("model"))
                                            object.addProperty(json.getKey(),
                                                json.getValue().getAsString().replaceAll("[()]", ""));
                                        else
                                            object.add(json.getKey(), json.getValue());
                                    }
                                    overrides2.add(object);
                                }
                                jsonObject.add(entry.getKey(), overrides2);
                            }
                        }
                    }

                    // Parent Stuff
                    if (jsonObject.has("parent")) {
                        // Change parent to lowercase
                        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                            if (entry.getKey().equals("parent")) {
                                String parent = entry.getValue().getAsString().toLowerCase();

                                parent = parent.replace(" ", "_");

                                // Get block/item parents renamed
                                if (this.from < Util.getVersionProtocol(this.packConverter.getGson(), "1.13")
                                    && this.version >= Util.getVersionProtocol(this.packConverter.getGson(), "1.13")) {
                                    if (parent.startsWith("block/"))
                                        parent = this.setParent("block/", "/forwards/blocks.json", parent, "1_13");
                                    else if (parent.startsWith("item/"))
                                        parent = this.setParent("item/", "/forwards/items.json", parent, "1_13");
                                }

                                if (this.from < Util.getVersionProtocol(this.packConverter.getGson(), "1.14")
                                    && this.version >= Util.getVersionProtocol(this.packConverter.getGson(), "1.14")) {
                                    if (parent.startsWith("block/"))
                                        parent = this.setParent("block/", "/forwards/blocks.json", parent, "1_14");
                                    else if (parent.startsWith("item/"))
                                        parent = this.setParent("item/", "/forwards/items.json", parent, "1_14");
                                }

                                if (this.from < Util.getVersionProtocol(this.packConverter.getGson(), "1.17")
                                    && this.version >= Util.getVersionProtocol(this.packConverter.getGson(), "1.17")) {
                                    if (parent.startsWith("block/"))
                                        parent = this.setParent("block/", "/forwards/blocks.json", parent, "1_17");
                                    else if (parent.startsWith("item/"))
                                        parent = this.setParent("item/", "/forwards/items.json", parent, "1_17");
                                }

                                if (this.from < Util.getVersionProtocol(this.packConverter.getGson(), "1.19")
                                    && this.version >= Util.getVersionProtocol(this.packConverter.getGson(), "1.19")) {
                                    if (parent.startsWith("block/"))
                                        parent = this.setParent("block/", "/forwards/blocks.json", parent, "1_19");
                                }

                                if (this.version >= Util.getVersionProtocol(this.packConverter.getGson(), "1.19.3")
                                    && this.from < Util.getVersionProtocol(this.packConverter.getGson(), "1.19.3")) {
                                    if (!parent.startsWith("minecraft:"))
                                        parent = "minecraft:" + parent;
                                }

                                jsonObject.addProperty(entry.getKey(), parent);
                            }
                        }
                    }

                    if (!Util.readJson(this.packConverter.getGson(), model).equals(jsonObject)) {
                        if (PackConverter.DEBUG)
                            Logger.log("Updating Model: " + model.getFileName());
                        Files.write(model, Collections.singleton(
                                this.packConverter.getGson().toJson(jsonObject)),
                            StandardCharsets.UTF_8);
                    }
                } catch (IOException e) {
                    throw Util.propagate(e);
                }
            });
    }

    /**
     * Gets parent object and sets a new one
     *
     * @param prefix prefix of file path
     * @param path   File path of json control
     * @param parent Parent String
     * @return New string with changed parent.
     */
    protected String setParent(String prefix, String path, String parent, String item) {
        String parent2 = parent.replace(prefix, "");
        JsonObject file = Util.readJsonResource(this.packConverter.getGson(), path).getAsJsonObject(item);
        if (file == null) {
            Logger.log("Prefix Failed on: " + parent);
            return "";
        }

        return file.has(parent2) ? prefix + file.get(parent2).getAsString() : parent;
    }

    protected static JsonObject updateDisplay(Gson gson, JsonObject display) {
        JsonObject defaults = Util.readJsonResource(gson, "/forwards/display.json");
        if (display == null) {
            return defaults.deepCopy();
        }

        // First Person
        if (display.has("firstperson")) {
            JsonObject firstPerson = display.remove("firstperson").getAsJsonObject();
            display.add("firstperson_righthand",
                ModelConverter.updateDisplayFirstPerson(gson, firstPerson));
        } else if (!display.has("firstperson_righthand")) {
            JsonObject rightHand = defaults.get("firstperson_righthand")
                .getAsJsonObject().deepCopy();
            display.add("firstperson_righthand", rightHand);
        }

        if (!display.has("firstperson_lefthand")) {
            display.add("firstperson_lefthand",
                ModelConverter.getLeftHand(gson,
                    display.get("firstperson_righthand").getAsJsonObject().deepCopy()
                ));
        }

        // Third Person
        if (display.has("thirdperson")) {
            JsonObject thirdPerson = display.remove("thirdperson").getAsJsonObject();
            display.add("thirdperson_righthand",
                ModelConverter.updateDisplayThirdPerson(gson, thirdPerson));
        } else if (!display.has("thirdperson_righthand")) {
            JsonObject rightHand = defaults.get("thirdperson_righthand")
                .getAsJsonObject().deepCopy();
            display.add("thirdperson_righthand", rightHand);
        }

        if (!display.has("thirdperson_lefthand")) {
            display.add("thirdperson_lefthand",
                ModelConverter.getLeftHand(gson,
                    display.get("thirdperson_righthand").getAsJsonObject().deepCopy()
                ));
        }

        if (!display.has("ground")) {
            display.add("ground",
                defaults.get("ground").getAsJsonObject().deepCopy());
        }

        if (!display.has("head")) {
            display.add("head",
                defaults.get("head").getAsJsonObject().deepCopy());
        }

        return display;
    }

    private static JsonObject getLeftHand(Gson gson, JsonObject old) {
        JsonObject newObject = old.deepCopy();
        if (old.has("rotation")) {
            JsonArray oldRotation = newObject.remove("rotation").getAsJsonArray();
            JsonArray rotation = new JsonArray();
            rotation.add(oldRotation.get(0).getAsNumber());
            rotation.add(0 - oldRotation.get(1).getAsDouble());
            rotation.add(0 - oldRotation.get(2).getAsDouble());
            newObject.add("rotation",
                rotation);
        }

        return newObject;
    }

    private static JsonObject updateDisplayFirstPerson(Gson gson, JsonObject old) {
        JsonObject newObject = old.deepCopy();
        if (old.has("rotation")) {
            JsonArray rotation = newObject.remove("rotation").getAsJsonArray();
            newObject.add("rotation",
                JsonUtil.add(
                    rotation,
                    JsonUtil.asArray(gson, "[0, 45, 0]")));
        }

        if (old.has("translation")) {
            JsonArray translation = newObject.remove("translation").getAsJsonArray();
            newObject.add("translation",
                JsonUtil.add(
                    JsonUtil.multiply(
                        JsonUtil.subtract(
                            translation,
                            JsonUtil.asArray(gson, "[0, 4, 2]")
                        ),
                        JsonUtil.asArray(gson, "[0.4, 0.4, 0.4]")
                    ),
                    JsonUtil.asArray(gson, "[1.13, 3.2, 1.13]")));
        }

        if (old.has("scale")) {
            JsonArray scale = newObject.remove("scale").getAsJsonArray();
            newObject.add("scale",
                JsonUtil.multiply(
                    scale,
                    JsonUtil.asArray(gson, "[0.4, 0.4, 0.4]"))
            );
        }

        return newObject;
    }

    private static JsonObject updateDisplayThirdPerson(Gson gson, JsonObject old) {
        JsonObject newObject = old.deepCopy();
        if (old.has("rotation")) {
            JsonArray rotation = newObject.remove("rotation").getAsJsonArray();
            newObject.add("rotation",
                JsonUtil.add(
                    JsonUtil.multiply(
                        rotation,
                        JsonUtil.asArray(gson, "[1, -1, -1]")
                    ),
                    JsonUtil.asArray(gson, "[0, 0, 20]")
                )
            );
        }

        if (old.has("translation")) {
            JsonArray translation = newObject.remove("translation").getAsJsonArray();
            newObject.add("translation",
                JsonUtil.add(
                    JsonUtil.multiply(
                        translation,
                        JsonUtil.asArray(gson, "[1, 1, -1]")
                    ),
                    JsonUtil.asArray(gson, "[0, 2.75, -3]")
                )
            );
        }

        // For keeping order
        if (old.has("scale")) {
            JsonArray scale = newObject.remove("scale").getAsJsonArray();
            newObject.add("scale", scale);
        }

        return newObject;
    }
}
