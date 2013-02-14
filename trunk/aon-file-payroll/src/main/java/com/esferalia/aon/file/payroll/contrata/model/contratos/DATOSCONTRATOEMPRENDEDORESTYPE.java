//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.5-2 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: PM.11.26 a las 06:54:59 PM CET 
//


package com.esferalia.aon.file.payroll.contrata.model.contratos;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Datos del contrato de emprendedores (RDL 3/2012). Opcional para los contratos de códigos 100, 150, 300 y 350 iniciados a partir del 12/02/2012.
 * 
 * <p>Clase Java para DATOS_CONTRATO_EMPRENDEDORESTYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOS_CONTRATO_EMPRENDEDORESTYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CODIGO_COLECTIVO_DEDUCCION_FISCAL" maxOccurs="2">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{2}"/>
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
@XmlType(name = "DATOS_CONTRATO_EMPRENDEDORESTYPE", propOrder = {
    "codigocolectivodeduccionfiscal"
})
public class DATOSCONTRATOEMPRENDEDORESTYPE {

    @XmlElement(name = "CODIGO_COLECTIVO_DEDUCCION_FISCAL", required = true)
    protected List<String> codigocolectivodeduccionfiscal;

    /**
     * Gets the value of the codigocolectivodeduccionfiscal property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the codigocolectivodeduccionfiscal property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCODIGOCOLECTIVODEDUCCIONFISCAL().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCODIGOCOLECTIVODEDUCCIONFISCAL() {
        if (codigocolectivodeduccionfiscal == null) {
            codigocolectivodeduccionfiscal = new ArrayList<String>();
        }
        return this.codigocolectivodeduccionfiscal;
    }

}
