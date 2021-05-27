
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.consultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.ClavePaginacionBienType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.FacturaModificadaType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.IDFacturaConsulta1Type;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.RangoFechaPresentacionType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.RegistroSii;


/**
 * <p>Clase Java para LRFiltroBienInversionType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="LRFiltroBienInversionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}RegistroSii"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IDFactura" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}IDFacturaConsulta1Type" minOccurs="0"/&gt;
 *         &lt;element name="FechaPresentacion" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}RangoFechaPresentacionType" minOccurs="0"/&gt;
 *         &lt;element name="FacturaModificada" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}FacturaModificadaType" minOccurs="0"/&gt;
 *         &lt;element name="IdentificacionBien" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}TextMax40Type" minOccurs="0"/&gt;
 *         &lt;element name="ClavePaginacion" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}ClavePaginacionBienType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LRFiltroBienInversionType", propOrder = {
    "idFactura",
    "fechaPresentacion",
    "facturaModificada",
    "identificacionBien",
    "clavePaginacion"
})
public class LRFiltroBienInversionType
    extends RegistroSii
{

    @XmlElement(name = "IDFactura")
    protected IDFacturaConsulta1Type idFactura;
    @XmlElement(name = "FechaPresentacion")
    protected RangoFechaPresentacionType fechaPresentacion;
    @XmlElement(name = "FacturaModificada")
    @XmlSchemaType(name = "string")
    protected FacturaModificadaType facturaModificada;
    @XmlElement(name = "IdentificacionBien")
    protected String identificacionBien;
    @XmlElement(name = "ClavePaginacion")
    protected ClavePaginacionBienType clavePaginacion;

    /**
     * Obtiene el valor de la propiedad idFactura.
     * 
     * @return
     *     possible object is
     *     {@link IDFacturaConsulta1Type }
     *     
     */
    public IDFacturaConsulta1Type getIDFactura() {
        return idFactura;
    }

    /**
     * Define el valor de la propiedad idFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link IDFacturaConsulta1Type }
     *     
     */
    public void setIDFactura(IDFacturaConsulta1Type value) {
        this.idFactura = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaPresentacion.
     * 
     * @return
     *     possible object is
     *     {@link RangoFechaPresentacionType }
     *     
     */
    public RangoFechaPresentacionType getFechaPresentacion() {
        return fechaPresentacion;
    }

    /**
     * Define el valor de la propiedad fechaPresentacion.
     * 
     * @param value
     *     allowed object is
     *     {@link RangoFechaPresentacionType }
     *     
     */
    public void setFechaPresentacion(RangoFechaPresentacionType value) {
        this.fechaPresentacion = value;
    }

    /**
     * Obtiene el valor de la propiedad facturaModificada.
     * 
     * @return
     *     possible object is
     *     {@link FacturaModificadaType }
     *     
     */
    public FacturaModificadaType getFacturaModificada() {
        return facturaModificada;
    }

    /**
     * Define el valor de la propiedad facturaModificada.
     * 
     * @param value
     *     allowed object is
     *     {@link FacturaModificadaType }
     *     
     */
    public void setFacturaModificada(FacturaModificadaType value) {
        this.facturaModificada = value;
    }

    /**
     * Obtiene el valor de la propiedad identificacionBien.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificacionBien() {
        return identificacionBien;
    }

    /**
     * Define el valor de la propiedad identificacionBien.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificacionBien(String value) {
        this.identificacionBien = value;
    }

    /**
     * Obtiene el valor de la propiedad clavePaginacion.
     * 
     * @return
     *     possible object is
     *     {@link ClavePaginacionBienType }
     *     
     */
    public ClavePaginacionBienType getClavePaginacion() {
        return clavePaginacion;
    }

    /**
     * Define el valor de la propiedad clavePaginacion.
     * 
     * @param value
     *     allowed object is
     *     {@link ClavePaginacionBienType }
     *     
     */
    public void setClavePaginacion(ClavePaginacionBienType value) {
        this.clavePaginacion = value;
    }

}
