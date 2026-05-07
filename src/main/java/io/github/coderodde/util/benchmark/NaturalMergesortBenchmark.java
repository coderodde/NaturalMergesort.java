package io.github.coderodde.util.benchmark;

import io.github.coderodde.util.NaturalMergesort;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

final class NaturalMergesortBenchmark {
    
    private static final int NUMBER_OF_ARRAYS = 20;
    
    public static void main(String[] args) {
        
        final Random random = new Random(114L);
        
        final List<Integer[]> dataA1 = createSortedArrays();
        final List<Integer[]> dataA2 = createRandomArrays(random);
        final List<Integer[]> dataA3 = createPresortedArrays(random);
        final List<Integer[]> dataA4 = createBadTailArrays(random);
        
        final List<Integer[]> dataB1 = createSortedArrays();
        final List<Integer[]> dataB2 = createRandomArrays(random);
        final List<Integer[]> dataB3 = createPresortedArrays(random);
        final List<Integer[]> dataB4 = createBadTailArrays(random);
        
        final NaturalMergesortBenchmarkRunnable runnableA1 = 
          new NaturalMergesortBenchmarkRunnable(dataA1);
        
        final NaturalMergesortBenchmarkRunnable runnableA2 = 
          new NaturalMergesortBenchmarkRunnable(dataA2);
        
        final NaturalMergesortBenchmarkRunnable runnableA3 = 
          new NaturalMergesortBenchmarkRunnable(dataA3);
        
        final NaturalMergesortBenchmarkRunnable runnableA4 = 
          new NaturalMergesortBenchmarkRunnable(dataA4);
        
        final ArraysSortBenchmarkRunnable runnableB1 = 
          new ArraysSortBenchmarkRunnable(dataB1);
        
        final NaturalMergesortBenchmarkRunnable runnableB2 = 
          new NaturalMergesortBenchmarkRunnable(dataB2);
        
        final NaturalMergesortBenchmarkRunnable runnableB3 = 
          new NaturalMergesortBenchmarkRunnable(dataB3);
        
        final NaturalMergesortBenchmarkRunnable runnableB4 = 
          new NaturalMergesortBenchmarkRunnable(dataB4);
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
            arrays.add(createBadTailArray(random));
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
    
    private static Integer[] createBadTailArray(final Random random) {
        final Integer[] array = createRandomArray(random);
        
        for (int i = 1_000_000 - 1_000; i < 1_000_000; ++i) {
            array[i] = i - 1_000_010;
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
        
        int getNumberOfDataArrays() {
            return data.size();
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
        
        int getNumberOfDataArrays() {
            return data.size();
        }
    }
}
