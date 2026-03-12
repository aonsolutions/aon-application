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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 					Nodo principal de la declaración, es común a todas las declaraciones
 *     			
 * 
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AUX" type="{}T_AUXILIAR" minOccurs="0"/&gt;
 *         &lt;element name="OTP" type="{}DATOS_PERSONALES" maxOccurs="unbounded"/&gt;
 *         &lt;element name="AUT" type="{}T_AUTOLIQUIDACION"/&gt;
 *         &lt;element name="RESULTADO_LIQUIDACION" type="{}RESULTADO_LIQUIDACION"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="ADM" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;length value="5"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="NDE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="MOD" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="ANY" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;length value="4"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="PER" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;length value="2"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="FIM"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;length value="8"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="COM"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;enumeration value="X"/&gt;
 *             &lt;enumeration value=""/&gt;
 *             &lt;minLength value="0"/&gt;
 *             &lt;maxLength value="1"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="NJA" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="ACR"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;enumeration value="X"/&gt;
 *             &lt;enumeration value=""/&gt;
 *             &lt;minLength value="0"/&gt;
 *             &lt;maxLength value="1"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="VER" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "aux",
    "otp",
    "aut",
    "resultadoliquidacion"
})
@XmlRootElement(name = "DEC")
public class DEC {

    @XmlElement(name = "AUX")
    protected TAUXILIAR aux;
    @XmlElement(name = "OTP", required = true)
    protected List<DATOSPERSONALES> otp;
    @XmlElement(name = "AUT", required = true)
    protected TAUTOLIQUIDACION aut;
    @XmlElement(name = "RESULTADO_LIQUIDACION", required = true)
    protected RESULTADOLIQUIDACION resultadoliquidacion;
    @XmlAttribute(name = "ADM", required = true)
    protected String adm;
    @XmlAttribute(name = "NDE")
    protected String nde;
    @XmlAttribute(name = "MOD", required = true)
    protected String mod;
    @XmlAttribute(name = "ANY", required = true)
    protected String any;
    @XmlAttribute(name = "PER", required = true)
    protected String per;
    @XmlAttribute(name = "FIM")
    protected String fim;
    @XmlAttribute(name = "COM")
    protected String com;
    @XmlAttribute(name = "NJA")
    protected String nja;
    @XmlAttribute(name = "ACR")
    protected String acr;
    @XmlAttribute(name = "VER")
    protected String ver;

    /**
     * Obtiene el valor de la propiedad aux.
     * 
     * @return
     *     possible object is
     *     {@link TAUXILIAR }
     *     
     */
    public TAUXILIAR getAUX() {
        return aux;
    }

    /**
     * Define el valor de la propiedad aux.
     * 
     * @param value
     *     allowed object is
     *     {@link TAUXILIAR }
     *     
     */
    public void setAUX(TAUXILIAR value) {
        this.aux = value;
    }

    /**
     * Gets the value of the otp property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the otp property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOTP().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DATOSPERSONALES }
     * 
     * 
     */
    public List<DATOSPERSONALES> getOTP() {
        if (otp == null) {
            otp = new ArrayList<DATOSPERSONALES>();
        }
        return this.otp;
    }

    /**
     * Obtiene el valor de la propiedad aut.
     * 
     * @return
     *     possible object is
     *     {@link TAUTOLIQUIDACION }
     *     
     */
    public TAUTOLIQUIDACION getAUT() {
        return aut;
    }

    /**
     * Define el valor de la propiedad aut.
     * 
     * @param value
     *     allowed object is
     *     {@link TAUTOLIQUIDACION }
     *     
     */
    public void setAUT(TAUTOLIQUIDACION value) {
        this.aut = value;
    }

    /**
     * Obtiene el valor de la propiedad resultadoliquidacion.
     * 
     * @return
     *     possible object is
     *     {@link RESULTADOLIQUIDACION }
     *     
     */
    public RESULTADOLIQUIDACION getRESULTADOLIQUIDACION() {
        return resultadoliquidacion;
    }

    /**
     * Define el valor de la propiedad resultadoliquidacion.
     * 
     * @param value
     *     allowed object is
     *     {@link RESULTADOLIQUIDACION }
     *     
     */
    public void setRESULTADOLIQUIDACION(RESULTADOLIQUIDACION value) {
        this.resultadoliquidacion = value;
    }

    /**
     * Obtiene el valor de la propiedad adm.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getADM() {
        return adm;
    }

    /**
     * Define el valor de la propiedad adm.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setADM(String value) {
        this.adm = value;
    }

    /**
     * Obtiene el valor de la propiedad nde.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNDE() {
        return nde;
    }

    /**
     * Define el valor de la propiedad nde.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNDE(String value) {
        this.nde = value;
    }

    /**
     * Obtiene el valor de la propiedad mod.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMOD() {
        return mod;
    }

    /**
     * Define el valor de la propiedad mod.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMOD(String value) {
        this.mod = value;
    }

    /**
     * Obtiene el valor de la propiedad any.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getANY() {
        return any;
    }

    /**
     * Define el valor de la propiedad any.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setANY(String value) {
        this.any = value;
    }

    /**
     * Obtiene el valor de la propiedad per.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPER() {
        return per;
    }

    /**
     * Define el valor de la propiedad per.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPER(String value) {
        this.per = value;
    }

    /**
     * Obtiene el valor de la propiedad fim.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFIM() {
        return fim;
    }

    /**
     * Define el valor de la propiedad fim.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFIM(String value) {
        this.fim = value;
    }

    /**
     * Obtiene el valor de la propiedad com.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCOM() {
        return com;
    }

    /**
     * Define el valor de la propiedad com.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCOM(String value) {
        this.com = value;
    }

    /**
     * Obtiene el valor de la propiedad nja.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNJA() {
        return nja;
    }

    /**
     * Define el valor de la propiedad nja.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNJA(String value) {
        this.nja = value;
    }

    /**
     * Obtiene el valor de la propiedad acr.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getACR() {
        return acr;
    }

    /**
     * Define el valor de la propiedad acr.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setACR(String value) {
        this.acr = value;
    }

    /**
     * Obtiene el valor de la propiedad ver.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVER() {
        return ver;
    }

    /**
     * Define el valor de la propiedad ver.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVER(String value) {
        this.ver = value;
    }

}
