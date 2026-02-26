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
 * Datos relativoa a firma
 * 
 * <p>Clase Java para T_FIRMA complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_FIRMA"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="LUG" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="FEC" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="CAL" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "T_FIRMA")
public class TFIRMA {

    @XmlAttribute(name = "LUG")
    protected String lug;
    @XmlAttribute(name = "FEC")
    protected String fec;
    @XmlAttribute(name = "CAL")
    protected String cal;

    /**
     * Obtiene el valor de la propiedad lug.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLUG() {
        return lug;
    }

    /**
     * Define el valor de la propiedad lug.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLUG(String value) {
        this.lug = value;
    }

    /**
     * Obtiene el valor de la propiedad fec.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFEC() {
        return fec;
    }

    /**
     * Define el valor de la propiedad fec.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFEC(String value) {
        this.fec = value;
    }

    /**
     * Obtiene el valor de la propiedad cal.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCAL() {
        return cal;
    }

    /**
     * Define el valor de la propiedad cal.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCAL(String value) {
        this.cal = value;
    }

}
