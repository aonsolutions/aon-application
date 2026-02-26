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
 * 				Se utilizará este elemento para almacenar
 * 				los datos
 * 				del medio de notificación empleado.
 * 			
 * 
 * <p>Clase Java para T_DIRECCION complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_DIRECCION"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="TDI" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="SVP" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;length value="2"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="SIG" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="NVP" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="TIP" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="NPK" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="CAL" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="BLO" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="ESC" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="PIS" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="PUE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="POR" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="COM" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="LOC" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="POP"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;length value="2"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="PRO" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="CMU" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="MUN" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="CP"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;length value="5"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="ZIP" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="EST" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="PAI"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;length value="2"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="TEL" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="MOV" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="FAX" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="EMA" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "T_DIRECCION")
public class TDIRECCION {

    @XmlAttribute(name = "TDI")
    protected String tdi;
    @XmlAttribute(name = "SVP", required = true)
    protected String svp;
    @XmlAttribute(name = "SIG")
    protected String sig;
    @XmlAttribute(name = "NVP")
    protected String nvp;
    @XmlAttribute(name = "TIP")
    protected String tip;
    @XmlAttribute(name = "NPK")
    protected String npk;
    @XmlAttribute(name = "CAL")
    protected String cal;
    @XmlAttribute(name = "BLO")
    protected String blo;
    @XmlAttribute(name = "ESC")
    protected String esc;
    @XmlAttribute(name = "PIS")
    protected String pis;
    @XmlAttribute(name = "PUE")
    protected String pue;
    @XmlAttribute(name = "POR")
    protected String por;
    @XmlAttribute(name = "COM")
    protected String com;
    @XmlAttribute(name = "LOC")
    protected String loc;
    @XmlAttribute(name = "POP")
    protected String pop;
    @XmlAttribute(name = "PRO")
    protected String pro;
    @XmlAttribute(name = "CMU")
    protected String cmu;
    @XmlAttribute(name = "MUN")
    protected String mun;
    @XmlAttribute(name = "CP")
    protected String cp;
    @XmlAttribute(name = "ZIP")
    protected String zip;
    @XmlAttribute(name = "EST")
    protected String est;
    @XmlAttribute(name = "PAI")
    protected String pai;
    @XmlAttribute(name = "TEL")
    protected String tel;
    @XmlAttribute(name = "MOV")
    protected String mov;
    @XmlAttribute(name = "FAX")
    protected String fax;
    @XmlAttribute(name = "EMA")
    protected String ema;

    /**
     * Obtiene el valor de la propiedad tdi.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTDI() {
        return tdi;
    }

    /**
     * Define el valor de la propiedad tdi.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTDI(String value) {
        this.tdi = value;
    }

    /**
     * Obtiene el valor de la propiedad svp.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSVP() {
        return svp;
    }

    /**
     * Define el valor de la propiedad svp.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSVP(String value) {
        this.svp = value;
    }

    /**
     * Obtiene el valor de la propiedad sig.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSIG() {
        return sig;
    }

    /**
     * Define el valor de la propiedad sig.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSIG(String value) {
        this.sig = value;
    }

    /**
     * Obtiene el valor de la propiedad nvp.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNVP() {
        return nvp;
    }

    /**
     * Define el valor de la propiedad nvp.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNVP(String value) {
        this.nvp = value;
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
     * Obtiene el valor de la propiedad npk.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNPK() {
        return npk;
    }

    /**
     * Define el valor de la propiedad npk.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNPK(String value) {
        this.npk = value;
    }

    /**
     * Obtiene el valor de la propiedad cal.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCAL() {
        return cal;
    }

    /**
     * Define el valor de la propiedad cal.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCAL(String value) {
        this.cal = value;
    }

    /**
     * Obtiene el valor de la propiedad blo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBLO() {
        return blo;
    }

    /**
     * Define el valor de la propiedad blo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBLO(String value) {
        this.blo = value;
    }

    /**
     * Obtiene el valor de la propiedad esc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getESC() {
        return esc;
    }

    /**
     * Define el valor de la propiedad esc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setESC(String value) {
        this.esc = value;
    }

    /**
     * Obtiene el valor de la propiedad pis.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPIS() {
        return pis;
    }

    /**
     * Define el valor de la propiedad pis.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPIS(String value) {
        this.pis = value;
    }

    /**
     * Obtiene el valor de la propiedad pue.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPUE() {
        return pue;
    }

    /**
     * Define el valor de la propiedad pue.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPUE(String value) {
        this.pue = value;
    }

    /**
     * Obtiene el valor de la propiedad por.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPOR() {
        return por;
    }

    /**
     * Define el valor de la propiedad por.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPOR(String value) {
        this.por = value;
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
     * Obtiene el valor de la propiedad loc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLOC() {
        return loc;
    }

    /**
     * Define el valor de la propiedad loc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLOC(String value) {
        this.loc = value;
    }

    /**
     * Obtiene el valor de la propiedad pop.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPOP() {
        return pop;
    }

    /**
     * Define el valor de la propiedad pop.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPOP(String value) {
        this.pop = value;
    }

    /**
     * Obtiene el valor de la propiedad pro.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRO() {
        return pro;
    }

    /**
     * Define el valor de la propiedad pro.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRO(String value) {
        this.pro = value;
    }

    /**
     * Obtiene el valor de la propiedad cmu.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCMU() {
        return cmu;
    }

    /**
     * Define el valor de la propiedad cmu.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCMU(String value) {
        this.cmu = value;
    }

    /**
     * Obtiene el valor de la propiedad mun.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMUN() {
        return mun;
    }

    /**
     * Define el valor de la propiedad mun.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMUN(String value) {
        this.mun = value;
    }

    /**
     * Obtiene el valor de la propiedad cp.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCP() {
        return cp;
    }

    /**
     * Define el valor de la propiedad cp.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCP(String value) {
        this.cp = value;
    }

    /**
     * Obtiene el valor de la propiedad zip.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZIP() {
        return zip;
    }

    /**
     * Define el valor de la propiedad zip.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZIP(String value) {
        this.zip = value;
    }

    /**
     * Obtiene el valor de la propiedad est.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEST() {
        return est;
    }

    /**
     * Define el valor de la propiedad est.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEST(String value) {
        this.est = value;
    }

    /**
     * Obtiene el valor de la propiedad pai.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPAI() {
        return pai;
    }

    /**
     * Define el valor de la propiedad pai.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPAI(String value) {
        this.pai = value;
    }

    /**
     * Obtiene el valor de la propiedad tel.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTEL() {
        return tel;
    }

    /**
     * Define el valor de la propiedad tel.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTEL(String value) {
        this.tel = value;
    }

    /**
     * Obtiene el valor de la propiedad mov.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMOV() {
        return mov;
    }

    /**
     * Define el valor de la propiedad mov.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMOV(String value) {
        this.mov = value;
    }

    /**
     * Obtiene el valor de la propiedad fax.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFAX() {
        return fax;
    }

    /**
     * Define el valor de la propiedad fax.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFAX(String value) {
        this.fax = value;
    }

    /**
     * Obtiene el valor de la propiedad ema.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEMA() {
        return ema;
    }

    /**
     * Define el valor de la propiedad ema.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEMA(String value) {
        this.ema = value;
    }

}
