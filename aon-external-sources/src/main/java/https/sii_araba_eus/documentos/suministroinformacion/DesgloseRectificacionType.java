
package https.sii_araba_eus.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Desglose de Base y Cuota sustituida en las Facturas Rectificativas sustitutivas
 * 
 * <p>Clase Java para DesgloseRectificacionType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DesgloseRectificacionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="BaseRectificada" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}ImporteSgn12.2Type"/&gt;
 *         &lt;element name="CuotaRectificada" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}ImporteSgn12.2Type"/&gt;
 *         &lt;element name="CuotaRecargoRectificado" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}ImporteSgn12.2Type" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DesgloseRectificacionType", propOrder = {
    "baseRectificada",
    "cuotaRectificada",
    "cuotaRecargoRectificado"
})
public class DesgloseRectificacionType {

    @XmlElement(name = "BaseRectificada", required = true)
    protected String baseRectificada;
    @XmlElement(name = "CuotaRectificada", required = true)
    protected String cuotaRectificada;
    @XmlElement(name = "CuotaRecargoRectificado")
    protected String cuotaRecargoRectificado;

    /**
     * Obtiene el valor de la propiedad baseRectificada.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseRectificada() {
        return baseRectificada;
    }

    /**
     * Define el valor de la propiedad baseRectificada.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseRectificada(String value) {
        this.baseRectificada = value;
    }

    /**
     * Obtiene el valor de la propiedad cuotaRectificada.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCuotaRectificada() {
        return cuotaRectificada;
    }

    /**
     * Define el valor de la propiedad cuotaRectificada.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCuotaRectificada(String value) {
        this.cuotaRectificada = value;
    }

    /**
     * Obtiene el valor de la propiedad cuotaRecargoRectificado.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCuotaRecargoRectificado() {
        return cuotaRecargoRectificado;
    }

    /**
     * Define el valor de la propiedad cuotaRecargoRectificado.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCuotaRecargoRectificado(String value) {
        this.cuotaRecargoRectificado = value;
    }

}
