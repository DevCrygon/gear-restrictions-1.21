package net.crygon.gearrestrictions;

import net.fabricmc.api.ModInitializer;

public class GearRestrictions implements ModInitializer {
	public static final String MOD_ID = "gearrestrictions";


	@Override
	public void onInitialize() {
		ArmourEquipHandler.register();
	}
}