package net.aonsolutions.aon.in.pdf.maker.warehouse;

import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;

public class PackagingTagDetail {
	
	private Item item;	
	private String barcode;
	private Double quantity;
	private String sscc;
	private String ean128;
	private CustomerFull customer;
	private Carrier carrier;

	public Item getItem() {
		return item;
	}

	public PackagingTagDetail setItem(Item item) {
		this.item = item;
		return this;
	}

	public String getBarcode() {
		return barcode;
	}

	public PackagingTagDetail setBarcode(String barcode) {
		this.barcode = barcode;
		return this;
	}

	public Double getQuantity() {
		return quantity;
	}

	public PackagingTagDetail setQuantity(Double quantity) {
		this.quantity = quantity;
		return this;
	}

	public String getSscc() {
		return sscc;
	}

	public PackagingTagDetail setSscc(String sscc) {
		this.sscc = sscc;
		return this;
	}

	public String getEan128() {
		return ean128;
	}

	public PackagingTagDetail setEan128(String ean128) {
		this.ean128 = ean128;
		return this;
	}
	
	public CustomerFull getCustomer() {
		if(customer == null) {
			customer =new CustomerFull();
		}
		return customer;
	}
	
	public PackagingTagDetail setCustomer(CustomerFull customer) {
		this.customer = customer;
		return this;
	}
	
	public Carrier getCarrier() {
		return carrier;
	}
	
	public PackagingTagDetail setCarrier(Carrier carrier) {
		this.carrier = carrier;
		return this;
	}
}
