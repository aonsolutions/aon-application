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
 * casillas régimen simplificado
 * 
 * <p>Clase Java para T_SIMPLIFICADO complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_SIMPLIFICADO"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="TRS" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="CAF" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="CBI" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="RCU" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="TOT" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="DAF" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="DBI" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="TOD" type="{}IMPA15Type" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "T_SIMPLIFICADO")
public class TSIMPLIFICADO {

    @XmlAttribute(name = "TRS")
    protected String trs;
    @XmlAttribute(name = "CAF")
    protected String caf;
    @XmlAttribute(name = "CBI")
    protected String cbi;
    @XmlAttribute(name = "RCU")
    protected String rcu;
    @XmlAttribute(name = "TOT")
    protected String tot;
    @XmlAttribute(name = "DAF")
    protected String daf;
    @XmlAttribute(name = "DBI")
    protected String dbi;
    @XmlAttribute(name = "TOD")
    protected String tod;

    /**
     * Obtiene el valor de la propiedad trs.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTRS() {
        return trs;
    }

    /**
     * Define el valor de la propiedad trs.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTRS(String value) {
        this.trs = value;
    }

    /**
     * Obtiene el valor de la propiedad caf.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCAF() {
        return caf;
    }

    /**
     * Define el valor de la propiedad caf.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCAF(String value) {
        this.caf = value;
    }

    /**
     * Obtiene el valor de la propiedad cbi.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCBI() {
        return cbi;
    }

    /**
     * Define el valor de la propiedad cbi.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCBI(String value) {
        this.cbi = value;
    }

    /**
     * Obtiene el valor de la propiedad rcu.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRCU() {
        return rcu;
    }

    /**
     * Define el valor de la propiedad rcu.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRCU(String value) {
        this.rcu = value;
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
     * Obtiene el valor de la propiedad daf.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDAF() {
        return daf;
    }

    /**
     * Define el valor de la propiedad daf.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDAF(String value) {
        this.daf = value;
    }

    /**
     * Obtiene el valor de la propiedad dbi.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDBI() {
        return dbi;
    }

    /**
     * Define el valor de la propiedad dbi.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDBI(String value) {
        this.dbi = value;
    }

    /**
     * Obtiene el valor de la propiedad tod.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTOD() {
        return tod;
    }

    /**
     * Define el valor de la propiedad tod.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTOD(String value) {
        this.tod = value;
    }

}
