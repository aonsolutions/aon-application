
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.consultalr.ConsultaCobrosType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.consultalr.ConsultaInmueblesAdicionalesType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.consultalr.ConsultaPagosType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.consultalr.LRConsultaAgenciasViajesType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.consultalr.LRConsultaBienesInversionType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.consultalr.LRConsultaCobrosMetalicoType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.consultalr.LRConsultaDetOperIntracomunitariasType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.consultalr.LRConsultaEmitidasType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.consultalr.LRConsultaLROperacionesSegurosType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.consultalr.LRConsultaRecibidasType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestaconsultalr.RespuestaConsultaFacturaCobrosType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestaconsultalr.RespuestaConsultaFacturaPagosType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestaconsultalr.RespuestaConsultaInmueblesType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestaconsultalr.RespuestaConsultaLRFacturasType;


/**
 *  Sii - Suministro Inmediato de Información, compuesto por datos 
 * 			                              de contexto y una secuencia de 1 o más registros. 
 * 
 * <p>Clase Java para ConsultaInformacion complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ConsultaInformacion"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Cabecera" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}CabeceraConsultaSii"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConsultaInformacion", propOrder = {
    "cabecera"
})
@XmlSeeAlso({
    LRConsultaRecibidasType.class,
    LRConsultaEmitidasType.class,
    LRConsultaBienesInversionType.class,
    LRConsultaDetOperIntracomunitariasType.class,
    LRConsultaCobrosMetalicoType.class,
    LRConsultaAgenciasViajesType.class,
    ConsultaCobrosType.class,
    ConsultaInmueblesAdicionalesType.class,
    ConsultaPagosType.class,
    LRConsultaLROperacionesSegurosType.class,
    RespuestaConsultaLRFacturasType.class,
    RespuestaConsultaFacturaCobrosType.class,
    RespuestaConsultaInmueblesType.class,
    RespuestaConsultaFacturaPagosType.class
})
public class ConsultaInformacion {

    @XmlElement(name = "Cabecera", required = true)
    protected CabeceraConsultaSii cabecera;

    /**
     * Obtiene el valor de la propiedad cabecera.
     * 
     * @return
     *     possible object is
     *     {@link CabeceraConsultaSii }
     *     
     */
    public CabeceraConsultaSii getCabecera() {
        return cabecera;
    }

    /**
     * Define el valor de la propiedad cabecera.
     * 
     * @param value
     *     allowed object is
     *     {@link CabeceraConsultaSii }
     *     
     */
    public void setCabecera(CabeceraConsultaSii value) {
        this.cabecera = value;
    }

}
