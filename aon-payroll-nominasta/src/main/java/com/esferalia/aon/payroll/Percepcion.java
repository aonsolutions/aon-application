package com.esferalia.aon.payroll;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.IPercepcion;

@Entity
@Table(name = "percep")
public class Percepcion implements IPercepcion, ITransferObject {

	private static final long serialVersionUID = 5955884950876640654L;
	
	private PercepcionPK id;
	private Date fechaInicio;
	private Date fechaFin;
	private Date fechaRetroactividad;
	private String descripcionComplemento;
	private String descripcionAbreviada;
	private String formaCalculo;
	private String tipoCotizacion;
	private Double unidades;
	private Double importeUnitario;
	private Double importe;
	private Integer mes;
	private Double garantizadoILT;
	private String redondeoPagaExtra;
//	private FijoVariable fijovar;
	private Date fechaCreacion;
	private Date horaCreacion;
	private Date fechaModificacion;
	private Date horaModificacion;
//	private IndiceComplemento indiceComplemento;
//	private TipoComplemento tipoComplemento;
//	private Retribuciones retribucion;
//	private Complemento complemento;
//	private Complemento complementoAplicar;
	private IEmpleado empleado;

	
	@EmbeddedId
	@Override
	public PercepcionPK getId() {
		return this.id;
	}
	public void setId(PercepcionPK id) {
		this.id = id;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fecini", nullable = false)
	@Override
	public Date getFechaInicio() {
		return this.fechaInicio;
	}
	@Override
	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}
	
	@Temporal(TemporalType.DATE)
	@Column(name = "fecfin")
	@Override
	public Date getFechaFin() {
		return this.fechaFin;
	}
	@Override
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fecret")
	@Override
	public Date getFechaRetroactividad() {
		return this.fechaRetroactividad;
	}
	@Override
	public void setFechaRetroactividad(Date fechaRetroactividad) {
		this.fechaRetroactividad = fechaRetroactividad;
	}

	@Column(name = "descom", nullable = false, length = 50)
	@Override
	public String getDescripcionComplemento() {
		return this.descripcionComplemento;
	}
	@Override
	public void setDescripcionComplemento(String descripcionComplemento) {
		this.descripcionComplemento = descripcionComplemento;
	}

	@Column(name = "desabr", nullable = false, length = 15)
	@Override
	public String getDescripcionAbreviada() {
		return this.descripcionAbreviada;
	}
	@Override
	public void setDescripcionAbreviada(String descripcionAbreviada) {
		this.descripcionAbreviada = descripcionAbreviada;
	}

	@Column(name = "calculo", nullable = false, length = 1)
	@Override
	public String getFormaCalculo() {
		return this.formaCalculo;
	}
	@Override
	public void setFormaCalculo(String formaCalculo) {
		this.formaCalculo = formaCalculo;
	}

	@Column(name = "tipcot", nullable = false, length = 1)
	@Override
	public String getTipoCotizacion() {
		return this.tipoCotizacion;
	}
	@Override
	public void setTipoCotizacion(String tipoCotizacion) {
		this.tipoCotizacion = tipoCotizacion;
	}

	@Column(name = "unidades", nullable = false, scale = 2, precision = 8)
	@Override
	public Double getUnidades() {
		return this.unidades;
	}
	@Override
	public void setUnidades(Double unidades) {
		this.unidades = unidades;
	}

	@Column(name = "impuni", nullable = false, scale = 2, precision = 11)
	@Override
	public Double getImporteUnitario() {
		return this.importeUnitario;
	}
	@Override
	public void setImporteUnitario(Double importeUnitario) {
		this.importeUnitario = importeUnitario;
	}

	@Column(name = "importe", nullable = false, scale = 2, precision = 11)
	@Override
	public Double getImporte() {
		return this.importe;
	}
	@Override
	public void setImporte(Double importe) {
		this.importe = importe;
	}

	@Column(name = "mes", nullable = false, length = 2)
	@Override
	public Integer getMes() {
		return this.mes;
	}
	@Override
	public void setMes(Integer mes) {
		this.mes = mes;
	}

	@Column(name = "garilt", nullable = false, scale = 2, precision = 5)
	@Override
	public Double getGarantizadoILT() {
		return this.garantizadoILT;
	}
	@Override
	public void setGarantizadoILT(Double garantizadoILT) {
		this.garantizadoILT = garantizadoILT;
	}

	@Column(name = "redext", length = 1)
	@Override
	public String getRedondeoPagaExtra() {
		return this.redondeoPagaExtra;
	}
	@Override
	public void setRedondeoPagaExtra(String redondeoPagaExtra) {
		this.redondeoPagaExtra = redondeoPagaExtra;
	}

//	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.code.aon.payroll.enumeration.FijoVariable") })
//	@Column(name = "fijovar", nullable = false, length = 1)
//	@Override
//	public FijoVariable getFijovar() {
//		return this.fijovar;
//	}
//	@Override
//	public void setFijovar(FijoVariable fijovar) {
//		this.fijovar = fijovar;
//	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fecnew")
	@Override
	public Date getFechaCreacion() {
		return this.fechaCreacion;
	}
	@Override
	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	@Temporal(TemporalType.TIME)
	@Column(name = "hornew")
	@Override
	public Date getHoraCreacion() {
		return this.horaCreacion;
	}
	@Override
	public void setHoraCreacion(Date horaCreacion) {
		this.horaCreacion = horaCreacion;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "fecmod")
	@Override
	public Date getFechaModificacion() {
		return this.fechaModificacion;
	}
	@Override
	public void setFechaModificacion(Date fechaModificacion) {
		this.fechaModificacion= fechaModificacion;
	}

	@Temporal(TemporalType.TIME)
	@Column(name = "hormod")
	@Override
	public Date getHoraModificacion() {
		return this.horaModificacion;
	}
	@Override
	public void setHoraModificacion(Date horaModificacion) {
		this.horaModificacion = horaModificacion;
	}

//	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.code.aon.payroll.enumeration.IndiceComplemento") })
//	@Column(name = "indcom", length = 1)
//	@Override
//	public IndiceComplemento getIndiceComplemento() {
//		return this.indiceComplemento;
//	}
//	@Override
//	public void setIndiceComplemento(IndiceComplemento indiceComplemento) {
//		this.indiceComplemento = indiceComplemento;
//	}

//	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.code.aon.payroll.enumeration.TipoComplemento") })
//	@Column(name = "tipcom", length = 1)
//	@Override
//	public TipoComplemento getTipoComplemento() {
//		return this.tipoComplemento;
//	}
//	@Override
//	public void setTipoComplemento(TipoComplemento tipoComplemento) {
//		this.tipoComplemento = tipoComplemento;
//	}

//	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.code.aon.payroll.enumeration.Retribuciones") })
//	@Column(name = "dinesp", nullable = false, length = 1)
//	@Override
//	public Retribuciones getRetribucion() {
//		return this.retribucion;
//	}
//	@Override
//	public void setRetribucion(Retribuciones retribucion) {
//		this.retribucion = retribucion;
//	}

//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "codcom", nullable = false)
//	@Override
//	public Complemento getComplemento() {
//		return this.complemento;
//	}
//	@Override
//	public void setComplemento(Complemento complemento) {
//		this.complemento = complemento;
//	}

//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "comapl")
//	@Override
//	public Complemento getComplementoAplicar() {
//		return this.complementoAplicar;
//	}
//	@Override
//	public void setComplementoAplicar(Complemento complementoAplicar) {
//		this.complementoAplicar = complementoAplicar;
//	}

	
	@ManyToOne(targetEntity = Empleado.class,fetch = FetchType.EAGER)
	@JoinColumn(name = "numero", insertable = false, updatable = false)
	@Override
	public IEmpleado getEmpleado() {
		return this.empleado;
	}
	@Override
	public void setEmpleado(IEmpleado empleado) {
		this.empleado = empleado;
	}

}
