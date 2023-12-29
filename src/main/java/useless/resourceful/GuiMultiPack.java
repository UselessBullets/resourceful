package useless.resourceful;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.options.components.ButtonComponent;
import net.minecraft.client.gui.options.components.TexturePackListComponent;
import net.minecraft.client.render.FontRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.texturepack.TexturePack;
import net.minecraft.client.render.texturepack.TexturePackDefault;
import net.minecraft.client.util.helper.Colors;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiMultiPack extends GuiScreen {
	private static final int BUTTON_HEIGHT = 32;
	private final List<TexturePackButton> packButtons = new ArrayList<>();
	private final List<TexturePackButton> selectedPackButtons = new ArrayList<>();
	private final float scrollAmount = 0.0f;
	private int updateTickCount = 0;
	private int top;
	private int bottom;
	private final int sidePadding = 5;
	private final int centerWidth = 20;
	protected static Minecraft mc = Minecraft.getMinecraft(Minecraft.class);
	public GuiMultiPack(GuiScreen parent) {
		super(parent);
	}
	@Override
	public void init() {
		this.top = 44;
		this.bottom = this.height - 28;
		this.controlList.add(new GuiButton(1, this.width / 2 - 100, height - 24,200, 20, I18n.getInstance().translateKey("gui.options.button.done")));
		createButtons();
	}
	@Override
	public void tick() {
		++this.updateTickCount;
		if (this.updateTickCount >= 40) {
			this.updateTickCount = 0;
			if (mc.texturePackList.updateAvailableTexturePacks()) {
				this.createButtons();
			}
		}
	}

	public void createButtons() {
		this.packButtons.clear();
		this.selectedPackButtons.clear();
		List<TexturePack> texturePacks = mc.texturePackList.availableTexturePacks();

		for (int i = 0; i < TexturePackManager.selectedPacks.size(); ++i) {
			TexturePack texturePack = TexturePackManager.selectedPacks.get(i);
			this.selectedPackButtons.add(new TexturePackButton(i, texturePack));
		}
		selectedPackButtons.add(new TexturePackButton(selectedPackButtons.size(), mc.texturePackList.getDefaultTexturePack()));

		for (int i = 0; i < texturePacks.size(); ++i) {
			TexturePack texturePack = texturePacks.get(i);
			if (texturePack == mc.texturePackList.getDefaultTexturePack()) continue;
			if (TexturePackManager.selectedPacks.contains(texturePack)) continue;
			this.packButtons.add(new TexturePackButton(i, texturePack));
		}

	}
	@Override
	protected void buttonPressed(GuiButton button) {
		if (button.id == 1){
			mc.displayGuiScreen(this.getParentScreen());
		}
	}
	@Override
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		for (int i = 0; i < this.packButtons.size(); ++i) {
			TexturePackButton button = this.packButtons.get(i);
			if (!button.isClickable() || mouseX < 0 || mouseX > width/2 - centerWidth/2 - sidePadding || mouseY < top + 3 + i * 35 || mouseY > top + 3 + i * 35 + 35) continue;
			TexturePackManager.addPack(button.texturePack);
			createButtons();
			return;
		}
		for (int i = 0; i < this.selectedPackButtons.size(); ++i) {
			TexturePackButton button = this.selectedPackButtons.get(i);
			if (!button.isClickable() || mouseX < (width + centerWidth)/2 + sidePadding || mouseX > width - sidePadding || mouseY < top + 3 + i * 35 || mouseY > top + 3 + i * 35 + 35) continue;
			TexturePackManager.removePack(button.texturePack);
			createButtons();
			return;
		}
	}
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTick) {
		drawDefaultBackground();
		drawStringCentered(fontRenderer, I18n.getInstance().translateKey("resourceful.pack.label.available"), (width - centerWidth)/4, 20, 0xFFFFFF);
		drawStringCentered(fontRenderer, I18n.getInstance().translateKey("resourceful.pack.label.selected"),  (width + centerWidth)/2 + ((width - centerWidth)/4), 20, 0xFFFFFF);
		super.drawScreen(mouseX, mouseY, partialTick);
		if (this.packButtons.isEmpty()) {
			mc.fontRenderer.drawCenteredString(I18n.getInstance().translateKey("gui.options.page.texture_packs.label.no_packs"), (width - centerWidth)/4, top + 4, 0x5F7F7F7F);
		}
		for (int i = 0; i < this.packButtons.size(); ++i) {
			TexturePackButton button = this.packButtons.get(i);
			button.render(sidePadding, top + 3 + i * 35, width/2 - centerWidth/2 - sidePadding, mouseX, mouseY);
		}
		for (int i = 0; i < this.selectedPackButtons.size(); ++i) {
			TexturePackButton button = this.selectedPackButtons.get(i);
			button.render((width + centerWidth)/2 + sidePadding, top + 3 + i * 35, width/2 - centerWidth/2 - sidePadding, mouseX, mouseY);
		}
	}
	@Override
	public void drawDefaultBackground() {
		super.drawDefaultBackground();
		if (mc.theWorld == null) {
			Tessellator tessellator = Tessellator.instance;
			GL11.glBindTexture(3553, mc.renderEngine.getTexture("/gui/background.png"));
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			float f1 = 32.0f;
			tessellator.startDrawingQuads();
			tessellator.setColorOpaque_I(0x202020);
			// Left
			tessellator.addVertexWithUV(0.0, this.bottom, 0.0, 0.0, (float)(this.bottom + (int)this.scrollAmount) / f1);
			tessellator.addVertexWithUV((width - centerWidth)/2f, this.bottom, 0.0, ((width - centerWidth)/2f) / f1, (float)(this.bottom + (int)this.scrollAmount) / f1);
			tessellator.addVertexWithUV((width - centerWidth)/2f, this.top, 0.0, ((width - centerWidth)/2f) / f1, (float)(this.top + (int)this.scrollAmount) / f1);
			tessellator.addVertexWithUV(0.0, this.top, 0.0, 0.0, (float)(this.top + (int)this.scrollAmount) / f1);

			// Right
			tessellator.addVertexWithUV((width + centerWidth)/2f, this.bottom, 0.0, ((width + centerWidth)/2f) / f1, (float)(this.bottom + (int)this.scrollAmount) / f1);
			tessellator.addVertexWithUV(this.width, this.bottom, 0.0, (width) / f1, (float)(this.bottom + (int)this.scrollAmount) / f1);
			tessellator.addVertexWithUV(this.width, this.top, 0.0, (width) / f1, (float)(this.top + (int)this.scrollAmount) / f1);
			tessellator.addVertexWithUV((width + centerWidth)/2f, this.top, 0.0, ((width + centerWidth)/2f) / f1, (float)(this.top + (int)this.scrollAmount) / f1);

			tessellator.draw();
		} else {
			this.drawRect(0, 0, this.width, this.top, 0x5F000000);
			this.drawRect(0, this.bottom, this.width, this.height, 0x5F000000);
			this.drawRect((width - centerWidth)/2, this.bottom, (width + centerWidth)/2, this.top, 0x5F000000);
		}
	}
	private static class TexturePackButton {
		public final int index;
		public final TexturePack texturePack;
		public final int height = 32;

		public TexturePackButton(int index, TexturePack texturePack) {
			this.index = index;
			this.texturePack = texturePack;
		}

		public boolean isClickable() {
			return true;
		}

		public void render(int x, int y, int width, int mouseX, int mouseY) {
			Tessellator tessellator = Tessellator.instance;
			FontRenderer fontRenderer = mc.fontRenderer;
			if (mc.texturePackList.selectedTexturePack == this.texturePack) {
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				GL11.glDisable(3553);
				tessellator.startDrawingQuads();
				tessellator.setColorOpaque_I(2139127936);
				tessellator.addVertexWithUV(x - 2, y + 32 + 2, 0.0, 0.0, 1.0);
				tessellator.addVertexWithUV(x + width + 2, y + 32 + 2, 0.0, 1.0, 1.0);
				tessellator.addVertexWithUV(x + width + 2, y - 2, 0.0, 1.0, 1.0);
				tessellator.addVertexWithUV(x - 2, y - 2, 0.0, 0.0, 0.0);
				tessellator.setColorOpaque_I(0);
				tessellator.addVertexWithUV(x - 1, y + 32 + 1, 0.0, 0.0, 1.0);
				tessellator.addVertexWithUV(x + width + 1, y + 32 + 1, 0.0, 1.0, 1.0);
				tessellator.addVertexWithUV(x + width + 1, y - 1, 0.0, 1.0, 1.0);
				tessellator.addVertexWithUV(x - 1, y - 1, 0.0, 0.0, 0.0);
				tessellator.draw();
				GL11.glEnable(3553);
			}
			this.texturePack.bindThumbnailTexture(mc);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			tessellator.startDrawingQuads();
			tessellator.setColorOpaque_I(0xFFFFFF);
			tessellator.addVertexWithUV(x, y + 32, 0.0, 0.0, 1.0);
			tessellator.addVertexWithUV(x + 32, y + 32, 0.0, 1.0, 1.0);
			tessellator.addVertexWithUV(x + 32, y, 0.0, 1.0, 0.0);
			tessellator.addVertexWithUV(x, y, 0.0, 0.0, 0.0);
			tessellator.draw();
			int rgbVersion = 0x808080;
			int format = this.texturePack.manifest.getFormat();
			if (format == 1) {
				fontRenderer.drawString(this.texturePack.manifest.getName(), x + 32 + 2, y + 1, 0xFFFFFF);
				fontRenderer.drawString(this.texturePack.manifest.getDescriptionLine1(), x + 32 + 2, y + 12, 0x808080);
				fontRenderer.drawString(this.texturePack.manifest.getDescriptionLine2(), x + 32 + 2, y + 22, 0x808080);
			} else if (format == -1) {
				fontRenderer.drawString(TextFormatting.RED + this.texturePack.manifest.getName(), x + 32 + 2, y + 1, 0xFFFFFF);
				fontRenderer.drawString(this.texturePack.manifest.getDescriptionLine1(), x + 32 + 2, y + 12, 0x808080);
				fontRenderer.drawString(this.texturePack.manifest.getDescriptionLine2(), x + 32 + 2, y + 22, 0x808080);
				rgbVersion = Colors.allChatColors[TextFormatting.RED.id].getARGB();
			} else {
				I18n i18n = I18n.getInstance();
				fontRenderer.drawString(TextFormatting.RED + this.texturePack.manifest.getName(), x + 32 + 2, y + 1, 0xFFFFFF);
				fontRenderer.drawString(i18n.translateKey("gui.options.page.texture_packs.label.outdated_pack.1"), x + 32 + 2, y + 12, 0x808080);
				fontRenderer.drawString(i18n.translateKey("gui.options.page.texture_packs.label.outdated_pack.2"), x + 32 + 2, y + 22, 0x808080);
				rgbVersion = Colors.allChatColors[TextFormatting.RED.id].getARGB();
			}
			String version = this.texturePack.manifest.getPackVersion();
			int versionWidth = fontRenderer.getStringWidth(version);
			fontRenderer.drawString(this.texturePack.manifest.getPackVersion(), x + width - versionWidth - 2, y + 1, rgbVersion);
		}
	}
}