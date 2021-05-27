
package eus.bizkaia.ogasuna.sii.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para DetalleIVAEmitidaPrestacionType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DetalleIVAEmitidaPrestacionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TipoImpositivo" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}Tipo2.2Type" minOccurs="0"/&gt;
 *         &lt;element name="BaseImponible" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}ImporteSgn12.2Type"/&gt;
 *         &lt;element name="CuotaRepercutida" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}ImporteSgn12.2Type" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DetalleIVAEmitidaPrestacionType", propOrder = {
    "tipoImpositivo",
    "baseImponible",
    "cuotaRepercutida"
})
public class DetalleIVAEmitidaPrestacionType {

    @XmlElement(name = "TipoImpositivo")
    protected String tipoImpositivo;
    @XmlElement(name = "BaseImponible", required = true)
    protected String baseImponible;
    @XmlElement(name = "CuotaRepercutida")
    protected String cuotaRepercutida;

    /**
     * Obtiene el valor de la propiedad tipoImpositivo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoImpositivo() {
        return tipoImpositivo;
    }

    /**
     * Define el valor de la propiedad tipoImpositivo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoImpositivo(String value) {
        this.tipoImpositivo = value;
    }

    /**
     * Obtiene el valor de la propiedad baseImponible.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseImponible() {
        return baseImponible;
    }

    /**
     * Define el valor de la propiedad baseImponible.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseImponible(String value) {
        this.baseImponible = value;
    }

    /**
     * Obtiene el valor de la propiedad cuotaRepercutida.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCuotaRepercutida() {
        return cuotaRepercutida;
    }

    /**
     * Define el valor de la propiedad cuotaRepercutida.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCuotaRepercutida(String value) {
        this.cuotaRepercutida = value;
    }

}
