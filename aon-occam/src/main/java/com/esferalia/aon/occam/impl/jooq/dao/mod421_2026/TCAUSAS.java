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
 * <p>Clase Java para T_CAUSAS complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_CAUSAS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="CA_DET" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="CA_OTR" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "T_CAUSAS")
public class TCAUSAS {

    @XmlAttribute(name = "CA_DET")
    protected Boolean cadet;
    @XmlAttribute(name = "CA_OTR", required = true)
    protected String caotr;

    /**
     * Obtiene el valor de la propiedad cadet.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isCADET() {
        if (cadet == null) {
            return false;
        } else {
            return cadet;
        }
    }

    /**
     * Define el valor de la propiedad cadet.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCADET(Boolean value) {
        this.cadet = value;
    }

    /**
     * Obtiene el valor de la propiedad caotr.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCAOTR() {
        return caotr;
    }

    /**
     * Define el valor de la propiedad caotr.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCAOTR(String value) {
        this.caotr = value;
    }

}
