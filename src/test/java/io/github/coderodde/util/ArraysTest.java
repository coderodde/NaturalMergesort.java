package io.github.coderodde.util;

import static io.github.coderodde.util.Utils.arraysEqual;
import static io.github.coderodde.util.Utils.isSorted;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

public class ArraysTest {

    @Test
    public void naturalMergesortsortsZeroElements() {
        Arrays.NaturalMergesort.doPerformPresort(false);
        Integer[] arr = {};
        Arrays.NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(Utils.isSorted(arr, Integer::compare));
    }
    
    @Test
    public void naturalMergesortsortsOneElements() {
        Arrays.NaturalMergesort.doPerformPresort(false);
        Integer[] arr = { -1 };
        Arrays.NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ -1 }));
    }
    
    @Test
    public void naturalMergesortsortsTwoElementsAlreadySorted() {
        Arrays.NaturalMergesort.doPerformPresort(false);
        Integer[] arr = { -1, 1 };
        Arrays.NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ -1, 1 }));
    }
    
    @Test
    public void naturalMergesortsortsTwoElementsDescending() {
        Arrays.NaturalMergesort.doPerformPresort(false);
        Integer[] arr = { 1, -1 };
        Arrays.NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ -1, 1 }));
    }
    
    @Test
    public void naturalMergesortsortsTwoElementsOnDescendingSubrange() {
        Arrays.NaturalMergesort.doPerformPresort(false);
        Integer[] arr = { 5, 1, -1, 6 };
        Arrays.NaturalMergesort.sort(arr, 1, 3, Integer::compare);
        assertTrue(isSorted(arr, 1, 3, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 5, -1, 1, 6 }));
    }
    
    @Test
    public void naturalMergesortsortsTwoElementsOnAscendingSubrange() {
        Arrays.NaturalMergesort.doPerformPresort(false);
        Integer[] arr = { 5, -1, 1, 6 };
        Arrays.NaturalMergesort.sort(arr, 1, 3, Integer::compare);
        assertTrue(isSorted(arr, 1, 3, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 5, -1, 1, 6 }));
    }
    
    @Test
    public void naturalMergesortsortOnTwoRuns() {
        Arrays.NaturalMergesort.doPerformPresort(false);
        Integer[] arr = { 6, 5, 1, 4, 3, 2 };
        Arrays.NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 1, 2, 3, 4, 5, 6 }));
    }
    
    @Test
    public void naturalMergesortsortOnThreeRuns() {
        Arrays.NaturalMergesort.doPerformPresort(false);
        Integer[] arr = { 7, 5, 8, 4, 2, 6, 3, 1 };
        Arrays.NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 1, 2, 3, 4, 5, 6, 7, 8 }));
    }
    
    @Test
    public void naturalMergesortdebug1() {
        final Random random = new Random(666L);
        Arrays.NaturalMergesort.doPerformPresort(false);
        Integer[] arr = createRandomArray(random, 40);
        Arrays.NaturalMergesort.sort(arr, 1, arr.length - 1, Integer::compare);
        assertTrue(isSorted(arr, 1, arr.length - 1, Integer::compare));
    }
    
    @Test
    public void naturalMergesortstressTest() {
        final Random random = new Random(13L);
        Arrays.NaturalMergesort.doPerformPresort(false);
        
        for (int i = 0; i < 10; ++i) {
            final Integer[] array1 = createRandomArray(random);
            final Integer[] array2 = array1.clone();
            final int rndInt1 = random.nextInt(array1.length);
            final int rndInt2 = random.nextInt(array1.length);
            final int fromIndex = Math.min(rndInt1, rndInt2);
            final int toIndex   = Math.max(rndInt1, rndInt2);
            
            Arrays.NaturalMergesort.sort(array1, 
                                         fromIndex, 
                                         toIndex, 
                                         Integer::compare);
            
            java.util.Arrays.sort(array2, fromIndex, toIndex, Integer::compare);
            assertTrue(isSorted(array1, fromIndex, toIndex, Integer::compare));
            assertTrue(isSorted(array2, fromIndex, toIndex, Integer::compare));
            assertTrue(arraysEqual(array1, array2));
        }
    }

    @Test
    public void naturalMergesortsortsZeroElementsPresorted() {
        Arrays.NaturalMergesort.doPerformPresort(true);
        Integer[] arr = {};
        Arrays.NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(Utils.isSorted(arr, Integer::compare));
    }
    
    @Test
    public void naturalMergesortsortsOneElementsPresorted() {
        Arrays.NaturalMergesort.doPerformPresort(true);
        Integer[] arr = { -1 };
        Arrays.NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ -1 }));
    }
    
    @Test
    public void naturalMergesortsortsTwoElementsAlreadySortedPresorted() {
        Arrays.NaturalMergesort.doPerformPresort(true);
        Integer[] arr = { -1, 1 };
        Arrays.NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ -1, 1 }));
    }
    
    @Test
    public void naturalMergesortsortsTwoElementsDescendingPresorted() {
        Arrays.NaturalMergesort.doPerformPresort(true);
        Integer[] arr = { 1, -1 };
        Arrays.NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ -1, 1 }));
    }
    
    @Test
    public void naturalMergesortsortsTwoElementsOnDescendingSubrangePresorted() {
        Arrays.NaturalMergesort.doPerformPresort(true);
        Integer[] arr = { 5, 1, -1, 6 };
        Arrays.NaturalMergesort.sort(arr, 1, 3, Integer::compare);
        assertTrue(isSorted(arr, 1, 3, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 5, -1, 1, 6 }));
    }
    
    @Test
    public void naturalMergesortsortsTwoElementsOnAscendingSubrangePresorted() {
        Arrays.NaturalMergesort.doPerformPresort(true);
        Integer[] arr = { 5, -1, 1, 6 };
        Arrays.NaturalMergesort.sort(arr, 1, 3, Integer::compare);
        assertTrue(isSorted(arr, 1, 3, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 5, -1, 1, 6 }));
    }
    
    @Test
    public void naturalMergesortsortOnTwoRunsPresorted() {
        Arrays.NaturalMergesort.doPerformPresort(true);
        Integer[] arr = { 6, 5, 1, 4, 3, 2 };
        Arrays.NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 1, 2, 3, 4, 5, 6 }));
    }
    
    @Test
    public void naturalMergesortsortOnThreeRunsPresorted() {
        Arrays.NaturalMergesort.doPerformPresort(true);
        Integer[] arr = { 7, 5, 8, 4, 2, 6, 3, 1 };
        Arrays.NaturalMergesort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 1, 2, 3, 4, 5, 6, 7, 8 }));
    }
    
    @Test
    public void naturalMergesortdebug1Presorted() {
        final Random random = new Random(666L);
        Arrays.NaturalMergesort.doPerformPresort(true);
        Integer[] arr = createRandomArray(random, 40);
        Arrays.NaturalMergesort.sort(arr, 1, arr.length - 1, Integer::compare);
        assertTrue(isSorted(arr, 1, arr.length - 1, Integer::compare));
    }
    
    @Test
    public void naturalMergesortstressTestPresorted() {
        final Random random = new Random(13L);
        Arrays.NaturalMergesort.doPerformPresort(true);
        
        for (int i = 0; i < 10; ++i) {
            final Integer[] array1 = createRandomArray(random);
            final Integer[] array2 = array1.clone();
            final int rndInt1 = random.nextInt(array1.length);
            final int rndInt2 = random.nextInt(array1.length);
            final int fromIndex = Math.min(rndInt1, rndInt2);
            final int toIndex   = Math.max(rndInt1, rndInt2);
            
            Arrays.NaturalMergesort.sort(array1, 
                                         fromIndex, 
                                         toIndex,
                                         Integer::compare);
            
            java.util.Arrays.sort(array2, fromIndex, toIndex, Integer::compare);
            assertTrue(isSorted(array1, fromIndex, toIndex, Integer::compare));
            assertTrue(isSorted(array2, fromIndex, toIndex, Integer::compare));
            assertTrue(arraysEqual(array1, array2));
        }
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void naturalMergesortthrowsOnNegativeFromIndex() {
        Arrays.NaturalMergesort.sort(new Integer[] { 1, 2 }, -1, 1, Integer::compare);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void naturalMergesortthrowsOnTooLargeToIndex() {
        Arrays.NaturalMergesort.sort(new Integer[] { 1, 2 }, 0, 3, Integer::compare);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void naturalMergesortthrowsOnFromIndexLargerThanToIndex() {
        Arrays.NaturalMergesort.sort(new Integer[] { 1, 2 }, 1, 0, Integer::compare);
    }
    
    @Test
    public void powerSortSortsZeroElements() {
        Integer[] arr = {};
        Arrays.Powersort.sort(arr, Integer::compare);
        assertTrue(Utils.isSorted(arr, Integer::compare));
    }
    
    @Test
    public void powerSortSortsOneElements() {
        Integer[] arr = { -1 };
        Arrays.Powersort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ -1 }));
    }
    
    @Test
    public void powerSortSortsTwoElementsAlreadySorted() {
        Integer[] arr = { -1, 1 };
        Arrays.Powersort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ -1, 1 }));
    }
    
    @Test
    public void powerSortSortsTwoElementsDescending() {
        Integer[] arr = { 1, -1 };
        Arrays.Powersort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ -1, 1 }));
    }
    
    @Test
    public void powerSortSortsTwoElementsOnDescendingSubrange() {
        Integer[] arr = { 5, 1, -1, 6 };
        Arrays.Powersort.sort(arr, 1, 3, Integer::compare);
        assertTrue(isSorted(arr, 1, 3, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 5, -1, 1, 6 }));
    }
    
    @Test
    public void powerSortSortsTwoElementsOnAscendingSubrange() {
        Integer[] arr = { 5, -1, 1, 6 };
        Arrays.Powersort.sort(arr, 1, 3, Integer::compare);
        assertTrue(isSorted(arr, 1, 3, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 5, -1, 1, 6 }));
    }
    
    @Test
    public void powerSortSortOnTwoRuns() {
        Integer[] arr = { 6, 5, 1, 4, 3, 2 };
        Arrays.Powersort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 1, 2, 3, 4, 5, 6 }));
    }
    
    @Test
    public void powerSortSortOnThreeRuns() {
        Integer[] arr = { 7, 5, 8, 4, 2, 6, 3, 1 };
        Arrays.Powersort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 1, 2, 3, 4, 5, 6, 7, 8 }));
    }
    
    @Test
    public void powerSortDebug1() {
        final Random random = new Random(666L);
        Integer[] arr = createRandomArray(random, 40);
        Arrays.Powersort.sort(arr, 1, arr.length - 1, Integer::compare);
        assertTrue(isSorted(arr, 1, arr.length - 1, Integer::compare));
    }
    
    @Test
    public void powerSortStressTest() {
        final Random random = new Random(13L);
        
        for (int i = 0; i < 10; ++i) {
            final Integer[] array1 = createRandomArray(random);
            final Integer[] array2 = array1.clone();
            final int rndInt1 = random.nextInt(array1.length);
            final int rndInt2 = random.nextInt(array1.length);
            final int fromIndex = Math.min(rndInt1, rndInt2);
            final int toIndex   = Math.max(rndInt1, rndInt2);
            
            Arrays.Powersort.sort(array1, fromIndex, toIndex, Integer::compare);
            java.util.Arrays.sort(array2, fromIndex, toIndex, Integer::compare);
            assertTrue(isSorted(array1, fromIndex, toIndex, Integer::compare));
            assertTrue(isSorted(array2, fromIndex, toIndex, Integer::compare));
            assertTrue(arraysEqual(array1, array2));
        }
    }
    
    @Test
    public void peekSortSortsZeroElements() {
        Integer[] arr = {};
        Arrays.Peeksort.sort(arr, Integer::compare);
        assertTrue(Utils.isSorted(arr, Integer::compare));
    }
    
    @Test
    public void peekSortSortsOneElements() {
        Integer[] arr = { -1 };
        Arrays.Peeksort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ -1 }));
    }
    
    @Test
    public void peekSortSortsTwoElementsAlreadySorted() {
        Integer[] arr = { -1, 1 };
        Arrays.Peeksort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ -1, 1 }));
    }
    
    @Test
    public void peekSortSortsTwoElementsDescending() {
        Integer[] arr = { 1, -1 };
        Arrays.Peeksort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ -1, 1 }));
    }
    
    @Test
    public void peekSortSortsTwoElementsOnDescendingSubrange() {
        Integer[] arr = { 5, 1, -1, 6 };
        Arrays.Peeksort.sort(arr, 1, 3, Integer::compare);
        assertTrue(isSorted(arr, 1, 3, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 5, -1, 1, 6 }));
    }
    
    @Test
    public void peekSortSortsTwoElementsOnAscendingSubrange() {
        Integer[] arr = { 5, -1, 1, 6 };
        Arrays.Peeksort.sort(arr, 1, 3, Integer::compare);
        assertTrue(isSorted(arr, 1, 3, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 5, -1, 1, 6 }));
    }
    
    @Test
    public void peekSortSortOnTwoRuns() {
        Integer[] arr = { 6, 5, 1, 4, 3, 2 };
        Arrays.Peeksort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 1, 2, 3, 4, 5, 6 }));
    }
    
    @Test
    public void peekSortSortOnThreeRuns() {
        Integer[] arr = { 7, 5, 8, 4, 2, 6, 3, 1 };
        Arrays.Peeksort.sort(arr, Integer::compare);
        assertTrue(isSorted(arr, Integer::compare));
        assertTrue(arraysEqual(arr, new Integer[]{ 1, 2, 3, 4, 5, 6, 7, 8 }));
    }
    
    @Test
    public void peekSortDebug1() {
        final Random random = new Random(666L);
        Integer[] arr = createRandomArray(random, 40);
        Arrays.Peeksort.sort(arr, 1, arr.length - 1, Integer::compare);
        assertTrue(isSorted(arr, 1, arr.length - 1, Integer::compare));
    }
    
    @Test
    public void peekSortStressTest() {
        final Random random = new Random(13L);
        
        for (int i = 0; i < 10; ++i) {
            final Integer[] array1 = createRandomArray(random);
            final Integer[] array2 = array1.clone();
            final int rndInt1 = random.nextInt(array1.length);
            final int rndInt2 = random.nextInt(array1.length);
            final int fromIndex = Math.min(rndInt1, rndInt2);
            final int toIndex   = Math.max(rndInt1, rndInt2);
            
            Arrays.Peeksort.sort(array1, fromIndex, toIndex, Integer::compare);
            java.util.Arrays.sort(array2, fromIndex, toIndex, Integer::compare);
            assertTrue(isSorted(array1, fromIndex, toIndex, Integer::compare));
            assertTrue(isSorted(array2, fromIndex, toIndex, Integer::compare));
            assertTrue(arraysEqual(array1, array2));
        }
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
