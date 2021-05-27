
package https.sii_araba_eus.documentos.respuestaconsultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import https.sii_araba_eus.documentos.suministroinformacion.DepositoType;
import https.sii_araba_eus.documentos.suministroinformacion.PersonaFisicaJuridicaType;
import https.sii_araba_eus.documentos.suministroinformacion.TipoClaveDeclaranteType;
import https.sii_araba_eus.documentos.suministroinformacion.VentaBienesConsignaType;


/**
 *  Datos de ventas de bienes en consigna  
 * 
 * <p>Clase Java para RespuestaVentaBienesConsignaType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RespuestaVentaBienesConsignaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ClaveDeclarante" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}TipoClaveDeclaranteType"/&gt;
 *         &lt;element name="TipoOperacion" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}TipoOperacionType"/&gt;
 *         &lt;element name="Contraparte" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}PersonaFisicaJuridicaType" minOccurs="0"/&gt;
 *         &lt;element name="SustitutoDestinatarioInicial" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}PersonaFisicaJuridicaType" minOccurs="0"/&gt;
 *         &lt;element name="Deposito" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}DepositoType" minOccurs="0"/&gt;
 *         &lt;element name="OperacionIntracomunitaria" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}VentaBienesConsignaType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RespuestaVentaBienesConsignaType", propOrder = {
    "claveDeclarante",
    "tipoOperacion",
    "contraparte",
    "sustitutoDestinatarioInicial",
    "deposito",
    "operacionIntracomunitaria"
})
public class RespuestaVentaBienesConsignaType {

    @XmlElement(name = "ClaveDeclarante", required = true)
    @XmlSchemaType(name = "string")
    protected TipoClaveDeclaranteType claveDeclarante;
    @XmlElement(name = "TipoOperacion", required = true)
    protected String tipoOperacion;
    @XmlElement(name = "Contraparte")
    protected PersonaFisicaJuridicaType contraparte;
    @XmlElement(name = "SustitutoDestinatarioInicial")
    protected PersonaFisicaJuridicaType sustitutoDestinatarioInicial;
    @XmlElement(name = "Deposito")
    protected DepositoType deposito;
    @XmlElement(name = "OperacionIntracomunitaria", required = true)
    protected VentaBienesConsignaType operacionIntracomunitaria;

    /**
     * Obtiene el valor de la propiedad claveDeclarante.
     * 
     * @return
     *     possible object is
     *     {@link TipoClaveDeclaranteType }
     *     
     */
    public TipoClaveDeclaranteType getClaveDeclarante() {
        return claveDeclarante;
    }

    /**
     * Define el valor de la propiedad claveDeclarante.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoClaveDeclaranteType }
     *     
     */
    public void setClaveDeclarante(TipoClaveDeclaranteType value) {
        this.claveDeclarante = value;
    }

    /**
     * Obtiene el valor de la propiedad tipoOperacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoOperacion() {
        return tipoOperacion;
    }

    /**
     * Define el valor de la propiedad tipoOperacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoOperacion(String value) {
        this.tipoOperacion = value;
    }

    /**
     * Obtiene el valor de la propiedad contraparte.
     * 
     * @return
     *     possible object is
     *     {@link PersonaFisicaJuridicaType }
     *     
     */
    public PersonaFisicaJuridicaType getContraparte() {
        return contraparte;
    }

    /**
     * Define el valor de la propiedad contraparte.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonaFisicaJuridicaType }
     *     
     */
    public void setContraparte(PersonaFisicaJuridicaType value) {
        this.contraparte = value;
    }

    /**
     * Obtiene el valor de la propiedad sustitutoDestinatarioInicial.
     * 
     * @return
     *     possible object is
     *     {@link PersonaFisicaJuridicaType }
     *     
     */
    public PersonaFisicaJuridicaType getSustitutoDestinatarioInicial() {
        return sustitutoDestinatarioInicial;
    }

    /**
     * Define el valor de la propiedad sustitutoDestinatarioInicial.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonaFisicaJuridicaType }
     *     
     */
    public void setSustitutoDestinatarioInicial(PersonaFisicaJuridicaType value) {
        this.sustitutoDestinatarioInicial = value;
    }

    /**
     * Obtiene el valor de la propiedad deposito.
     * 
     * @return
     *     possible object is
     *     {@link DepositoType }
     *     
     */
    public DepositoType getDeposito() {
        return deposito;
    }

    /**
     * Define el valor de la propiedad deposito.
     * 
     * @param value
     *     allowed object is
     *     {@link DepositoType }
     *     
     */
    public void setDeposito(DepositoType value) {
        this.deposito = value;
    }

    /**
     * Obtiene el valor de la propiedad operacionIntracomunitaria.
     * 
     * @return
     *     possible object is
     *     {@link VentaBienesConsignaType }
     *     
     */
    public VentaBienesConsignaType getOperacionIntracomunitaria() {
        return operacionIntracomunitaria;
    }

    /**
     * Define el valor de la propiedad operacionIntracomunitaria.
     * 
     * @param value
     *     allowed object is
     *     {@link VentaBienesConsignaType }
     *     
     */
    public void setOperacionIntracomunitaria(VentaBienesConsignaType value) {
        this.operacionIntracomunitaria = value;
    }

}
