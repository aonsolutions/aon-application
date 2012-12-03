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
 * Datos de la empresa que prorroga
 * 
 * <p>Clase Java para DATOS_EMPRESATYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOS_EMPRESATYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CIF_NIF_EMPRESA" type="{}CIFNIFTYPE"/>
 *         &lt;element name="CCC">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{15}"/>
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
@XmlType(name = "DATOS_EMPRESATYPE", propOrder = {
    "cifnifempresa",
    "ccc"
})
public class DATOSEMPRESATYPE {

    @XmlElement(name = "CIF_NIF_EMPRESA", required = true)
    protected CIFNIFTYPE cifnifempresa;
    @XmlElement(name = "CCC", required = true)
    protected String ccc;

    /**
     * Obtiene el valor de la propiedad cifnifempresa.
     * 
     * @return
     *     possible object is
     *     {@link CIFNIFTYPE }
     *     
     */
    public CIFNIFTYPE getCIFNIFEMPRESA() {
        return cifnifempresa;
    }

    /**
     * Define el valor de la propiedad cifnifempresa.
     * 
     * @param value
     *     allowed object is
     *     {@link CIFNIFTYPE }
     *     
     */
    public void setCIFNIFEMPRESA(CIFNIFTYPE value) {
        this.cifnifempresa = value;
    }

    /**
     * Obtiene el valor de la propiedad ccc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCCC() {
        return ccc;
    }

    /**
     * Define el valor de la propiedad ccc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCCC(String value) {
        this.ccc = value;
    }

}
