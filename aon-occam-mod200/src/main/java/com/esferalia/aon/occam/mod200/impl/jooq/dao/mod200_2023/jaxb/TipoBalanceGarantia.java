//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.8-b130911.1802 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
//  
//


package com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2023.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para tipo_BalanceGarantia complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_BalanceGarantia">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Pagina49" type="{}tipo_Pagina49" minOccurs="0"/>
 *         &lt;element name="Pagina50" type="{}tipo_Pagina50" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_BalanceGarantia", propOrder = {
    "pagina49",
    "pagina50"
})
public class TipoBalanceGarantia {

    @XmlElement(name = "Pagina49")
    protected TipoPagina49 pagina49;
    @XmlElement(name = "Pagina50")
    protected TipoPagina50 pagina50;

    /**
     * Obtiene el valor de la propiedad pagina49.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina49 }
     *     
     */
    public TipoPagina49 getPagina49() {
        return pagina49;
    }

    /**
     * Define el valor de la propiedad pagina49.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina49 }
     *     
     */
    public void setPagina49(TipoPagina49 value) {
        this.pagina49 = value;
    }

    /**
     * Obtiene el valor de la propiedad pagina50.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina50 }
     *     
     */
    public TipoPagina50 getPagina50() {
        return pagina50;
    }

    /**
     * Define el valor de la propiedad pagina50.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina50 }
     *     
     */
    public void setPagina50(TipoPagina50 value) {
        this.pagina50 = value;
    }

}
