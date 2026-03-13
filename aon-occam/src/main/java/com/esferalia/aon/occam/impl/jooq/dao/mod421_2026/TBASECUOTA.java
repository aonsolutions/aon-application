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
 * Bases, tipos y cuotas
 * 
 * <p>Clase Java para T_BASE_CUOTA complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_BASE_CUOTA"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="BAS" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="TIP" type="{}IMPA5Type" /&gt;
 *       &lt;attribute name="CUO" type="{}IMPA15Type" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "T_BASE_CUOTA")
public class TBASECUOTA {

    @XmlAttribute(name = "BAS")
    protected String bas;
    @XmlAttribute(name = "TIP")
    protected String tip;
    @XmlAttribute(name = "CUO")
    protected String cuo;

    /**
     * Obtiene el valor de la propiedad bas.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBAS() {
        return bas;
    }

    /**
     * Define el valor de la propiedad bas.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBAS(String value) {
        this.bas = value;
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
     * Obtiene el valor de la propiedad cuo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCUO() {
        return cuo;
    }

    /**
     * Define el valor de la propiedad cuo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCUO(String value) {
        this.cuo = value;
    }

}
