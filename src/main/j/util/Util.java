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
        // must test, otherwise end < start
        if (array.length > 0)
            shuffle(array, 0, array.length - 1);
    }

    /**
     * Shuffles a contiguous part of an array in-place, with each possible 
     * permutation having an equal chance of being the outcome.
     * @param array This array should only have unique elements.
     * @param start Zero-based index of the first element, inclusive.
     * @param end Zero-based index of the last element, inclusive.
     * @exception NullPointerException if array is null
     * @exception IllegalArgumentException if end is lesser than start
     * @exception IndexOutOfBoundsException if start or end is out of bounds
     */
    public static <T> void shuffle(T[] array, int start, int end)
    {
        if (end < start) throw new IllegalArgumentException("end < start");
        
        final Random rand = getRandom();

        for (int i = start; i < end; i++)
        {
            final int idx2 = rand.nextInt((end - i) + 1) + i;

            // Swap
            final T tmp = array[i];
            array[i] = array[idx2];
            array[idx2] = tmp;
        }
    }

    /**
     * Shuffle method for a primitive int array.
     */
    public static void shuffle(int[] array)
    {
        final Random rand = getRandom();
        for (int i = 0; i < array.length - 1; i++)
        {
            final int idx2 = rand.nextInt(array.length - i) + i;

            // Swap
            final int tmp = array[i];
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

