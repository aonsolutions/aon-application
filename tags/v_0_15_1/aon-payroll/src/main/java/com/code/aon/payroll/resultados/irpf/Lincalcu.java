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
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.payroll.enumeration.FijoVariable;

/**
 * Lincalcu
 */
@Entity
@Table(name = "lincalcu")
public class Lincalcu implements ITransferObject {

	private LincalcuPK id;
	private String codcom;
	private String descom;
	private Date fecini;
	private FijoVariable fijovar;
	private BigDecimal importeUni;
	private BigDecimal unidades;
	private BigDecimal importe;
	private Calculo calculo;
	

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "numero", column = @Column(name = "numero", nullable = false, length = 4)),
			@AttributeOverride(name = "anio", column = @Column(name = "anio", nullable = false, length = 2)),
			@AttributeOverride(name = "mes", column = @Column(name = "mes", nullable = false, length = 2)),
			@AttributeOverride(name = "dia", column = @Column(name = "dia", nullable = false, length = 2)),
			@AttributeOverride(name = "linea", column = @Column(name = "linea", nullable = false, length = 2)) })
	public LincalcuPK getId() {
		return this.id;
	}

	public void setId(LincalcuPK id) {
		this.id = id;
	}

	/**
	 * Codigo Complemento
	 * 
	 * @return
	 */
	@Column(name = "codcom", length = 2)
	public String getCodcom() {
		return this.codcom;
	}

	public void setCodcom(String codcom) {
		this.codcom = codcom;
	}

	/**
	 * Descripcion Complemento
	 * 
	 * @return
	 */
	@Column(name = "descom", length = 50)
	public String getDescom() {
		return this.descom;
	}

	public void setDescom(String descom) {
		this.descom = descom;
	}

	/**
	 * Fecha de Inicio del complemento
	 * 
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "fecini", length = 10)
	public Date getFecini() {
		return this.fecini;
	}

	public void setFecini(Date fecini) {
		this.fecini = fecini;
	}

	/**
	 * Fijo o Variable
	 * 
	 * @return
	 */
	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.code.aon.payroll.enumeration.FijoVariable") })
	@Column(name = "fijovar", length = 1)
	public FijoVariable getFijovar() {
		return this.fijovar;
	}

	public void setFijovar(FijoVariable fijovar) {
		this.fijovar = fijovar;
	}

	/**
	 * Importe Unitario
	 * 
	 * @return
	 */
	@Column(name = "importe_uni", nullable = false, precision = 13)
	public BigDecimal getImporteUni() {
		return this.importeUni;
	}

	public void setImporteUni(BigDecimal importeUni) {
		this.importeUni = importeUni;
	}

	/**
	 * Meses /Dias
	 * 
	 * @return
	 */
	@Column(name = "unidades", nullable = false, precision = 10)
	public BigDecimal getUnidades() {
		return this.unidades;
	}

	public void setUnidades(BigDecimal unidades) {
		this.unidades = unidades;
	}

	/**
	 * Importe
	 * 
	 * @return
	 */
	@Column(name = "importe", nullable = false, precision = 13)
	public BigDecimal getImporte() {
		return this.importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumns({
		@JoinColumn(name = "numero",referencedColumnName = "cdg", insertable = false, updatable = false),
		@JoinColumn(name = "anio",referencedColumnName = "anio", insertable = false, updatable = false),
		@JoinColumn(name = "mes",referencedColumnName = "mes", insertable = false, updatable = false),
		@JoinColumn(name = "dia",referencedColumnName = "dia", insertable = false, updatable = false)
	})
	public Calculo getCalculo() {
		return this.calculo;
	}

	public void setCalculo(Calculo calculo) {
		this.calculo = calculo;
	}

}
