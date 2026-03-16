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
 * <p>Clase Java para T_MODULO complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_MODULO"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="EPI" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="SEC" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="MOD1" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="MOD2" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="MOD3" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="MOD4" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="MOD5" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="MOD6" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="MOD7" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="TOT" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="CASA" use="required" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="CASB" use="required" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="CASC" use="required" type="{}IMPA5Type" /&gt;
 *       &lt;attribute name="CASD" use="required" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="CASE" use="required" type="{}IMPA5Type" /&gt;
 *       &lt;attribute name="CASF" use="required" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="CASG" type="{}IMPA15Type" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "T_MODULO")
public class TMODULO {

    @XmlAttribute(name = "EPI")
    protected String epi;
    @XmlAttribute(name = "SEC")
    protected String sec;
    @XmlAttribute(name = "MOD1")
    protected String mod1;
    @XmlAttribute(name = "MOD2")
    protected String mod2;
    @XmlAttribute(name = "MOD3")
    protected String mod3;
    @XmlAttribute(name = "MOD4")
    protected String mod4;
    @XmlAttribute(name = "MOD5")
    protected String mod5;
    @XmlAttribute(name = "MOD6")
    protected String mod6;
    @XmlAttribute(name = "MOD7")
    protected String mod7;
    @XmlAttribute(name = "TOT")
    protected String tot;
    @XmlAttribute(name = "CASA", required = true)
    protected String casa;
    @XmlAttribute(name = "CASB", required = true)
    protected String casb;
    @XmlAttribute(name = "CASC", required = true)
    protected String casc;
    @XmlAttribute(name = "CASD", required = true)
    protected String casd;
    @XmlAttribute(name = "CASE", required = true)
    protected String _case;
    @XmlAttribute(name = "CASF", required = true)
    protected String casf;
    @XmlAttribute(name = "CASG")
    protected String casg;

    /**
     * Obtiene el valor de la propiedad epi.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEPI() {
        return epi;
    }

    /**
     * Define el valor de la propiedad epi.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEPI(String value) {
        this.epi = value;
    }

    /**
     * Obtiene el valor de la propiedad sec.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSEC() {
        return sec;
    }

    /**
     * Define el valor de la propiedad sec.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSEC(String value) {
        this.sec = value;
    }

    /**
     * Obtiene el valor de la propiedad mod1.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMOD1() {
        return mod1;
    }

    /**
     * Define el valor de la propiedad mod1.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMOD1(String value) {
        this.mod1 = value;
    }

    /**
     * Obtiene el valor de la propiedad mod2.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMOD2() {
        return mod2;
    }

    /**
     * Define el valor de la propiedad mod2.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMOD2(String value) {
        this.mod2 = value;
    }

    /**
     * Obtiene el valor de la propiedad mod3.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMOD3() {
        return mod3;
    }

    /**
     * Define el valor de la propiedad mod3.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMOD3(String value) {
        this.mod3 = value;
    }

    /**
     * Obtiene el valor de la propiedad mod4.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMOD4() {
        return mod4;
    }

    /**
     * Define el valor de la propiedad mod4.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMOD4(String value) {
        this.mod4 = value;
    }

    /**
     * Obtiene el valor de la propiedad mod5.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMOD5() {
        return mod5;
    }

    /**
     * Define el valor de la propiedad mod5.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMOD5(String value) {
        this.mod5 = value;
    }

    /**
     * Obtiene el valor de la propiedad mod6.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMOD6() {
        return mod6;
    }

    /**
     * Define el valor de la propiedad mod6.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMOD6(String value) {
        this.mod6 = value;
    }

    /**
     * Obtiene el valor de la propiedad mod7.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMOD7() {
        return mod7;
    }

    /**
     * Define el valor de la propiedad mod7.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMOD7(String value) {
        this.mod7 = value;
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

    /**
     * Obtiene el valor de la propiedad casa.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCASA() {
        return casa;
    }

    /**
     * Define el valor de la propiedad casa.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCASA(String value) {
        this.casa = value;
    }

    /**
     * Obtiene el valor de la propiedad casb.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCASB() {
        return casb;
    }

    /**
     * Define el valor de la propiedad casb.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCASB(String value) {
        this.casb = value;
    }

    /**
     * Obtiene el valor de la propiedad casc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCASC() {
        return casc;
    }

    /**
     * Define el valor de la propiedad casc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCASC(String value) {
        this.casc = value;
    }

    /**
     * Obtiene el valor de la propiedad casd.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCASD() {
        return casd;
    }

    /**
     * Define el valor de la propiedad casd.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCASD(String value) {
        this.casd = value;
    }

    /**
     * Obtiene el valor de la propiedad case.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCASE() {
        return _case;
    }

    /**
     * Define el valor de la propiedad case.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCASE(String value) {
        this._case = value;
    }

    /**
     * Obtiene el valor de la propiedad casf.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCASF() {
        return casf;
    }

    /**
     * Define el valor de la propiedad casf.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCASF(String value) {
        this.casf = value;
    }

    /**
     * Obtiene el valor de la propiedad casg.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCASG() {
        return casg;
    }

    /**
     * Define el valor de la propiedad casg.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCASG(String value) {
        this.casg = value;
    }

}
