package spigey.asteroide.utils;

import java.security.SecureRandom;
import java.util.List;

public class RandUtils {
    static SecureRandom random = new SecureRandom();

    public static int withOffset(int base, int minOffset, int maxOffset){ return base + inRange(minOffset, maxOffset); }
    public static int inRange(int min, int max){ return random.nextInt(max - min + 1) + min; }
    public static int inRange(int max){ return random.nextInt(max); }
    public static Object inArray(Object[] array){ return array[inRange(array.length)]; }
    public static Object inList(List<?> list){ return list.get(inRange(list.size())); }
}
