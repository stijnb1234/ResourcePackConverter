package com.agentdid127.resourcepack.backwards.impl;

import com.agentdid127.resourcepack.library.PackConverter;
import com.agentdid127.resourcepack.library.RPConverter;
import com.agentdid127.resourcepack.library.pack.Pack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class EnchantPathConverter extends RPConverter {
    public EnchantPathConverter(PackConverter packConverter) {
        super(packConverter, "EnchantPathConverter", 1);
    }

    @Override
    public void convert() throws IOException {
        Path misc = this.pack.getWorkingPath().resolve("assets" + File.separator + "minecraft" + File.separator + "textures" + File.separator + "misc");

        if (misc.resolve("enchanted_glint_item.png").toFile().exists())
            Files.move(misc.resolve("enchanted_glint_item.png"), misc.resolve("enchanted_item_glint.png"));
        else if (misc.resolve("enchanted_glint_entity.png").toFile().exists())
            Files.move(misc.resolve("enchanted_glint_entity.png"), misc.resolve("enchanted_item_glint.png"));

        if (misc.resolve("enchanted_glint_item.png.mcmeta").toFile().exists())
            Files.move(misc.resolve("enchanted_glint_item.png.mcmeta"),
                    misc.resolve("enchanted_item_glint.png.mcmeta"));
        else if (misc.resolve("enchanted_glint_entity.png.mcmeta").toFile().exists())
            Files.move(misc.resolve("enchanted_glint_entity.png.mcmeta"),
                    misc.resolve("enchanted_item_glint.png.mcmeta"));

        Files.deleteIfExists(misc.resolve("enchanted_glint_entity.png.mcmeta"));
        Files.deleteIfExists(misc.resolve("enchanted_glint_item.png.mcmeta"));
        Files.deleteIfExists(misc.resolve("enchanted_glint_entity.png"));
        Files.deleteIfExists(misc.resolve("enchanted_glint_item.png"));
    }
}