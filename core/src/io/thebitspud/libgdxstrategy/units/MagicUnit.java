package io.thebitspud.libgdxstrategy.units;

import io.thebitspud.libgdxstrategy.StrategyGame;

public class MagicUnit extends Unit {
	public MagicUnit(int x, int y, boolean ally, StrategyGame app) {
		super(x, y, ID.MAGIC, ally, app);
		setStats(10, 2, 2, 3);
	}
}
