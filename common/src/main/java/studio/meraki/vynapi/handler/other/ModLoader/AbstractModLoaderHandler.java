package studio.meraki.vynapi.handler.other.ModLoader;

public abstract class AbstractModLoaderHandler implements ModLoaderHandler {

    @Override
    public String getRawGameVersion() {
        // Shared fallback logic every loader can use if it can't resolve its own version string
        return getPlatformGameVersion().orElse("unknown");
    }

    protected abstract java.util.Optional<String> getPlatformGameVersion();
}
