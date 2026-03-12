//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.1-b171012.0423 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.02.26 a las 01:00:22 PM CET 
//


package com.esferalia.aon.occam.impl.jooq.dao.mod421_2026;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para T_DEVENGADO complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_DEVENGADO"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DEV" type="{}T_BASE_CUOTA" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="DEG" type="{}T_BASE_CUOTA" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="OIN" type="{}T_BASE_CUOTA" minOccurs="0"/&gt;
 *         &lt;element name="MBC" type="{}T_BASE_CUOTA" minOccurs="0"/&gt;
 *         &lt;element name="GBC" type="{}T_BASE_CUOTA" minOccurs="0"/&gt;
 *         &lt;element name="CAT" type="{}T_BASE_CUOTA" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="TOT" type="{}IMPA15Type" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "T_DEVENGADO", propOrder = {
    "dev",
    "deg",
    "oin",
    "mbc",
    "gbc",
    "cat"
})
public class TDEVENGADO {

    @XmlElement(name = "DEV")
    protected List<TBASECUOTA> dev;
    @XmlElement(name = "DEG")
    protected List<TBASECUOTA> deg;
    @XmlElement(name = "OIN")
    protected TBASECUOTA oin;
    @XmlElement(name = "MBC")
    protected TBASECUOTA mbc;
    @XmlElement(name = "GBC")
    protected TBASECUOTA gbc;
    @XmlElement(name = "CAT")
    protected TBASECUOTA cat;
    @XmlAttribute(name = "TOT")
    protected String tot;

    /**
     * Gets the value of the dev property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dev property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDEV().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TBASECUOTA }
     * 
     * 
     */
    public List<TBASECUOTA> getDEV() {
        if (dev == null) {
            dev = new ArrayList<TBASECUOTA>();
        }
        return this.dev;
    }

    /**
     * Gets the value of the deg property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the deg property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDEG().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TBASECUOTA }
     * 
     * 
     */
    public List<TBASECUOTA> getDEG() {
        if (deg == null) {
            deg = new ArrayList<TBASECUOTA>();
        }
        return this.deg;
    }

    /**
     * Obtiene el valor de la propiedad oin.
     * 
     * @return
     *     possible object is
     *     {@link TBASECUOTA }
     *     
     */
    public TBASECUOTA getOIN() {
        return oin;
    }

    /**
     * Define el valor de la propiedad oin.
     * 
     * @param value
     *     allowed object is
     *     {@link TBASECUOTA }
     *     
     */
    public void setOIN(TBASECUOTA value) {
        this.oin = value;
    }

    /**
     * Obtiene el valor de la propiedad mbc.
     * 
     * @return
     *     possible object is
     *     {@link TBASECUOTA }
     *     
     */
    public TBASECUOTA getMBC() {
        return mbc;
    }

    /**
     * Define el valor de la propiedad mbc.
     * 
     * @param value
     *     allowed object is
     *     {@link TBASECUOTA }
     *     
     */
    public void setMBC(TBASECUOTA value) {
        this.mbc = value;
    }

    /**
     * Obtiene el valor de la propiedad gbc.
     * 
     * @return
     *     possible object is
     *     {@link TBASECUOTA }
     *     
     */
    public TBASECUOTA getGBC() {
        return gbc;
    }

    /**
     * Define el valor de la propiedad gbc.
     * 
     * @param value
     *     allowed object is
     *     {@link TBASECUOTA }
     *     
     */
    public void setGBC(TBASECUOTA value) {
        this.gbc = value;
    }

    /**
     * Obtiene el valor de la propiedad cat.
     * 
     * @return
     *     possible object is
     *     {@link TBASECUOTA }
     *     
     */
    public TBASECUOTA getCAT() {
        return cat;
    }

    /**
     * Define el valor de la propiedad cat.
     * 
     * @param value
     *     allowed object is
     *     {@link TBASECUOTA }
     *     
     */
    public void setCAT(TBASECUOTA value) {
        this.cat = value;
    }

    /**
     * Obtiene el valor de la propiedad tot.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTOT() {
        return tot;
    }

    /**
     * Define el valor de la propiedad tot.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTOT(String value) {
        this.tot = value;
    }

}
