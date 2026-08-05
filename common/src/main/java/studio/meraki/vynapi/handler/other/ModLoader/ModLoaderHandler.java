package studio.meraki.vynapi.handler.other.ModLoader;

public interface ModLoaderHandler {
    boolean isModLoaded(String modid);
    String getRawGameVersion();
}