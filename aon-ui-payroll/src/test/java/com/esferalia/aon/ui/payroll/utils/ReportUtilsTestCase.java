package com.esferalia.aon.ui.payroll.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;

import org.junit.jupiter.api.Test;

public class ReportUtilsTestCase {

	@Test
	public void testSpelloutLocaleDoubleInt() {
		Locale locale = new Locale("ES");
		
		String spellout = ReportUtils.spellout(locale, 1000.559 ,2);
		assertEquals("mil con cincuenta y seis", spellout);

		spellout = ReportUtils.spellout(locale, 1000.556 ,2);
		assertEquals("mil con cincuenta y seis", spellout);

		spellout = ReportUtils.spellout(locale, 1000.554 ,2);
		assertEquals("mil con cincuenta y cinco", spellout);

		spellout = ReportUtils.spellout(locale, 1000.555 ,2);
		assertEquals("mil con cincuenta y cinco", spellout);

		spellout = ReportUtils.spellout(locale, 1000.550 ,2);
		assertEquals("mil con cincuenta y cinco", spellout);

		spellout = ReportUtils.spellout(locale, 1000.5551 ,2);
		assertEquals("mil con cincuenta y seis", spellout);
		
		spellout = ReportUtils.spellout(locale, 1000.5550 ,2);
		assertEquals("mil con cincuenta y cinco", spellout);

		spellout = ReportUtils.spellout(locale, 1000.55 ,2);
		assertEquals("mil con cincuenta y cinco", spellout);
	}

}
