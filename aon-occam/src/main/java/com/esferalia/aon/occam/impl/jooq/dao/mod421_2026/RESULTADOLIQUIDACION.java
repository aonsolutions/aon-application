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
 * 
 * 				Resultado de la declaración
 * 	    	
 * 
 * <p>Clase Java para RESULTADO_LIQUIDACION complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RESULTADO_LIQUIDACION"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SAF" type="{}T_FRACCIONAMIENTO" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="TIP" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;length value="1"/&gt;
 *             &lt;enumeration value="I"/&gt;
 *             &lt;enumeration value="C"/&gt;
 *             &lt;enumeration value="D"/&gt;
 *             &lt;enumeration value="S"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="IMP" type="{}IMPA15ABSType" /&gt;
 *       &lt;attribute name="FPA"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;length value="1"/&gt;
 *             &lt;enumeration value="1"/&gt;
 *             &lt;enumeration value="2"/&gt;
 *             &lt;enumeration value="3"/&gt;
 *             &lt;enumeration value="4"/&gt;
 *             &lt;enumeration value="5"/&gt;
 *             &lt;enumeration value="6"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="CCC"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;length value="20"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="IBAN"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;length value="24"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RESULTADO_LIQUIDACION", propOrder = {
    "saf"
})
public class RESULTADOLIQUIDACION {

    @XmlElement(name = "SAF")
    protected TFRACCIONAMIENTO saf;
    @XmlAttribute(name = "TIP", required = true)
    protected String tip;
    @XmlAttribute(name = "IMP")
    protected String imp;
    @XmlAttribute(name = "FPA")
    protected String fpa;
    @XmlAttribute(name = "CCC")
    protected String ccc;
    @XmlAttribute(name = "IBAN")
    protected String iban;

    /**
     * Obtiene el valor de la propiedad saf.
     * 
     * @return
     *     possible object is
     *     {@link TFRACCIONAMIENTO }
     *     
     */
    public TFRACCIONAMIENTO getSAF() {
        return saf;
    }

    /**
     * Define el valor de la propiedad saf.
     * 
     * @param value
     *     allowed object is
     *     {@link TFRACCIONAMIENTO }
     *     
     */
    public void setSAF(TFRACCIONAMIENTO value) {
        this.saf = value;
    }

    /**
     * Obtiene el valor de la propiedad tip.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTIP() {
        return tip;
    }

    /**
     * Define el valor de la propiedad tip.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTIP(String value) {
        this.tip = value;
    }

    /**
     * Obtiene el valor de la propiedad imp.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIMP() {
        return imp;
    }

    /**
     * Define el valor de la propiedad imp.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIMP(String value) {
        this.imp = value;
    }

    /**
     * Obtiene el valor de la propiedad fpa.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFPA() {
        return fpa;
    }

    /**
     * Define el valor de la propiedad fpa.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFPA(String value) {
        this.fpa = value;
    }

    /**
     * Obtiene el valor de la propiedad ccc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCCC() {
        return ccc;
    }

    /**
     * Define el valor de la propiedad ccc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCCC(String value) {
        this.ccc = value;
    }

    /**
     * Obtiene el valor de la propiedad iban.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIBAN() {
        return iban;
    }

    /**
     * Define el valor de la propiedad iban.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIBAN(String value) {
        this.iban = value;
    }

}
