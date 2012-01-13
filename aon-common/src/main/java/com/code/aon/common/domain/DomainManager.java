package com.code.aon.common.domain;


public class DomainManager {

	private static ThreadLocal<IDomainProvider> domainProvider;
	
	public static IDomainProvider getDomainProvider() {
		if (DomainManager.domainProvider == null) {
			initialize();
		}
		return DomainManager.domainProvider.get();
	}

	private static void initialize() {
		DomainManager.domainProvider = new ThreadLocal<IDomainProvider>() {
			//
			// Se crea una implementación de la interfaz para poder
			// devolver un "1" en el caso de que no se haga una inicialización
			//
			// ATENCION!
			// 		¡¡Esta opción sólo debería ser válida en en la fase
			// 		multidominio monoempresa!!
			//
			@Override
			protected IDomainProvider initialValue() {
				return new IDomainProvider() {
					
					@Override
					public Integer getCurrentDomain() {
						return new Integer(1);
					}
				};
			}
		};
	}

	public static void setDomainProvider(IDomainProvider domainProvider) {
		if (DomainManager.domainProvider == null) {
			initialize();	
		}
		DomainManager.domainProvider.set(domainProvider);
	}

	public static Integer getCurrentDomain() {
		if (getDomainProvider() == null) {
			throw new IllegalStateException("No se ha definido un proveedor de Domain.");
		}
		if (getDomainProvider().getCurrentDomain() == null) {
			throw new IllegalStateException("El proveedor de Domain, no tiene un Domain activo.");
		}
		return getDomainProvider().getCurrentDomain();
	}

	
}