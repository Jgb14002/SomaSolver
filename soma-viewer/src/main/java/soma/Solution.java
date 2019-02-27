package soma;

import entities.Piece;
import entities.PieceIndex;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;

public class Solution
{
	@Getter
	final int[][][][] rawData;

	public Solution(int[][][][] rawData)
	{
		this.rawData = rawData;
	}

	public List<Piece> getPieces()
	{
		final List<Piece> pieces = new LinkedList<>();

		for (int i = 0; i < rawData.length; i++)
		{
			pieces.add(Piece.createFromData(Objects.requireNonNull(PieceIndex.getPieceFromIndex(i)), rawData[i]));
		}

		return pieces;
	}

	public boolean isDerrivedFrom(Collection<Piece> pieces)
	{
		for(Piece piece : pieces)
		{
			if(!Arrays.deepEquals(piece.getRawData(), rawData[piece.getPieceIndex().getIndex()]))
			{
				return false;
			}
		}
		return true;
	}

	public void invert()
	{
		int[][][] tmp = rawData[5];
		rawData[5] = rawData[6];
		rawData[6] = tmp;
	}
}
