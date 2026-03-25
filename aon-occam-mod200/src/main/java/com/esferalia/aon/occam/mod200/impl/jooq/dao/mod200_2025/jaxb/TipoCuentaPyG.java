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
 * <p>Clase Java para tipo_CuentaPyG complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_CuentaPyG"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Pagina46" type="{}tipo_Pagina46" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_CuentaPyG", propOrder = {
    "pagina46"
})
public class TipoCuentaPyG {

    @XmlElement(name = "Pagina46")
    protected TipoPagina46 pagina46;

    /**
     * Obtiene el valor de la propiedad pagina46.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina46 }
     *     
     */
    public TipoPagina46 getPagina46() {
        return pagina46;
    }

    /**
     * Define el valor de la propiedad pagina46.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina46 }
     *     
     */
    public void setPagina46(TipoPagina46 value) {
        this.pagina46 = value;
    }

}
