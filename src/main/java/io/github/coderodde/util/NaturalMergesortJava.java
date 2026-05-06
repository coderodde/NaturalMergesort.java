package io.github.coderodde.util;

import java.util.Comparator;
import java.util.Objects;

/**
 * This class implements natural merge sort for {@code Comparable} objects.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.61 (Oct 1, 2015)
 */
public final class NaturalMergesortJava {

    private static final int INSERTION_SORT_THRESHOLD = 32;
    
    public static <T> void sort(final T[] array, 
                                final Comparator<? super T> cmp) {
        Objects.requireNonNull(array, "The input array is null.");
        
        sort(array,
             0,
             array.length,
             cmp);
    }
    
    public static <T> void sort(final T[] array,
                                final int fromIndex,
                                final int toIndex,
                                final Comparator<? super T> cmp) {
        Objects.requireNonNull(array, "The input array is null.");
        checkIndices(fromIndex, toIndex, array.length);
        Objects.requireNonNull(cmp, "The input comparator is null.");
        
        final int rangeLength = toIndex - fromIndex;
        
        if (rangeLength < 2) {
            // Trivially sorted.
            return;
        }
        
        final RunLengthQueue<T> queue = new RunLengthQueue<>(array, 
                                                             fromIndex, 
                                                             toIndex,
                                                             cmp);
        
        queue.build(array,
                    fromIndex, 
                    toIndex);
        
        queue.presortRuns();
    }
    
    private NaturalMergesortJava() {
        
    }
    
    private static final class RunLengthQueue<T> {
        
        private static final int MINIMUM_CAPACITY = 256;
        
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
        
        void enqueue(final int runLength) {
            storage[tailIndex & mask] = runLength;
            tailIndex = (tailIndex + 1) & mask;
            ++size;
        }
        
        int dequeue() {
            final int runLength = storage[headIndex];
            headIndex = (headIndex + 1) & mask;
            --size;
            return runLength;
        }
        
        int size() {
            return size;
        }
        
        private static int fixCapacity(int capacity) {
            capacity = Math.max(capacity, MINIMUM_CAPACITY);
            int r = 1;
            
            while (r < capacity) {
                r <<= 1;
            }
            
            return r;
        }
        
        private void build(final T[] array,
                           final int fromIndex,
                           final int toIndex) {
            
            int leftIndex                   = fromIndex;
            int rightIndex                  = leftIndex + 1;
            final int upperBoundLeftIndex   = toIndex - 1;
            
            while (leftIndex < upperBoundLeftIndex) {
                int headIndex = leftIndex;
                
                // Decide the direction of the next run:
                if (cmp.compare(array[leftIndex], array[rightIndex]) <= 0) {
                    // Scan an ascending run:
                    while (leftIndex < upperBoundLeftIndex && 
                           cmp.compare(array[leftIndex], 
                                       array[rightIndex]) <= 0) {
                        
                        ++leftIndex;
                        ++rightIndex;
                    }
                } else {
                    // Scan a strictly descending run:
                    while (leftIndex < upperBoundLeftIndex &&
                           cmp.compare(array[leftIndex],
                                       array[rightIndex]) > 0) {
                        ++leftIndex;
                        ++rightIndex;
                    }
                    
                }
                
                enqueue(leftIndex - headIndex + 1);
                
                ++leftIndex;
                ++rightIndex;
            }
            
            if (leftIndex == upperBoundLeftIndex) {
                enqueue(1);
            }
        }
        
        private void presortRuns() {
            if (size == 1) {
                final boolean strictlyDescending = 
                    isStrictlyDescending(fromIndex);
                
                if (strictlyDescending) {
                    reverseSingleRun();
                }
                
                return;
            }
            
            
        }
        
        private void sortRuns() {
            
        }
        
        private void reverseSingleRun() {
            for (int lo = fromIndex, hi = toIndex - 1; lo < hi; ++lo, --hi) {
                final T tmp     = array[lo];
                array[lo]       = array[hi];
                array[hi]       = tmp;
            }
        }
        
        private boolean isStrictlyDescending(final int leftIndex) {
            return cmp.compare(array[leftIndex], array[leftIndex + 1]) > 0;
        }
        
        private boolean isAscending(final int leftIndex) {
            return !isStrictlyDescending(leftIndex);
        }
    }
    
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
    
    static int computeRawRunLengthCapacity(final int fromIndex,
                                           final int toIndex) {
        final int rangeLength = toIndex 
                              - fromIndex;

        return (rangeLength / 2) + 
               (rangeLength % 2 == 0 ? 0 : 1);
    }
    
    private static <T> RunLengthQueue<T> 
        buildRunLengthQueue(final T[] array,
                            final int fromIndex,
                            final int toIndex,
                            final Comparator<? super T> cmp) {
            
            final RunLengthQueue<T> runLengthQueue =
              new RunLengthQueue<>(array, fromIndex, toIndex, cmp);
            
            int head;
            int left  = fromIndex;
            int right = left + 1;
            final int last = toIndex - 1; // Last valid index.
            
            while (left < last) {
                head = left;
                
                // Decide the direction of the next run:
                if (cmp.compare(array[left++], array[right++]) <= 0) {
                    // Once here, the current run is ascending (not necessarily
                    // strictly):
                    while (left < last && 
                           cmp.compare(array[left], array[right]) <= 0) {
                        ++left;
                        ++right;
                    }
                } else {
                    // Once here, the current run is STRICTLY descending:
                    while (left < last && 
                           cmp.compare(array[left], array[right]) > 0) {
                        ++left;
                        ++right;
                    }
                }
                
                runLengthQueue.equals(left - head + 1);
                
                ++left;
                ++right;
            }
            
            // A special case: the very last element may be left without buddies
            // so make it (the only) 1-element run.
            if (left == last) {
                runLengthQueue.enqueue(1);
            }
            
            return runLengthQueue;
        }
    
//    private NaturalMergesort(Object[] array, int fromIndex, int toIndex) {
//        if (toIndex - fromIndex < 2) {
//            // Nothing to sort.
//            return;
//        }
//
//        this.queue = buildRunSizeQueue(array, fromIndex, toIndex);
//        Object[] buffer = Arrays.copyOfRange(array, fromIndex, toIndex);
//        int mergePasses = getPassAmount(queue.size());
//
//        if ((mergePasses & 1) == 1) {
//            // Odd amount of passes over the entire range; set the buffer array 
//            // as source so that the sorted shit ends up in the original array.
//            source = buffer;
//            target = array;
//            sourceOffset = 0;
//            targetOffset = fromIndex;
//        } else {
//            // Arrange the stuff such that after the last merge pass all shit is
//            // in the argument array.
//            source = array;
//            target = buffer;
//            sourceOffset = fromIndex;
//            targetOffset = 0;
//        }
//
//        sort();
//    }
//
//    private void sort() {    
//        // The amount of runs in current merge pass that were not processed yet.
//        int runsLeft = queue.size();
//        // The amount of elements processed from beginnig of the ranges.
//        int offset = 0;
//
//        // While there are runs to merge, do:
//        while (queue.size() > 1) {
//            if (runsLeft == 3) {
//                // We handle this special case in order to get fast to the state
//                // where the amount of remaining runs is a power of two. We do 
//                // this for the following reason: Suppose you have 1048577 =
//                // 1048576 + 1 = 2^(20) + 1 elements in the requested range.
//                // Now the algorithm would sort the first 2^(20) element AND
//                // will have to do one more merge pass just for putting the last
//                // orphan element to its correct position.
//                int leftRunLength = queue.dequeue();
//                int middleRunLength = queue.dequeue();
//                int rightRunLength = queue.dequeue();
//
//                merge(offset,
//                      leftRunLength,
//                      middleRunLength,
//                      rightRunLength);
//
//                queue.enqueue(leftRunLength +
//                              middleRunLength + rightRunLength);
//
//                int itmp = sourceOffset;
//                sourceOffset = targetOffset;
//                targetOffset = itmp;
//
//                Object[] tmp = source;
//                source = target;
//                target = tmp;
//
//                runsLeft = queue.size();
//                offset = 0;
//                continue;
//            }
//
//            int leftRunLength =  queue.dequeue();
//            int rightRunLength = queue.dequeue();
//
//            merge(offset,
//                  leftRunLength, 
//                  rightRunLength);
//
//            // Bounce the run we obtained by merging the two runs to the tail.
//            queue.enqueue(leftRunLength + rightRunLength);
//            offset += leftRunLength + rightRunLength;
//            runsLeft -= 2;
//
//            if (runsLeft == 0) {
//                // Swap array offsets.
//                int itmp = sourceOffset;
//                sourceOffset = targetOffset;
//                targetOffset = itmp;
//
//                // Swap array roles.
//                Object[] tmp = source;
//                source = target;
//                target = tmp;
//                // Go to the beginning of the array.
//                runsLeft = queue.size();
//                offset = 0;
//            }
//        }
//    }
//
//    /**
//     * Sorts the entire input array. 
//     * 
//     * @param array the array to sort.
//     */
//    public static void sort(Object[] array) {
//        sort(array, 0, array.length);       
//    }
//
//    /**
//     * Sorts a specific range in the input array.
//     * 
//     * @param array     the array holding the target range.
//     * @param fromIndex the starting, inclusive index of the range to sort.
//     * @param toIndex   the ending, exclusive index of the range to sort.
//     */
//    public static void sort(Object[] array, int fromIndex, int toIndex) {
//        new NaturalMergesort(array, fromIndex, toIndex).sort();
//    }
//
//    /**
//     * Reverses the range <code>array[fromIndex ... toIndex - 1]</code>. Used 
//     * for making descending runs ascending.
//     * 
//     * @param array the array holding the desired range.
//     * @param fromIndex the least index of the range to reverse.
//     * @param toIndex the index one past the greatest index of the range.
//     */
//    public static void reverseRun(Object[] array, 
//                                  int fromIndex,
//                                  int toIndex) {
//        for(int l = fromIndex, r = toIndex - 1; l < r; ++l, --r) {
//            Object tmp = array[l];
//            array[l] = array[r];
//            array[r] = tmp;
//        }
//    }
//
//    /**
//     * This method implements a 3-way merge operation.
//     * 
//     * @param offset          the amount of elements to skip from the beginning
//     *                        of the ranges.
//     * @param leftRunLength   the length of the left run.
//     * @param middleRunLength the length of the middle run.
//     * @param rightRunLength  the length of the right run.
//     */
//    private void merge(int offset,
//                       int leftRunLength,
//                       int middleRunLength,
//                       int rightRunLength) {
//        int left = sourceOffset + offset;
//        int middle = left + leftRunLength;
//        int right = middle + middleRunLength;
//
//        int leftBound = middle;
//        int middleBound = right;
//        int rightBound = right + rightRunLength;
//        int placementOffset = targetOffset + offset;
//
//        while (left < leftBound && middle < middleBound && right < rightBound) {
//            Comparable cLeft   = (Comparable) source[left];
//            Comparable cMiddle = (Comparable) source[middle];
//            Comparable cRight  = (Comparable) source[right];
//
//            if (cRight.compareTo(cMiddle) < 0) {
//                // Here, cRight < cMiddle
//                if (cRight.compareTo(cLeft) < 0) {
//                    target[placementOffset++] = cRight;
//                    ++right;
//                } else {
//                    target[placementOffset++] = cLeft;
//                    ++left;
//                }
//            } else {
//                // Here, cMiddle <= cRight.
//                if (cLeft.compareTo(cMiddle) <= 0) {
//                    target[placementOffset++] = cLeft;
//                    ++left;
//                } else {
//                    target[placementOffset++] = cMiddle;
//                    ++middle;
//                }
//            }
//        }
//
//        while (left < leftBound && middle < middleBound) {
//            Comparable cLeft   = (Comparable) source[left];
//            Comparable cMiddle = (Comparable) source[middle];
//            target[placementOffset++] = cMiddle.compareTo(cLeft) < 0 ?
//                    source[middle++] :
//                    source[left++] ;
//        }
//
//        while (left < leftBound && right < rightBound) {
//            Comparable cLeft  = (Comparable) source[left];
//            Comparable cRight = (Comparable) source[right];
//            target[placementOffset++] = cRight.compareTo(cLeft) < 0 ?
//                    source[right++] :
//                    source[left++];
//        }
//
//        while (middle < middleBound && right < rightBound) {
//            Comparable cMiddle = (Comparable) source[middle];
//            Comparable cRight  = (Comparable) source[right];
//            target[placementOffset++] = cMiddle.compareTo(cRight) < 0 ?
//                    source[middle++] :
//                    source[right++];
//        }
//
//        System.arraycopy(source, 
//                         left, 
//                         target, 
//                         placementOffset, 
//                         leftBound - left);
//
//        System.arraycopy(source, 
//                         middle, 
//                         target, 
//                         placementOffset, 
//                         middleBound - middle);
//
//        System.arraycopy(source, 
//                         right, 
//                         target, 
//                         placementOffset, 
//                         rightBound - right);
//    }
//
//    /**
//     * This method implements the merging routine.
//     * 
//     * @param offset         the amount of elements to skip from the beginning 
//     *                       of each array.
//     * @param leftRunLength  the length of the left run.
//     * @param rightRunLength the length of the right run.
//     */
//    private void merge(int offset,
//                       int leftRunLength,
//                       int rightRunLength) {
//        int left = sourceOffset + offset;
//        int right = left + leftRunLength;
//
//        int leftBound = right;
//        int rightBound = right + rightRunLength;
//        int placementOffset = targetOffset + offset;
//
//        while (left < leftBound && right < rightBound) {
//            target[placementOffset++] =
//                    ((Comparable) source[right]).compareTo(source[left]) < 0 ?
//                                  source[right++] :
//                                  source[left++];
//        }
//
//        System.arraycopy(source, 
//                         left, 
//                         target, 
//                         placementOffset, 
//                         leftBound - left);
//
//        System.arraycopy(source, 
//                         right, 
//                         target, 
//                         placementOffset, 
//                         rightBound - right);
//    }
//
//    /**
//     * This class method returns the amount of merge passes over the input range
//     * needed to sort {@code runAmount} runs.
//     */
//    private static int getPassAmount(int runAmount) {
//        return 32 - Integer.numberOfLeadingZeros(runAmount / 2);
//    }
//
//    /**
//     * Scans the runs over the range {@code array[fromIndex .. toIndex - 1]} and 
//     * returns a {@link UnsafeIntQueue} containing the sizes of scanned runs in 
//     * the same order as they appear in the input range.
//     * 
//     * @param array     the array containing the desired range.
//     * @param fromIndex the starting, inclusive index of the range to scan.
//     * @param toIndex   the ending, exclusive index of the range to scan.
//     * 
//     * @return a {@code UnsafeIntQueue} describing the lengths of the runs in 
//     * the input range.
//     */
//    static UnsafeIntQueue buildRunSizeQueue(Object[] array, 
//                                            int fromIndex,
//                                            int toIndex) {
//        UnsafeIntQueue queue = 
//          new UnsafeIntQueue(((toIndex - fromIndex) >>> 1) + 1);
//
//        int head;
//        int left = fromIndex;
//        int right = left + 1;
//        int last = toIndex - 1;
//
//        while (left < last) {
//            head = left;
//
//            // Decide the direction of the next run.
//            if (((Comparable) array[left++]).compareTo(array[right++]) <= 0) {
//                // Scan an ascending run.
//                while (left < last
//                        && ((Comparable) array[left])
//                              .compareTo(array[right]) <= 0) {
//                    ++left;
//                    ++right;
//                }
//
//                queue.enqueue(left - head + 1);
//            } else {
//                // Scan a strictly descending run.
//                while (left < last
//                        && ((Comparable) array[left])
//                              .compareTo(array[right]) > 0) {
//                    ++left;
//                    ++right;
//                }
//
//                queue.enqueue(left - head + 1);
//                // We reverse a strictly descending run as to minimize the
//                // the amount of runs scanned in total. Strictness is required.
//                reverseRun(array, head, right);
//            }
//
//            ++left;
//            ++right;
//        }
//
//        // A special case: the very last element may be left without buddies
//        // so make it (the only) 1-element run.
//        if (left == last) {
//            queue.enqueue(1);
//        }
//
//        return queue;
//    }
//
//    /**
//     * This is the implementation class for an array-based queue of integers. It 
//     * sacrifices under- and overflow checks as to squeeze a little bit more of
//     * efficiency and thus is an ad-hoc data structure hidden from the client
//     * programmers.
//     * 
//     * @author Rodion Efremov
//     * @version 2014.12.01
//     */
//    private static final class UnsafeIntQueue {
//
//        /**
//         * The minimum capacity of this queue.
//         */
//        private static final int MINIMUM_CAPACITY = 256;
//
//        /**
//         * Stores the actual elements.
//         */
//        private final int[] storage;
//
//        /**
//         * Points to the element that will be dequeued next.
//         */
//        private int head;
//
//        /**
//         * Points to the array component to which the next element will be 
//         * inserted.
//         */
//        private int tail;
//
//        /**
//         * Caches the amount of elements stored.
//         */
//        private int size;
//
//        /**
//         * Used for faster head/tail updates.
//         */
//        private final int mask;
//
//        /**
//         * Creates an empty integer queue with capacity of the least power of
//         * two no less than original capacity value.
//         */
//        UnsafeIntQueue(int capacity) {
//            capacity = fixCapacity(capacity);
//            this.mask = capacity - 1;
//            this.storage = new int[capacity];
//        }
//
//        /**
//         * Appends an integer to the tail of this queue.
//         * 
//         * @param num the integer to append.
//         */
//        void enqueue(int num) {
//            storage[tail & mask] = num;
//            tail = (tail + 1) & mask;
//            ++size;
//        }
//
//        /**
//         * Pops from the head of this queue an integer.
//         * 
//         * @return the integer at the head of this queue.
//         */
//        int dequeue() {
//            int ret = storage[head];
//            head = (head + 1) & mask;
//            --size;
//            return ret;
//        }
//
//        /**
//         * Returns the amount of values stored in this queue.
//         */
//        int size() {
//            return size;
//        }
//
//        /**
//         * This routine is responsible for computing an integer that is a power
//         * of two no less than {@code capacity}.
//         */
//        private static int fixCapacity(int capacity) {
//            capacity = Math.max(capacity, MINIMUM_CAPACITY);
//            int ret = 1;
//
//            while (ret < capacity) {
//                ret <<= 1;
//            }
//
//            return ret;
//        }
//    }
    
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
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException(
                String.format("fromIndex(%d) < 0", fromIndex));
        }
        
        if (toIndex > length) {
            throw new IndexOutOfBoundsException(
                String.format("toIndex(%d) > length(%d)", toIndex, length));
        }
        
        if (fromIndex > toIndex) {
            throw new IndexOutOfBoundsException(
                String.format(
                    "fromIndex(%d) > toIndex(%d)", fromIndex, toIndex));
        }
    }
}
