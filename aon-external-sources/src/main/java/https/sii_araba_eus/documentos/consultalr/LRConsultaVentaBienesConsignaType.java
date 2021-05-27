
package https.sii_araba_eus.documentos.consultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.sii_araba_eus.documentos.suministroinformacion.ConsultaInformacion;


/**
 * <p>Clase Java para LRConsultaVentaBienesConsignaType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="LRConsultaVentaBienesConsignaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}ConsultaInformacion"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="FiltroConsulta" type="{https://sii.araba.eus/documentos/ConsultaLR.xsd}LRFiltroVentaBienesConsignaType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LRConsultaVentaBienesConsignaType", propOrder = {
    "filtroConsulta"
})
public class LRConsultaVentaBienesConsignaType
    extends ConsultaInformacion
{

    @XmlElement(name = "FiltroConsulta", required = true)
    protected LRFiltroVentaBienesConsignaType filtroConsulta;

    /**
     * Obtiene el valor de la propiedad filtroConsulta.
     * 
     * @return
     *     possible object is
     *     {@link LRFiltroVentaBienesConsignaType }
     *     
     */
    public LRFiltroVentaBienesConsignaType getFiltroConsulta() {
        return filtroConsulta;
    }

    /**
     * Define el valor de la propiedad filtroConsulta.
     * 
     * @param value
     *     allowed object is
     *     {@link LRFiltroVentaBienesConsignaType }
     *     
     */
    public void setFiltroConsulta(LRFiltroVentaBienesConsignaType value) {
        this.filtroConsulta = value;
    }

}
