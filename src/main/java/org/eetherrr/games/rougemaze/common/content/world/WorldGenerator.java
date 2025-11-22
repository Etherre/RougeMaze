package org.eetherrr.games.rougemaze.common.content.world;

import org.eetherrr.games.rougemaze.common.content.world.base.Direction;
import org.eetherrr.games.rougemaze.common.content.world.base.Position;
import org.eetherrr.games.rougemaze.common.content.world.room.BaseRoom;
import org.eetherrr.games.rougemaze.common.content.world.room.EndRoom;
import org.eetherrr.games.rougemaze.common.content.world.room.MazeRoom;
import org.eetherrr.games.rougemaze.common.content.world.room.StartRoom;
import org.eetherrr.games.rougemaze.common.scene.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldGenerator {
	// 房间类型列表，可以在此添加新的房间类型
	private static final List<Class<? extends BaseRoom>> ROOM_TYPES = new ArrayList<>();

	// 初始化房间类型列表
	static {
		ROOM_TYPES.add(BaseRoom.class);
		ROOM_TYPES.add(MazeRoom.class);
		// 可以继续添加更多房间类型
		// ROOM_TYPES.add(CustomRoom.class);
	}

	// 维护一个房间二维数组，用于保存世界地图
	public final BaseRoom[][] worldMap;
	private final Random random;
	private final Position startPosition; // 起始房间的位置
	private final Position endPosition; // 结束房间的位置
	
	public WorldGenerator() {
		this.worldMap = new BaseRoom[Config.WORLD_SIZE][Config.WORLD_SIZE];
		this.random = new Random();
		// 随机选择起始点
		this.startPosition = new Position(
			random.nextInt(Config.WORLD_SIZE),
			random.nextInt(Config.WORLD_SIZE));
		this.endPosition = new Position(
			random.nextInt(Config.WORLD_SIZE),
			random.nextInt(Config.WORLD_SIZE));
		// 初始化世界
		initWorld();
		// 生成连通图，连接房间
		connectRooms();
	}
	
	private void connectRooms() {
		// 访问表标记对应房间的访问状态
		boolean[][] visited = new boolean[Config.WORLD_SIZE][Config.WORLD_SIZE];
		for(int i = 0; i<Config.WORLD_SIZE; i++) {
			for(int j = 0; j<Config.WORLD_SIZE; j++) {
				visited[i][j] = false;
			}
		}
		// 使用随机广度优先搜索算法连接所有房间
		List<Position> queue = new ArrayList<>();
		// 添加起始点到队列
		queue.add(startPosition);
		visited[startPosition.x()][startPosition.y()] = true;
		while(!queue.isEmpty()) {
			// 随机选择队列中的一个位置
			int index = random.nextInt(queue.size());
			Position current = queue.get(index);
			queue.remove(index);
			// 检查四个方向
			for(Direction direction : Direction.values()) {
				Position next = current.getNextPosition(direction);
				// 检查下一个位置是否有效且未访问
				if(next.isValid(
					Config.WORLD_SIZE,
					Config.WORLD_SIZE) && !visited[next.x()][next.y()]) {
					// 标记为已访问
					visited[next.x()][next.y()] = true;
					// 添加到队列中
					queue.add(next);
					// 获取当前房间和下一个房间
					BaseRoom currentRoom = worldMap[current.x()][current.y()];
					BaseRoom nextRoom = worldMap[next.x()][next.y()];
					// 获取两个房间的相邻方向
					Direction oppositeDirection = Direction.opposite(direction);
					// 为相邻房间设置对齐的门
					createAlignedGates(currentRoom, nextRoom, direction);
					// 设置两个房间的相邻关系
					currentRoom.setNeighbor(direction, nextRoom);
					nextRoom.setNeighbor(oppositeDirection, currentRoom);
				}
			}
		}
	}
	
	/**
	 * 为相邻房间创建对齐的门
	 *
	 * @param room1     第一个房间
	 * @param room2     第二个房间
	 * @param direction 从room1到room2的方向
	 */
	private void createAlignedGates(BaseRoom room1, BaseRoom room2, Direction direction) {
		// 在两个房间的相邻边界上随机选择一个位置放置门
		int gatePosition;
		Position gate1, gate2;
		switch(direction) {
			case NORTH: // room2在room1的上方
				gatePosition = 1+random.nextInt(Config.ROOM_COLS-2); // 避免角落
				gate1 = new Position(0, gatePosition); // room1的上边界
				gate2 = new Position(Config.ROOM_ROWS-1, gatePosition); // room2的下边界
				break;
			case EAST: // room2在room1的右侧
				gatePosition = 1+random.nextInt(Config.ROOM_ROWS-2); // 避免角落
				gate1 = new Position(gatePosition, Config.ROOM_COLS-1); // room1的右边界
				gate2 = new Position(gatePosition, 0); // room2的左边界
				break;
			case SOUTH: // room2在room1的下方
				gatePosition = 1+random.nextInt(Config.ROOM_COLS-2); // 避免角落
				gate1 = new Position(Config.ROOM_ROWS-1, gatePosition); // room1的下边界
				gate2 = new Position(0, gatePosition); // room2的上边界
				break;
			case WEST: // room2在room1的左侧
				gatePosition = 1+random.nextInt(Config.ROOM_ROWS-2); // 避免角落
				gate1 = new Position(gatePosition, 0); // room1的左边界
				gate2 = new Position(gatePosition, Config.ROOM_COLS-1); // room2的右边界
				break;
			default:
				return;
		}
		// 在选定的位置放置门
		room1.placeGate(gate1);
		room2.placeGate(gate2);
	}
	
	private void initWorld() {
		for(int i = 0; i<Config.WORLD_SIZE; i++) {
			for(int j = 0; j<Config.WORLD_SIZE; j++) {
				// 如果是起始位置，创建StartRoom而不是BaseRoom
				if(i==startPosition.x() && j==startPosition.y()) {
					worldMap[i][j] = new StartRoom();
				}else if(i==endPosition.x() && j==endPosition.y()) {
					worldMap[i][j] = new EndRoom();
				}else {
					// 随机选择一种房间类型进行创建
					try {
						int randomIndex = random.nextInt(ROOM_TYPES.size());
						Class<? extends BaseRoom> roomClass = ROOM_TYPES.get(randomIndex);
						worldMap[i][j] = roomClass.getDeclaredConstructor().newInstance();
					}catch(Exception e) {
						// 如果创建失败，默认创建BaseRoom
						worldMap[i][j] = new BaseRoom();
					}
				}
			}
		}
	}
	
	// 获取起始房间的位置
	public Position getStartPosition() {
		return startPosition;
	}
}