
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestaconsultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.DatosPresentacion2Type;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.IDFacturaExpedidaType;


/**
 * <p>Clase Java para RegistroRespuestaConsultaEmitidasType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RegistroRespuestaConsultaEmitidasType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IDFactura" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}IDFacturaExpedidaType"/&gt;
 *         &lt;element name="DatosFacturaEmitida" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/RespuestaConsultaLR.xsd}FacturaRespuestaExpedidaType"/&gt;
 *         &lt;element name="DatosPresentacion" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}DatosPresentacion2Type"/&gt;
 *         &lt;element name="EstadoFactura" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/RespuestaConsultaLR.xsd}EstadoFacturaType"/&gt;
 *         &lt;element name="DatosDescuadreContraparte" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/RespuestaConsultaLR.xsd}DatosDescuadreContraparteType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistroRespuestaConsultaEmitidasType", propOrder = {
    "idFactura",
    "datosFacturaEmitida",
    "datosPresentacion",
    "estadoFactura",
    "datosDescuadreContraparte"
})
public class RegistroRespuestaConsultaEmitidasType {

    @XmlElement(name = "IDFactura", required = true)
    protected IDFacturaExpedidaType idFactura;
    @XmlElement(name = "DatosFacturaEmitida", required = true)
    protected FacturaRespuestaExpedidaType datosFacturaEmitida;
    @XmlElement(name = "DatosPresentacion", required = true)
    protected DatosPresentacion2Type datosPresentacion;
    @XmlElement(name = "EstadoFactura", required = true)
    protected EstadoFacturaType estadoFactura;
    @XmlElement(name = "DatosDescuadreContraparte")
    protected DatosDescuadreContraparteType datosDescuadreContraparte;

    /**
     * Obtiene el valor de la propiedad idFactura.
     * 
     * @return
     *     possible object is
     *     {@link IDFacturaExpedidaType }
     *     
     */
    public IDFacturaExpedidaType getIDFactura() {
        return idFactura;
    }

    /**
     * Define el valor de la propiedad idFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link IDFacturaExpedidaType }
     *     
     */
    public void setIDFactura(IDFacturaExpedidaType value) {
        this.idFactura = value;
    }

    /**
     * Obtiene el valor de la propiedad datosFacturaEmitida.
     * 
     * @return
     *     possible object is
     *     {@link FacturaRespuestaExpedidaType }
     *     
     */
    public FacturaRespuestaExpedidaType getDatosFacturaEmitida() {
        return datosFacturaEmitida;
    }

    /**
     * Define el valor de la propiedad datosFacturaEmitida.
     * 
     * @param value
     *     allowed object is
     *     {@link FacturaRespuestaExpedidaType }
     *     
     */
    public void setDatosFacturaEmitida(FacturaRespuestaExpedidaType value) {
        this.datosFacturaEmitida = value;
    }

    /**
     * Obtiene el valor de la propiedad datosPresentacion.
     * 
     * @return
     *     possible object is
     *     {@link DatosPresentacion2Type }
     *     
     */
    public DatosPresentacion2Type getDatosPresentacion() {
        return datosPresentacion;
    }

    /**
     * Define el valor de la propiedad datosPresentacion.
     * 
     * @param value
     *     allowed object is
     *     {@link DatosPresentacion2Type }
     *     
     */
    public void setDatosPresentacion(DatosPresentacion2Type value) {
        this.datosPresentacion = value;
    }

    /**
     * Obtiene el valor de la propiedad estadoFactura.
     * 
     * @return
     *     possible object is
     *     {@link EstadoFacturaType }
     *     
     */
    public EstadoFacturaType getEstadoFactura() {
        return estadoFactura;
    }

    /**
     * Define el valor de la propiedad estadoFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link EstadoFacturaType }
     *     
     */
    public void setEstadoFactura(EstadoFacturaType value) {
        this.estadoFactura = value;
    }

    /**
     * Obtiene el valor de la propiedad datosDescuadreContraparte.
     * 
     * @return
     *     possible object is
     *     {@link DatosDescuadreContraparteType }
     *     
     */
    public DatosDescuadreContraparteType getDatosDescuadreContraparte() {
        return datosDescuadreContraparte;
    }

    /**
     * Define el valor de la propiedad datosDescuadreContraparte.
     * 
     * @param value
     *     allowed object is
     *     {@link DatosDescuadreContraparteType }
     *     
     */
    public void setDatosDescuadreContraparte(DatosDescuadreContraparteType value) {
        this.datosDescuadreContraparte = value;
    }

}
