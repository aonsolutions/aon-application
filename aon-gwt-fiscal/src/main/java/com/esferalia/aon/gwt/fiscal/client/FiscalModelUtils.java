package com.esferalia.aon.gwt.fiscal.client;

import java.util.Arrays;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IAdministrationVisitor;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalStatusVisitor;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;

public class FiscalModelUtils {
	
	private FiscalModelUtils() {
		
	}
	
	public static String getModelName(IFiscalModel fm) {
		return  com.esferalia.aon.occam.api.model.fiscal.FiscalModelUtils.getModelName(fm);
	}
	
	public static String getPeriodDescription(IFiscalModel fm) {
		return  com.esferalia.aon.occam.api.model.fiscal.FiscalModelUtils.getPeriodDescription(fm);
	}
	
//	public static <T extends FiscalModel> void fillPaymentInfo(FlowPanel paymentInfo,T mod) {
//		paymentInfo.clear();
//		paymentInfo.setVisible(mod.isFinished() || mod.isSent());
//		InlineLabel l1 = new InlineLabel(AON.MSG.result());
//		l1.setStyleName(AON.CSS.aonInnerLabel());
//		paymentInfo.add(l1);
//		InlineLabel l2 = new InlineLabel(AON.FMT.format(mod.getResult()));
//		l2.setStyleName(AON.CSS.aonInnerLabel());
//		l2.addStyleName(AON.CSS.aonBold());
//		paymentInfo.add(l2);
//		if (mod.getDeclarationType() != null) {
//			InlineLabel l3 = new InlineLabel(mod.getDeclarationType().getDescription());
//			l3.setStyleName(AON.CSS.aonInnerLabel());
//			l3.addStyleName(AON.CSS.aonBold());
//			paymentInfo.add(l3);
//		}
//		if (mod.getFinance() != null && mod.getFinance().getBankAccount() != null) {
//			InlineLabel l4 = new InlineLabel(mod.getFinance().getBankAccount().getIban());
//			l4.setStyleName(AON.CSS.aonInnerLabel());
//			l4.addStyleName(AON.CSS.aonBold());
//			paymentInfo.add(l4);
//			
//			InlineLabel l5 = new InlineLabel(mod.getFinance().getBankAlias());
//			l5.setStyleName(AON.CSS.aonInnerLabel());
//			paymentInfo.add(l5);
//		}
//	}

	private static class FiscalStatusBackgroundRGB implements IFiscalStatusVisitor<String> {
		@Override public String visitMissing() 	{return "White";}
		@Override public String visitPending() 	{return "LightGray";}
		@Override public String visitCustomerCheck() {return "LightYellow";}
		@Override public String visitBatched() 	{return "DarkOrchid";}
		@Override public String visitBlocked() 	{return "red";}
		@Override public String visitFinished() {return "#e3ffab";}
		@Override public String visitCustomerAccepted() {return "#e9ffdb"; }
		@Override public String visitCustomerRejected() {return "DarkRed";}
		@Override public String visitSent() 	{return "#3EC946";}   
		
	}
	private static final IFiscalStatusVisitor<String> FISCAL_STATUS_BACKGROUND_RGB = new FiscalStatusBackgroundRGB();
	public static String getStatusBckColorRGB(FiscalStatus status) {
		return ((status != null) ? status : FiscalStatus.MISSING).visit(FISCAL_STATUS_BACKGROUND_RGB);
	}

	private static class FiscalStatusForegroundRGB implements IFiscalStatusVisitor<String> {
		@Override public String visitPending() 	{return "black";}
		@Override public String visitFinished() {return "black";}
		@Override public String visitBatched() 	{return "black";}
		@Override public String visitBlocked() 	{return "white";}
		@Override public String visitSent() 	{return "white";}
		@Override public String visitMissing() 	{return "black";}
		@Override public String visitCustomerCheck() {return "black";}
		@Override public String visitCustomerAccepted() {return "black"; }
		@Override public String visitCustomerRejected() {return "white";}
	}
	private static final IFiscalStatusVisitor<String> FISCAL_STATUS_FOREGROUND_RGB = new FiscalStatusForegroundRGB();
	public static String getStatusFrgColorRGB(FiscalStatus status) {
		return ((status != null) ? status : FiscalStatus.MISSING).visit(FISCAL_STATUS_FOREGROUND_RGB);
	}
	
	private static class AdministrationIconStyle implements IAdministrationVisitor<String> {
		@Override public String visitAlava() 	{return AON.CSS.aonIconAraba();}
		@Override public String visitBizkaia() 	{return AON.CSS.aonIconBizkaia();}
		@Override public String visitGipuzkoa() {return AON.CSS.aonIconGipuzkoa();}
		@Override public String visitNavarra() 	{return AON.CSS.aonIconNavarra();}
		@Override public String visitCommonTerritory() 	{return AON.CSS.aonIconAeat();}
		@Override public String visitUnknown() 	{return AON.CSS.aonIconUnknown();}
	}
	private static final IAdministrationVisitor<String> ADMINISTRATION_ICON_STYLE = new AdministrationIconStyle();
	public static String getAdministrationIconStyle(Administration adm) {
		return ((adm != null) ? adm : Administration.COMMON_TERRITORY).visit(ADMINISTRATION_ICON_STYLE);
	}
	
	private static class AdministrationBWIconStyle implements IAdministrationVisitor<String> {
		@Override public String visitAlava() 	{return AON.CSS.aonIconArabaBw();}
		@Override public String visitBizkaia() 	{return AON.CSS.aonIconBizkaiaBw();}
		@Override public String visitGipuzkoa() {return AON.CSS.aonIconGipuzkoaBw();}
		@Override public String visitNavarra() 	{return AON.CSS.aonIconNavarraBw();}
		@Override public String visitCommonTerritory() 	{return AON.CSS.aonIconAeatBw();}
		@Override public String visitUnknown() 	{return AON.CSS.aonIconUnknown();}
	}
	private static final IAdministrationVisitor<String> ADMINISTRATION_BW_ICON_STYLE = new AdministrationBWIconStyle();
	public static String getAdministrationBWIconStyle(Administration adm) {
		return ((adm != null) ? adm : Administration.COMMON_TERRITORY).visit(ADMINISTRATION_BW_ICON_STYLE);
	}

	private static class AdministrationIconResource implements IAdministrationVisitor<DataResource> {
		@Override public DataResource visitAlava() 		{return AON.AON_SOLUTIONS_RESOURCES.aonIconAraba();}
		@Override public DataResource visitBizkaia() 	{return AON.AON_SOLUTIONS_RESOURCES.aonIconBizkaia();}
		@Override public DataResource visitGipuzkoa() 	{return AON.AON_SOLUTIONS_RESOURCES.aonIconGipuzkoa();}
		@Override public DataResource visitNavarra() 	{return AON.AON_SOLUTIONS_RESOURCES.aonIconNavarra();}
		@Override public DataResource visitCommonTerritory() 	{return AON.AON_SOLUTIONS_RESOURCES.aonIconAeat();}
		@Override public DataResource visitUnknown() 	{return AON.AON_SOLUTIONS_RESOURCES.aonIconUnknown();}
	}
	private static final IAdministrationVisitor<DataResource> ADMINISTRATION_ICON_RESOURCE = new AdministrationIconResource();
	public static DataResource getAdministrationIconDataResource(Administration adm) {
		return ((adm != null) ? adm : Administration.COMMON_TERRITORY).visit(ADMINISTRATION_ICON_RESOURCE);
	}

	private static class AdministrationBWIconResource implements IAdministrationVisitor<DataResource> {
		@Override public DataResource visitAlava() 		{return AON.AON_SOLUTIONS_RESOURCES.aonIconArabaBw();}
		@Override public DataResource visitBizkaia() 	{return AON.AON_SOLUTIONS_RESOURCES.aonIconBizkaiaBw();}
		@Override public DataResource visitGipuzkoa() 	{return AON.AON_SOLUTIONS_RESOURCES.aonIconGipuzkoaBw();}
		@Override public DataResource visitNavarra() 	{return AON.AON_SOLUTIONS_RESOURCES.aonIconNavarraBw();}
		@Override public DataResource visitCommonTerritory() 	{return AON.AON_SOLUTIONS_RESOURCES.aonIconAeatBw();}
		@Override public DataResource visitUnknown() 	{return AON.AON_SOLUTIONS_RESOURCES.aonIconUnknown();}
	}
	private static final IAdministrationVisitor<DataResource> ADMINISTRATION_BW_ICON_RESOURCE = new AdministrationBWIconResource();
	public static DataResource getAdministrationBWIconDataResource(Administration adm) {
		return ((adm != null) ? adm : Administration.COMMON_TERRITORY).visit(ADMINISTRATION_BW_ICON_RESOURCE);
	}
	
	private static class AdministrationBackgroundStyle implements IAdministrationVisitor<String> {
		@Override public String visitAlava() 	{return AON.CSS.aonArabaBackgroundColor();}
		@Override public String visitBizkaia() 	{return AON.CSS.aonBizkaiaBackgroundColor();}
		@Override public String visitGipuzkoa() {return AON.CSS.aonGipuzkoaBackgroundColor();}
		@Override public String visitNavarra() 	{return AON.CSS.aonNavarraBackgroundColor();}
		@Override public String visitCommonTerritory() 	{return AON.CSS.aonAeatBackgroundColor();}
		@Override public String visitUnknown() 	{return AON.CSS.aonAeatBackgroundColor();}
	}
	private static final IAdministrationVisitor<String> ADMINISTRATION_BACKGROUND_STYLE = new AdministrationBackgroundStyle();
	public static String getAdministrationBackgroundStyle(Administration adm) {
		return ((adm != null) ? adm : Administration.COMMON_TERRITORY).visit(ADMINISTRATION_BACKGROUND_STYLE);
	}
	

	// 	------------------------------------------------------------------------	
	// 	------------------------------------------------------------------------	
	// 	------------------------------------------------------------------------	
	// 	------------------------------------------------------------------------	
	// 	------------------------------------------------------------------------	
	// 	------------------------------------------------------------------------	
	// 	------------------------------------------------------------------------	
	// 	------------------------------------------------------------------------	

//	/**
//	 * @deprecated Use com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader
//	 */
//	@Deprecated
//	public static void paintHeaderTable(SimplePanel headerPanel, IFiscalModel fm) {
//		Administration admon = (fm == null?Administration.COMMON_TERRITORY:fm.getAdministration());
//		headerPanel.clear();
//		headerPanel.setStyleName(AON.AON_CSS.aonWidthAll());
//		
//		FlexTable headerTable = new FlexTable();
//		headerTable.getColumnFormatter().setWidth(0, "55px");
//		headerTable.getColumnFormatter().setWidth(1, "70px");
//		headerTable.getColumnFormatter().setWidth(2, "auto");
//		headerTable.getColumnFormatter().setWidth(3, "75px");
//		
//		headerTable.setStyleName(AON.AON_CSS.aonFiscalModelTable());
//		
//		Label image = new Label("");
//		image.setStyleName(getAdministrationImage(admon));
//		
//		headerTable.setWidget(0, 0, image);
//		headerTable.getFlexCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderImage());
//		headerTable.getFlexCellFormatter().setRowSpan(0, 0, 2);
//		
//		headerTable.setWidget(0, 1, new Label( FiscalModelUtils.getModelName(fm))); 
//		headerTable.getFlexCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
//		headerTable.getFlexCellFormatter().addStyleName(0, 1, getAdministrationBG(admon));
//		headerTable.getFlexCellFormatter().setRowSpan(0, 1, 2);
//		
//		
//		headerTable.setWidget(0, 2, new Label(AON.MSG.fiscalModelDescriptionlong(fm.getModel())));
//		headerTable.getFlexCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
//		headerTable.getFlexCellFormatter().addStyleName(0, 2, getAdministrationBG(admon));
//		headerTable.getFlexCellFormatter().setRowSpan(0, 2, 2);
//		
//		headerTable.setWidget(0, 3, new Label(""+fm.getYear()));
//		headerTable.getFlexCellFormatter().setStyleName(0, 3, AON.AON_CSS.aonFiscalModelTableHeaderModel());
//		headerTable.getFlexCellFormatter().addStyleName(0, 3, getAdministrationBG(admon));
//		
//		headerTable.setWidget(1, 0, new Label(getPeriodDescription(fm)));
//		headerTable.getFlexCellFormatter().setStyleName(1, 0, AON.AON_CSS.aonFiscalModelTableHeaderModel());
//		headerTable.getFlexCellFormatter().addStyleName(1, 0, getAdministrationBG(admon));
//
//		headerPanel.setWidget(headerTable);
//	}

//	@Deprecated
//	public static String getAdministrationBG(Administration admon) {
//		if (admon == Administration.ALAVA) {
//			return AON.AON_CSS.aonFiscalArabaBg();
//		} else if (admon == Administration.BIZKAIA) {
//			return AON.AON_CSS.aonFiscalBizkaiaBg();
//		} else if (admon == Administration.GIPUZKOA) {
//			return AON.AON_CSS.aonFiscalGipuzkoaBg();
//		} else if (admon == Administration.NAVARRA) {
//			return AON.AON_CSS.aonFiscalNavarraBg();
//		} 
//		return AON.AON_CSS.aonFiscalAeatBg();
//	}
	
//	@Deprecated
//	public static String getAdministrationImage(Administration adm) {
//		if (adm == Administration.ALAVA) {
//			return AON.AON_CSS.aonArabaHeaderImage();
//		} else if (adm == Administration.BIZKAIA) {
//			return AON.AON_CSS.aonBizkaiaHeaderImage();
//		} else if (adm == Administration.GIPUZKOA) {
//			return AON.AON_CSS.aonGipuzkoaHeaderImage();
//		} else if (adm == Administration.NAVARRA) {
//			return AON.AON_CSS.aonNavarraHeaderImage();
//		} 
//		return AON.AON_CSS.aonAeatHeaderImage();
//	}
	
	/**
	 * @param adm
	 * @return
	 * @deprecated Use getAdministrationIconStyle
	 */
	@Deprecated
	public static String getAdministrationIcon(Administration adm) {
		if (adm == Administration.ALAVA) {
			return AON.AON_CSS.aonIconAraba();
		} else if (adm == Administration.BIZKAIA) {
			return AON.AON_CSS.aonIconBizkaia();
		} else if (adm == Administration.GIPUZKOA) {
			return AON.AON_CSS.aonIconGipuzkoa();
		} else if (adm == Administration.NAVARRA) {
			return AON.AON_CSS.aonIconNavarra();
		} else if (adm == Administration.UNKNOWN) {
			return AON.AON_CSS.aonIconQuestion();
		} 
		return AON.AON_CSS.aonIconAeat();
	}
	@Deprecated
	public static String getAdministrationIconBW(Administration adm) {
		if (adm == Administration.ALAVA) {
			return AON.AON_CSS.aonIconArabaBW();
		} else if (adm == Administration.BIZKAIA) {
			return AON.AON_CSS.aonIconBizkaiaBW();
		} else if (adm == Administration.GIPUZKOA) {
			return AON.AON_CSS.aonIconGipuzkoaBW();
		} else if (adm == Administration.NAVARRA) {
			return AON.AON_CSS.aonIconNavarraBW();
		} else if (adm == Administration.UNKNOWN) {
			return AON.AON_CSS.aonIconQuestion();
		} 
		return AON.AON_CSS.aonIconAeatBW();
	}

	/**
	 * @deprecated Use getAdministrationIconDataResource
	 */
	@Deprecated
	public static ImageResource getAdministrationIconResource(Administration adm) {
		if (adm ==Administration.ALAVA) {
			return AON.AON_RESOURCES.aonIconAraba();	
		} else if (adm ==Administration.BIZKAIA) {
			return AON.AON_RESOURCES.aonIconBizkaia();
		} else if (adm ==Administration.GIPUZKOA) {
			return AON.AON_RESOURCES.aonIconGipuzkoa();
		} else if (adm ==Administration.NAVARRA) {
			return AON.AON_RESOURCES.aonIconNavarra();
		} else if (adm == Administration.UNKNOWN) {
			return AON.AON_RESOURCES.aonIconQuestion();
		} 
		return AON.AON_RESOURCES.aonAeat();
	}

	
//	/**
//	 * @deprecated Use fillPaymentInfo
//	 */
//	@Deprecated
//	public static <T extends FiscalModel> void paintPaymentInfo(FlowPanel paymentInfo,T mod) {
//		paymentInfo.clear();
//		paymentInfo.setVisible(mod.isFinished() || mod.isSent());
//		InlineLabel l1 = new InlineLabel(AON.MSG.result());
//		l1.setStyleName(AON.AON_CSS.aonInnerLabel());
//		paymentInfo.add(l1);
//		InlineLabel l2 = new InlineLabel(AON.FMT.format(mod.getResult()));
//		l2.setStyleName(AON.AON_CSS.aonInnerLabel());
//		l2.addStyleName(AON.AON_CSS.aonBold());
//		paymentInfo.add(l2);
//		if (mod.getDeclarationType() != null) {
//			InlineLabel l3 = new InlineLabel(mod.getDeclarationType().getDescription());
//			l3.setStyleName(AON.AON_CSS.aonInnerLabel());
//			l3.addStyleName(AON.AON_CSS.aonBold());
//			paymentInfo.add(l3);
//		}
//		if (mod.getFinance() != null && mod.getFinance().getBankAccount() != null) {
//			InlineLabel l4 = new InlineLabel(mod.getFinance().getBankAccount().getIban());
//			l4.setStyleName(AON.AON_CSS.aonInnerLabel());
//			l4.addStyleName(AON.AON_CSS.aonBold());
//			paymentInfo.add(l4);
//			
//			InlineLabel l5 = new InlineLabel(mod.getFinance().getBankAlias());
//			l5.setStyleName(AON.AON_CSS.aonInnerLabel());
//			paymentInfo.add(l5);
//		}
//	}
	
//	@Deprecated
//	public static FlowPanel getAnchorPanel(IFiscalModel model, String label, String href) {
//		FlowPanel p = new FlowPanel();
//		p.setStyleName(AON.AON_CSS.aonPadding2());
//		Anchor a = new Anchor(label,href,"_blank");
//		a.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
//		a.addStyleName(FiscalModelUtils.getAdministrationIcon(model.getAdministration()));
//		p.add(a);
//		return p;
//	}
	
//	@Deprecated
//	public static ImageResource getStatusImage(FiscalStatus status) {
//		if (status == FiscalStatus.FINISHED) return AON.AON_RESOURCES.aonIconPointLightGreen();
//		if (status == FiscalStatus.BATCHED) return AON.AON_RESOURCES.aonIconPointLightGreen();
//		if (status == FiscalStatus.SENT) return AON.AON_RESOURCES.aonIconPointGreen();
//		if (status == FiscalStatus.BLOCKED) return AON.AON_RESOURCES.aonIconPointRed();
//		if (status  == FiscalStatus.CUSTOMER_CHECK) return AON.AON_RESOURCES.aonIconPointYellow();
//		if (status  == FiscalStatus.MISSING)	return AON.AON_RESOURCES.aonIconQuestion();
//		return AON.AON_RESOURCES.aonIconPointOrange();
//	}
//	@Deprecated
//	public static String getStatusIconStyle(FiscalStatus status) {
//		if (status == FiscalStatus.FINISHED)	return AON.AON_CSS.aonIconPointLightGreen();
//		if (status == FiscalStatus.BATCHED)		return AON.AON_CSS.aonIconPointLightGreen();
//		if (status  == FiscalStatus.BLOCKED )	return AON.AON_CSS.aonIconPointRed();
//		if (status  == FiscalStatus.SENT )		return AON.AON_CSS.aonIconPointGreen();
//		if (status  == FiscalStatus.CUSTOMER_CHECK) return AON.AON_CSS.aonIconPointYellow();
//		if (status  == FiscalStatus.MISSING)	return AON.AON_CSS.aonIconQuestion();
//		return AON.AON_CSS.aonIconPointOrange();
//	}
	
	public static boolean canChangeStatus(final FiscalModel model, final FiscalStatus newStatus) {
		return newStatus.visit( new IFiscalStatusVisitor<Boolean>() {

			private boolean falseIfTransitionFrom(FiscalModel model, FiscalStatus ... statuses) {
				return Arrays.stream(statuses)
						.noneMatch( st -> st == model.getStatus() );
			}
			
			@Override 
			public Boolean visitPending() {
				return model.getStatus() != FiscalStatus.PENDING &&
					falseIfTransitionFrom(model, FiscalStatus.BATCHED
							,FiscalStatus.CUSTOMER_REJECTED
							,FiscalStatus.BLOCKED);
			}
			@Override 
			public Boolean visitFinished() {
				return model.getStatus() != FiscalStatus.FINISHED &&
					falseIfTransitionFrom(model
						,FiscalStatus.SENT
						,FiscalStatus.MISSING
						,FiscalStatus.CUSTOMER_CHECK
						,FiscalStatus.CUSTOMER_ACCEPTED
						,FiscalStatus.BATCHED
						,FiscalStatus.BLOCKED);
			}
			@Override 
			public Boolean visitBatched() {
				return model.getStatus() != FiscalStatus.BATCHED &&
					falseIfTransitionFrom(model, FiscalStatus.BLOCKED);
			}
			@Override 
			public Boolean visitBlocked() {
				return model.getStatus() != FiscalStatus.BLOCKED &&
					falseIfTransitionFrom(model, FiscalStatus.BATCHED);
			}

			@Override 
			public Boolean visitSent() {
				return model.getStatus() != FiscalStatus.SENT &&
					falseIfTransitionFrom(model
						,FiscalStatus.MISSING
						,FiscalStatus.PENDING
						,FiscalStatus.BATCHED
						,FiscalStatus.BLOCKED
						,FiscalStatus.CUSTOMER_CHECK
						,FiscalStatus.CUSTOMER_REJECTED);
			}

			@Override 
			public Boolean visitMissing() {
				return model.getStatus() != FiscalStatus.MISSING &&
					falseIfTransitionFrom(model, FiscalStatus.BATCHED,FiscalStatus.BLOCKED);
			}
			@Override 
			public Boolean visitCustomerCheck() {
				return model.getStatus() != FiscalStatus.CUSTOMER_CHECK &&
					falseIfTransitionFrom(model
						,FiscalStatus.PENDING
						,FiscalStatus.BATCHED
						,FiscalStatus.BLOCKED
						,FiscalStatus.MISSING
						,FiscalStatus.CUSTOMER_ACCEPTED
						,FiscalStatus.CUSTOMER_REJECTED);
			}
			@Override 
			public Boolean visitCustomerAccepted() {
				return model.getStatus() != FiscalStatus.CUSTOMER_ACCEPTED &&
					falseIfTransitionFrom(model, 
						FiscalStatus.PENDING,
						FiscalStatus.FINISHED,
						FiscalStatus.BATCHED,
						FiscalStatus.BLOCKED,
						FiscalStatus.SENT,
						FiscalStatus.MISSING,
						FiscalStatus.CUSTOMER_REJECTED);
			}
			@Override 
			public Boolean visitCustomerRejected() {
				return model.getStatus() != FiscalStatus.CUSTOMER_REJECTED &&
					falseIfTransitionFrom(model, 
						FiscalStatus.PENDING,
						FiscalStatus.FINISHED,
						FiscalStatus.BATCHED,
						FiscalStatus.BLOCKED,
						FiscalStatus.SENT,
						FiscalStatus.MISSING,
						FiscalStatus.CUSTOMER_ACCEPTED);
						
			}
		});
	}
	
	// Resultado 999,99 · <tipo_declaracion> · Nº de plazos <plazos> · Fecha primer plazo 99/99/9999 · IBAN <iban> <alias_banco> · NRC <nrc>
	public static String getPaymentInfo(FiscalModel mod) {
		// Resultado
		StringBuilder buff = new StringBuilder(AON.MSG.result());
		buff.append(AonStringUtils.SPACE);
		double result = AonNumberUtils.todouble(mod.getDeclarationResult());
		buff.append(AON.FMT.format(result));

		// Tipo Declaración
		if (mod.getDeclarationResultType() != null) {
			buff.append(AonStringUtils.SPACE);			
			buff.append(AonStringUtils.BULLET);
			buff.append(AonStringUtils.SPACE);
			buff.append(mod.getDeclarationResultType().getDescription());
		}
		
		// Solicitud de Aplazamiento: Número de Plazos y Fecha Primer Plazo
		if (mod.getDeclarationResultType() != null && mod.getDeclarationResultType() == FiscalModelDeclarationType.DEFERRAL) {
			if (mod.getPlazos() != 0) {
				buff.append(AonStringUtils.SPACE);
				buff.append(AonStringUtils.BULLET);
				buff.append(AonStringUtils.SPACE);			
				buff.append("N\u00BA de Plazos ");
				buff.append(mod.getPlazos());
			}
			if (AonStringUtils.isNotEmpty(mod.getFechaPlazo())) {
				buff.append(AonStringUtils.SPACE);
				buff.append(AonStringUtils.BULLET);
				buff.append(AonStringUtils.SPACE);			
				buff.append("Fecha Primer Plazo ");
				buff.append(mod.getFechaPlazo());				
			}
		}
		
		// IBAN
		if (mod.getFinance() != null && mod.getFinance().getBankAccount() != null && AonStringUtils.isNotBlank(mod.getFinance().getBankAccount().getIban())) {			
			buff.append(AonStringUtils.SPACE);
			buff.append(AonStringUtils.BULLET);
			buff.append(AonStringUtils.SPACE);
			buff.append("IBAN ");
			buff.append(AonStringUtils.defaultString(mod.getFinance().getBankAccount().getIban()));
			buff.append(AonStringUtils.SPACE);
			buff.append(AonStringUtils.defaultString(mod.getFinance().getBankAlias()));
		}
		
		// NRC
		if (AonStringUtils.isNotBlank(mod.getNrc())) {
			buff.append(AonStringUtils.SPACE);
			buff.append(AonStringUtils.BULLET);
			buff.append(AonStringUtils.SPACE);			
			buff.append("NRC ");
			buff.append(mod.getNrc());
		}
		
		return buff.toString();
	}
	

}


