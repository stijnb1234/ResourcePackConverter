package com.agentdid127.resourcepack.forwards.impl;

import com.agentdid127.converter.util.Logger;
import com.agentdid127.resourcepack.library.PackConverter;
import com.agentdid127.resourcepack.library.RPConverter;
import com.agentdid127.resourcepack.library.Util;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class AnimationConverter extends RPConverter {
    public AnimationConverter(PackConverter packConverter) {
        super(packConverter, "AnimationConverter", 1);
    }

    @Override
    public void convert() throws IOException {
        this.fixAnimations(this.pack.getWorkingPath().resolve(
                "assets" + File.separator + "minecraft" + File.separator + "textures" + File.separator + "block"));
        this.fixAnimations(this.pack.getWorkingPath().resolve(
                "assets" + File.separator + "minecraft" + File.separator + "textures" + File.separator + "item"));
    }

    /**
     * Updats animated images to newer versions
     * 
     * @param animations
     * @throws IOException
     */
    protected void fixAnimations(Path animations) throws IOException {
        if (!animations.toFile().exists())
            return;
        Files.list(animations)
                .filter(file -> file.toString().endsWith(".png.mcmeta"))
                .forEach(file -> {
                    try {
                        JsonObject json = Util.readJson(this.packConverter.getGson(), file);

                        boolean anyChanges = false;
                        JsonElement animationElement = json.get("animation");
                        if (animationElement instanceof JsonObject) {
                            JsonObject animationObject = (JsonObject) animationElement;

                            // TODO: Confirm this doesn't break any packs
                            animationObject.remove("width");
                            animationObject.remove("height");

                            anyChanges = true;
                        }

                        if (anyChanges) {
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
}