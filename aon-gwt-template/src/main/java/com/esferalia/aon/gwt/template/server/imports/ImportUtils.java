package com.esferalia.aon.gwt.template.server.imports;

import java.text.Collator;
import java.util.Locale;

public class ImportUtils {
	
	public boolean compare(String value1, String value2) {
		if(value1 == null || value2 == null) return false;
		Collator c = Collator.getInstance(new Locale("es"));
		c.setStrength(Collator.PRIMARY);
		return c.equals(value1, value2);
	}
	
	public boolean compare(String value, String... values) {
		if(value == null || values == null || values.length == 0) return false;
		Collator c = Collator.getInstance(new Locale("es"));
		c.setStrength(Collator.PRIMARY);
		for (String  value2 : values) {
			if(value2 != null && c.equals(value, value2))
				return true;
		}
		return false;
	}
}
