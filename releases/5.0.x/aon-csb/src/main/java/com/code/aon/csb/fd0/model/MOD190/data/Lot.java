package com.code.aon.csb.fd0.model.MOD190.data;

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
	private ArrayList<WithHolder> withHolders = new ArrayList<WithHolder>();

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
	 * Adds a WithHolder
	 * 
	 * @param withHolder
	 */
	public void addWithHolder(WithHolder withHolder) {
		this.withHolders.add(withHolder);
	}

	/**
	 * @return withHolders iterator
	 */
	public Iterator<WithHolder> getWithHoldersIterator() {
		return this.withHolders.iterator();
	}

	/**
	 * @return the numReceiver
	 */
	public Integer getNumReceiver() {
		int num = 0;
		Iterator iter = getWithHoldersIterator();
		while (iter.hasNext()) {
			WithHolder wh = (WithHolder) iter.next();
			num += wh.getC001();
		}
		return num;
	}

	/**
	 * @return the numWithHolder
	 */
	public Integer getNumWithHolder() {
		return withHolders.size();
	}


}
