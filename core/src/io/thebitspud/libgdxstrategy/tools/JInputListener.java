package io.thebitspud.libgdxstrategy.tools;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public abstract class JInputListener extends InputListener {
	public boolean down = false;

	@Override
	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		down = true;
		return true;
	}

	@Override
	public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
		down = false;
	}

	@Override
	public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
		if (this.down) onClick();
	}

	public abstract void onClick();
}