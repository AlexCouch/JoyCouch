package couch.test;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid="joycouchtest", version="1.0", name="JoyCouch Test")
public class JoycouchTest {

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        event.getModLog().debug("This is a test yo!!");
    }
}
