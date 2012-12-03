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
 * Datos anexos de los contratos de relevo.
 * 
 * <p>Clase Java para DATOS_ANEXOCONTRATORELEVOTYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOS_ANEXOCONTRATORELEVOTYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TIPO_TRABAJADOR">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{1}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="NOMBRE_APELLIDOS" type="{}NOMBREAPELLIDOSTYPE"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DATOS_ANEXOCONTRATORELEVOTYPE", propOrder = {
    "tipotrabajador",
    "nombreapellidos"
})
public class DATOSANEXOCONTRATORELEVOTYPE {

    @XmlElement(name = "TIPO_TRABAJADOR", required = true)
    protected String tipotrabajador;
    @XmlElement(name = "NOMBRE_APELLIDOS", required = true)
    protected NOMBREAPELLIDOSTYPE nombreapellidos;

    /**
     * Obtiene el valor de la propiedad tipotrabajador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTIPOTRABAJADOR() {
        return tipotrabajador;
    }

    /**
     * Define el valor de la propiedad tipotrabajador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTIPOTRABAJADOR(String value) {
        this.tipotrabajador = value;
    }

    /**
     * Obtiene el valor de la propiedad nombreapellidos.
     * 
     * @return
     *     possible object is
     *     {@link NOMBREAPELLIDOSTYPE }
     *     
     */
    public NOMBREAPELLIDOSTYPE getNOMBREAPELLIDOS() {
        return nombreapellidos;
    }

    /**
     * Define el valor de la propiedad nombreapellidos.
     * 
     * @param value
     *     allowed object is
     *     {@link NOMBREAPELLIDOSTYPE }
     *     
     */
    public void setNOMBREAPELLIDOS(NOMBREAPELLIDOSTYPE value) {
        this.nombreapellidos = value;
    }

}
