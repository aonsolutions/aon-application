package com.esferalia.aon.gwt.template.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.google.gwt.user.client.rpc.IsSerializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "EcommerceProduct")
public class EcommerceProduct implements Serializable, IsSerializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@XmlElement(name = "Template", required = true)
	protected EcommerceProduct.Template template;
    @XmlElement(name = "Product", required = true)
    protected EcommerceProduct.Product product;
    @XmlElement(name = "ProductData", required = true)
    protected EcommerceProduct.ProductData productData;

    /**
     * Obtiene el valor de la propiedad template.
     * 
     * @return
     *     possible object is
     *     {@link EcommerceProduct.Template }
     *     
     */
    public EcommerceProduct.Template getTemplate() {
        return template;
    }

    /**
     * Define el valor de la propiedad template.
     * 
     * @param value
     *     allowed object is
     *     {@link EcommerceProduct.Template }
     *     
     */
    public void setTemplate(EcommerceProduct.Template value) {
        this.template = value;
    }

    
    /**
     * Obtiene el valor de la propiedad product.
     * 
     * @return
     *     possible object is
     *     {@link EcommerceProduct.Product }
     *     
     */
    public EcommerceProduct.Product getProduct() {
        return product;
    }

    /**
     * Define el valor de la propiedad product.
     * 
     * @param value
     *     allowed object is
     *     {@link EcommerceProduct.Product }
     *     
     */
    public void setProduct(EcommerceProduct.Product value) {
        this.product = value;
    }

    /**
     * Obtiene el valor de la propiedad productData.
     * 
     * @return
     *     possible object is
     *     {@link EcommerceProduct.ProductData }
     *     
     */
    public EcommerceProduct.ProductData getProductData() {
        return productData;
    }

    /**
     * Define el valor de la propiedad productData.
     * 
     * @param value
     *     allowed object is
     *     {@link EcommerceProduct.ProductData }
     *     
     */
    public void setProductData(EcommerceProduct.ProductData value) {
        this.productData = value;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {

    })
    public static class Template implements Serializable, IsSerializable{

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@XmlElement(name = "ecommerce", required = true)
        protected String ecommerce;
        @XmlElement(name = "type", required = true)
        protected String type;
        @XmlElement(name = "category", required = true)
        protected String category;
        @XmlElement(name = "amazonTemplateType", required = false)
        protected String amazonTemplateType;
        @XmlElement(name = "amazonVersion", required = false)
        protected String amazonVersion;
        @XmlElement(name = "seller",  required = true)
        protected String seller;
        @XmlElement(name = "tag",  required = true)
        protected String tag;
        
        
		public String getEcommerce() {
			return ecommerce;
		}
		public void setEcommerce(String ecommerce) {
			this.ecommerce = ecommerce;
		}
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getAmazonTemplateType() {
			return amazonTemplateType;
		}
		public void setAmazonTemplateType(String amazonTemplateType) {
			this.amazonTemplateType = amazonTemplateType;
		}
		public String getAmazonVersion() {
			return amazonVersion;
		}
		public void setAmazonVersion(String amazonVersion) {
			this.amazonVersion = amazonVersion;
		}
		public String getSeller() {
			return seller;
		}
		public void setSeller(String seller) {
			this.seller = seller;
		}
		public String getTag() {
			return tag;
		}
		public void setTag(String tag) {
			this.tag = tag;
		}
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {

    })
    public static class Product implements Serializable, IsSerializable{

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@XmlElement(name = "id", required = true)
        protected String id;
        @XmlElement(name = "code", required = true)
        protected String code;
        @XmlElement(name = "name", required = true)
        protected String name;
        @XmlElement(name = "item", required = true)
        protected String item;
        
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getItem() {
			return item;
		}
		public void setItem(String item) {
			this.item = item;
		}
		
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
    		"ecommerce"
    })
    public static class ProductData implements Serializable, IsSerializable{

    	/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@XmlElement(name = "Ecommerce")
    	protected List<EcommerceProduct.ProductData.Ecommerce> ecommerce;  
    	
    	public List<EcommerceProduct.ProductData.Ecommerce> getEcommerce(){
    		if(ecommerce == null)
    			ecommerce = new ArrayList<EcommerceProduct.ProductData.Ecommerce>();
    		return this.ecommerce;
    	}
    	
    	public void setEcommerce(List<EcommerceProduct.ProductData.Ecommerce> ecommerce){
    		this.ecommerce = ecommerce;
    	}
    	
    	@XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {

        })
    	public static class Ecommerce implements Serializable, IsSerializable{
    		 /**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			@XmlElement(name = "code", required = true)
    		 protected String code;
    		 @XmlElement(name = "name", required = true)
    		 protected String name;
    		 @XmlElement(name = "value", required = true)
    		 protected String value;
    		 @XmlElement(name = "presetValues", required = false)
    		 protected EcommerceProduct.ProductData.Ecommerce.PresetValues presetValues;
    		 
    		 
			public String getCode() {
				return code;
			}
			public void setCode(String code) {
				this.code = code;
			}
			public String getName() {
				return name;
			}
			public void setName(String name) {
				this.name = name;
			}
			public String getValue() {
				return value;
			}
			public void setValue(String value) {
				this.value = value;
			}
			public EcommerceProduct.ProductData.Ecommerce.PresetValues getPresetValues() {
				return presetValues;
			}
			public void setPresetValues(
					EcommerceProduct.ProductData.Ecommerce.PresetValues presetValues) {
				this.presetValues = presetValues;
			}


			@XmlAccessorType(XmlAccessType.FIELD)
	        @XmlType(name = "", propOrder = {

	        })
	    	public static class PresetValues implements Serializable, IsSerializable{
				 /**
				 * 
				 */
				private static final long serialVersionUID = 1L;
				@XmlElement(name = "presetValue", required = true)
	    		 protected List<String> presetValue;

				public List<String> getPresetValue() {
					return presetValue;
				}

				public void setPresetValue(List<String> presetValue) {
					this.presetValue = presetValue;
				}
				 
				 
			}
    	}   
    }
}
