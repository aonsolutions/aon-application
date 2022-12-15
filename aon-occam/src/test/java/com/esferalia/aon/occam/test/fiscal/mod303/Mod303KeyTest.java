package com.esferalia.aon.occam.test.fiscal.mod303;

import static org.junit.Assert.assertTrue;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.type.Mod303Key;

public class Mod303KeyTest extends Mod303AbstractTest {
	
	@Test
	public void testValues() {
		Set<String> values = new HashSet<String>();
		for (Mod303Key key : Mod303Key.values()) {
			assertTrue( MessageFormat.format("La clave [{0}] tiene un valor duplicado: \"{1}\"", key.toString(),key.getValue()) ,values.add(key.getValue()));
		}
	}

}
