
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestaconsultalr;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.CompletaSinDestinatarioType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.CuponType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.DatosInmuebleType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.EmitidaPorTercerosType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.TipoConDesgloseType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.TipoSinDesgloseType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.VariosDestinatariosType;


/**
 *  Apunte correspondiente al libro de facturas expedidas. 
 * 
 * <p>Clase Java para FacturaRespuestaInformadaProveedorType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="FacturaRespuestaInformadaProveedorType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/RespuestaConsultaLR.xsd}FacturaRespuestaType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DatosInmueble" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="DetalleInmueble" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}DatosInmuebleType" maxOccurs="15"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ImporteTransmisionInmueblesSujetoAIVA" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}ImporteSgn12.2Type" minOccurs="0"/&gt;
 *         &lt;element name="EmitidaPorTercerosODestinatario" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}EmitidaPorTercerosType" minOccurs="0"/&gt;
 *         &lt;element name="FacturacionDispAdicionalTerceraYsextayDelMercadoOrganizadoDelGas" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}EmitidaPorTercerosType" minOccurs="0"/&gt;
 *         &lt;element name="VariosDestinatarios" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}VariosDestinatariosType" minOccurs="0"/&gt;
 *         &lt;element name="Cupon" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}CuponType" minOccurs="0"/&gt;
 *         &lt;element name="FacturaSinIdentifDestinatarioAritculo6.1.d" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}CompletaSinDestinatarioType" minOccurs="0"/&gt;
 *         &lt;element name="TipoDesglose"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;choice&gt;
 *                   &lt;element name="DesgloseFactura" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}TipoSinDesgloseType"/&gt;
 *                   &lt;element name="DesgloseTipoOperacion" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}TipoConDesgloseType"/&gt;
 *                 &lt;/choice&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Cobros" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/RespuestaConsultaLR.xsd}FacturaARType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FacturaRespuestaInformadaProveedorType", propOrder = {
    "datosInmueble",
    "importeTransmisionInmueblesSujetoAIVA",
    "emitidaPorTercerosODestinatario",
    "facturacionDispAdicionalTerceraYsextayDelMercadoOrganizadoDelGas",
    "variosDestinatarios",
    "cupon",
    "facturaSinIdentifDestinatarioAritculo61D",
    "tipoDesglose",
    "cobros"
})
public class FacturaRespuestaInformadaProveedorType
    extends FacturaRespuestaType
{

    @XmlElement(name = "DatosInmueble")
    protected FacturaRespuestaInformadaProveedorType.DatosInmueble datosInmueble;
    @XmlElement(name = "ImporteTransmisionInmueblesSujetoAIVA")
    protected String importeTransmisionInmueblesSujetoAIVA;
    @XmlElement(name = "EmitidaPorTercerosODestinatario")
    @XmlSchemaType(name = "string")
    protected EmitidaPorTercerosType emitidaPorTercerosODestinatario;
    @XmlElement(name = "FacturacionDispAdicionalTerceraYsextayDelMercadoOrganizadoDelGas")
    @XmlSchemaType(name = "string")
    protected EmitidaPorTercerosType facturacionDispAdicionalTerceraYsextayDelMercadoOrganizadoDelGas;
    @XmlElement(name = "VariosDestinatarios")
    @XmlSchemaType(name = "string")
    protected VariosDestinatariosType variosDestinatarios;
    @XmlElement(name = "Cupon")
    @XmlSchemaType(name = "string")
    protected CuponType cupon;
    @XmlElement(name = "FacturaSinIdentifDestinatarioAritculo6.1.d")
    @XmlSchemaType(name = "string")
    protected CompletaSinDestinatarioType facturaSinIdentifDestinatarioAritculo61D;
    @XmlElement(name = "TipoDesglose", required = true)
    protected FacturaRespuestaInformadaProveedorType.TipoDesglose tipoDesglose;
    @XmlElement(name = "Cobros", required = true)
    @XmlSchemaType(name = "string")
    protected FacturaARType cobros;

    /**
     * Obtiene el valor de la propiedad datosInmueble.
     * 
     * @return
     *     possible object is
     *     {@link FacturaRespuestaInformadaProveedorType.DatosInmueble }
     *     
     */
    public FacturaRespuestaInformadaProveedorType.DatosInmueble getDatosInmueble() {
        return datosInmueble;
    }

    /**
     * Define el valor de la propiedad datosInmueble.
     * 
     * @param value
     *     allowed object is
     *     {@link FacturaRespuestaInformadaProveedorType.DatosInmueble }
     *     
     */
    public void setDatosInmueble(FacturaRespuestaInformadaProveedorType.DatosInmueble value) {
        this.datosInmueble = value;
    }

    /**
     * Obtiene el valor de la propiedad importeTransmisionInmueblesSujetoAIVA.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImporteTransmisionInmueblesSujetoAIVA() {
        return importeTransmisionInmueblesSujetoAIVA;
    }

    /**
     * Define el valor de la propiedad importeTransmisionInmueblesSujetoAIVA.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImporteTransmisionInmueblesSujetoAIVA(String value) {
        this.importeTransmisionInmueblesSujetoAIVA = value;
    }

    /**
     * Obtiene el valor de la propiedad emitidaPorTercerosODestinatario.
     * 
     * @return
     *     possible object is
     *     {@link EmitidaPorTercerosType }
     *     
     */
    public EmitidaPorTercerosType getEmitidaPorTercerosODestinatario() {
        return emitidaPorTercerosODestinatario;
    }

    /**
     * Define el valor de la propiedad emitidaPorTercerosODestinatario.
     * 
     * @param value
     *     allowed object is
     *     {@link EmitidaPorTercerosType }
     *     
     */
    public void setEmitidaPorTercerosODestinatario(EmitidaPorTercerosType value) {
        this.emitidaPorTercerosODestinatario = value;
    }

    /**
     * Obtiene el valor de la propiedad facturacionDispAdicionalTerceraYsextayDelMercadoOrganizadoDelGas.
     * 
     * @return
     *     possible object is
     *     {@link EmitidaPorTercerosType }
     *     
     */
    public EmitidaPorTercerosType getFacturacionDispAdicionalTerceraYsextayDelMercadoOrganizadoDelGas() {
        return facturacionDispAdicionalTerceraYsextayDelMercadoOrganizadoDelGas;
    }

    /**
     * Define el valor de la propiedad facturacionDispAdicionalTerceraYsextayDelMercadoOrganizadoDelGas.
     * 
     * @param value
     *     allowed object is
     *     {@link EmitidaPorTercerosType }
     *     
     */
    public void setFacturacionDispAdicionalTerceraYsextayDelMercadoOrganizadoDelGas(EmitidaPorTercerosType value) {
        this.facturacionDispAdicionalTerceraYsextayDelMercadoOrganizadoDelGas = value;
    }

    /**
     * Obtiene el valor de la propiedad variosDestinatarios.
     * 
     * @return
     *     possible object is
     *     {@link VariosDestinatariosType }
     *     
     */
    public VariosDestinatariosType getVariosDestinatarios() {
        return variosDestinatarios;
    }

    /**
     * Define el valor de la propiedad variosDestinatarios.
     * 
     * @param value
     *     allowed object is
     *     {@link VariosDestinatariosType }
     *     
     */
    public void setVariosDestinatarios(VariosDestinatariosType value) {
        this.variosDestinatarios = value;
    }

    /**
     * Obtiene el valor de la propiedad cupon.
     * 
     * @return
     *     possible object is
     *     {@link CuponType }
     *     
     */
    public CuponType getCupon() {
        return cupon;
    }

    /**
     * Define el valor de la propiedad cupon.
     * 
     * @param value
     *     allowed object is
     *     {@link CuponType }
     *     
     */
    public void setCupon(CuponType value) {
        this.cupon = value;
    }

    /**
     * Obtiene el valor de la propiedad facturaSinIdentifDestinatarioAritculo61D.
     * 
     * @return
     *     possible object is
     *     {@link CompletaSinDestinatarioType }
     *     
     */
    public CompletaSinDestinatarioType getFacturaSinIdentifDestinatarioAritculo61D() {
        return facturaSinIdentifDestinatarioAritculo61D;
    }

    /**
     * Define el valor de la propiedad facturaSinIdentifDestinatarioAritculo61D.
     * 
     * @param value
     *     allowed object is
     *     {@link CompletaSinDestinatarioType }
     *     
     */
    public void setFacturaSinIdentifDestinatarioAritculo61D(CompletaSinDestinatarioType value) {
        this.facturaSinIdentifDestinatarioAritculo61D = value;
    }

    /**
     * Obtiene el valor de la propiedad tipoDesglose.
     * 
     * @return
     *     possible object is
     *     {@link FacturaRespuestaInformadaProveedorType.TipoDesglose }
     *     
     */
    public FacturaRespuestaInformadaProveedorType.TipoDesglose getTipoDesglose() {
        return tipoDesglose;
    }

    /**
     * Define el valor de la propiedad tipoDesglose.
     * 
     * @param value
     *     allowed object is
     *     {@link FacturaRespuestaInformadaProveedorType.TipoDesglose }
     *     
     */
    public void setTipoDesglose(FacturaRespuestaInformadaProveedorType.TipoDesglose value) {
        this.tipoDesglose = value;
    }

    /**
     * Obtiene el valor de la propiedad cobros.
     * 
     * @return
     *     possible object is
     *     {@link FacturaARType }
     *     
     */
    public FacturaARType getCobros() {
        return cobros;
    }

    /**
     * Define el valor de la propiedad cobros.
     * 
     * @param value
     *     allowed object is
     *     {@link FacturaARType }
     *     
     */
    public void setCobros(FacturaARType value) {
        this.cobros = value;
    }


    /**
     * Desglose de inmuebles
     * 
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="DetalleInmueble" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}DatosInmuebleType" maxOccurs="15"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "detalleInmueble"
    })
    public static class DatosInmueble {

        @XmlElement(name = "DetalleInmueble", required = true)
        protected List<DatosInmuebleType> detalleInmueble;

        /**
         * Gets the value of the detalleInmueble property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the detalleInmueble property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDetalleInmueble().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link DatosInmuebleType }
         * 
         * 
         */
        public List<DatosInmuebleType> getDetalleInmueble() {
            if (detalleInmueble == null) {
                detalleInmueble = new ArrayList<DatosInmuebleType>();
            }
            return this.detalleInmueble;
        }

    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;choice&gt;
     *         &lt;element name="DesgloseFactura" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}TipoSinDesgloseType"/&gt;
     *         &lt;element name="DesgloseTipoOperacion" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}TipoConDesgloseType"/&gt;
     *       &lt;/choice&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "desgloseFactura",
        "desgloseTipoOperacion"
    })
    public static class TipoDesglose {

        @XmlElement(name = "DesgloseFactura")
        protected TipoSinDesgloseType desgloseFactura;
        @XmlElement(name = "DesgloseTipoOperacion")
        protected TipoConDesgloseType desgloseTipoOperacion;

        /**
         * Obtiene el valor de la propiedad desgloseFactura.
         * 
         * @return
         *     possible object is
         *     {@link TipoSinDesgloseType }
         *     
         */
        public TipoSinDesgloseType getDesgloseFactura() {
            return desgloseFactura;
        }

        /**
         * Define el valor de la propiedad desgloseFactura.
         * 
         * @param value
         *     allowed object is
         *     {@link TipoSinDesgloseType }
         *     
         */
        public void setDesgloseFactura(TipoSinDesgloseType value) {
            this.desgloseFactura = value;
        }

        /**
         * Obtiene el valor de la propiedad desgloseTipoOperacion.
         * 
         * @return
         *     possible object is
         *     {@link TipoConDesgloseType }
         *     
         */
        public TipoConDesgloseType getDesgloseTipoOperacion() {
            return desgloseTipoOperacion;
        }

        /**
         * Define el valor de la propiedad desgloseTipoOperacion.
         * 
         * @param value
         *     allowed object is
         *     {@link TipoConDesgloseType }
         *     
         */
        public void setDesgloseTipoOperacion(TipoConDesgloseType value) {
            this.desgloseTipoOperacion = value;
        }

    }

}
