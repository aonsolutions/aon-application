package solutions.aon.circe;

import java.util.Date;

public class DatosEmpresa {

	private String anagrama;
	private Date inicioActividad;
	private Date cierreEjercicio;
	private String pagWebCorporativa;
	private String duracionPersonaJuridica;
	private int cantidad;
	private String denominacionSocial;
	private int capitalSocial;
	private String actividades;
	private String actividadSingular;
	private int numPaticipaciones;
	private int importeParticipacion;
	private String pagWeb;
	private String tipoRetribucion;
	private int numPersonasTrabajadoras;
	private String numExpediente;
	private Domicilio domicilioSocial;
	private Domicilio domicilioFiscal;
	private Domicilio domicilioNotificacion;
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
		this.cantidad = datosEmpresaBuilder.cantidad;
		this.denominacionSocial = datosEmpresaBuilder.denominacionSocial;
		this.capitalSocial = datosEmpresaBuilder.capitalSocial;
		this.actividades = datosEmpresaBuilder.actividades;
		this.actividadSingular = datosEmpresaBuilder.actividadSingular;
		this.numPaticipaciones = datosEmpresaBuilder.numPaticipaciones;
		this.importeParticipacion = datosEmpresaBuilder.importeParticipacion;
		this.pagWeb = datosEmpresaBuilder.pagWeb;
		this.tipoRetribucion = datosEmpresaBuilder.tipoRetribucion;
		this.numPersonasTrabajadoras = datosEmpresaBuilder.numPersonasTrabajadoras;
		this.numExpediente = datosEmpresaBuilder.numExpediente;
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
	
	public int getCantidad() {
		return cantidad;
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

	public int getNumPaticipaciones() {
		return numPaticipaciones;
	}

	public int getImporteParticipacion() {
		return importeParticipacion;
	}

	public String getPagWeb() {
		return pagWeb;
	}

	public String getTipoRetribucion() {
		return tipoRetribucion;
	}
	
	public int getNumPersonasTrabajadoras() {
		return numPersonasTrabajadoras;
	}
	
	public String getNumExpediente() {
		return numExpediente;
	}

	public Domicilio getDomicilioSocial() {
		return domicilioSocial;
	}

	public Domicilio getDomicilioFiscal() {
		return domicilioFiscal;
	}

	public Domicilio getDomicilioNotificacion() {
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
		private int cantidad;
		private String denominacionSocial;
		private int capitalSocial;
		private String actividades;
		private String actividadSingular;
		private int numPaticipaciones;
		private int importeParticipacion;
		private String pagWeb;
		private String tipoRetribucion;
		private int numPersonasTrabajadoras;
		private String numExpediente;
		private Domicilio domicilioSocial;
		private Domicilio domicilioFiscal;
		private Domicilio domicilioNotificacion;
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
		
		public DatosEmpresaBuilder cantidad(int cantidad) {
			this.cantidad = cantidad;
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
		
		public DatosEmpresaBuilder numPaticipaciones(int numPaticipaciones) {
			this.numPaticipaciones = numPaticipaciones;
			return this;
		}
		
		public DatosEmpresaBuilder importeParticipacion(int importeParticipacion) {
			this.importeParticipacion = importeParticipacion;
			return this;
		}
		
		public DatosEmpresaBuilder pagWeb(String pagWeb) {
			this.pagWeb = pagWeb;
			return this;
		}
		
		public DatosEmpresaBuilder tipoRetribucion(String tipoRetribucion) {
			this.tipoRetribucion = tipoRetribucion;
			return this;
		}

		public DatosEmpresaBuilder numPersonasTrabajadoras(int numPersonasTrabajadoras) {
			this.numPersonasTrabajadoras = numPersonasTrabajadoras;
			return this;
		}
		
		public DatosEmpresaBuilder numExpediente(String numExpediente) {
			this.numExpediente = numExpediente;
			return this;
		}

		public DatosEmpresaBuilder domicilioSocial(Domicilio domicilioSocial) {
			this.domicilioSocial = domicilioSocial;
			return this;
		}

		public DatosEmpresaBuilder domicilioFiscal(Domicilio domicilioFiscal) {
			this.domicilioFiscal = domicilioFiscal;
			return this;
		}

		public DatosEmpresaBuilder domicilioNotificacion(Domicilio domicilioNotificacion) {
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
