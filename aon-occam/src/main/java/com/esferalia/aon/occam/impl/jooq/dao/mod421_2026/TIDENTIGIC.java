//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.1-b171012.0423 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.02.26 a las 01:00:22 PM CET 
//


package com.esferalia.aon.occam.impl.jooq.dao.mod421_2026;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Datos identificativos y situación tributaria
 * 
 * <p>Clase Java para T_IDENT_IGIC complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_IDENT_IGIC"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="OTP" type="{}DATOS_PERSONALES"/&gt;
 *         &lt;element name="ENT" type="{}T_ENTIDAD_IGIC" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="ACO" type="{}SINOType" /&gt;
 *       &lt;attribute name="RDM" type="{}SINOType" /&gt;
 *       &lt;attribute name="RECC" type="{}SINOType" /&gt;
 *       &lt;attribute name="DRECC" type="{}SINOType" /&gt;
 *       &lt;attribute name="EOP" type="{}SINOType" /&gt;
 *       &lt;attribute name="ACR" type="{}SINOType" /&gt;
 *       &lt;attribute name="FAC"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;pattern value="\d{2}/\d{2}/\d{4}"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="TAC" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="TRA" type="{}SINOType" /&gt;
 *       &lt;attribute name="VAO" type="{}SINOType" /&gt;
 *       &lt;attribute name="GEM" type="{}SINOType" /&gt;
 *       &lt;attribute name="RPE" type="{}SINOType" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "T_IDENT_IGIC", propOrder = {
    "otp",
    "ent"
})
public class TIDENTIGIC {

    @XmlElement(name = "OTP", required = true)
    protected DATOSPERSONALES otp;
    @XmlElement(name = "ENT")
    protected TENTIDADIGIC ent;
    @XmlAttribute(name = "ACO")
    protected SINOType aco;
    @XmlAttribute(name = "RDM")
    protected SINOType rdm;
    @XmlAttribute(name = "RECC")
    protected SINOType recc;
    @XmlAttribute(name = "DRECC")
    protected SINOType drecc;
    @XmlAttribute(name = "EOP")
    protected SINOType eop;
    @XmlAttribute(name = "ACR")
    protected SINOType acr;
    @XmlAttribute(name = "FAC")
    protected String fac;
    @XmlAttribute(name = "TAC")
    protected String tac;
    @XmlAttribute(name = "TRA")
    protected SINOType tra;
    @XmlAttribute(name = "VAO")
    protected SINOType vao;
    @XmlAttribute(name = "GEM")
    protected SINOType gem;
    @XmlAttribute(name = "RPE")
    protected SINOType rpe;

    /**
     * Obtiene el valor de la propiedad otp.
     * 
     * @return
     *     possible object is
     *     {@link DATOSPERSONALES }
     *     
     */
    public DATOSPERSONALES getOTP() {
        return otp;
    }

    /**
     * Define el valor de la propiedad otp.
     * 
     * @param value
     *     allowed object is
     *     {@link DATOSPERSONALES }
     *     
     */
    public void setOTP(DATOSPERSONALES value) {
        this.otp = value;
    }

    /**
     * Obtiene el valor de la propiedad ent.
     * 
     * @return
     *     possible object is
     *     {@link TENTIDADIGIC }
     *     
     */
    public TENTIDADIGIC getENT() {
        return ent;
    }

    /**
     * Define el valor de la propiedad ent.
     * 
     * @param value
     *     allowed object is
     *     {@link TENTIDADIGIC }
     *     
     */
    public void setENT(TENTIDADIGIC value) {
        this.ent = value;
    }

    /**
     * Obtiene el valor de la propiedad aco.
     * 
     * @return
     *     possible object is
     *     {@link SINOType }
     *     
     */
    public SINOType getACO() {
        return aco;
    }

    /**
     * Define el valor de la propiedad aco.
     * 
     * @param value
     *     allowed object is
     *     {@link SINOType }
     *     
     */
    public void setACO(SINOType value) {
        this.aco = value;
    }

    /**
     * Obtiene el valor de la propiedad rdm.
     * 
     * @return
     *     possible object is
     *     {@link SINOType }
     *     
     */
    public SINOType getRDM() {
        return rdm;
    }

    /**
     * Define el valor de la propiedad rdm.
     * 
     * @param value
     *     allowed object is
     *     {@link SINOType }
     *     
     */
    public void setRDM(SINOType value) {
        this.rdm = value;
    }

    /**
     * Obtiene el valor de la propiedad recc.
     * 
     * @return
     *     possible object is
     *     {@link SINOType }
     *     
     */
    public SINOType getRECC() {
        return recc;
    }

    /**
     * Define el valor de la propiedad recc.
     * 
     * @param value
     *     allowed object is
     *     {@link SINOType }
     *     
     */
    public void setRECC(SINOType value) {
        this.recc = value;
    }

    /**
     * Obtiene el valor de la propiedad drecc.
     * 
     * @return
     *     possible object is
     *     {@link SINOType }
     *     
     */
    public SINOType getDRECC() {
        return drecc;
    }

    /**
     * Define el valor de la propiedad drecc.
     * 
     * @param value
     *     allowed object is
     *     {@link SINOType }
     *     
     */
    public void setDRECC(SINOType value) {
        this.drecc = value;
    }

    /**
     * Obtiene el valor de la propiedad eop.
     * 
     * @return
     *     possible object is
     *     {@link SINOType }
     *     
     */
    public SINOType getEOP() {
        return eop;
    }

    /**
     * Define el valor de la propiedad eop.
     * 
     * @param value
     *     allowed object is
     *     {@link SINOType }
     *     
     */
    public void setEOP(SINOType value) {
        this.eop = value;
    }

    /**
     * Obtiene el valor de la propiedad acr.
     * 
     * @return
     *     possible object is
     *     {@link SINOType }
     *     
     */
    public SINOType getACR() {
        return acr;
    }

    /**
     * Define el valor de la propiedad acr.
     * 
     * @param value
     *     allowed object is
     *     {@link SINOType }
     *     
     */
    public void setACR(SINOType value) {
        this.acr = value;
    }

    /**
     * Obtiene el valor de la propiedad fac.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFAC() {
        return fac;
    }

    /**
     * Define el valor de la propiedad fac.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFAC(String value) {
        this.fac = value;
    }

    /**
     * Obtiene el valor de la propiedad tac.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTAC() {
        return tac;
    }

    /**
     * Define el valor de la propiedad tac.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTAC(String value) {
        this.tac = value;
    }

    /**
     * Obtiene el valor de la propiedad tra.
     * 
     * @return
     *     possible object is
     *     {@link SINOType }
     *     
     */
    public SINOType getTRA() {
        return tra;
    }

    /**
     * Define el valor de la propiedad tra.
     * 
     * @param value
     *     allowed object is
     *     {@link SINOType }
     *     
     */
    public void setTRA(SINOType value) {
        this.tra = value;
    }

    /**
     * Obtiene el valor de la propiedad vao.
     * 
     * @return
     *     possible object is
     *     {@link SINOType }
     *     
     */
    public SINOType getVAO() {
        return vao;
    }

    /**
     * Define el valor de la propiedad vao.
     * 
     * @param value
     *     allowed object is
     *     {@link SINOType }
     *     
     */
    public void setVAO(SINOType value) {
        this.vao = value;
    }

    /**
     * Obtiene el valor de la propiedad gem.
     * 
     * @return
     *     possible object is
     *     {@link SINOType }
     *     
     */
    public SINOType getGEM() {
        return gem;
    }

    /**
     * Define el valor de la propiedad gem.
     * 
     * @param value
     *     allowed object is
     *     {@link SINOType }
     *     
     */
    public void setGEM(SINOType value) {
        this.gem = value;
    }

    /**
     * Obtiene el valor de la propiedad rpe.
     * 
     * @return
     *     possible object is
     *     {@link SINOType }
     *     
     */
    public SINOType getRPE() {
        return rpe;
    }

    /**
     * Define el valor de la propiedad rpe.
     * 
     * @param value
     *     allowed object is
     *     {@link SINOType }
     *     
     */
    public void setRPE(SINOType value) {
        this.rpe = value;
    }

}
