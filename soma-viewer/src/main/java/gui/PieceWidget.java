package gui;

import com.google.common.eventbus.Subscribe;
import entities.PieceIndex;
import events.MouseClickedEvent;
import events.PieceDeletedEvent;
import events.PieceWidgetClickedEvent;
import input.Input;
import launcher.GeneralSettings;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import org.lwjgl.opengl.GL11;
import scene.SceneLoader;
import shaders.GUIShader;
import textures.Texture;
import utilities.OpenGLUtils;

@Slf4j
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class PieceWidget extends UIElement
{
	private final static GUIShader shader = GUIShader.getInstance();

	@EqualsAndHashCode.Include
	@Getter
	private final PieceIndex pieceIndex;

	@Getter
	@Setter
	private boolean selected;

	public PieceWidget(PieceIndex pieceIndex)
	{
		super(UIShape.QUAD, Texture.newTexture(pieceIndex.getWidgetTexture()).create(), 0f, 0f);
		this.pieceIndex = pieceIndex;
		Input.getEventBus().register(this);
	}

	@Override
	public void render()
	{
		shader.start();
		OpenGLUtils.enableAlphaBlending();
		shader.transformationMatrix.loadMatrix(transformationMatrix);
		shader.foregroundColor.loadVec4(color);
		shader.borderColor.loadVec4(borderColor);
		shader.borderWidth.loadInt(borderWidth);
		shader.dimensions.loadVec2(width, height);
		shader.hovered.loadInt(mouseHovered || selected ? 1 : 0);
		shader.hoverColor.loadVec4(hoverColor);
		model.bind(0);
		getTexture().bindToUnit(0);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, model.getIndexCount());
		model.unbind(0);
		shader.stop();
		OpenGLUtils.disableBlending();
	}

	@Override
	public void onHoverStatusChanged(boolean entered)
	{
		if (GeneralSettings.DEBUG_ENABLED)
		{
			log.debug((entered) ? "Mouse entered" : "Mouse exited");
		}

		mouseHovered = entered;
	}

	@Override
	public void update()
	{
		pollInput();
	}

	@Subscribe
	public void onMouseClicked(MouseClickedEvent e)
	{
		if (e.getAction() != GLFW_RELEASE && boundingBox.contains(Input.getMousePosition().x, Input.getMousePosition().y))
		{
			if (GeneralSettings.DEBUG_ENABLED)
			{
				log.debug("Mouse clicked");
			}

			SceneLoader.getEventBus().post(new PieceWidgetClickedEvent(e.getButton(), pieceIndex));
		}
	}

	protected void pollInput()
	{
		boolean flag = boundingBox.contains(Input.getMousePosition().x, Input.getMousePosition().y);
		if (mouseHovered != flag)
		{
			onHoverStatusChanged(flag);
			mouseHovered = flag;
		}
	}

}
