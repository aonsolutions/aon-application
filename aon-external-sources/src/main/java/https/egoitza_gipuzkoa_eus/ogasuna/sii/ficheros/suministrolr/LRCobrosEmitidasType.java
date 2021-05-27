
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.CobrosType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.IDFacturaExpedidaBCType;


/**
 * <p>Clase Java para LRCobrosEmitidasType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="LRCobrosEmitidasType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IDFactura" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}IDFacturaExpedidaBCType"/&gt;
 *         &lt;element name="Cobros" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}CobrosType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LRCobrosEmitidasType", propOrder = {
    "idFactura",
    "cobros"
})
public class LRCobrosEmitidasType {

    @XmlElement(name = "IDFactura", required = true)
    protected IDFacturaExpedidaBCType idFactura;
    @XmlElement(name = "Cobros", required = true)
    protected CobrosType cobros;

    /**
     * Obtiene el valor de la propiedad idFactura.
     * 
     * @return
     *     possible object is
     *     {@link IDFacturaExpedidaBCType }
     *     
     */
    public IDFacturaExpedidaBCType getIDFactura() {
        return idFactura;
    }

    /**
     * Define el valor de la propiedad idFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link IDFacturaExpedidaBCType }
     *     
     */
    public void setIDFactura(IDFacturaExpedidaBCType value) {
        this.idFactura = value;
    }

    /**
     * Obtiene el valor de la propiedad cobros.
     * 
     * @return
     *     possible object is
     *     {@link CobrosType }
     *     
     */
    public CobrosType getCobros() {
        return cobros;
    }

    /**
     * Define el valor de la propiedad cobros.
     * 
     * @param value
     *     allowed object is
     *     {@link CobrosType }
     *     
     */
    public void setCobros(CobrosType value) {
        this.cobros = value;
    }

}
