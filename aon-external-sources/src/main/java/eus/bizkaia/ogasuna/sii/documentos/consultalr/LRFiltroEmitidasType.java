
package eus.bizkaia.ogasuna.sii.documentos.consultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.ContraparteConsultaType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.FacturaModificadaType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.IDFacturaConsulta2Type;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.IDFacturaExpedidaBCType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.RangoFechaPresentacionType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.RegistroSii;


/**
 * <p>Clase Java para LRFiltroEmitidasType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="LRFiltroEmitidasType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}RegistroSii"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IDFactura" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}IDFacturaConsulta2Type" minOccurs="0"/&gt;
 *         &lt;element name="Contraparte" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}ContraparteConsultaType" minOccurs="0"/&gt;
 *         &lt;element name="FechaPresentacion" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}RangoFechaPresentacionType" minOccurs="0"/&gt;
 *         &lt;element name="FechaCuadre" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}RangoFechaPresentacionType" minOccurs="0"/&gt;
 *         &lt;element name="FacturaModificada" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}FacturaModificadaType" minOccurs="0"/&gt;
 *         &lt;element name="EstadoCuadre" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}EstadoCuadreType" minOccurs="0"/&gt;
 *         &lt;element name="ClavePaginacion" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}IDFacturaExpedidaBCType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LRFiltroEmitidasType", propOrder = {
    "idFactura",
    "contraparte",
    "fechaPresentacion",
    "fechaCuadre",
    "facturaModificada",
    "estadoCuadre",
    "clavePaginacion"
})
public class LRFiltroEmitidasType
    extends RegistroSii
{

    @XmlElement(name = "IDFactura")
    protected IDFacturaConsulta2Type idFactura;
    @XmlElement(name = "Contraparte")
    protected ContraparteConsultaType contraparte;
    @XmlElement(name = "FechaPresentacion")
    protected RangoFechaPresentacionType fechaPresentacion;
    @XmlElement(name = "FechaCuadre")
    protected RangoFechaPresentacionType fechaCuadre;
    @XmlElement(name = "FacturaModificada")
    @XmlSchemaType(name = "string")
    protected FacturaModificadaType facturaModificada;
    @XmlElement(name = "EstadoCuadre")
    protected String estadoCuadre;
    @XmlElement(name = "ClavePaginacion")
    protected IDFacturaExpedidaBCType clavePaginacion;

    /**
     * Obtiene el valor de la propiedad idFactura.
     * 
     * @return
     *     possible object is
     *     {@link IDFacturaConsulta2Type }
     *     
     */
    public IDFacturaConsulta2Type getIDFactura() {
        return idFactura;
    }

    /**
     * Define el valor de la propiedad idFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link IDFacturaConsulta2Type }
     *     
     */
    public void setIDFactura(IDFacturaConsulta2Type value) {
        this.idFactura = value;
    }

    /**
     * Obtiene el valor de la propiedad contraparte.
     * 
     * @return
     *     possible object is
     *     {@link ContraparteConsultaType }
     *     
     */
    public ContraparteConsultaType getContraparte() {
        return contraparte;
    }

    /**
     * Define el valor de la propiedad contraparte.
     * 
     * @param value
     *     allowed object is
     *     {@link ContraparteConsultaType }
     *     
     */
    public void setContraparte(ContraparteConsultaType value) {
        this.contraparte = value;
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
     * Obtiene el valor de la propiedad fechaCuadre.
     * 
     * @return
     *     possible object is
     *     {@link RangoFechaPresentacionType }
     *     
     */
    public RangoFechaPresentacionType getFechaCuadre() {
        return fechaCuadre;
    }

    /**
     * Define el valor de la propiedad fechaCuadre.
     * 
     * @param value
     *     allowed object is
     *     {@link RangoFechaPresentacionType }
     *     
     */
    public void setFechaCuadre(RangoFechaPresentacionType value) {
        this.fechaCuadre = value;
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
     * Obtiene el valor de la propiedad estadoCuadre.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstadoCuadre() {
        return estadoCuadre;
    }

    /**
     * Define el valor de la propiedad estadoCuadre.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstadoCuadre(String value) {
        this.estadoCuadre = value;
    }

    /**
     * Obtiene el valor de la propiedad clavePaginacion.
     * 
     * @return
     *     possible object is
     *     {@link IDFacturaExpedidaBCType }
     *     
     */
    public IDFacturaExpedidaBCType getClavePaginacion() {
        return clavePaginacion;
    }

    /**
     * Define el valor de la propiedad clavePaginacion.
     * 
     * @param value
     *     allowed object is
     *     {@link IDFacturaExpedidaBCType }
     *     
     */
    public void setClavePaginacion(IDFacturaExpedidaBCType value) {
        this.clavePaginacion = value;
    }

}
