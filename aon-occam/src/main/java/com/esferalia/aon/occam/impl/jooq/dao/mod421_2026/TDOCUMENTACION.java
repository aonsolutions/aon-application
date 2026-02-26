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
 * <p>Clase Java para T_DOCUMENTACION complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_DOCUMENTACION"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="DO_AU_MOF" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="DO_GA_CAS" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="DO_GA_CSC" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="DO_GA_OTG" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="DO_OTR_DOC" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="DO_ODS_FAE" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="DO_ODS_JDO" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="DO_ODS_FAR" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="DO_ODS_COP" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "T_DOCUMENTACION")
public class TDOCUMENTACION {

    @XmlAttribute(name = "DO_AU_MOF")
    protected Boolean doaumof;
    @XmlAttribute(name = "DO_GA_CAS")
    protected Boolean dogacas;
    @XmlAttribute(name = "DO_GA_CSC")
    protected Boolean dogacsc;
    @XmlAttribute(name = "DO_GA_OTG")
    protected Boolean dogaotg;
    @XmlAttribute(name = "DO_OTR_DOC", required = true)
    protected String dootrdoc;
    @XmlAttribute(name = "DO_ODS_FAE")
    protected Boolean doodsfae;
    @XmlAttribute(name = "DO_ODS_JDO")
    protected Boolean doodsjdo;
    @XmlAttribute(name = "DO_ODS_FAR")
    protected Boolean doodsfar;
    @XmlAttribute(name = "DO_ODS_COP")
    protected Boolean doodscop;

    /**
     * Obtiene el valor de la propiedad doaumof.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isDOAUMOF() {
        if (doaumof == null) {
            return false;
        } else {
            return doaumof;
        }
    }

    /**
     * Define el valor de la propiedad doaumof.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDOAUMOF(Boolean value) {
        this.doaumof = value;
    }

    /**
     * Obtiene el valor de la propiedad dogacas.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isDOGACAS() {
        if (dogacas == null) {
            return false;
        } else {
            return dogacas;
        }
    }

    /**
     * Define el valor de la propiedad dogacas.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDOGACAS(Boolean value) {
        this.dogacas = value;
    }

    /**
     * Obtiene el valor de la propiedad dogacsc.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isDOGACSC() {
        if (dogacsc == null) {
            return false;
        } else {
            return dogacsc;
        }
    }

    /**
     * Define el valor de la propiedad dogacsc.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDOGACSC(Boolean value) {
        this.dogacsc = value;
    }

    /**
     * Obtiene el valor de la propiedad dogaotg.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isDOGAOTG() {
        if (dogaotg == null) {
            return false;
        } else {
            return dogaotg;
        }
    }

    /**
     * Define el valor de la propiedad dogaotg.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDOGAOTG(Boolean value) {
        this.dogaotg = value;
    }

    /**
     * Obtiene el valor de la propiedad dootrdoc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDOOTRDOC() {
        return dootrdoc;
    }

    /**
     * Define el valor de la propiedad dootrdoc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDOOTRDOC(String value) {
        this.dootrdoc = value;
    }

    /**
     * Obtiene el valor de la propiedad doodsfae.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isDOODSFAE() {
        if (doodsfae == null) {
            return false;
        } else {
            return doodsfae;
        }
    }

    /**
     * Define el valor de la propiedad doodsfae.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDOODSFAE(Boolean value) {
        this.doodsfae = value;
    }

    /**
     * Obtiene el valor de la propiedad doodsjdo.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isDOODSJDO() {
        if (doodsjdo == null) {
            return false;
        } else {
            return doodsjdo;
        }
    }

    /**
     * Define el valor de la propiedad doodsjdo.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDOODSJDO(Boolean value) {
        this.doodsjdo = value;
    }

    /**
     * Obtiene el valor de la propiedad doodsfar.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isDOODSFAR() {
        if (doodsfar == null) {
            return false;
        } else {
            return doodsfar;
        }
    }

    /**
     * Define el valor de la propiedad doodsfar.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDOODSFAR(Boolean value) {
        this.doodsfar = value;
    }

    /**
     * Obtiene el valor de la propiedad doodscop.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isDOODSCOP() {
        if (doodscop == null) {
            return false;
        } else {
            return doodscop;
        }
    }

    /**
     * Define el valor de la propiedad doodscop.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDOODSCOP(Boolean value) {
        this.doodscop = value;
    }

}
