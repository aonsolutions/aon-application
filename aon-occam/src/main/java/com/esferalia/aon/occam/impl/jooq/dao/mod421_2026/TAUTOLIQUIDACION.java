//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.1-b171012.0423 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2026.02.26 a las 01:00:22 PM CET 
//


package com.esferalia.aon.occam.impl.jooq.dao.mod421_2026;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Datos de las casillas y totales de la autoliquidación.
 * 				Tendrán caracter obligatorio internamente las casillas
 * 				06, 07, 08, 09, 10, 11, 19 (campos calculados), salvo
 * 				en el tipo de resultado "Sin Actividad", donde no se 
 * 				incluirá el atributo.
 * 			
 * 
 * <p>Clase Java para T_AUTOLIQUIDACION complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="T_AUTOLIQUIDACION"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="EPIGRAFES"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="EPIGRAFE" maxOccurs="unbounded" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;attribute name="EPI" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                           &lt;attribute name="SEC" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                           &lt;attribute name="MOD1" type="{}IMPA8ABSType" /&gt;
 *                           &lt;attribute name="MOD2" type="{}IMPA8ABSType" /&gt;
 *                           &lt;attribute name="MOD3" type="{}IMPA8ABSType" /&gt;
 *                           &lt;attribute name="MOD4" type="{}IMPA8ABSType" /&gt;
 *                           &lt;attribute name="MOD5" type="{}IMPA8ABSType" /&gt;
 *                           &lt;attribute name="MOD6" type="{}IMPA8ABSType" /&gt;
 *                           &lt;attribute name="MOD7" type="{}IMPA8ABSType" /&gt;
 *                           &lt;attribute name="TOT" use="required" type="{}IMPA15Type" /&gt;
 *                           &lt;attribute name="DIAS_EJE_ANT" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                           &lt;attribute name="DIAS_TRI_CUR" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                           &lt;attribute name="IND_COR" type="{}IMPA5Type" /&gt;
 *                           &lt;attribute name="CUO_RES_TRIM" type="{}IMPA15Type" /&gt;
 *                           &lt;attribute name="CUO_SOP" type="{}IMPA15Type" /&gt;
 *                           &lt;attribute name="DIAS_EJE_CUR" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                           &lt;attribute name="CUO_RES_ULT_TRIM" type="{}IMPA15Type" /&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="RED" type="{}IMPA5Type" /&gt;
 *       &lt;attribute name="C06" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="C07" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="C08" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="C09" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="C10" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="C11" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="C12" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="C13" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="C14" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="C15" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="C16" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="C17" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="C18" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="C19" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="TRIM_1" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="TRIM_2" type="{}IMPA15Type" /&gt;
 *       &lt;attribute name="TRIM_3" type="{}IMPA15Type" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "T_AUTOLIQUIDACION", propOrder = {
    "epigrafes"
})
public class TAUTOLIQUIDACION {

    @XmlElement(name = "EPIGRAFES", required = true)
    protected TAUTOLIQUIDACION.EPIGRAFES epigrafes;
    @XmlAttribute(name = "RED")
    protected String red;
    @XmlAttribute(name = "C06")
    protected String c06;
    @XmlAttribute(name = "C07")
    protected String c07;
    @XmlAttribute(name = "C08")
    protected String c08;
    @XmlAttribute(name = "C09")
    protected String c09;
    @XmlAttribute(name = "C10")
    protected String c10;
    @XmlAttribute(name = "C11")
    protected String c11;
    @XmlAttribute(name = "C12")
    protected String c12;
    @XmlAttribute(name = "C13")
    protected String c13;
    @XmlAttribute(name = "C14")
    protected String c14;
    @XmlAttribute(name = "C15")
    protected String c15;
    @XmlAttribute(name = "C16")
    protected String c16;
    @XmlAttribute(name = "C17")
    protected String c17;
    @XmlAttribute(name = "C18")
    protected String c18;
    @XmlAttribute(name = "C19")
    protected String c19;
    @XmlAttribute(name = "TRIM_1")
    protected String trim1;
    @XmlAttribute(name = "TRIM_2")
    protected String trim2;
    @XmlAttribute(name = "TRIM_3")
    protected String trim3;

    /**
     * Obtiene el valor de la propiedad epigrafes.
     * 
     * @return
     *     possible object is
     *     {@link TAUTOLIQUIDACION.EPIGRAFES }
     *     
     */
    public TAUTOLIQUIDACION.EPIGRAFES getEPIGRAFES() {
        return epigrafes;
    }

    /**
     * Define el valor de la propiedad epigrafes.
     * 
     * @param value
     *     allowed object is
     *     {@link TAUTOLIQUIDACION.EPIGRAFES }
     *     
     */
    public void setEPIGRAFES(TAUTOLIQUIDACION.EPIGRAFES value) {
        this.epigrafes = value;
    }

    /**
     * Obtiene el valor de la propiedad red.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRED() {
        return red;
    }

    /**
     * Define el valor de la propiedad red.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRED(String value) {
        this.red = value;
    }

    /**
     * Obtiene el valor de la propiedad c06.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getC06() {
        return c06;
    }

    /**
     * Define el valor de la propiedad c06.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setC06(String value) {
        this.c06 = value;
    }

    /**
     * Obtiene el valor de la propiedad c07.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getC07() {
        return c07;
    }

    /**
     * Define el valor de la propiedad c07.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setC07(String value) {
        this.c07 = value;
    }

    /**
     * Obtiene el valor de la propiedad c08.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getC08() {
        return c08;
    }

    /**
     * Define el valor de la propiedad c08.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setC08(String value) {
        this.c08 = value;
    }

    /**
     * Obtiene el valor de la propiedad c09.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getC09() {
        return c09;
    }

    /**
     * Define el valor de la propiedad c09.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setC09(String value) {
        this.c09 = value;
    }

    /**
     * Obtiene el valor de la propiedad c10.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getC10() {
        return c10;
    }

    /**
     * Define el valor de la propiedad c10.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setC10(String value) {
        this.c10 = value;
    }

    /**
     * Obtiene el valor de la propiedad c11.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getC11() {
        return c11;
    }

    /**
     * Define el valor de la propiedad c11.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setC11(String value) {
        this.c11 = value;
    }

    /**
     * Obtiene el valor de la propiedad c12.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getC12() {
        return c12;
    }

    /**
     * Define el valor de la propiedad c12.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setC12(String value) {
        this.c12 = value;
    }

    /**
     * Obtiene el valor de la propiedad c13.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getC13() {
        return c13;
    }

    /**
     * Define el valor de la propiedad c13.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setC13(String value) {
        this.c13 = value;
    }

    /**
     * Obtiene el valor de la propiedad c14.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getC14() {
        return c14;
    }

    /**
     * Define el valor de la propiedad c14.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setC14(String value) {
        this.c14 = value;
    }

    /**
     * Obtiene el valor de la propiedad c15.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getC15() {
        return c15;
    }

    /**
     * Define el valor de la propiedad c15.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setC15(String value) {
        this.c15 = value;
    }

    /**
     * Obtiene el valor de la propiedad c16.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getC16() {
        return c16;
    }

    /**
     * Define el valor de la propiedad c16.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setC16(String value) {
        this.c16 = value;
    }

    /**
     * Obtiene el valor de la propiedad c17.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getC17() {
        return c17;
    }

    /**
     * Define el valor de la propiedad c17.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setC17(String value) {
        this.c17 = value;
    }

    /**
     * Obtiene el valor de la propiedad c18.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getC18() {
        return c18;
    }

    /**
     * Define el valor de la propiedad c18.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setC18(String value) {
        this.c18 = value;
    }

    /**
     * Obtiene el valor de la propiedad c19.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getC19() {
        return c19;
    }

    /**
     * Define el valor de la propiedad c19.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setC19(String value) {
        this.c19 = value;
    }

    /**
     * Obtiene el valor de la propiedad trim1.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTRIM1() {
        return trim1;
    }

    /**
     * Define el valor de la propiedad trim1.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTRIM1(String value) {
        this.trim1 = value;
    }

    /**
     * Obtiene el valor de la propiedad trim2.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTRIM2() {
        return trim2;
    }

    /**
     * Define el valor de la propiedad trim2.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTRIM2(String value) {
        this.trim2 = value;
    }

    /**
     * Obtiene el valor de la propiedad trim3.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTRIM3() {
        return trim3;
    }

    /**
     * Define el valor de la propiedad trim3.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTRIM3(String value) {
        this.trim3 = value;
    }


    /**
     * 
     * 							No existe obligatoriedad de introducir epígrafes.
     * 							En caso de introducirse alguno, será obligatorio cumplimentar el
     * 							código (EPI), los módulos aplicables al epígrafe (MOD1...MOD7) y
     * 							el total de cuotas devengadas (TOT).
     * 						
     * 
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="EPIGRAFE" maxOccurs="unbounded" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;attribute name="EPI" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *                 &lt;attribute name="SEC" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *                 &lt;attribute name="MOD1" type="{}IMPA8ABSType" /&gt;
     *                 &lt;attribute name="MOD2" type="{}IMPA8ABSType" /&gt;
     *                 &lt;attribute name="MOD3" type="{}IMPA8ABSType" /&gt;
     *                 &lt;attribute name="MOD4" type="{}IMPA8ABSType" /&gt;
     *                 &lt;attribute name="MOD5" type="{}IMPA8ABSType" /&gt;
     *                 &lt;attribute name="MOD6" type="{}IMPA8ABSType" /&gt;
     *                 &lt;attribute name="MOD7" type="{}IMPA8ABSType" /&gt;
     *                 &lt;attribute name="TOT" use="required" type="{}IMPA15Type" /&gt;
     *                 &lt;attribute name="DIAS_EJE_ANT" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *                 &lt;attribute name="DIAS_TRI_CUR" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *                 &lt;attribute name="IND_COR" type="{}IMPA5Type" /&gt;
     *                 &lt;attribute name="CUO_RES_TRIM" type="{}IMPA15Type" /&gt;
     *                 &lt;attribute name="CUO_SOP" type="{}IMPA15Type" /&gt;
     *                 &lt;attribute name="DIAS_EJE_CUR" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *                 &lt;attribute name="CUO_RES_ULT_TRIM" type="{}IMPA15Type" /&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "epigrafe"
    })
    public static class EPIGRAFES {

        @XmlElement(name = "EPIGRAFE")
        protected List<TAUTOLIQUIDACION.EPIGRAFES.EPIGRAFE> epigrafe;

        /**
         * Gets the value of the epigrafe property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the epigrafe property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEPIGRAFE().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TAUTOLIQUIDACION.EPIGRAFES.EPIGRAFE }
         * 
         * 
         */
        public List<TAUTOLIQUIDACION.EPIGRAFES.EPIGRAFE> getEPIGRAFE() {
            if (epigrafe == null) {
                epigrafe = new ArrayList<TAUTOLIQUIDACION.EPIGRAFES.EPIGRAFE>();
            }
            return this.epigrafe;
        }


        /**
         * <p>Clase Java para anonymous complex type.
         * 
         * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;attribute name="EPI" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *       &lt;attribute name="SEC" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *       &lt;attribute name="MOD1" type="{}IMPA8ABSType" /&gt;
         *       &lt;attribute name="MOD2" type="{}IMPA8ABSType" /&gt;
         *       &lt;attribute name="MOD3" type="{}IMPA8ABSType" /&gt;
         *       &lt;attribute name="MOD4" type="{}IMPA8ABSType" /&gt;
         *       &lt;attribute name="MOD5" type="{}IMPA8ABSType" /&gt;
         *       &lt;attribute name="MOD6" type="{}IMPA8ABSType" /&gt;
         *       &lt;attribute name="MOD7" type="{}IMPA8ABSType" /&gt;
         *       &lt;attribute name="TOT" use="required" type="{}IMPA15Type" /&gt;
         *       &lt;attribute name="DIAS_EJE_ANT" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *       &lt;attribute name="DIAS_TRI_CUR" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *       &lt;attribute name="IND_COR" type="{}IMPA5Type" /&gt;
         *       &lt;attribute name="CUO_RES_TRIM" type="{}IMPA15Type" /&gt;
         *       &lt;attribute name="CUO_SOP" type="{}IMPA15Type" /&gt;
         *       &lt;attribute name="DIAS_EJE_CUR" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *       &lt;attribute name="CUO_RES_ULT_TRIM" type="{}IMPA15Type" /&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class EPIGRAFE {

            @XmlAttribute(name = "EPI", required = true)
            protected String epi;
            @XmlAttribute(name = "SEC")
            protected String sec;
            @XmlAttribute(name = "MOD1")
            protected String mod1;
            @XmlAttribute(name = "MOD2")
            protected String mod2;
            @XmlAttribute(name = "MOD3")
            protected String mod3;
            @XmlAttribute(name = "MOD4")
            protected String mod4;
            @XmlAttribute(name = "MOD5")
            protected String mod5;
            @XmlAttribute(name = "MOD6")
            protected String mod6;
            @XmlAttribute(name = "MOD7")
            protected String mod7;
            @XmlAttribute(name = "TOT", required = true)
            protected String tot;
            @XmlAttribute(name = "DIAS_EJE_ANT")
            protected String diasejeant;
            @XmlAttribute(name = "DIAS_TRI_CUR")
            protected String diastricur;
            @XmlAttribute(name = "IND_COR")
            protected String indcor;
            @XmlAttribute(name = "CUO_RES_TRIM")
            protected String cuorestrim;
            @XmlAttribute(name = "CUO_SOP")
            protected String cuosop;
            @XmlAttribute(name = "DIAS_EJE_CUR")
            protected String diasejecur;
            @XmlAttribute(name = "CUO_RES_ULT_TRIM")
            protected String cuoresulttrim;

            /**
             * Obtiene el valor de la propiedad epi.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getEPI() {
                return epi;
            }

            /**
             * Define el valor de la propiedad epi.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setEPI(String value) {
                this.epi = value;
            }

            /**
             * Obtiene el valor de la propiedad sec.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getSEC() {
                return sec;
            }

            /**
             * Define el valor de la propiedad sec.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setSEC(String value) {
                this.sec = value;
            }

            /**
             * Obtiene el valor de la propiedad mod1.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getMOD1() {
                return mod1;
            }

            /**
             * Define el valor de la propiedad mod1.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setMOD1(String value) {
                this.mod1 = value;
            }

            /**
             * Obtiene el valor de la propiedad mod2.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getMOD2() {
                return mod2;
            }

            /**
             * Define el valor de la propiedad mod2.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setMOD2(String value) {
                this.mod2 = value;
            }

            /**
             * Obtiene el valor de la propiedad mod3.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getMOD3() {
                return mod3;
            }

            /**
             * Define el valor de la propiedad mod3.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setMOD3(String value) {
                this.mod3 = value;
            }

            /**
             * Obtiene el valor de la propiedad mod4.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getMOD4() {
                return mod4;
            }

            /**
             * Define el valor de la propiedad mod4.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setMOD4(String value) {
                this.mod4 = value;
            }

            /**
             * Obtiene el valor de la propiedad mod5.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getMOD5() {
                return mod5;
            }

            /**
             * Define el valor de la propiedad mod5.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setMOD5(String value) {
                this.mod5 = value;
            }

            /**
             * Obtiene el valor de la propiedad mod6.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getMOD6() {
                return mod6;
            }

            /**
             * Define el valor de la propiedad mod6.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setMOD6(String value) {
                this.mod6 = value;
            }

            /**
             * Obtiene el valor de la propiedad mod7.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getMOD7() {
                return mod7;
            }

            /**
             * Define el valor de la propiedad mod7.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setMOD7(String value) {
                this.mod7 = value;
            }

            /**
             * Obtiene el valor de la propiedad tot.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getTOT() {
                return tot;
            }

            /**
             * Define el valor de la propiedad tot.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setTOT(String value) {
                this.tot = value;
            }

            /**
             * Obtiene el valor de la propiedad diasejeant.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getDIASEJEANT() {
                return diasejeant;
            }

            /**
             * Define el valor de la propiedad diasejeant.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDIASEJEANT(String value) {
                this.diasejeant = value;
            }

            /**
             * Obtiene el valor de la propiedad diastricur.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getDIASTRICUR() {
                return diastricur;
            }

            /**
             * Define el valor de la propiedad diastricur.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDIASTRICUR(String value) {
                this.diastricur = value;
            }

            /**
             * Obtiene el valor de la propiedad indcor.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getINDCOR() {
                return indcor;
            }

            /**
             * Define el valor de la propiedad indcor.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setINDCOR(String value) {
                this.indcor = value;
            }

            /**
             * Obtiene el valor de la propiedad cuorestrim.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCUORESTRIM() {
                return cuorestrim;
            }

            /**
             * Define el valor de la propiedad cuorestrim.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCUORESTRIM(String value) {
                this.cuorestrim = value;
            }

            /**
             * Obtiene el valor de la propiedad cuosop.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCUOSOP() {
                return cuosop;
            }

            /**
             * Define el valor de la propiedad cuosop.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCUOSOP(String value) {
                this.cuosop = value;
            }

            /**
             * Obtiene el valor de la propiedad diasejecur.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getDIASEJECUR() {
                return diasejecur;
            }

            /**
             * Define el valor de la propiedad diasejecur.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDIASEJECUR(String value) {
                this.diasejecur = value;
            }

            /**
             * Obtiene el valor de la propiedad cuoresulttrim.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getCUORESULTTRIM() {
                return cuoresulttrim;
            }

            /**
             * Define el valor de la propiedad cuoresulttrim.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setCUORESULTTRIM(String value) {
                this.cuoresulttrim = value;
            }

        }

    }

}
