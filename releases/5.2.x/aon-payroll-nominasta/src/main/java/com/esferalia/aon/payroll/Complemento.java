package com.esferalia.aon.payroll;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.core.IComplemento;

@Entity
@Table(name = "complemento")
public class Complemento implements IComplemento, ITransferObject {

	private static final long serialVersionUID = -6860234498375858900L;
	
	private String cdg;
//     private TipoCotizaciones tipcot;
//     private String description;
//     private String desabr;
//     private TipoComplemento tipcom;
//     private FijoVariable fijovar;
//     private IndiceComplemento indcom;
//     private Retribuciones dinesp;

    @Id     
    @Column(name="cdg", unique=true, nullable=false, length=2)
    @Override
    public String getCdg() {
        return this.cdg;
    }
   
    @Override
    public void setCdg(String cdg) {
        this.cdg = cdg;
    }
   
//    /**
//     * Devuelve el Tipo de Cotizacion
//     * 
//     * @return
//     */
//    @Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.TipoCotizaciones")} )
//    @Column(name="tipcot", length=1)
//    public TipoCotizaciones getTipcot() {
//        return this.tipcot;
//    }
//
//    public void setTipcot(TipoCotizaciones tipcot) {
//        this.tipcot = tipcot;
//    }
//
//    /**
//     * Devuelve la Descripcion de Complemento
//     * 
//     * @return
//     */
//    @Column(name="descripcion", length=50)
//    public String getDescription() {
//        return this.description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    /**
//     * Devuelve la Descripcion Abreviada de Complemento
//     * @return
//     */
//    @Column(name="desabr", length=15)
//    public String getDesabr() {
//        return this.desabr;
//    }
//
//    public void setDesabr(String desabr) {
//        this.desabr = desabr;
//    }
//
//    /**
//     * Devuelve el Tipo de Complemento
//     * 
//     * @return
//     */
//    @Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.TipoComplemento")} )
//    @Column(name="tipcom", nullable=false, length=1)
//    public TipoComplemento getTipcom() {
//        return this.tipcom;
//    }
//
//    public void setTipcom(TipoComplemento tipcom) {
//        this.tipcom = tipcom;
//    }
//
//    /**
//     * Devuelve si es Fijo o Variable
//     * 
//     * @return
//     */
//    @Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.FijoVariable")} )
//    @Column(name="fijovar", length=1)
//    public FijoVariable getFijovar() {
//        return this.fijovar;
//    }
//
//    public void setFijovar(FijoVariable fijovar) {
//        this.fijovar = fijovar;
//    }
//
//    /**
//     * Devuelve el Indicador de Complemento
//     * 
//     * @return
//     */
//    @Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.IndiceComplemento")} )
//    @Column(name="indcom", length=1)
//    public IndiceComplemento getIndcom() {
//        return this.indcom;
//    }
//
//    public void setIndcom(IndiceComplemento indcom) {
//        this.indcom = indcom;
//    }
//
//    /**
//     * Devuelve si es Dinerario o en Especie
//     * @return
//     */
//    @Type(type="stringEnum",parameters= { @Parameter(name="enumClassname", value="com.code.aon.payroll.enumeration.Retribuciones")} )
//    @Column(name="dinesp", nullable=false, length=1)
//    public Retribuciones getDinesp() {
//        return this.dinesp;
//    }
//
//    public void setDinesp(Retribuciones dinesp) {
//        this.dinesp = dinesp;
//    }

}




