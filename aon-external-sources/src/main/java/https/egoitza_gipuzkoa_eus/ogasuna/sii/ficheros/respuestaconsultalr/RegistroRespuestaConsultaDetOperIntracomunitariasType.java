
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestaconsultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.DatosPresentacion2Type;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.IDFacturaComunitariaType;


/**
 * <p>Clase Java para RegistroRespuestaConsultaDetOperIntracomunitariasType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RegistroRespuestaConsultaDetOperIntracomunitariasType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IDFactura" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}IDFacturaComunitariaType"/&gt;
 *         &lt;element name="DatosDetOperIntracomunitarias" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/RespuestaConsultaLR.xsd}RespuestaDetOperIntracomunitariaType"/&gt;
 *         &lt;element name="DatosPresentacion" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}DatosPresentacion2Type"/&gt;
 *         &lt;element name="EstadoFactura" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/RespuestaConsultaLR.xsd}EstadoFactura2Type"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistroRespuestaConsultaDetOperIntracomunitariasType", propOrder = {
    "idFactura",
    "datosDetOperIntracomunitarias",
    "datosPresentacion",
    "estadoFactura"
})
public class RegistroRespuestaConsultaDetOperIntracomunitariasType {

    @XmlElement(name = "IDFactura", required = true)
    protected IDFacturaComunitariaType idFactura;
    @XmlElement(name = "DatosDetOperIntracomunitarias", required = true)
    protected RespuestaDetOperIntracomunitariaType datosDetOperIntracomunitarias;
    @XmlElement(name = "DatosPresentacion", required = true)
    protected DatosPresentacion2Type datosPresentacion;
    @XmlElement(name = "EstadoFactura", required = true)
    protected EstadoFactura2Type estadoFactura;

    /**
     * Obtiene el valor de la propiedad idFactura.
     * 
     * @return
     *     possible object is
     *     {@link IDFacturaComunitariaType }
     *     
     */
    public IDFacturaComunitariaType getIDFactura() {
        return idFactura;
    }

    /**
     * Define el valor de la propiedad idFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link IDFacturaComunitariaType }
     *     
     */
    public void setIDFactura(IDFacturaComunitariaType value) {
        this.idFactura = value;
    }

    /**
     * Obtiene el valor de la propiedad datosDetOperIntracomunitarias.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaDetOperIntracomunitariaType }
     *     
     */
    public RespuestaDetOperIntracomunitariaType getDatosDetOperIntracomunitarias() {
        return datosDetOperIntracomunitarias;
    }

    /**
     * Define el valor de la propiedad datosDetOperIntracomunitarias.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaDetOperIntracomunitariaType }
     *     
     */
    public void setDatosDetOperIntracomunitarias(RespuestaDetOperIntracomunitariaType value) {
        this.datosDetOperIntracomunitarias = value;
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
     *     {@link EstadoFactura2Type }
     *     
     */
    public EstadoFactura2Type getEstadoFactura() {
        return estadoFactura;
    }

    /**
     * Define el valor de la propiedad estadoFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link EstadoFactura2Type }
     *     
     */
    public void setEstadoFactura(EstadoFactura2Type value) {
        this.estadoFactura = value;
    }

}
