package solutions.aon.circe;

import java.util.Date;

public class DatosPersonales {

	private String tipoDocIdentidad;
	private String numDocIdentidad;
	private Date fechaNacimiento;
	private String nacionalidad;
	private String sexo;
	private String nombre;
	private String primerApellido;
	private String segundoApellido;
	private String domino;
	private String estadoCivil;
	private Date fechaEstadoCivil;
	private String domicilioResidencia;
	private String domicilioFiscal;
	private String domicilioNotificacion;
	private int telefono;
	private String email;
	private String prefijoPais;

	public DatosPersonales() {

	}

	public DatosPersonales(DatosPersonalesBuilder datosPersonalesBuilder) {
		this.tipoDocIdentidad = datosPersonalesBuilder.tipoDocIdentidad;
		this.numDocIdentidad = datosPersonalesBuilder.numDocIdentidad;
		this.fechaNacimiento = datosPersonalesBuilder.fechaNacimiento;
		this.nacionalidad = datosPersonalesBuilder.nacionalidad;
		this.sexo = datosPersonalesBuilder.sexo;
		this.nombre = datosPersonalesBuilder.nombre;
		this.primerApellido = datosPersonalesBuilder.primerApellido;
		this.segundoApellido = datosPersonalesBuilder.segundoApellido;
		this.domino = datosPersonalesBuilder.domino;
		this.estadoCivil = datosPersonalesBuilder.estadoCivil;
		this.fechaEstadoCivil = datosPersonalesBuilder.fechaEstadoCivil;
		this.domicilioResidencia = datosPersonalesBuilder.domicilioResidencia;
		this.domicilioFiscal = datosPersonalesBuilder.domicilioFiscal;
		this.domicilioNotificacion = datosPersonalesBuilder.domicilioNotificacion;
		this.telefono = datosPersonalesBuilder.telefono;
		this.email = datosPersonalesBuilder.email;
		this.prefijoPais = datosPersonalesBuilder.prefijoPais;
	}

	public String getTipoDocIdentidad() {
		return tipoDocIdentidad;
	}

	public String getNumDocIdentidad() {
		return numDocIdentidad;
	}

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public String getNacionalidad() {
		return nacionalidad;
	}

	public String getSexo() {
		return sexo;
	}

	public String getNombre() {
		return nombre;
	}

	public String getPrimerApellido() {
		return primerApellido;
	}

	public String getSegundoApellido() {
		return segundoApellido;
	}

	public String getDomino() {
		return domino;
	}

	public String getEstadoCivil() {
		return estadoCivil;
	}

	public Date getFechaEstadoCivil() {
		return fechaEstadoCivil;
	}

	public String getDomicilioResidencia() {
		return domicilioResidencia;
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

	public String getPrefijoPais() {
		return prefijoPais;
	}
	
	public static class DatosPersonalesBuilder{
		
		private String tipoDocIdentidad;
		private String numDocIdentidad;
		private Date fechaNacimiento;
		private String nacionalidad;
		private String sexo;
		private String nombre;
		private String primerApellido;
		private String segundoApellido;
		private String domino;
		private String estadoCivil;
		private Date fechaEstadoCivil;
		private String domicilioResidencia;
		private String domicilioFiscal;
		private String domicilioNotificacion;
		private int telefono;
		private String email;
		private String prefijoPais;
		
		public DatosPersonalesBuilder tipoDocIdentidad(String tipoDocIdentidad) {
			this.tipoDocIdentidad = tipoDocIdentidad;
			return this;
		}
		
		public DatosPersonalesBuilder numDocIdentidad(String numDocIdentidad) {
			this.numDocIdentidad = numDocIdentidad;
			return this;
		}
		
		public DatosPersonalesBuilder fechaNacimiento(Date fechaNacimiento) {
			this.fechaNacimiento = fechaNacimiento;
			return this;
		}
		
		public DatosPersonalesBuilder nacionalidad(String nacionalidad) {
			this.nacionalidad = nacionalidad;
			return this;
		}
		
		public DatosPersonalesBuilder sexo(String sexo) {
			this.sexo = sexo;
			return this;
		}
		
		public DatosPersonalesBuilder nombre(String nombre) {
			this.nombre = nombre;
			return this;
		}
		
		public DatosPersonalesBuilder primerApellido(String primerApellido) {
			this.primerApellido = primerApellido;
			return this;
		}
		
		public DatosPersonalesBuilder segundoApellido(String segundoApellido) {
			this.segundoApellido = segundoApellido;
			return this;
		}
		
		public DatosPersonalesBuilder domino(String domino) {
			this.domino = domino;
			return this;
		}
		
		public DatosPersonalesBuilder estadoCivil(String estadoCivil) {
			this.estadoCivil = estadoCivil;
			return this;
		}
		
		public DatosPersonalesBuilder fechaEstadoCivil(Date fechaEstadoCivil) {
			this.fechaEstadoCivil = fechaEstadoCivil;
			return this;
		}
		
		public DatosPersonalesBuilder domicilioResidencia(String domicilioResidencia) {
			this.domicilioResidencia = domicilioResidencia;
			return this;
		}
		
		public DatosPersonalesBuilder domicilioFiscal(String domicilioFiscal) {
			this.domicilioFiscal = domicilioFiscal;
			return this;
		}
		
		public DatosPersonalesBuilder domicilioNotificacion(String domicilioNotificacion) {
			this.domicilioNotificacion = domicilioNotificacion;
			return this;
		}
		
		public DatosPersonalesBuilder telefono(int telefono) {
			this.telefono = telefono;
			return this;
		}
		
		public DatosPersonalesBuilder email(String email) {
			this.email = email;
			return this;
		}
		
		public DatosPersonalesBuilder prefijoPais(String prefijoPais) {
			this.prefijoPais = prefijoPais;
			return this;
		}
		
		public DatosPersonales build() {
			return new DatosPersonales(this);
		}
		
	}
}
