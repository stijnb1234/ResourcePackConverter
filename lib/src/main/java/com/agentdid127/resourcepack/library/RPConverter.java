package com.agentdid127.resourcepack.library;

import com.agentdid127.resourcepack.library.pack.Pack;
import com.agentdid127.converter.Converter;
import java.io.IOException;

public abstract class RPConverter extends Converter {
    
    protected PackConverter packConverter;
    protected Pack pack;

    private final boolean unstable;
    
    public RPConverter(PackConverter packConverter, String name, int priority) {
	super(name, priority);
        this.packConverter = packConverter;
	      pack = null;
        unstable = false;
    }

  public RPConverter(PackConverter packConverter, String name, int priority, boolean unstable) {
    super(name, priority);
    this.packConverter = packConverter;
    pack = null;
    this.unstable = unstable;
  }

    public void convert(Pack pack) throws IOException {
	    this.pack = pack;
      this.convert();
    }

    public boolean isUnstable() {
      return this.unstable;
    }
}
