package io.github.coderodde.util;

import static io.github.coderodde.util.Utils.arraysEqual;
import static io.github.coderodde.util.Utils.isSorted;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

public class NaturalMergesortTest {

    @Test
    public void sortsZeroElements() {
        Arrays.NaturalMergesort.doPerformPresort(false);
        Integer[] arr = {};
        Arrays.NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(Utils.isSorted(arr, Integer::compare));
    }
    
    @Test
    public void sortsOneElements() {
        Arrays.NaturalMergesort.doPerformPresort(false);
        Integer[] arr = { -1 };
        Arrays.NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ -1 }));
    }
    
    @Test
    public void sortsTwoElementsAlreadySorted() {
        Arrays.NaturalMergesort.doPerformPresort(false);
        Integer[] arr = { -1, 1 };
        Arrays.NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ -1, 1 }));
    }
    
    @Test
    public void sortsTwoElementsDescending() {
        Arrays.NaturalMergesort.doPerformPresort(false);
        Integer[] arr = { 1, -1 };
        Arrays.NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ -1, 1 }));
    }
    
    @Test
    public void sortsTwoElementsOnDescendingSubrange() {
        Arrays.NaturalMergesort.doPerformPresort(false);
        Integer[] arr = { 5, 1, -1, 6 };
        Arrays.NaturalMergesort.sort(arr, 1, 3, Integer::compare);
        assertTrue(isSorted(arr, 1, 3, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 5, -1, 1, 6 }));
    }
    
    @Test
    public void sortsTwoElementsOnAscendingSubrange() {
        Arrays.NaturalMergesort.doPerformPresort(false);
        Integer[] arr = { 5, -1, 1, 6 };
        Arrays.NaturalMergesort.sort(arr, 1, 3, Integer::compare);
        assertTrue(isSorted(arr, 1, 3, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 5, -1, 1, 6 }));
    }
    
    @Test
    public void sortOnTwoRuns() {
        Arrays.NaturalMergesort.doPerformPresort(false);
        Integer[] arr = { 6, 5, 1, 4, 3, 2 };
        Arrays.NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 1, 2, 3, 4, 5, 6 }));
    }
    
    @Test
    public void sortOnThreeRuns() {
        Arrays.NaturalMergesort.doPerformPresort(false);
        Integer[] arr = { 7, 5, 8, 4, 2, 6, 3, 1 };
        Arrays.NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 1, 2, 3, 4, 5, 6, 7, 8 }));
    }
    
    @Test
    public void debug1() {
        final Random random = new Random(666L);
        Arrays.NaturalMergesort.doPerformPresort(false);
        Integer[] arr = createRandomArray(random, 40);
        Arrays.NaturalMergesort.sort(arr, 1, arr.length - 1, Integer::compare);
        assertTrue(isSorted(arr, 1, arr.length - 1, Integer::compare));
    }
    
    @Test
    public void stressTest() {
        final Random random = new Random(13L);
        Arrays.NaturalMergesort.doPerformPresort(false);
        
        for (int i = 0; i < 10; ++i) {
            final Integer[] array1 = createRandomArray(random);
            final Integer[] array2 = array1.clone();
            final int rndInt1 = random.nextInt(array1.length);
            final int rndInt2 = random.nextInt(array1.length);
            final int fromIndex = Math.min(rndInt1, rndInt2);
            final int toIndex   = Math.max(rndInt1, rndInt2);
            
            Arrays.NaturalMergesort.sort(array1, fromIndex, toIndex, Integer::compare);
            Arrays.NaturalMergesort.sort(array2, fromIndex, toIndex, Integer::compare);
            assertTrue(isSorted(array1, fromIndex, toIndex, Integer::compare));
            assertTrue(isSorted(array2, fromIndex, toIndex, Integer::compare));
            assertTrue(arraysEqual(array1, array2));
        }
    }

    @Test
    public void sortsZeroElementsPresorted() {
        Arrays.NaturalMergesort.doPerformPresort(true);
        Integer[] arr = {};
        Arrays.NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(Utils.isSorted(arr, Integer::compare));
    }
    
    @Test
    public void sortsOneElementsPresorted() {
        Arrays.NaturalMergesort.doPerformPresort(true);
        Integer[] arr = { -1 };
        Arrays.NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ -1 }));
    }
    
    @Test
    public void sortsTwoElementsAlreadySortedPresorted() {
        Arrays.NaturalMergesort.doPerformPresort(true);
        Integer[] arr = { -1, 1 };
        Arrays.NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ -1, 1 }));
    }
    
    @Test
    public void sortsTwoElementsDescendingPresorted() {
        Arrays.NaturalMergesort.doPerformPresort(true);
        Integer[] arr = { 1, -1 };
        Arrays.NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ -1, 1 }));
    }
    
    @Test
    public void sortsTwoElementsOnDescendingSubrangePresorted() {
        Arrays.NaturalMergesort.doPerformPresort(true);
        Integer[] arr = { 5, 1, -1, 6 };
        Arrays.NaturalMergesort.sort(arr, 1, 3, Integer::compare);
        assertTrue(isSorted(arr, 1, 3, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 5, -1, 1, 6 }));
    }
    
    @Test
    public void sortsTwoElementsOnAscendingSubrangePresorted() {
        Arrays.NaturalMergesort.doPerformPresort(true);
        Integer[] arr = { 5, -1, 1, 6 };
        Arrays.NaturalMergesort.sort(arr, 1, 3, Integer::compare);
        assertTrue(isSorted(arr, 1, 3, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 5, -1, 1, 6 }));
    }
    
    @Test
    public void sortOnTwoRunsPresorted() {
        Arrays.NaturalMergesort.doPerformPresort(true);
        Integer[] arr = { 6, 5, 1, 4, 3, 2 };
        Arrays.NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 1, 2, 3, 4, 5, 6 }));
    }
    
    @Test
    public void sortOnThreeRunsPresorted() {
        Arrays.NaturalMergesort.doPerformPresort(true);
        Integer[] arr = { 7, 5, 8, 4, 2, 6, 3, 1 };
        Arrays.NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 1, 2, 3, 4, 5, 6, 7, 8 }));
    }
    
    @Test
    public void debug1Presorted() {
        final Random random = new Random(666L);
        Arrays.NaturalMergesort.doPerformPresort(true);
        Integer[] arr = createRandomArray(random, 40);
        Arrays.NaturalMergesort.sort(arr, 1, arr.length - 1, Integer::compare);
        assertTrue(isSorted(arr, 1, arr.length - 1, Integer::compare));
    }
    
    @Test
    public void stressTestPresorted() {
        final Random random = new Random(13L);
        Arrays.NaturalMergesort.doPerformPresort(true);
        
        for (int i = 0; i < 10; ++i) {
            final Integer[] array1 = createRandomArray(random);
            final Integer[] array2 = array1.clone();
            final int rndInt1 = random.nextInt(array1.length);
            final int rndInt2 = random.nextInt(array1.length);
            final int fromIndex = Math.min(rndInt1, rndInt2);
            final int toIndex   = Math.max(rndInt1, rndInt2);
            
            Arrays.NaturalMergesort.sort(array1, fromIndex, toIndex, Integer::compare);
            Arrays.NaturalMergesort.sort(array2, fromIndex, toIndex, Integer::compare);
            assertTrue(isSorted(array1, fromIndex, toIndex, Integer::compare));
            assertTrue(isSorted(array2, fromIndex, toIndex, Integer::compare));
            assertTrue(arraysEqual(array1, array2));
        }
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void throwsOnNegativeFromIndex() {
        Arrays.NaturalMergesort.sort(new Integer[] { 1, 2 }, -1, 1, Integer::compare);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void throwsOnTooLargeToIndex() {
        Arrays.NaturalMergesort.sort(new Integer[] { 1, 2 }, 0, 3, Integer::compare);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void throwsOnFromIndexLargerThanToIndex() {
        Arrays.NaturalMergesort.sort(new Integer[] { 1, 2 }, 1, 0, Integer::compare);
    }
    
    private static Integer[] createRandomArray(final Random random) {
        final int arrayLength = getRandom(1, 1000, random);
        final Integer[] array = new Integer[arrayLength];
        
        for (int i = 0; i < array.length; ++i) {
            array[i] = random.nextInt(600);
        }
        
        return array;
    }
    
    private static Integer[] createRandomArray(final Random random, 
                                               final int arrayLength) {
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
