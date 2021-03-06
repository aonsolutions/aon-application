
package eus.bizkaia.ogasuna.sii.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 *  Datos de contexto de un suministro sin especificar el tipo de comunicacion 
 * 
 * <p>Clase Java para CabeceraConsultaSiiProveedor complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="CabeceraConsultaSiiProveedor"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IDVersionSii" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}VersionSiiType"/&gt;
 *         &lt;element name="TitularLRFR" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}PersonaFisicaJuridicaUnicaESType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CabeceraConsultaSiiProveedor", propOrder = {
    "idVersionSii",
    "titularLRFR"
})
public class CabeceraConsultaSiiProveedor {

    @XmlElement(name = "IDVersionSii", required = true)
    protected String idVersionSii;
    @XmlElement(name = "TitularLRFR", required = true)
    protected PersonaFisicaJuridicaUnicaESType titularLRFR;

    /**
     * Obtiene el valor de la propiedad idVersionSii.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIDVersionSii() {
        return idVersionSii;
    }

    /**
     * Define el valor de la propiedad idVersionSii.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIDVersionSii(String value) {
        this.idVersionSii = value;
    }

    /**
     * Obtiene el valor de la propiedad titularLRFR.
     * 
     * @return
     *     possible object is
     *     {@link PersonaFisicaJuridicaUnicaESType }
     *     
     */
    public PersonaFisicaJuridicaUnicaESType getTitularLRFR() {
        return titularLRFR;
    }

    /**
     * Define el valor de la propiedad titularLRFR.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonaFisicaJuridicaUnicaESType }
     *     
     */
    public void setTitularLRFR(PersonaFisicaJuridicaUnicaESType value) {
        this.titularLRFR = value;
    }

}
