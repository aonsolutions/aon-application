//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.1-b171012.0423 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2025.12.18 a las 11:51:10 AM CET 
//


package com.esferalia.aon.occam.impl.jooq.dao.mod425_2025;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para T_REGIMEN_GENERAL_425 complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_REGIMEN_GENERAL_425"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DEV" type="{}T_DEVENGADO_425" minOccurs="0"/&gt;
 *         &lt;element name="DED" type="{}T_DEDUCIBLE" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="RES" type="{}IMPA15Type" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "T_REGIMEN_GENERAL_425", propOrder = {
    "dev",
    "ded"
})
public class TREGIMENGENERAL425 {

    @XmlElement(name = "DEV")
    protected TDEVENGADO425 dev;
    @XmlElement(name = "DED")
    protected TDEDUCIBLE ded;
    @XmlAttribute(name = "RES")
    protected String res;

    /**
     * Obtiene el valor de la propiedad dev.
     * 
     * @return
     *     possible object is
     *     {@link TDEVENGADO425 }
     *     
     */
    public TDEVENGADO425 getDEV() {
        return dev;
    }

    /**
     * Define el valor de la propiedad dev.
     * 
     * @param value
     *     allowed object is
     *     {@link TDEVENGADO425 }
     *     
     */
    public void setDEV(TDEVENGADO425 value) {
        this.dev = value;
    }

    /**
     * Obtiene el valor de la propiedad ded.
     * 
     * @return
     *     possible object is
     *     {@link TDEDUCIBLE }
     *     
     */
    public TDEDUCIBLE getDED() {
        return ded;
    }

    /**
     * Define el valor de la propiedad ded.
     * 
     * @param value
     *     allowed object is
     *     {@link TDEDUCIBLE }
     *     
     */
    public void setDED(TDEDUCIBLE value) {
        this.ded = value;
    }

    /**
     * Obtiene el valor de la propiedad res.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRES() {
        return res;
    }

    /**
     * Define el valor de la propiedad res.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRES(String value) {
        this.res = value;
    }

}
