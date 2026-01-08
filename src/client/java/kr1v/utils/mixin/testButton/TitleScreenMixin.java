package kr1v.utils.mixin.testButton;

import kr1v.utils.config.TestWorld;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
	protected TitleScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "addDevelopmentWidgets", at = @At("HEAD"), cancellable = true)
	private void addTestWorldWidget(int y, int spacingY, CallbackInfoReturnable<Integer> cir) {
		if (!TestWorld.ADD_TEST_WORLD_BUTTON.getBooleanValue()) return;
		String buttonName = TestWorld.PERSIST.getBooleanValue() ? "Open Test World" : "Create Test World";
		this.addDrawableChild(
				ButtonWidget.builder(Text.literal(buttonName), button -> TestWorld.doWorld())
						.dimensions(this.width / 2 - 100, y += spacingY, 200, 20)
						.build()
		);
		cir.setReturnValue(y);
	}
}
