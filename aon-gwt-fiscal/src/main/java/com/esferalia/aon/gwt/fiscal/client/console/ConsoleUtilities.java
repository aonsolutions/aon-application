package com.esferalia.aon.gwt.fiscal.client.console;

import java.io.IOException;
import java.io.Serializable;

public enum ConsoleUtilities implements Serializable {
	
	LIST_DOMAINS ("Listado de dominios") {
		@Override 
		public <T> T visit(ConsoleUtilitiesVisitor<T> visitor) throws IOException {
			return visitor.visitListDomains();
		}
	}
	
	,NORDIGEN_FIX ("Arreglo NORDIGEN") {
		@Override 
		public <T> T visit(ConsoleUtilitiesVisitor<T> visitor) throws IOException {
			return visitor.visitNordigenFix();
		}
	}
	
	;
	private String description;
	private ConsoleUtilities(String description) {
			this.description = description;
	}
	
	public String getDescription() {
		return this.description;
	}

	public abstract <T> T visit(ConsoleUtilitiesVisitor<T> visitor) throws IOException;

	public interface ConsoleUtilitiesVisitor<T> {
		public T visitListDomains() throws IOException;
		public T visitNordigenFix() throws IOException;
		
	}

}