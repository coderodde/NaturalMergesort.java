package io.github.coderodde.util.benchmark;

import io.github.coderodde.statistics.run.Runner;
import io.github.coderodde.util.Arrays;
import static io.github.coderodde.util.Utils.arraysEqual;
import static io.github.coderodde.util.Utils.isSorted;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class ArraysBenchmark {
    
    private static final int NUMBER_OF_ARRAYS = 15;
    
    public static void main(String[] args) {
        
        System.out.println("Preparing benchmark data...");
        final Random random = new Random(114L);
        
        final List<Integer[]> dataA1 = createSortedArrays();
        final List<Integer[]> dataA2 = createRandomArrays(random);
        final List<Integer[]> dataA3 = createPresortedArrays(random);
        final List<Integer[]> dataA4 = createBadTailArrays();
        final List<Integer[]> dataA5 = createZigZagArrays();
        final List<Integer[]> dataA6 = createSkewedArrays(random);
        
        final List<Integer[]> dataB1 = copy(dataA1);
        final List<Integer[]> dataB2 = copy(dataA2);
        final List<Integer[]> dataB3 = copy(dataA3);
        final List<Integer[]> dataB4 = copy(dataA4);
        final List<Integer[]> dataB5 = copy(dataA5);
        final List<Integer[]> dataB6 = copy(dataA6);
        
        final List<Integer[]> dataC1 = copy(dataA1);
        final List<Integer[]> dataC2 = copy(dataA2);
        final List<Integer[]> dataC3 = copy(dataA3);
        final List<Integer[]> dataC4 = copy(dataA4);
        final List<Integer[]> dataC5 = copy(dataA5);
        final List<Integer[]> dataC6 = copy(dataA6);
        
        final List<Integer[]> dataD1 = copy(dataA1);
        final List<Integer[]> dataD2 = copy(dataA2);
        final List<Integer[]> dataD3 = copy(dataA3);
        final List<Integer[]> dataD4 = copy(dataA4);
        final List<Integer[]> dataD5 = copy(dataA5);
        final List<Integer[]> dataD6 = copy(dataA6);
        
        System.out.println("Benchmark data prepared.");
        
        final NaturalMergesortBenchmarkRunnable runnableA1 = 
          new NaturalMergesortBenchmarkRunnable(dataA1);
        
        final NaturalMergesortBenchmarkRunnable runnableA2 = 
          new NaturalMergesortBenchmarkRunnable(dataA2);
        
        final NaturalMergesortBenchmarkRunnable runnableA3 = 
          new NaturalMergesortBenchmarkRunnable(dataA3);
        
        final NaturalMergesortBenchmarkRunnable runnableA4 = 
          new NaturalMergesortBenchmarkRunnable(dataA4);
        
        final NaturalMergesortBenchmarkRunnable runnableA5 = 
          new NaturalMergesortBenchmarkRunnable(dataA5);
        
        final NaturalMergesortBenchmarkRunnable runnableA6 = 
          new NaturalMergesortBenchmarkRunnable(dataA6);
        
        final NaturalMergesortV2BenchmarkRunnable runnableB1 = 
          new NaturalMergesortV2BenchmarkRunnable(dataB1);
        
        final NaturalMergesortV2BenchmarkRunnable runnableB2 = 
          new NaturalMergesortV2BenchmarkRunnable(dataB2);
        
        final NaturalMergesortV2BenchmarkRunnable runnableB3 = 
          new NaturalMergesortV2BenchmarkRunnable(dataB3);
        
        final NaturalMergesortV2BenchmarkRunnable runnableB4 = 
          new NaturalMergesortV2BenchmarkRunnable(dataB4);
        
        final NaturalMergesortV2BenchmarkRunnable runnableB5 = 
          new NaturalMergesortV2BenchmarkRunnable(dataB5);
        
        final NaturalMergesortV2BenchmarkRunnable runnableB6 = 
          new NaturalMergesortV2BenchmarkRunnable(dataB6);
        
        final ArraysSortBenchmarkRunnable runnableC1 = 
          new ArraysSortBenchmarkRunnable(dataC1);
        
        final ArraysSortBenchmarkRunnable runnableC2 = 
          new ArraysSortBenchmarkRunnable(dataC2);
        
        final ArraysSortBenchmarkRunnable runnableC3 = 
          new ArraysSortBenchmarkRunnable(dataC3);
        
        final ArraysSortBenchmarkRunnable runnableC4 = 
          new ArraysSortBenchmarkRunnable(dataC4);
        
        final ArraysSortBenchmarkRunnable runnableC5 = 
          new ArraysSortBenchmarkRunnable(dataC5);
        
        final ArraysSortBenchmarkRunnable runnableC6 = 
          new ArraysSortBenchmarkRunnable(dataC6);
        
        final PowersortBenchmarkRunnable runnableD1 = 
          new PowersortBenchmarkRunnable(dataD1);
        
        final PowersortBenchmarkRunnable runnableD2 = 
          new PowersortBenchmarkRunnable(dataD2);
        
        final PowersortBenchmarkRunnable runnableD3 = 
          new PowersortBenchmarkRunnable(dataD3);
        
        final PowersortBenchmarkRunnable runnableD4 = 
          new PowersortBenchmarkRunnable(dataD4);
        
        final PowersortBenchmarkRunnable runnableD5 = 
          new PowersortBenchmarkRunnable(dataD5);
        
        final PowersortBenchmarkRunnable runnableD6 = 
          new PowersortBenchmarkRunnable(dataD6);
        
        System.out.println(
        """
        *********************************************************************
        * After each title --- Title ---, the first row is for the          *
        * NaturalMergesort.sort, the second row is for the                  *
        * NaturalMergesort.sort iwth presorting, the third row is for the   *
        * Arrays.sort, and the fourth row is for the Powersort.sort.        *
        *********************************************************************
        """
        );
        
        System.out.println("--- Sorted data ---");
        System.out.println(Runner.measure(runnableA1, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableB1, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableC1, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableD1, NUMBER_OF_ARRAYS));
        System.out.println();
        
        System.out.println("--- Random data ---");
        System.out.println(Runner.measure(runnableA2, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableB2, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableC2, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableD2, NUMBER_OF_ARRAYS));
        System.out.println();
        
        System.gc();
        
        System.out.println("--- Presorted data ---");
        System.out.println(Runner.measure(runnableA3, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableB3, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableC3, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableD3, NUMBER_OF_ARRAYS));
        System.out.println();
        
        System.gc();
        
        System.out.println("--- Bad tail data ---");
        System.out.println(Runner.measure(runnableA4, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableB4, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableC4, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableD4, NUMBER_OF_ARRAYS));
        System.out.println();
        
        System.gc();
        
        System.out.println("--- Zig zag data ---");
        System.out.println(Runner.measure(runnableA5, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableB5, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableC5, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableD5, NUMBER_OF_ARRAYS));
        System.out.println();
        
        System.gc();
        
        System.out.println("--- Skewed data ---");
        System.out.println(Runner.measure(runnableA6, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableB6, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableC6, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableD6, NUMBER_OF_ARRAYS));
        System.out.println();
        
        final boolean equal1  = arrayListsEqual(dataA1, dataB1);
        final boolean equal2  = arrayListsEqual(dataA2, dataB2);
        final boolean equal3  = arrayListsEqual(dataA3, dataB3);
        final boolean equal4  = arrayListsEqual(dataA4, dataB4);
        final boolean equal5  = arrayListsEqual(dataA5, dataB5);
        final boolean equal6  = arrayListsEqual(dataA6, dataB6);
        final boolean equal7  = arrayListsEqual(dataA1, dataC1);
        final boolean equal8  = arrayListsEqual(dataA2, dataC2);
        final boolean equal9  = arrayListsEqual(dataA3, dataC3);
        final boolean equal10 = arrayListsEqual(dataA4, dataC4);
        final boolean equal11 = arrayListsEqual(dataA5, dataC5);
        final boolean equal12 = arrayListsEqual(dataA6, dataC6);
        final boolean equal13 = arrayListsEqual(dataA1, dataD1);
        final boolean equal14 = arrayListsEqual(dataA2, dataD2);
        final boolean equal15 = arrayListsEqual(dataA3, dataD3);
        final boolean equal16 = arrayListsEqual(dataA4, dataD4);
        final boolean equal17 = arrayListsEqual(dataA5, dataD5);
        final boolean equal18 = arrayListsEqual(dataA6, dataD6);
        
        System.out.printf("Algorithms agree: %b.\n", equal1 &&
                                                     equal2 &&
                                                     equal3 &&
                                                     equal4 &&
                                                     equal5 &&
                                                     equal6 &&
                                                     equal7 &&
                                                     equal8 &&
                                                     equal9 &&
                                                     equal10 &&
                                                     equal11 &&
                                                     equal12 &&
                                                     equal13 &&
                                                     equal14 &&
                                                     equal15 &&
                                                     equal16 &&
                                                     equal17 &&
                                                     equal18);
        
        System.out.printf("All arrays are sorted: %b.\n",
                          allSorted(dataA1) &&
                          allSorted(dataA2) &&
                          allSorted(dataA3) &&
                          allSorted(dataA4) &&
                          allSorted(dataA5) &&
                          allSorted(dataA6) &&
                          allSorted(dataB1) &&
                          allSorted(dataB2) &&
                          allSorted(dataB3) &&
                          allSorted(dataB4) &&
                          allSorted(dataB5) &&
                          allSorted(dataB6) &&
                          allSorted(dataC1) &&
                          allSorted(dataC2) &&
                          allSorted(dataC3) &&
                          allSorted(dataC4) &&
                          allSorted(dataC5) &&
                          allSorted(dataC6) &&
                          allSorted(dataD1) &&
                          allSorted(dataD2) &&
                          allSorted(dataD3) &&
                          allSorted(dataD4) &&
                          allSorted(dataD5) &&
                          allSorted(dataD6));
    }
    
    private static List<Integer[]> createSortedArrays() {
        List<Integer[]> arrays = new ArrayList<>(NUMBER_OF_ARRAYS);
        
        for (int i = 0; i < NUMBER_OF_ARRAYS; ++i) {
            arrays.add(createSortedArray());
        }
        
        return arrays;
    }
    
    private static List<Integer[]> createRandomArrays(final Random random) {
        List<Integer[]> arrays = new ArrayList<>(NUMBER_OF_ARRAYS);
        
        for (int i = 0; i < NUMBER_OF_ARRAYS; ++i) {
            arrays.add(createRandomArray(random));
        }
        
        return arrays;
    }
    
    private static List<Integer[]> createPresortedArrays(final Random random) {
        List<Integer[]> arrays = new ArrayList<>(NUMBER_OF_ARRAYS);
        
        for (int i = 0; i < NUMBER_OF_ARRAYS; ++i) {
            arrays.add(createPresortedArray(random));
        }
        
        return arrays;
    }
    
    private static List<Integer[]> createBadTailArrays() {
        List<Integer[]> arrays = new ArrayList<>(NUMBER_OF_ARRAYS);
        
        for (int i = 0; i < NUMBER_OF_ARRAYS; ++i) {
            arrays.add(createBadTailArray());
        }
        
        return arrays;
    }
    
    private static List<Integer[]> createZigZagArrays() {
        List<Integer[]> arrays = new ArrayList<>(NUMBER_OF_ARRAYS);
        
        for (int i = 0; i < NUMBER_OF_ARRAYS; ++i) {
            arrays.add(createZigZagArray());
        }
        
        return arrays;
    }
    
    private static List<Integer[]> createSkewedArrays(final Random random) {
        List<Integer[]> arrays = new ArrayList<>(NUMBER_OF_ARRAYS);
        
        for (int i = 0; i < NUMBER_OF_ARRAYS; ++i) {
            arrays.add(createSkewedArray(random));
        }
        
        return arrays;
    }
    
    private static List<Integer[]> copy(final List<Integer[]> integerList) {
        final List<Integer[]> arrays = new ArrayList<>(integerList.size());
        
        for (int i = 0; i < integerList.size(); ++i) {
            arrays.add(integerList.get(i).clone());
        }
        
        return arrays;
    }
    
    private static Integer[] createSortedArray() {
        final Integer[] array = new Integer[1_000_000];
        
        for (int i = 0; i < 1_000_000; ++i) {
            array[i] = i;
        }
        
        return array;
    }
    
    private static Integer[] createRandomArray(final Random random) {
        final Integer[] array = new Integer[1_000_000];
        
        for (int i = 0; i < 1_000_000; ++i) {
            array[i] = random.nextInt();
        }
        
        return array;
    }
    
    private static Integer[] createPresortedArray(final Random random) {
        final Integer[] array = createRandomArray(random);
        
        for (int i = 0; i < 1_000; ++i) {
            java.util.Arrays.sort(
                    array, 
                    1_000 * i, 
                    1_000 * (i + 1), 
                    Integer::compare);
        }
        
        return array;
    }
    
    private static Integer[] createBadTailArray() {
        final Integer[] array = new Integer[1024 * 1024 + 1000];
        
        int index = 0;
        
        for (int runIndex = 0; runIndex < 1024; ++runIndex) {
            for (int i = 0; i < 1024; ++i) {
                array[index++] = i;
            }
        }
        
        for (int i = 1024 * 1024; i < array.length; ++i) {
            array[index++] = i - 2_000_000;
        }
        
        return array;
        
    }
    
    private static Integer[] createZigZagArray() {
        final Integer[] array = new Integer[1_000_000];
        
        for (int i = 0; i < array.length; ++i) {
            array[i] = i;
        }
        
        for (int i = 0; i < array.length; i += 2) {
            Integer tmp = array[i];
            array[i] = array[i + 1];
            array[i + 1] = tmp;
        }
        
        return array;
        
    }
    
    private static Integer[] createSkewedArray(final Random random) {
        final Integer[] array = new Integer[1_000_000];
        
        int index = 0;
        
        while (index < array.length) {
            final int remaining = array.length - index;
            int currentRunLength = (1 + random.nextInt(remaining)) / 57;
            currentRunLength = Math.min(remaining, 
                                        Math.max(4, currentRunLength));
            
            for (int j = index; 
                    j < Math.min(index + currentRunLength, array.length);
                    j++) {
                
                array[j] = j - index;
            }
            
            index += currentRunLength;
        }
        
        for (int i = 0; i + 1 < array.length; i += 2) {
            Integer tmp = array[i];
            array[i] = array[i + 1];
            array[i + 1] = tmp;
        }
        
        return array;
    }
    
    private static final class NaturalMergesortBenchmarkRunnable 
            implements Runnable {

        private final List<Integer[]> data = new ArrayList<>();
        private int runned = 0;
        
        NaturalMergesortBenchmarkRunnable(final List<Integer[]> data) {
            this.data.addAll(data);
        }
        
        @Override
        public void run() {
            if (runned == data.size()) {
                throw new IllegalStateException("Should not get here.");
            }
            
            Arrays.NaturalMergesort.doPerformPresort(false);
            Arrays.NaturalMergesort.sort(data.get(runned++), Integer::compare);
        }
    }
    
    private static final class NaturalMergesortV2BenchmarkRunnable 
            implements Runnable {

        private final List<Integer[]> data = new ArrayList<>();
        private int runned = 0;
        
        NaturalMergesortV2BenchmarkRunnable(final List<Integer[]> data) {
            this.data.addAll(data);
        }
        
        @Override
        public void run() {
            if (runned == data.size()) {
                throw new IllegalStateException("Should not get here.");
            }
            
            Arrays.NaturalMergesort.doPerformPresort(true);
            Arrays.NaturalMergesort.sort(data.get(runned++), Integer::compare);
        }
    }
    
    private static final class PowersortBenchmarkRunnable 
            implements Runnable {

        private final List<Integer[]> data = new ArrayList<>();
        private int runned = 0;
        
        PowersortBenchmarkRunnable(final List<Integer[]> data) {
            this.data.addAll(data);
        }
        
        @Override
        public void run() {
            if (runned == data.size()) {
                throw new IllegalStateException("Should not get here.");
            }
            
            Arrays.Powersort.sort(data.get(runned++), Integer::compare);
        }
    }
    
    private static final class ArraysSortBenchmarkRunnable 
            implements Runnable {

        private final List<Integer[]> data = new ArrayList<>();
        private int runned = 0;
        
        ArraysSortBenchmarkRunnable(final List<Integer[]> data) {
            this.data.addAll(data);
        }
        
        @Override
        public void run() {
            if (runned == data.size()) {
                throw new IllegalStateException("Should not get here.");
            }
            
            java.util.Arrays.sort(data.get(runned++), Integer::compare);
        }
    }
        
    private static boolean arrayListsEqual(final List<Integer[]> l1,
                                           final List<Integer[]> l2) {
        if (l1.size() != l2.size()) {
            return false;
        }
        
        for (int i = 0; i < l1.size(); ++i) {
            if (!arraysEqual(l1.get(i), l2.get(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    private static boolean allSorted(final List<Integer[]> arrayList) {
        for (final Integer[] array : arrayList) {
            if (!isSorted(array, Integer::compare)) {
                return false;
            }
        }
        
        return true;
    }
}
