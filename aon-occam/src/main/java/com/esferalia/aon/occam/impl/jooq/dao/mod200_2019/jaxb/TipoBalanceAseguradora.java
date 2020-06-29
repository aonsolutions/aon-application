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
 * <p>Clase Java para tipo_BalanceAseguradora complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_BalanceAseguradora">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Pagina34" type="{}tipo_Pagina34" minOccurs="0"/>
 *         &lt;element name="Pagina35" type="{}tipo_Pagina35" minOccurs="0"/>
 *         &lt;element name="Pagina36" type="{}tipo_Pagina36" minOccurs="0"/>
 *         &lt;element name="Pagina37" type="{}tipo_Pagina37" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_BalanceAseguradora", propOrder = {
    "pagina34",
    "pagina35",
    "pagina36",
    "pagina37"
})
public class TipoBalanceAseguradora {

    @XmlElement(name = "Pagina34")
    protected TipoPagina34 pagina34;
    @XmlElement(name = "Pagina35")
    protected TipoPagina35 pagina35;
    @XmlElement(name = "Pagina36")
    protected TipoPagina36 pagina36;
    @XmlElement(name = "Pagina37")
    protected TipoPagina37 pagina37;

    /**
     * Obtiene el valor de la propiedad pagina34.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina34 }
     *     
     */
    public TipoPagina34 getPagina34() {
        return pagina34;
    }

    /**
     * Define el valor de la propiedad pagina34.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina34 }
     *     
     */
    public void setPagina34(TipoPagina34 value) {
        this.pagina34 = value;
    }

    /**
     * Obtiene el valor de la propiedad pagina35.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina35 }
     *     
     */
    public TipoPagina35 getPagina35() {
        return pagina35;
    }

    /**
     * Define el valor de la propiedad pagina35.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina35 }
     *     
     */
    public void setPagina35(TipoPagina35 value) {
        this.pagina35 = value;
    }

    /**
     * Obtiene el valor de la propiedad pagina36.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina36 }
     *     
     */
    public TipoPagina36 getPagina36() {
        return pagina36;
    }

    /**
     * Define el valor de la propiedad pagina36.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina36 }
     *     
     */
    public void setPagina36(TipoPagina36 value) {
        this.pagina36 = value;
    }

    /**
     * Obtiene el valor de la propiedad pagina37.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina37 }
     *     
     */
    public TipoPagina37 getPagina37() {
        return pagina37;
    }

    /**
     * Define el valor de la propiedad pagina37.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina37 }
     *     
     */
    public void setPagina37(TipoPagina37 value) {
        this.pagina37 = value;
    }

}
