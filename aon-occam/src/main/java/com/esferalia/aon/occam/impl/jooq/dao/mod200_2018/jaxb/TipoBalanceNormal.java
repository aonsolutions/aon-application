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
 * <p>Clase Java para tipo_BalanceNormal complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_BalanceNormal">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Pagina03" type="{}tipo_Pagina03" minOccurs="0"/>
 *         &lt;element name="Pagina04" type="{}tipo_Pagina04" minOccurs="0"/>
 *         &lt;element name="Pagina05" type="{}tipo_Pagina05" minOccurs="0"/>
 *         &lt;element name="Pagina06" type="{}tipo_Pagina06" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_BalanceNormal", propOrder = {
    "pagina03",
    "pagina04",
    "pagina05",
    "pagina06"
})
public class TipoBalanceNormal {

    @XmlElement(name = "Pagina03")
    protected TipoPagina03 pagina03;
    @XmlElement(name = "Pagina04")
    protected TipoPagina04 pagina04;
    @XmlElement(name = "Pagina05")
    protected TipoPagina05 pagina05;
    @XmlElement(name = "Pagina06")
    protected TipoPagina06 pagina06;

    /**
     * Obtiene el valor de la propiedad pagina03.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina03 }
     *     
     */
    public TipoPagina03 getPagina03() {
        return pagina03;
    }

    /**
     * Define el valor de la propiedad pagina03.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina03 }
     *     
     */
    public void setPagina03(TipoPagina03 value) {
        this.pagina03 = value;
    }

    /**
     * Obtiene el valor de la propiedad pagina04.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina04 }
     *     
     */
    public TipoPagina04 getPagina04() {
        return pagina04;
    }

    /**
     * Define el valor de la propiedad pagina04.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina04 }
     *     
     */
    public void setPagina04(TipoPagina04 value) {
        this.pagina04 = value;
    }

    /**
     * Obtiene el valor de la propiedad pagina05.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina05 }
     *     
     */
    public TipoPagina05 getPagina05() {
        return pagina05;
    }

    /**
     * Define el valor de la propiedad pagina05.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina05 }
     *     
     */
    public void setPagina05(TipoPagina05 value) {
        this.pagina05 = value;
    }

    /**
     * Obtiene el valor de la propiedad pagina06.
     * 
     * @return
     *     possible object is
     *     {@link TipoPagina06 }
     *     
     */
    public TipoPagina06 getPagina06() {
        return pagina06;
    }

    /**
     * Define el valor de la propiedad pagina06.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPagina06 }
     *     
     */
    public void setPagina06(TipoPagina06 value) {
        this.pagina06 = value;
    }

}
