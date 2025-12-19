//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.1-b171012.0423 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2025.12.18 a las 11:51:10 AM CET 
//


package com.esferalia.aon.occam.impl.jooq.dao.mod425_2025;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para T_DATOS_ESTADISTICOS_425 complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_DATOS_ESTADISTICOS_425"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ACT" type="{}OPERACION_DATOS_ESTADISTICOS" maxOccurs="unbounded"/&gt;
 *         &lt;element name="PER" type="{}T_DATOS_PERSONALES" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="DTP" type="{}SINOType" /&gt;
 *       &lt;attribute name="RECC" type="{}SINOType" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "T_DATOS_ESTADISTICOS_425", propOrder = {
    "act",
    "per"
})
public class TDATOSESTADISTICOS425 {

    @XmlElement(name = "ACT", required = true)
    protected List<OPERACIONDATOSESTADISTICOS> act;
    @XmlElement(name = "PER")
    protected TDATOSPERSONALES per;
    @XmlAttribute(name = "DTP")
    protected SINOType dtp;
    @XmlAttribute(name = "RECC")
    protected SINOType recc;

    /**
     * Gets the value of the act property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the act property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getACT().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OPERACIONDATOSESTADISTICOS }
     * 
     * 
     */
    public List<OPERACIONDATOSESTADISTICOS> getACT() {
        if (act == null) {
            act = new ArrayList<OPERACIONDATOSESTADISTICOS>();
        }
        return this.act;
    }

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
     * Obtiene el valor de la propiedad dtp.
     * 
     * @return
     *     possible object is
     *     {@link SINOType }
     *     
     */
    public SINOType getDTP() {
        return dtp;
    }

    /**
     * Define el valor de la propiedad dtp.
     * 
     * @param value
     *     allowed object is
     *     {@link SINOType }
     *     
     */
    public void setDTP(SINOType value) {
        this.dtp = value;
    }

    /**
     * Obtiene el valor de la propiedad recc.
     * 
     * @return
     *     possible object is
     *     {@link SINOType }
     *     
     */
    public SINOType getRECC() {
        return recc;
    }

    /**
     * Define el valor de la propiedad recc.
     * 
     * @param value
     *     allowed object is
     *     {@link SINOType }
     *     
     */
    public void setRECC(SINOType value) {
        this.recc = value;
    }

}
