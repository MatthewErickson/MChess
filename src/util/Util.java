package util;

import java.util.Random;

/**
 * Original file created by Matt Erickson
 * Date: 12/4/2016
 */
public class Util
{
	public static boolean randomlyTrue(Integer possibilities, Integer total)
	{
		return possibilities > 0 && total > 0 &&
				new Random().nextInt(total) < possibilities;
	}

	public static void sleepWithoutInterruption(Integer millis)
	{
		try
		{
			Thread.sleep(millis);
		}
		catch (InterruptedException ignored)
		{
		}
	}
}
