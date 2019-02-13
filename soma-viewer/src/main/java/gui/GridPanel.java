package gui;

import com.google.common.eventbus.Subscribe;
import events.MouseClickedEvent;
import input.Input;
import java.util.ArrayList;
import java.util.List;
import launcher.GeneralSettings;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector2f;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import org.lwjgl.opengl.GL11;
import shaders.GUIShader;
import utilities.OpenGLUtils;

@Slf4j
public class GridPanel extends UIElement
{
	private final static GUIShader shader = GUIShader.getInstance();
	private final int rows;
	private final int columns;
	private final float gridWidth;
	private final float gridHeight;

	@Getter
	private List<UIElement> children = new ArrayList<>();

	public GridPanel(float width, float height, int rows, int columns)
	{
		super(UIShape.QUAD, width, height);
		this.rows = rows;
		this.columns = columns;
		this.gridWidth = width / columns;
		this.gridHeight = height / rows;
		Input.getEventBus().register(this);
	}

	public void addElement(UIElement element)
	{
		if(children.size() >= rows * columns)
			return;

		int index = children.size();
		int col = index % columns;
		int row = index / columns;
		refresh();
		children.add(element);
		element.setWidth(gridWidth);
		element.setHeight(gridHeight);
		element.setPosition(new Vector2f(position.x + (col * gridWidth), position.y + (row * gridHeight)));
		element.refresh();
	}

	@Override
	public void render()
	{
		shader.start();
		OpenGLUtils.enableAlphaBlending();

		if(children.size() > 0)
		{
			children.forEach(UIElement::render);
		}
		else
		{
			shader.transformationMatrix.loadMatrix(transformationMatrix);
			shader.foregroundColor.loadVec4(color);
			shader.borderColor.loadVec4(borderColor);
			shader.borderWidth.loadInt(borderWidth);
			shader.dimensions.loadVec2(width, height);
			shader.hovered.loadInt(mouseHovered ? 1 : 0);
			shader.hoverColor.loadVec4(hoverColor);
			model.bind(0);
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, model.getIndexCount());
			model.unbind(0);
		}

		shader.stop();
		OpenGLUtils.disableBlending();
	}

	@Override
	public void onHoverStatusChanged(boolean entered)
	{
		if(GeneralSettings.DEBUG_ENABLED)
			log.debug((entered) ? "Mouse entered" : "Mouse exited");
		mouseHovered = entered;
	}

	@Override
	public void update()
	{
		pollInput();
		children.forEach(UIElement::update);
	}

	protected void pollInput()
	{
		boolean flag = boundingBox.contains(Input.getMousePosition().x, Input.getMousePosition().y);
		if(mouseHovered != flag)
		{
			onHoverStatusChanged(flag);
			mouseHovered = flag;
		}
	}

	@Subscribe
	public void onMouseClicked(MouseClickedEvent e)
	{
		if(e.getAction() != GLFW_RELEASE && boundingBox.contains(Input.getMousePosition().x, Input.getMousePosition().y))
		{
			if(GeneralSettings.DEBUG_ENABLED)
				log.debug("Mouse clicked");
		}
	}
}
