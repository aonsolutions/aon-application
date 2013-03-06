
package com.esferalia.aon.sepe.api.SWConsultaDatos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id_envio" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="usuarioConectado" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="usuarioPrincipal" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="idioma" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="comunidad" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "idEnvio",
    "usuarioConectado",
    "usuarioPrincipal",
    "password",
    "idioma",
    "comunidad"
})
@XmlRootElement(name = "servicioConsulta")
public class ServicioConsulta {

    @XmlElement(name = "id_envio", required = true, nillable = true)
    protected String idEnvio;
    @XmlElement(required = true, nillable = true)
    protected String usuarioConectado;
    @XmlElement(required = true, nillable = true)
    protected String usuarioPrincipal;
    @XmlElement(required = true, nillable = true)
    protected String password;
    @XmlElement(required = true, nillable = true)
    protected String idioma;
    @XmlElement(required = true, nillable = true)
    protected String comunidad;

    /**
     * Obtiene el valor de la propiedad idEnvio.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdEnvio() {
        return idEnvio;
    }

    /**
     * Define el valor de la propiedad idEnvio.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdEnvio(String value) {
        this.idEnvio = value;
    }

    /**
     * Obtiene el valor de la propiedad usuarioConectado.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsuarioConectado() {
        return usuarioConectado;
    }

    /**
     * Define el valor de la propiedad usuarioConectado.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsuarioConectado(String value) {
        this.usuarioConectado = value;
    }

    /**
     * Obtiene el valor de la propiedad usuarioPrincipal.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsuarioPrincipal() {
        return usuarioPrincipal;
    }

    /**
     * Define el valor de la propiedad usuarioPrincipal.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsuarioPrincipal(String value) {
        this.usuarioPrincipal = value;
    }

    /**
     * Obtiene el valor de la propiedad password.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Define el valor de la propiedad password.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Obtiene el valor de la propiedad idioma.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdioma() {
        return idioma;
    }

    /**
     * Define el valor de la propiedad idioma.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdioma(String value) {
        this.idioma = value;
    }

    /**
     * Obtiene el valor de la propiedad comunidad.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComunidad() {
        return comunidad;
    }

    /**
     * Define el valor de la propiedad comunidad.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComunidad(String value) {
        this.comunidad = value;
    }

}
