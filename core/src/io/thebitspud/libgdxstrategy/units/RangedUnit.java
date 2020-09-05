package io.thebitspud.libgdxstrategy.units;

import io.thebitspud.libgdxstrategy.StrategyGame;
import io.thebitspud.libgdxstrategy.players.Player;

public class RangedUnit extends Unit {
	public RangedUnit(int x, int y, Player player, StrategyGame app) {
		super(x, y, ID.RANGED, player, app);
		setStats(10, 3, 3, 2, 50);
	}
}