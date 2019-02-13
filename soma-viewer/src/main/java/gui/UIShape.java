package gui;

import lombok.Getter;

public enum UIShape
{
	QUAD(new float[] {-1,1,-1,-1,1,1,1,-1}, new int[] {0, 1, 2, 3});

	@Getter
	final float[] positions;
	@Getter
	final int[] indices;

	UIShape(float[] positions, int[] indices)
	{
		this.positions = positions;
		this.indices = indices;
	}
}
