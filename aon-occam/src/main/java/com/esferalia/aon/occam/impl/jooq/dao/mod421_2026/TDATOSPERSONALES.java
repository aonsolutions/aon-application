//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.1-b171012.0423 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.02.26 a las 01:00:22 PM CET 
//


package com.esferalia.aon.occam.impl.jooq.dao.mod421_2026;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Se utilizará este elemento para almacenar los datos
 * 				de cualquiera de las personas que figuran en la declaración.
 * 			
 * 
 * <p>Clase Java para T_DATOS_PERSONALES complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_DATOS_PERSONALES"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="NIF" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="NRS" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="DEN" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="FNA"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;pattern value="\d{2}/\d{2}/\d{4}"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="FFA"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;pattern value="\d{2}/\d{2}/\d{4}"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "T_DATOS_PERSONALES")
public class TDATOSPERSONALES {

    @XmlAttribute(name = "NIF", required = true)
    protected String nif;
    @XmlAttribute(name = "NRS", required = true)
    protected String nrs;
    @XmlAttribute(name = "DEN")
    protected String den;
    @XmlAttribute(name = "FNA")
    protected String fna;
    @XmlAttribute(name = "FFA")
    protected String ffa;

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
     * Obtiene el valor de la propiedad nrs.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNRS() {
        return nrs;
    }

    /**
     * Define el valor de la propiedad nrs.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNRS(String value) {
        this.nrs = value;
    }

    /**
     * Obtiene el valor de la propiedad den.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDEN() {
        return den;
    }

    /**
     * Define el valor de la propiedad den.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDEN(String value) {
        this.den = value;
    }

    /**
     * Obtiene el valor de la propiedad fna.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFNA() {
        return fna;
    }

    /**
     * Define el valor de la propiedad fna.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFNA(String value) {
        this.fna = value;
    }

    /**
     * Obtiene el valor de la propiedad ffa.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFFA() {
        return ffa;
    }

    /**
     * Define el valor de la propiedad ffa.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFFA(String value) {
        this.ffa = value;
    }

}
