package com.esferalia.aon.gwt.payroll.shared;

import com.esferalia.aon.gwt.common.shared.HasId;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.URL;

public interface CretaService {

	public static enum Parameter {
		CCC, 
		MES, 
		ANHO, 
		TIPO, 
		NAFS, 
		FILE, 
		COMMENTS, 
		DEFAULTS, 
		AUTORIZADO, 
		SKIP_EXISTING, 
		ACEPTAR_BASES_ANTERIORES, 
		TIPO_MOVIMIENTO, 
		TIPO_ACCION, 
		IBAN, 
		TITULAR, 
		DOCUMENTO, 
		TIPO_DOCUMENTO
	}

	public static enum File {

		BASES {

			@Override
			public String getFilename() {
				return "SLD-Fichero de Bases";
			}

			@Override
			public <T, L, E extends Throwable> void accept(
					Visitor<T, L, E> visitor, T t, L l) throws E {
				visitor.visitBases(t, l);
			}
		},
		RESPUESTA {
			@Override
			public String getFilename() {
				return "SLD-Fichero de Respuesta";
			}

			@Override
			public <T, L, E extends Throwable> void accept(
					Visitor<T, L, E> visitor, T t, L l) throws E {
				visitor.visitRespuesta(t, l);
			}
		},
		TRABAJADORES_TRAMOS {
			@Override
			public String getFilename() {
				return "SLD-Fichero de Trabajadores y Tramos";
			}

			@Override
			public <T, L, E extends Throwable> void accept(
					Visitor<T, L, E> visitor, T t, L l) throws E {
				visitor.visitTrabajadoresTramos(t, l);
			}
		},
		SOLICITUD_BORRADOR {

			@Override
			public String getFilename() {
				return "SLD-Fichero de Solicitud de Borrador";
			}

			@Override
			public <T, L, E extends Throwable> void accept(
					Visitor<T, L, E> visitor, T t, L l) throws E {
				visitor.visitSolicitudBorrador(t, l);
			}
		},
		SOLICITUD_CALCULOS {
			@Override
			public String getFilename() {
				return "SLD-Fichero de Solicitud de Calculos";
			}

			@Override
			public <T, L, E extends Throwable> void accept(
					Visitor<T, L, E> visitor, T t, L l) throws E {
				visitor.visitSolicitudCalculos(t, l);
			}

		},
		SOLICITUD_CONFIRMACION {
			@Override
			public String getFilename() {
				return "SLD-Fichero de Solicitud de Confirmaci\u00FAn";
			}

			@Override
			public <T, L, E extends Throwable> void accept(
					Visitor<T, L, E> visitor, T t, L l) throws E {
				visitor.visitSolicitudConfirmacion(t, l);
			}
		},
		SOLICITUD_TRABAJADORES_TRAMOS {
			@Override
			public String getFilename() {
				return "SLD-Fichero de Solicitud de Trabajadores y Tramos";
			}

			@Override
			public <T, L, E extends Throwable> void accept(
					Visitor<T, L, E> visitor, T t, L l) throws E {
				visitor.visitSolicitudTrabajadoresTramos(t, l);
			}
		},
		COMUNICACION_DATOS_BANCARIOS{
			@Override
			public String getFilename() {
				return "SLD-Fichero de Comunicaci\u00F3n de Datos Bancarios";
			}
			
			@Override
			public <T, L, E extends Throwable> void accept(
					Visitor<T, L, E> visitor, T t, L l) throws E {
				visitor.visitComunicacionDatosBancarios(t, l);
				
			}
		},
		DOCUMENTO_CALCULO_LIQUIDACION{
			@Override
			public String getFilename() {
				return "SLD-Documento C\u00E1lculo Liquidaci\u00F3n";
			}
			
			@Override
			public <T, L, E extends Throwable> void accept(
					Visitor<T, L, E> visitor, T t, L l) throws E {
				visitor.visitDocumentoCalculoLiquidacion(t, l);
				
			}
		}
		;

		public static interface Visitor<T, L, E extends Throwable> {
			void visitBases(T t, L l) throws E;

			void visitRespuesta(T t, L l) throws E;

			void visitTrabajadoresTramos(T t, L l) throws E;

			void visitSolicitudBorrador(T t, L l) throws E;

			void visitSolicitudCalculos(T t, L l) throws E;

			void visitSolicitudConfirmacion(T t, L l) throws E;

			void visitSolicitudTrabajadoresTramos(T t, L l) throws E;

			void visitComunicacionDatosBancarios(T t, L l) throws E;
			
			void visitDocumentoCalculoLiquidacion(T t, L l ) throws E; 
		}

		abstract public String getFilename();

		abstract public <T, L, E extends Throwable> void accept(
				Visitor<T, L, E> visitor, T t, L l) throws E;

	}

	// ------------------------------------------------------------------------

	public static class JsEvent extends JavaScriptObject {
		protected JsEvent() {
		}

		// ----------------------------------- JSNI (Native JavaScript Methods)

		public final native String getMessage() /*-{
			return this.message;
		}-*/;
	}

	public static class JsCCC extends JavaScriptObject {
		protected JsCCC() {
		}

		// ----------------------------------- JSNI (Native JavaScript Methods)

		public final native String getNumber() /*-{
			return this.number;
		}-*/;

		public final native String getRegime() /*-{
			return this.regime;
		}-*/;

		public final native String getProvince() /*-{
			return this.province;
		}-*/;

	}

	public static class JsUnknownDato extends JsEvent {
		protected JsUnknownDato() {
		}

		// ----------------------------------- JSNI (Native JavaScript Methods)

//		public final native JsCCC getCCC() /*-{
//			return this.ccc;
//		}-*/;

		public final native String getType() /*-{
			return this.type;
		}-*/;

		public final native String getCode() /*-{
			return this.code;
		}-*/;

		public final native boolean isMandatory() /*-{
			return this.mandatory;
		}-*/;
	}

	public static class JsBasesResult extends JavaScriptObject {

		protected JsBasesResult() {
		}

		public final String getBasesFile() {
			String bases = getFullBases();
			return URL.decodeQueryString(bases);

		}

		public final String getChangedBasesFile() {
			String bases = getDiffBases();
			return URL.decodeQueryString(bases);

		}

		public final String getDraftRequestFile() {
			String draft = getDraftRequest();
			return URL.decodeQueryString(draft);

		}

		// ----------------------------------- JSNI (Native JavaScript Methods)

		public final native String getFullBases() /*-{
			return this.full_bases;
		}-*/;

		public final native String getDiffBases() /*-{
			return this.diff_bases;
		}-*/;

		public final native String getDraftRequest() /*-{
			return this.draft_request;
		}-*/;

		public final native JsEvent[] getErrors() /*-{
			return this.errors;
		}-*/;

		public final native JsEvent[] getWarnings() /*-{
			return this.warnings;
		}-*/;

		public final native JsUnknownDato[] getUnknown() /*-{
			return this.unknown;
		}-*/;
	}

	public static class JsError extends JavaScriptObject {
		protected JsError() {
		}
		
		public final String getMessage() {
			String msg = getMsg();
			return URL.decodeQueryString(msg);
		}

		// ----------------------------------- JSNI (Native JavaScript Methods)

		public final native String getCode() /*-{
			return this.code;
		}-*/;

		public final native String getMsg() /*-{
			return this.msg;
		}-*/;

	}

	public static class JsEmployee extends JavaScriptObject {
		protected JsEmployee() {
		}

		// ----------------------------------- JSNI (Native JavaScript Methods)

		public final native String getNaf() /*-{
			return this.naf;
		}-*/;

	}

	public static class JsFile extends JavaScriptObject
			implements HasId<String> {
		protected JsFile() {
		}

		public final String getXML() {
			String file = getFile();
			return URL.decodeQueryString(file);

		}

		// ------------------------------------------------------ HasId<String>

		@Override
		public final String getId() {
			return getCCC() + getFrom() ;
		}
		// ----------------------------------- JSNI (Native JavaScript Methods)

		public final native String getTo() /*-{
			return this.from;
		}-*/;

		public final native String getFrom() /*-{
			return this.from;
		}-*/;

		public final native String getDate() /*-{
			return this.date;
		}-*/;

		public final native String getTime() /*-{
			return this.time;
		}-*/;

		public final native String getCCC() /*-{
			return this.ccc;
		}-*/;

		public final native String getFile() /*-{
			return this.file;
		}-*/;

		public final native String getName() /*-{
			return this.name ? this.name : 'TRABAJADORES_TRAMOS';
		}-*/;
		

	}

	public static class JsTrabajadoresYTramos extends JsFile {
		protected JsTrabajadoresYTramos() {
		}

	}

	public static class JsRespuesta extends JsFile {
		protected JsRespuesta() {
		}

		// ----------------------------------- JSNI (Native JavaScript Methods)

		public final native JsError[] getErrors() /*-{
			return this.errors;
		}-*/;

		public final native JsEmployee[] getEmployees() /*-{
			return this.employees;
		}-*/;
	}

	public static class JsDCLResult extends JsEvent {
		
		protected JsDCLResult() {
		}

		// ----------------------------------- JSNI (Native JavaScript Methods)

		public final native String getDescription() /*-{
			return this.description;
		}-*/;

		public final native Long getSLDBase() /*-{
			return this.sldBase;
		}-*/;
	
		public final native Long getSLDImporte() /*-{
			return this.sldImporte;
		}-*/;

		public final native Long getAONBase() /*-{
			return this.aonBase;
		}-*/;
		
		public final native Long getAONImporte() /*-{
			return this.aonImporte;
		}-*/;
		
	}
	
	public static final String CRETA_URL = URL
			.encode(GWT.getModuleBaseURL() + "sdl");

}
