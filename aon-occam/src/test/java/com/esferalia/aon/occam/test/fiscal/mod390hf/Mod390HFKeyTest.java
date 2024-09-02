package com.esferalia.aon.occam.test.fiscal.mod390hf;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.occam.test.AbstractOccamTest;

class Mod390HFKeyTest extends AbstractOccamTest {
	
	@Test
	void testValues() {
		Set<String> values = new HashSet<String>();
		for (Mod390Key key : Mod390Key.values()) {
			assertTrue(values.add(key.getValue())
				,MessageFormat.format("La clave [{0}] tiene un valor duplicado: \"{1}\"", key.toString(),key.getValue()));
		}
	}

}
