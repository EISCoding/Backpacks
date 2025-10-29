package de.eiscoding.content;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public enum BackpackType {
    LEATHER("leather", 9, 1, Items.LEATHER, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 8.0f, 0.0f),
    COPPER("copper", 9, 2, Items.COPPER_INGOT, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 10.0f, 0.0f),
    IRON("iron", 9, 3, Items.IRON_INGOT, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 12.0f, 0.0f),
    GOLD("gold", 3, 4, Items.GOLD_INGOT, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 15.0f, 0.0f),
    DIAMOND("diamond", 9, 5, Items.DIAMOND, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 20.0f, 2.0f),
    NETHERITE("netherite", 9, 10, Items.NETHERITE_INGOT, SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE, 25.0f, 3.0f);

    private final String name;
    private final int columns;
    private final int rows;
    private final ItemConvertible ingredient;
    private final RegistryEntry<SoundEvent> equipSound;
    private final float toughness;
    private final float knockbackResistance;

    BackpackType(String name, int columns, int rows, ItemConvertible ingredient, RegistryEntry<SoundEvent> equipSound,
                 float toughness, float knockbackResistance) {
        this.name = name;
        this.columns = columns;
        this.rows = rows;
        this.ingredient = ingredient;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
    }

    public String getName() {
        return name;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public int getSlotCount() {
        return columns * rows;
    }

    public Item getIngredient() {
        return ingredient.asItem();
    }

    public SoundEvent getEquipSound() {
        return equipSound.value();
    }

    public float getToughness() {
        return toughness;
    }

    public float getKnockbackResistance() {
        return knockbackResistance;
    }
}
