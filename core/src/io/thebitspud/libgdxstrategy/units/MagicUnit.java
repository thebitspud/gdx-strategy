package io.thebitspud.libgdxstrategy.units;

import io.thebitspud.libgdxstrategy.StrategyGame;
import io.thebitspud.libgdxstrategy.players.Player;

public class MagicUnit extends Unit {
	public MagicUnit(int x, int y, Player player, StrategyGame app) {
		super(x, y, ID.MAGIC, player, app);
		setStats(10, 2, 2, 4, 75);
	}
}
