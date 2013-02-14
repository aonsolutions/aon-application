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
 * Nombre y apellidos.
 * 
 * <p>Clase Java para NOMBREAPELLIDOSTYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="NOMBREAPELLIDOSTYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NOMBRE">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="15"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="PRIMER_APELLIDO">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="20"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="SEGUNDO_APELLIDO" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="20"/>
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
@XmlType(name = "NOMBREAPELLIDOSTYPE", propOrder = {
    "nombre",
    "primerapellido",
    "segundoapellido"
})
public class NOMBREAPELLIDOSTYPE {

    @XmlElement(name = "NOMBRE", required = true)
    protected String nombre;
    @XmlElement(name = "PRIMER_APELLIDO", required = true)
    protected String primerapellido;
    @XmlElement(name = "SEGUNDO_APELLIDO")
    protected String segundoapellido;

    /**
     * Obtiene el valor de la propiedad nombre.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNOMBRE() {
        return nombre;
    }

    /**
     * Define el valor de la propiedad nombre.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNOMBRE(String value) {
        this.nombre = value;
    }

    /**
     * Obtiene el valor de la propiedad primerapellido.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRIMERAPELLIDO() {
        return primerapellido;
    }

    /**
     * Define el valor de la propiedad primerapellido.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRIMERAPELLIDO(String value) {
        this.primerapellido = value;
    }

    /**
     * Obtiene el valor de la propiedad segundoapellido.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSEGUNDOAPELLIDO() {
        return segundoapellido;
    }

    /**
     * Define el valor de la propiedad segundoapellido.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSEGUNDOAPELLIDO(String value) {
        this.segundoapellido = value;
    }

}
