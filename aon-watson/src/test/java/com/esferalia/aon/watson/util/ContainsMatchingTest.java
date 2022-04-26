package com.esferalia.aon.watson.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class ContainsMatchingTest {
	
	
	@Test 
	public void generalLevenshteinDistanceTest() {
		
		final String searcher = "La nóminas d imprtación";
		final String searcher2 = "La naminas de impertación";
		final String text = "Las nóminas de importación";
		
		AonStringUtils.containsMatching(text, searcher, 2);
		
	}

	@Test
	public void accentVariationTest() {
		
		final String searcher = "nomina";
		final String text = "Importación de nóminas y trabajadores";
		
		assertTrue(AonStringUtils.containsMatching(text, searcher, 2));
	
	}
	
	@Test
	public void accentBigVariationFailTest() {
		
		final String searcher = "nominaaa";
		final String text = "Importación de nóminas y trabajadores";
		
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
	
	@Test
	public void containsFractionTest() {
		
		final String searcher = "imp";
		final String text = "IMPORTACIÓN";
		
		assertTrue(AonStringUtils.containsMatching(text, searcher, 2));			
	}
	
	@Test
	public void nWithTildeTest() {

		final String searcher = "ninos";
		final String text = "NIÑOS EN EL PARQUE";
		
		assertTrue(AonStringUtils.containsMatching(text, searcher, 2));					
	
	}
	
	@Test 
	public void noSenseSearch() {
		
		final String searcher = "importe";
		final String text = "I M P O R T E inqorle iljorte";
		
		assertFalse(AonStringUtils.containsMatching(text, searcher, 2));			
		
	}
	
	@Test
	public void getMatchingWordTest() {
		final String searcher = "nom";
		final String text = "IMPORTACIÓN DE Nóminas Y TRABAJADORES";

		assertEquals("Nóminas",AonStringUtils.getMatchingWord(text, searcher));	
	}
	

	
	

}
