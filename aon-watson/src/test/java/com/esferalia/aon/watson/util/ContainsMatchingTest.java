package com.esferalia.aon.watson.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class ContainsMatchingTest {

	@Test
	public void accentVariationTest() {
		
		final String searcher = "nomina";
		final String text = "Importación de nominas y trabajadores";
		
		assertTrue(AonStringUtils.containsMatching(text, searcher, 2));
	
	}
	
	@Test
	public void accentBigVariationFailTest() {
		
		final String searcher = "nominaaa";
		final String text = "Importación de nominas y trabajadores";
		
		assertFalse(AonStringUtils.containsMatching(text, searcher, 2));
	
	}
	
	
	@Test
	public void capitalVariationTest() {
		
		final String searcher = "nomina";
		final String text = "IMPORTACIÓN DE NÓMINAS Y TRABAJADORES";
		
		assertTrue(AonStringUtils.containsMatching(text, searcher, 2));
	}
	
	@Test 
	public void containingStringTest() {
		
		final String searcher = "nom";
		final String text = "IMPORTACIÓN DE NÓMINAS Y TRABAJADORES";
		
		assertTrue(AonStringUtils.containsMatching(text, searcher, 2));
		
		
	}
	
	
	

}
