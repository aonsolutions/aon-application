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
import com.code.aon.payroll.cotizacion.Bonificacion;
import com.code.aon.payroll.enumeration.TipoImporte;
import com.code.aon.payroll.principales.persona.Trabajador;

/**
 * Mantenimiento de Bonifica
 */
@Entity
@Table(name = "bonifica")
public class Bonifica implements ITransferObject {

	private BonificaPK id;
	private Date fecfin;
	private Integer horas;
	private BigDecimal importe;
	private TipoImporte tipo;
	private Boolean prorrateo;
	private Trabajador emprper;
	private Bonificacion tipboni;

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "numero", column = @Column(name = "numero", nullable = false, length = 4)),
			@AttributeOverride(name = "cdg", column = @Column(name = "cdg", nullable = false, length = 2)),
			@AttributeOverride(name = "fecini", column = @Column(name = "fecini", nullable = false, length = 10)) })
	public BonificaPK getId() {
		return this.id;
	}

	public void setId(BonificaPK id) {
		this.id = id;
	}

	@Temporal(TemporalType.DATE)
	// @DataDefinition(label="Fecha Terminacion Bonificacion")
	@Column(name = "fecfin")
	public Date getFecfin() {
		return this.fecfin;
	}

	public void setFecfin(Date fecfin) {
		this.fecfin = fecfin;
	}

	// @DataDefinition(label="Numero Horas Formacion")
	@Column(name = "horas", nullable = false, length = 2)
	public Integer getHoras() {
		return this.horas;
	}

	public void setHoras(Integer horas) {
		this.horas = horas;
	}

	// @DataDefinition(label="Importe Bonificacion Directo")
	@Column(name = "importe", nullable = false, precision = 8)
	public BigDecimal getImporte() {
		return this.importe;
	}

	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}

	// @DataDefinition(label="Tipo importe")
	@Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.TipoImporte")} )
	@Column(name = "tipo", length = 1)
	public TipoImporte getTipo() {
		return this.tipo;
	}

	public void setTipo(TipoImporte tipo) {
		this.tipo = tipo;
	}

	// @DataDefinition(label="Prorrateo")
	@Type(type="siNoType" )
	@Column(name = "prorrateo", nullable = false, length = 1)
	public Boolean getProrrateo() {
		return this.prorrateo;
	}

	public void setProrrateo(Boolean prorrateo) {
		this.prorrateo = prorrateo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "numero", insertable = false, updatable = false)
	public Trabajador getEmprper() {
		return this.emprper;
	}

	public void setEmprper(Trabajador emprper) {
		this.emprper = emprper;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cdg", insertable = false, updatable = false)
	public Bonificacion getTipboni() {
		return this.tipboni;
	}

	public void setTipboni(Bonificacion tipboni) {
		this.tipboni = tipboni;
	}

}
