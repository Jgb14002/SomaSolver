package controllers;

import application.SomaController;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import entities.Piece;
import entities.PieceIndex;
import events.ControllerLinkEvent;
import events.PieceAddedEvent;
import events.PieceDeletedEvent;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import export.Instruction;
import export.InstructionType;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import loaders.SolutionManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import models.InstructionEntry;
import models.PieceEntry;
import models.SolutionEntry;
import scene.SceneLoader;
import scene.SomaScene;

@Slf4j
public class UIController
{
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("hh:mm:ss");
	private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");

	@Getter
	private static UIController instance;
	private final DirectoryChooser solverExportChooser;
	private final FileChooser replayFileChooser;
	@FXML
	public MenuItem consoleMenuItem;
	@FXML
	public ScrollPane consoleScroll;
	@Setter
	private Stage stage;
	@FXML
	private TextField solverExportField;
	@FXML
	private Button solverBrowseButton;
	@FXML
	private Button solverSolveButton;
	@FXML
	private Button solverResetButton;
	@FXML
	private TableView solverTable;
	@FXML
	private Button solverClearButton;
	@FXML
	private Button solverExportButton;
	@FXML
	private TextField replayImportField;
	@FXML
	private Button replayBrowseButton;
	@FXML
	private Button replayResetButton;
	@FXML
	private TableView replayTable;
	@FXML
	private Button replayNextButton;
	@FXML
	private Button replayUndoButton;
	@FXML
	private Button explorerLoadButton;
	@FXML
	private Button explorerResetButton;
	@FXML
	private TableView explorerTable;
	@FXML
	private Button explorerNextButton;
	@FXML
	private Button explorerPreviousButton;
	@FXML
	private TextFlow outText;
	private File solverExportDir;
	private File replayFile;
	private List<Instruction> importedInstructions = new LinkedList<>();
	private int importedIndex = 0;

	private Map<Integer, int[][][]> cachedStructure = new LinkedHashMap<>();
	private String exportName;


	public UIController()
	{
		instance = this;

		solverExportChooser = new DirectoryChooser();
		solverExportChooser.setTitle("Select Export Directory");
		solverExportChooser.setInitialDirectory(new File(System.getProperty("user.home")));

		replayFileChooser = new FileChooser();
		replayFileChooser.setTitle("Choose a Replay File");
		replayFileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		replayFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Soma Files (*.soma)", "*.soma"));
	}

	private static Map.Entry<Integer, int[][][]> getElementAt(Map<Integer, int[][][]> map, int index)
	{
		for (Map.Entry entry : map.entrySet())
		{
			if (index-- == 0)
			{
				return entry;
			}
		}
		return null;
	}

	@FXML //After our fields are injected -> access to FXML fields
	private void initialize()
	{
		outText.setTextAlignment(TextAlignment.JUSTIFY);
		bindControls();
		bindTableModels();
		loadSolutions();
		log.info("Created UI frame");
		SceneLoader.linkController(this);
	}

	private void loadSolutions()
	{
		final int count = SolutionManager.getSolutions().size();
		for (int i = 0; i < count; i++)
		{
			explorerTable.getItems().add(new SolutionEntry(i + 1));
		}

		log(String.format("Loaded %d solutions into the solution explorer", count));
	}

	@SuppressWarnings("all")
	private void bindTableModels()
	{
		solverTable.getColumns().forEach(column -> {
			final TableColumn col = (TableColumn) column;
			col.setCellValueFactory(new PropertyValueFactory<PieceEntry, String>(col.getText().toLowerCase()));
		});

		replayTable.getColumns().forEach(column -> {
			final TableColumn col = (TableColumn) column;
			col.setCellValueFactory(new PropertyValueFactory<InstructionEntry, String>(col.getText().toLowerCase()));
		});

		explorerTable.getColumns().forEach(column -> {
			final TableColumn col = (TableColumn) column;
			col.setCellValueFactory(new PropertyValueFactory<SolutionEntry, String>(col.getText().toLowerCase()));
		});

		//Draggable table rows
		solverTable.setRowFactory(tv -> {

			final TableRow<PieceEntry> row = new TableRow<>();
			row.setOnDragDetected(event -> {
				if (!row.isEmpty())
				{
					Integer index = row.getIndex();
					Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
					db.setDragView(row.snapshot(null, null));
					ClipboardContent cc = new ClipboardContent();
					cc.put(SERIALIZED_MIME_TYPE, index);
					db.setContent(cc);
					event.consume();
				}
			});

			row.setOnDragOver(event -> {
				Dragboard db = event.getDragboard();
				if (db.hasContent(SERIALIZED_MIME_TYPE))
				{
					if (row.getIndex() != ((Integer) db.getContent(SERIALIZED_MIME_TYPE)).intValue())
					{
						event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						event.consume();
					}
				}
			});

			row.setOnDragDropped(event -> {
				Dragboard db = event.getDragboard();
				if (db.hasContent(SERIALIZED_MIME_TYPE))
				{
					int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
					PieceEntry draggedPiece = (PieceEntry) solverTable.getItems().remove(draggedIndex);

					int dropIndex;

					if (row.isEmpty())
					{
						dropIndex = solverTable.getItems().size();
					}
					else
					{
						dropIndex = row.getIndex();
					}

					solverTable.getItems().add(dropIndex, draggedPiece);

					event.setDropCompleted(true);
					solverTable.getSelectionModel().select(dropIndex);
					event.consume();
				}
			});

			return row;

		});
	}

	private void bindControls()
	{
		//Console
		consoleMenuItem.setOnAction(e -> outText.getChildren().clear());

		//Solver controls
		solverBrowseButton.setOnAction(e -> {
			log.info("Solver - Opening export file dialog");
			solverExportDir = solverExportChooser.showDialog(stage);

			if (solverExportDir != null)
			{
				final String path = solverExportDir.getAbsolutePath();
				log.info("Solver - Set export directory to: {}", path);
				solverExportField.setText(path);
			}
		});
		solverExportButton.setOnAction(e -> {
			if (solverExportDir != null)
			{
				if (solverTable.getItems().isEmpty() && cachedStructure.isEmpty())
				{
					alert("Solution Solver", "There is no data to export. Try adding some pieces first.", Alert.AlertType.ERROR);
					return;
				}

				TextInputDialog dialog = new TextInputDialog();
				Stage txtStage = (Stage) dialog.getDialogPane().getScene().getWindow();
				txtStage.getIcons().add(new Image(SomaController.class.getResource("/images/icon.png").toString()));
				dialog.setTitle("Export");
				dialog.setHeaderText(null);
				dialog.setContentText("Save as:");

				Optional<String> fileName = (exportName == null) ? dialog.showAndWait() : Optional.of(exportName);
				fileName.ifPresent(name -> {
					exportName = name;
					try
					{
						final File output = new File(solverExportDir, name.concat(".soma"));
						output.createNewFile();
						exportChanges(output);

					}
					catch (IOException e1)
					{
						log.error(e1.getMessage());
						alertStackTrace(e1, "An error occurred while exporting data.");
					}
				});
			}
			else
			{
				alert("Solution Solver", "You must select an export directory before you can export data.", Alert.AlertType.ERROR);
			}
		});
		solverSolveButton.setOnAction(e -> {

			long start = System.currentTimeMillis();
			log("Attempting to solve structure...");

			Collection<Piece> pieces = ((SomaScene) SceneLoader.getCurrentScene()).getPieces().values();
			int count = SolutionManager.getPossibleSolutionCount(pieces);

			log(String.format("Found %d possible solutions in %.2f ms", count, (System.currentTimeMillis() - start) / 1.0f));

			if (count <= 0)
			{
				alert("Solution Solver", "No solutions were found", Alert.AlertType.INFORMATION);
			}
			else
			{
				alert("Solution Solver", String.format("%d solutions were found", count), Alert.AlertType.INFORMATION);
			}
		});
		solverClearButton.setOnAction(e -> {
			solverTable.getItems().clear();
		});
		solverResetButton.setOnAction(e -> {
			importedIndex = 0;
			solverTable.getItems().clear();
			SceneLoader.getCurrentScene().reset();
			log("Reset current structure");
		});

		//Replay controls
		replayBrowseButton.setOnAction(e -> {
			log.info("Replay - Opening replay file dialog");
			replayFile = replayFileChooser.showOpenDialog(stage);

			if (replayFile != null)
			{
				final String path = replayFile.getAbsolutePath();
				log.info("Replay - Importing replay file at: {}", path);
				replayImportField.setText(path);

				if (replayFile != null)
				{
					try (BufferedReader reader = new BufferedReader(new FileReader(replayFile)))
					{
						final ObservableList tableItems = replayTable.getItems();
						tableItems.clear();

						Gson gson = new Gson();

						String line;
						while ((line = reader.readLine()) != null)
						{
							Instruction ins = gson.fromJson(line, Instruction.class);
							if (ins != null)
							{
								importedInstructions.add(ins);
								tableItems.add(new InstructionEntry(ins.getType(), ins.getIndex() + 1));
							}
						}

						SceneLoader.getCurrentScene().reset();
						alert("Solution Playback", "Successfully imported structure", Alert.AlertType.INFORMATION);
						importedIndex = 0;
						solverTable.getItems().clear();

					}
					catch (IOException e1)
					{
						alertStackTrace(e1, "An error occurred while importing the requested structure.");
						log("Import failed");
						log.error(e1.getMessage());
					}
				}
			}
		});
		replayNextButton.setOnAction(e -> {
			if (!importedInstructions.isEmpty())
			{
				Instruction ins = importedInstructions.get(importedIndex);
				processInstruction(ins);
				Platform.runLater(() -> selectRow(replayTable, importedIndex));
				importedIndex = Math.min(importedInstructions.size() - 1, importedIndex + 1);
			}
			else
			{
				alert("Solution Playback", "You have not yet imported a data file.", Alert.AlertType.ERROR);
			}
		});
		replayUndoButton.setOnAction(e -> {
			if (!importedInstructions.isEmpty())
			{
				importedIndex = Math.max(0, importedIndex - 1);
				if (importedIndex < importedInstructions.size())
				{
					SceneLoader.getCurrentScene().reset();
					for (int i = 0; i < importedIndex; i++)
					{
						processInstruction(importedInstructions.get(i));
					}
					Platform.runLater(() -> selectRow(replayTable, importedIndex));
				}
			}
			else
			{
				alert("Solution Playback", "You have not yet imported a data file.", Alert.AlertType.ERROR);
			}
		});
		replayResetButton.setOnAction(e -> {
			SceneLoader.getCurrentScene().reset();
			solverTable.getItems().clear();
			importedIndex = 0;
			log("Reset current structure");
		});

		//Explorer controls
		explorerLoadButton.setOnAction(e -> {

			SolutionEntry selectedEntry = (SolutionEntry) explorerTable.getSelectionModel().getSelectedItem();

			if (selectedEntry != null)
			{
				final int index = selectedEntry.getSolution();
				loadSolution(index - 1);
			}
			else
			{
				alert("Solution Explorer", "Unable to load solution. Try selecting one first.", Alert.AlertType.ERROR);
			}
		});
		explorerNextButton.setOnAction(e -> {

			int index = explorerTable.getSelectionModel().getSelectedIndex();
			final int next = index + 1;

			if (next >= explorerTable.getItems().size())
			{
				return;
			}

			Platform.runLater(() -> {
				selectRow(explorerTable, next);
				loadSolution(next);
			});

		});
		explorerPreviousButton.setOnAction(e -> {

			final int index = explorerTable.getSelectionModel().getSelectedIndex();
			final int prev = (index > 0) ? index - 1 : explorerTable.getItems().size() - 1;

			selectRow(explorerTable, prev);
			loadSolution(prev);

		});
		explorerResetButton.setOnAction(e -> {
			solverTable.getItems().clear();
			importedIndex = 0;
			SceneLoader.getCurrentScene().reset();
			log("Reset current structure");
		});
	}

	private void processInstruction(Instruction ins)
	{
		SomaScene scene = (SomaScene) SceneLoader.getCurrentScene();
		PieceIndex index = PieceIndex.getPieceFromIndex(ins.getIndex());
		switch (ins.getType())
		{
			case ADD:
				scene.getTaskQueue().add(() -> scene.addPiece(Piece.createFromData(index, ins.getData())));
				break;
			case MOVE:
				scene.getTaskQueue().add(() -> scene.getPieces().replace(index, Piece.createFromData(index, ins.getData())));
				break;
			case REMOVE:
				scene.getTaskQueue().add(() -> scene.getPieces().remove(index));
				break;
			case RESET:
				scene.reset();
				break;
		}
	}

	private void loadSolution(int index)
	{
		solverTable.getItems().clear();
		importedIndex = 0;

		SomaScene scene = (SomaScene) SceneLoader.getCurrentScene();
		scene.getTaskQueue().add(() -> scene.loadSolution(index));
		log(String.format("Loaded solution %d", index));
	}

	private void exportChanges(File file)
	{
		SomaScene scene = (SomaScene) SceneLoader.getCurrentScene();
		Map<Integer, int[][][]> current = scene.getPieces().values().stream().collect(Collectors.toMap(p -> p.getPieceIndex().getIndex(), Piece::getRawData));

		List<Instruction> instructions = getInstructions(current);
		if (!instructions.isEmpty())
		{

			try (Writer writer = new FileWriter(file, true))
			{
				Gson gson = new GsonBuilder().create();
				for (Instruction ins : instructions)
				{
					writer.write(gson.toJson(ins).concat(System.lineSeparator()));
				}
			}
			catch (IOException e)
			{
				alertStackTrace(e, "Unable to write data to file.");
				log.error(e.getMessage());
			}

			log(String.format("Exported %d changes to %s", instructions.size(), file.getAbsolutePath()));

		}
		else
		{
			alert("Solution Solver", "The current structure is identical to the last exported structure.", Alert.AlertType.WARNING);
		}
		cachedStructure = current;
	}

	private List<Instruction> getInstructions(Map<Integer, int[][][]> current)
	{
		List<Instruction> instructions = new LinkedList<>();

		if (current.isEmpty())
		{
			instructions.add(new Instruction(InstructionType.RESET));
		}
		else
		{
			cachedStructure.forEach((k, v) -> {
				if (!current.containsKey(k))
				{
					instructions.add(new Instruction(InstructionType.REMOVE, k));
				}
				else if (!Arrays.deepEquals(v, current.get(k)))
				{
					instructions.add(new Instruction(InstructionType.MOVE, k, current.get(k)));
				}
			});

			current.forEach((k, v) -> {
				if (!cachedStructure.containsKey(k))
				{
					instructions.add(new Instruction(InstructionType.ADD, k, v));
				}
			});
		}

		return instructions;
	}

	private void selectRow(TableView table, int index)
	{
		Platform.runLater(() -> {
			table.requestFocus();
			table.getSelectionModel().clearAndSelect(index);
			table.getFocusModel().focus(index);
			table.scrollTo(index);
		});
	}

	public void log(String message)
	{
		log.info(message);
		final String out = String.format("%s - %s", DATE_FORMAT.format(new Date()), message);

		ObservableList<Node> items = outText.getChildren();
		if (items.size() > 0)
		{
			items.add(new Text(System.lineSeparator()));
		}

		items.add(new Text(out));
		consoleScroll.layout();
		consoleScroll.setVvalue(1.0f);

	}

	public void alert(String title, String message, Alert.AlertType type)
	{
		Alert alert = new Alert(type);

		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(SomaController.class.getResource("/images/icon.png").toString()));

		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);

		alert.showAndWait();
	}

	public void alertStackTrace(Exception e, String info)
	{
		Alert alert = new Alert(Alert.AlertType.ERROR);
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(SomaController.class.getResource("/images/icon.png").toString()));

		alert.setTitle("Error");
		alert.setHeaderText("Take a picture and send it to Jason");
		alert.setContentText(info);

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label("The exception stacktrace was:");

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		alert.getDialogPane().setExpandableContent(expContent);

		alert.showAndWait();
	}

	@Subscribe
	public void onControllerLinked(ControllerLinkEvent event)
	{
		Platform.runLater(() -> log("Listening for viewer events..."));
	}

	@Subscribe
	public void onPieceDeleted(PieceDeletedEvent event)
	{
		Platform.runLater(() -> {
			final int index = event.getPieceIndex().getIndex();
			solverTable.getItems().removeIf(entry -> ((PieceEntry) entry).getPiece() == index + 1);
			log(String.format("Piece %d was deleted", index + 1));
		});
	}

	@Subscribe
	public void onPieceAdded(PieceAddedEvent event)
	{
		Platform.runLater(() -> {
			final int index = event.getPieceIndex().getIndex();
			solverTable.getItems().add(new PieceEntry(index + 1));
			log(String.format("Piece %d was added", index + 1));
		});
	}

}
