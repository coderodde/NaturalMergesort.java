package io.github.coderodde.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

/**
 * This class implements natural merge sort.
 */
public final class NaturalMergesort {
    
    /**
     * If presorting is enabled, this constant specifies the minimum run length
     * after presorting.
     */
    private static final int PRESORTED_RUN_LENGTH = 16;
    
    /**
     * The boolean flag indicating whether to presort or not.
     */
    private static volatile boolean performPresort = false;
    
    public static void doPerformPresort(final boolean performPresort) {
        NaturalMergesort.performPresort = performPresort;
    }
    
    /**
     * Sorts entirely the input array.
     * 
     * @param <T>   the type of array components.
     * @param array the array to sort.
     * @param cmp   the comparator.
     */
    public static <T> void sort(final T[] array, 
                                final Comparator<? super T> cmp) {
        Objects.requireNonNull(array, "The input array is null.");
        
        sort(array,
             0,
             array.length,
             cmp);
    }
     
    /**
     * Sorts the range {@code array[fromIndex ... (toIndex - 1)]}.
     * 
     * @param <T>       the type of array components.
     * @param array     the array to sort.
     * @param fromIndex the start index.
     * @param toIndex   the ending index.
     * @param cmp       the comparator.
     */
    public static <T> void sort(final T[] array,
                                final int fromIndex,
                                final int toIndex,
                                final Comparator<? super T> cmp) {
        
        Objects.requireNonNull(array, "The input array is null.");
        
        checkIndices(fromIndex, 
                     toIndex, 
                     array.length);
        
        Objects.requireNonNull(cmp, "The input comparator is null.");
        
        final int rangeLength = toIndex - fromIndex;
        
        if (rangeLength < 2) {
            // Trivially sorted.
            return;
        }
        
        RunLengthQueue<T> queue = new RunLengthQueue<>(array,
                                                       fromIndex, 
                                                       toIndex,
                                                       cmp);
        // Let the run length queue build itself:
        queue.build();
        
        if (performPresort) {
            queue = queue.presort();
        }
        
        if (queue.size() < 2) {
            // No runs to merge, return.
            return;
        }
        
        final int mergePasses = computeNumberOfMergePasses(queue.size());
        final T[] buffer;
        
        T[] source;
        T[] target;
        
        int sourceOffset;
        int targetOffset;
        
        if (mergePasses % 2 == 1) {
            buffer = Arrays.copyOfRange(array, 
                                        fromIndex, 
                                        toIndex);
            
            source = buffer;
            target = array;
            sourceOffset = 0;
            targetOffset = fromIndex;
        } else {
            buffer = (T[]) new Object[rangeLength];
            source = array;
            target = buffer;
            sourceOffset = fromIndex;
            targetOffset = 0;
        }
        
        int runsLeft = queue.size();
        int offset = 0;
        
        while (queue.size() > 1) {
            if (runsLeft == 3) {
                // Suppose the length of the array to sort is 2^10 + 1 = 1025.
                // If we didn't handle this case, after the first merge pass,
                // there would be 2^9 + 1 = 513 + 1 runs left. All this boils 
                // down to the fact that the very last merge will be 
                // superfluous. If we opt to this arrangement in the code, the
                // first merge pass will produce exactly 512 = 2^9, and so the
                // merges will be balanced.
                final int runLengthLeft   = queue.dequeue();
                final int runLengthMiddle = queue.dequeue();
                final int runLengthRight  = queue.dequeue();
                
                // Call 3-way merge procedure:
                merge(source, 
                      target,
                      sourceOffset + offset, 
                      targetOffset + offset, 
                      runLengthLeft,
                      runLengthMiddle,
                      runLengthRight,
                      cmp);
                
                final int runLengthMerged = runLengthLeft
                                          + runLengthMiddle
                                          + runLengthRight;
                
                queue.enqueue(runLengthMerged);
                
                runsLeft = queue.size();
                offset   = 0; // Reset the merging offset.
                
                // Swap the array offsets:
                final int itmp = sourceOffset;
                sourceOffset   = targetOffset;
                targetOffset   = itmp;
                
                // Swap the array roles:
                final T[] atmp = source;
                source = target;
                target = atmp;
                
                continue;
            }
            
            final int runLengthLeft  = queue.dequeue();
            final int runLengthRight = queue.dequeue();
            
            merge(source, 
                  target, 
                  sourceOffset + offset, 
                  targetOffset + offset, 
                  runLengthLeft, 
                  runLengthRight, 
                  cmp);
            
            final int runLengthMerged = runLengthLeft
                                      + runLengthRight;
            
            queue.enqueue(runLengthMerged);
            offset   += runLengthMerged;
            runsLeft -= 2;
            
            if (runsLeft == 0) {
                // Swap the array offsets:
                final int itmp = sourceOffset;
                sourceOffset   = targetOffset;
                targetOffset   = itmp;
                
                // Swap the array roles:
                final T[] atmp = source;
                source = target;
                target = atmp;
                
                // Go to the beginning of the sorted range:
                runsLeft = queue.size();
                offset   = 0;
            }
        }
    }
    
    private NaturalMergesort() {
        
    }
    
    /**
     * This static inner class implements the actual run length queue.
     * 
     * @param <T> the type of the array components.
     */
    private static final class RunLengthQueue<T> {
        
        private final T[]                       array;
        private final int                       fromIndex;
        private final int                       toIndex;
        private final int[]                     storage;
        private final Comparator<? super T>     cmp;
        private final int                       mask;
        private int                             headIndex;
        private int                             tailIndex;
        private int                             size;
        
        RunLengthQueue(final T[] array,
                       final int fromIndex,
                       final int toIndex,
                       final Comparator<? super T> cmp) {
            
            this.array                  = array;
            this.fromIndex              = fromIndex;
            this.toIndex                = toIndex;
            
            final int capacity          = computeRawRunLengthCapacity(fromIndex,
                                                                      toIndex);
            final int fixedCapacity     = fixCapacity(capacity);
            this.mask                   = fixedCapacity - 1;
            this.storage                = new int[fixedCapacity];
            this.cmp                    = cmp;
        }
        
        RunLengthQueue(final T[] array,
                       final int fromIndex,
                       final int toIndex,
                       final int capacity,
                       final Comparator<? super T> cmp) {
            
            this.array     = array;
            this.fromIndex = fromIndex;
            this.toIndex   = toIndex;
            this.storage   = new int[capacity];
            this.mask      = capacity - 1;
            this.cmp       = cmp;
        }
        
        /**
         * Enqueues the run length to the tail of this run length queue.
         * 
         * @param runLength the run length to enqueue.
         */
        void enqueue(final int runLength) {
            storage[tailIndex & mask] = runLength;
            tailIndex = (tailIndex + 1) & mask;
            ++size;
        }
        
        /**
         * Dequeues the head run length.
         * 
         * @return the head run length.
         */
        int dequeue() {
            final int runLength = storage[headIndex];
            headIndex = (headIndex + 1) & mask;
            --size;
            return runLength;
        }
        
        /**
         * Returns the number of runs in this run length queue.
         * 
         * @return the number of runs in this run length queue.
         */
        int size() {
            return size;
        }
        
        /**
         * Returns the smallest power of two no smaller than {@code capacity}.
         * 
         * @param capacity the capacity to fix.
         * 
         * @return a fixed capacity.
         */
        private static int fixCapacity(int capacity) {
            int r = 1;
            
            while (r < capacity) {
                r <<= 1;
            }
            
            return r;
        }
        
        /**
         * Builds the run length queue.
         * 
         * @param array     the array holding the range to sort.
         * @param fromIndex the starting index of the range.
         * @param toIndex   one past the ending index of the range.
         */
        private void build() {
            int indexLeft                 = fromIndex;
            int indexRight                = indexLeft + 1;
            final int upperBoundLeftIndex = toIndex - 1;
            
            while (indexLeft < upperBoundLeftIndex) {
                int indexHead = indexLeft;
                
                // Decide the direction of the next run:
                if (cmp.compare(array[indexLeft], array[indexRight]) <= 0) {
                    // Scan an ascending run:
                    while (indexLeft < upperBoundLeftIndex && 
                           cmp.compare(array[indexLeft], 
                                       array[indexRight]) <= 0) {
                        
                        ++indexLeft;
                        ++indexRight;
                    }
                } else {
                    // Scan a strictly descending run:
                    while (indexLeft < upperBoundLeftIndex &&
                           cmp.compare(array[indexLeft],
                                       array[indexRight]) > 0) {
                        ++indexLeft;
                        ++indexRight;
                    }
                    
                    // Make a strictly descending run strictly ascending:
                    reverseRun(array, 
                               indexHead, 
                               indexRight);
                }
                
                // Store the length of the currently scanned run:
                enqueue(indexRight - indexHead);
                
                // Move the indices to the next pair:
                ++indexLeft;
                ++indexRight;
            }
            
            if (indexLeft == upperBoundLeftIndex) {
                // Once here, we have a leftover run of length 1:
                enqueue(1);
            }
        }
        
        RunLengthQueue<T> presort() {
            final RunLengthQueue<T> presortedQueue = 
              new RunLengthQueue<>(array,
                                   fromIndex,
                                   toIndex, 
                                   fixCapacity(size()),
                                   cmp);
            
            int offset = fromIndex;
            
            mainLoop:
            while (size() != 0) {
                // Start pumping a run:
                final int runLengthLeft = dequeue();
                
                if (runLengthLeft >= PRESORTED_RUN_LENGTH || size() == 0) {
                    // The currently read run is too large to be presorted:
                    offset += runLengthLeft;
                    presortedQueue.enqueue(runLengthLeft);
                    continue;
                }
                
                int runLengthTentative = runLengthLeft;
                
                while (true) {
                    // Keep adding sorted short runs:
                    final int runLengthNext = dequeue();
                    runLengthTentative += runLengthNext;
                    
                    if (runLengthTentative > PRESORTED_RUN_LENGTH) {
                        presortedQueue.enqueue(runLengthTentative - 
                                               runLengthNext);
                        
                        presortedQueue.enqueue(runLengthNext);
                        break;
                    }
                    
                    insertionSort(array,
                                  offset, 
                                  offset + runLengthTentative,
                                  cmp);
                    
                    if (size() == 0) {
                        presortedQueue.enqueue(runLengthTentative);
                        break mainLoop;
                    }
                }
                
                offset += runLengthTentative;
            }
            
            return presortedQueue;
        }
    }
    
    /**
     * Sorts the range {@code array[fromIndex ... toIndex - 1]} via insertion
     * sort.
     * 
     * @param <T>       the type of the array component.
     * @param array     the target array.
     * @param fromIndex the starting, inclusive index.
     * @param toIndex   the ending, exclusive index.
     * @param cmp       the comparator.
     */
    private static <T> void insertionSort(final T[] array,
                                          final int fromIndex,
                                          final int toIndex,
                                          final Comparator<? super T> cmp) {
        for (int i = fromIndex + 1; i < toIndex; ++i) {
            final T key = array[i];
            int j = i - 1;
            
            if (cmp.compare(array[fromIndex], key) < 0) {
                while (cmp.compare(array[j], key) > 0) {
                    array[j + 1] = array[j];
                    --j;
                }
            } else {
                while (j >= fromIndex) {
                    array[j + 1] = array[j];
                    --j;
                }
            }

            array[j + 1] = key;
        }
    }
        
    /**
     * Merges two consecutive runs into one run.
     * 
     * @param <T>            the type of the array components.
     * @param source         the source array.
     * @param target         the target array.
     * @param sourceOffset   the offset to the source array.
     * @param targetOffset   the offset to the target array.
     * @param runLengthLeft  the length of the left run.
     * @param runLengthRight the length of the right run.
     * @param cmp            the comparator.
     */
    private static <T> void merge(final T[] source,
                                  final T[] target,
                                  final int sourceOffset,
                                  int targetOffset,
                                  final int runLengthLeft,
                                  final int runLengthRight,
                                  final Comparator<? super T> cmp) {
        
        int indexLeft  = sourceOffset;
        int indexRight = sourceOffset + runLengthLeft;
        
        final int indexBoundLeft  = indexRight;
        final int indexBoundRight = indexRight + runLengthRight;
        
        while (indexLeft != indexBoundLeft && indexRight != indexBoundRight) {
            
            final T elementLeft  = source[indexLeft];
            final T elementRight = source[indexRight];
            
            target[targetOffset++] = cmp.compare(elementRight, 
                                                 elementLeft) < 0 ?
                    source[indexRight++] :
                    source[indexLeft++];
        }
        
        System.arraycopy(source, 
                         indexLeft, 
                         target, 
                         targetOffset, 
                         indexBoundLeft - indexLeft);
        
        System.arraycopy(source, 
                         indexRight, 
                         target, 
                         targetOffset, 
                         indexBoundRight - indexRight);
    }
    
    /**
     * Performs a 3-way merge.
     * 
     * @param <T>              the type of the array components.
     * @param source           the source array.
     * @param target           the target array.
     * @param sourceOffset     the offset to the source array.
     * @param targetOffset     the offset to the target array.
     * @param runLengthLeft    the length of the left run.
     * @param runLengthMiddle  the length of the middle run.
     * @param runLengthRight   the length of the right run.
     * @param cmp              the comparator.
     */
    private static <T> void merge(final T[] source,
                                  final T[] target,
                                  final int sourceOffset,
                                  int targetOffset,
                                  final int runLengthLeft,
                                  final int runLengthMiddle,
                                  final int runLengthRight,
                                  final Comparator<? super T> cmp) {
        
        int indexLeft   = sourceOffset;
        int indexMiddle = sourceOffset + runLengthLeft;
        int indexRight  = indexMiddle + runLengthMiddle;
        
        final int indexBoundLeft   = indexMiddle;
        final int indexBoundMiddle = indexRight;
        final int IndexBoundRight  = indexRight + runLengthRight;
        
        while (indexLeft   != indexBoundLeft   &&
               indexMiddle != indexBoundMiddle &&
               indexRight  != IndexBoundRight) {
            
            final T elementLeft   = source[indexLeft];
            final T elementMiddle = source[indexMiddle];
            final T elementRight  = source[indexRight];
            
            if (cmp.compare(elementRight, elementMiddle) < 0) {
                // Here, elmeentRight < elementMiddle.
                if (cmp.compare(elementRight, elementLeft) < 0) {
                    target[targetOffset++] = elementRight;
                    ++indexRight;
                } else {
                    target[targetOffset++] = elementLeft;
                    ++indexLeft;
                }
            } else {
                // Here, elementMiddle <= elementRight.
                if (cmp.compare(elementLeft, elementMiddle) <= 0) {
                    target[targetOffset++] = elementLeft;
                    ++indexLeft;
                } else {
                    target[targetOffset++] = elementMiddle;
                    ++indexMiddle;
                }
            }
        }
        
        while (indexLeft != indexBoundLeft && indexMiddle != indexBoundMiddle) {
            
            final T elementLeft   = source[indexLeft];
            final T elementMiddle = source[indexMiddle]; 
            
            target[targetOffset++] = cmp.compare(elementMiddle,
                                                 elementLeft) < 0 ?
                    source[indexMiddle++] :
                    source[indexLeft++];
        }
        
        while (indexLeft != indexBoundLeft && indexRight != IndexBoundRight) {
            
            final T elementLeft  = source[indexLeft];
            final T elementRight = source[indexRight];
            
            target[targetOffset++] = cmp.compare(elementRight,
                                                 elementLeft) < 0 ? 
                    source[indexRight++] :
                    source[indexLeft++];
        }
        
        while (indexMiddle != indexBoundMiddle &&
               indexRight != IndexBoundRight) {
            
            final T elementMiddle = source[indexMiddle];
            final T elementRight  = source[indexRight];
            
            target[targetOffset++] = cmp.compare(elementRight, 
                                                 elementMiddle) < 0 ?
                    source[indexRight++] :
                    source[indexMiddle++];
        }
        
        System.arraycopy(source, 
                         indexLeft, 
                         target, 
                         targetOffset, 
                         indexBoundLeft - indexLeft);
        
        System.arraycopy(source, 
                         indexMiddle, 
                         target, 
                         targetOffset, 
                         indexBoundMiddle - indexMiddle);
        
        System.arraycopy(source, 
                         indexRight, 
                         target, 
                         targetOffset, 
                         IndexBoundRight - indexRight);
    }
    
    /**
     * Computes the run length capacity that can definitely fit all possible 
     * runs in the data to sort.
     * 
     * @param fromIndex the starting index.
     * @param toIndex   the index upper bound.
     * @return capacity for the run length queue.
     */
    static int computeRawRunLengthCapacity(final int fromIndex,
                                           final int toIndex) {
        final int rangeLength = toIndex 
                              - fromIndex;

        return (rangeLength / 2) + 
               (rangeLength % 2 == 0 ? 0 : 1);
    }
    
    /**
     * Reverses the range <code>array[fromIndex ... toIndex - 1]</code>. Used 
     * for making descending runs ascending.
     * 
     * @param array the array holding the desired range.
     * @param fromIndex the least index of the range to reverse.
     * @param toIndex the index one past the greatest element index of the 
     *                range.
     */
    private static <T> void reverseRun(final T[] array, 
                                       final int fromIndex,
                                       final int toIndex) {
        for(int lo = fromIndex, hi = toIndex - 1; lo < hi; ++lo, --hi) {
            final T tmp = array[lo];
            array[lo] = array[hi];
            array[hi] = tmp;
        }
    }

    /**
     * Returns the amount of merge passes over the input range needed to sort 
     * {@code runAmount} runs.
     */
    private static int computeNumberOfMergePasses(final int runAmount) {
        return 32 - Integer.numberOfLeadingZeros(runAmount / 2);
    }
    
    /**
     * Checks the validity of the range indices.
     * 
     * @param fromIndex the index of the leftmost element in the subarray to 
     *                  sort.
     * @param toIndex   the index of the subarray element that is right after 
     *                  the rightmost element belonging to the subarray to sort.
     * @param length    the length of the actual array containing the target
     *                  range to sort.
     */
    private static void checkIndices(final int fromIndex,
                                     final int toIndex,
                                     final int length) {
        
        String exceptionMessage = null;
        
        if (fromIndex < 0) {
            exceptionMessage = String.format("fromIndex(%d) < 0", fromIndex);
        }
        
        if (toIndex > length) {
            exceptionMessage = String.format("toIndex(%d) > length(%d)",
                                             toIndex, 
                                             length);
        }
        
        if (fromIndex > toIndex) {
            exceptionMessage = String.format("fromIndex(%d) > toIndex(%d)", 
                                             fromIndex,
                                             toIndex);
        }
        
        if (exceptionMessage != null) {
            throw new IndexOutOfBoundsException(exceptionMessage);
        }
    }
}
