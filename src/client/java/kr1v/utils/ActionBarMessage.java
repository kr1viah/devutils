package kr1v.utils;

import com.google.common.base.Objects;
import net.minecraft.text.Text;

public class ActionBarMessage {
	private final boolean tinted;
	private final Text text;
	public int timeRemaining;

	public ActionBarMessage(Text text, boolean tinted, int timeRemaining) {
		this.text = text;
		this.tinted = tinted;
		this.timeRemaining = timeRemaining;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ActionBarMessage that)) return false;
		return tinted == that.tinted && Objects.equal(text, that.text);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(text, tinted);
	}

	public Text getText() {
		return text;
	}

	public boolean isTinted() {
		return tinted;
	}
}
