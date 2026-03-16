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
 * Información adicional
 * 
 * <p>Clase Java para T_INFO_ADICIONAL complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_INFO_ADICIONAL"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="EOA" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="ODD" type="{}IMPA15Type" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "T_INFO_ADICIONAL")
public class TINFOADICIONAL {

    @XmlAttribute(name = "EOA")
    protected String eoa;
    @XmlAttribute(name = "ODD")
    protected String odd;

    /**
     * Obtiene el valor de la propiedad eoa.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEOA() {
        return eoa;
    }

    /**
     * Define el valor de la propiedad eoa.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEOA(String value) {
        this.eoa = value;
    }

    /**
     * Obtiene el valor de la propiedad odd.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getODD() {
        return odd;
    }

    /**
     * Define el valor de la propiedad odd.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setODD(String value) {
        this.odd = value;
    }

}
