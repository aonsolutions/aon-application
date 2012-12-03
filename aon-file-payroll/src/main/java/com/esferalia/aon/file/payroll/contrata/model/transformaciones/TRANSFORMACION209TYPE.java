//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.5-2 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: PM.11.26 a las 06:55:00 PM CET 
//


package com.esferalia.aon.file.payroll.contrata.model.transformaciones;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.esferalia.aon.file.payroll.contract.model.ITransformacionType;


/**
 * Transformación a tiempo parcial bonificada
 * 
 * <p>Clase Java para TRANSFORMACION_209TYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="TRANSFORMACION_209TYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DATOS_EMPRESA" type="{}DATOS_EMPRESATYPE"/>
 *         &lt;element name="DATOS_CONTRATO" type="{}DATOS_CONTRATOTYPE"/>
 *         &lt;element name="DATOS_GENERALES_TRANSFORMACION" type="{}DATOS_GENERALES_TRANSFORMACIONTYPE"/>
 *         &lt;element name="DATOS_CONTRATO_TIEMPO_PARCIAL" type="{}DATOS_CONTRATOTIEMPOPARCIALTYPE"/>
 *         &lt;element name="DATOS_MEDIDAS_FOMENTO" type="{}DATOS_MEDIDASFOMENTOTYPE" minOccurs="0"/>
 *         &lt;element name="DATOS_BONIFICACION" type="{}DATOS_BONIFICACIONTYPE" minOccurs="0"/>
 *         &lt;element name="DATOS_ADICIONALES_TRANSFORMACION" type="{}DATOS_ADICIONALES_TRANSFORMACIONTYPE" minOccurs="0"/>
 *         &lt;element name="DATOS_ANEXO_CONTRATO_RELEVO" type="{}DATOS_ANEXOCONTRATORELEVOTYPE" minOccurs="0"/>
 *         &lt;element name="DATOS_COMUNICA_COPIA_BASICA" type="{}DATOS_COMUNICA_COPIA_BASICATYPE" minOccurs="0"/>
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
@XmlType(name = "TRANSFORMACION_209TYPE", propOrder = {
    "datosempresa",
    "datoscontrato",
    "datosgeneralestransformacion",
    "datoscontratotiempoparcial",
    "datosmedidasfomento",
    "datosbonificacion",
    "datosadicionalestransformacion",
    "datosanexocontratorelevo",
    "datoscomunicacopiabasica",
    "datosusolibreempresa"
})
public class TRANSFORMACION209TYPE implements ITransformacionType
{

    @XmlElement(name = "DATOS_EMPRESA", required = true)
    protected DATOSEMPRESATYPE datosempresa;
    @XmlElement(name = "DATOS_CONTRATO", required = true)
    protected DATOSCONTRATOTYPE datoscontrato;
    @XmlElement(name = "DATOS_GENERALES_TRANSFORMACION", required = true)
    protected DATOSGENERALESTRANSFORMACIONTYPE datosgeneralestransformacion;
    @XmlElement(name = "DATOS_CONTRATO_TIEMPO_PARCIAL", required = true)
    protected DATOSCONTRATOTIEMPOPARCIALTYPE datoscontratotiempoparcial;
    @XmlElement(name = "DATOS_MEDIDAS_FOMENTO")
    protected DATOSMEDIDASFOMENTOTYPE datosmedidasfomento;
    @XmlElement(name = "DATOS_BONIFICACION")
    protected DATOSBONIFICACIONTYPE datosbonificacion;
    @XmlElement(name = "DATOS_ADICIONALES_TRANSFORMACION")
    protected DATOSADICIONALESTRANSFORMACIONTYPE datosadicionalestransformacion;
    @XmlElement(name = "DATOS_ANEXO_CONTRATO_RELEVO")
    protected DATOSANEXOCONTRATORELEVOTYPE datosanexocontratorelevo;
    @XmlElement(name = "DATOS_COMUNICA_COPIA_BASICA")
    protected DATOSCOMUNICACOPIABASICATYPE datoscomunicacopiabasica;
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
     * Obtiene el valor de la propiedad datosgeneralestransformacion.
     * 
     * @return
     *     possible object is
     *     {@link DATOSGENERALESTRANSFORMACIONTYPE }
     *     
     */
    public DATOSGENERALESTRANSFORMACIONTYPE getDATOSGENERALESTRANSFORMACION() {
        return datosgeneralestransformacion;
    }

    /**
     * Define el valor de la propiedad datosgeneralestransformacion.
     * 
     * @param value
     *     allowed object is
     *     {@link DATOSGENERALESTRANSFORMACIONTYPE }
     *     
     */
    public void setDATOSGENERALESTRANSFORMACION(DATOSGENERALESTRANSFORMACIONTYPE value) {
        this.datosgeneralestransformacion = value;
    }

    /**
     * Obtiene el valor de la propiedad datoscontratotiempoparcial.
     * 
     * @return
     *     possible object is
     *     {@link DATOSCONTRATOTIEMPOPARCIALTYPE }
     *     
     */
    public DATOSCONTRATOTIEMPOPARCIALTYPE getDATOSCONTRATOTIEMPOPARCIAL() {
        return datoscontratotiempoparcial;
    }

    /**
     * Define el valor de la propiedad datoscontratotiempoparcial.
     * 
     * @param value
     *     allowed object is
     *     {@link DATOSCONTRATOTIEMPOPARCIALTYPE }
     *     
     */
    public void setDATOSCONTRATOTIEMPOPARCIAL(DATOSCONTRATOTIEMPOPARCIALTYPE value) {
        this.datoscontratotiempoparcial = value;
    }

    /**
     * Obtiene el valor de la propiedad datosmedidasfomento.
     * 
     * @return
     *     possible object is
     *     {@link DATOSMEDIDASFOMENTOTYPE }
     *     
     */
    public DATOSMEDIDASFOMENTOTYPE getDATOSMEDIDASFOMENTO() {
        return datosmedidasfomento;
    }

    /**
     * Define el valor de la propiedad datosmedidasfomento.
     * 
     * @param value
     *     allowed object is
     *     {@link DATOSMEDIDASFOMENTOTYPE }
     *     
     */
    public void setDATOSMEDIDASFOMENTO(DATOSMEDIDASFOMENTOTYPE value) {
        this.datosmedidasfomento = value;
    }

    /**
     * Obtiene el valor de la propiedad datosbonificacion.
     * 
     * @return
     *     possible object is
     *     {@link DATOSBONIFICACIONTYPE }
     *     
     */
    public DATOSBONIFICACIONTYPE getDATOSBONIFICACION() {
        return datosbonificacion;
    }

    /**
     * Define el valor de la propiedad datosbonificacion.
     * 
     * @param value
     *     allowed object is
     *     {@link DATOSBONIFICACIONTYPE }
     *     
     */
    public void setDATOSBONIFICACION(DATOSBONIFICACIONTYPE value) {
        this.datosbonificacion = value;
    }

    /**
     * Obtiene el valor de la propiedad datosadicionalestransformacion.
     * 
     * @return
     *     possible object is
     *     {@link DATOSADICIONALESTRANSFORMACIONTYPE }
     *     
     */
    public DATOSADICIONALESTRANSFORMACIONTYPE getDATOSADICIONALESTRANSFORMACION() {
        return datosadicionalestransformacion;
    }

    /**
     * Define el valor de la propiedad datosadicionalestransformacion.
     * 
     * @param value
     *     allowed object is
     *     {@link DATOSADICIONALESTRANSFORMACIONTYPE }
     *     
     */
    public void setDATOSADICIONALESTRANSFORMACION(DATOSADICIONALESTRANSFORMACIONTYPE value) {
        this.datosadicionalestransformacion = value;
    }

    /**
     * Obtiene el valor de la propiedad datosanexocontratorelevo.
     * 
     * @return
     *     possible object is
     *     {@link DATOSANEXOCONTRATORELEVOTYPE }
     *     
     */
    public DATOSANEXOCONTRATORELEVOTYPE getDATOSANEXOCONTRATORELEVO() {
        return datosanexocontratorelevo;
    }

    /**
     * Define el valor de la propiedad datosanexocontratorelevo.
     * 
     * @param value
     *     allowed object is
     *     {@link DATOSANEXOCONTRATORELEVOTYPE }
     *     
     */
    public void setDATOSANEXOCONTRATORELEVO(DATOSANEXOCONTRATORELEVOTYPE value) {
        this.datosanexocontratorelevo = value;
    }

    /**
     * Obtiene el valor de la propiedad datoscomunicacopiabasica.
     * 
     * @return
     *     possible object is
     *     {@link DATOSCOMUNICACOPIABASICATYPE }
     *     
     */
    public DATOSCOMUNICACOPIABASICATYPE getDATOSCOMUNICACOPIABASICA() {
        return datoscomunicacopiabasica;
    }

    /**
     * Define el valor de la propiedad datoscomunicacopiabasica.
     * 
     * @param value
     *     allowed object is
     *     {@link DATOSCOMUNICACOPIABASICATYPE }
     *     
     */
    public void setDATOSCOMUNICACOPIABASICA(DATOSCOMUNICACOPIABASICATYPE value) {
        this.datoscomunicacopiabasica = value;
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
