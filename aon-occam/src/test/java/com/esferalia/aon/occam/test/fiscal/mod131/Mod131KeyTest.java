package com.esferalia.aon.occam.test.fiscal.mod131;

import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.occam.test.AbstractOccamTest;

public class Mod131KeyTest extends AbstractOccamTest {
	
	@Test
	public void testValues() {
		Set<String> values = new HashSet<String>();
		for (Mod131Key key : Mod131Key.values()) {
			assertTrue( MessageFormat.format("La clave [{0}] tiene un valor duplicado: \"{1}\"", key.toString(),key.getValue()) ,values.add(key.getValue()));
		}
	}

}
