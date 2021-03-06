
package https.sii_araba_eus.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import https.sii_araba_eus.documentos.consultalr.ConsultaLRFactInformadasAgrupadasProveedorType;
import https.sii_araba_eus.documentos.consultalr.ConsultaLRFactInformadasProveedorType;
import https.sii_araba_eus.documentos.respuestaconsultalr.RespuestaConsultaLRFacturasAgrupadasProveedorType;
import https.sii_araba_eus.documentos.respuestaconsultalr.RespuestaConsultaLRFacturasProveedorType;


/**
 *  Sii - Suministro Inmediato de Información, compuesto por datos de contexto y una secuencia de 1 o más registros. 
 * 
 * <p>Clase Java para ConsultaInformacionProveedor complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ConsultaInformacionProveedor"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Cabecera" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}CabeceraConsultaSiiProveedor"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConsultaInformacionProveedor", propOrder = {
    "cabecera"
})
@XmlSeeAlso({
    ConsultaLRFactInformadasProveedorType.class,
    ConsultaLRFactInformadasAgrupadasProveedorType.class,
    RespuestaConsultaLRFacturasProveedorType.class,
    RespuestaConsultaLRFacturasAgrupadasProveedorType.class
})
public class ConsultaInformacionProveedor {

    @XmlElement(name = "Cabecera", required = true)
    protected CabeceraConsultaSiiProveedor cabecera;

    /**
     * Obtiene el valor de la propiedad cabecera.
     * 
     * @return
     *     possible object is
     *     {@link CabeceraConsultaSiiProveedor }
     *     
     */
    public CabeceraConsultaSiiProveedor getCabecera() {
        return cabecera;
    }

    /**
     * Define el valor de la propiedad cabecera.
     * 
     * @param value
     *     allowed object is
     *     {@link CabeceraConsultaSiiProveedor }
     *     
     */
    public void setCabecera(CabeceraConsultaSiiProveedor value) {
        this.cabecera = value;
    }

}
