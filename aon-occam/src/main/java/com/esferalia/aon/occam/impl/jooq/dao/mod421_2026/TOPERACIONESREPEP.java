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
 * 				Declaración informativa del volumen de operaciones en el
 * 				régimen especial del pequeño empresario o profesional.
 * 			
 * 
 * <p>Clase Java para T_OPERACIONES_REPEP complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_OPERACIONES_REPEP"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="EPE" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="ECM" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="ONS" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="OFC" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="IST" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="TOT" type="{}IMPA15Type" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "T_OPERACIONES_REPEP")
public class TOPERACIONESREPEP {

    @XmlAttribute(name = "EPE")
    protected String epe;
    @XmlAttribute(name = "ECM")
    protected String ecm;
    @XmlAttribute(name = "ONS")
    protected String ons;
    @XmlAttribute(name = "OFC")
    protected String ofc;
    @XmlAttribute(name = "IST")
    protected String ist;
    @XmlAttribute(name = "TOT")
    protected String tot;

    /**
     * Obtiene el valor de la propiedad epe.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEPE() {
        return epe;
    }

    /**
     * Define el valor de la propiedad epe.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEPE(String value) {
        this.epe = value;
    }

    /**
     * Obtiene el valor de la propiedad ecm.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getECM() {
        return ecm;
    }

    /**
     * Define el valor de la propiedad ecm.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setECM(String value) {
        this.ecm = value;
    }

    /**
     * Obtiene el valor de la propiedad ons.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getONS() {
        return ons;
    }

    /**
     * Define el valor de la propiedad ons.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setONS(String value) {
        this.ons = value;
    }

    /**
     * Obtiene el valor de la propiedad ofc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOFC() {
        return ofc;
    }

    /**
     * Define el valor de la propiedad ofc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOFC(String value) {
        this.ofc = value;
    }

    /**
     * Obtiene el valor de la propiedad ist.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIST() {
        return ist;
    }

    /**
     * Define el valor de la propiedad ist.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIST(String value) {
        this.ist = value;
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
