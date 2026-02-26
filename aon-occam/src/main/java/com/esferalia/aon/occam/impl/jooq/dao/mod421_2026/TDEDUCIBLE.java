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
 * <p>Clase Java para T_DEDUCIBLE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_DEDUCIBLE"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="OIC" type="{}T_BASE_CUOTA" minOccurs="0"/&gt;
 *         &lt;element name="OII" type="{}T_BASE_CUOTA" minOccurs="0"/&gt;
 *         &lt;element name="IMC" type="{}T_BASE_CUOTA" minOccurs="0"/&gt;
 *         &lt;element name="IMI" type="{}T_BASE_CUOTA" minOccurs="0"/&gt;
 *         &lt;element name="RED" type="{}T_BASE_CUOTA" minOccurs="0"/&gt;
 *         &lt;element name="CRA" type="{}T_BASE_CUOTA" minOccurs="0"/&gt;
 *         &lt;element name="RBI" type="{}T_BASE_CUOTA" minOccurs="0"/&gt;
 *         &lt;element name="RIA" type="{}T_BASE_CUOTA" minOccurs="0"/&gt;
 *         &lt;element name="RPP" type="{}T_BASE_CUOTA" minOccurs="0"/&gt;
 *         &lt;element name="OGC" type="{}T_BASE_CUOTA" minOccurs="0"/&gt;
 *         &lt;element name="OGI" type="{}T_BASE_CUOTA" minOccurs="0"/&gt;
 *         &lt;element name="REG" type="{}T_BASE_CUOTA" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="TOT" type="{}IMPA15Type" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "T_DEDUCIBLE", propOrder = {
    "oic",
    "oii",
    "imc",
    "imi",
    "red",
    "cra",
    "rbi",
    "ria",
    "rpp",
    "ogc",
    "ogi",
    "reg"
})
public class TDEDUCIBLE {

    @XmlElement(name = "OIC")
    protected TBASECUOTA oic;
    @XmlElement(name = "OII")
    protected TBASECUOTA oii;
    @XmlElement(name = "IMC")
    protected TBASECUOTA imc;
    @XmlElement(name = "IMI")
    protected TBASECUOTA imi;
    @XmlElement(name = "RED")
    protected TBASECUOTA red;
    @XmlElement(name = "CRA")
    protected TBASECUOTA cra;
    @XmlElement(name = "RBI")
    protected TBASECUOTA rbi;
    @XmlElement(name = "RIA")
    protected TBASECUOTA ria;
    @XmlElement(name = "RPP")
    protected TBASECUOTA rpp;
    @XmlElement(name = "OGC")
    protected TBASECUOTA ogc;
    @XmlElement(name = "OGI")
    protected TBASECUOTA ogi;
    @XmlElement(name = "REG")
    protected TBASECUOTA reg;
    @XmlAttribute(name = "TOT")
    protected String tot;

    /**
     * Obtiene el valor de la propiedad oic.
     * 
     * @return
     *     possible object is
     *     {@link TBASECUOTA }
     *     
     */
    public TBASECUOTA getOIC() {
        return oic;
    }

    /**
     * Define el valor de la propiedad oic.
     * 
     * @param value
     *     allowed object is
     *     {@link TBASECUOTA }
     *     
     */
    public void setOIC(TBASECUOTA value) {
        this.oic = value;
    }

    /**
     * Obtiene el valor de la propiedad oii.
     * 
     * @return
     *     possible object is
     *     {@link TBASECUOTA }
     *     
     */
    public TBASECUOTA getOII() {
        return oii;
    }

    /**
     * Define el valor de la propiedad oii.
     * 
     * @param value
     *     allowed object is
     *     {@link TBASECUOTA }
     *     
     */
    public void setOII(TBASECUOTA value) {
        this.oii = value;
    }

    /**
     * Obtiene el valor de la propiedad imc.
     * 
     * @return
     *     possible object is
     *     {@link TBASECUOTA }
     *     
     */
    public TBASECUOTA getIMC() {
        return imc;
    }

    /**
     * Define el valor de la propiedad imc.
     * 
     * @param value
     *     allowed object is
     *     {@link TBASECUOTA }
     *     
     */
    public void setIMC(TBASECUOTA value) {
        this.imc = value;
    }

    /**
     * Obtiene el valor de la propiedad imi.
     * 
     * @return
     *     possible object is
     *     {@link TBASECUOTA }
     *     
     */
    public TBASECUOTA getIMI() {
        return imi;
    }

    /**
     * Define el valor de la propiedad imi.
     * 
     * @param value
     *     allowed object is
     *     {@link TBASECUOTA }
     *     
     */
    public void setIMI(TBASECUOTA value) {
        this.imi = value;
    }

    /**
     * Obtiene el valor de la propiedad red.
     * 
     * @return
     *     possible object is
     *     {@link TBASECUOTA }
     *     
     */
    public TBASECUOTA getRED() {
        return red;
    }

    /**
     * Define el valor de la propiedad red.
     * 
     * @param value
     *     allowed object is
     *     {@link TBASECUOTA }
     *     
     */
    public void setRED(TBASECUOTA value) {
        this.red = value;
    }

    /**
     * Obtiene el valor de la propiedad cra.
     * 
     * @return
     *     possible object is
     *     {@link TBASECUOTA }
     *     
     */
    public TBASECUOTA getCRA() {
        return cra;
    }

    /**
     * Define el valor de la propiedad cra.
     * 
     * @param value
     *     allowed object is
     *     {@link TBASECUOTA }
     *     
     */
    public void setCRA(TBASECUOTA value) {
        this.cra = value;
    }

    /**
     * Obtiene el valor de la propiedad rbi.
     * 
     * @return
     *     possible object is
     *     {@link TBASECUOTA }
     *     
     */
    public TBASECUOTA getRBI() {
        return rbi;
    }

    /**
     * Define el valor de la propiedad rbi.
     * 
     * @param value
     *     allowed object is
     *     {@link TBASECUOTA }
     *     
     */
    public void setRBI(TBASECUOTA value) {
        this.rbi = value;
    }

    /**
     * Obtiene el valor de la propiedad ria.
     * 
     * @return
     *     possible object is
     *     {@link TBASECUOTA }
     *     
     */
    public TBASECUOTA getRIA() {
        return ria;
    }

    /**
     * Define el valor de la propiedad ria.
     * 
     * @param value
     *     allowed object is
     *     {@link TBASECUOTA }
     *     
     */
    public void setRIA(TBASECUOTA value) {
        this.ria = value;
    }

    /**
     * Obtiene el valor de la propiedad rpp.
     * 
     * @return
     *     possible object is
     *     {@link TBASECUOTA }
     *     
     */
    public TBASECUOTA getRPP() {
        return rpp;
    }

    /**
     * Define el valor de la propiedad rpp.
     * 
     * @param value
     *     allowed object is
     *     {@link TBASECUOTA }
     *     
     */
    public void setRPP(TBASECUOTA value) {
        this.rpp = value;
    }

    /**
     * Obtiene el valor de la propiedad ogc.
     * 
     * @return
     *     possible object is
     *     {@link TBASECUOTA }
     *     
     */
    public TBASECUOTA getOGC() {
        return ogc;
    }

    /**
     * Define el valor de la propiedad ogc.
     * 
     * @param value
     *     allowed object is
     *     {@link TBASECUOTA }
     *     
     */
    public void setOGC(TBASECUOTA value) {
        this.ogc = value;
    }

    /**
     * Obtiene el valor de la propiedad ogi.
     * 
     * @return
     *     possible object is
     *     {@link TBASECUOTA }
     *     
     */
    public TBASECUOTA getOGI() {
        return ogi;
    }

    /**
     * Define el valor de la propiedad ogi.
     * 
     * @param value
     *     allowed object is
     *     {@link TBASECUOTA }
     *     
     */
    public void setOGI(TBASECUOTA value) {
        this.ogi = value;
    }

    /**
     * Obtiene el valor de la propiedad reg.
     * 
     * @return
     *     possible object is
     *     {@link TBASECUOTA }
     *     
     */
    public TBASECUOTA getREG() {
        return reg;
    }

    /**
     * Define el valor de la propiedad reg.
     * 
     * @param value
     *     allowed object is
     *     {@link TBASECUOTA }
     *     
     */
    public void setREG(TBASECUOTA value) {
        this.reg = value;
    }

    /**
     * Obtiene el valor de la propiedad tot.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTOT() {
        return tot;
    }

    /**
     * Define el valor de la propiedad tot.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTOT(String value) {
        this.tot = value;
    }

}
