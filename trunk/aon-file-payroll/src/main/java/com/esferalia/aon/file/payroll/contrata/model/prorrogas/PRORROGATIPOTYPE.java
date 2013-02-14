//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.5-2 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: PM.11.26 a las 07:11:31 PM CET 
//


package com.esferalia.aon.file.payroll.contrata.model.prorrogas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.esferalia.aon.file.payroll.contract.model.IProrrogaType;


/**
 * Datos generales de la prórroga
 * 
 * <p>Clase Java para PRORROGATIPOTYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="PRORROGATIPOTYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DATOS_EMPRESA" type="{}DATOS_EMPRESATYPE"/>
 *         &lt;element name="DATOS_CONTRATO" type="{}DATOS_CONTRATOTYPE"/>
 *         &lt;element name="DATOS_GENERALES_PRORROGA" type="{}DATOS_GENERALESPRORROGATYPE"/>
 *         &lt;element name="DATOS_ADICIONALES_PRORROGA" type="{}DATOS_ADICIONALESPRORROGATYPE" minOccurs="0"/>
 *         &lt;element name="DATOS_USOLIBRE_EMPRESA" type="{}DATOS_USOLIBRE_EMPRESATYPE" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PRORROGATIPOTYPE", propOrder = {
    "datosempresa",
    "datoscontrato",
    "datosgeneralesprorroga",
    "datosadicionalesprorroga",
    "datosusolibreempresa"
})
public class PRORROGATIPOTYPE
    implements IProrrogaType
{

    @XmlElement(name = "DATOS_EMPRESA", required = true)
    protected DATOSEMPRESATYPE datosempresa;
    @XmlElement(name = "DATOS_CONTRATO", required = true)
    protected DATOSCONTRATOTYPE datoscontrato;
    @XmlElement(name = "DATOS_GENERALES_PRORROGA", required = true)
    protected DATOSGENERALESPRORROGATYPE datosgeneralesprorroga;
    @XmlElement(name = "DATOS_ADICIONALES_PRORROGA")
    protected DATOSADICIONALESPRORROGATYPE datosadicionalesprorroga;
    @XmlElement(name = "DATOS_USOLIBRE_EMPRESA")
    protected DATOSUSOLIBREEMPRESATYPE datosusolibreempresa;

    /**
     * Obtiene el valor de la propiedad datosempresa.
     * 
     * @return
     *     possible object is
     *     {@link DATOSEMPRESATYPE }
     *     
     */
    public DATOSEMPRESATYPE getDATOSEMPRESA() {
        return datosempresa;
    }

    /**
     * Define el valor de la propiedad datosempresa.
     * 
     * @param value
     *     allowed object is
     *     {@link DATOSEMPRESATYPE }
     *     
     */
    public void setDATOSEMPRESA(DATOSEMPRESATYPE value) {
        this.datosempresa = value;
    }

    /**
     * Obtiene el valor de la propiedad datoscontrato.
     * 
     * @return
     *     possible object is
     *     {@link DATOSCONTRATOTYPE }
     *     
     */
    public DATOSCONTRATOTYPE getDATOSCONTRATO() {
        return datoscontrato;
    }

    /**
     * Define el valor de la propiedad datoscontrato.
     * 
     * @param value
     *     allowed object is
     *     {@link DATOSCONTRATOTYPE }
     *     
     */
    public void setDATOSCONTRATO(DATOSCONTRATOTYPE value) {
        this.datoscontrato = value;
    }

    /**
     * Obtiene el valor de la propiedad datosgeneralesprorroga.
     * 
     * @return
     *     possible object is
     *     {@link DATOSGENERALESPRORROGATYPE }
     *     
     */
    public DATOSGENERALESPRORROGATYPE getDATOSGENERALESPRORROGA() {
        return datosgeneralesprorroga;
    }

    /**
     * Define el valor de la propiedad datosgeneralesprorroga.
     * 
     * @param value
     *     allowed object is
     *     {@link DATOSGENERALESPRORROGATYPE }
     *     
     */
    public void setDATOSGENERALESPRORROGA(DATOSGENERALESPRORROGATYPE value) {
        this.datosgeneralesprorroga = value;
    }

    /**
     * Obtiene el valor de la propiedad datosadicionalesprorroga.
     * 
     * @return
     *     possible object is
     *     {@link DATOSADICIONALESPRORROGATYPE }
     *     
     */
    public DATOSADICIONALESPRORROGATYPE getDATOSADICIONALESPRORROGA() {
        return datosadicionalesprorroga;
    }

    /**
     * Define el valor de la propiedad datosadicionalesprorroga.
     * 
     * @param value
     *     allowed object is
     *     {@link DATOSADICIONALESPRORROGATYPE }
     *     
     */
    public void setDATOSADICIONALESPRORROGA(DATOSADICIONALESPRORROGATYPE value) {
        this.datosadicionalesprorroga = value;
    }

    /**
     * Obtiene el valor de la propiedad datosusolibreempresa.
     * 
     * @return
     *     possible object is
     *     {@link DATOSUSOLIBREEMPRESATYPE }
     *     
     */
    public DATOSUSOLIBREEMPRESATYPE getDATOSUSOLIBREEMPRESA() {
        return datosusolibreempresa;
    }

    /**
     * Define el valor de la propiedad datosusolibreempresa.
     * 
     * @param value
     *     allowed object is
     *     {@link DATOSUSOLIBREEMPRESATYPE }
     *     
     */
    public void setDATOSUSOLIBREEMPRESA(DATOSUSOLIBREEMPRESATYPE value) {
        this.datosusolibreempresa = value;
    }

}
