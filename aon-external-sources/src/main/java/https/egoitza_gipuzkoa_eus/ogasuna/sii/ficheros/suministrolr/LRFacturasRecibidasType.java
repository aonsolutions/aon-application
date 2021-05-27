
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.FacturaRecibidaType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.IDFacturaRecibidaType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.RegistroSii;


/**
 * Datos correspondientes al libro de Facturas recibidas
 * 
 * <p>Clase Java para LRFacturasRecibidasType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="LRFacturasRecibidasType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}RegistroSii"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IDFactura" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}IDFacturaRecibidaType"/&gt;
 *         &lt;element name="FacturaRecibida" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}FacturaRecibidaType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LRFacturasRecibidasType", propOrder = {
    "idFactura",
    "facturaRecibida"
})
public class LRFacturasRecibidasType
    extends RegistroSii
{

    @XmlElement(name = "IDFactura", required = true)
    protected IDFacturaRecibidaType idFactura;
    @XmlElement(name = "FacturaRecibida", required = true)
    protected FacturaRecibidaType facturaRecibida;

    /**
     * Obtiene el valor de la propiedad idFactura.
     * 
     * @return
     *     possible object is
     *     {@link IDFacturaRecibidaType }
     *     
     */
    public IDFacturaRecibidaType getIDFactura() {
        return idFactura;
    }

    /**
     * Define el valor de la propiedad idFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link IDFacturaRecibidaType }
     *     
     */
    public void setIDFactura(IDFacturaRecibidaType value) {
        this.idFactura = value;
    }

    /**
     * Obtiene el valor de la propiedad facturaRecibida.
     * 
     * @return
     *     possible object is
     *     {@link FacturaRecibidaType }
     *     
     */
    public FacturaRecibidaType getFacturaRecibida() {
        return facturaRecibida;
    }

    /**
     * Define el valor de la propiedad facturaRecibida.
     * 
     * @param value
     *     allowed object is
     *     {@link FacturaRecibidaType }
     *     
     */
    public void setFacturaRecibida(FacturaRecibidaType value) {
        this.facturaRecibida = value;
    }

}
