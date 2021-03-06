
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.consultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.ConsultaInformacion;


/**
 * <p>Clase Java para ConsultaPagosType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ConsultaPagosType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}ConsultaInformacion"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="FiltroConsultaPagos" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd}LRFiltroPagosType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConsultaPagosType", propOrder = {
    "filtroConsultaPagos"
})
public class ConsultaPagosType
    extends ConsultaInformacion
{

    @XmlElement(name = "FiltroConsultaPagos", required = true)
    protected LRFiltroPagosType filtroConsultaPagos;

    /**
     * Obtiene el valor de la propiedad filtroConsultaPagos.
     * 
     * @return
     *     possible object is
     *     {@link LRFiltroPagosType }
     *     
     */
    public LRFiltroPagosType getFiltroConsultaPagos() {
        return filtroConsultaPagos;
    }

    /**
     * Define el valor de la propiedad filtroConsultaPagos.
     * 
     * @param value
     *     allowed object is
     *     {@link LRFiltroPagosType }
     *     
     */
    public void setFiltroConsultaPagos(LRFiltroPagosType value) {
        this.filtroConsultaPagos = value;
    }

}
