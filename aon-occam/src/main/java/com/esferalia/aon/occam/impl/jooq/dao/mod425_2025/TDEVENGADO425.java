//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.1-b171012.0423 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2025.11.07 a las 01:49:16 PM CET 
//


package com.esferalia.aon.occam.impl.jooq.dao.mod425_2025;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para T_DEVENGADO_425 complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_DEVENGADO_425"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RGO" type="{}T_BASE_CUOTA" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="RBU" type="{}T_BASE_CUOTA" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="ROA" type="{}T_BASE_CUOTA" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="RCC" type="{}T_BASE_CUOTA" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="REA" type="{}T_BASE_CUOTA" minOccurs="0"/&gt;
 *         &lt;element name="MBC" type="{}T_BASE_CUOTA" minOccurs="0"/&gt;
 *         &lt;element name="MCA" type="{}T_BASE_CUOTA" minOccurs="0"/&gt;
 *         &lt;element name="OIN" type="{}T_BASE_CUOTA" minOccurs="0"/&gt;
 *         &lt;element name="CRV" type="{}T_BASE_CUOTA" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="TBA" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="TCU" type="{}IMPA15Type" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "T_DEVENGADO_425", propOrder = {
    "rgo",
    "rbu",
    "roa",
    "rcc",
    "rea",
    "mbc",
    "mca",
    "oin",
    "crv"
})
public class TDEVENGADO425 {

    @XmlElement(name = "RGO")
    protected List<TBASECUOTA> rgo;
    @XmlElement(name = "RBU")
    protected List<TBASECUOTA> rbu;
    @XmlElement(name = "ROA")
    protected List<TBASECUOTA> roa;
    @XmlElement(name = "RCC")
    protected List<TBASECUOTA> rcc;
    @XmlElement(name = "REA")
    protected TBASECUOTA rea;
    @XmlElement(name = "MBC")
    protected TBASECUOTA mbc;
    @XmlElement(name = "MCA")
    protected TBASECUOTA mca;
    @XmlElement(name = "OIN")
    protected TBASECUOTA oin;
    @XmlElement(name = "CRV")
    protected TBASECUOTA crv;
    @XmlAttribute(name = "TBA")
    protected String tba;
    @XmlAttribute(name = "TCU")
    protected String tcu;

    /**
     * Gets the value of the rgo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rgo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRGO().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TBASECUOTA }
     * 
     * 
     */
    public List<TBASECUOTA> getRGO() {
        if (rgo == null) {
            rgo = new ArrayList<TBASECUOTA>();
        }
        return this.rgo;
    }

    /**
     * Gets the value of the rbu property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rbu property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRBU().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TBASECUOTA }
     * 
     * 
     */
    public List<TBASECUOTA> getRBU() {
        if (rbu == null) {
            rbu = new ArrayList<TBASECUOTA>();
        }
        return this.rbu;
    }

    /**
     * Gets the value of the roa property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the roa property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getROA().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TBASECUOTA }
     * 
     * 
     */
    public List<TBASECUOTA> getROA() {
        if (roa == null) {
            roa = new ArrayList<TBASECUOTA>();
        }
        return this.roa;
    }

    /**
     * Gets the value of the rcc property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rcc property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRCC().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TBASECUOTA }
     * 
     * 
     */
    public List<TBASECUOTA> getRCC() {
        if (rcc == null) {
            rcc = new ArrayList<TBASECUOTA>();
        }
        return this.rcc;
    }

    /**
     * Obtiene el valor de la propiedad rea.
     * 
     * @return
     *     possible object is
     *     {@link TBASECUOTA }
     *     
     */
    public TBASECUOTA getREA() {
        return rea;
    }

    /**
     * Define el valor de la propiedad rea.
     * 
     * @param value
     *     allowed object is
     *     {@link TBASECUOTA }
     *     
     */
    public void setREA(TBASECUOTA value) {
        this.rea = value;
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
     * Obtiene el valor de la propiedad mca.
     * 
     * @return
     *     possible object is
     *     {@link TBASECUOTA }
     *     
     */
    public TBASECUOTA getMCA() {
        return mca;
    }

    /**
     * Define el valor de la propiedad mca.
     * 
     * @param value
     *     allowed object is
     *     {@link TBASECUOTA }
     *     
     */
    public void setMCA(TBASECUOTA value) {
        this.mca = value;
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
     * Obtiene el valor de la propiedad crv.
     * 
     * @return
     *     possible object is
     *     {@link TBASECUOTA }
     *     
     */
    public TBASECUOTA getCRV() {
        return crv;
    }

    /**
     * Define el valor de la propiedad crv.
     * 
     * @param value
     *     allowed object is
     *     {@link TBASECUOTA }
     *     
     */
    public void setCRV(TBASECUOTA value) {
        this.crv = value;
    }

    /**
     * Obtiene el valor de la propiedad tba.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTBA() {
        return tba;
    }

    /**
     * Define el valor de la propiedad tba.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTBA(String value) {
        this.tba = value;
    }

    /**
     * Obtiene el valor de la propiedad tcu.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTCU() {
        return tcu;
    }

    /**
     * Define el valor de la propiedad tcu.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTCU(String value) {
        this.tcu = value;
    }

}
