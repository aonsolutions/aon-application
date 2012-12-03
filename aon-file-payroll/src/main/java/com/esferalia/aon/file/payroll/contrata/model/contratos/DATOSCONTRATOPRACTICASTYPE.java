//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.5-2 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: PM.11.26 a las 06:54:59 PM CET 
//


package com.esferalia.aon.file.payroll.contrata.model.contratos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Datos de los contratos de prácticas.
 * 
 * <p>Clase Java para DATOSCONTRATOPRACTICASTYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOSCONTRATOPRACTICASTYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TITULACION_ACADEMICA">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{12}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="IND_CERTIF_PROFESIONALIDAD" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[SN\s]"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DATOSCONTRATOPRACTICASTYPE", propOrder = {
    "titulacionacademica",
    "indcertifprofesionalidad"
})
public class DATOSCONTRATOPRACTICASTYPE {

    @XmlElement(name = "TITULACION_ACADEMICA", required = true)
    protected String titulacionacademica;
    @XmlElement(name = "IND_CERTIF_PROFESIONALIDAD")
    protected String indcertifprofesionalidad;

    /**
     * Obtiene el valor de la propiedad titulacionacademica.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTITULACIONACADEMICA() {
        return titulacionacademica;
    }

    /**
     * Define el valor de la propiedad titulacionacademica.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTITULACIONACADEMICA(String value) {
        this.titulacionacademica = value;
    }

    /**
     * Obtiene el valor de la propiedad indcertifprofesionalidad.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINDCERTIFPROFESIONALIDAD() {
        return indcertifprofesionalidad;
    }

    /**
     * Define el valor de la propiedad indcertifprofesionalidad.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINDCERTIFPROFESIONALIDAD(String value) {
        this.indcertifprofesionalidad = value;
    }

}
