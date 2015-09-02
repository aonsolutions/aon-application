package com.esferalia.aon.gwt.payroll.shared;


public interface CretaService {

	static enum Parameter {
		CCC ,
		MES ,
		ANHO ,
		TIPO ,
		NAFS, 
		FILE, 
		COMMENTS,
		DEFAULTS, 
		AUTORIZADO, 
		SKIP_EXISTING,
		ACEPTAR_BASES_ANTERIORES
	}

	static enum File {
		BASES {
			
			@Override
			public String getFilename() {
				return "SLD-Fichero de Solicitud de Bases";
			}
			
			@Override
			public <T, L, E extends Throwable> void accept(Visitor<T, L, E> visitor, T t, L l) throws E{
				visitor.visitBases(t,l);
			}
		},
		BORRADOR {
			
			@Override
			public String getFilename() {
				return "SLD-Fichero de Solicitud de Borrador";
			}
			
			@Override
			public <T, L, E extends Throwable> void accept(Visitor<T, L, E> visitor, T t, L l) throws E{
				visitor.visitBorrador(t,l);
			}
		},
		CALCULOS {
			@Override
			public String getFilename() {
				return "SLD-Fichero de Solicitud de Calculos";
			}
			
			@Override
			public <T, L, E extends Throwable> void accept(Visitor<T, L, E> visitor, T t, L l) throws E{
				visitor.visitCalculos(t,l);
			}
		},
		CONFIRMACION {
			@Override
			public String getFilename() {
				return "SLD-Fichero de Solicitud de Confirmacion";
			}
			
			@Override
			public <T, L, E extends Throwable> void accept(Visitor<T, L, E> visitor, T t, L l) throws E{
				visitor.visitConfirmacion(t,l);
			}
		},
		TRABAJADORES_TRAMOS{
			@Override
			public String getFilename() {
				return "SLD-Fichero de Solicitud de Trabajadores y Tramos";
			}
			

			@Override
			public <T, L, E extends Throwable> void accept(Visitor<T, L, E> visitor, T t, L l) throws E{
				visitor.visitTrabajadoresTramos(t,l);
			}
		}
		;
		
		public static interface Visitor<T,L,E extends Throwable> {
			void visitBases(T t, L l) throws E;
			void visitBorrador(T t, L l) throws E;
			void visitCalculos(T t, L l) throws E;
			void visitConfirmacion(T t, L l) throws E;
			void visitTrabajadoresTramos(T t, L l) throws E;
		}
		
		abstract public String getFilename();
		
		abstract public <T, L, E extends Throwable> void  accept(Visitor<T, L, E> visitor, T t, L l)throws E;
		
	}
	
	
}
