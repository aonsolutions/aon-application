
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestaconsultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.DatosPresentacion2Type;


/**
 * <p>Clase Java para RegistroRespuestaConsultaCobrosMetalicoType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RegistroRespuestaConsultaCobrosMetalicoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DatosCobroMetalico" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/RespuestaConsultaLR.xsd}RespuestaCobrosMetalicoType"/&gt;
 *         &lt;element name="DatosPresentacion" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}DatosPresentacion2Type"/&gt;
 *         &lt;element name="EstadoCobroMetalico" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/RespuestaConsultaLR.xsd}EstadoFactura2Type"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistroRespuestaConsultaCobrosMetalicoType", propOrder = {
    "datosCobroMetalico",
    "datosPresentacion",
    "estadoCobroMetalico"
})
public class RegistroRespuestaConsultaCobrosMetalicoType {

    @XmlElement(name = "DatosCobroMetalico", required = true)
    protected RespuestaCobrosMetalicoType datosCobroMetalico;
    @XmlElement(name = "DatosPresentacion", required = true)
    protected DatosPresentacion2Type datosPresentacion;
    @XmlElement(name = "EstadoCobroMetalico", required = true)
    protected EstadoFactura2Type estadoCobroMetalico;

    /**
     * Obtiene el valor de la propiedad datosCobroMetalico.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaCobrosMetalicoType }
     *     
     */
    public RespuestaCobrosMetalicoType getDatosCobroMetalico() {
        return datosCobroMetalico;
    }

    /**
     * Define el valor de la propiedad datosCobroMetalico.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaCobrosMetalicoType }
     *     
     */
    public void setDatosCobroMetalico(RespuestaCobrosMetalicoType value) {
        this.datosCobroMetalico = value;
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
     * Obtiene el valor de la propiedad estadoCobroMetalico.
     * 
     * @return
     *     possible object is
     *     {@link EstadoFactura2Type }
     *     
     */
    public EstadoFactura2Type getEstadoCobroMetalico() {
        return estadoCobroMetalico;
    }

    /**
     * Define el valor de la propiedad estadoCobroMetalico.
     * 
     * @param value
     *     allowed object is
     *     {@link EstadoFactura2Type }
     *     
     */
    public void setEstadoCobroMetalico(EstadoFactura2Type value) {
        this.estadoCobroMetalico = value;
    }

}
