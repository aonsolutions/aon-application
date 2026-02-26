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
 * <p>Clase Java para T_ENTIDAD_IGIC complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_ENTIDAD_IGIC"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="TPE" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="NIFD" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "T_ENTIDAD_IGIC")
public class TENTIDADIGIC {

    @XmlAttribute(name = "TPE")
    protected String tpe;
    @XmlAttribute(name = "NIFD")
    protected String nifd;

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

    /**
     * Obtiene el valor de la propiedad nifd.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNIFD() {
        return nifd;
    }

    /**
     * Define el valor de la propiedad nifd.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNIFD(String value) {
        this.nifd = value;
    }

}
