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
import com.esferalia.aon.file.payroll.contract.model.IContratoType;


/**
 * Indefinido minusválido a tiempo completo
 * 
 * <p>Clase Java para CONTRATO130TYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="CONTRATO130TYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DATOS_EMPRESA" type="{}DATOS_EMPRESATYPE"/>
 *         &lt;element name="DATOS_TRABAJADOR" type="{}DATOS_TRABAJADORTYPE"/>
 *         &lt;element name="DATOS_GENERALES_CONTRATO" type="{}DATOS_GENERALESCONTRATOTYPE"/>
 *         &lt;element name="DATOS_BONIFICACION" type="{}DATOS_BONIFICACIONTYPE" minOccurs="0"/>
 *         &lt;element name="DATOS_MEDIDAS_FOMENTO" type="{}DATOS_MEDIDASFOMENTOTYPE" minOccurs="0"/>
 *         &lt;element name="DATOS_ANEXO_CONTRATO_RELEVO" type="{}DATOS_ANEXOCONTRATORELEVOTYPE" minOccurs="0"/>
 *         &lt;element name="DATOS_ET_CO_TE" type="{}DATOS_ET_CO_TYPE" minOccurs="0"/>
 *         &lt;element name="DATOS_ETT" type="{}DATOS_ETT_TYPE" minOccurs="0"/>
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
@XmlType(name = "CONTRATO130TYPE", propOrder = {
    "datosempresa",
    "datostrabajador",
    "datosgeneralescontrato",
    "datosbonificacion",
    "datosmedidasfomento",
    "datosanexocontratorelevo",
    "datosetcote",
    "datosett",
    "datoscomunicacopiabasica",
    "datosusolibreempresa"
})
public class CONTRATO130TYPE implements IContratoType
{

    @XmlElement(name = "DATOS_EMPRESA", required = true)
    protected DATOSEMPRESATYPE datosempresa;
    @XmlElement(name = "DATOS_TRABAJADOR", required = true)
    protected DATOSTRABAJADORTYPE datostrabajador;
    @XmlElement(name = "DATOS_GENERALES_CONTRATO", required = true)
    protected DATOSGENERALESCONTRATOTYPE datosgeneralescontrato;
    @XmlElement(name = "DATOS_BONIFICACION")
    protected DATOSBONIFICACIONTYPE datosbonificacion;
    @XmlElement(name = "DATOS_MEDIDAS_FOMENTO")
    protected DATOSMEDIDASFOMENTOTYPE datosmedidasfomento;
    @XmlElement(name = "DATOS_ANEXO_CONTRATO_RELEVO")
    protected DATOSANEXOCONTRATORELEVOTYPE datosanexocontratorelevo;
    @XmlElement(name = "DATOS_ET_CO_TE")
    protected DATOSETCOTYPE datosetcote;
    @XmlElement(name = "DATOS_ETT")
    protected DATOSETTTYPE datosett;
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
     * Obtiene el valor de la propiedad datostrabajador.
     * 
     * @return
     *     possible object is
     *     {@link DATOSTRABAJADORTYPE }
     *     
     */
    public DATOSTRABAJADORTYPE getDATOSTRABAJADOR() {
        return datostrabajador;
    }

    /**
     * Define el valor de la propiedad datostrabajador.
     * 
     * @param value
     *     allowed object is
     *     {@link DATOSTRABAJADORTYPE }
     *     
     */
    public void setDATOSTRABAJADOR(DATOSTRABAJADORTYPE value) {
        this.datostrabajador = value;
    }

    /**
     * Obtiene el valor de la propiedad datosgeneralescontrato.
     * 
     * @return
     *     possible object is
     *     {@link DATOSGENERALESCONTRATOTYPE }
     *     
     */
    public DATOSGENERALESCONTRATOTYPE getDATOSGENERALESCONTRATO() {
        return datosgeneralescontrato;
    }

    /**
     * Define el valor de la propiedad datosgeneralescontrato.
     * 
     * @param value
     *     allowed object is
     *     {@link DATOSGENERALESCONTRATOTYPE }
     *     
     */
    public void setDATOSGENERALESCONTRATO(DATOSGENERALESCONTRATOTYPE value) {
        this.datosgeneralescontrato = value;
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
     * Obtiene el valor de la propiedad datosetcote.
     * 
     * @return
     *     possible object is
     *     {@link DATOSETCOTYPE }
     *     
     */
    public DATOSETCOTYPE getDATOSETCOTE() {
        return datosetcote;
    }

    /**
     * Define el valor de la propiedad datosetcote.
     * 
     * @param value
     *     allowed object is
     *     {@link DATOSETCOTYPE }
     *     
     */
    public void setDATOSETCOTE(DATOSETCOTYPE value) {
        this.datosetcote = value;
    }

    /**
     * Obtiene el valor de la propiedad datosett.
     * 
     * @return
     *     possible object is
     *     {@link DATOSETTTYPE }
     *     
     */
    public DATOSETTTYPE getDATOSETT() {
        return datosett;
    }

    /**
     * Define el valor de la propiedad datosett.
     * 
     * @param value
     *     allowed object is
     *     {@link DATOSETTTYPE }
     *     
     */
    public void setDATOSETT(DATOSETTTYPE value) {
        this.datosett = value;
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
