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
 * Datos de los contratos de inserción.
 * 
 * <p>Clase Java para DATOS_CONTRATOINSERCIONTYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOS_CONTRATOINSERCIONTYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GRUPOCOTIZACIONSEGSOCIAL">
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
@XmlType(name = "DATOS_CONTRATOINSERCIONTYPE", propOrder = {
    "grupocotizacionsegsocial"
})
public class DATOSCONTRATOINSERCIONTYPE {

    @XmlElement(name = "GRUPOCOTIZACIONSEGSOCIAL", required = true)
    protected String grupocotizacionsegsocial;

    /**
     * Obtiene el valor de la propiedad grupocotizacionsegsocial.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGRUPOCOTIZACIONSEGSOCIAL() {
        return grupocotizacionsegsocial;
    }

    /**
     * Define el valor de la propiedad grupocotizacionsegsocial.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGRUPOCOTIZACIONSEGSOCIAL(String value) {
        this.grupocotizacionsegsocial = value;
    }

}
