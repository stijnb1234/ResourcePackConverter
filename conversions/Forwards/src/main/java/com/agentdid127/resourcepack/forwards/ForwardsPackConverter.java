package com.agentdid127.resourcepack.forwards;

import com.agentdid127.converter.util.Logger;
import com.agentdid127.resourcepack.forwards.impl.textures.*;
import com.agentdid127.resourcepack.library.PackConverter;
import com.agentdid127.resourcepack.library.Util;
import com.agentdid127.resourcepack.forwards.impl.*;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

public class ForwardsPackConverter extends PackConverter {
    Path INPUT_DIR;

    public ForwardsPackConverter(String from, String to, String light, boolean minify, Path input, boolean debug, boolean unstable) {
        GsonBuilder gsonBuilder = new GsonBuilder().disableHtmlEscaping();
        if (!minify)
            gsonBuilder.setPrettyPrinting();
      this.gson = gsonBuilder.create();
      PackConverter.DEBUG = debug;
        Logger.log(from);
        Logger.log(to);
        INPUT_DIR = input;
      this.converterRunner(from, to, light);
      PackConverter.UNSTABLE = unstable;
    }

    protected void converterRunner(String from, String to, String light) {
        // this needs to be run first, other converters might reference new directory
        // names
        registerConverter(
                new NameConverter(this, Util.getVersionProtocol(this.gson, from), Util.getVersionProtocol(
                    this.gson, to)));

        registerConverter(new PackMetaConverter(this, Util.getVersionProtocol(this.gson, to)));

        if (Util.getVersionProtocol(this.gson, from) < Util.getVersionProtocol(this.gson, "1.9")
                && Util.getVersionProtocol(this.gson, to) >= Util.getVersionProtocol(this.gson, "1.9"))
            registerConverter(
                    new CompassConverter(this, Util.getVersionProtocol(this.gson, to)));

        if (Util.getVersionProtocol(this.gson, from) < Util.getVersionProtocol(this.gson, "1.11")
                && Util.getVersionProtocol(this.gson, to) >= Util.getVersionProtocol(this.gson, "1.11"))
            registerConverter(new SpacesConverter(this));

        registerConverter(new ModelConverter(this, light, Util.getVersionProtocol(this.gson, to),
                Util.getVersionProtocol(this.gson, from)));

        if (Util.getVersionProtocol(this.gson, from) <= Util.getVersionProtocol(this.gson, "1.12.2")
                && Util.getVersionProtocol(this.gson, to) >= Util.getVersionProtocol(this.gson, "1.13")) {
            registerConverter(new SoundsConverter(this));
            registerConverter(new AnimationConverter(this));
            registerConverter(new MapIconConverter(this));
            registerConverter(new WaterConverter(this));
        }

        registerConverter(
                new BlockStateConverter(this, Util.getVersionProtocol(this.gson, from), Util.getVersionProtocol(
                    this.gson, to)));

        if (Util.getVersionProtocol(this.gson, to) >= Util.getVersionProtocol(this.gson, "1.13"))
            registerConverter(new LangConverter(this, from, to));

        registerConverter(new ParticleTextureConverter(this, Util.getVersionProtocol(this.gson, from),
                Util.getVersionProtocol(this.gson, to)));

        registerConverter(new ChestConverter(this));

        if (Util.getVersionProtocol(this.gson, from) <= Util.getVersionProtocol(this.gson, "1.13")
                && Util.getVersionProtocol(this.gson, to) >= Util.getVersionProtocol(this.gson, "1.14.4"))
            registerConverter(new PaintingConverter(this));

        if (Util.getVersionProtocol(this.gson, from) <= Util.getVersionProtocol(this.gson, "1.13.2")
                && Util.getVersionProtocol(this.gson, to) >= Util.getVersionProtocol(this.gson, "1.14"))
            registerConverter(new MobEffectAtlasConverter(this));

        if (Util.getVersionProtocol(this.gson, from) < Util.getVersionProtocol(this.gson, "1.15")
                && Util.getVersionProtocol(this.gson, to) >= Util.getVersionProtocol(this.gson, "1.15"))
            registerConverter(new EnchantConverter(this));

        if (Util.getVersionProtocol(this.gson, from) < Util.getVersionProtocol(this.gson, "1.18")
                && Util.getVersionProtocol(this.gson, to) >= Util.getVersionProtocol(this.gson, "1.18"))
            registerConverter(new ParticleConverter(this));

        registerConverter(new InventoryConverter(this));

        if (Util.getVersionProtocol(this.gson, from) < Util.getVersionProtocol(this.gson, "1.19.3")
                && Util.getVersionProtocol(this.gson, to) >= Util.getVersionProtocol(this.gson, "1.19.3"))
            registerConverter(new AtlasConverter(this));

        if (Util.getVersionProtocol(this.gson, from) < Util.getVersionProtocol(this.gson, "1.19.4")
                && Util.getVersionProtocol(this.gson, to) >= Util.getVersionProtocol(this.gson, "1.19.4"))
            registerConverter(new EnchantPathConverter(this));

        if (Util.getVersionProtocol(this.gson, from) <= Util.getVersionProtocol(this.gson, "1.19.4")
                && Util.getVersionProtocol(this.gson, to) >= Util.getVersionProtocol(this.gson, "1.20"))
            registerConverter(new TitleConverter(this));

    }

    public void runDir() throws IOException {
      this.runDir(this.INPUT_DIR);
    }
}
