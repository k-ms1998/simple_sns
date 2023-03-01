package com.project.sns.util;

import java.util.Optional;

public class ClassUtils {

    public static <T>  T getSafeCastInstance(Object o, Class<T> clazz){
        return clazz != null && clazz.isInstance(o) ? clazz.cast(o) : null;
    }
}
