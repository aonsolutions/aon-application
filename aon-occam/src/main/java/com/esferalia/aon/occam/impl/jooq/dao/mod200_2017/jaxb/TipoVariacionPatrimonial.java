//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.8-b130911.1802 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2018.05.25 a las 09:12:21 AM CEST 
//


package com.esferalia.aon.occam.impl.jooq.dao.mod200_2017.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para tipo_VariacionPatrimonial complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_VariacionPatrimonial">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Pagina47" type="{}tipo_Pagina47" minOccurs="0"/>
 *         &lt;element name="Pagina48" type="{}tipo_Pagina48" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_VariacionPatrimonial", propOrder = {
    "pagina47",
    "pagina48"
})
public class TipoVariacionPatrimonial {

    @XmlElement(name = "Pagina47")
    protected TipoPagina47 pagina47;
    @XmlElement(name = "Pagina48")
    protected TipoPagina48 pagina48;

    /**
     * Obtiene el valor de la propiedad pagina47.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina47 }
     *     
     */
    public TipoPagina47 getPagina47() {
        return pagina47;
    }

    /**
     * Define el valor de la propiedad pagina47.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina47 }
     *     
     */
    public void setPagina47(TipoPagina47 value) {
        this.pagina47 = value;
    }

    /**
     * Obtiene el valor de la propiedad pagina48.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina48 }
     *     
     */
    public TipoPagina48 getPagina48() {
        return pagina48;
    }

    /**
     * Define el valor de la propiedad pagina48.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina48 }
     *     
     */
    public void setPagina48(TipoPagina48 value) {
        this.pagina48 = value;
    }

}
