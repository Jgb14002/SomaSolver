package entities;

import graph.Direction;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import launcher.GeneralSettings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import utilities.ResourceFile;

@AllArgsConstructor
public enum PieceIndex
{
	PIECE_ONE(0, new int[] {0,-1,0, 0,0,0, 1,-1,0}, new ResourceFile(GeneralSettings.TEXTURE_FOLDER, "ui_piece_one.png")),
	PIECE_TWO(1, new int[] {0,-1,0, 0,0,0, 0,1,0, 1,-1,0}, new ResourceFile(GeneralSettings.TEXTURE_FOLDER, "ui_piece_two.png")),
	PIECE_THREE(2, new int[] {-1,0,0, 0,0,0, 0,-1,0, 1,-1,0}, new ResourceFile(GeneralSettings.TEXTURE_FOLDER, "ui_piece_three.png")),
	PIECE_FOUR(3, new int[] {-1,-1,0, 0,-1,0, 0,0,0, 1,-1,0}, new ResourceFile(GeneralSettings.TEXTURE_FOLDER, "ui_piece_four.png")),
	PIECE_FIVE(4, new int[] {0,-1,0, 0,0,0, 1,-1,0, 0,-1,1}, new ResourceFile(GeneralSettings.TEXTURE_FOLDER, "ui_piece_five.png")),
	PIECE_SIX(5, new int[] {0,-1,0, 1,0,0, 1,0,1, 1,-1,0}, new ResourceFile(GeneralSettings.TEXTURE_FOLDER, "ui_piece_six.png")),
	PIECE_SEVEN(6, new int[] {0,-1,0, 0,0,0, 0,0,1, 1,-1,0}, new ResourceFile(GeneralSettings.TEXTURE_FOLDER, "ui_piece_seven.png"));

	@Getter
	private final int index;

	@Getter
	private final int[] gridPositions;

	@Getter
	private final ResourceFile widgetTexture;

	// {Direction.UP, Direction.DOWN, Direction.RIGHT}
	// {Direction.LEFT, Direction.UP, Direction.UP}
	// {Direction.LEFT, Direction.UP, Direction.LEFT}
	// {Direction.LEFT, Direction.UP, Direction.DOWN, Direction.LEFT}
	// {Direction.LEFT, Direction.UP, Direction.DOWN, Direction.BACKWARDS}
	// {Direction.RIGHT, Direction.UP, Direction.BACKWARDS}
	// {Direction.LEFT, Direction.UP, Direction.BACKWARDS}

	@Nullable
	public static PieceIndex getPieceFromIndex(int index)
	{
		Optional<PieceIndex> pieceIndex = Arrays.stream(values()).filter(piece -> piece.index == index).findFirst();
		return pieceIndex.orElse(null);
	}

}
