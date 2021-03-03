package scene;

import com.google.common.eventbus.Subscribe;
import entities.Cube;
import entities.Floor;
import entities.GameObject;
import entities.Piece;
import entities.PieceIndex;
import entities.Sky;
import events.PieceAddedEvent;
import events.PieceDeletedEvent;
import events.PieceWidgetClickedEvent;
import graph.Direction;
import graph.GraphBuilder;
import graph.Node;
import gui.GridPanel;
import gui.PieceWidget;
import gui.UIElement;
import input.Input;

import java.util.*;
import java.util.concurrent.*;

import launcher.GeneralSettings;
import loaders.SolutionManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.joml.Vector2f;
import org.joml.Vector3i;
import processing.Fbo;
import processing.PostProcessing;
import shaders.FloorShader;
import shaders.GUIShader;
import shaders.SimpleShader;
import shaders.SkyboxShader;

import static org.lwjgl.glfw.GLFW.*;

@Slf4j
public class SomaScene extends Scene
{

	private final Fbo multisampleFbo = new Fbo(GeneralSettings.WIDTH, GeneralSettings.HEIGHT);
	private final Fbo outputFbo = new Fbo(GeneralSettings.WIDTH, GeneralSettings.HEIGHT, Fbo.DEPTH_TEXTURE);
	private final Floor floor = Floor.create();
	private final Sky sky = Sky.create();

	private final List<UIElement> uiElements = new LinkedList<>();
	@Getter
	private final Map<PieceIndex, Piece> pieces = new LinkedHashMap<>();
	private Piece selectedPiece;

	private List<Piece> selectedPositions;
	private int selectedIndex;

	@Getter
	private final ConcurrentLinkedQueue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();
	private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

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
		pieces.values().forEach(piece -> piece.render(getCamera()));
		floor.render(getCamera());

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
		executorService.shutdown();
	}

	private int[][][] getLayout()
	{
		final int[][][] layout = new int[3][3][3];

		for(Piece piece : pieces.values())
		{
			piece.getCubes().forEach(cube -> {
				final Vector3i grid = cube.getGridPosition();
				layout[1 - grid.y][grid.z + 1][grid.x + 1] = 1;
			});
		}
		return layout;
	}

	@Override
	public void processInput()
	{
		getCamera().update();

		if(selectedPiece == null)
		{
			return;
		}


		final PieceIndex index = selectedPiece.getPieceIndex();

		if(selectedPositions != null)
		{
			selectedIndex = (selectedIndex < 0 || selectedIndex > selectedPositions.size() - 1) ? 0 : selectedIndex;

			if(Input.isKeyPressed(GLFW_KEY_UP) && selectedPositions.size() > 0)
			{
				pieces.put(index, selectedPositions.get(selectedIndex));
				selectedIndex++;
			}

			if(Input.isKeyPressed(GLFW_KEY_DOWN) && selectedPositions.size() > 0)
			{
				pieces.put(index, selectedPositions.get(selectedIndex));
				selectedIndex--;
			}
		}
		else
		{
			if(Input.isKeyPressed(GLFW_KEY_UP) || Input.isKeyPressed(GLFW_KEY_DOWN))
			{
				pieces.remove(selectedPiece.getPieceIndex());
				CompletableFuture<GraphBuilder> asyncGraph = new CompletableFuture<>();

				asyncGraph.whenComplete((graph, ex) -> taskQueue.add(() -> {
					selectedPositions = graph.getPieces(selectedPiece.getPieceIndex());
					if(!selectedPositions.isEmpty())
					{
						pieces.put(index, selectedPositions.get(0));
					}
				}));

				executorService.submit(() -> asyncGraph.complete(new GraphBuilder(getLayout()).build()));
			}
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
						selectedPositions = null;
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
			selectedPositions = null;

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
