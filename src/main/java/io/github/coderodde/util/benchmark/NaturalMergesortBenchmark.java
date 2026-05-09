package io.github.coderodde.util.benchmark;

import io.github.coderodde.statistics.run.Runner;
import io.github.coderodde.util.NaturalMergesort;
import static io.github.coderodde.util.Utils.arraysEqual;
import static io.github.coderodde.util.Utils.isSorted;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

final class NaturalMergesortBenchmark {
    
    private static final int NUMBER_OF_ARRAYS = 10;
    
    public static void main(String[] args) {
        
        final Random random = new Random(114L);
        
        final List<Integer[]> dataA1 = createSortedArrays();
        final List<Integer[]> dataA2 = createRandomArrays(random);
        final List<Integer[]> dataA3 = createPresortedArrays(random);
        final List<Integer[]> dataA4 = createBadTailArrays(random);
        
        final List<Integer[]> dataB1 = copy(dataA1);
        final List<Integer[]> dataB2 = copy(dataA2);
        final List<Integer[]> dataB3 = copy(dataA3);
        final List<Integer[]> dataB4 = copy(dataA4);
        
        final List<Integer[]> dataC1 = copy(dataA1);
        final List<Integer[]> dataC2 = copy(dataA2);
        final List<Integer[]> dataC3 = copy(dataA3);
        final List<Integer[]> dataC4 = copy(dataA4);
        
        final NaturalMergesortBenchmarkRunnable runnableA1 = 
          new NaturalMergesortBenchmarkRunnable(dataA1);
        
        final NaturalMergesortBenchmarkRunnable runnableA2 = 
          new NaturalMergesortBenchmarkRunnable(dataA2);
        
        final NaturalMergesortBenchmarkRunnable runnableA3 = 
          new NaturalMergesortBenchmarkRunnable(dataA3);
        
        final NaturalMergesortBenchmarkRunnable runnableA4 = 
          new NaturalMergesortBenchmarkRunnable(dataA4);
        
        final NaturalMergesortV2BenchmarkRunnable runnableB1 = 
          new NaturalMergesortV2BenchmarkRunnable(dataB1);
        
        final NaturalMergesortV2BenchmarkRunnable runnableB2 = 
          new NaturalMergesortV2BenchmarkRunnable(dataB2);
        
        final NaturalMergesortV2BenchmarkRunnable runnableB3 = 
          new NaturalMergesortV2BenchmarkRunnable(dataB3);
        
        final NaturalMergesortV2BenchmarkRunnable runnableB4 = 
          new NaturalMergesortV2BenchmarkRunnable(dataB4);
        
        final ArraysSortBenchmarkRunnable runnableC1 = 
          new ArraysSortBenchmarkRunnable(dataC1);
        
        final ArraysSortBenchmarkRunnable runnableC2 = 
          new ArraysSortBenchmarkRunnable(dataC2);
        
        final ArraysSortBenchmarkRunnable runnableC3 = 
          new ArraysSortBenchmarkRunnable(dataC3);
        
        final ArraysSortBenchmarkRunnable runnableC4 = 
          new ArraysSortBenchmarkRunnable(dataC4);
        
        System.out.println(
        """
        *********************************************************************
        * After each title --- Title ---, the first row is for the          *
        * NaturalMergesort.sort, the second row is for the                  *
        * NaturalMergesortV2.sort, and the last row is for the Arrays.sort. *
        *********************************************************************
        """
        );
        
        System.out.println("--- Sorted data ---");
        System.out.println(Runner.measure(runnableA1, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableB1, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableC1, NUMBER_OF_ARRAYS));
        System.out.println();
        
        System.out.println("--- Random data ---");
        System.out.println(Runner.measure(runnableA2, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableB2, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableC2, NUMBER_OF_ARRAYS));
        System.out.println();
        
        System.out.println("--- Presorted data ---");
        System.out.println(Runner.measure(runnableA3, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableB3, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableC3, NUMBER_OF_ARRAYS));
        System.out.println();
        
        System.out.println("--- Bad tail data ---");
        System.out.println(Runner.measure(runnableA4, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableB4, NUMBER_OF_ARRAYS));
        System.out.println(Runner.measure(runnableC4, NUMBER_OF_ARRAYS));
        System.out.println();
        
        final boolean equal1 = arrayListsEqual(dataA1, dataB1);
        final boolean equal2 = arrayListsEqual(dataA2, dataB2);
        final boolean equal3 = arrayListsEqual(dataA3, dataB3);
        final boolean equal4 = arrayListsEqual(dataA4, dataB4);
        final boolean equal5 = arrayListsEqual(dataA1, dataC1);
        final boolean equal6 = arrayListsEqual(dataA2, dataC2);
        final boolean equal7 = arrayListsEqual(dataA3, dataC3);
        final boolean equal8 = arrayListsEqual(dataA4, dataC4);
        
        System.out.printf("Algorithms agree: %b.\n", equal1 &&
                                                     equal2 &&
                                                     equal3 &&
                                                     equal4 &&
                                                     equal5 &&
                                                     equal6 &&
                                                     equal7 &&
                                                     equal8);
        
        System.out.printf("All arrays are sorted: %b.\n",
                          allSorted(dataA1) &&
                          allSorted(dataA2) &&
                          allSorted(dataA3) &&
                          allSorted(dataA4) &&
                          allSorted(dataB1) &&
                          allSorted(dataB2) &&
                          allSorted(dataB3) &&
                          allSorted(dataB4) &&
                          allSorted(dataC1) &&
                          allSorted(dataC2) &&
                          allSorted(dataC3) &&
                          allSorted(dataC4));
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
    
    private static List<Integer[]> createBadTailArrays(final Random random) {
        List<Integer[]> arrays = new ArrayList<>(NUMBER_OF_ARRAYS);
        
        for (int i = 0; i < NUMBER_OF_ARRAYS; ++i) {
            arrays.add(createBadTailArray());
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
            Arrays.sort(array, 1_000 * i, 1_000 * (i + 1), Integer::compare);
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
            
            NaturalMergesort.sort(data.get(runned++), Integer::compare);
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
            
            NaturalMergesort.sort(data.get(runned++), Integer::compare);
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
            
            Arrays.sort(data.get(runned++), Integer::compare);
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
