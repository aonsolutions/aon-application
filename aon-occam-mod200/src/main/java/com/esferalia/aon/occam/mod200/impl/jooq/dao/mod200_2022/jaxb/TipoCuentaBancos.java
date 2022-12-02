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
 * <p>Clase Java para tipo_CuentaBancos complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_CuentaBancos">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Pagina30" type="{}tipo_Pagina30" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_CuentaBancos", propOrder = {
    "pagina30"
})
public class TipoCuentaBancos {

    @XmlElement(name = "Pagina30")
    protected TipoPagina30 pagina30;

    /**
     * Obtiene el valor de la propiedad pagina30.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina30 }
     *     
     */
    public TipoPagina30 getPagina30() {
        return pagina30;
    }

    /**
     * Define el valor de la propiedad pagina30.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina30 }
     *     
     */
    public void setPagina30(TipoPagina30 value) {
        this.pagina30 = value;
    }

}
