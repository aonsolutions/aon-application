package com.code.aon.ui.tasDelivery.util;

import java.util.List;

import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;

/**
 * Wrapper of <code>Delivery</code>. It contains a Delivery and a list with the <code>deliveryDetail</code>s.
 */
public class DeliveryWrapper {

	/** The delivery. */
	private Delivery delivery;
	
	/** The lines. */
	private List<DeliveryDetail> lines;

	/**
	 * Gets the delivery.
	 * 
	 * @return the delivery
	 */
	public Delivery getDelivery() {
		return delivery;
	}

	/**
	 * Sets the delivery.
	 * 
	 * @param delivery the delivery
	 */
	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}

	/**
	 * Gets the lines.
	 * 
	 * @return the lines
	 */
	public List<DeliveryDetail> getLines() {
		return lines;
	}

	/**
	 * Sets the lines.
	 * 
	 * @param lines the lines
	 */
	public void setLines(List<DeliveryDetail> lines) {
		this.lines = lines;
	}
}
