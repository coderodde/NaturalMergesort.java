package io.github.coderodde.util;

import java.util.Comparator;
import java.util.Objects;

/**
 * This class is dedicated to various utility methods.
 */
public final class Utils {

    private Utils() {
        
    }
    
    public static <T> boolean isSorted(final T[] array, 
                                       final int fromIndex,
                                       final int toIndex,
                                       final Comparator<? super T> cmp) {
        
        for (int i = fromIndex; i < toIndex - 1; ++i) {
            final T left  = array[i];
            final T right = array[i + 1];
            
            if (cmp.compare(left, right) > 0) {
                return false;
            }
        }
        
        return true;
    }
    
    public static <T> boolean isSorted(final T[] array,
                                       final Comparator<? super T> cmp) {
        return isSorted(array, 0, array.length, cmp);
    }
    
    public static <T> boolean equals(final T[] array1,
                                     final T[] array2,
                                     final int fromIndex,
                                     final int toIndex) {
        if (array1.length != array2.length) {
            return false;
        }
        
        for (int i = fromIndex; i < toIndex; ++i) {
            if (!Objects.equals(array1[i], array2[i])) {
                return false;
            }
        }
    
        return true;
    }
    
    public static <T> boolean equals(final T[] array1,
                                     final T[] array2) {
        return equals(array1, array2, 0, array1.length);
    }
}
