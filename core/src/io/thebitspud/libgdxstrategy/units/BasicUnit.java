package io.thebitspud.libgdxstrategy.units;

import io.thebitspud.libgdxstrategy.StrategyGame;
import io.thebitspud.libgdxstrategy.players.Player;

public class BasicUnit extends Unit {
	public BasicUnit(int x, int y, Player player, StrategyGame app) {
		super(x, y, ID.BASIC, player, app);
		setStats(10, 3, 1, 3, 50);
	}
}
