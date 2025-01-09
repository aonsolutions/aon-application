//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.1-b171012.0423 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2024.12.05 a las 04:40:27 PM CET 
//


package com.esferalia.aon.occam.impl.jooq.dao.mod390_2024;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para tipo_BaseImponible_y_Cuota complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_BaseImponible_y_Cuota"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="BI" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *         &lt;element name="Cuota" type="{}tipo_ImpNegativo" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_BaseImponible_y_Cuota", propOrder = {
    "bi",
    "cuota"
})
public class TipoBaseImponibleYCuota {

    @XmlElement(name = "BI")
    protected BigDecimal bi;
    @XmlElement(name = "Cuota")
    protected BigDecimal cuota;

    /**
     * Obtiene el valor de la propiedad bi.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getBI() {
        return bi;
    }

    /**
     * Define el valor de la propiedad bi.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setBI(BigDecimal value) {
        this.bi = value;
    }

    /**
     * Obtiene el valor de la propiedad cuota.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getCuota() {
        return cuota;
    }

    /**
     * Define el valor de la propiedad cuota.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setCuota(BigDecimal value) {
        this.cuota = value;
    }

}
