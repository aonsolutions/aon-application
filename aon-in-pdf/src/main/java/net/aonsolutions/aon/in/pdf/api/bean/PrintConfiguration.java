package net.aonsolutions.aon.in.pdf.api.bean;

import java.util.Locale;
import java.util.Optional;

/**
 * Abstract class from print configuration data
 * Also ensures having default locale value (Spain)
 * 
 * @author akrck02
 *
 */
public abstract class PrintConfiguration {

	private Optional<Locale> language;

	public PrintConfiguration(Locale language) {
		this.language = Optional.ofNullable(language);
	}

	public Locale getLanguage() {
		return language.orElse(new Locale("Es"));
	}

	public void setLanguage(Locale language) {
		this.language = Optional.ofNullable(language);
	}
}
