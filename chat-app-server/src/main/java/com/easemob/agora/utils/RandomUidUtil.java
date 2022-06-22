package com.easemob.agora.utils;

import org.apache.commons.lang.math.RandomUtils;

public class RandomUidUtil {

    public static String getUid() {
        int randomUid = RandomUtils.nextInt(Integer.MAX_VALUE);
        if (randomUid == 0) {
            return String.valueOf(randomUid + 1);
        }

        return String.valueOf(randomUid);
    }
}
