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
 * Datos de bonificación en la cotización a la Seguridad Social.
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
 *         &lt;element name="CODIGO_COLECTIVO_BONIF" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="2"/>
 *               &lt;maxLength value="3"/>
 *               &lt;pattern value="([0-9])+"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="INDIC_EMPLEAD_AUTONOMO" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[12]"/>
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
    "codigocolectivobonif",
    "indicempleadautonomo"
})
public class DATOSBONIFICACIONTYPE {

    @XmlElement(name = "CODIGO_COLECTIVO_BONIF")
    protected String codigocolectivobonif;
    @XmlElement(name = "INDIC_EMPLEAD_AUTONOMO")
    protected String indicempleadautonomo;

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

    /**
     * Obtiene el valor de la propiedad indicempleadautonomo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINDICEMPLEADAUTONOMO() {
        return indicempleadautonomo;
    }

    /**
     * Define el valor de la propiedad indicempleadautonomo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINDICEMPLEADAUTONOMO(String value) {
        this.indicempleadautonomo = value;
    }

}
