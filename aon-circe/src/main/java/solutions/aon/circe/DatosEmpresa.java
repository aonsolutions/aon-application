package solutions.aon.circe;

import java.util.Date;

public class DatosEmpresa {

	private String anagrama;
	private Date inicioActividad;
	private Date cierreEjercicio;
	private String pagWebCorporativa;
	private String duracionPersonaJuridica;
	private String denominacionSocial;
	private int capitalSocial;
	private String actividades;
	private String actividadSingular;
	private int numPersonasTrabajadoras;
	private String domicilioSocial;
	private String domicilioFiscal;
	private String domicilioNotificacion;
	private int telefono;
	private String email;
	private String medioNotificacion;
	private String prefijoPais;

	public DatosEmpresa() {

	}

	public DatosEmpresa(DatosEmpresaBuilder datosEmpresaBuilder) {
		this.anagrama = datosEmpresaBuilder.anagrama;
		this.inicioActividad = datosEmpresaBuilder.inicioActividad;
		this.cierreEjercicio = datosEmpresaBuilder.cierreEjercicio;
		this.pagWebCorporativa = datosEmpresaBuilder.pagWebCorporativa;
		this.duracionPersonaJuridica = datosEmpresaBuilder.duracionPersonaJuridica;
		this.denominacionSocial = datosEmpresaBuilder.denominacionSocial;
		this.capitalSocial = datosEmpresaBuilder.capitalSocial;
		this.actividades = datosEmpresaBuilder.actividades;
		this.actividadSingular = datosEmpresaBuilder.actividadSingular;
		this.numPersonasTrabajadoras = datosEmpresaBuilder.numPersonasTrabajadoras;
		this.domicilioSocial = datosEmpresaBuilder.domicilioSocial;
		this.domicilioFiscal = datosEmpresaBuilder.domicilioFiscal;
		this.domicilioNotificacion = datosEmpresaBuilder.domicilioNotificacion;
		this.telefono = datosEmpresaBuilder.telefono;
		this.email = datosEmpresaBuilder.email;
		this.medioNotificacion = datosEmpresaBuilder.medioNotificacion;
		this.prefijoPais = datosEmpresaBuilder.prefijoPais;

	}

	public String getAnagrama() {
		return anagrama;
	}

	public Date getInicioActividad() {
		return inicioActividad;
	}

	public Date getCierreEjercicio() {
		return cierreEjercicio;
	}

	public String getPagWebCorporativa() {
		return pagWebCorporativa;
	}

	public String getDuracionPersonaJuridica() {
		return duracionPersonaJuridica;
	}

	public String getDenominacionSocial() {
		return denominacionSocial;
	}

	public int getCapitalSocial() {
		return capitalSocial;
	}

	public String getActividades() {
		return actividades;
	}

	public String getActividadSingular() {
		return actividadSingular;
	}

	public int getNumPersonasTrabajadoras() {
		return numPersonasTrabajadoras;
	}

	public String getDomicilioSocial() {
		return domicilioSocial;
	}

	public String getDomicilioFiscal() {
		return domicilioFiscal;
	}

	public String getDomicilioNotificacion() {
		return domicilioNotificacion;
	}

	public int getTelefono() {
		return telefono;
	}

	public String getEmail() {
		return email;
	}

	public String getMedioNotificacion() {
		return medioNotificacion;
	}

	public String getPrefijoPais() {
		return prefijoPais;
	}

	public static class DatosEmpresaBuilder {

		private String anagrama;
		private Date inicioActividad;
		private Date cierreEjercicio;
		private String pagWebCorporativa;
		private String duracionPersonaJuridica;
		private String denominacionSocial;
		private int capitalSocial;
		private String actividades;
		private String actividadSingular;
		private int numPersonasTrabajadoras;
		private String domicilioSocial;
		private String domicilioFiscal;
		private String domicilioNotificacion;
		private int telefono;
		private String email;
		private String medioNotificacion;
		private String prefijoPais;

		public DatosEmpresaBuilder anagrama(String anagrama) {
			this.anagrama = anagrama;
			return this;
		}

		public DatosEmpresaBuilder inicioActividad(Date inicioActividad) {
			this.inicioActividad = inicioActividad;
			return this;
		}

		public DatosEmpresaBuilder cierreEjercicio(Date cierreEjercicio) {
			this.cierreEjercicio = cierreEjercicio;
			return this;
		}

		public DatosEmpresaBuilder pagWebCorporativa(String pagWebCorporativa) {
			this.pagWebCorporativa = pagWebCorporativa;
			return this;
		}

		public DatosEmpresaBuilder duracionPersonaJuridica(String duracionPersonaJuridica) {
			this.duracionPersonaJuridica = duracionPersonaJuridica;
			return this;
		}

		public DatosEmpresaBuilder denominacionSocial(String denominacionSocial) {
			this.denominacionSocial = denominacionSocial;
			return this;
		}

		public DatosEmpresaBuilder capitalSocial(int capitalSocial) {
			this.capitalSocial = capitalSocial;
			return this;
		}

		public DatosEmpresaBuilder actividades(String actividades) {
			this.actividades = actividades;
			return this;
		}

		public DatosEmpresaBuilder actividadSingular(String actividadSingular) {
			this.actividadSingular = actividadSingular;
			return this;
		}

		public DatosEmpresaBuilder numPersonasTrabajadoras(int numPersonasTrabajadoras) {
			this.numPersonasTrabajadoras = numPersonasTrabajadoras;
			return this;
		}

		public DatosEmpresaBuilder domicilioSocial(String domicilioSocial) {
			this.domicilioSocial = domicilioSocial;
			return this;
		}

		public DatosEmpresaBuilder domicilioFiscal(String domicilioFiscal) {
			this.domicilioFiscal = domicilioFiscal;
			return this;
		}

		public DatosEmpresaBuilder domicilioNotificacion(String domicilioNotificacion) {
			this.domicilioNotificacion = domicilioNotificacion;
			return this;
		}

		public DatosEmpresaBuilder telefono(int telefono) {
			this.telefono = telefono;
			return this;
		}

		public DatosEmpresaBuilder email(String email) {
			this.email = email;
			return this;
		}

		public DatosEmpresaBuilder medioNotificacion(String medioNotificacion) {
			this.medioNotificacion = medioNotificacion;
			return this;
		}

		public DatosEmpresaBuilder prefijoPais(String prefijoPais) {
			this.prefijoPais = prefijoPais;
			return this;
		}

		public DatosEmpresa build() {
			return new DatosEmpresa(this);
		}
	}
}
