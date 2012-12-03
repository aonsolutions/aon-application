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
 * Datos del contrato transformado
 * 
 * <p>Clase Java para DATOS_CONTRATOTYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOS_CONTRATOTYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CLAVE_CONTRATO" type="{}CLAVECONTRATOTYPE" minOccurs="0"/>
 *         &lt;element name="IDENTIFICADORPFISICA" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="12"/>
 *               &lt;pattern value="[DEUW][0-9XYZ ]+\d{7}[A-Z]"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="FECHA_INICIO_CTO" type="{}FECHATYPE" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DATOS_CONTRATOTYPE", propOrder = {
    "clavecontrato",
    "identificadorpfisica",
    "fechainiciocto"
})
public class DATOSCONTRATOTYPE {

    @XmlElement(name = "CLAVE_CONTRATO")
    protected String clavecontrato;
    @XmlElement(name = "IDENTIFICADORPFISICA")
    protected String identificadorpfisica;
    @XmlElement(name = "FECHA_INICIO_CTO")
    protected String fechainiciocto;

    /**
     * Obtiene el valor de la propiedad clavecontrato.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCLAVECONTRATO() {
        return clavecontrato;
    }

    /**
     * Define el valor de la propiedad clavecontrato.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCLAVECONTRATO(String value) {
        this.clavecontrato = value;
    }

    /**
     * Obtiene el valor de la propiedad identificadorpfisica.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIDENTIFICADORPFISICA() {
        return identificadorpfisica;
    }

    /**
     * Define el valor de la propiedad identificadorpfisica.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIDENTIFICADORPFISICA(String value) {
        this.identificadorpfisica = value;
    }

    /**
     * Obtiene el valor de la propiedad fechainiciocto.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFECHAINICIOCTO() {
        return fechainiciocto;
    }

    /**
     * Define el valor de la propiedad fechainiciocto.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFECHAINICIOCTO(String value) {
        this.fechainiciocto = value;
    }

}
