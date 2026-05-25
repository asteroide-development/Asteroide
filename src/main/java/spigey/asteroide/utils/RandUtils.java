package spigey.asteroide.utils;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

public class RandUtils {
    static Random random = new Random();

    public static int withOffset(int base, int minOffset, int maxOffset){ return base + inRange(minOffset, maxOffset); }
    public static int inRange(int min, int max){ return random.nextInt(max - min + 1) + min; }
    public static int inRange(int max){ return random.nextInt(max); }
    public static Object inArray(Object[] array){ return array[inRange(array.length)]; }
    public static Object inList(List<?> list){ return list.get(inRange(list.size())); }
    public static String string(int length){
        StringBuilder sb = new StringBuilder(length);
        for(int i = 0; i < length; i++) sb.append((char) inRange(32, 126));
        return sb.toString();
    }
}
