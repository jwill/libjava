package j.util;

import java.util.Random;

/**
 * Misc utilities.
 * @author Lucas Tan
 */
public final class Util
{
    private Util(){}

    private static final ThreadLocal random = new ThreadLocal() {
        @Override
        protected synchronized Object initialValue() 
        {
            return new Random();
        }
    };

    /**
     * Gets the thread-local Random object which is thread-safe.
     */
    public static Random getRandom()
    {
        return (Random) random.get();
    }

    /**
     * Shuffles an array in-place with each possible permutation
     * having an equal chance of being the outcome.
     * @param array This array should only have unique elements.
     * See http://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle
     * @exception NullPointerException if array is null
     */
    public static <T> void shuffle(T[] array)
    {
        final Random rand = getRandom();
        for (int i = 0; i < array.length - 1; i++)
        {
            final int idx2 = rand.nextInt(array.length - i) + i;

            // Swap
            final T tmp = array[i];
            array[i] = array[idx2];
            array[idx2] = tmp;
        }
    }

    /**
     * Shuffle method for a primitive char array.
     */
    public static void shuffle(char[] array)
    {
        final Random rand = getRandom();
        for (int i = 0; i < array.length - 1; i++)
        {
            final int idx2 = rand.nextInt(array.length - i) + i;

            // Swap
            final char tmp = array[i];
            array[i] = array[idx2];
            array[idx2] = tmp;
        }
    }
}

