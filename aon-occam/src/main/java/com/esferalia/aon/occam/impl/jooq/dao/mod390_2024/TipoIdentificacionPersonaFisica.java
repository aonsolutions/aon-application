//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.1-b171012.0423 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2024.11.25 a las 11:44:35 AM CET 
//


package com.esferalia.aon.occam.impl.jooq.dao.mod390_2024;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para tipo_IdentificacionPersonaFisica complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_IdentificacionPersonaFisica"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="NIF" type="{}tipo_Nif"/&gt;
 *         &lt;element name="Ape1" type="{}tipo_Nombre"/&gt;
 *         &lt;element name="Ape2" type="{}tipo_Nombre" minOccurs="0"/&gt;
 *         &lt;element name="Nombre" type="{}tipo_Nombre"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_IdentificacionPersonaFisica", propOrder = {
    "nif",
    "ape1",
    "ape2",
    "nombre"
})
public class TipoIdentificacionPersonaFisica {

    @XmlElement(name = "NIF", required = true)
    protected String nif;
    @XmlElement(name = "Ape1", required = true)
    protected String ape1;
    @XmlElement(name = "Ape2")
    protected String ape2;
    @XmlElement(name = "Nombre", required = true)
    protected String nombre;

    /**
     * Obtiene el valor de la propiedad nif.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNIF() {
        return nif;
    }

    /**
     * Define el valor de la propiedad nif.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNIF(String value) {
        this.nif = value;
    }

    /**
     * Obtiene el valor de la propiedad ape1.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApe1() {
        return ape1;
    }

    /**
     * Define el valor de la propiedad ape1.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApe1(String value) {
        this.ape1 = value;
    }

    /**
     * Obtiene el valor de la propiedad ape2.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApe2() {
        return ape2;
    }

    /**
     * Define el valor de la propiedad ape2.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApe2(String value) {
        this.ape2 = value;
    }

    /**
     * Obtiene el valor de la propiedad nombre.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombre() {
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
    public void setNombre(String value) {
        this.nombre = value;
    }

}
