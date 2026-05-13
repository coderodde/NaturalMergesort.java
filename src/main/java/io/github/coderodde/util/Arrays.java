package io.github.coderodde.util;

import io.github.coderodde.util.Arrays.Powersort.RunStack.RunStackEntry;
import java.util.Comparator;
import java.util.Objects;

/**
 * This class contains some different natural mergesort variants. All are run-
 * adaptive, stable and run in the worst-case running time of 
 * {@code O(n log n)}.
 */
public final class Arrays {
    
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
                buffer = java.util.Arrays.copyOfRange(array, 
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
            
            void extendTailBy(final int runLength) {
                storage[(tailIndex - 1) & mask] += runLength;
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
                int indexLeft                    = fromIndex;
                int indexRight                   = indexLeft + 1;
                final int upperBoundLeftIndex    = toIndex - 1;
                boolean previousRunWasDescending = false;
                
                while (indexLeft < upperBoundLeftIndex) {
                    final int indexHead = indexLeft;

                    // Decide the direction of the next run:
                    if (cmp.compare(array[indexLeft], array[indexRight]) <= 0) {
                        // Scan an ascending run:
                        while (indexLeft < upperBoundLeftIndex && 
                               cmp.compare(array[indexLeft], 
                                           array[indexRight]) <= 0) {

                            ++indexLeft;
                            ++indexRight;
                        }
                        
                        final int runLength = indexRight - indexHead;
                        
                        if (previousRunWasDescending && 
                            cmp.compare(array[indexHead - 1], 
                                        array[indexHead]) <= 0) {
                            
                            extendTailBy(runLength);
                        } else {
                            enqueue(runLength);
                        }
                        
                        previousRunWasDescending = false;
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
                        
                        final int runLength = indexRight - indexHead;
                    
                        if (previousRunWasDescending &&
                            cmp.compare(array[indexHead - 1], 
                                        array[indexHead]) <= 0) {
                            
                            extendTailBy(runLength);
                        } else {
                            enqueue(runLength);
                        }
                        
                        previousRunWasDescending = true;
                    }

                    // Move the indices to the next pair:
                    ++indexLeft;
                    ++indexRight;
                }

                if (indexLeft == upperBoundLeftIndex) {
                    if (cmp.compare(array[indexLeft - 1], array[indexLeft]) <= 0) {
                        extendTailBy(1);
                    } else {
                        enqueue(1);
                    }
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
         * Returns the amount of merge passes over the input range needed to sort 
         * {@code runAmount} runs.
         */
        private static int computeNumberOfMergePasses(final int runAmount) {
            return 32 - Integer.numberOfLeadingZeros(runAmount / 2);
        }
    }

    public static final class Powersort {
        
        public static <T> void sort(final T[] array,
                                    final Comparator<? super T> cmp) {
            Objects.requireNonNull(array, "The input array is null.");
            sort(array, 0, array.length, cmp);
        }
        
        public static <T> void sort(final T[] array,
                                    final int fromIndex,
                                    final int toIndex,
                                    final Comparator<? super T> cmp) {
            Objects.requireNonNull(array, "The input array is null.");
            checkIndices(fromIndex, toIndex, array.length);
            Objects.requireNonNull(cmp, "The comparator is null.");
            
            final int rangeLength = toIndex - fromIndex;
            
            if (rangeLength < 2) {
                return;
            }
            
            final T[] buffer = (T[])  new Object[rangeLength / 2];
            final RunStack runStack = new RunStack(rangeLength);
            
            int b1 = fromIndex;
            int e1 = firstRunOf(array, 
                                b1,
                                toIndex,
                                cmp);
            
            while (e1 < toIndex) {
                int b2 = e1;
                int e2 = firstRunOf(array,
                                    b2,
                                    toIndex,
                                    cmp);
                
                final int power = nodePower(rangeLength, 
                                            b1 - fromIndex, 
                                            e1 - fromIndex,
                                            b2 - fromIndex, 
                                            e2 - fromIndex);
                
                while (!runStack.isEmpty() && runStack.top().runPower > power) {
                    final RunStackEntry entry = runStack.pop();
                    final int offset = entry.runOffset;
                    final int runLengthLeft  = entry.runLength;
                    final int runLengthRight = e1 - b1;

                    merge(array, 
                          buffer, 
                          offset,
                          runLengthLeft, 
                          runLengthRight,
                          cmp);

                    b1 = offset;
                    e1 = offset + runLengthLeft + runLengthRight;
                }
                
                runStack.push(b1, e1 - b1, power);
                b1 = b2;
                e1 = e2;
            }
            
            while (!runStack.isEmpty()) {
                final RunStackEntry entry = runStack.pop();
                final int offset         = entry.runOffset;
                final int runLengthLeft  = entry.runLength;
                final int runLengthRight = e1 - b1;
                
                merge(array, 
                      buffer, 
                      offset,
                      runLengthLeft, 
                      runLengthRight,
                      cmp);
                
                b1 = offset;
                e1 = offset + runLengthLeft + runLengthRight;
            }
        }
        
        private Powersort() {
            
        }
        
        private static <T> void merge(final T[] array,
                                      final T[] buffer,
                                      final int offset,
                                      final int runLengthLeft,
                                      final int runLengthRight,
                                      final Comparator<? super T> cmp) {
            if (runLengthLeft <= runLengthRight) {
                // Once here, the left run is copied to the buffer:
                System.arraycopy(array,
                                 offset, 
                                 buffer,
                                 0, 
                                 runLengthLeft);
                
                int indexLeft  = 0;
                int indexRight = offset + runLengthLeft;
                int indexArray = offset;
                
                final int indexBoundLeft  = runLengthLeft;
                final int indexBoundRight = offset 
                                          + runLengthLeft
                                          + runLengthRight;
                
                while (indexLeft  != indexBoundLeft &&
                       indexRight != indexBoundRight) {
                    
                    array[indexArray++] = 
                            cmp.compare(buffer[indexLeft],
                                        array[indexRight]) <= 0 ?
                            buffer[indexLeft++] :
                            array[indexRight++];
                }
                
                System.arraycopy(buffer,
                                 indexLeft, 
                                 array, 
                                 indexArray, 
                                 indexBoundLeft - indexLeft);
            } else {
                // Once here, the right run is copied to the buffer:
                System.arraycopy(array,
                                 offset + runLengthLeft, 
                                 buffer,
                                 0, 
                                 runLengthRight);
                
                int indexLeft  = offset + runLengthLeft - 1;
                int indexRight = runLengthRight - 1;
                int indexArray = offset + runLengthLeft + runLengthRight - 1;
                
                final int indexBoundLeft  = offset;
                
                while (indexLeft  >= indexBoundLeft && indexRight >= 0) {
                    
                    array[indexArray--] = 
                            cmp.compare(array[indexLeft], 
                                        buffer[indexRight]) > 0 ?
                            array[indexLeft--] :
                            buffer[indexRight--];
                } 
                
                System.arraycopy(buffer, 
                                 0, 
                                 array, 
                                 offset, 
                                 indexRight + 1);
            }
        }
        
        static final class RunStack {
            
            final static class RunStackEntry {
                int runOffset;
                int runLength;
                int runPower;
                
                RunStackEntry(final int runOffset,
                              final int runLength,
                              final int runPower) {
                    this.runOffset = runOffset;
                    this.runLength = runLength;
                    this.runPower  = runPower;
                }
            }
            
            private final RunStackEntry[] stackData;
            private int size;
            
            RunStack(final int n) {
                final int stackDataCapacity = 1 
                                            + (int) Math.ceil(Math.log(n) / 
                                                              Math.log(2.0));
                
                this.stackData = new RunStackEntry[stackDataCapacity];
            }
            
            boolean isEmpty() {
                return size == 0;
            }
            
            void push(final int runOffset,
                      final int runLength,
                      final int runPower) {
                stackData[size++] = new RunStackEntry(runOffset,
                                                      runLength,
                                                      runPower);
            }
            
            RunStackEntry pop() {
                return stackData[--size];
            }
            
            RunStackEntry top() {
                return stackData[size - 1];
            }
        }
        
        private static int nodePower(final int n, 
                                     final int b1,
                                     final int e1,
                                     final int b2,
                                     final int e2) {
            final long l2 = (long) b1 + b2;
            final long r2 = (long) b2 + e2;
            
            final int a = (int) ((l2 << 30) / n);
            final int b = (int) ((r2 << 30) / n);
            
            return Integer.numberOfLeadingZeros(a ^ b);
        }
        
        private static <T> int firstRunOf(final T[] array,
                                          final int fromIndex,
                                          final int toIndex,
                                          final Comparator<? super T> cmp) {
            
            int indexLeft                    = fromIndex;
            int indexRight                   = indexLeft + 1;
            final int upperBoundIndexLeft    = toIndex - 1;
            boolean previousRunWasDescending = false;
            
            while (indexLeft < upperBoundIndexLeft) {
                final int indexHead = indexLeft;
                
                if (cmp.compare(array[indexLeft], array[indexRight]) <= 0) {
                    while (indexLeft < upperBoundIndexLeft && 
                           cmp.compare(array[indexLeft], 
                                       array[indexRight]) <= 0) {
                        
                        ++indexLeft;
                        ++indexRight;
                    }
                    
                    
                    if (previousRunWasDescending &&
                        cmp.compare(array[indexHead - 1],
                                    array[indexHead]) <= 0) {
                        
                        final int runLength = indexRight - indexHead;
                        indexRight += runLength;
                    } else {
                        return indexRight;
                    }
                    
                    previousRunWasDescending = false;
                } else {
                    while (indexLeft < upperBoundIndexLeft &&
                           cmp.compare(array[indexLeft],
                                       array[indexRight]) > 0) {
                        
                        ++indexLeft;
                        ++indexRight;
                    }
                    
                    reverseRun(array, 
                               fromIndex,
                               indexRight);
                    
                    
                    if (previousRunWasDescending &&
                        cmp.compare(array[indexHead - 1],
                                    array[indexHead]) <= 0) {
                        
                        final int runLength = indexRight - indexHead;
                        indexRight += runLength;
                    } else {
                        return indexRight;
                    }
                    
                    previousRunWasDescending = true;
                }
                
                ++indexLeft;
                ++indexRight;
            }
            
            return toIndex;
        }
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
}