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
 * Datos adicionales de la transformación
 * 
 * <p>Clase Java para DATOS_ADICIONALES_TRANSFORMACIONTYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOS_ADICIONALES_TRANSFORMACIONTYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IND_DISCAPACIDAD" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[SC\s]"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CODIGO_COLECTIVO_REDUCCION" minOccurs="0">
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
@XmlType(name = "DATOS_ADICIONALES_TRANSFORMACIONTYPE", propOrder = {
    "inddiscapacidad",
    "codigocolectivoreduccion"
})
public class DATOSADICIONALESTRANSFORMACIONTYPE {

    @XmlElement(name = "IND_DISCAPACIDAD")
    protected String inddiscapacidad;
    @XmlElement(name = "CODIGO_COLECTIVO_REDUCCION")
    protected String codigocolectivoreduccion;

    /**
     * Obtiene el valor de la propiedad inddiscapacidad.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINDDISCAPACIDAD() {
        return inddiscapacidad;
    }

    /**
     * Define el valor de la propiedad inddiscapacidad.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINDDISCAPACIDAD(String value) {
        this.inddiscapacidad = value;
    }

    /**
     * Obtiene el valor de la propiedad codigocolectivoreduccion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCODIGOCOLECTIVOREDUCCION() {
        return codigocolectivoreduccion;
    }

    /**
     * Define el valor de la propiedad codigocolectivoreduccion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCODIGOCOLECTIVOREDUCCION(String value) {
        this.codigocolectivoreduccion = value;
    }

}
