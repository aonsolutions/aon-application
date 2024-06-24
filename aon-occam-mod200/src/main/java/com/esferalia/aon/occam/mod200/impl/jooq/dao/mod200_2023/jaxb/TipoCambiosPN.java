//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.8-b130911.1802 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
//  
//


package com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2023.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para tipo_CambiosPN complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_CambiosPN">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Pagina09" type="{}tipo_Pagina09" minOccurs="0"/>
 *         &lt;element name="Pagina10" type="{}tipo_Pagina10" minOccurs="0"/>
 *         &lt;element name="Pagina11" type="{}tipo_Pagina11" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_CambiosPN", propOrder = {
    "pagina09",
    "pagina10",
    "pagina11"
})
public class TipoCambiosPN {

    @XmlElement(name = "Pagina09")
    protected TipoPagina09 pagina09;
    @XmlElement(name = "Pagina10")
    protected TipoPagina10 pagina10;
    @XmlElement(name = "Pagina11")
    protected TipoPagina11 pagina11;

    /**
     * Obtiene el valor de la propiedad pagina09.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina09 }
     *     
     */
    public TipoPagina09 getPagina09() {
        return pagina09;
    }

    /**
     * Define el valor de la propiedad pagina09.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina09 }
     *     
     */
    public void setPagina09(TipoPagina09 value) {
        this.pagina09 = value;
    }

    /**
     * Obtiene el valor de la propiedad pagina10.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina10 }
     *     
     */
    public TipoPagina10 getPagina10() {
        return pagina10;
    }

    /**
     * Define el valor de la propiedad pagina10.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina10 }
     *     
     */
    public void setPagina10(TipoPagina10 value) {
        this.pagina10 = value;
    }

    /**
     * Obtiene el valor de la propiedad pagina11.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina11 }
     *     
     */
    public TipoPagina11 getPagina11() {
        return pagina11;
    }

    /**
     * Define el valor de la propiedad pagina11.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina11 }
     *     
     */
    public void setPagina11(TipoPagina11 value) {
        this.pagina11 = value;
    }

}
