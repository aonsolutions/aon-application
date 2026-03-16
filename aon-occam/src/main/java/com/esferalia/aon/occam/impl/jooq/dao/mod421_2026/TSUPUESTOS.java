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
 * <p>Clase Java para T_SUPUESTOS complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_SUPUESTOS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="SU_GE_SG" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="SU_GE_CG" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="SU_ES_A82SG" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="SU_ES_A83CG" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="SU_ES_A39SG" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="SU_ES_A39CG" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="SU_ES_A113SG" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="SU_ES_OTR" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "T_SUPUESTOS")
public class TSUPUESTOS {

    @XmlAttribute(name = "SU_GE_SG")
    protected Boolean sugesg;
    @XmlAttribute(name = "SU_GE_CG")
    protected Boolean sugecg;
    @XmlAttribute(name = "SU_ES_A82SG")
    protected Boolean suesa82SG;
    @XmlAttribute(name = "SU_ES_A83CG")
    protected Boolean suesa83CG;
    @XmlAttribute(name = "SU_ES_A39SG")
    protected Boolean suesa39SG;
    @XmlAttribute(name = "SU_ES_A39CG")
    protected Boolean suesa39CG;
    @XmlAttribute(name = "SU_ES_A113SG")
    protected Boolean suesa113SG;
    @XmlAttribute(name = "SU_ES_OTR", required = true)
    protected String suesotr;

    /**
     * Obtiene el valor de la propiedad sugesg.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isSUGESG() {
        if (sugesg == null) {
            return false;
        } else {
            return sugesg;
        }
    }

    /**
     * Define el valor de la propiedad sugesg.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSUGESG(Boolean value) {
        this.sugesg = value;
    }

    /**
     * Obtiene el valor de la propiedad sugecg.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isSUGECG() {
        if (sugecg == null) {
            return false;
        } else {
            return sugecg;
        }
    }

    /**
     * Define el valor de la propiedad sugecg.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSUGECG(Boolean value) {
        this.sugecg = value;
    }

    /**
     * Obtiene el valor de la propiedad suesa82SG.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isSUESA82SG() {
        if (suesa82SG == null) {
            return false;
        } else {
            return suesa82SG;
        }
    }

    /**
     * Define el valor de la propiedad suesa82SG.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSUESA82SG(Boolean value) {
        this.suesa82SG = value;
    }

    /**
     * Obtiene el valor de la propiedad suesa83CG.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isSUESA83CG() {
        if (suesa83CG == null) {
            return false;
        } else {
            return suesa83CG;
        }
    }

    /**
     * Define el valor de la propiedad suesa83CG.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSUESA83CG(Boolean value) {
        this.suesa83CG = value;
    }

    /**
     * Obtiene el valor de la propiedad suesa39SG.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isSUESA39SG() {
        if (suesa39SG == null) {
            return false;
        } else {
            return suesa39SG;
        }
    }

    /**
     * Define el valor de la propiedad suesa39SG.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSUESA39SG(Boolean value) {
        this.suesa39SG = value;
    }

    /**
     * Obtiene el valor de la propiedad suesa39CG.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isSUESA39CG() {
        if (suesa39CG == null) {
            return false;
        } else {
            return suesa39CG;
        }
    }

    /**
     * Define el valor de la propiedad suesa39CG.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSUESA39CG(Boolean value) {
        this.suesa39CG = value;
    }

    /**
     * Obtiene el valor de la propiedad suesa113SG.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isSUESA113SG() {
        if (suesa113SG == null) {
            return false;
        } else {
            return suesa113SG;
        }
    }

    /**
     * Define el valor de la propiedad suesa113SG.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSUESA113SG(Boolean value) {
        this.suesa113SG = value;
    }

    /**
     * Obtiene el valor de la propiedad suesotr.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSUESOTR() {
        return suesotr;
    }

    /**
     * Define el valor de la propiedad suesotr.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSUESOTR(String value) {
        this.suesotr = value;
    }

}
