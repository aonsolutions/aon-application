//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.5-2 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: PM.11.26 a las 06:55:00 PM CET 
//


package com.esferalia.aon.file.payroll.contrata.model.transformaciones;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="5">
 *         &lt;element name="TRANSFORMACION_109" type="{}TRANSFORMACION_109TYPE" minOccurs="0"/>
 *         &lt;element name="TRANSFORMACION_139" type="{}TRANSFORMACION_139TYPE" minOccurs="0"/>
 *         &lt;element name="TRANSFORMACION_189" type="{}TRANSFORMACION_189TYPE" minOccurs="0"/>
 *         &lt;element name="TRANSFORMACION_209" type="{}TRANSFORMACION_209TYPE" minOccurs="0"/>
 *         &lt;element name="TRANSFORMACION_239" type="{}TRANSFORMACION_239TYPE" minOccurs="0"/>
 *         &lt;element name="TRANSFORMACION_289" type="{}TRANSFORMACION_289TYPE" minOccurs="0"/>
 *         &lt;element name="TRANSFORMACION_309" type="{}TRANSFORMACION_309TYPE" minOccurs="0"/>
 *         &lt;element name="TRANSFORMACION_339" type="{}TRANSFORMACION_339TYPE" minOccurs="0"/>
 *         &lt;element name="TRANSFORMACION_389" type="{}TRANSFORMACION_389TYPE" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "transformacion109AndTRANSFORMACION139AndTRANSFORMACION189"
})
@XmlRootElement(name = "TRANSFORMACIONES")
public class TRANSFORMACIONES {

    @XmlElements({
        @XmlElement(name = "TRANSFORMACION_109", type = TRANSFORMACION109TYPE.class),
        @XmlElement(name = "TRANSFORMACION_139", type = TRANSFORMACION139TYPE.class),
        @XmlElement(name = "TRANSFORMACION_189", type = TRANSFORMACION189TYPE.class),
        @XmlElement(name = "TRANSFORMACION_209", type = TRANSFORMACION209TYPE.class),
        @XmlElement(name = "TRANSFORMACION_239", type = TRANSFORMACION239TYPE.class),
        @XmlElement(name = "TRANSFORMACION_289", type = TRANSFORMACION289TYPE.class),
        @XmlElement(name = "TRANSFORMACION_309", type = TRANSFORMACION309TYPE.class),
        @XmlElement(name = "TRANSFORMACION_339", type = TRANSFORMACION339TYPE.class),
        @XmlElement(name = "TRANSFORMACION_389", type = TRANSFORMACION389TYPE.class)
    })
    protected List<Object> transformacion109AndTRANSFORMACION139AndTRANSFORMACION189;

    /**
     * Gets the value of the transformacion109AndTRANSFORMACION139AndTRANSFORMACION189 property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transformacion109AndTRANSFORMACION139AndTRANSFORMACION189 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTRANSFORMACION109AndTRANSFORMACION139AndTRANSFORMACION189().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRANSFORMACION109TYPE }
     * {@link TRANSFORMACION139TYPE }
     * {@link TRANSFORMACION189TYPE }
     * {@link TRANSFORMACION209TYPE }
     * {@link TRANSFORMACION239TYPE }
     * {@link TRANSFORMACION289TYPE }
     * {@link TRANSFORMACION309TYPE }
     * {@link TRANSFORMACION339TYPE }
     * {@link TRANSFORMACION389TYPE }
     * 
     * 
     */
    public List<Object> getTRANSFORMACION109AndTRANSFORMACION139AndTRANSFORMACION189() {
        if (transformacion109AndTRANSFORMACION139AndTRANSFORMACION189 == null) {
            transformacion109AndTRANSFORMACION139AndTRANSFORMACION189 = new ArrayList<Object>();
        }
        return this.transformacion109AndTRANSFORMACION139AndTRANSFORMACION189;
    }

}
