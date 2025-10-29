package de.eiscoding;

import de.eiscoding.content.BackpackItem;
import de.eiscoding.registry.BackpackRegistries;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Backpacks implements ModInitializer {
    public static final String MOD_ID = "backpacks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        BackpackRegistries.registerAll();
        LOGGER.info("Loaded Backpacks mod");
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static final Item BACKPACK = Registry.register(
            Registries.ITEM,
            new Identifier(MOD_ID, "backpack"),
            new BackpackItem(new Item.Settings()
                    .armor(net.minecraft.item.equipment.ArmorMaterials.LEATHER,
                            net.minecraft.item.equipment.EquipmentType.CHESTPLATE))
    );
}
