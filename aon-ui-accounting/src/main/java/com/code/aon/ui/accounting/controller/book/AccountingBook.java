package com.code.aon.ui.accounting.controller.book;


import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.accounting.Balance;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.registry.RegistryAttachment;

public class AccountingBook implements Comparable<AccountingBook>, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private int order;
	private int mergeOrder;
	private BookType bookType;
	private MimeType mimeType;
	private String description;
	private int number;
	private boolean mergeable;
	private Balance balance;
	private Integer attachId;

	public AccountingBook() {
	}

	public AccountingBook(int order, int mergeOrder, int number, MimeType mimeType, 
		BookType bookType, boolean mergeable) {
			
		setOrder(order);
		setMergeOrder(mergeOrder);
		setNumber(number);
		setMimeType(mimeType);
		setBookType(bookType);
		setMergeable(mergeable);
	}


	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}

	public int getMergeOrder() {
		return mergeOrder;
	}
	public void setMergeOrder(int mergeOrder) {
		this.mergeOrder = mergeOrder;
	}

	public AonReportType getAonReportType() {
		return getBookType()==null?null:getBookType().getAonReportType();
	}

	public BookType getBookType() {
		return bookType;
	}
	public void setBookType(BookType bookType) {
		this.bookType = bookType;
		setDescription((getBookType() != null)?bookType.getDescription():null);	
	}

	public MimeType getMimeType() {
		if (mimeType == null && getAttachId() != null) {
			setMimeType( resolveMimeType() );
		}
		return mimeType;
	}
	private MimeType resolveMimeType() {
		try {
			IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
			RegistryAttachment ra = (RegistryAttachment) bean.get( getAttachId() );
			return ra.getMimeType();
		} catch (ManagerBeanException e) {
			// Nada.
		}
		return null;
	}

	public void setMimeType(MimeType mimeType) {
		this.mimeType = mimeType;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	
	public boolean isMergeable() {
		return mergeable;
	}
	public void setMergeable(boolean mergeable) {
		this.mergeable = mergeable;
	}

	public Balance getBalance() {
		return balance;
	}
	public void setBalance(Balance balance) {
		this.balance = balance;
	}
	
	public Integer getAttachId() {
		return attachId;
	}
	public void setAttachId(Integer attachId) {
		this.attachId = attachId;
	}

	// FALTA - NOMBRE DEL FICHERO <NOMBRE>_XXX.<EXTENSION> DONDE XXX ES EL NUMERO RELLENO CEROS
//	public String getName() {
//		return bookType + "_" + getNumber() + "." + getMimeType().getExtension();
//	}
	
	public String getName() {
		String name = "" + bookType;
		if (bookType == BookType.BAL_SUMS1 || bookType == BookType.BAL_SUMS2 || bookType == BookType.BAL_SUMS3 || bookType == BookType.BAL_SUMS4) {
			name = "BAL_SUMS";
		} else if (bookType == BookType.IVAR || bookType == BookType.IVAS || bookType == BookType.IVAI) {
			name = "IVA";
		}
		String numberStr = String.format("%03d", getNumber());
		return name + "_" + numberStr + "." + getMimeType().getExtension();
	}
	
	public boolean isBalanceReport() {
		return getBookType() == BookType.BALANCES;
	}
	public boolean isProfitAndLossReport() {
		return getBookType() == BookType.PER_GAN;
	}
	public boolean isOther() {
		return getBookType() == BookType.OTROS
			|| getBookType() == BookType.MEMORIA
			|| getBookType() == BookType.INVENTAR;
	}

	public String getErrorIfNotValid() {
		if (getOrder() == 0 ) {
			return "No se ha definido número en el listado #" + getOrder() + " ( "+ getBookType().getDescription() +")";
		}
		if (getNumber() == 0 ) {
			return  "No se ha definido número en el listado numero #" + getOrder() + " ( "+ getBookType().getDescription() +")";
		}
		if ((isProfitAndLossReport() || isBalanceReport()) && getBalance() == null) {
			return  "No se ha definido el balance en el listado numero #" + getOrder() + " ( "+ getBookType().getDescription() +")";
		} else if (isOther() && getAttachId() == null) { 
			return  "No se ha definido el archivo en el listado numero #" + getOrder() + " ( "+ getBookType().getDescription() +")";
		} else {
			if (StringUtils.isBlank( getDescription() )) {
				return  "No se ha definido descripción en el listado numero #" + getOrder() + " ( "+ getBookType().getDescription() +")";
			}
		}
		if (isMergeable() && getMimeType() != MimeType.MIME_PDF) {
			return  "No se puede añadir al PDF único el listado numero #" + getOrder() 
					+ " ( "+ getBookType().getDescription() +"). Es de tipo '"+ getMimeType().getName() + "'"
					+ " y debe ser un PDF.";
		}
		return null;
	}

	@Override
	public int compareTo(AccountingBook o) {
		int i = Integer.compare(getOrder(), o.getOrder());
		if (i == 0) {
			i = Integer.compare(getMergeOrder(), o.getMergeOrder());	
		}
		return i;
	}
	
}
