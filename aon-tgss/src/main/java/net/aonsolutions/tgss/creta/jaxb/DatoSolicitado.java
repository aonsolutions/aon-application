package net.aonsolutions.tgss.creta.jaxb;

public interface DatoSolicitado extends Dato {
	
	default String getIndicadorObligatoriedad() {
		return "B";
	}

}
