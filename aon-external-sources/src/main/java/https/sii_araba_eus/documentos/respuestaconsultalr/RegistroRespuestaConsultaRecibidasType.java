
package https.sii_araba_eus.documentos.respuestaconsultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.sii_araba_eus.documentos.suministroinformacion.DatosPresentacion2Type;
import https.sii_araba_eus.documentos.suministroinformacion.IDFacturaRecibidaType;


/**
 * <p>Clase Java para RegistroRespuestaConsultaRecibidasType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RegistroRespuestaConsultaRecibidasType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IDFactura" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}IDFacturaRecibidaType"/&gt;
 *         &lt;element name="DatosFacturaRecibida" type="{https://sii.araba.eus/documentos/RespuestaConsultaLR.xsd}FacturaRespuestaRecibidaType"/&gt;
 *         &lt;element name="DatosPresentacion" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}DatosPresentacion2Type"/&gt;
 *         &lt;element name="EstadoFactura" type="{https://sii.araba.eus/documentos/RespuestaConsultaLR.xsd}EstadoFacturaType"/&gt;
 *         &lt;element name="DatosDescuadreContraparte" type="{https://sii.araba.eus/documentos/RespuestaConsultaLR.xsd}DatosDescuadreContraparteType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistroRespuestaConsultaRecibidasType", propOrder = {
    "idFactura",
    "datosFacturaRecibida",
    "datosPresentacion",
    "estadoFactura",
    "datosDescuadreContraparte"
})
public class RegistroRespuestaConsultaRecibidasType {

    @XmlElement(name = "IDFactura", required = true)
    protected IDFacturaRecibidaType idFactura;
    @XmlElement(name = "DatosFacturaRecibida", required = true)
    protected FacturaRespuestaRecibidaType datosFacturaRecibida;
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
     *     {@link IDFacturaRecibidaType }
     *     
     */
    public IDFacturaRecibidaType getIDFactura() {
        return idFactura;
    }

    /**
     * Define el valor de la propiedad idFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link IDFacturaRecibidaType }
     *     
     */
    public void setIDFactura(IDFacturaRecibidaType value) {
        this.idFactura = value;
    }

    /**
     * Obtiene el valor de la propiedad datosFacturaRecibida.
     * 
     * @return
     *     possible object is
     *     {@link FacturaRespuestaRecibidaType }
     *     
     */
    public FacturaRespuestaRecibidaType getDatosFacturaRecibida() {
        return datosFacturaRecibida;
    }

    /**
     * Define el valor de la propiedad datosFacturaRecibida.
     * 
     * @param value
     *     allowed object is
     *     {@link FacturaRespuestaRecibidaType }
     *     
     */
    public void setDatosFacturaRecibida(FacturaRespuestaRecibidaType value) {
        this.datosFacturaRecibida = value;
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
