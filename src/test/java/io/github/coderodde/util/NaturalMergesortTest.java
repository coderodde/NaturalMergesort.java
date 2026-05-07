package io.github.coderodde.util;

import static io.github.coderodde.util.Utils.arraysEqual;
import static io.github.coderodde.util.Utils.isSorted;
import java.util.Arrays;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

public class NaturalMergesortTest {

    @Test
    public void sortsZeroElements() {
        Integer[] arr = {};
        NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(Utils.isSorted(arr, Integer::compare));
    }
    
    @Test
    public void sortsOneElements() {
        Integer[] arr = { -1 };
        NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ -1 }));
    }
    
    @Test
    public void sortsTwoElementsAlreadySorted() {
        Integer[] arr = { -1, 1 };
        NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ -1, 1 }));
    }
    
    @Test
    public void sortsTwoElementsDescending() {
        Integer[] arr = { 1, -1 };
        NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ -1, 1 }));
    }
    
    @Test
    public void sortsTwoElementsOnDescendingSubrange() {
        Integer[] arr = { 5, 1, -1, 6 };
        NaturalMergesort.sort(arr, 1, 3, Integer::compare);
        assertTrue(isSorted(arr, 1, 3, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 5, -1, 1, 6 }));
    }
    
    @Test
    public void sortsTwoElementsOnAscendingSubrange() {
        Integer[] arr = { 5, -1, 1, 6 };
        NaturalMergesort.sort(arr, 1, 3, Integer::compare);
        assertTrue(isSorted(arr, 1, 3, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 5, -1, 1, 6 }));
    }
    
    @Test
    public void sortOnTwoRuns() {
        Integer[] arr = { 6, 5, 1, 4, 3, 2 };
        NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 1, 2, 3, 4, 5, 6 }));
    }
    
    @Test
    public void sortOnThreeRuns() {
        Integer[] arr = { 7, 5, 8, 4, 2, 6, 3, 1 };
        NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 1, 2, 3, 4, 5, 6, 7, 8 }));
    }
    
    @Test
    public void stressTest() {
        final Random random = new Random(13L);
        
        for (int i = 0; i < 10; ++i) {
            final Integer[] array1 = createRandomArray(random);
            final Integer[] array2 = array1.clone();
            final int rndInt1 = random.nextInt(array1.length);
            final int rndInt2 = random.nextInt(array1.length);
            final int fromIndex = Math.min(rndInt1, rndInt2);
            final int toIndex   = Math.max(rndInt1, rndInt2);
            
            NaturalMergesort.sort(array1, fromIndex, toIndex, Integer::compare);
            Arrays.sort(array2, fromIndex, toIndex, Integer::compare);
            
            assertTrue(isSorted(array1, fromIndex, toIndex, Integer::compare));
            assertTrue(isSorted(array2, fromIndex, toIndex, Integer::compare));
            assertTrue(arraysEqual(array1, array2));
        }
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void throwsOnNegativeFromIndex() {
        NaturalMergesort.sort(new Integer[] { 1, 2 }, -1, 1, Integer::compare);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void throwsOnTooLargeToIndex() {
        NaturalMergesort.sort(new Integer[] { 1, 2 }, 0, 3, Integer::compare);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void throwsOnFromIndexLargerThanToIndex() {
        NaturalMergesort.sort(new Integer[] { 1, 2 }, 1, 0, Integer::compare);
    }
    
    private static Integer[] createRandomArray(final Random random) {
        final int arrayLength = getRandom(1, 1000, random);
        final Integer[] array = new Integer[arrayLength];
        
        for (int i = 0; i < array.length; ++i) {
            array[i] = random.nextInt(600);
        }
        
        return array;
    }
    
    private static int getRandom(final int min, 
                                 final int max,
                                 final Random random) {
        
        return min + random.nextInt(max - min + 1);
    }
}
