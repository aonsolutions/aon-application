//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.5-2 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: PM.11.26 a las 07:11:31 PM CET 
//


package com.esferalia.aon.file.payroll.contrata.model.prorrogas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Datos para uso de la empresa. No se efectúa ninguna validación ni modificación sobre estos datos.
 * 
 * <p>Clase Java para DATOS_USOLIBRE_EMPRESATYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOS_USOLIBRE_EMPRESATYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="USOLIBRE_EMPRESA">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="30"/>
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
@XmlType(name = "DATOS_USOLIBRE_EMPRESATYPE", propOrder = {
    "usolibreempresa"
})
public class DATOSUSOLIBREEMPRESATYPE {

    @XmlElement(name = "USOLIBRE_EMPRESA", required = true)
    protected String usolibreempresa;

    /**
     * Obtiene el valor de la propiedad usolibreempresa.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUSOLIBREEMPRESA() {
        return usolibreempresa;
    }

    /**
     * Define el valor de la propiedad usolibreempresa.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUSOLIBREEMPRESA(String value) {
        this.usolibreempresa = value;
    }

}
