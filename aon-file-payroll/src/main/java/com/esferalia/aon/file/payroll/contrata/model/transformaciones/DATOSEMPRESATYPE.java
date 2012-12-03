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
 * Datos de la empresa que realiza la transformación
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
 *         &lt;element name="CODIGO_CUENTA_COTIZACION">
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
    "codigocuentacotizacion"
})
public class DATOSEMPRESATYPE {

    @XmlElement(name = "CIF_NIF_EMPRESA", required = true)
    protected CIFNIFTYPE cifnifempresa;
    @XmlElement(name = "CODIGO_CUENTA_COTIZACION", required = true)
    protected String codigocuentacotizacion;

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
     * Obtiene el valor de la propiedad codigocuentacotizacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCODIGOCUENTACOTIZACION() {
        return codigocuentacotizacion;
    }

    /**
     * Define el valor de la propiedad codigocuentacotizacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCODIGOCUENTACOTIZACION(String value) {
        this.codigocuentacotizacion = value;
    }

}
