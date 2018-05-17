package net.aonsolutions.core.tgss.creta.jaxb;

public interface DatoSolicitado extends Dato {
	
	default String getIndicadorObligatoriedad() {
		return "B";
	}

}
