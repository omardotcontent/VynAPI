package studio.meraki.vynapi.platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import studio.meraki.vynapi.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

/**
 * Service loaders are a built-in Java feature that allow us to locate implementations of an interface that vary from one
 * environment to another. In the context of MultiLoader we use this feature to access a mock API in the common code that
 * is swapped out for the platform specific implementation at runtime.
 */
public class Services {

    public static final String MOD_ID = "vynapi";
    public static final String MOD_NAME = "VynAPI";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    /**
     * This code is used to load a service for the current environment. Your implementation of the service must be defined
     * manually by including a text file in META-INF/services named with the fully qualified class name of the service.
     * Inside the file you should write the fully qualified class name of the implementation to load for the platform.
     * For example our file on NeoForge points to NeoForgePlatformHelper while Fabric points to FabricPlatformHelper.
     */
    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz, Services.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
