package io.thebitspud.libgdxstrategy.map;

public enum Tile {
	VOID(0, MovementProfile.SOLID),
	GRASS(1, MovementProfile.FREE),
	SAND(2, MovementProfile.FREE),
	FOREST(3,  MovementProfile.REDUCE),
	MOUNTAIN(4,  MovementProfile.SOLID),
	WATER(5,  MovementProfile.SOLID);

	private final int id;
	private final MovementProfile profile;

	Tile(int id, MovementProfile profile) {
		this.id = id;
		this.profile = profile;
	}

	public int getID() {
		return id;
	}

	public boolean isSolid() {
		return profile == MovementProfile.SOLID;
	}

	public boolean reducesMovement() {
		return profile == MovementProfile.REDUCE;
	}

	enum MovementProfile {
		FREE,
		REDUCE,
		SOLID,
	}
}