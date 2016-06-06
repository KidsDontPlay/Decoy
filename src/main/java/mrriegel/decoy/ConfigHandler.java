package mrriegel.decoy;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {

	public static Configuration config;

	public static int range, maxAge, color;
	public static boolean ignorePlayer, negateExplosion, decoyHome;

	public static void refreshConfig(File file) {
		config = new Configuration(file);
		config.load();

		range = config.getInt("range", Configuration.CATEGORY_GENERAL, 6, 1, 64, "Decoys will attract monster in this range.");
		maxAge = config.getInt("maxAge", Configuration.CATEGORY_GENERAL, 0, 0, Integer.MAX_VALUE, "Decoys will despawn after x seconds. 0 = no despawn.");
		color = config.getInt("color", Configuration.CATEGORY_CLIENT, -1, -1, (int) Math.pow(256, 3), "Color of decoys. -1 = random color.");
		ignorePlayer = config.getBoolean("ignorePlayer", Configuration.CATEGORY_GENERAL, false, "If enabled monsters will ignore players while hugging the decoy.");
		negateExplosion = config.getBoolean("negateExplosion", Configuration.CATEGORY_GENERAL, true, "If enabled explosion by creepers nearby are negated.");
		decoyHome = config.getBoolean("decoyHome", Configuration.CATEGORY_GENERAL, true, "If enabled decoy will move to its home position.");
		if (config.hasChanged()) {
			config.save();
		}
	}

}
