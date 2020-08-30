package io.thebitspud.libgdxstrategy.tools;

import com.badlogic.gdx.assets.AssetManager;

public class AssetLibrary extends AssetManager {
	public AssetLibrary() {

	}

	public void loadAll() {
		loadFonts();
		loadTextures();
		loadAudio();

		finishLoading();
		assignTextures();
		assignAudio();
	}

	private void loadFonts() {
	}

	private void loadTextures() {
	}

	private void assignTextures() {}

	private void loadAudio() {
	}

	private void assignAudio() {}
}
