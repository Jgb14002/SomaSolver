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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
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
	private Button replayLoadButton;
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
	private Map<Integer, int[][][]> importedStructure;


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
			col.setCellValueFactory(new PropertyValueFactory<PieceEntry, String>(col.getText().toLowerCase()));
		});
		replayTable.setSelectionModel(null);

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
				if (solverTable.getItems().size() <= 0)
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

				Optional<String> fileName = dialog.showAndWait();
				fileName.ifPresent(name -> {
					try
					{
						final File output = new File(solverExportDir, name.concat(".soma"));
						boolean created = output.createNewFile();

						if (created)
						{
							saveStructure(output);
						}
						else
						{
							Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
							Stage confirmStage = (Stage) alert.getDialogPane().getScene().getWindow();
							confirmStage.getIcons().add(new Image(SomaController.class.getResource("/images/icon.png").toString()));

							alert.setTitle("Export Warning");
							alert.setHeaderText(null);
							alert.setContentText("A file with this name already exists. Would you like to overwrite the data?");

							Optional<ButtonType> result = alert.showAndWait();
							if (result.isPresent() && result.get() == ButtonType.OK)
							{
								saveStructure(output);
							}
							else
							{
								log("Export cancelled");
							}
						}
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

				if(replayFile != null)
				{
					try(FileReader reader = new FileReader(replayFile))
					{
						Gson gson = new Gson();
						importedStructure = gson.fromJson(reader, new TypeToken<LinkedHashMap<Integer, int[][][]>>(){}.getType());

						final ObservableList tableItems = replayTable.getItems();
						tableItems.clear();

						SceneLoader.getCurrentScene().reset();
						importedStructure.keySet().forEach(index -> tableItems.add(new PieceEntry(index)));
						alert("Solution Playback", "Successfully imported structure", Alert.AlertType.INFORMATION);
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
		replayLoadButton.setOnAction(e -> {
			if(importedStructure != null)
			{

			}
			else
			{
				alert("Solution Playback", "You have not yet imported a data file.", Alert.AlertType.ERROR);
			}
		});
		replayNextButton.setOnAction(e -> {
		});
		replayUndoButton.setOnAction(e -> {
		});
		replayResetButton.setOnAction(e -> {
			SceneLoader.getCurrentScene().reset();
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
			SceneLoader.getCurrentScene().reset();
			log("Reset current structure");
		});
	}

	private void loadSolution(int index)
	{
		SomaScene scene = (SomaScene) SceneLoader.getCurrentScene();
		scene.getTaskQueue().add(() -> scene.loadSolution(index));
		log(String.format("Loaded solution %d", index));
	}

	private void saveStructure(File file)
	{
		final Map<Integer, int[][][]> export = new LinkedHashMap<>();

		SomaScene scene = (SomaScene) SceneLoader.getCurrentScene();
		Map<PieceIndex, Piece> pieces = scene.getPieces();

		solverTable.getItems().forEach(entry -> {
			int index = ((PieceEntry) entry).getPiece() - 1;
			Piece piece = pieces.get(PieceIndex.getPieceFromIndex(index));
			export.put(piece.getPieceIndex().getIndex(), piece.getRawData());
		});

		try (Writer writer = new FileWriter(file))
		{
			Gson gson = new GsonBuilder().create();
			gson.toJson(export, writer);
		}
		catch (IOException e)
		{
			alertStackTrace(e, "Unable to write data to file.");
			log.error(e.getMessage());
		}

		log(String.format("Exported structure to %s", file.getAbsolutePath()));
		alert("Solution Solver", "The current structure has been successfully exported", Alert.AlertType.INFORMATION);
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
			final int index = event.getPieceIndex().getIndex() + 1;
			solverTable.getItems().removeIf(entry -> ((PieceEntry) entry).getPiece() == index);
			log(String.format("Piece %d was deleted", index));
		});
	}

	@Subscribe
	public void onPieceAdded(PieceAddedEvent event)
	{
		Platform.runLater(() -> {
			final int index = event.getPieceIndex().getIndex() + 1;
			solverTable.getItems().add(new PieceEntry(index));
			log(String.format("Piece %d was added", index));
		});
	}

}
