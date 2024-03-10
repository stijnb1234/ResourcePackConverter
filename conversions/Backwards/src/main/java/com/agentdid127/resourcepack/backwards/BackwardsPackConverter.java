package com.agentdid127.resourcepack.backwards;

import com.agentdid127.resourcepack.backwards.impl.*;
import com.agentdid127.resourcepack.backwards.impl.textures.*;
import com.agentdid127.resourcepack.library.PackConverter;
import com.agentdid127.resourcepack.library.Util;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Path;
public class BackwardsPackConverter extends PackConverter {
    Path INPUT_DIR;

    public BackwardsPackConverter(String from, String to, String light, boolean minify, Path input, boolean debug, boolean unstable) {
        GsonBuilder gsonBuilder = new GsonBuilder().disableHtmlEscaping();
        if (!minify)
            gsonBuilder.setPrettyPrinting();
      this.gson = gsonBuilder.create();
      PackConverter.DEBUG = debug;
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

        registerConverter(
                new DeleteFileConverter(this, Util.getVersionProtocol(this.gson, from), Util.getVersionProtocol(
                    this.gson, to)));

        // TODO: backwards title converter for going from 1.20 to anything below

        if (Util.getVersionProtocol(this.gson, from) >= Util.getVersionProtocol(this.gson, "1.19.4")
                && Util.getVersionProtocol(this.gson, to) < Util.getVersionProtocol(this.gson, "1.19.4"))
            registerConverter(new EnchantPathConverter(this));

        if (Util.getVersionProtocol(this.gson, from) > Util.getVersionProtocol(this.gson, "1.18")
                && Util.getVersionProtocol(this.gson, to) <= Util.getVersionProtocol(this.gson, "1.18")) {
            registerConverter(new ParticleConverter(this));
            registerConverter(new InventoryConverter(this));
        }

        if (Util.getVersionProtocol(this.gson, from) >= Util.getVersionProtocol(this.gson, "1.13")
                && Util.getVersionProtocol(this.gson, to) <= Util.getVersionProtocol(this.gson, "1.14.4"))
            registerConverter(new PaintingConverter(this));

        if (Util.getVersionProtocol(this.gson, from) > Util.getVersionProtocol(this.gson, "1.15")
                && Util.getVersionProtocol(this.gson, to) <= Util.getVersionProtocol(this.gson, "1.15")) {
            registerConverter(new EnchantConverter(this));
            registerConverter(new ChestConverter(this));
        }

        registerConverter(new ParticleTextureConverter(this, Util.getVersionProtocol(this.gson, from),
                Util.getVersionProtocol(this.gson, to)));

        if (Util.getVersionProtocol(this.gson, to) <= Util.getVersionProtocol(this.gson, "1.13"))
            registerConverter(new LangConverter(this, from, to));

        if (Util.getVersionProtocol(this.gson, from) >= Util.getVersionProtocol(this.gson, "1.12.2")
                && Util.getVersionProtocol(this.gson, to) <= Util.getVersionProtocol(this.gson, "1.13")) {
            registerConverter(new WaterConverter(this));
            registerConverter(new MapIconConverter(this));
        }

        registerConverter(
                new BlockStateConverter(this, Util.getVersionProtocol(this.gson, from), Util.getVersionProtocol(
                    this.gson, to)));

        registerConverter(new ModelConverter(this, light, Util.getVersionProtocol(this.gson, to),
                Util.getVersionProtocol(this.gson, from)));

        if (Util.getVersionProtocol(this.gson, from) > Util.getVersionProtocol(this.gson, "1.9")
                && Util.getVersionProtocol(this.gson, to) <= Util.getVersionProtocol(this.gson, "1.9"))
            registerConverter(
                    new CompassConverter(this, Util.getVersionProtocol(this.gson, from), Util.getVersionProtocol(
                        this.gson, to)));
    }



    public void runDir() throws IOException {
      this.runDir(this.INPUT_DIR);
    }
}
