package strategy;

import java.awt.*;

/**
 * Created by Matthew on 3/22/2016.
 */
public class Move
{

	public Point start, end;

	private Move(Point start, Point end)
	{
		this.start = start;
		this.end = end;
	}

	public static Move make(Point start, Point end)
	{
		return new Move(start, end);
	}

	@Override
	public String toString()
	{
		return String.format("Move (%d, %d) -> (%d, %d)", start.x, start.y, end.x, end.y);
	}

}
