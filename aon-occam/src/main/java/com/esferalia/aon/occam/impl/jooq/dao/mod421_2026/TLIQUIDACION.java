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
import javax.xml.bind.annotation.XmlType;


/**
 * Liquidacion
 * 
 * <p>Clase Java para T_LIQUIDACION complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_LIQUIDACION"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="DIF" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="RCU" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="CPA" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="SUT" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="DAC" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="RLI" type="{}IMPA15Type" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "T_LIQUIDACION")
public class TLIQUIDACION {

    @XmlAttribute(name = "DIF")
    protected String dif;
    @XmlAttribute(name = "RCU")
    protected String rcu;
    @XmlAttribute(name = "CPA")
    protected String cpa;
    @XmlAttribute(name = "SUT")
    protected String sut;
    @XmlAttribute(name = "DAC")
    protected String dac;
    @XmlAttribute(name = "RLI")
    protected String rli;

    /**
     * Obtiene el valor de la propiedad dif.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDIF() {
        return dif;
    }

    /**
     * Define el valor de la propiedad dif.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDIF(String value) {
        this.dif = value;
    }

    /**
     * Obtiene el valor de la propiedad rcu.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRCU() {
        return rcu;
    }

    /**
     * Define el valor de la propiedad rcu.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRCU(String value) {
        this.rcu = value;
    }

    /**
     * Obtiene el valor de la propiedad cpa.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCPA() {
        return cpa;
    }

    /**
     * Define el valor de la propiedad cpa.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCPA(String value) {
        this.cpa = value;
    }

    /**
     * Obtiene el valor de la propiedad sut.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSUT() {
        return sut;
    }

    /**
     * Define el valor de la propiedad sut.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSUT(String value) {
        this.sut = value;
    }

    /**
     * Obtiene el valor de la propiedad dac.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDAC() {
        return dac;
    }

    /**
     * Define el valor de la propiedad dac.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDAC(String value) {
        this.dac = value;
    }

    /**
     * Obtiene el valor de la propiedad rli.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRLI() {
        return rli;
    }

    /**
     * Define el valor de la propiedad rli.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRLI(String value) {
        this.rli = value;
    }

}
