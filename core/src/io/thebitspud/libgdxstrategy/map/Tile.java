package io.thebitspud.libgdxstrategy.map;

public enum Tile {
	VOID(0, true),
	GRASS(1, false),
	SAND(2, false),
	FOREST(3, false),
	MOUNTAIN(4, true),
	WATER(5, true);

	private final int id;
	private final boolean solid;

	Tile(int id, boolean solid) {
		this.id = id;
		this.solid = solid;
	}

	public int getID() {
		return id;
	}

	public boolean isSolid() {
		return solid;
	}
}