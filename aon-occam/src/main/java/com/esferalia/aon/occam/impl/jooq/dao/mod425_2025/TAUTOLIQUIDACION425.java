//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.1-b171012.0423 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2025.12.18 a las 11:51:10 AM CET 
//


package com.esferalia.aon.occam.impl.jooq.dao.mod425_2025;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * Autoliquidacion
 * 
 * <p>Clase Java para T_AUTOLIQUIDACION_425 complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_AUTOLIQUIDACION_425"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="TOA" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="TOM" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="IMC" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="IMD" type="{}IMPA15Type" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "T_AUTOLIQUIDACION_425")
public class TAUTOLIQUIDACION425 {

    @XmlAttribute(name = "TOA")
    protected String toa;
    @XmlAttribute(name = "TOM")
    protected String tom;
    @XmlAttribute(name = "IMC")
    protected String imc;
    @XmlAttribute(name = "IMD")
    protected String imd;

    /**
     * Obtiene el valor de la propiedad toa.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTOA() {
        return toa;
    }

    /**
     * Define el valor de la propiedad toa.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTOA(String value) {
        this.toa = value;
    }

    /**
     * Obtiene el valor de la propiedad tom.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTOM() {
        return tom;
    }

    /**
     * Define el valor de la propiedad tom.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTOM(String value) {
        this.tom = value;
    }

    /**
     * Obtiene el valor de la propiedad imc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIMC() {
        return imc;
    }

    /**
     * Define el valor de la propiedad imc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIMC(String value) {
        this.imc = value;
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
