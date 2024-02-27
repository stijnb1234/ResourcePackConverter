package com.agentdid127.resourcepack.forwards.impl;

import com.agentdid127.converter.util.Logger;
import com.agentdid127.resourcepack.library.PackConverter;
import com.agentdid127.resourcepack.library.RPConverter;
import com.agentdid127.resourcepack.library.Util;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NameConverter extends RPConverter {

    protected int to;
    protected int from;
    protected final Mapping blockMapping = new BlockMapping("1_13");
    protected final Mapping newBlockMapping = new BlockMapping("1_14");
    protected final Mapping blockMapping17 = new BlockMapping("1_17");
    protected final Mapping blockMapping19 = new BlockMapping("1_19");
    protected final Mapping itemMapping = new ItemMapping("1_13");
    protected final Mapping newItemMapping = new ItemMapping("1_14");
    protected final Mapping itemMapping17 = new ItemMapping("1_17");
    protected final Mapping entityMapping = new EntityMapping("1_13");
    protected final Mapping langMapping = new LangMapping("1_13");
    protected final Mapping langMapping14 = new LangMapping("1_14");

    public NameConverter(PackConverter packConverter, int from, int to) {
        super(packConverter, "NameConverter", 0);
        this.from = from;
        this.to = to;
    }

    /**
     * Fixes folder names and file names
     *
     * @throws IOException Error handler
     */
    @Override
    public void convert() throws IOException {
        Path mc = pack.getWorkingPath().resolve("assets" + File.separator + "minecraft");
        // Less than 1.12
        if (from <= Util.getVersionProtocol(packConverter.getGson(), "1.12.2")
                && to > Util.getVersionProtocol(packConverter.getGson(), "1.13")) {
            if (packConverter.DEBUG)
                Logger.log("Finding files that are less than 1.12");
            findFiles(mc);
        }

        // Version is greater than 1.13
        if (to >= Util.getVersionProtocol(packConverter.getGson(), "1.13")) {


            // 1.13 Models
            Path models = pack.getWorkingPath()
                    .resolve("assets" + File.separator + "minecraft" + File.separator + "models");
            if (models.toFile().exists()) {
                // 1.13 block/item name change
                if (models.resolve("blocks").toFile().exists()) {
                    if (models.resolve("block").toFile().exists())
                        Util.deleteDirectoryAndContents(models.resolve("block"));
                    Files.move(models.resolve("blocks"), models.resolve("block"));
                }

                // Update all blocks for 1.13
                renameAll(blockMapping, ".json", models.resolve("block"));
                if (models.resolve("items").toFile().exists()) {
                    if (models.resolve("item").toFile().exists())
                        Util.deleteDirectoryAndContents(models.resolve("item"));
                    Files.move(models.resolve("items"), models.resolve("item"));
                }

                // Update all items for 1.13
                renameAll(itemMapping, ".json", models.resolve("item"));

                // Update 1.14 items
                if (to > Util.getVersionProtocol(packConverter.getGson(), "1.13")) {
                    renameAll(newItemMapping, ".json", models.resolve("item"));
                    if (!models.resolve("item" + File.separator + "ink_sac.json").toFile().exists()
                            && models.resolve("item" + File.separator + "black_dye.json").toFile().exists())
                        Files.copy(models.resolve("item" + File.separator + "black_dye.json"),
                                models.resolve("item" + File.separator + "ink_sac.json"));
                    if (!models.resolve("item" + File.separator + "cocoa_beans.json").toFile().exists()
                            && models.resolve("item" + File.separator + "brown_dye.json").toFile().exists())
                        Files.copy(models.resolve("item" + File.separator + "brown_dye.json"),
                                models.resolve("item" + File.separator + "cocoa_beans.json"));
                    if (!models.resolve("item" + File.separator + "bone_meal.json").toFile().exists()
                            && models.resolve("item" + File.separator + "white_dye.json").toFile().exists())
                        Files.copy(models.resolve("item" + File.separator + "white_dye.json"),
                                models.resolve("item" + File.separator + "bone_meal.json"));
                    if (!models.resolve("item" + File.separator + "lapis_lazuli.json").toFile().exists()
                            && models.resolve("item" + File.separator + "blue_dye.json").toFile().exists())
                        Files.copy(models.resolve("item" + File.separator + "blue_dye.json"),
                                models.resolve("item" + File.separator + "lapis_lazuli.json"));
                }

                if (to > Util.getVersionProtocol(packConverter.getGson(), "1.19")) {
                    renameAll(blockMapping19, ".json", models.resolve("block"));
                }
            }

            // Update BlockStates
            Path blockStates = pack.getWorkingPath()
                    .resolve("assets" + File.separator + "minecraft" + File.separator + "blockstates");

            if (blockStates.toFile().exists())
                renameAll(blockMapping, ".json", blockStates);

            // Update textures
            Path textures = pack.getWorkingPath()
                    .resolve("assets" + File.separator + "minecraft" + File.separator + "textures");
            if (textures.toFile().exists()) {
                if (textures.resolve("blocks").toFile().exists())
                    Files.move(textures.resolve("blocks"), textures.resolve("block"));
                if (from < Util.getVersionProtocol(packConverter.getGson(), "1.13")) {
                    renameAll(blockMapping, ".png", textures.resolve("block"));
                    renameAll(blockMapping, ".png.mcmeta", textures.resolve("block"));
                }
                if (to > Util.getVersionProtocol(packConverter.getGson(), "1.13")) {
                    renameAll(newBlockMapping, ".png", textures.resolve("block"));
                    renameAll(newBlockMapping, ".png.mcmeta", textures.resolve("block"));
                }
                if (from < Util.getVersionProtocol(packConverter.getGson(), "1.13")) {
                    renameAll(itemMapping, ".png", textures.resolve("item"));
                    renameAll(itemMapping, ".png.mcmeta", textures.resolve("item"));
                }
                if (to > Util.getVersionProtocol(packConverter.getGson(), "1.13.2")) {
                    renameAll(newItemMapping, ".png", textures.resolve("item"));
                    renameAll(newItemMapping, ".png.mcmeta", textures.resolve("item"));
                    if (!models.resolve("item" + File.separator + "ink_sac.png").toFile().exists()
                            && models.resolve("item" + File.separator + "black_dye.png").toFile().exists())
                        Files.copy(models.resolve("item" + File.separator + "black_dye.png"),
                                models.resolve("item" + File.separator + "ink_sac.png"));
                    if (!models.resolve("item" + File.separator + "cocoa_beans.png").toFile().exists()
                            && models.resolve("item" + File.separator + "brown_dye.png").toFile().exists())
                        Files.copy(models.resolve("item" + File.separator + "brown_dye.png"),
                                models.resolve("item" + File.separator + "cocoa_beans.png"));
                    if (!models.resolve("item" + File.separator + "bone_meal.png").toFile().exists()
                            && models.resolve("item" + File.separator + "white_dye.png").toFile().exists())
                        Files.copy(models.resolve("item" + File.separator + "white_dye.png"),
                                models.resolve("item" + File.separator + "bone_meal.png"));
                    if (!models.resolve("item" + File.separator + "lapis_lazuli.png").toFile().exists()
                            && models.resolve("item" + File.separator + "blue_dye.png").toFile().exists())
                        Files.copy(models.resolve("item" + File.separator + "blue_dye.png"),
                                models.resolve("item" + File.separator + "lapis_lazuli.png"));
                }
                // 1.16 Iron golems
                if (from < Util.getVersionProtocol(packConverter.getGson(), "1.15")
                        && to >= Util.getVersionProtocol(packConverter.getGson(), "1.15")) {
                    if (!textures.resolve("entity" + File.separator + "iron_golem").toFile().exists())
                        textures.resolve("entity" + File.separator + "iron_golem" + File.separator).toFile().mkdir();
                    if (textures.resolve("entity" + File.separator + "iron_golem.png").toFile().exists())
                        Files.move(textures.resolve("entity" + File.separator + "iron_golem.png"), textures
                                .resolve("entity" + File.separator + "iron_golem" + File.separator + "iron_golem.png"));
                }
                // 1.17 Squid
                if (to >= Util.getVersionProtocol(packConverter.getGson(), "1.17")
                        && from < Util.getVersionProtocol(packConverter.getGson(), "1.17")) {
                    renameAll(blockMapping17, ".png", textures.resolve("block"));
                    renameAll(itemMapping17, ".png", textures.resolve("item"));
                    renameAll(blockMapping17, ".png", models.resolve("block"));
                    renameAll(itemMapping17, ".png", models.resolve("item"));
                    if (!textures.resolve("entity" + File.separator + "squid").toFile().exists())
                        textures.resolve("entity" + File.separator + "squid" + File.separator).toFile().mkdir();
                    if (textures.resolve("entity" + File.separator + "squid.png").toFile().exists())
                        Files.move(textures.resolve("entity" + File.separator + "squid.png"),
                                textures.resolve("entity" + File.separator + "squid" + File.separator + "squid.png"));
                }

                if (to >= Util.getVersionProtocol(packConverter.getGson(), "1.19")
                        && from < Util.getVersionProtocol(packConverter.getGson(), "1.19")) {
                    renameAll(blockMapping19, ".png", textures.resolve("block"));
                }

                // 1.13 End Crystals
                if (textures.resolve("entity" + File.separator + "endercrystal").toFile().exists()
                        && !textures.resolve("entity" + File.separator + "end_crystal").toFile().exists())
                    Files.move(textures.resolve("entity" + File.separator + "endercrystal"),
                            textures.resolve("entity" + File.separator + "end_crystal"));
                findEntityFiles(textures.resolve("entity"));
            }
        }

    }

    /**
     * Finds files in entity folder
     * 
     * @param path
     * @throws IOException
     */
    protected void findEntityFiles(Path path) throws IOException {
        if (path.toFile().exists()) {
            File directory = new File(path.toString());
            File[] fList = directory.listFiles();
            assert fList != null;
            for (File file : fList) {
                if (file.isDirectory()) {
                    renameAll(entityMapping, ".png", Paths.get(file.getPath()));
                    renameAll(entityMapping, ".png.mcmeta", Paths.get(file.getPath()));
                    findEntityFiles(Paths.get(file.getPath()));
                }
            }
        }
    }

    /**
     * Finds files in folders called
     * 
     * @param path
     * @throws IOException
     */
    protected void findFiles(Path path) throws IOException {
        if (path.toFile().exists()) {
            File directory = new File(path.toString());
            File[] fList = directory.listFiles();
            for (File file : fList) {
                if (file.isDirectory()) {
                    if (file.getName().equals("items")) {
                        if (packConverter.DEBUG)
                            Logger.log("Found Items folder, renaming");
                        Util.renameFile(path.resolve(file.getName()), file.getName().replaceAll("items", "item"));
                    }
                    if (file.getName().equals("blocks")) {
                        if (packConverter.DEBUG)
                            Logger.log("Found blocks folder, renaming");
                        Util.renameFile(path.resolve(file.getName()), file.getName().replaceAll("blocks", "block"));
                    }
                    findFiles(Paths.get(file.getPath()));
                }
                if (file.getName().contains("("))
                    Util.renameFile(path.resolve(file.getName()), file.getName().replaceAll("[()]", ""));
                if (!file.getName().equals(file.getName().toLowerCase()))
                    if (packConverter.DEBUG)
                        Logger.log("Renamed: " + file.getName() + "->" + file.getName().toLowerCase());
                Util.renameFile(path.resolve(file.getName()), file.getName().toLowerCase());
            }
        }
    }

    /**
     * Renames folder
     * 
     * @param mapping
     * @param extension
     * @param path
     * @throws IOException
     */
    protected void renameAll(Mapping mapping, String extension, Path path) throws IOException {
        if (path.toFile().exists()) {
            // remap grass blocks in order due to the cyclical way their names have changed,
            // i.e grass -> grass_block, tall_grass -> grass, double_grass -> tall_grass
            List<String> grasses = Arrays.asList("grass", "tall_grass", "double_grass");
            if (from <= Util.getVersionProtocol(packConverter.getGson(), "1.12.2")) {
                if ((path.endsWith("blockstates") || path.endsWith("textures" + File.separator + "block"))) {
                    grasses.stream().forEach(name -> {
                        String newName = mapping.remap(name);
                        Boolean ret = Util.renameFile(Paths.get(path + File.separator + name + extension),
                                newName + extension);
                        if (ret == null)
                            return;
                        if (ret && packConverter.DEBUG) {
                            Logger.log("      Renamed: " + name + extension + "->" + newName + extension);
                        } else if (!ret) {
                            System.err.println(
                                    "      Failed to rename: " + name + extension + "->" + newName + extension);
                        }
                    });
                }
            }
            // remap snow jsons, but not images.
            if (from < Util.getVersionProtocol(packConverter.getGson(), "1.13")
                    && to >= Util.getVersionProtocol(packConverter.getGson(), "1.13")) {
                if (path.resolve("snow_layer.json").toFile().exists())
                    Util.renameFile(path.resolve("snow_layer" + extension), "snow" + extension);
            }
            Files.list(path).forEach(path1 -> {
                if (!path1.toString().endsWith(extension))
                    return;

                String baseName = path1.getFileName().toString().substring(0,
                        path1.getFileName().toString().length() - extension.length());
                // skip the already renamed grass blocks
                if (grasses.contains(baseName)
                        && (path.endsWith("blockstates") || path.endsWith("textures" + File.separator + "block"))) {

                    return;
                }
                String newName = mapping.remap(baseName);
                if (newName != null && !newName.equals(baseName)) {
                    Boolean ret = Util.renameFile(path1, newName + extension);
                    if (ret && packConverter.DEBUG) {
                        Logger.log(
                                "      Renamed: " + path1.getFileName().toString() + "->" + newName + extension);
                    } else if (!ret) {
                        System.err.println("      Failed to rename: " + path1.getFileName().toString() + "->" + newName
                                + extension);
                    }
                }
            });
        }
    }

    public Mapping getBlockMapping() {
        return blockMapping;
    }

    public Mapping getItemMapping() {
        return itemMapping;
    }

    public Mapping getNewBlockMapping() {
        return newBlockMapping;
    }

    public Mapping getNewItemMapping() {
        return newItemMapping;
    }

    public Mapping getItemMapping17() {
        return itemMapping17;
    }

    public Mapping getBlockMapping17() {
        return blockMapping17;
    }

    public Mapping getBlockMapping19() {
        return blockMapping19;
    }

    protected abstract static class Mapping {
        protected final Map<String, String> mapping = new HashMap<>();

        public Mapping(String version) {
            load(version);
        }

        protected abstract void load(String version);

        /**
         * @return remapped or in if not present
         */
        public String remap(String in) {
            return mapping.getOrDefault(in, in);
        }
    }

    protected class BlockMapping extends Mapping {

        public BlockMapping(String version) {
            super(version);
        }

        @Override
        protected void load(String version) {
            JsonObject blocks = Util.readJsonResource(packConverter.getGson(), "/forwards/blocks.json")
                    .getAsJsonObject(version);
            if (blocks == null)
                return;
            for (Map.Entry<String, JsonElement> entry : blocks.entrySet()) {
                this.mapping.put(entry.getKey(), entry.getValue().getAsString());
            }
        }
    }

    protected class LangMapping extends Mapping {

        public LangMapping(String version) {
            super(version);
        }

        @Override
        protected void load(String version) {
            JsonObject entities = Util.readJsonResource(packConverter.getGson(), "/forwards/lang.json")
                    .getAsJsonObject(version);
            if (entities == null)
                return;
            for (Map.Entry<String, JsonElement> entry : entities.entrySet()) {
                this.mapping.put(entry.getKey(), entry.getValue().getAsString());
            }
        }
    }

    protected class EntityMapping extends Mapping {

        public EntityMapping(String version) {
            super(version);
        }

        @Override
        protected void load(String version) {
            JsonObject entities = Util.readJsonResource(packConverter.getGson(), "/forwards/entities.json");
            if (entities == null)
                return;
            for (Map.Entry<String, JsonElement> entry : entities.entrySet()) {
                this.mapping.put(entry.getKey(), entry.getValue().getAsString());
            }
        }
    }

    protected class ItemMapping extends Mapping {

        public ItemMapping(String version) {
            super(version);
        }

        @Override
        protected void load(String version) {
            JsonObject items = Util.readJsonResource(packConverter.getGson(), "/forwards/items.json")
                    .getAsJsonObject(version);
            if (items == null)
                return;
            for (Map.Entry<String, JsonElement> entry : items.entrySet()) {
                this.mapping.put(entry.getKey(), entry.getValue().getAsString());
            }
        }
    }


}
