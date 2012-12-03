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
 * Datos propios del contrato
 * 
 * <p>Clase Java para DATOS_GENERALESCONTRATOTYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOS_GENERALESCONTRATOTYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FECHA_INICIO" type="{}FECHATYPE"/>
 *         &lt;element name="FECHA_TERMINO" type="{}FECHATYPE" minOccurs="0"/>
 *         &lt;element name="IND_CONVENIO_COLECTIVO" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[SN\s]"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="NIVEL_FORMATIVO">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{2}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="IND_DISCAPACIDAD" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[SCEFG\s]"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CODIGO_OCUPACION">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="8"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ID_OFERTA" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{17}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CODIGOPROGRAMAEMPLEO" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{2}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="NACIONALIDAD_CT" type="{}PAISTYPE"/>
 *         &lt;element name="MUNICIPIO_CT" type="{}MUNICIPIOTYPE" minOccurs="0"/>
 *         &lt;element name="OTRAS_LEGISLACIONES" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{3}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="TEMPORAL_MINUSV_BONIFICADO" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[SN\s]"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="FORMACION_BONIFICADO" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[SN\s]"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="DATOS_CAMPAÑAS" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{2}.{3}\d{4}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="IND_EMPRESA_AAPP_UNIVERSIDAD" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[SN\s]"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="REGULARIZACION_RDL_5_2011" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[S\s]"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ACOGIDO_LEGISLACION_ANTERIOR" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[S\s]"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="PROYECTO_EMPLEO_FORMACION" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[S\s]"/>
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
@XmlType(name = "DATOS_GENERALESCONTRATOTYPE", propOrder = {
    "fechainicio",
    "fechatermino",
    "indconveniocolectivo",
    "nivelformativo",
    "inddiscapacidad",
    "codigoocupacion",
    "idoferta",
    "codigoprogramaempleo",
    "nacionalidadct",
    "municipioct",
    "otraslegislaciones",
    "temporalminusvbonificado",
    "formacionbonificado",
    "datoscampa\u00f1as",
    "indempresaaappuniversidad",
    "regularizacionrdl52011",
    "acogidolegislacionanterior",
    "proyectoempleoformacion"
})
public class DATOSGENERALESCONTRATOTYPE {

    @XmlElement(name = "FECHA_INICIO", required = true)
    protected String fechainicio;
    @XmlElement(name = "FECHA_TERMINO")
    protected String fechatermino;
    @XmlElement(name = "IND_CONVENIO_COLECTIVO")
    protected String indconveniocolectivo;
    @XmlElement(name = "NIVEL_FORMATIVO", required = true)
    protected String nivelformativo;
    @XmlElement(name = "IND_DISCAPACIDAD")
    protected String inddiscapacidad;
    @XmlElement(name = "CODIGO_OCUPACION", required = true)
    protected String codigoocupacion;
    @XmlElement(name = "ID_OFERTA")
    protected String idoferta;
    @XmlElement(name = "CODIGOPROGRAMAEMPLEO")
    protected String codigoprogramaempleo;
    @XmlElement(name = "NACIONALIDAD_CT", required = true)
    protected String nacionalidadct;
    @XmlElement(name = "MUNICIPIO_CT")
    protected String municipioct;
    @XmlElement(name = "OTRAS_LEGISLACIONES")
    protected String otraslegislaciones;
    @XmlElement(name = "TEMPORAL_MINUSV_BONIFICADO")
    protected String temporalminusvbonificado;
    @XmlElement(name = "FORMACION_BONIFICADO")
    protected String formacionbonificado;
    @XmlElement(name = "DATOS_CAMPA\u00d1AS")
    protected String datoscampañas;
    @XmlElement(name = "IND_EMPRESA_AAPP_UNIVERSIDAD")
    protected String indempresaaappuniversidad;
    @XmlElement(name = "REGULARIZACION_RDL_5_2011")
    protected String regularizacionrdl52011;
    @XmlElement(name = "ACOGIDO_LEGISLACION_ANTERIOR")
    protected String acogidolegislacionanterior;
    @XmlElement(name = "PROYECTO_EMPLEO_FORMACION")
    protected String proyectoempleoformacion;

    /**
     * Obtiene el valor de la propiedad fechainicio.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFECHAINICIO() {
        return fechainicio;
    }

    /**
     * Define el valor de la propiedad fechainicio.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFECHAINICIO(String value) {
        this.fechainicio = value;
    }

    /**
     * Obtiene el valor de la propiedad fechatermino.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFECHATERMINO() {
        return fechatermino;
    }

    /**
     * Define el valor de la propiedad fechatermino.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFECHATERMINO(String value) {
        this.fechatermino = value;
    }

    /**
     * Obtiene el valor de la propiedad indconveniocolectivo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINDCONVENIOCOLECTIVO() {
        return indconveniocolectivo;
    }

    /**
     * Define el valor de la propiedad indconveniocolectivo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINDCONVENIOCOLECTIVO(String value) {
        this.indconveniocolectivo = value;
    }

    /**
     * Obtiene el valor de la propiedad nivelformativo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNIVELFORMATIVO() {
        return nivelformativo;
    }

    /**
     * Define el valor de la propiedad nivelformativo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNIVELFORMATIVO(String value) {
        this.nivelformativo = value;
    }

    /**
     * Obtiene el valor de la propiedad inddiscapacidad.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINDDISCAPACIDAD() {
        return inddiscapacidad;
    }

    /**
     * Define el valor de la propiedad inddiscapacidad.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINDDISCAPACIDAD(String value) {
        this.inddiscapacidad = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoocupacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCODIGOOCUPACION() {
        return codigoocupacion;
    }

    /**
     * Define el valor de la propiedad codigoocupacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCODIGOOCUPACION(String value) {
        this.codigoocupacion = value;
    }

    /**
     * Obtiene el valor de la propiedad idoferta.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIDOFERTA() {
        return idoferta;
    }

    /**
     * Define el valor de la propiedad idoferta.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIDOFERTA(String value) {
        this.idoferta = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoprogramaempleo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCODIGOPROGRAMAEMPLEO() {
        return codigoprogramaempleo;
    }

    /**
     * Define el valor de la propiedad codigoprogramaempleo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCODIGOPROGRAMAEMPLEO(String value) {
        this.codigoprogramaempleo = value;
    }

    /**
     * Obtiene el valor de la propiedad nacionalidadct.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNACIONALIDADCT() {
        return nacionalidadct;
    }

    /**
     * Define el valor de la propiedad nacionalidadct.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNACIONALIDADCT(String value) {
        this.nacionalidadct = value;
    }

    /**
     * Obtiene el valor de la propiedad municipioct.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMUNICIPIOCT() {
        return municipioct;
    }

    /**
     * Define el valor de la propiedad municipioct.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMUNICIPIOCT(String value) {
        this.municipioct = value;
    }

    /**
     * Obtiene el valor de la propiedad otraslegislaciones.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOTRASLEGISLACIONES() {
        return otraslegislaciones;
    }

    /**
     * Define el valor de la propiedad otraslegislaciones.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOTRASLEGISLACIONES(String value) {
        this.otraslegislaciones = value;
    }

    /**
     * Obtiene el valor de la propiedad temporalminusvbonificado.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTEMPORALMINUSVBONIFICADO() {
        return temporalminusvbonificado;
    }

    /**
     * Define el valor de la propiedad temporalminusvbonificado.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTEMPORALMINUSVBONIFICADO(String value) {
        this.temporalminusvbonificado = value;
    }

    /**
     * Obtiene el valor de la propiedad formacionbonificado.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFORMACIONBONIFICADO() {
        return formacionbonificado;
    }

    /**
     * Define el valor de la propiedad formacionbonificado.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFORMACIONBONIFICADO(String value) {
        this.formacionbonificado = value;
    }

    /**
     * Obtiene el valor de la propiedad datoscampañas.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDATOSCAMPAÑAS() {
        return datoscampañas;
    }

    /**
     * Define el valor de la propiedad datoscampañas.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDATOSCAMPAÑAS(String value) {
        this.datoscampañas = value;
    }

    /**
     * Obtiene el valor de la propiedad indempresaaappuniversidad.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINDEMPRESAAAPPUNIVERSIDAD() {
        return indempresaaappuniversidad;
    }

    /**
     * Define el valor de la propiedad indempresaaappuniversidad.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINDEMPRESAAAPPUNIVERSIDAD(String value) {
        this.indempresaaappuniversidad = value;
    }

    /**
     * Obtiene el valor de la propiedad regularizacionrdl52011.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREGULARIZACIONRDL52011() {
        return regularizacionrdl52011;
    }

    /**
     * Define el valor de la propiedad regularizacionrdl52011.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREGULARIZACIONRDL52011(String value) {
        this.regularizacionrdl52011 = value;
    }

    /**
     * Obtiene el valor de la propiedad acogidolegislacionanterior.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getACOGIDOLEGISLACIONANTERIOR() {
        return acogidolegislacionanterior;
    }

    /**
     * Define el valor de la propiedad acogidolegislacionanterior.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setACOGIDOLEGISLACIONANTERIOR(String value) {
        this.acogidolegislacionanterior = value;
    }

    /**
     * Obtiene el valor de la propiedad proyectoempleoformacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPROYECTOEMPLEOFORMACION() {
        return proyectoempleoformacion;
    }

    /**
     * Define el valor de la propiedad proyectoempleoformacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPROYECTOEMPLEOFORMACION(String value) {
        this.proyectoempleoformacion = value;
    }

}
