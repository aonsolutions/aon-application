package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;

public enum AonLanguage implements Serializable{
	
	BASQUE("eu"),
	CATALAN("ca"),
	VALENCIAN("va"),
	DEUTSCH("de"),
	ENGLISH("en"),
	GALICIAN("gl"),
	SPANISH("es");
	
	String language;
	
	private AonLanguage(String language) {
		this.language = language;
	}
}
