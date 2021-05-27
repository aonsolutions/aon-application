
package eus.bizkaia.ogasuna.sii.documentos.suministrolr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.IDFacturaRecibidaNombreBCType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.RegistroSii;


/**
 * Datos correspondientes a la baja de Facturas recibidas 
 * 
 * <p>Clase Java para LRBajaRecibidasType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="LRBajaRecibidasType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}RegistroSii"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IDFactura" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}IDFacturaRecibidaNombreBCType"/&gt;
 *         &lt;element name="RefExterna" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}TextMax60Type" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LRBajaRecibidasType", propOrder = {
    "idFactura",
    "refExterna"
})
public class LRBajaRecibidasType
    extends RegistroSii
{

    @XmlElement(name = "IDFactura", required = true)
    protected IDFacturaRecibidaNombreBCType idFactura;
    @XmlElement(name = "RefExterna")
    protected String refExterna;

    /**
     * Obtiene el valor de la propiedad idFactura.
     * 
     * @return
     *     possible object is
     *     {@link IDFacturaRecibidaNombreBCType }
     *     
     */
    public IDFacturaRecibidaNombreBCType getIDFactura() {
        return idFactura;
    }

    /**
     * Define el valor de la propiedad idFactura.
     * 
     * @param value
     *     allowed object is
     *     {@link IDFacturaRecibidaNombreBCType }
     *     
     */
    public void setIDFactura(IDFacturaRecibidaNombreBCType value) {
        this.idFactura = value;
    }

    /**
     * Obtiene el valor de la propiedad refExterna.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefExterna() {
        return refExterna;
    }

    /**
     * Define el valor de la propiedad refExterna.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefExterna(String value) {
        this.refExterna = value;
    }

}
