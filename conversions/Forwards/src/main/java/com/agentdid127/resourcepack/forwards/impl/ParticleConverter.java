package com.agentdid127.resourcepack.forwards.impl;

import com.agentdid127.resourcepack.library.PackConverter;
import com.agentdid127.resourcepack.library.RPConverter;
import com.agentdid127.resourcepack.library.pack.Pack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ParticleConverter extends RPConverter {
    private Path particles;

    // Set it up.
    public ParticleConverter(PackConverter packConverter) {
        super(packConverter, "ParticleConverter", 1);
    }

    @Override
    public void convert() throws IOException {
        // The directory to convert
      this.particles = this.pack.getWorkingPath()
                .resolve("assets" + File.separator + "minecraft" + File.separator + "particles");

        // Check if the two merged files exist.
        boolean barrier = false;
        boolean light = false;

        if (this.particles.resolve("barrier.json").toFile().exists())
            barrier = true;
        else if (this.particles.resolve("light.json").toFile().exists())
            light = true;

        // Move around files depending on what exists or what doesn't exist.
        if (barrier) {
            Files.move(this.particles.resolve("barrier.json"), this.particles.resolve("block_marker.json"));
            if (light)
                Files.delete(this.particles.resolve("light.json"));
        } else if (light) {
            Files.move(this.particles.resolve("light.json"), this.particles.resolve("block_marker.json"));
        }
    }
}
