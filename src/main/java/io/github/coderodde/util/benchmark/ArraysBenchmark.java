package io.github.coderodde.util.benchmark;

import io.github.coderodde.util.Arrays;
import static io.github.coderodde.util.Utils.isSorted;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1)
@Measurement(iterations = 3)
@Fork(value = 1)
@Threads(1)
@State(Scope.Thread)
public class ArraysBenchmark {

    private static final int NUMBER_OF_ARRAYS = 5;
    private static final int ARRAY_LENGTH = 500_000;
    private static final long SEED = 114L;

    @Param({
        "SORTED",
        "RANDOM",
        "PRESORTED",
        "BAD_TAIL",
        "ZIG_ZAG",
        "SKEWED"
    })
    private DataShape dataShape;

    private List<Integer[]> sourceData;
    private Integer[] invocationArray;
    private int sourceIndex;

    @Setup(Level.Trial)
    public void createSourceData() {
        final Random random = new Random(SEED);

        sourceData = switch (dataShape) {
            case SORTED -> createSortedArrays();
            case RANDOM -> createRandomArrays(random);
            case PRESORTED -> createPresortedArrays(random);
            case BAD_TAIL -> createBadTailArrays();
            case ZIG_ZAG -> createZigZagArrays();
            case SKEWED -> createSkewedArrays(random);
        };

        sourceIndex = 0;
    }

    @Setup(Level.Invocation)
    public void createInvocationData() {
        invocationArray = sourceData.get(sourceIndex).clone();
        sourceIndex = (sourceIndex + 1) % sourceData.size();
    }

    @Benchmark
    public Integer[] naturalMergesort() {
        Arrays.NaturalMergesort.doPerformPresort(false);
        Arrays.NaturalMergesort.sort(invocationArray, Integer::compare);
        return invocationArray;
    }

    @Benchmark
    public Integer[] naturalMergesortWithPresort() {
        Arrays.NaturalMergesort.doPerformPresort(true);
        Arrays.NaturalMergesort.sort(invocationArray, Integer::compare);
        return invocationArray;
    }

    @Benchmark
    public Integer[] javaArraysSort() {
        java.util.Arrays.sort(invocationArray, Integer::compare);
        return invocationArray;
    }

    @Benchmark
    public Integer[] powersort() {
        Arrays.Powersort.sort(invocationArray, Integer::compare);
        return invocationArray;
    }

    @Benchmark
    public Integer[] peeksort() {
        Arrays.Peeksort.sort(invocationArray, Integer::compare);
        return invocationArray;
    }

    @TearDown(Level.Invocation)
    public void verifyInvocationData() {
        if (!isSorted(invocationArray, Integer::compare)) {
            throw new AssertionError("Benchmark produced non-sorted output.");
        }
    }

    private static List<Integer[]> createSortedArrays() {
        final List<Integer[]> arrays = new ArrayList<>(NUMBER_OF_ARRAYS);

        for (int i = 0; i < NUMBER_OF_ARRAYS; ++i) {
            arrays.add(createSortedArray());
        }

        return arrays;
    }

    private static List<Integer[]> createRandomArrays(final Random random) {
        final List<Integer[]> arrays = new ArrayList<>(NUMBER_OF_ARRAYS);

        for (int i = 0; i < NUMBER_OF_ARRAYS; ++i) {
            arrays.add(createRandomArray(random));
        }

        return arrays;
    }

    private static List<Integer[]> createPresortedArrays(final Random random) {
        final List<Integer[]> arrays = new ArrayList<>(NUMBER_OF_ARRAYS);

        for (int i = 0; i < NUMBER_OF_ARRAYS; ++i) {
            arrays.add(createPresortedArray(random));
        }

        return arrays;
    }

    private static List<Integer[]> createBadTailArrays() {
        final List<Integer[]> arrays = new ArrayList<>(NUMBER_OF_ARRAYS);

        for (int i = 0; i < NUMBER_OF_ARRAYS; ++i) {
            arrays.add(createBadTailArray());
        }

        return arrays;
    }

    private static List<Integer[]> createZigZagArrays() {
        final List<Integer[]> arrays = new ArrayList<>(NUMBER_OF_ARRAYS);

        for (int i = 0; i < NUMBER_OF_ARRAYS; ++i) {
            arrays.add(createZigZagArray());
        }

        return arrays;
    }

    private static List<Integer[]> createSkewedArrays(final Random random) {
        final List<Integer[]> arrays = new ArrayList<>(NUMBER_OF_ARRAYS);

        for (int i = 0; i < NUMBER_OF_ARRAYS; ++i) {
            arrays.add(createSkewedArray(random));
        }

        return arrays;
    }

    private static Integer[] createSortedArray() {
        final Integer[] array = new Integer[ARRAY_LENGTH];

        for (int i = 0; i < array.length; ++i) {
            array[i] = i;
        }

        return array;
    }

    private static Integer[] createRandomArray(final Random random) {
        final Integer[] array = new Integer[ARRAY_LENGTH];

        for (int i = 0; i < array.length; ++i) {
            array[i] = random.nextInt();
        }

        return array;
    }

    private static Integer[] createPresortedArray(final Random random) {
        final Integer[] array = createRandomArray(random);

        for (int i = 0; i < 1_000; ++i) {
            java.util.Arrays.sort(
                    array,
                    500 * i,
                    500 * (i + 1),
                    Integer::compare);
        }

        return array;
    }

    private static Integer[] createBadTailArray() {
        final Integer[] array = new Integer[512 * 1024 + 1_000];
        int index = 0;

        for (int runIndex = 0; runIndex < 1_024; ++runIndex) {
            for (int i = 0; i < 512; ++i) {
                array[index++] = i;
            }
        }

        for (int i = 512 * 1_024; i < array.length; ++i) {
            array[index++] = i - 1_000_000;
        }

        return array;
    }

    private static Integer[] createZigZagArray() {
        final Integer[] array = new Integer[ARRAY_LENGTH];

        for (int i = 0; i < array.length; ++i) {
            array[i] = i;
        }

        for (int i = 0; i + 1 < array.length; i += 2) {
            swap(array, i, i + 1);
        }

        return array;
    }

    private static Integer[] createSkewedArray(final Random random) {
        final Integer[] array = new Integer[ARRAY_LENGTH];
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
            swap(array, i, i + 1);
        }

        return array;
    }

    private static void swap(final Integer[] array,
                             final int index1,
                             final int index2) {

        final Integer tmp = array[index1];
        array[index1] = array[index2];
        array[index2] = tmp;
    }

    public enum DataShape {
        SORTED,
        RANDOM,
        PRESORTED,
        BAD_TAIL,
        ZIG_ZAG,
        SKEWED
    }
}
