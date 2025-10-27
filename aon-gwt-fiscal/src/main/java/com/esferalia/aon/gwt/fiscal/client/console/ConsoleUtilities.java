package com.esferalia.aon.gwt.fiscal.client.console;

import java.io.Serializable;

public enum ConsoleUtilities implements Serializable {
	
	LIST_DOMAINS ("Listado de dominios") {
		@Override 
		public <T> T visit(ConsoleUtilitiesVisitor<T> visitor) {
			return visitor.visitListDomains();
		}
	}
	
	,SCOPE_INTEGRITY_FIX ("Arreglo integridad de SCOPES") {
		@Override 
		public <T> T visit(ConsoleUtilitiesVisitor<T> visitor) { 
			return visitor.visitScopeIntegrityFix();
		}
	}
	
	,NORDIGEN_FIX ("Arreglo NORDIGEN") {
		@Override 
		public <T> T visit(ConsoleUtilitiesVisitor<T> visitor){
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

	public abstract <T> T visit(ConsoleUtilitiesVisitor<T> visitor);

	public interface ConsoleUtilitiesVisitor<T> {
		public T visitListDomains();
		public T visitScopeIntegrityFix();
		public T visitNordigenFix();
		
	}

}