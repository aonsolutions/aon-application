
package https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestaconsultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.DatosPresentacion2Type;


/**
 * <p>Clase Java para RegistroRespuestaConsultaOperacionesSegurosType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RegistroRespuestaConsultaOperacionesSegurosType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DatosOperacionesSeguros" type="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/RespuestaConsultaLR.xsd}RespuestaOperacionesSegurosType"/&gt;
 *         &lt;element name="DatosPresentacion" type="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroInformacion.xsd}DatosPresentacion2Type"/&gt;
 *         &lt;element name="EstadoOperacionesSeguros" type="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/RespuestaConsultaLR.xsd}EstadoFactura2Type"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistroRespuestaConsultaOperacionesSegurosType", propOrder = {
    "datosOperacionesSeguros",
    "datosPresentacion",
    "estadoOperacionesSeguros"
})
public class RegistroRespuestaConsultaOperacionesSegurosType {

    @XmlElement(name = "DatosOperacionesSeguros", required = true)
    protected RespuestaOperacionesSegurosType datosOperacionesSeguros;
    @XmlElement(name = "DatosPresentacion", required = true)
    protected DatosPresentacion2Type datosPresentacion;
    @XmlElement(name = "EstadoOperacionesSeguros", required = true)
    protected EstadoFactura2Type estadoOperacionesSeguros;

    /**
     * Obtiene el valor de la propiedad datosOperacionesSeguros.
     * 
     * @return
     *     possible object is
     *     {@link RespuestaOperacionesSegurosType }
     *     
     */
    public RespuestaOperacionesSegurosType getDatosOperacionesSeguros() {
        return datosOperacionesSeguros;
    }

    /**
     * Define el valor de la propiedad datosOperacionesSeguros.
     * 
     * @param value
     *     allowed object is
     *     {@link RespuestaOperacionesSegurosType }
     *     
     */
    public void setDatosOperacionesSeguros(RespuestaOperacionesSegurosType value) {
        this.datosOperacionesSeguros = value;
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
     * Obtiene el valor de la propiedad estadoOperacionesSeguros.
     * 
     * @return
     *     possible object is
     *     {@link EstadoFactura2Type }
     *     
     */
    public EstadoFactura2Type getEstadoOperacionesSeguros() {
        return estadoOperacionesSeguros;
    }

    /**
     * Define el valor de la propiedad estadoOperacionesSeguros.
     * 
     * @param value
     *     allowed object is
     *     {@link EstadoFactura2Type }
     *     
     */
    public void setEstadoOperacionesSeguros(EstadoFactura2Type value) {
        this.estadoOperacionesSeguros = value;
    }

}
