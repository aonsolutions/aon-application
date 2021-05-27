
package eus.bizkaia.ogasuna.sii.documentos.respuestaconsultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.DatosPresentacion2Type;


/**
 * <p>Clase Java para RegistroRespuestaConsultaAgenciasViajesType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RegistroRespuestaConsultaAgenciasViajesType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DatosAgenciasViajes" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd}RespuestaCobrosMetalicoType"/&gt;
 *         &lt;element name="DatosPresentacion" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}DatosPresentacion2Type"/&gt;
 *         &lt;element name="EstadoAgenciasViajes" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/RespuestaConsultaLR.xsd}EstadoFactura2Type"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistroRespuestaConsultaAgenciasViajesType", propOrder = {
    "datosAgenciasViajes",
    "datosPresentacion",
    "estadoAgenciasViajes"
})
public class RegistroRespuestaConsultaAgenciasViajesType {

    @XmlElement(name = "DatosAgenciasViajes", required = true)
    protected RespuestaCobrosMetalicoType datosAgenciasViajes;
    @XmlElement(name = "DatosPresentacion", required = true)
    protected DatosPresentacion2Type datosPresentacion;
    @XmlElement(name = "EstadoAgenciasViajes", required = true)
    protected EstadoFactura2Type estadoAgenciasViajes;

    /**
     * Obtiene el valor de la propiedad datosAgenciasViajes.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaCobrosMetalicoType }
     *     
     */
    public RespuestaCobrosMetalicoType getDatosAgenciasViajes() {
        return datosAgenciasViajes;
    }

    /**
     * Define el valor de la propiedad datosAgenciasViajes.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaCobrosMetalicoType }
     *     
     */
    public void setDatosAgenciasViajes(RespuestaCobrosMetalicoType value) {
        this.datosAgenciasViajes = value;
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
     * Obtiene el valor de la propiedad estadoAgenciasViajes.
     * 
     * @return
     *     possible object is
     *     {@link EstadoFactura2Type }
     *     
     */
    public EstadoFactura2Type getEstadoAgenciasViajes() {
        return estadoAgenciasViajes;
    }

    /**
     * Define el valor de la propiedad estadoAgenciasViajes.
     * 
     * @param value
     *     allowed object is
     *     {@link EstadoFactura2Type }
     *     
     */
    public void setEstadoAgenciasViajes(EstadoFactura2Type value) {
        this.estadoAgenciasViajes = value;
    }

}
