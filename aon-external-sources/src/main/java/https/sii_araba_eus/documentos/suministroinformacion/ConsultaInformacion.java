
package https.sii_araba_eus.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import https.sii_araba_eus.documentos.consultalr.ConsultaCobrosType;
import https.sii_araba_eus.documentos.consultalr.ConsultaInmueblesAdicionalesType;
import https.sii_araba_eus.documentos.consultalr.ConsultaPagosType;
import https.sii_araba_eus.documentos.consultalr.LRConsultaAgenciasViajesType;
import https.sii_araba_eus.documentos.consultalr.LRConsultaBienesInversionType;
import https.sii_araba_eus.documentos.consultalr.LRConsultaCobrosMetalicoType;
import https.sii_araba_eus.documentos.consultalr.LRConsultaDetOperIntracomunitariasType;
import https.sii_araba_eus.documentos.consultalr.LRConsultaEmitidasType;
import https.sii_araba_eus.documentos.consultalr.LRConsultaLROperacionesSegurosType;
import https.sii_araba_eus.documentos.consultalr.LRConsultaRecibidasType;
import https.sii_araba_eus.documentos.consultalr.LRConsultaVentaBienesConsignaType;
import https.sii_araba_eus.documentos.respuestaconsultalr.RespuestaConsultaFacturaCobrosType;
import https.sii_araba_eus.documentos.respuestaconsultalr.RespuestaConsultaFacturaPagosType;
import https.sii_araba_eus.documentos.respuestaconsultalr.RespuestaConsultaInmueblesType;
import https.sii_araba_eus.documentos.respuestaconsultalr.RespuestaConsultaLRFacturasType;
import https.sii_araba_eus.documentos.respuestaconsultalr.RespuestaConsultaLRVentaBVType;


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
 *         &lt;element name="Cabecera" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}CabeceraConsultaSii"/&gt;
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
    LRConsultaVentaBienesConsignaType.class,
    RespuestaConsultaLRFacturasType.class,
    RespuestaConsultaLRVentaBVType.class,
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
