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
 * Datos de reducción de cuotas por contratación a tiempo parcial (RDL 1/2011). Opcional para los contratos a tiempo parcial (salvo los de interinidad, jubilación parcial, relevo e inserción con los que no es compatible) iniciados a partir del 13/02/2011.  Tampoco es compatible con contratos de extranjeros en origen , ni con los de centros especiales de empleo.
 * 
 * <p>Clase Java para DATOS_REDUCCION_RDL_1_2011TYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOS_REDUCCION_RDL_1_2011TYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CODIGO_COLECTIVO_REDUCCION">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{2}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="PORCENTAJE_REDUCCION">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="2"/>
 *               &lt;maxLength value="3"/>
 *               &lt;pattern value="([0-9])+"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="PORCENTAJE_JORNADA_REDUCCION">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{4}"/>
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
@XmlType(name = "DATOS_REDUCCION_RDL_1_2011TYPE", propOrder = {
    "codigocolectivoreduccion",
    "porcentajereduccion",
    "porcentajejornadareduccion"
})
public class DATOSREDUCCIONRDL12011TYPE {

    @XmlElement(name = "CODIGO_COLECTIVO_REDUCCION", required = true)
    protected String codigocolectivoreduccion;
    @XmlElement(name = "PORCENTAJE_REDUCCION", required = true)
    protected String porcentajereduccion;
    @XmlElement(name = "PORCENTAJE_JORNADA_REDUCCION", required = true)
    protected String porcentajejornadareduccion;

    /**
     * Obtiene el valor de la propiedad codigocolectivoreduccion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCODIGOCOLECTIVOREDUCCION() {
        return codigocolectivoreduccion;
    }

    /**
     * Define el valor de la propiedad codigocolectivoreduccion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCODIGOCOLECTIVOREDUCCION(String value) {
        this.codigocolectivoreduccion = value;
    }

    /**
     * Obtiene el valor de la propiedad porcentajereduccion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPORCENTAJEREDUCCION() {
        return porcentajereduccion;
    }

    /**
     * Define el valor de la propiedad porcentajereduccion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPORCENTAJEREDUCCION(String value) {
        this.porcentajereduccion = value;
    }

    /**
     * Obtiene el valor de la propiedad porcentajejornadareduccion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPORCENTAJEJORNADAREDUCCION() {
        return porcentajejornadareduccion;
    }

    /**
     * Define el valor de la propiedad porcentajejornadareduccion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPORCENTAJEJORNADAREDUCCION(String value) {
        this.porcentajejornadareduccion = value;
    }

}
