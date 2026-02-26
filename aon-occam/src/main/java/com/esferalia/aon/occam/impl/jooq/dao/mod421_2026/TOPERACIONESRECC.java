//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.1-b171012.0423 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.02.26 a las 01:00:22 PM CET 
//


package com.esferalia.aon.occam.impl.jooq.dao.mod421_2026;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Datos exclusivos para sujetos acogidos al régimen especial
 * 				de criterio de caja y/o destinatarios de operaciones afectadas
 * 				por el mismo.
 * 			
 * 
 * <p>Clase Java para T_OPERACIONES_RECC complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_OPERACIONES_RECC"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IEB" type="{}T_BASE_CUOTA" minOccurs="0"/&gt;
 *         &lt;element name="IAB" type="{}T_BASE_CUOTA" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "T_OPERACIONES_RECC", propOrder = {
    "ieb",
    "iab"
})
public class TOPERACIONESRECC {

    @XmlElement(name = "IEB")
    protected TBASECUOTA ieb;
    @XmlElement(name = "IAB")
    protected TBASECUOTA iab;

    /**
     * Obtiene el valor de la propiedad ieb.
     * 
     * @return
     *     possible object is
     *     {@link TBASECUOTA }
     *     
     */
    public TBASECUOTA getIEB() {
        return ieb;
    }

    /**
     * Define el valor de la propiedad ieb.
     * 
     * @param value
     *     allowed object is
     *     {@link TBASECUOTA }
     *     
     */
    public void setIEB(TBASECUOTA value) {
        this.ieb = value;
    }

    /**
     * Obtiene el valor de la propiedad iab.
     * 
     * @return
     *     possible object is
     *     {@link TBASECUOTA }
     *     
     */
    public TBASECUOTA getIAB() {
        return iab;
    }

    /**
     * Define el valor de la propiedad iab.
     * 
     * @param value
     *     allowed object is
     *     {@link TBASECUOTA }
     *     
     */
    public void setIAB(TBASECUOTA value) {
        this.iab = value;
    }

}
