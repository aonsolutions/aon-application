
package https.sii_araba_eus.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Identificador de persona Física o jurídica distinto del NIF 
 *         								 (Código pais, Tipo de Identificador, y hasta 15 caractéres)
 *         								 No se permite CodigoPais=ES e IDType=01-NIFContraparte
 *         								 para ese caso, debe utilizarse NIF en lugar de IDOtro.
 *         	
 * 
 * <p>Clase Java para IDOtroType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="IDOtroType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CodigoPais" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}CountryType2" minOccurs="0"/&gt;
 *         &lt;element name="IDType" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}PersonaFisicaJuridicaIDTypeType"/&gt;
 *         &lt;element name="ID" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}TextMax20Type"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IDOtroType", propOrder = {
    "codigoPais",
    "idType",
    "id"
})
public class IDOtroType {

    @XmlElement(name = "CodigoPais")
    @XmlSchemaType(name = "string")
    protected CountryType2 codigoPais;
    @XmlElement(name = "IDType", required = true)
    protected String idType;
    @XmlElement(name = "ID", required = true)
    protected String id;

    /**
     * Obtiene el valor de la propiedad codigoPais.
     * 
     * @return
     *     possible object is
     *     {@link CountryType2 }
     *     
     */
    public CountryType2 getCodigoPais() {
        return codigoPais;
    }

    /**
     * Define el valor de la propiedad codigoPais.
     * 
     * @param value
     *     allowed object is
     *     {@link CountryType2 }
     *     
     */
    public void setCodigoPais(CountryType2 value) {
        this.codigoPais = value;
    }

    /**
     * Obtiene el valor de la propiedad idType.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIDType() {
        return idType;
    }

    /**
     * Define el valor de la propiedad idType.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIDType(String value) {
        this.idType = value;
    }

    /**
     * Obtiene el valor de la propiedad id.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getID() {
        return id;
    }

    /**
     * Define el valor de la propiedad id.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setID(String value) {
        this.id = value;
    }

}
