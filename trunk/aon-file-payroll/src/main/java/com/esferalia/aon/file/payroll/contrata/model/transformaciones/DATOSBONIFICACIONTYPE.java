//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.5-2 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: PM.11.26 a las 06:55:00 PM CET 
//


package com.esferalia.aon.file.payroll.contrata.model.transformaciones;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Datos de bonificación de la transformación.
 * 
 * <p>Clase Java para DATOS_BONIFICACIONTYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOS_BONIFICACIONTYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ACOGIDO_MATERNIDAD_EXCEDENCIA" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[S\s]"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="COLECTIVO_DISCAPACITADOS" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="2"/>
 *               &lt;maxLength value="3"/>
 *               &lt;pattern value="([0-9])+"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CODIGO_COLECTIVO_BONIF" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="2"/>
 *               &lt;maxLength value="3"/>
 *               &lt;pattern value="([0-9])+"/>
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
@XmlType(name = "DATOS_BONIFICACIONTYPE", propOrder = {
    "acogidomaternidadexcedencia",
    "colectivodiscapacitados",
    "codigocolectivobonif"
})
public class DATOSBONIFICACIONTYPE {

    @XmlElement(name = "ACOGIDO_MATERNIDAD_EXCEDENCIA")
    protected String acogidomaternidadexcedencia;
    @XmlElement(name = "COLECTIVO_DISCAPACITADOS")
    protected String colectivodiscapacitados;
    @XmlElement(name = "CODIGO_COLECTIVO_BONIF")
    protected String codigocolectivobonif;

    /**
     * Obtiene el valor de la propiedad acogidomaternidadexcedencia.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getACOGIDOMATERNIDADEXCEDENCIA() {
        return acogidomaternidadexcedencia;
    }

    /**
     * Define el valor de la propiedad acogidomaternidadexcedencia.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setACOGIDOMATERNIDADEXCEDENCIA(String value) {
        this.acogidomaternidadexcedencia = value;
    }

    /**
     * Obtiene el valor de la propiedad colectivodiscapacitados.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCOLECTIVODISCAPACITADOS() {
        return colectivodiscapacitados;
    }

    /**
     * Define el valor de la propiedad colectivodiscapacitados.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCOLECTIVODISCAPACITADOS(String value) {
        this.colectivodiscapacitados = value;
    }

    /**
     * Obtiene el valor de la propiedad codigocolectivobonif.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCODIGOCOLECTIVOBONIF() {
        return codigocolectivobonif;
    }

    /**
     * Define el valor de la propiedad codigocolectivobonif.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCODIGOCOLECTIVOBONIF(String value) {
        this.codigocolectivobonif = value;
    }

}
