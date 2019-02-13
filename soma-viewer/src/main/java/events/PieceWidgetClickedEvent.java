package events;

import entities.PieceIndex;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class PieceWidgetClickedEvent
{
	@Getter
	private final int mouseButton;

	@Getter
	private final PieceIndex pieceIndex;

}
