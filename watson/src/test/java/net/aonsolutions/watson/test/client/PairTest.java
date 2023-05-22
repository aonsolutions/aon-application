package net.aonsolutions.watson.test.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import net.aonsolutions.watson.client.Pair;


class PairTest {
	
	@Test()
	void instanceTest() {
		Integer left = Integer.valueOf(1);
		String right = "a";
		Pair<Integer,String> pair = new Pair<>( left, right);
		assertNotNull(pair);
		assertNotNull(pair.getLeft());
		assertNotNull(pair.getRight());
		
		pair = Pair.of( left, right );
		assertNotNull(pair);
		assertNotNull(pair.getLeft());
		assertNotNull(pair.getRight());
		
		assertEquals(pair.getLeft(),pair.getKey());
		assertEquals(pair.getRight(),pair.getValue());
		
		Integer left2 = Integer.valueOf(2);
		String right2 = "b";
		pair.setLeft(left2);
		assertEquals(left2, pair.getLeft());
		assertEquals(pair.getLeft(),pair.getKey());
		
		pair.setRight(right2);
		assertEquals(right2, pair.getRight());
		assertEquals(pair.getRight(),pair.getValue());
		
		
	}
}
