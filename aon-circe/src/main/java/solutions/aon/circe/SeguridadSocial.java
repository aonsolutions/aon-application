package solutions.aon.circe;

import java.util.Date;

public class SeguridadSocial {
	private String tipoAutonomo;
	private String nombre;
	private String docIdentidad;
	private String nss;
	private boolean solicitudNumAfiliacionSS;
	private String cnae;
	private String nombreApellidosRepresentante;
	private String tipoDocIdentidadRepresentante;
	private String docIdentidadRepresentante;
	private String nssrepresentante;
	private String regimen;
	private String grupo;
	private String trl;
	private String subgrupo;
	private boolean integradoColegioProfesional;
	private String colegioProfesional;
	private boolean discapacidad;
	private String tipoDiscapacidad;
	private int gradoDiscapacidad;
	private Date fechaEfectoDiscapacidad;
	private boolean cotizacionMinima;
	private boolean cotizacionMaxima;
	private boolean cotizacionOtra;
	private float baseCotizacion;
	private float rendimientoNetoAnual;
	private String mutuaIT;

	public SeguridadSocial() {

	}

	public SeguridadSocial(SeguridadSocialBuilder seguridadSocialBuilder) {
		this.tipoAutonomo = seguridadSocialBuilder.tipoAutonomo;
		this.nombre = seguridadSocialBuilder.nombre;
		this.docIdentidad = seguridadSocialBuilder.docIdentidad;
		this.nss = seguridadSocialBuilder.nss;
		this.solicitudNumAfiliacionSS = seguridadSocialBuilder.solicitudNumAfiliacionSS;
		this.cnae = seguridadSocialBuilder.cnae;
		this.nombreApellidosRepresentante = seguridadSocialBuilder.nombreApellidosRepresentante;
		this.tipoDocIdentidadRepresentante = seguridadSocialBuilder.tipoDocIdentidadRepresentante;
		this.docIdentidadRepresentante = seguridadSocialBuilder.docIdentidadRepresentante;
		this.nssrepresentante = seguridadSocialBuilder.nssrepresentante;
		this.regimen = seguridadSocialBuilder.regimen;
		this.grupo = seguridadSocialBuilder.grupo;
		this.trl = seguridadSocialBuilder.trl;
		this.subgrupo = seguridadSocialBuilder.subgrupo;
		this.integradoColegioProfesional = seguridadSocialBuilder.integradoColegioProfesional;
		this.colegioProfesional = seguridadSocialBuilder.colegioProfesional;
		this.discapacidad = seguridadSocialBuilder.discapacidad;
		this.tipoDiscapacidad = seguridadSocialBuilder.tipoDiscapacidad;
		this.gradoDiscapacidad = seguridadSocialBuilder.gradoDiscapacidad;
		this.fechaEfectoDiscapacidad = seguridadSocialBuilder.fechaEfectoDiscapacidad;
		this.cotizacionMinima = seguridadSocialBuilder.cotizacionMinima;
		this.cotizacionMaxima = seguridadSocialBuilder.cotizacionMaxima;
		this.cotizacionOtra = seguridadSocialBuilder.cotizacionOtra;
		this.baseCotizacion = seguridadSocialBuilder.baseCotizacion;
		this.rendimientoNetoAnual = seguridadSocialBuilder.rendimientoNetoAnual;
		this.mutuaIT = seguridadSocialBuilder.mutuaIT;
	}

	public String getTipoAutonomo() {
		return tipoAutonomo;
	}

	public String getNombre() {
		return nombre;
	}

	public String getDocIdentidad() {
		return docIdentidad;
	}

	public String getNss() {
		return nss;
	}

	public boolean isSolicitudNumAfiliacionSS() {
		return solicitudNumAfiliacionSS;
	}

	public String getCnae() {
		return cnae;
	}

	public String getNombreApellidosRepresentante() {
		return nombreApellidosRepresentante;
	}

	
	public String getTipoDocIdentidadRepresentante() {
		return tipoDocIdentidadRepresentante;
	}

	public String getDocIdentidadRepresentante() {
		return docIdentidadRepresentante;
	}

	public String getNssrepresentante() {
		return nssrepresentante;
	}

	public String getRegimen() {
		return regimen;
	}

	public String getGrupo() {
		return grupo;
	}

	public String getTrl() {
		return trl;
	}

	public String getSubgrupo() {
		return subgrupo;
	}

	public boolean isIntegradoColegioProfesional() {
		return integradoColegioProfesional;
	}

	public String getColegioProfesional() {
		return colegioProfesional;
	}

	public boolean isDiscapacidad() {
		return discapacidad;
	}

	public String getTipoDiscapacidad() {
		return tipoDiscapacidad;
	}

	public int getGradoDiscapacidad() {
		return gradoDiscapacidad;
	}

	public Date getFechaEfectoDiscapacidad() {
		return fechaEfectoDiscapacidad;
	}

	public boolean isCotizacionMinima() {
		return cotizacionMinima;
	}

	public boolean isCotizacionMaxima() {
		return cotizacionMaxima;
	}

	public boolean isCotizacionOtra() {
		return cotizacionOtra;
	}

	public float getBaseCotizacion() {
		return baseCotizacion;
	}

	public float getRendimientoNetoAnual() {
		return rendimientoNetoAnual;
	}

	public String getMutuaIT() {
		return mutuaIT;
	}

	public static class SeguridadSocialBuilder {

		private String tipoAutonomo;
		private String nombre;
		private String docIdentidad;
		private String nss;
		private boolean solicitudNumAfiliacionSS;
		private String cnae;
		private String nombreApellidosRepresentante;
		private String tipoDocIdentidadRepresentante;
		private String docIdentidadRepresentante;
		private String nssrepresentante;
		private String regimen;
		private String grupo;
		private String trl;
		private String subgrupo;
		private boolean integradoColegioProfesional;
		private String colegioProfesional;
		private boolean discapacidad;
		private String tipoDiscapacidad;
		private int gradoDiscapacidad;
		private Date fechaEfectoDiscapacidad;
		private boolean cotizacionMinima;
		private boolean cotizacionMaxima;
		private boolean cotizacionOtra;
		private float baseCotizacion;
		private float rendimientoNetoAnual;
		private String mutuaIT;

		public SeguridadSocialBuilder tipoAutonomo(String tipoAutonomo) {
			this.tipoAutonomo = tipoAutonomo;
			return this;
		}

		public SeguridadSocialBuilder nombre(String nombre) {
			this.nombre = nombre;
			return this;
		}

		public SeguridadSocialBuilder docIdentidad(String docIdentidad) {
			this.docIdentidad = docIdentidad;
			return this;
		}

		public SeguridadSocialBuilder nss(String nss) {
			this.nss = nss;
			return this;
		}

		public SeguridadSocialBuilder solicitudNumAfiliacionSS(boolean solicitudNumAfiliacionSS) {
			this.solicitudNumAfiliacionSS = solicitudNumAfiliacionSS;
			return this;
		}

		public SeguridadSocialBuilder cnae(String cnae) {
			this.cnae = cnae;
			return this;
		}

		public SeguridadSocialBuilder nombreApellidosRepresentante(String nombreApellidosRepresentante) {
			this.nombreApellidosRepresentante = nombreApellidosRepresentante;
			return this;
		}
		
		public SeguridadSocialBuilder tipoDocIdentidadRepresentante(String tipoDocIdentidadRepresentante) {
			this.tipoDocIdentidadRepresentante = tipoDocIdentidadRepresentante;
			return this;
		}

		public SeguridadSocialBuilder docIdentidadRepresentante(String docIdentidadRepresentante) {
			this.docIdentidadRepresentante = docIdentidadRepresentante;
			return this;
		}

		public SeguridadSocialBuilder nssrepresentante(String nssrepresentante) {
			this.nssrepresentante = nssrepresentante;
			return this;
		}

		public SeguridadSocialBuilder regimen(String regimen) {
			this.regimen = regimen;
			return this;
		}

		public SeguridadSocialBuilder grupo(String grupo) {
			this.grupo = grupo;
			return this;
		}

		public SeguridadSocialBuilder trl(String trl) {
			this.trl = trl;
			return this;
		}

		public SeguridadSocialBuilder subgrupo(String subgrupo) {
			this.subgrupo = subgrupo;
			return this;
		}

		public SeguridadSocialBuilder integradoColegioProfesional(boolean integradoColegioProfesional) {
			this.integradoColegioProfesional = integradoColegioProfesional;
			return this;
		}

		public SeguridadSocialBuilder colegioProfesional(String colegioProfesional) {
			this.colegioProfesional = colegioProfesional;
			return this;
		}

		public SeguridadSocialBuilder discapacidad(boolean discapacidad) {
			this.discapacidad = discapacidad;
			return this;
		}

		public SeguridadSocialBuilder tipoDiscapacidad(String tipoDiscapacidad) {
			this.tipoDiscapacidad = tipoDiscapacidad;
			return this;
		}

		public SeguridadSocialBuilder gradoDiscapacidad(int gradoDiscapacidad) {
			this.gradoDiscapacidad = gradoDiscapacidad;
			return this;
		}

		public SeguridadSocialBuilder fechaEfectoDiscapacidad(Date fechaEfectoDiscapacidad) {
			this.fechaEfectoDiscapacidad = fechaEfectoDiscapacidad;
			return this;
		}

		public SeguridadSocialBuilder cotizacionMinima(boolean cotizacionMinima) {
			this.cotizacionMinima = cotizacionMinima;
			return this;
		}

		public SeguridadSocialBuilder cotizacionMaxima(boolean cotizacionMaxima) {
			this.cotizacionMaxima = cotizacionMaxima;
			return this;
		}

		public SeguridadSocialBuilder cotizacionOtra(boolean cotizacionOtra) {
			this.cotizacionOtra = cotizacionOtra;
			return this;
		}

		public SeguridadSocialBuilder baseCotizacion(float baseCotizacion) {
			this.baseCotizacion = baseCotizacion;
			return this;
		}

		public SeguridadSocialBuilder rendimientoNetoAnual(float rendimientoNetoAnual) {
			this.rendimientoNetoAnual = rendimientoNetoAnual;
			return this;
		}

		public SeguridadSocialBuilder mutuaIT(String mutuaIT) {
			this.mutuaIT = mutuaIT;
			return this;
		}

		public SeguridadSocial build() {
			return new SeguridadSocial(this);
		}
	}
}
