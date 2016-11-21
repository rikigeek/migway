package migway.demo.dds.gui;

import java.util.List;

import migway.demo.dds.model.Platform;

public interface IPlatformListener {

    public void reflectPlatformUpdate(List<Platform> platforms);
    public void reflectPlatformUpdate(Platform platform);
}
