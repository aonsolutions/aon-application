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
 * <p>Clase Java para T_PERSONA complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_PERSONA"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PER" type="{}T_DATOS_PERSONALES"/&gt;
 *         &lt;element name="DIR" type="{}T_DIRECCION"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="SEC" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="TPE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "T_PERSONA", propOrder = {
    "per",
    "dir"
})
public class TPERSONA {

    @XmlElement(name = "PER", required = true)
    protected TDATOSPERSONALES per;
    @XmlElement(name = "DIR", required = true)
    protected TDIRECCION dir;
    @XmlAttribute(name = "SEC")
    protected Integer sec;
    @XmlAttribute(name = "TPE")
    protected String tpe;

    /**
     * Obtiene el valor de la propiedad per.
     * 
     * @return
     *     possible object is
     *     {@link TDATOSPERSONALES }
     *     
     */
    public TDATOSPERSONALES getPER() {
        return per;
    }

    /**
     * Define el valor de la propiedad per.
     * 
     * @param value
     *     allowed object is
     *     {@link TDATOSPERSONALES }
     *     
     */
    public void setPER(TDATOSPERSONALES value) {
        this.per = value;
    }

    /**
     * Obtiene el valor de la propiedad dir.
     * 
     * @return
     *     possible object is
     *     {@link TDIRECCION }
     *     
     */
    public TDIRECCION getDIR() {
        return dir;
    }

    /**
     * Define el valor de la propiedad dir.
     * 
     * @param value
     *     allowed object is
     *     {@link TDIRECCION }
     *     
     */
    public void setDIR(TDIRECCION value) {
        this.dir = value;
    }

    /**
     * Obtiene el valor de la propiedad sec.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSEC() {
        return sec;
    }

    /**
     * Define el valor de la propiedad sec.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSEC(Integer value) {
        this.sec = value;
    }

    /**
     * Obtiene el valor de la propiedad tpe.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTPE() {
        return tpe;
    }

    /**
     * Define el valor de la propiedad tpe.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTPE(String value) {
        this.tpe = value;
    }

}
