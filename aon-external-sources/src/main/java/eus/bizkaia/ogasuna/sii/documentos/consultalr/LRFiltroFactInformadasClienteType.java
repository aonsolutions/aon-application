
package eus.bizkaia.ogasuna.sii.documentos.consultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.ClavePaginacionClienteType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.PersonaFisicaJuridicaUnicaESType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.RangoFechaType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.RegistroSiiImputacion;


/**
 * <p>Clase Java para LRFiltroFactInformadasClienteType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="LRFiltroFactInformadasClienteType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}RegistroSiiImputacion"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Cliente" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}PersonaFisicaJuridicaUnicaESType" minOccurs="0"/&gt;
 *         &lt;element name="NumSerieFacturaEmisor" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}TextoIDFacturaType" minOccurs="0"/&gt;
 *         &lt;element name="EstadoCuadre" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}EstadoCuadreImputacionType" minOccurs="0"/&gt;
 *         &lt;element name="FechaExpedicion" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}RangoFechaType" minOccurs="0"/&gt;
 *         &lt;element name="FechaOperacion" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}RangoFechaType" minOccurs="0"/&gt;
 *         &lt;element name="ClavePaginacion" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}ClavePaginacionClienteType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LRFiltroFactInformadasClienteType", propOrder = {
    "cliente",
    "numSerieFacturaEmisor",
    "estadoCuadre",
    "fechaExpedicion",
    "fechaOperacion",
    "clavePaginacion"
})
public class LRFiltroFactInformadasClienteType
    extends RegistroSiiImputacion
{

    @XmlElement(name = "Cliente")
    protected PersonaFisicaJuridicaUnicaESType cliente;
    @XmlElement(name = "NumSerieFacturaEmisor")
    protected String numSerieFacturaEmisor;
    @XmlElement(name = "EstadoCuadre")
    protected String estadoCuadre;
    @XmlElement(name = "FechaExpedicion")
    protected RangoFechaType fechaExpedicion;
    @XmlElement(name = "FechaOperacion")
    protected RangoFechaType fechaOperacion;
    @XmlElement(name = "ClavePaginacion")
    protected ClavePaginacionClienteType clavePaginacion;

    /**
     * Obtiene el valor de la propiedad cliente.
     * 
     * @return
     *     possible object is
     *     {@link PersonaFisicaJuridicaUnicaESType }
     *     
     */
    public PersonaFisicaJuridicaUnicaESType getCliente() {
        return cliente;
    }

    /**
     * Define el valor de la propiedad cliente.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonaFisicaJuridicaUnicaESType }
     *     
     */
    public void setCliente(PersonaFisicaJuridicaUnicaESType value) {
        this.cliente = value;
    }

    /**
     * Obtiene el valor de la propiedad numSerieFacturaEmisor.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumSerieFacturaEmisor() {
        return numSerieFacturaEmisor;
    }

    /**
     * Define el valor de la propiedad numSerieFacturaEmisor.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumSerieFacturaEmisor(String value) {
        this.numSerieFacturaEmisor = value;
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
     * Obtiene el valor de la propiedad fechaExpedicion.
     * 
     * @return
     *     possible object is
     *     {@link RangoFechaType }
     *     
     */
    public RangoFechaType getFechaExpedicion() {
        return fechaExpedicion;
    }

    /**
     * Define el valor de la propiedad fechaExpedicion.
     * 
     * @param value
     *     allowed object is
     *     {@link RangoFechaType }
     *     
     */
    public void setFechaExpedicion(RangoFechaType value) {
        this.fechaExpedicion = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaOperacion.
     * 
     * @return
     *     possible object is
     *     {@link RangoFechaType }
     *     
     */
    public RangoFechaType getFechaOperacion() {
        return fechaOperacion;
    }

    /**
     * Define el valor de la propiedad fechaOperacion.
     * 
     * @param value
     *     allowed object is
     *     {@link RangoFechaType }
     *     
     */
    public void setFechaOperacion(RangoFechaType value) {
        this.fechaOperacion = value;
    }

    /**
     * Obtiene el valor de la propiedad clavePaginacion.
     * 
     * @return
     *     possible object is
     *     {@link ClavePaginacionClienteType }
     *     
     */
    public ClavePaginacionClienteType getClavePaginacion() {
        return clavePaginacion;
    }

    /**
     * Define el valor de la propiedad clavePaginacion.
     * 
     * @param value
     *     allowed object is
     *     {@link ClavePaginacionClienteType }
     *     
     */
    public void setClavePaginacion(ClavePaginacionClienteType value) {
        this.clavePaginacion = value;
    }

}
