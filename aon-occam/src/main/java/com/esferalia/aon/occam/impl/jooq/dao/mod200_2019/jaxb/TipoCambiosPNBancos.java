//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.8-b130911.1802 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2019.05.15 a las 12:12:26 PM CEST 
//


package com.esferalia.aon.occam.impl.jooq.dao.mod200_2019.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para tipo_CambiosPN_Bancos complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_CambiosPN_Bancos">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Pagina31" type="{}tipo_Pagina31" minOccurs="0"/>
 *         &lt;element name="Pagina32" type="{}tipo_Pagina32" minOccurs="0"/>
 *         &lt;element name="Pagina33" type="{}tipo_Pagina33" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_CambiosPN_Bancos", propOrder = {
    "pagina31",
    "pagina32",
    "pagina33"
})
public class TipoCambiosPNBancos {

    @XmlElement(name = "Pagina31")
    protected TipoPagina31 pagina31;
    @XmlElement(name = "Pagina32")
    protected TipoPagina32 pagina32;
    @XmlElement(name = "Pagina33")
    protected TipoPagina33 pagina33;

    /**
     * Obtiene el valor de la propiedad pagina31.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina31 }
     *     
     */
    public TipoPagina31 getPagina31() {
        return pagina31;
    }

    /**
     * Define el valor de la propiedad pagina31.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina31 }
     *     
     */
    public void setPagina31(TipoPagina31 value) {
        this.pagina31 = value;
    }

    /**
     * Obtiene el valor de la propiedad pagina32.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina32 }
     *     
     */
    public TipoPagina32 getPagina32() {
        return pagina32;
    }

    /**
     * Define el valor de la propiedad pagina32.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina32 }
     *     
     */
    public void setPagina32(TipoPagina32 value) {
        this.pagina32 = value;
    }

    /**
     * Obtiene el valor de la propiedad pagina33.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina33 }
     *     
     */
    public TipoPagina33 getPagina33() {
        return pagina33;
    }

    /**
     * Define el valor de la propiedad pagina33.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina33 }
     *     
     */
    public void setPagina33(TipoPagina33 value) {
        this.pagina33 = value;
    }

}
