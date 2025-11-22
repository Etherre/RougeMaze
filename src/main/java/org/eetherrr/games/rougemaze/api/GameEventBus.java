package org.eetherrr.games.rougemaze.api;

import java.util.function.Consumer;

public interface GameEventBus {
	
	// 注册事件监听器
	void register(Object target);
	
	// 添加事件监听器
	<T extends Event> void addListener(Consumer<T> consumer);
	
	// 注销事件监听器
	void unregister(Object object);
	
	// 发布事件到总线
	<T extends Event> T post(T event);
}
