package mrriegel.decoy;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

	public static Configuration config;

	public static int range;
	public static boolean ignorePlayer, negateExplosion;

	public static void refreshConfig(File file) {
		config = new Configuration(file);
		config.load();

		range = config.getInt("range", Configuration.CATEGORY_GENERAL, 6, 1, 64, "Decoys will attract monster in this range.");
		ignorePlayer = config.getBoolean("ignorePlayer", Configuration.CATEGORY_GENERAL, false, "If enabled monsters will ignore players while hugging the decoy.");
		negateExplosion = config.getBoolean("negateExplosion", Configuration.CATEGORY_GENERAL, true, "If enabled explosion by creepers near are negated.");
		if (config.hasChanged()) {
			config.save();
		}
	}

}
