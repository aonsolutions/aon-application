package solutions.aon.circe;

import java.util.Date;

public class Actividades {
	private int superficieTotal;
	private int superficieComputable;
	private int superficieRectificada;
	private int numReferencia;
	private Domicilio domicilio;
	private String nombreComercial;
	private Date inicioActividad;
	private int numTrabajadores;
	private int claveCNAE;
	private String descripcionCNAE;
	private int claveIAE;
	private String descripcionIAE;
	private String tipoActividadIAE;
	private boolean realizarComunicacion;
	private int epigrafeAELugarFueraDelLocal;
	private String tipoActividadLugarFueraDelLocal;
	private String provinciaLugarFueraDelLocal;
	private String municipioLugarFueraDelLocal;
	private int fechaInicioLugarFueraDelLocal;
	private int epigrafeAELocalAfectado;
	private String tipoActividadLocalAfectado;
	private String afectacionLocalAfectado;
	private String localAfectado;
	private int superficieLocalAfectado;
	private int gradoLocalAfectado;
	private int fechaInicioLocalAfectado;
	private String usoLocalAfectado;

	public Actividades() {

	}

	public Actividades(ActividadesBuilder actividadesBuilder) {
		this.superficieTotal = actividadesBuilder.superficieTotal;
		this.superficieComputable = actividadesBuilder.superficieComputable;
		this.superficieRectificada = actividadesBuilder.superficieRectificada;
		this.numReferencia = actividadesBuilder.numReferencia;
		this.domicilio = actividadesBuilder.domicilio;
		this.nombreComercial = actividadesBuilder.nombreComercial;
		this.inicioActividad = actividadesBuilder.inicioActividad;
		this.numTrabajadores = actividadesBuilder.numTrabajadores;
		this.claveCNAE = actividadesBuilder.claveCNAE;
		this.descripcionCNAE = actividadesBuilder.descripcionCNAE;
		this.claveIAE = actividadesBuilder.claveIAE;
		this.descripcionIAE = actividadesBuilder.descripcionIAE;
		this.tipoActividadIAE = actividadesBuilder.tipoActividadIAE;
		this.realizarComunicacion = actividadesBuilder.realizarComunicacion;
		this.epigrafeAELugarFueraDelLocal = actividadesBuilder.epigrafeAELugarFueraDelLocal;
		this.tipoActividadLugarFueraDelLocal = actividadesBuilder.tipoActividadLugarFueraDelLocal;
		this.provinciaLugarFueraDelLocal = actividadesBuilder.provinciaLugarFueraDelLocal;
		this.municipioLugarFueraDelLocal = actividadesBuilder.municipioLugarFueraDelLocal;
		this.fechaInicioLugarFueraDelLocal = actividadesBuilder.fechaInicioLugarFueraDelLocal;
		this.epigrafeAELocalAfectado = actividadesBuilder.epigrafeAELocalAfectado;
		this.tipoActividadLocalAfectado = actividadesBuilder.tipoActividadLocalAfectado;
		this.afectacionLocalAfectado = actividadesBuilder.afectacionLocalAfectado;
		this.localAfectado = actividadesBuilder.localAfectado;
		this.superficieLocalAfectado = actividadesBuilder.superficieLocalAfectado;
		this.gradoLocalAfectado = actividadesBuilder.gradoLocalAfectado;
		this.fechaInicioLocalAfectado = actividadesBuilder.fechaInicioLocalAfectado;
		this.usoLocalAfectado = actividadesBuilder.usoLocalAfectado;

	}

	public int getSuperficieTotal() {
		return superficieTotal;
	}

	public int getSuperficieComputable() {
		return superficieComputable;
	}

	public int getSuperficieRectificada() {
		return superficieRectificada;
	}

	public int getNumReferencia() {
		return numReferencia;
	}

	public Domicilio getDomicilio() {
		return domicilio;
	}

	public String getNombreComercial() {
		return nombreComercial;
	}

	public Date getInicioActividad() {
		return inicioActividad;
	}

	public int getNumTrabajadores() {
		return numTrabajadores;
	}

	public int getClaveCNAE() {
		return claveCNAE;
	}

	public String getDescripcionCNAE() {
		return descripcionCNAE;
	}

	public int getClaveIAE() {
		return claveIAE;
	}

	public String getDescripcionIAE() {
		return descripcionIAE;
	}

	public String getTipoActividadIAE() {
		return tipoActividadIAE;
	}

	public boolean isRealizarComunicacion() {
		return realizarComunicacion;
	}

	public int getEpigrafeAELugarFueraDelLocal() {
		return epigrafeAELugarFueraDelLocal;
	}

	public String getTipoActividadLugarFueraDelLocal() {
		return tipoActividadLugarFueraDelLocal;
	}

	public String getProvinciaLugarFueraDelLocal() {
		return provinciaLugarFueraDelLocal;
	}

	public String getMunicipioLugarFueraDelLocal() {
		return municipioLugarFueraDelLocal;
	}

	public int getFechaInicioLugarFueraDelLocal() {
		return fechaInicioLugarFueraDelLocal;
	}

	public int getEpigrafeAELocalAfectado() {
		return epigrafeAELocalAfectado;
	}

	public String getTipoActividadLocalAfectado() {
		return tipoActividadLocalAfectado;
	}

	public String getAfectacionLocalAfectado() {
		return afectacionLocalAfectado;
	}

	public String getLocalAfectado() {
		return localAfectado;
	}

	public int getSuperficieLocalAfectado() {
		return superficieLocalAfectado;
	}

	public int getGradoLocalAfectado() {
		return gradoLocalAfectado;
	}

	public int getFechaInicioLocalAfectado() {
		return fechaInicioLocalAfectado;
	}

	public String getUsoLocalAfectado() {
		return usoLocalAfectado;
	}

	public static class ActividadesBuilder {
		private int superficieTotal;
		private int superficieComputable;
		private int superficieRectificada;
		private int numReferencia;
		private Domicilio domicilio;
		private String nombreComercial;
		private Date inicioActividad;
		private int numTrabajadores;
		private int claveCNAE;
		private String descripcionCNAE;
		private int claveIAE;
		private String descripcionIAE;
		private String tipoActividadIAE;
		private boolean realizarComunicacion;
		private int epigrafeAELugarFueraDelLocal;
		private String tipoActividadLugarFueraDelLocal;
		private String provinciaLugarFueraDelLocal;
		private String municipioLugarFueraDelLocal;
		private int fechaInicioLugarFueraDelLocal;
		private int epigrafeAELocalAfectado;
		private String tipoActividadLocalAfectado;
		private String afectacionLocalAfectado;
		private String localAfectado;
		private int superficieLocalAfectado;
		private int gradoLocalAfectado;
		private int fechaInicioLocalAfectado;
		private String usoLocalAfectado;

		public ActividadesBuilder superficieTotal(int superficieTotal) {
			this.superficieTotal = superficieTotal;
			return this;
		}

		public ActividadesBuilder superficieComputable(int superficieComputable) {
			this.superficieComputable = superficieComputable;
			return this;
		}

		public ActividadesBuilder superficieRectificada(int superficieRectificada) {
			this.superficieRectificada = superficieRectificada;
			return this;
		}

		public ActividadesBuilder numReferencia(int numReferencia) {
			this.numReferencia = numReferencia;
			return this;
		}

		public ActividadesBuilder domicilio(Domicilio domicilio) {
			this.domicilio = domicilio;
			return this;
		}

		public ActividadesBuilder nombreComercial(String nombreComercial) {
			this.nombreComercial = nombreComercial;
			return this;
		}

		public ActividadesBuilder inicioActividad(Date inicioActividad) {
			this.inicioActividad = inicioActividad;
			return this;
		}

		public ActividadesBuilder numTrabajadores(int numTrabajadores) {
			this.numTrabajadores = numTrabajadores;
			return this;
		}

		public ActividadesBuilder claveCNAE(int claveCNAE) {
			this.claveCNAE = claveCNAE;
			return this;
		}

		public ActividadesBuilder descripcionCNAE(String descripcionCNAE) {
			this.descripcionCNAE = descripcionCNAE;
			return this;
		}

		public ActividadesBuilder claveIAE(int claveIAE) {
			this.claveIAE = claveIAE;
			return this;
		}

		public ActividadesBuilder descripcionIAE(String descripcionIAE) {
			this.descripcionIAE = descripcionIAE;
			return this;
		}

		public ActividadesBuilder tipoActividadIAE(String tipoActividadIAE) {
			this.tipoActividadIAE = tipoActividadIAE;
			return this;
		}

		public ActividadesBuilder realizarComunicacion(boolean realizarComunicacion) {
			this.realizarComunicacion = realizarComunicacion;
			return this;
		}

		public ActividadesBuilder epigrafeAELugarFueraDelLocal(int epigrafeAELugarFueraDelLocal) {
			this.epigrafeAELugarFueraDelLocal = epigrafeAELugarFueraDelLocal;
			return this;
		}

		public ActividadesBuilder tipoActividadLugarFueraDelLocal(String tipoActividadLugarFueraDelLocal) {
			this.tipoActividadLugarFueraDelLocal = tipoActividadLugarFueraDelLocal;
			return this;
		}

		public ActividadesBuilder provinciaLugarFueraDelLocal(String provinciaLugarFueraDelLocal) {
			this.provinciaLugarFueraDelLocal = provinciaLugarFueraDelLocal;
			return this;
		}

		public ActividadesBuilder municipioLugarFueraDelLocal(String municipioLugarFueraDelLocal) {
			this.municipioLugarFueraDelLocal = municipioLugarFueraDelLocal;
			return this;
		}

		public ActividadesBuilder fechaInicioLugarFueraDelLocal(int fechaInicioLugarFueraDelLocal) {
			this.fechaInicioLugarFueraDelLocal = fechaInicioLugarFueraDelLocal;
			return this;
		}

		public ActividadesBuilder epigrafeAELocalAfectado(int epigrafeAELocalAfectado) {
			this.epigrafeAELocalAfectado = epigrafeAELocalAfectado;
			return this;
		}

		public ActividadesBuilder tipoActividadLocalAfectado(String tipoActividadLocalAfectado) {
			this.tipoActividadLocalAfectado = tipoActividadLocalAfectado;
			return this;
		}

		public ActividadesBuilder afectacionLocalAfectado(String afectacionLocalAfectado) {
			this.afectacionLocalAfectado = afectacionLocalAfectado;
			return this;
		}

		public ActividadesBuilder localAfectado(String localAfectado) {
			this.localAfectado = localAfectado;
			return this;
		}

		public ActividadesBuilder superficieLocalAfectado(int superficieLocalAfectado) {
			this.superficieLocalAfectado = superficieLocalAfectado;
			return this;
		}

		public ActividadesBuilder gradoLocalAfectado(int gradoLocalAfectado) {
			this.gradoLocalAfectado = gradoLocalAfectado;
			return this;
		}

		public ActividadesBuilder fechaInicioLocalAfectado(int fechaInicioLocalAfectado) {
			this.fechaInicioLocalAfectado = fechaInicioLocalAfectado;
			return this;
		}

		public ActividadesBuilder usoLocalAfectado(String usoLocalAfectado) {
			this.usoLocalAfectado = usoLocalAfectado;
			return this;
		}

		public Actividades build() {
			return new Actividades(this);
		}

	}

}
