package com.agentdid127.resourcepack.library.pack;

import com.agentdid127.resourcepack.library.Util;
import com.agentdid127.converter.util.Logger;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;

import java.io.IOException;
import java.nio.file.Path;

public class ZipPack extends Pack {
    public ZipPack(Path path) {
        super(path);
    }

    @Override
    public ZipPack.Handler createHandler() {
        return new ZipPack.Handler(this);
    }

    @Override
    public String getFileName() {
        return this.path.getFileName().toString().substring(0,
            this.path.getFileName().toString().length() - 4);
    }

    public static class Handler extends Pack.Handler {
        public Handler(Pack pack) {
            super(pack);
        }

        public Path getConvertedZipPath() {
            return this.pack.getWorkingPath().getParent().resolve(
                this.pack.getWorkingPath().getFileName() + ".zip");
        }

        /**
         * Removes Existing Conversions and starts new one
         * 
         * @throws IOException Any issue with loading the pack, or deleting previous
         *                     packs
         */
        @Override
        public void setup() throws IOException {
            if (this.pack.getWorkingPath().toFile().exists()) {
                Logger.log("  Deleting existing conversion");
                Util.deleteDirectoryAndContents(this.pack.getWorkingPath());
            }

            Path convertedZipPath = this.getConvertedZipPath();
            if (convertedZipPath.toFile().exists()) {
                Logger.log("  Deleting existing conversion zip");
                convertedZipPath.toFile().delete();
            }

          this.pack.getWorkingPath().toFile().mkdir();

            try {
                ZipFile zipFile = new ZipFile(this.pack.getOriginalPath().toFile());
                zipFile.extractAll(this.pack.getWorkingPath().toString());
            } catch (ZipException e) {
                Util.propagate(e);
            }

          Handler.bomRemover(this.pack.getWorkingPath());
            return;
        }

        /**
         * Runs after program is finished. Zips directory.
         * 
         * @throws IOException Any IO exception
         */
        @Override
        public void finish() throws IOException {
            try {
                Logger.log("  Zipping working directory");
                ZipFile zipFile = new ZipFile(this.getConvertedZipPath().toFile());
                ZipParameters parameters = new ZipParameters();
                parameters.setIncludeRootFolder(false);
                zipFile.createSplitZipFileFromFolder(this.pack.getWorkingPath().toFile(), parameters, false, 65536);
            } catch (ZipException e) {
                Util.propagate(e);
            }

            Logger.log("  Deleting working directory");
            Util.deleteDirectoryAndContents(this.pack.getWorkingPath());
        }

        @Override
        public String toString() {
            return "Handler{} " + super.toString();
        }
    }
}
