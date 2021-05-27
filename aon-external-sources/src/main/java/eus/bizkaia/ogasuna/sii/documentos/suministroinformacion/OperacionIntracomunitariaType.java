
package eus.bizkaia.ogasuna.sii.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 *  Apunte correspondiente al libro de operaciones intracomunitarias. 
 * 
 * <p>Clase Java para OperacionIntracomunitariaType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="OperacionIntracomunitariaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TipoOperacion"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="A"/&gt;
 *               &lt;enumeration value="B"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ClaveDeclarado"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="D"/&gt;
 *               &lt;enumeration value="R"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="EstadoMiembro" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}CountryMiembroType"/&gt;
 *         &lt;element name="PlazoOperacion" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;pattern value="\d{1,3}"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="DescripcionBienes" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}TextMax40Type"/&gt;
 *         &lt;element name="DireccionOperador" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}TextMax120Type"/&gt;
 *         &lt;element name="FacturasODocumentacion" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}TextMax150Type" minOccurs="0"/&gt;
 *         &lt;element name="RefExterna" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}TextMax60Type" minOccurs="0"/&gt;
 *         &lt;element name="NumRegistroAcuerdoFacturacion" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}TextMax15Type" minOccurs="0"/&gt;
 *         &lt;element name="EntidadSucedida" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}PersonaFisicaJuridicaUnicaESType" minOccurs="0"/&gt;
 *         &lt;element name="RegPrevioGGEEoREDEMEoCompetencia" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}RegPrevioGGEEoREDEMEoCompetenciaType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OperacionIntracomunitariaType", propOrder = {
    "tipoOperacion",
    "claveDeclarado",
    "estadoMiembro",
    "plazoOperacion",
    "descripcionBienes",
    "direccionOperador",
    "facturasODocumentacion",
    "refExterna",
    "numRegistroAcuerdoFacturacion",
    "entidadSucedida",
    "regPrevioGGEEoREDEMEoCompetencia"
})
public class OperacionIntracomunitariaType {

    @XmlElement(name = "TipoOperacion", required = true)
    protected String tipoOperacion;
    @XmlElement(name = "ClaveDeclarado", required = true)
    protected String claveDeclarado;
    @XmlElement(name = "EstadoMiembro", required = true)
    @XmlSchemaType(name = "string")
    protected CountryMiembroType estadoMiembro;
    @XmlElement(name = "PlazoOperacion")
    protected String plazoOperacion;
    @XmlElement(name = "DescripcionBienes", required = true)
    protected String descripcionBienes;
    @XmlElement(name = "DireccionOperador", required = true)
    protected String direccionOperador;
    @XmlElement(name = "FacturasODocumentacion")
    protected String facturasODocumentacion;
    @XmlElement(name = "RefExterna")
    protected String refExterna;
    @XmlElement(name = "NumRegistroAcuerdoFacturacion")
    protected String numRegistroAcuerdoFacturacion;
    @XmlElement(name = "EntidadSucedida")
    protected PersonaFisicaJuridicaUnicaESType entidadSucedida;
    @XmlElement(name = "RegPrevioGGEEoREDEMEoCompetencia")
    @XmlSchemaType(name = "string")
    protected RegPrevioGGEEoREDEMEoCompetenciaType regPrevioGGEEoREDEMEoCompetencia;

    /**
     * Obtiene el valor de la propiedad tipoOperacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoOperacion() {
        return tipoOperacion;
    }

    /**
     * Define el valor de la propiedad tipoOperacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoOperacion(String value) {
        this.tipoOperacion = value;
    }

    /**
     * Obtiene el valor de la propiedad claveDeclarado.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClaveDeclarado() {
        return claveDeclarado;
    }

    /**
     * Define el valor de la propiedad claveDeclarado.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClaveDeclarado(String value) {
        this.claveDeclarado = value;
    }

    /**
     * Obtiene el valor de la propiedad estadoMiembro.
     * 
     * @return
     *     possible object is
     *     {@link CountryMiembroType }
     *     
     */
    public CountryMiembroType getEstadoMiembro() {
        return estadoMiembro;
    }

    /**
     * Define el valor de la propiedad estadoMiembro.
     * 
     * @param value
     *     allowed object is
     *     {@link CountryMiembroType }
     *     
     */
    public void setEstadoMiembro(CountryMiembroType value) {
        this.estadoMiembro = value;
    }

    /**
     * Obtiene el valor de la propiedad plazoOperacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlazoOperacion() {
        return plazoOperacion;
    }

    /**
     * Define el valor de la propiedad plazoOperacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlazoOperacion(String value) {
        this.plazoOperacion = value;
    }

    /**
     * Obtiene el valor de la propiedad descripcionBienes.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcionBienes() {
        return descripcionBienes;
    }

    /**
     * Define el valor de la propiedad descripcionBienes.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcionBienes(String value) {
        this.descripcionBienes = value;
    }

    /**
     * Obtiene el valor de la propiedad direccionOperador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDireccionOperador() {
        return direccionOperador;
    }

    /**
     * Define el valor de la propiedad direccionOperador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDireccionOperador(String value) {
        this.direccionOperador = value;
    }

    /**
     * Obtiene el valor de la propiedad facturasODocumentacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFacturasODocumentacion() {
        return facturasODocumentacion;
    }

    /**
     * Define el valor de la propiedad facturasODocumentacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFacturasODocumentacion(String value) {
        this.facturasODocumentacion = value;
    }

    /**
     * Obtiene el valor de la propiedad refExterna.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefExterna() {
        return refExterna;
    }

    /**
     * Define el valor de la propiedad refExterna.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefExterna(String value) {
        this.refExterna = value;
    }

    /**
     * Obtiene el valor de la propiedad numRegistroAcuerdoFacturacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumRegistroAcuerdoFacturacion() {
        return numRegistroAcuerdoFacturacion;
    }

    /**
     * Define el valor de la propiedad numRegistroAcuerdoFacturacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumRegistroAcuerdoFacturacion(String value) {
        this.numRegistroAcuerdoFacturacion = value;
    }

    /**
     * Obtiene el valor de la propiedad entidadSucedida.
     * 
     * @return
     *     possible object is
     *     {@link PersonaFisicaJuridicaUnicaESType }
     *     
     */
    public PersonaFisicaJuridicaUnicaESType getEntidadSucedida() {
        return entidadSucedida;
    }

    /**
     * Define el valor de la propiedad entidadSucedida.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonaFisicaJuridicaUnicaESType }
     *     
     */
    public void setEntidadSucedida(PersonaFisicaJuridicaUnicaESType value) {
        this.entidadSucedida = value;
    }

    /**
     * Obtiene el valor de la propiedad regPrevioGGEEoREDEMEoCompetencia.
     * 
     * @return
     *     possible object is
     *     {@link RegPrevioGGEEoREDEMEoCompetenciaType }
     *     
     */
    public RegPrevioGGEEoREDEMEoCompetenciaType getRegPrevioGGEEoREDEMEoCompetencia() {
        return regPrevioGGEEoREDEMEoCompetencia;
    }

    /**
     * Define el valor de la propiedad regPrevioGGEEoREDEMEoCompetencia.
     * 
     * @param value
     *     allowed object is
     *     {@link RegPrevioGGEEoREDEMEoCompetenciaType }
     *     
     */
    public void setRegPrevioGGEEoREDEMEoCompetencia(RegPrevioGGEEoREDEMEoCompetenciaType value) {
        this.regPrevioGGEEoREDEMEoCompetencia = value;
    }

}
