package com.code.aon.file.tax.model.MOD349.data;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 * A lot of WithHolders with a Presenter
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 *
 */
public class Lot {

	/**
	 * The lot presenter
	 */
	private Presenter presenter;
	/**
	 * WithHolders list
	 */
	private ArrayList<Deponent> deponents = new ArrayList<Deponent>();

	/**
	 * @return the presenter
	 */
	public Presenter getPresenter() {
		return presenter;
	}

	/**
	 * @param presenter the presenter to set
	 */
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	/**
	 * Adds a Deponent
	 * 
	 * @param deponent
	 */
	public void addDeponents(Deponent deponent) {
		this.deponents.add(deponent);
	}

	/**
	 * @return deponents iterator
	 */
	public Iterator<Deponent> getDeponentsIterator() {
		return this.deponents.iterator();
	}

	/**
	 * @return the numDeclareds
	 */
	public Integer getNumDeclareds() {
		int num = 0;
		Iterator iter = getDeponentsIterator();
		while (iter.hasNext()) {
			Deponent d = (Deponent) iter.next();
			num += d.getC001();
			num += d.getC003();
		}
		return num;
	}

	/**
	 * @return the numDeponents
	 */
	public Integer getNumDeponents() {
		return deponents.size();
	}


}
