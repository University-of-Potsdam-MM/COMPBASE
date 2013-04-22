package util;

public abstract class Stop {

	private static int currentIndent = 0;

	public static void watch(String message, Timeable timeable) {
		time(message, timeable);
	}

	public static <T> T watch(String message, ResultingTimeable<T> timeable) {
		time(message, timeable);
		return timeable.getResult();
	}

	private static void time(String message, Timeable timeable) {
		long startTime = System.currentTimeMillis();

		currentIndent++;
		timeable.time();

		long endTime = System.currentTimeMillis();
		long timeTaken = endTime - startTime;

		for (int count = 0; count < currentIndent - 1; count++)
			System.out.print("|");
		if (currentIndent > 0)
			System.out.print("+");

		System.out.print("Aktion: " + message + ". ");
		System.out.print("Benötigte Zeit: ");
		System.out.println((double) timeTaken / 1000 + "s");

		currentIndent--;
	}
}
