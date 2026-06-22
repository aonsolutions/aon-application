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
 * <p>Clase Java para tipo_CuentaAseguradora complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_CuentaAseguradora"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Pagina38" type="{}tipo_Pagina38" minOccurs="0"/&gt;
 *         &lt;element name="Pagina39" type="{}tipo_Pagina39" minOccurs="0"/&gt;
 *         &lt;element name="Pagina40" type="{}tipo_Pagina40" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_CuentaAseguradora", propOrder = {
    "pagina38",
    "pagina39",
    "pagina40"
})
public class TipoCuentaAseguradora {

    @XmlElement(name = "Pagina38")
    protected TipoPagina38 pagina38;
    @XmlElement(name = "Pagina39")
    protected TipoPagina39 pagina39;
    @XmlElement(name = "Pagina40")
    protected TipoPagina40 pagina40;

    /**
     * Obtiene el valor de la propiedad pagina38.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina38 }
     *     
     */
    public TipoPagina38 getPagina38() {
        return pagina38;
    }

    /**
     * Define el valor de la propiedad pagina38.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina38 }
     *     
     */
    public void setPagina38(TipoPagina38 value) {
        this.pagina38 = value;
    }

    /**
     * Obtiene el valor de la propiedad pagina39.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina39 }
     *     
     */
    public TipoPagina39 getPagina39() {
        return pagina39;
    }

    /**
     * Define el valor de la propiedad pagina39.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina39 }
     *     
     */
    public void setPagina39(TipoPagina39 value) {
        this.pagina39 = value;
    }

    /**
     * Obtiene el valor de la propiedad pagina40.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina40 }
     *     
     */
    public TipoPagina40 getPagina40() {
        return pagina40;
    }

    /**
     * Define el valor de la propiedad pagina40.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina40 }
     *     
     */
    public void setPagina40(TipoPagina40 value) {
        this.pagina40 = value;
    }

}
