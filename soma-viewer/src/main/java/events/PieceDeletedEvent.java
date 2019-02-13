package events;

import entities.PieceIndex;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class PieceDeletedEvent
{
	@Getter
	private final PieceIndex pieceIndex;
}
