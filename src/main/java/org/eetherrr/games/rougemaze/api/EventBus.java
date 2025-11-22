package org.eetherrr.games.rougemaze.api;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class EventBus implements GameEventBus {
	private final ConcurrentHashMap<Object, List<GameEventListener>> listeners = new ConcurrentHashMap<>();
	
	@Override
	public void register(final Object target) {
	
	}
	
	@Override
	public <T extends Event> void addListener(Consumer<T> consumer) {
	
	}
	
	@Override
	public void unregister(Object object) {
	
	}
	
	@Override
	public <T extends Event> T post(T event) {
		return null;
	}
}
