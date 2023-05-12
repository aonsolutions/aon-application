package net.aonsolutions.watson.test.client.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;

import net.aonsolutions.watson.client.util.AonCollectionUtils;


class AonCollectionUtilsTests {

    @Test
    void isCollectionEmpty(){
    	LinkedList<String> list = null; 
    	assertTrue(AonCollectionUtils.isEmpty(list));
    	list = new LinkedList<>();
    	assertTrue(AonCollectionUtils.isEmpty(list));
    	list.add("Something");
    	assertFalse(AonCollectionUtils.isEmpty(list));
	}
    @Test
    void isCollectionNotEmpty() {
    	LinkedList<String> list = null; 
    	assertFalse(AonCollectionUtils.isNotEmpty(list));
    	list = new LinkedList<>();
    	assertFalse(AonCollectionUtils.isNotEmpty(list));
    	list.add("Something");
    	assertTrue(AonCollectionUtils.isNotEmpty(list));
    }
    
    @Test
    void isMapEmpty(){
    	HashMap<String,String> map = null; 
    	assertTrue(AonCollectionUtils.isEmpty(map));
    	map = new HashMap<>();
    	assertTrue(AonCollectionUtils.isEmpty(map));
    	map.put("key","Something");
    	assertFalse(AonCollectionUtils.isEmpty(map));
	}
    
    @Test
    void isMapNotEmpty(){
    	HashMap<String,String> map = null; 
    	assertFalse(AonCollectionUtils.isNotEmpty(map));
    	map = new HashMap<>();
    	assertFalse(AonCollectionUtils.isNotEmpty(map));
    	map.put("key","Something");
    	assertTrue(AonCollectionUtils.isNotEmpty(map));
	}
}
