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
 * <p>Clase Java para tipo_CuentaNormal complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_CuentaNormal">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Pagina07" type="{}tipo_Pagina07" minOccurs="0"/>
 *         &lt;element name="Pagina08" type="{}tipo_Pagina08" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_CuentaNormal", propOrder = {
    "pagina07",
    "pagina08"
})
public class TipoCuentaNormal {

    @XmlElement(name = "Pagina07")
    protected TipoPagina07 pagina07;
    @XmlElement(name = "Pagina08")
    protected TipoPagina08 pagina08;

    /**
     * Obtiene el valor de la propiedad pagina07.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina07 }
     *     
     */
    public TipoPagina07 getPagina07() {
        return pagina07;
    }

    /**
     * Define el valor de la propiedad pagina07.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina07 }
     *     
     */
    public void setPagina07(TipoPagina07 value) {
        this.pagina07 = value;
    }

    /**
     * Obtiene el valor de la propiedad pagina08.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina08 }
     *     
     */
    public TipoPagina08 getPagina08() {
        return pagina08;
    }

    /**
     * Define el valor de la propiedad pagina08.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina08 }
     *     
     */
    public void setPagina08(TipoPagina08 value) {
        this.pagina08 = value;
    }

}
