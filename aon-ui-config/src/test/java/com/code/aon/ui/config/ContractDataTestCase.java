package com.code.aon.ui.config;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.util.AonStringUtils;

public class ContractDataTestCase {

	@Test
	public void testEmptySearchAndReplace() throws IOException {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(ContractDataTestCase.class.getResourceAsStream("names.txt"), "UTF-8")))
		{
			
			for ( String name = in.readLine(); name != null ; name = in.readLine() ) {
				
				if ( !AonStringUtils.containsMatching(name, "") ) {
					fail("Empty must matching all");
				}
				String replaced = DomainData.replace(name, "",  string -> "<b>" + string + "</b>");
				assertEquals(name, replaced);
			}
		}
	}

	@Test
	public void testMultipleSpaces() throws IOException {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(ContractDataTestCase.class.getResourceAsStream("names.txt"), "UTF-8")))
		{
			
			for ( String name = in.readLine(); name != null ; name = in.readLine() ) {
				for ( String match : AonStringUtils.getMatching(name, "�") ) {
					assertFalse(AonStringUtils.isBlank(match), "match: '"+ match+ "'");
				}
			}
		}
	}


	@Test
	public void testReplace() throws IOException {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(ContractDataTestCase.class.getResourceAsStream("names.txt"), "UTF-8")))
		{
			
			for ( String name = in.readLine(); name != null ; name = in.readLine() ) {
				for ( String match : AonStringUtils.getMatching(name, "a") ) {
					String replaced = DomainData.replace(name, match,  string -> "<b>" + string + "</b>");
					assertTrue(AonStringUtils.containsIgnoreCase(replaced, "<b>"+match+"</b>"), "match: '"+ replaced+ "'");
				}
			}
		}
	}
}
