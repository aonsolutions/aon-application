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
 * Datos de los contratos de exclusión social.
 * 
 * <p>Clase Java para DATOS_EXCLUSIONSOCIALTYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOS_EXCLUSIONSOCIALTYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MODALIDAD_EXCLUSION">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{3}"/>
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
@XmlType(name = "DATOS_EXCLUSIONSOCIALTYPE", propOrder = {
    "modalidadexclusion"
})
public class DATOSEXCLUSIONSOCIALTYPE {

    @XmlElement(name = "MODALIDAD_EXCLUSION", required = true)
    protected String modalidadexclusion;

    /**
     * Obtiene el valor de la propiedad modalidadexclusion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMODALIDADEXCLUSION() {
        return modalidadexclusion;
    }

    /**
     * Define el valor de la propiedad modalidadexclusion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMODALIDADEXCLUSION(String value) {
        this.modalidadexclusion = value;
    }

}
