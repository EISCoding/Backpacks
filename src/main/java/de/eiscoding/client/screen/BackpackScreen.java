package de.eiscoding.client.screen;

import de.eiscoding.content.BackpackScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class BackpackScreen extends HandledScreen<BackpackScreenHandler> {
    private final int columns;
    private final int rows;

    public BackpackScreen(BackpackScreenHandler handler, PlayerInventory playerInventory, Text title) {
        super(handler, playerInventory, title);
        this.columns = handler.getColumns();
        this.rows = handler.getRows();
        this.backgroundWidth = 14 + columns * 18;
        this.backgroundHeight = 18 + rows * 18 + 96;
        this.playerInventoryTitleY = rows * 18 + 32;
    }

    @Override
    protected void init() {
        super.init();
        this.titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = this.x;
        int y = this.y;
        context.fill(x, y, x + backgroundWidth, y + backgroundHeight, 0xFF1B1B1F);
        context.drawBorder(x, y, backgroundWidth, backgroundHeight, 0xFF444444);
        int invY = y + rows * 18 + 12;
        context.fill(x + 1, invY, x + backgroundWidth - 1, invY + 1, 0xFF444444);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
