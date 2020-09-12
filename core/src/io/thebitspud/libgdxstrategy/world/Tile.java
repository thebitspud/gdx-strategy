package io.thebitspud.libgdxstrategy.world;

public enum Tile {
	VOID(0, MovementProfile.NONE),
	GRASS(1, MovementProfile.FAST),
	SAND(2, MovementProfile.FAST),
	FOREST(3,  MovementProfile.SLOW),
	MOUNTAIN(4,  MovementProfile.NONE),
	WATER(5,  MovementProfile.NONE);

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
		return profile == MovementProfile.NONE;
	}

	public boolean reducesMovement() {
		return profile == MovementProfile.SLOW;
	}

	public String getTileInfo() {
		String idText = "\nTile." + this;
		String propertiesText = "\nMovement: " + this.profile;

		return idText + propertiesText;
	}

	enum MovementProfile {
		FAST, // full movement
		SLOW, // reduced movement
		NONE, // no movement
	}
}