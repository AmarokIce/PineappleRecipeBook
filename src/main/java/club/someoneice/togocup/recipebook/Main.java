package club.someoneice.togocup.recipebook;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("All")
@Mod(modid = Main.MODID)
public class Main {
    public static final String MODID = "pineapple_recipe_book";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        JarUtil.getInstance().read();
    }
}
