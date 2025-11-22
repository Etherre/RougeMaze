package org.eetherrr.games.rougemaze.api.event.button;

import org.eetherrr.games.rougemaze.api.Event;

public class ButtonClickEvent extends Event {
	private final String buttonLabel;
	
	public ButtonClickEvent(String buttonLabel) {
		this.buttonLabel = buttonLabel;
	}
	
	public String getButtonLabel() {
		return buttonLabel;
	}
}
