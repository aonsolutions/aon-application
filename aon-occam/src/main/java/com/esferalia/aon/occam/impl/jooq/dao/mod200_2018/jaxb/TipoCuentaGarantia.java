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
 * <p>Clase Java para tipo_CuentaGarantia complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_CuentaGarantia">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Pagina51" type="{}tipo_Pagina51" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_CuentaGarantia", propOrder = {
    "pagina51"
})
public class TipoCuentaGarantia {

    @XmlElement(name = "Pagina51")
    protected TipoPagina51 pagina51;

    /**
     * Obtiene el valor de la propiedad pagina51.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina51 }
     *     
     */
    public TipoPagina51 getPagina51() {
        return pagina51;
    }

    /**
     * Define el valor de la propiedad pagina51.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina51 }
     *     
     */
    public void setPagina51(TipoPagina51 value) {
        this.pagina51 = value;
    }

}
