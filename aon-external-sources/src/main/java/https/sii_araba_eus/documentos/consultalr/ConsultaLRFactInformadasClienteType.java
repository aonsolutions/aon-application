
package https.sii_araba_eus.documentos.consultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.sii_araba_eus.documentos.suministroinformacion.ConsultaInformacionCliente;


/**
 * <p>Clase Java para ConsultaLRFactInformadasClienteType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ConsultaLRFactInformadasClienteType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}ConsultaInformacionCliente"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="FiltroConsulta" type="{https://sii.araba.eus/documentos/ConsultaLR.xsd}LRFiltroFactInformadasClienteType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConsultaLRFactInformadasClienteType", propOrder = {
    "filtroConsulta"
})
public class ConsultaLRFactInformadasClienteType
    extends ConsultaInformacionCliente
{

    @XmlElement(name = "FiltroConsulta", required = true)
    protected LRFiltroFactInformadasClienteType filtroConsulta;

    /**
     * Obtiene el valor de la propiedad filtroConsulta.
     * 
     * @return
     *     possible object is
     *     {@link LRFiltroFactInformadasClienteType }
     *     
     */
    public LRFiltroFactInformadasClienteType getFiltroConsulta() {
        return filtroConsulta;
    }

    /**
     * Define el valor de la propiedad filtroConsulta.
     * 
     * @param value
     *     allowed object is
     *     {@link LRFiltroFactInformadasClienteType }
     *     
     */
    public void setFiltroConsulta(LRFiltroFactInformadasClienteType value) {
        this.filtroConsulta = value;
    }

}
