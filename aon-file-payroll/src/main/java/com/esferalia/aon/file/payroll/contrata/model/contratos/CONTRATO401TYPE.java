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
 * Obra o servicio a tiempo completo
 * 
 * <p>Clase Java para CONTRATO401TYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="CONTRATO401TYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DATOS_EMPRESA" type="{}DATOS_EMPRESATYPE"/>
 *         &lt;element name="DATOS_TRABAJADOR" type="{}DATOS_TRABAJADORTYPE"/>
 *         &lt;element name="DATOS_GENERALES_CONTRATO" type="{}DATOS_GENERALESCONTRATOTYPE"/>
 *         &lt;element name="DATOS_ET_CO_TE" type="{}DATOS_ET_CO_TYPE" minOccurs="0"/>
 *         &lt;element name="DATOS_CONTRATO_INVESTIGACION" type="{}DATOS_CONTRATOINVESTIGACIONTYPE" minOccurs="0"/>
 *         &lt;element name="PROG_EMPLEO_PUBLICO" type="{}DATOS_PROGEMPLEOPUBLICOTYPE" minOccurs="0"/>
 *         &lt;element name="DATOS_ETT" type="{}DATOS_ETT_TYPE" minOccurs="0"/>
 *         &lt;element name="DATOS_CONTRATO_EXTRANJERO" type="{}DATOS_CONTRATO_EXTRANJEROTYPE" minOccurs="0"/>
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
@XmlType(name = "CONTRATO401TYPE", propOrder = {
    "datosempresa",
    "datostrabajador",
    "datosgeneralescontrato",
    "datosetcote",
    "datoscontratoinvestigacion",
    "progempleopublico",
    "datosett",
    "datoscontratoextranjero",
    "datoscomunicacopiabasica",
    "datosusolibreempresa"
})
public class CONTRATO401TYPE implements IContratoType
{

    @XmlElement(name = "DATOS_EMPRESA", required = true)
    protected DATOSEMPRESATYPE datosempresa;
    @XmlElement(name = "DATOS_TRABAJADOR", required = true)
    protected DATOSTRABAJADORTYPE datostrabajador;
    @XmlElement(name = "DATOS_GENERALES_CONTRATO", required = true)
    protected DATOSGENERALESCONTRATOTYPE datosgeneralescontrato;
    @XmlElement(name = "DATOS_ET_CO_TE")
    protected DATOSETCOTYPE datosetcote;
    @XmlElement(name = "DATOS_CONTRATO_INVESTIGACION")
    protected DATOSCONTRATOINVESTIGACIONTYPE datoscontratoinvestigacion;
    @XmlElement(name = "PROG_EMPLEO_PUBLICO")
    protected DATOSPROGEMPLEOPUBLICOTYPE progempleopublico;
    @XmlElement(name = "DATOS_ETT")
    protected DATOSETTTYPE datosett;
    @XmlElement(name = "DATOS_CONTRATO_EXTRANJERO")
    protected DATOSCONTRATOEXTRANJEROTYPE datoscontratoextranjero;
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
     * Obtiene el valor de la propiedad datoscontratoinvestigacion.
     * 
     * @return
     *     possible object is
     *     {@link DATOSCONTRATOINVESTIGACIONTYPE }
     *     
     */
    public DATOSCONTRATOINVESTIGACIONTYPE getDATOSCONTRATOINVESTIGACION() {
        return datoscontratoinvestigacion;
    }

    /**
     * Define el valor de la propiedad datoscontratoinvestigacion.
     * 
     * @param value
     *     allowed object is
     *     {@link DATOSCONTRATOINVESTIGACIONTYPE }
     *     
     */
    public void setDATOSCONTRATOINVESTIGACION(DATOSCONTRATOINVESTIGACIONTYPE value) {
        this.datoscontratoinvestigacion = value;
    }

    /**
     * Obtiene el valor de la propiedad progempleopublico.
     * 
     * @return
     *     possible object is
     *     {@link DATOSPROGEMPLEOPUBLICOTYPE }
     *     
     */
    public DATOSPROGEMPLEOPUBLICOTYPE getPROGEMPLEOPUBLICO() {
        return progempleopublico;
    }

    /**
     * Define el valor de la propiedad progempleopublico.
     * 
     * @param value
     *     allowed object is
     *     {@link DATOSPROGEMPLEOPUBLICOTYPE }
     *     
     */
    public void setPROGEMPLEOPUBLICO(DATOSPROGEMPLEOPUBLICOTYPE value) {
        this.progempleopublico = value;
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
     * Obtiene el valor de la propiedad datoscontratoextranjero.
     * 
     * @return
     *     possible object is
     *     {@link DATOSCONTRATOEXTRANJEROTYPE }
     *     
     */
    public DATOSCONTRATOEXTRANJEROTYPE getDATOSCONTRATOEXTRANJERO() {
        return datoscontratoextranjero;
    }

    /**
     * Define el valor de la propiedad datoscontratoextranjero.
     * 
     * @param value
     *     allowed object is
     *     {@link DATOSCONTRATOEXTRANJEROTYPE }
     *     
     */
    public void setDATOSCONTRATOEXTRANJERO(DATOSCONTRATOEXTRANJEROTYPE value) {
        this.datoscontratoextranjero = value;
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
