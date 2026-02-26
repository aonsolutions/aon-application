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
 * <p>Clase Java para T_AUXILIAR complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_AUXILIAR"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="COL"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="SIS"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;minLength value="1"/&gt;
 *             &lt;maxLength value="1"/&gt;
 *             &lt;enumeration value="1"/&gt;
 *             &lt;enumeration value="2"/&gt;
 *             &lt;enumeration value="3"/&gt;
 *             &lt;enumeration value="9"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="VIC"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="ORG"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;minLength value="2"/&gt;
 *             &lt;maxLength value="2"/&gt;
 *             &lt;enumeration value="PA"/&gt;
 *             &lt;enumeration value="MI"/&gt;
 *             &lt;enumeration value="ML"/&gt;
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
@XmlType(name = "T_AUXILIAR")
public class TAUXILIAR {

    @XmlAttribute(name = "COL")
    protected String col;
    @XmlAttribute(name = "SIS")
    protected String sis;
    @XmlAttribute(name = "VIC")
    protected String vic;
    @XmlAttribute(name = "ORG")
    protected String org;

    /**
     * Obtiene el valor de la propiedad col.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCOL() {
        return col;
    }

    /**
     * Define el valor de la propiedad col.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCOL(String value) {
        this.col = value;
    }

    /**
     * Obtiene el valor de la propiedad sis.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSIS() {
        return sis;
    }

    /**
     * Define el valor de la propiedad sis.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSIS(String value) {
        this.sis = value;
    }

    /**
     * Obtiene el valor de la propiedad vic.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVIC() {
        return vic;
    }

    /**
     * Define el valor de la propiedad vic.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVIC(String value) {
        this.vic = value;
    }

    /**
     * Obtiene el valor de la propiedad org.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getORG() {
        return org;
    }

    /**
     * Define el valor de la propiedad org.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setORG(String value) {
        this.org = value;
    }

}
