
package eus.bizkaia.ogasuna.sii.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import eus.bizkaia.ogasuna.sii.documentos.consultalr.ConsultaCobrosType;
import eus.bizkaia.ogasuna.sii.documentos.consultalr.ConsultaInmueblesAdicionalesType;
import eus.bizkaia.ogasuna.sii.documentos.consultalr.ConsultaPagosType;
import eus.bizkaia.ogasuna.sii.documentos.consultalr.LRConsultaAgenciasViajesType;
import eus.bizkaia.ogasuna.sii.documentos.consultalr.LRConsultaBienesInversionType;
import eus.bizkaia.ogasuna.sii.documentos.consultalr.LRConsultaCobrosMetalicoType;
import eus.bizkaia.ogasuna.sii.documentos.consultalr.LRConsultaDetOperIntracomunitariasType;
import eus.bizkaia.ogasuna.sii.documentos.consultalr.LRConsultaEmitidasType;
import eus.bizkaia.ogasuna.sii.documentos.consultalr.LRConsultaLROperacionesSegurosType;
import eus.bizkaia.ogasuna.sii.documentos.consultalr.LRConsultaRecibidasType;
import eus.bizkaia.ogasuna.sii.documentos.respuestaconsultalr.RespuestaConsultaFacturaCobrosType;
import eus.bizkaia.ogasuna.sii.documentos.respuestaconsultalr.RespuestaConsultaFacturaPagosType;
import eus.bizkaia.ogasuna.sii.documentos.respuestaconsultalr.RespuestaConsultaInmueblesType;
import eus.bizkaia.ogasuna.sii.documentos.respuestaconsultalr.RespuestaConsultaLRFacturasType;


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
 *         &lt;element name="Cabecera" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}CabeceraConsultaSii"/&gt;
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
