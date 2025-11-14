//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.1-b171012.0423 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2025.11.07 a las 01:49:16 PM CET 
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
 * 
 * 				Datos régimen simplificado
 * 			
 * 
 * <p>Clase Java para T_REGIMEN_SIMPLIFICADO_425 complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_REGIMEN_SIMPLIFICADO_425"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="MOD" type="{}T_MODULO" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="CRS" type="{}T_SIMPLIFICADO" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="RES" type="{}IMPA15Type" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "T_REGIMEN_SIMPLIFICADO_425", propOrder = {
    "mod",
    "crs"
})
public class TREGIMENSIMPLIFICADO425 {

    @XmlElement(name = "MOD")
    protected List<TMODULO> mod;
    @XmlElement(name = "CRS")
    protected TSIMPLIFICADO crs;
    @XmlAttribute(name = "RES")
    protected String res;

    /**
     * Gets the value of the mod property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mod property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMOD().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TMODULO }
     * 
     * 
     */
    public List<TMODULO> getMOD() {
        if (mod == null) {
            mod = new ArrayList<TMODULO>();
        }
        return this.mod;
    }

    /**
     * Obtiene el valor de la propiedad crs.
     * 
     * @return
     *     possible object is
     *     {@link TSIMPLIFICADO }
     *     
     */
    public TSIMPLIFICADO getCRS() {
        return crs;
    }

    /**
     * Define el valor de la propiedad crs.
     * 
     * @param value
     *     allowed object is
     *     {@link TSIMPLIFICADO }
     *     
     */
    public void setCRS(TSIMPLIFICADO value) {
        this.crs = value;
    }

    /**
     * Obtiene el valor de la propiedad res.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRES() {
        return res;
    }

    /**
     * Define el valor de la propiedad res.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRES(String value) {
        this.res = value;
    }

}
