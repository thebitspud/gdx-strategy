package io.thebitspud.libgdxstrategy.units;

import io.thebitspud.libgdxstrategy.StrategyGame;
import io.thebitspud.libgdxstrategy.players.Player;

public class HeavyUnit extends Unit {
	public HeavyUnit(int x, int y, Player player, StrategyGame app) {
		super(x, y, ID.HEAVY, player, app);
		setStats(15, 2, 1, 4);
	}
}
