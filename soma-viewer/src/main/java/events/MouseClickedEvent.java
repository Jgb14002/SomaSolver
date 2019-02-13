package events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class MouseClickedEvent
{
	@Getter
	private final int action;
	@Getter
	private final int button;
}
