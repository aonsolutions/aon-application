
package https.sii_araba_eus.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para DetalleIVARecibidaType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DetalleIVARecibidaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TipoImpositivo" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}Tipo2.2Type" minOccurs="0"/&gt;
 *         &lt;element name="BaseImponible" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}ImporteSgn12.2Type"/&gt;
 *         &lt;element name="CuotaSoportada" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}ImporteSgn12.2Type" minOccurs="0"/&gt;
 *         &lt;element name="TipoRecargoEquivalencia" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}Tipo2.2Type" minOccurs="0"/&gt;
 *         &lt;element name="CuotaRecargoEquivalencia" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}ImporteSgn12.2Type" minOccurs="0"/&gt;
 *         &lt;element name="PorcentCompensacionREAGYP" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}Tipo2.2Type" minOccurs="0"/&gt;
 *         &lt;element name="ImporteCompensacionREAGYP" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}ImporteSgn12.2Type" minOccurs="0"/&gt;
 *         &lt;element name="BienInversion" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}BienInversionType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DetalleIVARecibidaType", propOrder = {
    "tipoImpositivo",
    "baseImponible",
    "cuotaSoportada",
    "tipoRecargoEquivalencia",
    "cuotaRecargoEquivalencia",
    "porcentCompensacionREAGYP",
    "importeCompensacionREAGYP",
    "bienInversion"
})
public class DetalleIVARecibidaType {

    @XmlElement(name = "TipoImpositivo")
    protected String tipoImpositivo;
    @XmlElement(name = "BaseImponible", required = true)
    protected String baseImponible;
    @XmlElement(name = "CuotaSoportada")
    protected String cuotaSoportada;
    @XmlElement(name = "TipoRecargoEquivalencia")
    protected String tipoRecargoEquivalencia;
    @XmlElement(name = "CuotaRecargoEquivalencia")
    protected String cuotaRecargoEquivalencia;
    @XmlElement(name = "PorcentCompensacionREAGYP")
    protected String porcentCompensacionREAGYP;
    @XmlElement(name = "ImporteCompensacionREAGYP")
    protected String importeCompensacionREAGYP;
    @XmlElement(name = "BienInversion")
    @XmlSchemaType(name = "string")
    protected BienInversionType bienInversion;

    /**
     * Obtiene el valor de la propiedad tipoImpositivo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoImpositivo() {
        return tipoImpositivo;
    }

    /**
     * Define el valor de la propiedad tipoImpositivo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoImpositivo(String value) {
        this.tipoImpositivo = value;
    }

    /**
     * Obtiene el valor de la propiedad baseImponible.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseImponible() {
        return baseImponible;
    }

    /**
     * Define el valor de la propiedad baseImponible.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseImponible(String value) {
        this.baseImponible = value;
    }

    /**
     * Obtiene el valor de la propiedad cuotaSoportada.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCuotaSoportada() {
        return cuotaSoportada;
    }

    /**
     * Define el valor de la propiedad cuotaSoportada.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCuotaSoportada(String value) {
        this.cuotaSoportada = value;
    }

    /**
     * Obtiene el valor de la propiedad tipoRecargoEquivalencia.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoRecargoEquivalencia() {
        return tipoRecargoEquivalencia;
    }

    /**
     * Define el valor de la propiedad tipoRecargoEquivalencia.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoRecargoEquivalencia(String value) {
        this.tipoRecargoEquivalencia = value;
    }

    /**
     * Obtiene el valor de la propiedad cuotaRecargoEquivalencia.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCuotaRecargoEquivalencia() {
        return cuotaRecargoEquivalencia;
    }

    /**
     * Define el valor de la propiedad cuotaRecargoEquivalencia.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCuotaRecargoEquivalencia(String value) {
        this.cuotaRecargoEquivalencia = value;
    }

    /**
     * Obtiene el valor de la propiedad porcentCompensacionREAGYP.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPorcentCompensacionREAGYP() {
        return porcentCompensacionREAGYP;
    }

    /**
     * Define el valor de la propiedad porcentCompensacionREAGYP.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPorcentCompensacionREAGYP(String value) {
        this.porcentCompensacionREAGYP = value;
    }

    /**
     * Obtiene el valor de la propiedad importeCompensacionREAGYP.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImporteCompensacionREAGYP() {
        return importeCompensacionREAGYP;
    }

    /**
     * Define el valor de la propiedad importeCompensacionREAGYP.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImporteCompensacionREAGYP(String value) {
        this.importeCompensacionREAGYP = value;
    }

    /**
     * Obtiene el valor de la propiedad bienInversion.
     * 
     * @return
     *     possible object is
     *     {@link BienInversionType }
     *     
     */
    public BienInversionType getBienInversion() {
        return bienInversion;
    }

    /**
     * Define el valor de la propiedad bienInversion.
     * 
     * @param value
     *     allowed object is
     *     {@link BienInversionType }
     *     
     */
    public void setBienInversion(BienInversionType value) {
        this.bienInversion = value;
    }

}
