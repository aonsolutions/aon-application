package com.esferalia.aon.gwt.fiscal.client.invoice;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IAdministrationVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus.InvoiceCommunicationStatusVisitor;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType.InvoiceCommunicationTypeVisitor;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.watson.mutable.MutableObject;
import com.google.gwt.user.client.ui.InlineLabel;

class InvoiceCommunicationIcon extends InlineLabel {
	
	public InvoiceCommunicationIcon(InvoiceModuleOptions options, Invoice invoice, InvoiceCommunicationType type, InvoiceCommunicationStatus status) {
		super();
		if (type == null && status == null) {
			setTitle(AON.MSG.noCommunication());
			setStyleName(AON.CSS.aonLabelWithIcon());
			addStyleName(AON.CSS.aonIconLock());
		} else {
			boolean emptyAdministration = (options.getConfiguration() == null 
					|| options.getConfiguration().getCommunicationConfig()==null
					|| options.getConfiguration().getCommunicationConfig().getAdministration()==null
					);
			Administration admon = emptyAdministration 
				? Administration.UNKNOWN
				: options.getConfiguration().getCommunicationConfig().getAdministration(invoice.getExpDate()) 
			;
			initIcon(admon, type, status);
		}
	}
	
	public InvoiceCommunicationIcon(InvoiceModuleOptions options, Invoice invoice ) {
		super();
		setTitle(AON.MSG.noSif());
		setStyleName(AON.CSS.aonLabelWithIcon());
		addStyleName(AON.CSS.aonIconBlock());
	}	
	
	private void initIcon(Administration admon, InvoiceCommunicationType type, InvoiceCommunicationStatus status) {
		StringBuilder title = new StringBuilder();
		if (type != null) title.append( type.getAbbr() );
		if (status != null) {
			if (title.length() > 0) title.append(" - ");
			title.append( getStatusDescription( type, status));
		}
		setTitle(title.toString());
		
		setStyleName(AON.CSS.aonLabelWithIcon());
		if (type == null) {
			addStyleName(AON.CSS.aonIconUnknown());
		} else {
			decorateIcon(type, admon, status);
		}
	}

	private String  getStatusDescription(InvoiceCommunicationType type, InvoiceCommunicationStatus status) {
		if (type == InvoiceCommunicationType.NO_VERIFACTU && status == InvoiceCommunicationStatus.PENDING) {
			return AON.MSG.archived();
		}
		if (type == InvoiceCommunicationType.SIF && status == InvoiceCommunicationStatus.ACCEPTED) {
			return AON.MSG.archived();
		}
		return status == null ? InvoiceCommunicationStatus.PENDING.getDescription() : status.getDescription();
	}
	
	private void decorateIcon(InvoiceCommunicationType type, Administration admon, InvoiceCommunicationStatus status) {
		try {
			type.visit(new InvoiceCommunicationTypeVisitor() {
				
				@Override
				public void visitVERIFACTU() throws InvoiceCommunicationException {
					if ( status == null ) {
						addStyleName(AON.CSS.aonIconAeat());
					} else {
						status.accept(new InvoiceCommunicationStatusVisitor() {
							@Override public void visitPending() {addStyleName( AON.CSS.aonIconAeatOrange());}
							@Override public void visitAccepted() {addStyleName( AON.CSS.aonIconAeatGreen());}
							@Override public void visitAcceptedWithErrors() {addStyleName( AON.CSS.aonIconAeatGreen());}
							@Override public void visitWrong() {addStyleName( AON.CSS.aonIconAeatRed());}
							@Override public void visitCancelled() {addStyleName( AON.CSS.aonIconAeatBw());}
							@Override public void visitExternallyCommunicated() {addStyleName( AON.CSS.aonIconAeatBlue());}
						});
					}
				}
				
				@Override
				public void visitNO_VERIFACTU() throws InvoiceCommunicationException {
					if ( status == null ) {
						addStyleName(AON.CSS.aonIconAeatYellow());
					} else {
						status.accept(new InvoiceCommunicationStatusVisitor() {
							@Override public void visitPending() {addStyleName( AON.CSS.aonIconAeatLightGreen());}
							@Override public void visitAccepted() {addStyleName( AON.CSS.aonIconAeatGreen());}
							@Override public void visitAcceptedWithErrors() {addStyleName( AON.CSS.aonIconAeatGreen());}
							@Override public void visitWrong() {addStyleName( AON.CSS.aonIconAeatRed());}
							@Override public void visitCancelled() {addStyleName( AON.CSS.aonIconAeatBw());}
							@Override public void visitExternallyCommunicated() {addStyleName( AON.CSS.aonIconAeatBlue());}
						});
					}
				}

				@Override
				public void visitSIF() throws InvoiceCommunicationException {
					if ( status == null ) {
						addStyleName(AON.CSS.aonIconSif());
					} else {
						status.accept(new InvoiceCommunicationStatusVisitor() {
							@Override public void visitPending() {addStyleName( AON.CSS.aonIconSifOrange());}
							@Override public void visitAccepted() {addStyleName( AON.CSS.aonIconSifGreen());}
							@Override public void visitAcceptedWithErrors() {addStyleName( AON.CSS.aonIconSifGreen());}
							@Override public void visitWrong() {addStyleName( AON.CSS.aonIconSifRed());}
							@Override public void visitCancelled() {addStyleName( AON.CSS.aonIconSif());}
							@Override public void visitExternallyCommunicated() {addStyleName( AON.CSS.aonIconSifBlue());}
						});
					}
				}

				@Override
				public void visitTBAI() throws InvoiceCommunicationException {
					if (admon == null) {
						addStyleName(AON.CSS.aonIconUnknown() );
					} else {
						addStyleName(
							admon.visit(new IAdministrationVisitor<String>() {
								@Override public String visitAlava() { return getArabaStyle(status);  }
								@Override public String visitGipuzkoa() {return getGipuzkoaStyle(status);  }
								@Override public String visitBizkaia() {return AON.CSS.aonIconUnknown(); }
								@Override public String visitNavarra() {return AON.CSS.aonIconUnknown(); }
								@Override public String visitCommonTerritory() {return AON.CSS.aonIconUnknown(); }
								@Override public String visitUnknown() {return AON.CSS.aonIconUnknown(); }
								@Override public String visitCanarias() {return AON.CSS.aonIconUnknown(); }
							})
						);
					}
				}
				
				private String getArabaStyle(InvoiceCommunicationStatus status) {
					if ( status == null ) return AON.CSS.aonIconAraba();
					MutableObject<String> style = new MutableObject<>();
					status.accept(new InvoiceCommunicationStatusVisitor() {
						@Override public void visitPending() {style.setValue( AON.CSS.aonIconArabaOrange());}
						@Override public void visitAccepted() {style.setValue( AON.CSS.aonIconArabaGreen());}
						@Override public void visitAcceptedWithErrors() {style.setValue( AON.CSS.aonIconArabaGreen());}
						@Override public void visitWrong() {style.setValue( AON.CSS.aonIconArabaRed());}
						@Override public void visitCancelled() {style.setValue( AON.CSS.aonIconArabaBw());}
						@Override public void visitExternallyCommunicated() {addStyleName( AON.CSS.aonIconArabaBlue());}
					});
					return style.getValue();
				}
				
				private String getGipuzkoaStyle(InvoiceCommunicationStatus status) {
					if ( status == null ) return AON.CSS.aonIconGipuzkoa();
					MutableObject<String> style = new MutableObject<>();
					status.accept(new InvoiceCommunicationStatusVisitor() {
						@Override public void visitPending() {style.setValue( AON.CSS.aonIconGipuzkoaOrange());}
						@Override public void visitAccepted() {style.setValue( AON.CSS.aonIconGipuzkoaGreen());}
						@Override public void visitAcceptedWithErrors() {style.setValue( AON.CSS.aonIconGipuzkoaGreen());}
						@Override public void visitWrong() {style.setValue( AON.CSS.aonIconGipuzkoaRed());}
						@Override public void visitCancelled() {style.setValue( AON.CSS.aonIconGipuzkoaBw());}
						@Override public void visitExternallyCommunicated() {addStyleName( AON.CSS.aonIconGipuzkoaBlue());}
					});
					return style.getValue();
				}
				
				@Override
				public void visitLROE() throws InvoiceCommunicationException {
					if ( status == null ) {
						addStyleName(AON.CSS.aonIconBizkaia());
					} else {
						status.accept(new InvoiceCommunicationStatusVisitor() {
							@Override public void visitPending() {addStyleName( AON.CSS.aonIconBizkaiaOrange());}
							@Override public void visitAccepted() {addStyleName( AON.CSS.aonIconBizkaiaGreen());}
							@Override public void visitAcceptedWithErrors() {addStyleName( AON.CSS.aonIconBizkaiaGreen());}
							@Override public void visitWrong() {addStyleName( AON.CSS.aonIconBizkaiaRed());}
							@Override public void visitCancelled() {addStyleName( AON.CSS.aonIconBizkaiaBw());}
							@Override public void visitExternallyCommunicated() {addStyleName( AON.CSS.aonIconBizkaiaBlue());}
						});
					}
				}
				
				// -----------------------------------------
				@Override
				public void visitSII() throws InvoiceCommunicationException {
					visitVERIFACTU();
				}
				
				@Override
				public void visitFACTURAE() throws InvoiceCommunicationException {
					addStyleName(AON.CSS.aonIconEdit());
				}

				@Override
				public void visitSERES() throws InvoiceCommunicationException {
					addStyleName(AON.CSS.aonIconLetterS());
				}
				
				@Override
				public void visitEMAIL() throws InvoiceCommunicationException {
					addStyleName(AON.CSS.aonIconEmail() );
				}
				
				@Override
				public void visitCLOSING() throws InvoiceCommunicationException {
					addStyleName(AON.CSS.aonIconLock() );
				}
			});
		} catch (Exception e) {
			addStyleName(AON.CSS.aonIconUnknown());
		}
	}
	
}
