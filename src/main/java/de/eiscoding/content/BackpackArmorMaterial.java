package de.eiscoding.content;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;

public class BackpackArmorMaterial implements ArmorMaterial {
    private final BackpackType type;

    public BackpackArmorMaterial(BackpackType type) {
        this.type = type;
    }

    @Override
    public int getDurability(EquipmentSlot slot) {
        return ArmorMaterials.LEATHER.getDurability(slot);
    }

    @Override
    public int getProtection(EquipmentSlot slot) {
        return slot == EquipmentSlot.CHEST ? 2 : 0;
    }

    @Override
    public int getEnchantability() {
        return 12;
    }

    @Override
    public SoundEvent getEquipSound() {
        return type.getEquipSound();
    }

    @Override
    public Ingredient getRepairIngredient() {
        Item ingredient = type.getIngredient();
        return ingredient == null ? Ingredient.EMPTY : Ingredient.ofItems(ingredient);
    }

    @Override
    public String getName() {
        return type.getName() + "_backpack";
    }

    @Override
    public float getToughness() {
        return type.getToughness();
    }

    @Override
    public float getKnockbackResistance() {
        return type.getKnockbackResistance();
    }
}
