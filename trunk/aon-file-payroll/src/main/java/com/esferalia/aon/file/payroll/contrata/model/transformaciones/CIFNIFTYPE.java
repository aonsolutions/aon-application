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
 * CIF / NIF.
 * 
 * <p>Clase Java para CIFNIFTYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="CIFNIFTYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CIF_NIF">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[0-9A-Z]\d{7}[0-9A-Z]"/>
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
@XmlType(name = "CIFNIFTYPE", propOrder = {
    "cifnif"
})
public class CIFNIFTYPE {

    @XmlElement(name = "CIF_NIF", required = true)
    protected String cifnif;

    /**
     * Obtiene el valor de la propiedad cifnif.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCIFNIF() {
        return cifnif;
    }

    /**
     * Define el valor de la propiedad cifnif.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCIFNIF(String value) {
        this.cifnif = value;
    }

}
