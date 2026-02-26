package studio.meraki.vynapi.model;

import me.abdelaziz.runtime.Environment;

import java.util.function.Consumer;

public interface VynAddon {

    Consumer<Environment> onEnable();

}
