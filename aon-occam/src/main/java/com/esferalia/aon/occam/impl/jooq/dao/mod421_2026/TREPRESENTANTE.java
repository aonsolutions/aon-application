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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para T_REPRESENTANTE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_REPRESENTANTE"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="OTP" type="{}T_PERSONA"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="CAP" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="TIP" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="CAR" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="TIT" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="OTR" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="NOT" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="FPO"&gt;
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
@XmlType(name = "T_REPRESENTANTE", propOrder = {
    "otp"
})
public class TREPRESENTANTE {

    @XmlElement(name = "OTP", required = true)
    protected TPERSONA otp;
    @XmlAttribute(name = "CAP")
    protected String cap;
    @XmlAttribute(name = "TIP")
    protected String tip;
    @XmlAttribute(name = "CAR")
    protected String car;
    @XmlAttribute(name = "TIT")
    protected String tit;
    @XmlAttribute(name = "OTR")
    protected String otr;
    @XmlAttribute(name = "NOT")
    protected String not;
    @XmlAttribute(name = "FPO")
    protected String fpo;

    /**
     * Obtiene el valor de la propiedad otp.
     * 
     * @return
     *     possible object is
     *     {@link TPERSONA }
     *     
     */
    public TPERSONA getOTP() {
        return otp;
    }

    /**
     * Define el valor de la propiedad otp.
     * 
     * @param value
     *     allowed object is
     *     {@link TPERSONA }
     *     
     */
    public void setOTP(TPERSONA value) {
        this.otp = value;
    }

    /**
     * Obtiene el valor de la propiedad cap.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCAP() {
        return cap;
    }

    /**
     * Define el valor de la propiedad cap.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCAP(String value) {
        this.cap = value;
    }

    /**
     * Obtiene el valor de la propiedad tip.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTIP() {
        return tip;
    }

    /**
     * Define el valor de la propiedad tip.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTIP(String value) {
        this.tip = value;
    }

    /**
     * Obtiene el valor de la propiedad car.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCAR() {
        return car;
    }

    /**
     * Define el valor de la propiedad car.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCAR(String value) {
        this.car = value;
    }

    /**
     * Obtiene el valor de la propiedad tit.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTIT() {
        return tit;
    }

    /**
     * Define el valor de la propiedad tit.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTIT(String value) {
        this.tit = value;
    }

    /**
     * Obtiene el valor de la propiedad otr.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOTR() {
        return otr;
    }

    /**
     * Define el valor de la propiedad otr.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOTR(String value) {
        this.otr = value;
    }

    /**
     * Obtiene el valor de la propiedad not.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNOT() {
        return not;
    }

    /**
     * Define el valor de la propiedad not.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNOT(String value) {
        this.not = value;
    }

    /**
     * Obtiene el valor de la propiedad fpo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFPO() {
        return fpo;
    }

    /**
     * Define el valor de la propiedad fpo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFPO(String value) {
        this.fpo = value;
    }

}
