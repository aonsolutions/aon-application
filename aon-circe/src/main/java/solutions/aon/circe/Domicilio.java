package solutions.aon.circe;

import solutions.aon.circe.DatosEmpresa.DatosEmpresaBuilder;

public class Domicilio {
	private String tipoVia;
	private String nombreVia;
	private int km;
	private int num;
	private String calificadorNum;
	private String bloque;
	private String portal;
	private int piso;
	private String escalera;
	private String puerta;
	private String coplemnetoDomicilio;
	private String pais;
	private String provincia;
	private String municipio;
	private String localidad;
	private int codigoPostal;
	private String indicadorReferenciaCatastral;
	private String referenciaCataastral;
	
	public Domicilio() {
		
	}
	
	public Domicilio(DomicilioBuilder domicilioBuilder) {
		this.tipoVia = domicilioBuilder.tipoVia;
		this.nombreVia = domicilioBuilder.nombreVia;
		this.km = domicilioBuilder.km;
		this.num = domicilioBuilder.num;
		this.calificadorNum = domicilioBuilder.calificadorNum;
		this.bloque = domicilioBuilder.bloque;
		this.portal = domicilioBuilder.portal;
		this.piso = domicilioBuilder.piso;
		this.escalera = domicilioBuilder.escalera;
		this.puerta = domicilioBuilder.puerta;
		this.coplemnetoDomicilio = domicilioBuilder.coplemnetoDomicilio;
		this.pais = domicilioBuilder.pais;
		this.provincia = domicilioBuilder.provincia;
		this.municipio = domicilioBuilder.municipio;
		this.localidad = domicilioBuilder.localidad;
		this.codigoPostal = domicilioBuilder.codigoPostal;
		this.indicadorReferenciaCatastral = domicilioBuilder.indicadorReferenciaCatastral;
		this.referenciaCataastral = domicilioBuilder.referenciaCataastral;

	}

	public String getTipoVia() {
		return tipoVia;
	}

	public String getNombreVia() {
		return nombreVia;
	}

	public int getKm() {
		return km;
	}

	public int getNum() {
		return num;
	}

	public String getCalificadorNum() {
		return calificadorNum;
	}

	public String getBloque() {
		return bloque;
	}

	public String getPortal() {
		return portal;
	}

	public int getPiso() {
		return piso;
	}

	public String getEscalera() {
		return escalera;
	}

	public String getPuerta() {
		return puerta;
	}

	public String getCoplemnetoDomicilio() {
		return coplemnetoDomicilio;
	}

	public String getPais() {
		return pais;
	}

	public String getProvincia() {
		return provincia;
	}

	public String getMunicipio() {
		return municipio;
	}

	public String getLocalidad() {
		return localidad;
	}

	public int getCodigoPostal() {
		return codigoPostal;
	}

	public String getIndicadorReferenciaCatastral() {
		return indicadorReferenciaCatastral;
	}

	public String getReferenciaCataastral() {
		return referenciaCataastral;
	}
	
	public String toString() {
		return this.nombreVia+" "+this.num+" "+this.codigoPostal+" "+this.provincia+" ("+") "+this.municipio;
		
	}
	
	
	
	
	public static class DomicilioBuilder {
		
		private String tipoVia;
		private String nombreVia;
		private int km;
		private int num;
		private String calificadorNum;
		private String bloque;
		private String portal;
		private int piso;
		private String escalera;
		private String puerta;
		private String coplemnetoDomicilio;
		private String pais;
		private String provincia;
		private String municipio;
		private String localidad;
		private int codigoPostal;
		private String indicadorReferenciaCatastral;
		private String referenciaCataastral;
		

		public DomicilioBuilder tipoVia(String tipoVia) {
			this.tipoVia = tipoVia;
			return this;
		}
		public DomicilioBuilder nombreVia(String nombreVia) {
			this.nombreVia = nombreVia;
			return this;
		}
		public DomicilioBuilder km(int km) {
			this.km = km;
			return this;
		}
		public DomicilioBuilder num(int num) {
			this.num = num;
			return this;
		}
		public DomicilioBuilder calificadorNum(String calificadorNum) {
			this.calificadorNum = calificadorNum;
			return this;
		}
		public DomicilioBuilder bloque(String bloque) {
			this.bloque = bloque;
			return this;
		}
		public DomicilioBuilder portal(String portal) {
			this.portal = portal;
			return this;
		}
		public DomicilioBuilder piso(int piso) {
			this.piso = piso;
			return this;
		}
		public DomicilioBuilder escalera(String escalera) {
			this.escalera = escalera;
			return this;
		}
		public DomicilioBuilder puerta(String puerta) {
			this.puerta = puerta;
			return this;
		}
		public DomicilioBuilder coplemnetoDomicilio(String coplemnetoDomicilio) {
			this.coplemnetoDomicilio = coplemnetoDomicilio;
			return this;
		}
		public DomicilioBuilder pais(String pais) {
			this.pais = pais;
			return this;
		}
		public DomicilioBuilder provincia(String provincia) {
			this.provincia = provincia;
			return this;
		}
		public DomicilioBuilder municipio(String municipio) {
			this.municipio = municipio;
			return this;
		}
		public DomicilioBuilder localidad(String localidad) {
			this.localidad = localidad;
			return this;
		}
		public DomicilioBuilder codigoPostal(int codigoPostal) {
			this.codigoPostal = codigoPostal;
			return this;
		}
		public DomicilioBuilder indicadorReferenciaCatastral(String indicadorReferenciaCatastral) {
			this.indicadorReferenciaCatastral = indicadorReferenciaCatastral;
			return this;
		}
		public DomicilioBuilder referenciaCataastral(String referenciaCataastral) {
			this.referenciaCataastral = referenciaCataastral;
			return this;
		}
		
		public Domicilio build() {
			return new Domicilio(this);
		}
		
	}

}
