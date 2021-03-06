
package eus.bizkaia.ogasuna.sii.documentos.consultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.ConsultaInformacion;


/**
 * <p>Clase Java para LRConsultaDetOperIntracomunitariasType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="LRConsultaDetOperIntracomunitariasType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}ConsultaInformacion"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="FiltroConsulta" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/ConsultaLR.xsd}LRFiltroDetOperIntracomunitariasType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LRConsultaDetOperIntracomunitariasType", propOrder = {
    "filtroConsulta"
})
public class LRConsultaDetOperIntracomunitariasType
    extends ConsultaInformacion
{

    @XmlElement(name = "FiltroConsulta", required = true)
    protected LRFiltroDetOperIntracomunitariasType filtroConsulta;

    /**
     * Obtiene el valor de la propiedad filtroConsulta.
     * 
     * @return
     *     possible object is
     *     {@link LRFiltroDetOperIntracomunitariasType }
     *     
     */
    public LRFiltroDetOperIntracomunitariasType getFiltroConsulta() {
        return filtroConsulta;
    }

    /**
     * Define el valor de la propiedad filtroConsulta.
     * 
     * @param value
     *     allowed object is
     *     {@link LRFiltroDetOperIntracomunitariasType }
     *     
     */
    public void setFiltroConsulta(LRFiltroDetOperIntracomunitariasType value) {
        this.filtroConsulta = value;
    }

}
