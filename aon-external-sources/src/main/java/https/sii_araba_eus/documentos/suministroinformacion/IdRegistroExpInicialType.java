
package https.sii_araba_eus.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para IdRegistroExpInicialType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="IdRegistroExpInicialType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Ejercicio" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}YearType"/&gt;
 *         &lt;element name="Periodo" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}TipoPeriodoType"/&gt;
 *         &lt;element name="IdExpInicial" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}TextMax60Type"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdRegistroExpInicialType", propOrder = {
    "ejercicio",
    "periodo",
    "idExpInicial"
})
public class IdRegistroExpInicialType {

    @XmlElement(name = "Ejercicio", required = true)
    protected String ejercicio;
    @XmlElement(name = "Periodo", required = true)
    protected String periodo;
    @XmlElement(name = "IdExpInicial", required = true)
    protected String idExpInicial;

    /**
     * Obtiene el valor de la propiedad ejercicio.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEjercicio() {
        return ejercicio;
    }

    /**
     * Define el valor de la propiedad ejercicio.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEjercicio(String value) {
        this.ejercicio = value;
    }

    /**
     * Obtiene el valor de la propiedad periodo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * Define el valor de la propiedad periodo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPeriodo(String value) {
        this.periodo = value;
    }

    /**
     * Obtiene el valor de la propiedad idExpInicial.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdExpInicial() {
        return idExpInicial;
    }

    /**
     * Define el valor de la propiedad idExpInicial.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdExpInicial(String value) {
        this.idExpInicial = value;
    }

}
