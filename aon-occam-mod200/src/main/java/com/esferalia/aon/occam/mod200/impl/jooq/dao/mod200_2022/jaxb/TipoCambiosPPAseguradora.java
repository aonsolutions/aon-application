//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.8-b130911.1802 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
//  
//


package com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2022.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para tipo_CambiosPP_Aseguradora complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_CambiosPP_Aseguradora">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Pagina41" type="{}tipo_Pagina41" minOccurs="0"/>
 *         &lt;element name="Pagina42" type="{}tipo_Pagina42" minOccurs="0"/>
 *         &lt;element name="Pagina43" type="{}tipo_Pagina43" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_CambiosPP_Aseguradora", propOrder = {
    "pagina41",
    "pagina42",
    "pagina43"
})
public class TipoCambiosPPAseguradora {

    @XmlElement(name = "Pagina41")
    protected TipoPagina41 pagina41;
    @XmlElement(name = "Pagina42")
    protected TipoPagina42 pagina42;
    @XmlElement(name = "Pagina43")
    protected TipoPagina43 pagina43;

    /**
     * Obtiene el valor de la propiedad pagina41.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina41 }
     *     
     */
    public TipoPagina41 getPagina41() {
        return pagina41;
    }

    /**
     * Define el valor de la propiedad pagina41.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina41 }
     *     
     */
    public void setPagina41(TipoPagina41 value) {
        this.pagina41 = value;
    }

    /**
     * Obtiene el valor de la propiedad pagina42.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina42 }
     *     
     */
    public TipoPagina42 getPagina42() {
        return pagina42;
    }

    /**
     * Define el valor de la propiedad pagina42.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina42 }
     *     
     */
    public void setPagina42(TipoPagina42 value) {
        this.pagina42 = value;
    }

    /**
     * Obtiene el valor de la propiedad pagina43.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina43 }
     *     
     */
    public TipoPagina43 getPagina43() {
        return pagina43;
    }

    /**
     * Define el valor de la propiedad pagina43.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina43 }
     *     
     */
    public void setPagina43(TipoPagina43 value) {
        this.pagina43 = value;
    }

}
