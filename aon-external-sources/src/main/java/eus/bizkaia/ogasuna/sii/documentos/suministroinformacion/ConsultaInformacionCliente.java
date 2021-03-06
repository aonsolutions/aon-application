
package eus.bizkaia.ogasuna.sii.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import eus.bizkaia.ogasuna.sii.documentos.consultalr.ConsultaLRFactInformadasAgrupadasClienteType;
import eus.bizkaia.ogasuna.sii.documentos.consultalr.ConsultaLRFactInformadasClienteType;
import eus.bizkaia.ogasuna.sii.documentos.respuestaconsultalr.RespuestaConsultaLRFacturasAgrupadasClienteType;
import eus.bizkaia.ogasuna.sii.documentos.respuestaconsultalr.RespuestaConsultaLRFacturasClienteType;


/**
 *  Sii - Suministro Inmediato de Información, compuesto por datos de contexto y una secuencia de 1 o más registros. 
 * 
 * <p>Clase Java para ConsultaInformacionCliente complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ConsultaInformacionCliente"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Cabecera" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}CabeceraConsultaSiiCliente"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConsultaInformacionCliente", propOrder = {
    "cabecera"
})
@XmlSeeAlso({
    ConsultaLRFactInformadasClienteType.class,
    ConsultaLRFactInformadasAgrupadasClienteType.class,
    RespuestaConsultaLRFacturasClienteType.class,
    RespuestaConsultaLRFacturasAgrupadasClienteType.class
})
public class ConsultaInformacionCliente {

    @XmlElement(name = "Cabecera", required = true)
    protected CabeceraConsultaSiiCliente cabecera;

    /**
     * Obtiene el valor de la propiedad cabecera.
     * 
     * @return
     *     possible object is
     *     {@link CabeceraConsultaSiiCliente }
     *     
     */
    public CabeceraConsultaSiiCliente getCabecera() {
        return cabecera;
    }

    /**
     * Define el valor de la propiedad cabecera.
     * 
     * @param value
     *     allowed object is
     *     {@link CabeceraConsultaSiiCliente }
     *     
     */
    public void setCabecera(CabeceraConsultaSiiCliente value) {
        this.cabecera = value;
    }

}
