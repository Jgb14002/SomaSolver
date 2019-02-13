package scene;

import com.google.common.eventbus.Subscribe;
import entities.Floor;
import entities.GameObject;
import entities.Piece;
import entities.PieceIndex;
import entities.Sky;
import events.PieceAddedEvent;
import events.PieceDeletedEvent;
import events.PieceWidgetClickedEvent;
import gui.GridPanel;
import gui.PieceWidget;
import gui.UIElement;
import input.Input;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import launcher.GeneralSettings;
import loaders.SolutionManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector2f;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_0;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Y;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;
import processing.Fbo;
import processing.PostProcessing;
import shaders.FloorShader;
import shaders.GUIShader;
import shaders.SimpleShader;
import shaders.SkyboxShader;

@Slf4j
public class SomaScene extends Scene
{

	private final Fbo multisampleFbo = new Fbo(GeneralSettings.WIDTH, GeneralSettings.HEIGHT);
	private final Fbo outputFbo = new Fbo(GeneralSettings.WIDTH, GeneralSettings.HEIGHT, Fbo.DEPTH_TEXTURE);
	private final Floor floor = Floor.create();
	private final Sky sky = Sky.create();

	private final List<UIElement> uiElements = new LinkedList<>();
	@Getter
	private final Map<PieceIndex, Piece> pieces = new HashMap<>();
	private Piece selectedPiece;

	@Getter
	private final ConcurrentLinkedQueue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();

	SomaScene(ICamera camera)
	{
		super(camera);
	}

	@Override
	public void init()
	{
		PostProcessing.init();
		createUI();
	}

	@Override
	public void update()
	{
		uiElements.forEach(UIElement::update);
		if(!taskQueue.isEmpty())
		{
			taskQueue.poll().run();
		}
	}

	@Override
	public void render()
	{
		multisampleFbo.bindFrameBuffer();

		sky.render(getCamera());
		uiElements.forEach(UIElement::render);
		floor.render(getCamera());
		pieces.values().forEach(piece -> piece.render(getCamera()));

		multisampleFbo.unbindFrameBuffer();
		multisampleFbo.resolveToFbo(outputFbo);
		PostProcessing.doPostProcessing(outputFbo.getColourTexture());

	}

	@Override
	public void cleanUp()
	{
		SimpleShader.getInstance().cleanUp();
		SkyboxShader.getInstance().cleanUp();
		FloorShader.getInstance().cleanUp();
		GUIShader.getInstance().cleanUp();
		multisampleFbo.cleanUp();
		outputFbo.cleanUp();
	}

	@Override
	public void processInput()
	{
		getCamera().update();
		if(selectedPiece == null)
		{
			return;
		}

		if (Input.isKeyPressed(GLFW_KEY_W))
		{
			selectedPiece.translate(VectorMapping.getForwardDirection(getCamera().getYaw()));
		}
		if (Input.isKeyPressed(GLFW_KEY_A))
		{
			selectedPiece.translate(VectorMapping.getLeftDirection(getCamera().getYaw()));
		}
		if (Input.isKeyPressed(GLFW_KEY_S))
		{
			selectedPiece.translate(VectorMapping.getBackwardDirection(getCamera().getYaw()));
		}
		if (Input.isKeyPressed(GLFW_KEY_D))
		{
			selectedPiece.translate(VectorMapping.getRightDirection(getCamera().getYaw()));
		}
		if (Input.isKeyPressed(GLFW_KEY_R))
		{
			selectedPiece.translate(0, 1, 0);
		}
		if (Input.isKeyPressed(GLFW_KEY_F))
		{
			selectedPiece.translate(0, -1, 0);
		}
		if (Input.isKeyPressed(GLFW_KEY_X))
		{
			selectedPiece.rotate(GameObject.Axis.X_AXIS);
		}
		if (Input.isKeyPressed(GLFW_KEY_Y))
		{
			selectedPiece.rotate(GameObject.Axis.Y_AXIS);
		}
		if (Input.isKeyPressed(GLFW_KEY_Z))
		{
			selectedPiece.rotate(GameObject.Axis.Z_AXIS);
		}
	}

	@Override
	public void reset()
	{
		selectedPiece = null;
		pieces.clear();

		GridPanel panel = (GridPanel) uiElements.get(0);
		panel.getChildren().forEach(widget -> {
			if (widget instanceof PieceWidget)
			{
				final PieceWidget pieceWidget = (PieceWidget) widget;
				pieceWidget.setSelected(false);
			}
		});
	}

	public void loadSolution(int index)
	{
		reset();
		SolutionManager.getSolutions().get(index).getPieces().forEach(piece -> pieces.put(piece.getPieceIndex(), piece));
	}

	public void addPiece(Piece piece)
	{
		final PieceIndex index = piece.getPieceIndex();
		pieces.put(index, piece);
		SceneLoader.getEventBus().post(new PieceAddedEvent(index));
	}

	public void removePiece(PieceIndex index)
	{
		pieces.remove(index);
	}

	@Subscribe
	public void onPieceWidgetClicked(PieceWidgetClickedEvent event)
	{
		final int button = event.getMouseButton();
		final PieceIndex index = event.getPieceIndex();

		if(button == 0)
		{
			GridPanel panel = (GridPanel) uiElements.get(0);

			for (UIElement widget : panel.getChildren())
			{
				if (widget instanceof PieceWidget)
				{
					final PieceWidget pieceWidget = (PieceWidget) widget;
					if (pieceWidget.getPieceIndex().equals(index))
					{
						pieceWidget.setSelected(true);
						if (!pieces.containsKey(index))
						{
							addPiece(Piece.getFromIndex(index));
						}

						selectedPiece = pieces.get(index);
					}
					else
					{
						pieceWidget.setSelected(false);
					}
				}
			}
		}
		else if(button == 1)
		{
			if (selectedPiece == null)
			{
				return;
			}

			if (index != selectedPiece.getPieceIndex())
			{
				return;
			}
			SceneLoader.getEventBus().post(new PieceDeletedEvent(index));
			pieces.remove(selectedPiece.getPieceIndex());
			selectedPiece = null;

			GridPanel panel = (GridPanel) uiElements.get(0);

			panel.getChildren().forEach(widget -> {
				if (widget instanceof PieceWidget)
				{
					final PieceWidget pieceWidget = (PieceWidget) widget;
					pieceWidget.setSelected(false);
				}
			});
		}
	}

	private void createUI()
	{
		final GridPanel gridPanel = new GridPanel(128 * 7f, 128, 1, 7);
		gridPanel.setPosition(new Vector2f((GeneralSettings.WIDTH / 2f) - (128f * 7f / 2f), 0f));

		PieceIndex[] pieceIndexes = PieceIndex.values();

		for (PieceIndex pieceIndex : pieceIndexes)
		{
			gridPanel.addElement(new PieceWidget(pieceIndex));
		}

		uiElements.add(gridPanel);
	}
}