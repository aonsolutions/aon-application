package com.code.aon.payroll.principales.personas;

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

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.payroll.enumeration.Afectados;
import com.code.aon.payroll.enumeration.ImporteIndicar;
import com.code.aon.payroll.principales.persona.Trabajador;

/**
 * Mantenimiento de Trabdto
 */
@Entity
// @Lines(joinProperty="emprper")
@Table(name = "trabdto")
public class Trabdto implements ITransferObject {

	private TrabdtoPK id;
	private Date fecini;
	private Date fecfin;
	private Integer linea;
	private String concepto;
	private Afectados afecta;
	private BigDecimal importe;
	private ImporteIndicar indimp;
	private Date fecnew;
	private Date hornew;
	private Date fecmod;
	private Date hormod;
	private Trabajador emprper;

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "cdg", column = @Column(name = "cdg", nullable = false, length = 4)),
			@AttributeOverride(name = "orden", column = @Column(name = "orden", nullable = false, length = 2)) })
	public TrabdtoPK getId() {
		return this.id;
	}

	public void setId(TrabdtoPK id) {
		this.id = id;
	}

	@Temporal(TemporalType.DATE)
	// @DataDefinition(label="Fecha Inicio Vigencia")
	@Column(name = "fecini", nullable = false)
	public Date getFecini() {
		return this.fecini;
	}

	public void setFecini(Date fecini) {
		this.fecini = fecini;
	}

	@Temporal(TemporalType.DATE)
	// @DataDefinition(label="Fecha Fin Vigencia")
	@Column(name = "fecfin")
	public Date getFecfin() {
		return this.fecfin;
	}

	public void setFecfin(Date fecfin) {
		this.fecfin = fecfin;
	}

	// @DataDefinition(label="Numero de Linea en Nomina")
	@Column(name = "linea", nullable = false, length = 2)
	public Integer getLinea() {
		return this.linea;
	}

	public void setLinea(Integer linea) {
		this.linea = linea;
	}

	// @DataDefinition(label="Concepto de Minoracion",descriptionColumn=true)
	@Column(name = "concepto", nullable = false, length = 30)
	public String getConcepto() {
		return this.concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	// @DataDefinition(label="Nominas a las que afecta")
	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.Afectados")} )
	@Column(name = "afecta", nullable = false, length = 1)
	public Afectados getAfecta() {
		return this.afecta;
	}

	public void setAfecta(Afectados afecta) {
		this.afecta = afecta;
	}

	// @DataDefinition(label="Importe Minoracion")
	@Column(name = "importe", nullable = false, precision = 10)
	public BigDecimal getImporte() {
		return this.importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	// @DataDefinition(label="Importe a Indicar")
	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.ImporteIndicar")} )
	@Column(name = "indimp", length = 1)
	public ImporteIndicar getIndimp() {
		return this.indimp;
	}

	public void setIndimp(ImporteIndicar indimp) {
		this.indimp = indimp;
	}

	@Temporal(TemporalType.DATE)
	// @DataDefinition(label="Fecha Creacion Fila")
	@Column(name = "fecnew")
	public Date getFecnew() {
		return this.fecnew;
	}

	public void setFecnew(Date fecnew) {
		this.fecnew = fecnew;
	}

	@Temporal(TemporalType.TIME)
	// @DataDefinition(label="Hora Creacion Fila")
	@Column(name = "hornew")
	public Date getHornew() {
		return this.hornew;
	}

	public void setHornew(Date hornew) {
		this.hornew = hornew;
	}

	@Temporal(TemporalType.DATE)
	// @DataDefinition(label="Fecha Modificacion Fila")
	@Column(name = "fecmod")
	public Date getFecmod() {
		return this.fecmod;
	}

	public void setFecmod(Date fecmod) {
		this.fecmod = fecmod;
	}

	@Temporal(TemporalType.TIME)
	// @DataDefinition(label="Hora Modificacion Fila")
	@Column(name = "hormod")
	public Date getHormod() {
		return this.hormod;
	}

	public void setHormod(Date hormod) {
		this.hormod = hormod;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cdg", insertable = false, updatable = false)
	public Trabajador getEmprper() {
		return this.emprper;
	}

	public void setEmprper(Trabajador emprper) {
		this.emprper = emprper;
	}

}
