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
 * <p>Clase Java para tipo_CambiosPN_Garantia complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_CambiosPN_Garantia"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Pagina53" type="{}tipo_Pagina53" minOccurs="0"/&gt;
 *         &lt;element name="Pagina54" type="{}tipo_Pagina54" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_CambiosPN_Garantia", propOrder = {
    "pagina53",
    "pagina54"
})
public class TipoCambiosPNGarantia {

    @XmlElement(name = "Pagina53")
    protected TipoPagina53 pagina53;
    @XmlElement(name = "Pagina54")
    protected TipoPagina54 pagina54;

    /**
     * Obtiene el valor de la propiedad pagina53.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina53 }
     *     
     */
    public TipoPagina53 getPagina53() {
        return pagina53;
    }

    /**
     * Define el valor de la propiedad pagina53.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina53 }
     *     
     */
    public void setPagina53(TipoPagina53 value) {
        this.pagina53 = value;
    }

    /**
     * Obtiene el valor de la propiedad pagina54.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina54 }
     *     
     */
    public TipoPagina54 getPagina54() {
        return pagina54;
    }

    /**
     * Define el valor de la propiedad pagina54.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina54 }
     *     
     */
    public void setPagina54(TipoPagina54 value) {
        this.pagina54 = value;
    }

}
