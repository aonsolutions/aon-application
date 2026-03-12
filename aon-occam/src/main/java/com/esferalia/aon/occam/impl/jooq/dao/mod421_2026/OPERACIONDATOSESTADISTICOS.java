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
 * <p>Clase Java para OPERACION_DATOS_ESTADISTICOS complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="OPERACION_DATOS_ESTADISTICOS"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="CLA"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;length value="1"/&gt;
 *             &lt;pattern value="[0-9]{1}"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="EPI" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="REG"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;length value="1"/&gt;
 *             &lt;pattern value="[0-9]{1}"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="PRO" type="{}IMPA5Type" /&gt;
 *       &lt;attribute name="DEF" type="{}IMPA5Type" /&gt;
 *       &lt;attribute name="ESP" type="{}CHECKType" /&gt;
 *       &lt;attribute name="TIP" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="IMP" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="IMD" type="{}IMPA15Type" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OPERACION_DATOS_ESTADISTICOS")
public class OPERACIONDATOSESTADISTICOS {

    @XmlAttribute(name = "CLA")
    protected String cla;
    @XmlAttribute(name = "EPI")
    protected String epi;
    @XmlAttribute(name = "REG")
    protected String reg;
    @XmlAttribute(name = "PRO")
    protected String pro;
    @XmlAttribute(name = "DEF")
    protected String def;
    @XmlAttribute(name = "ESP")
    protected String esp;
    @XmlAttribute(name = "TIP")
    protected String tip;
    @XmlAttribute(name = "IMP")
    protected String imp;
    @XmlAttribute(name = "IMD")
    protected String imd;

    /**
     * Obtiene el valor de la propiedad cla.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCLA() {
        return cla;
    }

    /**
     * Define el valor de la propiedad cla.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCLA(String value) {
        this.cla = value;
    }

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
     * Obtiene el valor de la propiedad reg.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREG() {
        return reg;
    }

    /**
     * Define el valor de la propiedad reg.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREG(String value) {
        this.reg = value;
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
     * Obtiene el valor de la propiedad def.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDEF() {
        return def;
    }

    /**
     * Define el valor de la propiedad def.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDEF(String value) {
        this.def = value;
    }

    /**
     * Obtiene el valor de la propiedad esp.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getESP() {
        return esp;
    }

    /**
     * Define el valor de la propiedad esp.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setESP(String value) {
        this.esp = value;
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
     * Obtiene el valor de la propiedad imp.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIMP() {
        return imp;
    }

    /**
     * Define el valor de la propiedad imp.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIMP(String value) {
        this.imp = value;
    }

    /**
     * Obtiene el valor de la propiedad imd.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIMD() {
        return imd;
    }

    /**
     * Define el valor de la propiedad imd.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIMD(String value) {
        this.imd = value;
    }

}
