package com.esferalia.aon.watson.util;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

public class SmartSearchTest {
	
	@BeforeEach
	public void beforeEach(TestInfo testInfo) {
		boolean isRepeatedTest = AonStringUtils.contains(testInfo.getDisplayName(),"repetition");
		String className = testInfo.getTestClass().map(clazz -> clazz.getName()).orElse("?");
		String methodName = testInfo.getTestMethod().map(tm -> tm.getName()).map(mn -> mn + "()").orElse("?");
		String repetitionInfo = isRepeatedTest?testInfo.getDisplayName():""; 
		System.out.println( 
			String.format("Running [ %s.%s %s ]"
				,className
				,methodName
				,repetitionInfo 
		));				
	}
	
	private void matching(String text, String searcher) {
		assertTrue(AonStringUtils.containsMatching(text, searcher));
	}
	private void notMatching(String text, String searcher) {
		assertFalse(AonStringUtils.containsMatching(text, searcher));
	}
	
	
	@Test 
	public void generalLevenshteinDistanceTest() {
		matching("Las nóminas de importación", "La nóminas d imprtación");		
	}

	@Test
	public void accentVariationTest() {		
		matching("Importación de nóminas y trabajadores", "nomina");
	}
	
	@Test
	public void accentBigVariationFailTest() {
		notMatching("Importación de nóminas y trabajadores", "nominaaa");
	}
	
	@Test
	public void capitalVariationTest() {		
		matching("IMPORTACIÓN DE NÓMINAS Y TRABAJADORES", "nomina");
	}
	
	@Test 
	public void containingStringTest() {
		matching("IMPORTACIÓN DE NÓMINAS Y TRABAJADORES", "nom");
	}
	
	@Test
	public void containsFractionTest() {
		matching("IMPORTACIÓN", "imp");		
	}
	
	@Test
	public void nWithTildeTest() {		
		matching("NIÑOS EN EL PARQUE", "ninos");		
	}
	
	@Test 
	public void noSenseSearch() {		
		notMatching("I M P O R T E inqorle iljorte", "importe");		
	}
	
	@Test
	public void spacesMatchingTest() {
		matching("THE LEGEND         OF          ZELDA  ", "  ZELDA      ");
	}
	
	
	@Test
	public void getMatchingWordTest() {
		final String searcher = "nom";
		final String text = "IMPORTACIÓN DE Nóminas Y TRABAJADORES";

		assertEquals("Nóminas",AonStringUtils.getMatchingWord(text, searcher));	
	}
	
	@Test
	public void getMatchingTest() {
		final String searcher = "nom trabajidores";
		final String text = "IMPORTACIÓN DE Nóminas Y TRABAJADORES";
		
		List<String> matching = AonStringUtils.getMatching(text, searcher);
		if(matching.size() != 2) {
			fail("Incorrect matching word number");
		}
		assertEquals("Nóminas", matching.get(0));
		assertEquals("TRABAJADORES", matching.get(1));
	}
	
	@Test
	public void getMatchingMultipleSpacingTest() {
		final String searcher = "2";
		final String text = "IMPORTACIÓN   DE Nóminas  TRABAJADORES";		
		List<String> matching = AonStringUtils.getMatching(text, searcher);
		assertEquals(0, matching.size());
	}	

	@Test
	public void searchFilterTest() {
		
		LinkedList<String> results = new LinkedList<>();
		results.add("Modelo 303 - I.V.A. Autoliquidación");
		results.add("Modelo 347 - Resumen anual de operaciones");
		results.add("Modelo 349 - Resumen de operaciones intracomunitarias");
		results.add("Modelo 390 - Declaración Resumen Anual IVA");
		results.add("Modelo 111 - Retenciones e ingresos a cuenta sobre rendimientos de trabajo");
		results.add("Modelo 115 - Retenciones e ingresos a cuenta sobre determinadas rentas");
		results.add("Modelo 123 - Retenciones e ingresos a cuenta sobre determinados rendimientos");
		
		final String searcher = "trabaji Retencions";
		LinkedList<String> filteredResults = results
			.stream()
			.filter(p -> AonStringUtils.containsMatching(p, searcher))
			.collect(Collectors.toCollection(LinkedList::new));
		assertEquals(results.get(4),filteredResults.get(0));
			
	}
	

}
