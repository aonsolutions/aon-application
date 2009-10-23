package com.code.aon.payroll.resultados.irpf;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;

/**
 * LinImpresos190
 */
@Entity
@Table(name = "lin190")
public class LinImpresos190 implements ITransferObject {

	private LinImpresos190PK id;
	private String numdoc;
	private String clave;
	private String subclave;
	private BigDecimal impPerDin;
	private BigDecimal impRetDin;
	private BigDecimal impPerEsp;
	private BigDecimal impIngCta;
	private BigDecimal impIngRep;
	private Integer devengo;
	private Integer anionac;
	private Integer xminus;
	private Integer sitfam;
	private String nifcony;
	private Integer hijo3;
	private Integer hijo16;
	private Integer hijo25;
	private Integer minus33;
	private Integer minus65;
	private Integer hijos;
	private Integer relacion;
	private BigDecimal impReducc;
	private BigDecimal impGastos;
	private BigDecimal impPension;
	private BigDecimal impAnual;
	private String nomapel;
	private String provincia;
	private String cM;
	private Integer descentero;
	private Integer totalasc;
	private Integer ascentero;
	private Integer ascminus33;
	private Integer ascminus65;
	private Date movilidad;
	private Boolean prolongacion;
	private Integer descme3;
	private Integer descme3e;
	private Integer descma3;
	private Integer descma3e;
	private Integer descdi33;
	private Integer descdi33e;
	private Integer descdimr;
	private Integer descdimre;
	private Integer descdi65;
	private Integer descdi65e;
	private Integer ascme75;
	private Integer ascme75e;
	private Integer ascma75;
	private Integer ascma75e;
	private Integer ascdi33;
	private Integer ascdi33e;
	private Integer ascdimr;
	private Integer ascdimre;
	private Integer ascdi65;
	private Integer ascdi65e;
	private Impresos190 impresos190;

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "cdg", column = @Column(name = "cdg", nullable = false, length = 4)),
			@AttributeOverride(name = "linea", column = @Column(name = "linea", nullable = false, length = 2)) })
	public LinImpresos190PK getId() {
		return this.id;
	}

	public void setId(LinImpresos190PK id) {
		this.id = id;
	}

	/**
	 * NIF Perceptor
	 * 
	 * @return
	 */
	@Column(name = "numdoc", nullable = false, length = 10)
	public String getNumdoc() {
		return this.numdoc;
	}

	public void setNumdoc(String numdoc) {
		this.numdoc = numdoc;
	}

	/**
	 * Clave Percepcion
	 * 
	 * @return
	 */
	@Column(name = "clave", length = 1)
	public String getClave() {
		return this.clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	/**
	 * Subclave Percepcion
	 * 
	 * @return
	 */
	@Column(name = "subclave", length = 2)
	public String getSubclave() {
		return this.subclave;
	}

	public void setSubclave(String subclave) {
		this.subclave = subclave;
	}

	/**
	 * Percepcion Dineraria
	 * 
	 * @return
	 */
	@Column(name = "imp_per_din", nullable = false, precision = 11)
	public BigDecimal getImpPerDin() {
		return this.impPerDin;
	}

	public void setImpPerDin(BigDecimal impPerDin) {
		this.impPerDin = impPerDin;
	}

	/**
	 * Importe Retencion Dineraria
	 * 
	 * @return
	 */
	@Column(name = "imp_ret_din", nullable = false, precision = 11)
	public BigDecimal getImpRetDin() {
		return this.impRetDin;
	}

	public void setImpRetDin(BigDecimal impRetDin) {
		this.impRetDin = impRetDin;
	}

	/**
	 * Importe Percepcion En Especie
	 * 
	 * @return
	 */
	@Column(name = "imp_per_esp", nullable = false, precision = 11)
	public BigDecimal getImpPerEsp() {
		return this.impPerEsp;
	}

	public void setImpPerEsp(BigDecimal impPerEsp) {
		this.impPerEsp = impPerEsp;
	}

	/**
	 * Importe Ingresos a Cuenta
	 * 
	 * @return
	 */
	@Column(name = "imp_ing_cta", nullable = false, precision = 11)
	public BigDecimal getImpIngCta() {
		return this.impIngCta;
	}

	public void setImpIngCta(BigDecimal impIngCta) {
		this.impIngCta = impIngCta;
	}

	/**
	 * Importe Ingresos Repercutidos
	 * 
	 * @return
	 */
	@Column(name = "imp_ing_rep", nullable = false, precision = 11)
	public BigDecimal getImpIngRep() {
		return this.impIngRep;
	}

	public void setImpIngRep(BigDecimal impIngRep) {
		this.impIngRep = impIngRep;
	}

	/**
	 * Ejercicio Devengo
	 * 
	 * @return
	 */
	@Column(name = "devengo", length = 2)
	public Integer getDevengo() {
		return this.devengo;
	}

	public void setDevengo(Integer devengo) {
		this.devengo = devengo;
	}

	/**
	 * Anio Nacimiento
	 * 
	 * @return
	 */
	@Column(name = "anionac", length = 2)
	public Integer getAnionac() {
		return this.anionac;
	}

	public void setAnionac(Integer anionac) {
		this.anionac = anionac;
	}

	/**
	 * Grado de Minusvalia
	 * 
	 * @return
	 */
	@Column(name = "xminus", length = 2)
	public Integer getXminus() {
		return this.xminus;
	}

	public void setXminus(Integer xminus) {
		this.xminus = xminus;
	}

	/**
	 * Situacion Familiar
	 * 
	 * @return
	 */
	@Column(name = "sitfam", length = 2)
	public Integer getSitfam() {
		return this.sitfam;
	}

	public void setSitfam(Integer sitfam) {
		this.sitfam = sitfam;
	}

	/**
	 * NIF Conyuge
	 * 
	 * @return
	 */
	@Column(name = "nifcony", length = 10)
	public String getNifcony() {
		return this.nifcony;
	}

	public void setNifcony(String nifcony) {
		this.nifcony = nifcony;
	}

	/**
	 * Hijos menores de 3 anios
	 * 
	 * @return
	 */
	@Column(name = "hijo_3", length = 2)
	public Integer getHijo3() {
		return this.hijo3;
	}

	public void setHijo3(Integer hijo3) {
		this.hijo3 = hijo3;
	}

	/**
	 * Hijos entre 3 y 16
	 * 
	 * @return
	 */
	@Column(name = "hijo_16", length = 2)
	public Integer getHijo16() {
		return this.hijo16;
	}

	public void setHijo16(Integer hijo16) {
		this.hijo16 = hijo16;
	}

	/**
	 * Hijos entre 16 y 25
	 * 
	 * @return
	 */
	@Column(name = "hijo_25", length = 2)
	public Integer getHijo25() {
		return this.hijo25;
	}

	public void setHijo25(Integer hijo25) {
		this.hijo25 = hijo25;
	}

	/**
	 * Hijos Discapacitados entre 33 y 65
	 * 
	 * @return
	 */
	@Column(name = "minus_33", length = 2)
	public Integer getMinus33() {
		return this.minus33;
	}

	public void setMinus33(Integer minus33) {
		this.minus33 = minus33;
	}

	/**
	 * Hijos Discapacitados mas de 65
	 * 
	 * @return
	 */
	@Column(name = "minus_65", length = 2)
	public Integer getMinus65() {
		return this.minus65;
	}

	public void setMinus65(Integer minus65) {
		this.minus65 = minus65;
	}

	/**
	 * Numero total de hijos
	 * 
	 * @return
	 */
	@Column(name = "hijos", length = 2)
	public Integer getHijos() {
		return this.hijos;
	}

	public void setHijos(Integer hijos) {
		this.hijos = hijos;
	}

	/**
	 * Tipo de Relacion
	 * 
	 * @return
	 */
	@Column(name = "relacion", length = 2)
	public Integer getRelacion() {
		return this.relacion;
	}

	public void setRelacion(Integer relacion) {
		this.relacion = relacion;
	}

	/**
	 * Importe Reducciones
	 * 
	 * @return
	 */
	@Column(name = "imp_reducc", nullable = false, precision = 11)
	public BigDecimal getImpReducc() {
		return this.impReducc;
	}

	public void setImpReducc(BigDecimal impReducc) {
		this.impReducc = impReducc;
	}

	/**
	 * Importe Gastos
	 * 
	 * @return
	 */
	@Column(name = "imp_gastos", nullable = false, precision = 11)
	public BigDecimal getImpGastos() {
		return this.impGastos;
	}

	public void setImpGastos(BigDecimal impGastos) {
		this.impGastos = impGastos;
	}

	/**
	 * Importe Pension Compensatoria
	 * 
	 * @return
	 */
	@Column(name = "imp_pension", nullable = false, precision = 11)
	public BigDecimal getImpPension() {
		return this.impPension;
	}

	public void setImpPension(BigDecimal impPension) {
		this.impPension = impPension;
	}

	/**
	 * Importe Anualidades
	 * 
	 * @return
	 */
	@Column(name = "imp_anual", nullable = false, precision = 11)
	public BigDecimal getImpAnual() {
		return this.impAnual;
	}

	public void setImpAnual(BigDecimal impAnual) {
		this.impAnual = impAnual;
	}

	/**
	 * Nombre y Apellidos
	 * 
	 * @return
	 */
	@Column(name = "nomapel", length = 85)
	public String getNomapel() {
		return this.nomapel;
	}

	public void setNomapel(String nomapel) {
		this.nomapel = nomapel;
	}

	/**
	 * Provincia
	 * 
	 * @return
	 */
	@Column(name = "provincia", length = 2)
	public String getProvincia() {
		return this.provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	/**
	 * Ceuta / Melilla
	 * 
	 * @return
	 */
	@Column(name = "c_m", length = 1)
	public String getcM() {
		return this.cM;
	}

	public void setcM(String cM) {
		this.cM = cM;
	}

	/**
	 * Total Descendientes por Entero
	 * 
	 * @return
	 */
	@Column(name = "descentero", length = 2)
	public Integer getDescentero() {
		return this.descentero;
	}

	public void setDescentero(Integer descentero) {
		this.descentero = descentero;
	}

	/**
	 * Total Ascendientes
	 * 
	 * @return
	 */
	@Column(name = "totalasc", length = 2)
	public Integer getTotalasc() {
		return this.totalasc;
	}

	public void setTotalasc(Integer totalasc) {
		this.totalasc = totalasc;
	}

	/**
	 * Total Ascendentes por Entero
	 * 
	 * @return
	 */
	@Column(name = "ascentero", length = 2)
	public Integer getAscentero() {
		return this.ascentero;
	}

	public void setAscentero(Integer ascentero) {
		this.ascentero = ascentero;
	}

	/**
	 * Ascendientes Discapacitados entre 33 y 65
	 * 
	 * @return
	 */
	@Column(name = "ascminus_33", length = 2)
	public Integer getAscminus33() {
		return this.ascminus33;
	}

	public void setAscminus33(Integer ascminus33) {
		this.ascminus33 = ascminus33;
	}

	/**
	 * Ascendientes Discapacitados mas de 65
	 * 
	 * @return
	 */
	@Column(name = "ascminus_65", length = 2)
	public Integer getAscminus65() {
		return this.ascminus65;
	}

	public void setAscminus65(Integer ascminus65) {
		this.ascminus65 = ascminus65;
	}

	/**
	 * Movilidad geográfica
	 * 
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "movilidad", length = 10)
	public Date getMovilidad() {
		return this.movilidad;
	}

	public void setMovilidad(Date movilidad) {
		this.movilidad = movilidad;
	}

	/**
	 * Prolongación actividad laboral
	 * 
	 * @return
	 */
	@Type(type="siNoType" )
	@Column(name = "prolongacion", length = 1)
	public Boolean getProlongacion() {
		return this.prolongacion;
	}

	public void setProlongacion(Boolean prolongacion) {
		this.prolongacion = prolongacion;
	}

	/**
	 * Descendientes Menores de 3 años
	 * 
	 * @return
	 */
	@Column(name = "descme3", length = 2)
	public Integer getDescme3() {
		return this.descme3;
	}

	public void setDescme3(Integer descme3) {
		this.descme3 = descme3;
	}

	/**
	 * Descendientes Menores de 3 años Enteros
	 * 
	 * @return
	 */
	@Column(name = "descme3e", length = 2)
	public Integer getDescme3e() {
		return this.descme3e;
	}

	public void setDescme3e(Integer descme3e) {
		this.descme3e = descme3e;
	}

	/**
	 * Descendientes Mayores de 3 años
	 * 
	 * @return
	 */
	@Column(name = "descma3", length = 2)
	public Integer getDescma3() {
		return this.descma3;
	}

	public void setDescma3(Integer descma3) {
		this.descma3 = descma3;
	}

	/**
	 * Descendientes Mayores de 3 años Enteros
	 * 
	 * @return
	 */
	@Column(name = "descma3e", length = 2)
	public Integer getDescma3e() {
		return this.descma3e;
	}

	public void setDescma3e(Integer descma3e) {
		this.descma3e = descma3e;
	}

	/**
	 * Descendientes Discapacitados >=33% <65%
	 * 
	 * @return
	 */
	@Column(name = "descdi33", length = 2)
	public Integer getDescdi33() {
		return this.descdi33;
	}

	public void setDescdi33(Integer descdi33) {
		this.descdi33 = descdi33;
	}

	/**
	 * Descendientes Discapacitados >=33% <65% Enteros
	 * 
	 * @return
	 */
	@Column(name = "descdi33e", length = 2)
	public Integer getDescdi33e() {
		return this.descdi33e;
	}

	public void setDescdi33e(Integer descdi33e) {
		this.descdi33e = descdi33e;
	}

	/**
	 * Descendientes Discapacitados Movilidad Reducida
	 * 
	 * @return
	 */
	@Column(name = "descdimr", length = 2)
	public Integer getDescdimr() {
		return this.descdimr;
	}

	public void setDescdimr(Integer descdimr) {
		this.descdimr = descdimr;
	}

	/**
	 * Descendientes Discapacitados Movilidad Reducida Enteros
	 * 
	 * @return
	 */
	@Column(name = "descdimre", length = 2)
	public Integer getDescdimre() {
		return this.descdimre;
	}

	public void setDescdimre(Integer descdimre) {
		this.descdimre = descdimre;
	}

	/**
	 * Descendientes Discapacitados >65%
	 * 
	 * @return
	 */
	@Column(name = "descdi65", length = 2)
	public Integer getDescdi65() {
		return this.descdi65;
	}

	public void setDescdi65(Integer descdi65) {
		this.descdi65 = descdi65;
	}

	/**
	 * Descendientes Discapacitados >65% Enteros
	 * 
	 * @return
	 */
	@Column(name = "descdi65e", length = 2)
	public Integer getDescdi65e() {
		return this.descdi65e;
	}

	public void setDescdi65e(Integer descdi65e) {
		this.descdi65e = descdi65e;
	}

	/**
	 * Ascendientes Menores de 75 años
	 * 
	 * @return
	 */
	@Column(name = "ascme75", length = 2)
	public Integer getAscme75() {
		return this.ascme75;
	}

	public void setAscme75(Integer ascme75) {
		this.ascme75 = ascme75;
	}

	/**
	 * Ascendientes Menores de 75 años Enteros
	 * 
	 * @return
	 */
	@Column(name = "ascme75e", length = 2)
	public Integer getAscme75e() {
		return this.ascme75e;
	}

	public void setAscme75e(Integer ascme75e) {
		this.ascme75e = ascme75e;
	}

	/**
	 * Ascendientes Mayores de 75 años
	 * 
	 * @return
	 */
	@Column(name = "ascma75", length = 2)
	public Integer getAscma75() {
		return this.ascma75;
	}

	public void setAscma75(Integer ascma75) {
		this.ascma75 = ascma75;
	}

	/**
	 * Ascendientes Mayores de 75 años Enteros
	 * 
	 * @return
	 */
	@Column(name = "ascma75e", length = 2)
	public Integer getAscma75e() {
		return this.ascma75e;
	}

	public void setAscma75e(Integer ascma75e) {
		this.ascma75e = ascma75e;
	}

	/**
	 * Ascendientes Discapacitados >=33% <65%
	 * 
	 * @return
	 */
	@Column(name = "ascdi33", length = 2)
	public Integer getAscdi33() {
		return this.ascdi33;
	}

	public void setAscdi33(Integer ascdi33) {
		this.ascdi33 = ascdi33;
	}

	/**
	 * Ascendientes Discapacitados >=33% <65% Enteros
	 * 
	 * @return
	 */
	@Column(name = "ascdi33e", length = 2)
	public Integer getAscdi33e() {
		return this.ascdi33e;
	}

	public void setAscdi33e(Integer ascdi33e) {
		this.ascdi33e = ascdi33e;
	}

	/**
	 * Ascendientes Discapacitados Movilidad Reducida
	 * 
	 * @return
	 */
	@Column(name = "ascdimr", length = 2)
	public Integer getAscdimr() {
		return this.ascdimr;
	}

	public void setAscdimr(Integer ascdimr) {
		this.ascdimr = ascdimr;
	}

	/**
	 * Ascendientes Discapacitados Movilidad Reducida Enteros
	 * 
	 * @return
	 */
	@Column(name = "ascdimre", length = 2)
	public Integer getAscdimre() {
		return this.ascdimre;
	}

	public void setAscdimre(Integer ascdimre) {
		this.ascdimre = ascdimre;
	}

	/**
	 * Ascendientes Discapacitados >65%
	 * 
	 * @return
	 */
	@Column(name = "ascdi65", length = 2)
	public Integer getAscdi65() {
		return this.ascdi65;
	}

	public void setAscdi65(Integer ascdi65) {
		this.ascdi65 = ascdi65;
	}

	/**
	 * Ascendientes Discapacitados >65% Enteros
	 * 
	 * @return
	 */
	@Column(name = "ascdi65e", length = 2)
	public Integer getAscdi65e() {
		return this.ascdi65e;
	}

	public void setAscdi65e(Integer ascdi65e) {
		this.ascdi65e = ascdi65e;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cdg", insertable = false, updatable = false)
	public Impresos190 getImpresos190() {
		return this.impresos190;
	}

	public void setImpresos190(Impresos190 impresos190) {
		this.impresos190 = impresos190;
	}

}
