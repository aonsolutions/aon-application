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
 * <p>Clase Java para tipo_RepresentanteJuridica complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_RepresentanteJuridica"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Nombre" type="{}tipo_NombreRepresentanteJuridica" minOccurs="0"/&gt;
 *         &lt;element name="NIF" type="{}tipo_Nif" minOccurs="0"/&gt;
 *         &lt;element name="FechaPoder" type="{}tipo_DiaMesAnno" minOccurs="0"/&gt;
 *         &lt;element name="Notaria" type="{}tipo_Notaria" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_RepresentanteJuridica", propOrder = {
    "nombre",
    "nif",
    "fechaPoder",
    "notaria"
})
public class TipoRepresentanteJuridica {

    @XmlElement(name = "Nombre")
    protected String nombre;
    @XmlElement(name = "NIF")
    protected String nif;
    @XmlElement(name = "FechaPoder")
    protected String fechaPoder;
    @XmlElement(name = "Notaria")
    protected String notaria;

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
     * Obtiene el valor de la propiedad fechaPoder.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaPoder() {
        return fechaPoder;
    }

    /**
     * Define el valor de la propiedad fechaPoder.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaPoder(String value) {
        this.fechaPoder = value;
    }

    /**
     * Obtiene el valor de la propiedad notaria.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotaria() {
        return notaria;
    }

    /**
     * Define el valor de la propiedad notaria.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotaria(String value) {
        this.notaria = value;
    }

}
