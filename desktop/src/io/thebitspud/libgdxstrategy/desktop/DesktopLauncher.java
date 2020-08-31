package io.thebitspud.libgdxstrategy.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.thebitspud.libgdxstrategy.StrategyGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

		config.setWindowedMode(1366, 768);
		config.setResizable(false);
		config.setTitle("LibGDX Strategy Game");

		new Lwjgl3Application(new StrategyGame(), config);
	}
}
