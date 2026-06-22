//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.1-b171012.0423 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2025.05.05 a las 11:04:44 AM CEST 
//


package com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2025.jaxb;

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
 * &lt;complexType name="tipo_CuentaBancos"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Pagina30" type="{}tipo_Pagina30" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
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
