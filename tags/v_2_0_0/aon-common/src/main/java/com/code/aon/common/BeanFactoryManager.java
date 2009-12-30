package com.code.aon.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Factories manager.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 24-nov-2005
 * @since 1.0
 * @deprecated
 */
public class BeanFactoryManager {

    private static Map<String,String> factories = new HashMap<String,String>();

    /**
     * @param name
     * @param factory
     */
    public static void register(String name, String factory) {
        if ( !factories.containsKey(name) ) {
            factories.put(name, factory);
        }
    }

    /**
     * @param name
     * @return String
     */
    public static String getManagerBeanFactory(String name) {
        return factories.get(name);
    }

}
