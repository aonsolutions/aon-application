//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.5-2 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: PM.11.26 a las 07:11:31 PM CET 
//


package com.esferalia.aon.file.payroll.contrata.model.prorrogas;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="PRORROGA_TIPO" type="{}PRORROGATIPOTYPE"/>
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
    "prorrogatipo"
})
@XmlRootElement(name = "PRORROGAS")
public class PRORROGAS {

    @XmlElement(name = "PRORROGA_TIPO", required = true)
    protected List<PRORROGATIPOTYPE> prorrogatipo;

    /**
     * Gets the value of the prorrogatipo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the prorrogatipo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPRORROGATIPO().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PRORROGATIPOTYPE }
     * 
     * 
     */
    public List<PRORROGATIPOTYPE> getPRORROGATIPO() {
        if (prorrogatipo == null) {
            prorrogatipo = new ArrayList<PRORROGATIPOTYPE>();
        }
        return this.prorrogatipo;
    }

}
