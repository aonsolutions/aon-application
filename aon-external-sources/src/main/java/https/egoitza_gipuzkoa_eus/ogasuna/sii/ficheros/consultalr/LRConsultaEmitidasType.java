
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.consultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.ConsultaInformacion;


/**
 * <p>Clase Java para LRConsultaEmitidasType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="LRConsultaEmitidasType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}ConsultaInformacion"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="FiltroConsulta" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd}LRFiltroEmitidasType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LRConsultaEmitidasType", propOrder = {
    "filtroConsulta"
})
public class LRConsultaEmitidasType
    extends ConsultaInformacion
{

    @XmlElement(name = "FiltroConsulta", required = true)
    protected LRFiltroEmitidasType filtroConsulta;

    /**
     * Obtiene el valor de la propiedad filtroConsulta.
     * 
     * @return
     *     possible object is
     *     {@link LRFiltroEmitidasType }
     *     
     */
    public LRFiltroEmitidasType getFiltroConsulta() {
        return filtroConsulta;
    }

    /**
     * Define el valor de la propiedad filtroConsulta.
     * 
     * @param value
     *     allowed object is
     *     {@link LRFiltroEmitidasType }
     *     
     */
    public void setFiltroConsulta(LRFiltroEmitidasType value) {
        this.filtroConsulta = value;
    }

}
