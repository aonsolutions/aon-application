//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.8-b130911.1802 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2019.05.15 a las 12:12:26 PM CEST 
//


package com.esferalia.aon.occam.impl.jooq.dao.mod200_2018.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para tipo_BalanceBancos complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_BalanceBancos">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Pagina27" type="{}tipo_Pagina27" minOccurs="0"/>
 *         &lt;element name="Pagina28" type="{}tipo_Pagina28" minOccurs="0"/>
 *         &lt;element name="Pagina29" type="{}tipo_Pagina29" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_BalanceBancos", propOrder = {
    "pagina27",
    "pagina28",
    "pagina29"
})
public class TipoBalanceBancos {

    @XmlElement(name = "Pagina27")
    protected TipoPagina27 pagina27;
    @XmlElement(name = "Pagina28")
    protected TipoPagina28 pagina28;
    @XmlElement(name = "Pagina29")
    protected TipoPagina29 pagina29;

    /**
     * Obtiene el valor de la propiedad pagina27.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina27 }
     *     
     */
    public TipoPagina27 getPagina27() {
        return pagina27;
    }

    /**
     * Define el valor de la propiedad pagina27.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina27 }
     *     
     */
    public void setPagina27(TipoPagina27 value) {
        this.pagina27 = value;
    }

    /**
     * Obtiene el valor de la propiedad pagina28.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina28 }
     *     
     */
    public TipoPagina28 getPagina28() {
        return pagina28;
    }

    /**
     * Define el valor de la propiedad pagina28.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina28 }
     *     
     */
    public void setPagina28(TipoPagina28 value) {
        this.pagina28 = value;
    }

    /**
     * Obtiene el valor de la propiedad pagina29.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina29 }
     *     
     */
    public TipoPagina29 getPagina29() {
        return pagina29;
    }

    /**
     * Define el valor de la propiedad pagina29.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina29 }
     *     
     */
    public void setPagina29(TipoPagina29 value) {
        this.pagina29 = value;
    }

}
