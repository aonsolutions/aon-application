//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.5-2 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: PM.11.26 a las 06:54:59 PM CET 
//


package com.esferalia.aon.file.payroll.contrata.model.contratos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Datos de la obra o servicio en Corporaciones Locales.
 * 
 * <p>Clase Java para DATOS_PROGEMPLEOPUBLICOTYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOS_PROGEMPLEOPUBLICOTYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CORPORACION_LOCAL" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{1}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ACTUACION" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="3"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="EJERCICIO_PRESUPUESTARIO" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{4}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="GRUPOCOTIZACION_CORPORACIONLOCAL" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{2}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DATOS_PROGEMPLEOPUBLICOTYPE", propOrder = {
    "corporacionlocal",
    "actuacion",
    "ejerciciopresupuestario",
    "grupocotizacioncorporacionlocal"
})
public class DATOSPROGEMPLEOPUBLICOTYPE {

    @XmlElement(name = "CORPORACION_LOCAL")
    protected String corporacionlocal;
    @XmlElement(name = "ACTUACION")
    protected String actuacion;
    @XmlElement(name = "EJERCICIO_PRESUPUESTARIO")
    protected String ejerciciopresupuestario;
    @XmlElement(name = "GRUPOCOTIZACION_CORPORACIONLOCAL")
    protected String grupocotizacioncorporacionlocal;

    /**
     * Obtiene el valor de la propiedad corporacionlocal.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCORPORACIONLOCAL() {
        return corporacionlocal;
    }

    /**
     * Define el valor de la propiedad corporacionlocal.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCORPORACIONLOCAL(String value) {
        this.corporacionlocal = value;
    }

    /**
     * Obtiene el valor de la propiedad actuacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getACTUACION() {
        return actuacion;
    }

    /**
     * Define el valor de la propiedad actuacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setACTUACION(String value) {
        this.actuacion = value;
    }

    /**
     * Obtiene el valor de la propiedad ejerciciopresupuestario.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEJERCICIOPRESUPUESTARIO() {
        return ejerciciopresupuestario;
    }

    /**
     * Define el valor de la propiedad ejerciciopresupuestario.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEJERCICIOPRESUPUESTARIO(String value) {
        this.ejerciciopresupuestario = value;
    }

    /**
     * Obtiene el valor de la propiedad grupocotizacioncorporacionlocal.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGRUPOCOTIZACIONCORPORACIONLOCAL() {
        return grupocotizacioncorporacionlocal;
    }

    /**
     * Define el valor de la propiedad grupocotizacioncorporacionlocal.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGRUPOCOTIZACIONCORPORACIONLOCAL(String value) {
        this.grupocotizacioncorporacionlocal = value;
    }

}
