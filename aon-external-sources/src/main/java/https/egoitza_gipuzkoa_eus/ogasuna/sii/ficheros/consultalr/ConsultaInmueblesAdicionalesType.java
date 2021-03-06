
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.consultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.ConsultaInformacion;


/**
 * <p>Clase Java para ConsultaInmueblesAdicionalesType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ConsultaInmueblesAdicionalesType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}ConsultaInformacion"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="FiltroConsultaInmueblesAdicionales" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/ConsultaLR.xsd}LRFiltroInmueblesAdicionalesType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConsultaInmueblesAdicionalesType", propOrder = {
    "filtroConsultaInmueblesAdicionales"
})
public class ConsultaInmueblesAdicionalesType
    extends ConsultaInformacion
{

    @XmlElement(name = "FiltroConsultaInmueblesAdicionales", required = true)
    protected LRFiltroInmueblesAdicionalesType filtroConsultaInmueblesAdicionales;

    /**
     * Obtiene el valor de la propiedad filtroConsultaInmueblesAdicionales.
     * 
     * @return
     *     possible object is
     *     {@link LRFiltroInmueblesAdicionalesType }
     *     
     */
    public LRFiltroInmueblesAdicionalesType getFiltroConsultaInmueblesAdicionales() {
        return filtroConsultaInmueblesAdicionales;
    }

    /**
     * Define el valor de la propiedad filtroConsultaInmueblesAdicionales.
     * 
     * @param value
     *     allowed object is
     *     {@link LRFiltroInmueblesAdicionalesType }
     *     
     */
    public void setFiltroConsultaInmueblesAdicionales(LRFiltroInmueblesAdicionalesType value) {
        this.filtroConsultaInmueblesAdicionales = value;
    }

}
