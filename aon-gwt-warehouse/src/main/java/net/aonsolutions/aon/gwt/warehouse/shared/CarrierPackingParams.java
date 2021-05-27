package net.aonsolutions.aon.gwt.warehouse.shared;

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
@XmlRootElement(name = "params")
public class CarrierPackingParams implements  IsSerializable{
	@XmlElement(name = "param")
	protected List<CarrierPackingParams.Param> param;  
    
    public List<CarrierPackingParams.Param> getParam() {
		return param;
	}

	public void setParam(List<CarrierPackingParams.Param> param) {
		this.param = param;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {

    })
    public static class Param implements IsSerializable{

		@XmlElement(name = "name", required = true)
		protected String name;
		@XmlElement(name = "value", required = true)
		protected String value;
		
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
    }
}
