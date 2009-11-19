package com.code.aon.payroll.resultados.irpf;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.payroll.auxiliares.Admon;
import com.code.aon.payroll.divisa.Divisa;
import com.code.aon.payroll.enumeration.ModalidadImpuesto;
import com.code.aon.payroll.enumeration.TipoImpreso;
import com.code.aon.payroll.geograficas.Provincia;
import com.code.aon.payroll.principales.empresa.Empresa;

/**
 * Impresos11x
 */
@Entity
@Table(name = "impr11x")
public class Impresos11x implements ITransferObject {

	private Integer cdg;
	private TipoImpreso tipo;
	private Integer anio;
	private Integer trimestre;
	private Integer mes;	
	private Integer tradinper;
	private BigDecimal tradinimp;
	private BigDecimal tradinret;
	private Integer traespper;
	private BigDecimal traespimp;
	private BigDecimal traespret;
	private Integer actdinper;
	private BigDecimal actdinimp;
	private BigDecimal actdinret;
	private Integer actespper;
	private BigDecimal actespimp;
	private BigDecimal actespret;
	private Integer predinper;
	private BigDecimal predinimp;
	private BigDecimal predinret;
	private Integer preespper;
	private BigDecimal preespimp;
	private BigDecimal preespret;
	private BigDecimal liqtotal;
	private Integer imgper;
	private BigDecimal imgimp;
	private BigDecimal imgret;
	private String fpago;
	private String entidad;
	private String sucursal;
	private String dc;
	private String cuenta;
	private Date fecha;
	private Date fecremimp;
	private Date fecnew;
	private Date hornew;
	private Date fecmod;
	private Date hormod;
	private ModalidadImpuesto modimpuesto;
	private String nrc;
	private Admon admon;
	private Divisa divisa;
	private Empresa emprnif;
	private Provincia provincia;

	/**
	 * Codigo de 11X
	 * 
	 * @return
	 */
	@Id
	@Column(name = "cdg", unique = true, nullable = false, length = 4)
	public Integer getCdg() {
		return this.cdg;
	}

	public void setCdg(Integer cdg) {
		this.cdg = cdg;
	}

	/**
	 * Tipo de Impreso
	 * 
	 * @return
	 */
	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.TipoImpreso")} )
	@Column(name = "tipo", nullable = false, length = 1)
	public TipoImpreso getTipo() {
		return this.tipo;
	}

	public void setTipo(TipoImpreso tipo) {
		this.tipo = tipo;
	}

	/**
	 * Anio de Devengo
	 * 
	 * @return
	 */
	@Column(name = "anio", length = 2)
	public Integer getAnio() {
		return this.anio;
	}

	public void setAnio(Integer anio) {
		this.anio = anio;
	}

	/**
	 * Numero Trimestre -- 110
	 * 
	 * @return
	 */
	@Column(name = "trimestre", length = 2)
	public Integer getTrimestre() {
		return this.trimestre;
	}

	public void setTrimestre(Integer trimestre) {
		this.trimestre = trimestre;
	}

	/**
	 * Numero de Mes -- 111
	 * 
	 * @return
	 */
	@Column(name = "mes", length = 2)
	public Integer getMes() {
		return this.mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	/**
	 * Trabajo-Dinerario-Num. Perceptores
	 * 
	 * @return
	 */
	@Column(name = "tradinper", length = 2)
	public Integer getTradinper() {
		return this.tradinper;
	}

	public void setTradinper(Integer tradinper) {
		this.tradinper = tradinper;
	}

	/**
	 * Trabajo-Dinerario-Importe
	 * 
	 * @return
	 */
	@Column(name = "tradinimp", scale=2, precision=11)
	public BigDecimal getTradinimp() {
		return this.tradinimp;
	}

	public void setTradinimp(BigDecimal tradinimp) {
		this.tradinimp = tradinimp;
	}

	/**
	 * Trabajo-Dinerario-Retencion
	 * 
	 * @return
	 */
	@Column(name = "tradinret", scale=2, precision=11)
	public BigDecimal getTradinret() {
		return this.tradinret;
	}

	public void setTradinret(BigDecimal tradinret) {
		this.tradinret = tradinret;
	}

	/**
	 * Trabajo-Especie-Num. Perceptores
	 * 
	 * @return
	 */
	@Column(name = "traespper", length = 2)
	public Integer getTraespper() {
		return this.traespper;
	}

	public void setTraespper(Integer traespper) {
		this.traespper = traespper;
	}

	/**
	 * Trabajo-Especie-Importe
	 * 
	 * @return
	 */
	@Column(name = "traespimp", scale=2, precision=11)
	public BigDecimal getTraespimp() {
		return this.traespimp;
	}

	public void setTraespimp(BigDecimal traespimp) {
		this.traespimp = traespimp;
	}

	/**
	 * Trabajo-EspecieRetencion
	 * 
	 * @return
	 */
	@Column(name = "traespret", scale=2, precision=11)
	public BigDecimal getTraespret() {
		return this.traespret;
	}

	public void setTraespret(BigDecimal traespret) {
		this.traespret = traespret;
	}

	/**
	 * Act.Profes.-Dinerario-Num. Perceptores
	 * 
	 * @return
	 */
	@Column(name = "actdinper", length = 2)
	public Integer getActdinper() {
		return this.actdinper;
	}

	public void setActdinper(Integer actdinper) {
		this.actdinper = actdinper;
	}

	/**
	 * Act.Profes.-Dinerario-Importe
	 * 
	 * @return
	 */
	@Column(name = "actdinimp", scale=2, precision=11)
	public BigDecimal getActdinimp() {
		return this.actdinimp;
	}

	public void setActdinimp(BigDecimal actdinimp) {
		this.actdinimp = actdinimp;
	}

	/**
	 * Act.Profes.-Dinerario-Retencion
	 * 
	 * @return
	 */
	@Column(name = "actdinret", scale=2, precision=11)
	public BigDecimal getActdinret() {
		return this.actdinret;
	}

	public void setActdinret(BigDecimal actdinret) {
		this.actdinret = actdinret;
	}

	/**
	 * Act.Profes.-Especie-Num. Perceptores
	 * 
	 * @return
	 */
	@Column(name = "actespper", length = 2)
	public Integer getActespper() {
		return this.actespper;
	}

	public void setActespper(Integer actespper) {
		this.actespper = actespper;
	}

	/**
	 * Act.Profes.-Especie-Importe
	 * 
	 * @return
	 */
	@Column(name = "actespimp", scale=2, precision=11)
	public BigDecimal getActespimp() {
		return this.actespimp;
	}

	public void setActespimp(BigDecimal actespimp) {
		this.actespimp = actespimp;
	}

	/**
	 * Act.Profes.-EspecieRetencion
	 * 
	 * @return
	 */
	@Column(name = "actespret", scale=2, precision=11)
	public BigDecimal getActespret() {
		return this.actespret;
	}

	public void setActespret(BigDecimal actespret) {
		this.actespret = actespret;
	}

	/**
	 * Premios-Dinerario-Num. Perceptores
	 * 
	 * @return
	 */
	@Column(name = "predinper", length = 2)
	public Integer getPredinper() {
		return this.predinper;
	}

	public void setPredinper(Integer predinper) {
		this.predinper = predinper;
	}

	/**
	 * Premios-Dinerario-Importe
	 * 
	 * @return
	 */
	@Column(name = "predinimp", scale=2, precision=11)
	public BigDecimal getPredinimp() {
		return this.predinimp;
	}

	public void setPredinimp(BigDecimal predinimp) {
		this.predinimp = predinimp;
	}

	/**
	 * Premios-Dinerario-Retencion
	 * 
	 * @return
	 */
	@Column(name = "predinret", scale=2, precision=11)
	public BigDecimal getPredinret() {
		return this.predinret;
	}

	public void setPredinret(BigDecimal predinret) {
		this.predinret = predinret;
	}

	/**
	 * Premios-Especie-Num. Perceptores
	 * 
	 * @return
	 */
	@Column(name = "preespper", length = 2)
	public Integer getPreespper() {
		return this.preespper;
	}

	public void setPreespper(Integer preespper) {
		this.preespper = preespper;
	}

	/**
	 * Premios-Especie-Importe
	 * 
	 * @return
	 */
	@Column(name = "preespimp", scale=2, precision=11)
	public BigDecimal getPreespimp() {
		return this.preespimp;
	}

	public void setPreespimp(BigDecimal preespimp) {
		this.preespimp = preespimp;
	}

	/**
	 * Premios-EspecieRetencion
	 * 
	 * @return
	 */
	@Column(name = "preespret", scale=2, precision=11)
	public BigDecimal getPreespret() {
		return this.preespret;
	}

	public void setPreespret(BigDecimal preespret) {
		this.preespret = preespret;
	}

	/**
	 * Total Liquidacion
	 * 
	 * @return
	 */
	@Column(name = "liqtotal", scale=2, precision=11)
	public BigDecimal getLiqtotal() {
		return this.liqtotal;
	}

	public void setLiqtotal(BigDecimal liqtotal) {
		this.liqtotal = liqtotal;
	}

	/**
	 * Forma de Pago
	 * 
	 * @return
	 */
	@Column(name = "fpago", length = 1)
	public String getFpago() {
		return this.fpago;
	}

	public void setFpago(String fpago) {
		this.fpago = fpago;
	}

	/**
	 * Entidad Bancaria
	 * 
	 * @return
	 */
	@Column(name = "entidad", length = 4)
	public String getEntidad() {
		return this.entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	/**
	 * Sucursal Bancaria
	 * 
	 * @return
	 */
	@Column(name = "sucursal", length = 4)
	public String getSucursal() {
		return this.sucursal;
	}

	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}

	/**
	 * Digito Control
	 * 
	 * @return
	 */
	@Column(name = "dc", length = 2)
	public String getDc() {
		return this.dc;
	}

	public void setDc(String dc) {
		this.dc = dc;
	}

	/**
	 * Numero de Cuenta
	 * 
	 * @return
	 */
	@Column(name = "cuenta", length = 10)
	public String getCuenta() {
		return this.cuenta;
	}

	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}

	/**
	 * Fecha Calculo
	 * 
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "fecha", nullable = false, length = 10)
	public Date getFecha() {
		return this.fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	/**
	 * Fecha de remesa o impresión
	 * 
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "fecremimp", length = 10)
	public Date getFecremimp() {
		return fecremimp;
	}

	public void setFecremimp(Date fecremimp) {
		this.fecremimp = fecremimp;
	}


	/**
	 * Fecha Creacion Fila
	 * 
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "fecnew", length = 10)
	public Date getFecnew() {
		return this.fecnew;
	}

	public void setFecnew(Date fecnew) {
		this.fecnew = fecnew;
	}

	/**
	 * Hora Creacion Fila
	 * 
	 * @return
	 */
	@Temporal(TemporalType.TIME)
	@Column(name = "hornew", length = 8)
	public Date getHornew() {
		return this.hornew;
	}

	public void setHornew(Date hornew) {
		this.hornew = hornew;
	}

	/**
	 * Fecha Modificacion Fila
	 * 
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "fecmod", length = 10)
	public Date getFecmod() {
		return this.fecmod;
	}

	public void setFecmod(Date fecmod) {
		this.fecmod = fecmod;
	}

	/**
	 * Hora Modificacion Fila
	 * 
	 * @return
	 */
	@Temporal(TemporalType.TIME)
	@Column(name = "hormod", length = 8)
	public Date getHormod() {
		return this.hormod;
	}

	public void setHormod(Date hormod) {
		this.hormod = hormod;
	}

	/**
	 * Derechos-Imagen-Num. Perceptores
	 * 
	 * @return
	 */
	@Column(name = "imgper", length = 2)
	public Integer getImgper() {
		return this.imgper;
	}

	public void setImgper(Integer imgper) {
		this.imgper = imgper;
	}

	/**
	 * Derechos-Imagen-Importe
	 * 
	 * @return
	 */
	@Column(name = "imgimp", scale=2, precision=11)
	public BigDecimal getImgimp() {
		return this.imgimp;
	}

	public void setImgimp(BigDecimal imgimp) {
		this.imgimp = imgimp;
	}

	/**
	 * Derechos-Imagen-Retencion
	 * 
	 * @return
	 */
	@Column(name = "imgret", scale=2, precision=11)
	public BigDecimal getImgret() {
		return this.imgret;
	}

	public void setImgret(BigDecimal imgret) {
		this.imgret = imgret;
	}

	/**
	 * Modalidad declaraciones de impuestos
	 * @return
	 */
	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.ModalidadImpuesto")} )
	@Column(name = "modimpuesto", length = 1)
	public ModalidadImpuesto getModimpuesto() {
		return modimpuesto;
	}

	public void setModimpuesto(ModalidadImpuesto modimpuesto) {
		this.modimpuesto = modimpuesto;
	}

	
	/**
	 * Número de referencia completo
	 * @return
	 */
	@Column(name = "nrc", length = 22)
	public String getNrc() {
		return nrc;
	}

	public void setNrc(String nrc) {
		this.nrc = nrc;
	}

	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codadm")
	public Admon getAdmon() {
		return this.admon;
	}

	public void setAdmon(Admon admon) {
		this.admon = admon;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "divisa")
	public Divisa getDivisa() {
		return this.divisa;
	}

	public void setDivisa(Divisa divisa) {
		this.divisa = divisa;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codemp", nullable = false)
	public Empresa getEmprnif() {
		return this.emprnif;
	}

	public void setEmprnif(Empresa emprnif) {
		this.emprnif = emprnif;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "provincia")
	public Provincia getProvincia() {
		return this.provincia;
	}

	public void setProvincia(Provincia provincia) {
		this.provincia = provincia;
	}

}
