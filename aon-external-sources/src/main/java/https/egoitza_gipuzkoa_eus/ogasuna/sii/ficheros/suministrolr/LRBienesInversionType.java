
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.BienDeInversionType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.IDFacturaComunitariaType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.RegistroSii;


/**
 * Datos correspondientes al libro de Bienes de inversión
 * 
 * <p>Clase Java para LRBienesInversionType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="LRBienesInversionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}RegistroSii"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IDFactura" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}IDFacturaComunitariaType"/&gt;
 *         &lt;element name="BienesInversion" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}BienDeInversionType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LRBienesInversionType", propOrder = {
    "idFactura",
    "bienesInversion"
})
public class LRBienesInversionType
    extends RegistroSii
{

    @XmlElement(name = "IDFactura", required = true)
    protected IDFacturaComunitariaType idFactura;
    @XmlElement(name = "BienesInversion", required = true)
    protected BienDeInversionType bienesInversion;

    /**
     * Obtiene el valor de la propiedad idFactura.
     * 
     * @return
     *     possible object is
     *     {@link IDFacturaComunitariaType }
     *     
     */
    public IDFacturaComunitariaType getIDFactura() {
        return idFactura;
    }

    /**
     * Define el valor de la propiedad idFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link IDFacturaComunitariaType }
     *     
     */
    public void setIDFactura(IDFacturaComunitariaType value) {
        this.idFactura = value;
    }

    /**
     * Obtiene el valor de la propiedad bienesInversion.
     * 
     * @return
     *     possible object is
     *     {@link BienDeInversionType }
     *     
     */
    public BienDeInversionType getBienesInversion() {
        return bienesInversion;
    }

    /**
     * Define el valor de la propiedad bienesInversion.
     * 
     * @param value
     *     allowed object is
     *     {@link BienDeInversionType }
     *     
     */
    public void setBienesInversion(BienDeInversionType value) {
        this.bienesInversion = value;
    }

}
