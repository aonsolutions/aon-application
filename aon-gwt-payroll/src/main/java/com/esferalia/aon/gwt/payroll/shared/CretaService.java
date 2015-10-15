package com.esferalia.aon.gwt.payroll.shared;

import com.esferalia.aon.gwt.common.shared.HasId;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.URL;

public interface CretaService {

	public static enum Parameter {
		CCC, MES, ANHO, TIPO, NAFS, FILE, COMMENTS, DEFAULTS, AUTORIZADO, SKIP_EXISTING, ACEPTAR_BASES_ANTERIORES
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
				return "SLD-Fichero de Solicitud de Confirmacion";
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
		};

		public static interface Visitor<T, L, E extends Throwable> {
			void visitBases(T t, L l) throws E;

			void visitRespuesta(T t, L l) throws E;

			void visitTrabajadoresTramos(T t, L l) throws E;

			void visitSolicitudBorrador(T t, L l) throws E;

			void visitSolicitudCalculos(T t, L l) throws E;

			void visitSolicitudConfirmacion(T t, L l) throws E;

			void visitSolicitudTrabajadoresTramos(T t, L l) throws E;
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

		// ----------------------------------- JSNI (Native JavaScript Methods)

		public final native String getFullBases() /*-{
			return this.full_bases;
		}-*/;

		public final native String getDiffBases() /*-{
			return this.diff_bases;
		}-*/;

		public final native JsEvent[] getErrors() /*-{
			return this.errors;
		}-*/;

		public final native JsEvent[] getWarnings() /*-{
			return this.warnings;
		}-*/;

	}

	public static class JsError extends JavaScriptObject {
		protected JsError() {
		}

		// ----------------------------------- JSNI (Native JavaScript Methods)

		public final native String getCode() /*-{
			return this.code;
		}-*/;

		public final native String getMessage() /*-{
			return this.msg;
		}-*/;

	}


	public static class JsFile extends JavaScriptObject implements HasId<String> {
		protected JsFile() {
		}

		public final String getXML() {
			String file = getFile();
			return URL.decodeQueryString(file);

		}

		// ------------------------------------------------------ HasId<String>

		@Override
		public final String getId() {
			return getCCC() + getFrom();
		}
		// ----------------------------------- JSNI (Native JavaScript Methods)

		public final native String getTo() /*-{
			return this.from;
		}-*/;

		public final native String getFrom() /*-{
			return this.from;
		}-*/;

		public final native String getCCC() /*-{
			return this.ccc;
		}-*/;

		public final native String getFile() /*-{
			return this.file;
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

	}

	public static final String CRETA_URL = URL
			.encode(GWT.getModuleBaseURL() + "sdl");

}
