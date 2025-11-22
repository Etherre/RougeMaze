package org.eetherrr.games.rougemaze.api;

public abstract class GameEventListener<T> {
	//提供事件监听的函数式接口
	public abstract void invoke(Event event);
}
