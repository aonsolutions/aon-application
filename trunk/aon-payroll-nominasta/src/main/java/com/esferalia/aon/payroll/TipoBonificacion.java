package com.esferalia.aon.payroll;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.core.cotizacion.ITipoBonificacion;

/**
 * Tipos de Bonificacion
 */
@Entity
@Table(name = "tipboni")
public class TipoBonificacion implements ITransferObject, ITipoBonificacion {

	private static final long serialVersionUID = 3418444674669580634L;
	
	private Integer id;
	private String description;
	// private FormaCalculo calculo;
	private Double porcentajeContingenciasGenerales;
	private Double porcentajeAccidentes;
	private Double porcentajeBaseConjunto;
	private String porcentajeBonificacionSS;
	private String mayor60;
	private String realDecretoLey052006;
	private String restarIT;

	@Id
	@Column(name = "cdg", unique = true, nullable = false, length = 2)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "descripcion", nullable = false, length = 50)
	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public void setDescription(String descripcion) {
		this.description = descripcion;
	}

	// /**
	// * Devuelve la forma de calculo del tipo de bonificacion.
	// *
	// * @return
	// */
	// @Type(type="stringEnum",parameters= { @Parameter(name="enumClassname",
	// value="com.code.aon.payroll.enumeration.FormaCalculo")} )
	// @Column(name="calculo", length=1)
	// public FormaCalculo getCalculo() {
	// return this.calculo;
	// }
	// 
	// public void setCalculo(FormaCalculo calculo) {
	// this.calculo = calculo;
	// }
	// 

	@Column(name = "prc_cg", nullable = false, scale = 2, precision = 5)
	@Override
	public Double getPorcentajeContingenciasGenerales() {
		return this.porcentajeContingenciasGenerales;
	}

	@Override
	public void setPorcentajeContingenciasGenerales(
			Double porcentajeContingenciasGenerales) {
		this.porcentajeContingenciasGenerales = porcentajeContingenciasGenerales;
	}

	@Column(name = "prc_acc", nullable = false, scale = 2, precision = 5)
	@Override
	public Double getPorcentajeAccidentes() {
		return this.porcentajeAccidentes;
	}

	@Override
	public void setPorcentajeAccidentes(Double porcentajeAccidentes) {
		this.porcentajeAccidentes = porcentajeAccidentes;
	}

	@Column(name = "prc_accfgs", nullable = false, scale = 2, precision = 5)
	@Override
	public Double getPorcentajeBaseConjunto() {
		return this.porcentajeBaseConjunto;
	}

	@Override
	public void setPorcentajeBaseConjunto(Double porcentajeBaseConjunto) {
		this.porcentajeBaseConjunto = porcentajeBaseConjunto;
	}

	@Column(name = "boniss", length = 1)
	@Override
	public String getPorcentajeBonificacionSS() {
		return this.porcentajeBonificacionSS;
	}

	@Override
	public void setPorcentajeBonificacionSS(String porcentajeBonificacionSS) {
		this.porcentajeBonificacionSS = porcentajeBonificacionSS;
	}

	@Column(name = "mayor60", length = 1)
	@Override
	public String getMayor60() {
		return this.mayor60;
	}

	@Override
	public void setMayor60(String mayor60) {
		this.mayor60 = mayor60;
	}

	@Column(name = "rdl052006", length = 1)
	@Override
	public String getRealDecretoLey052006() {
		return this.realDecretoLey052006;
	}

	@Override
	public void setRealDecretoLey052006(String realDecretoLey052006) {
		this.realDecretoLey052006 = realDecretoLey052006;
	}

	@Column(name = "restait", length = 1)
	@Override
	public String getRestarIT() {
		return this.restarIT;
	}

	@Override
	public void setRestarIT(String restarIT) {
		this.restarIT = restarIT;
	}

	// //TODO A la espera de implementar un SelectBooleanCheckboxRenderer.
	// @Transient
	// public Boolean getBonissbol() {
	// return (getBoniss() != null && getBoniss().equals("S")?true:false );
	// }
	// public void setBonissbol(Boolean bol) {
	// setBoniss( (bol!=null && bol)? "S":"N" );
	// }
	//
	// @Transient
	// public Boolean getMayor60bol() {
	// return (getMayor60() != null && getMayor60().equals("S")?true:false );
	// }
	// public void setMayor60bol(Boolean bol) {
	// setMayor60( (bol!=null && bol)? "S":"N" );
	// }
	//
	// @Transient
	// public Boolean getRdl052006bol() {
	// return (getRdl052006() != null && getRdl052006().equals("S")?true:false
	// );
	// }
	// public void setRdl052006bol(Boolean bol) {
	// setRdl052006( (bol!=null && bol)? "S":"N" );
	// }
	//
	// @Transient
	// public Boolean getRestaitbol() {
	// return (getRestait() != null && getRestait().equals("S")?true:false );
	// }
	// public void setRestaitbol(Boolean bol) {
	// setRestait( (bol!=null && bol)? "S":"N" );
	// }
	// // ******************************************************************

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
