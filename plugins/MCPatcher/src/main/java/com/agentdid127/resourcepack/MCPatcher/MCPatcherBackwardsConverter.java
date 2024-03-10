package com.agentdid127.resourcepack.MCPatcher;

import com.agentdid127.converter.util.Logger;
import com.agentdid127.resourcepack.backwards.impl.NameConverter;
import com.agentdid127.resourcepack.library.PackConverter;
import com.agentdid127.resourcepack.library.RPConverter;
import com.agentdid127.resourcepack.library.Util;
import com.agentdid127.resourcepack.library.utilities.PropertiesEx;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MCPatcherBackwardsConverter extends RPConverter {

    public MCPatcherBackwardsConverter(PackConverter packConverter) {
        super(packConverter, "MCPatcherConverter", 1);
    }

    /**
     * Parent conversion for MCPatcher
     *
     * @throws IOException
     */
    @Override
    public void convert() throws IOException {
        Path mc = this.pack.getWorkingPath()
            .resolve("assets" + File.separator + "minecraft");
        if (mc.resolve("optifine").toFile().exists()) {
            if (PackConverter.DEBUG) {
                Logger.log("OptiFine exists, switching to MCPatcher");
            }
            Files.move(mc.resolve("optifine"), mc.resolve("mcpatcher"));
        }

        Path models = mc.resolve("mcpatcher");
        if (models.toFile().exists())
            this.findFiles(models);
    }

    /**
     * Finds all files in Path path
     * 
     * @param path
     * @throws IOException
     */
    protected void findFiles(Path path) throws IOException {
        File directory = new File(path.toString());
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isDirectory()) {
                this.remapProperties(Paths.get(file.getPath()));
                this.findFiles(Paths.get(file.getPath()));
            }
        }
    }

    /**
     * Remaps properties to work in newer versions
     * 
     * @param path
     * @throws IOException
     */
    protected void remapProperties(Path path) throws IOException {
        if (!path.toFile().exists())
            return;
        Files.list(path)
                .filter(path1 -> path1.toString().endsWith(".properties"))
                .forEach(model -> {
                    try (InputStream input = new FileInputStream(model.toString())) {
                        if (PackConverter.DEBUG)
                            Logger.log("Updating:" + model.getFileName());
                        PropertiesEx prop = new PropertiesEx();
                        prop.load(input);

                        try (OutputStream output = new FileOutputStream(model.toString())) {
                            // updates textures
                            if (prop.containsKey("texture"))
                                prop.setProperty("texture", this.replaceTextures(prop));

                            // Updates Item IDs
                            if (prop.containsKey("matchItems"))
                                prop.setProperty("matchItems",
                                    this.updateID("matchItems", prop, "regular").replaceAll("\"", ""));

                            if (prop.containsKey("items"))
                                prop.setProperty("items",
                                    this.updateID("items", prop, "regular").replaceAll("\"", ""));

                            if (prop.containsKey("matchBlocks"))
                                prop.setProperty("matchBlocks",
                                    this.updateID("matchBlocks", prop, "regular").replaceAll("\"", ""));

                            // Saves File
                            prop.store(output, "");
                            output.close();
                        } catch (IOException io) {
                            io.printStackTrace();
                        }
                    } catch (IOException e) {
                        throw Util.propagate(e);
                    }
                });
    }

    /**
     * Replaces texture paths with blocks and items
     * 
     * @param prop
     * @return
     */
    protected String replaceTextures(PropertiesEx prop) {
        NameConverter nameConverter = this.packConverter.getConverter(NameConverter.class);
        String properties = prop.getProperty("texture");
        if (properties.startsWith("textures/block/"))
            properties = "textures/blocks/" + nameConverter.getBlockMapping();
        else if (properties.startsWith("textures/item/"))
            properties = "textures/items/" + nameConverter.getItemMapping();
        return properties;
    }

    /**
     * Fixes item IDs and switches them from a numerical id to minecraft: something
     * 
     * @param type
     * @param prop
     * @return
     */
    protected String updateID(String type, PropertiesEx prop, String selection) {
        JsonObject id = Util.readJsonResource(this.packConverter.getGson(), "/backwards/ids.json").get(selection)
                .getAsJsonObject();
        String[] split = prop.getProperty(type).split(" ");
        String properties2 = " ";
        for (int i = 0; i < split.length; i++)
            if (id.get(split[i]) != null)
                split[i] = id.get(split[i]).getAsString();
        for (String item : split)
            properties2 += item + " ";
        properties2.substring(0, properties2.length() - 1);
        prop.remove("metadata");
        return properties2;
    }
}
