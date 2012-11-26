package com.code.aon.ui.document.tree;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.builder.EqualsBuilder;

import com.code.aon.common.enumeration.MimeType;

public class EnterpriseTreeData {

	public static final String ENTERPRISE_ICON = "/css/images/tree-root.png";
	
	public static final String PROJECT_ICON = "/css/images/folder.png";
	
	public static final String DOCUMENT_ICON = "/css/images/document.png";
	
	public static final String DOCUMENT_IMAGE_ICON = "/css/images/document-image.png";
	
	public static final String DOCUMENT_PDF_ICON = "/css/images/document-pdf.png";
	
	public static final String DOCUMENT_PPT_ICON = "/css/images/document-ppt.png";
	
	public static final String DOCUMENT_WORD_ICON = "/css/images/document-word.png";
	
	public static final String DOCUMENT_XLS_ICON = "/css/images/document-xls.png";
	
	public static final String DOCUMENT_ZIP_ICON = "/css/images/document-zip.png";

	private Serializable id;
	
	private MimeType mimeType;
	
	private AonTreeKey key;
	
	private int count;

	public EnterpriseTreeData(Serializable id, AonTreeKey key) {
		this.id = id;
		this.key = key;
	}

	public Serializable getId() {
		return id;
	}

	public String getLabel() {
		return key.getLabel();
	}

	public EnterpriseTreeType getType() {
		return key.getType();
	}

	public String getTypeName() {
		return getType().toString();
	}
	
	public void actionListener( ActionEvent event ) {
		if ( getType().getActionListener() != null ) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			getType().getActionListener().invoke(ctx.getELContext(), new Object[]{event});			
		}
	}

	public AonTreeKey getKey() {
		return this.key;
	}
	
	public int getCount() {
		return count;
	}

	public void incCount() {
		this.count++;
	}

	public void decCount() {
		this.count--;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	public MimeType getMimeType() {
		return mimeType;
	}

	public void setMimeType(MimeType mimeType) {
		this.mimeType = mimeType;
	}

	public String getIcon() {
		String icon = getType().getIcon();
		if ( mimeType != null ) {
			if ( mimeType.getName().startsWith("image") ) {
				icon = DOCUMENT_IMAGE_ICON;
			} else {
				switch ( mimeType ) {
					case MIME_PDF:
					case MIME_SIGNED_PDF:
						icon = DOCUMENT_PDF_ICON;
						break;
					case MIME_MS_WORD:
					case MIME_MS_WORD_2007:
						icon = DOCUMENT_WORD_ICON;
						break;
					case MIME_MS_EXCEL:
					case MIME_MS_EXCEL_2007:
						icon = DOCUMENT_XLS_ICON;
						break;
					case MIME_MS_POWER_POINT:
					case MIME_MS_POWER_POINT_2007:
						icon = DOCUMENT_PPT_ICON;
						break;
					case MIME_ZIP:
						icon = DOCUMENT_ZIP_ICON;
						break;
				}				
			}
		}
		return icon;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final EnterpriseTreeData o = (EnterpriseTreeData) obj;
		return new EqualsBuilder()
			.append(this.id, o.id)
			.append(this.key, o.key)
			.append(this.mimeType, o.mimeType)
			.isEquals();
	}
	
}
