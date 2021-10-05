package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IAdministrationVisitor;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalStatusVisitor;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

public class FiscalModelUtils {
	
	@FunctionalInterface
	private interface IFiscalModelTypeName {
		boolean accept(IFiscalModel mod);
	}
	
	private enum FiscalModelTypeName {
		// ********** MODELO 111 ********** 
		 M111	("111"	,mod -> mod.getModel() == FiscalModelType.M111 && ( mod.isAEAT() || (mod.isMonthPeriod() && (mod.isAraba() || mod.isBizkaia() || mod.isGipuzkoa() ) ) ) ) 
		,M110	("110"	,mod -> mod.getModel() == FiscalModelType.M111 && mod.isQuarterPeriod() && (mod.isAraba() || mod.isBizkaia() || mod.isGipuzkoa() ) )
		,M745	("745"	,mod -> mod.getModel() == FiscalModelType.M111 && mod.isNavarra() && mod.isMonthPeriod() )
		,M715	("715"	,mod -> mod.getModel() == FiscalModelType.M111 && mod.isNavarra() && mod.isQuarterPeriod() )
		// ********** MODELO 115 ********** 
		,M115 	("115"	,mod -> mod.getModel() == FiscalModelType.M115 && !mod.isAraba() && !mod.isNavarra() )
		,M115A 	("115-A",mod -> mod.getModel() == FiscalModelType.M115 && mod.isAraba())
		,M760 	("760"	,mod -> mod.getModel() == FiscalModelType.M115 && mod.isNavarra() && mod.isMonthPeriod() )
		,M759 	("759"	,mod -> mod.getModel() == FiscalModelType.M115 && mod.isNavarra() && mod.isQuarterPeriod() )
		// ********** MODELO 123 ********** 
		,M123 	("123"	,mod -> mod.getModel() == FiscalModelType.M123 && !mod.isNavarra() )
		,M716 	("716"	,mod -> mod.getModel() == FiscalModelType.M123 && mod.isNavarra() )

		// ********** MODELO IVA **********		
		,MF69	("F69",mod -> (mod.getModel() == FiscalModelType.M303_RG || mod.getModel() == FiscalModelType.M303_RS || mod.getModel() == FiscalModelType.M303) && mod.isNavarra() )	
		,M303	("303",mod -> (mod.getModel() == FiscalModelType.M303_RG || mod.getModel() == FiscalModelType.M303_RS || mod.getModel() == FiscalModelType.M303 ) 
				&& (mod.isAEAT() || ((mod.isAraba() || mod.isBizkaia()) && !mod.isLastPeriod()))
		)
		,M300	("300",mod -> (mod.getModel() == FiscalModelType.M303_RG || mod.getModel() == FiscalModelType.M303_RS || mod.getModel() == FiscalModelType.M303) && mod.isGipuzkoa() && mod.isQuarterPeriod() && !mod.isLastPeriod() )	
		,M320	("320",mod -> (mod.getModel() == FiscalModelType.M303_RG || mod.getModel() == FiscalModelType.M303_RS || mod.getModel() == FiscalModelType.M303) && mod.isGipuzkoa() && mod.isMonthPeriod() && !mod.isLastPeriod() )	
		
		// ********** MODELO 390 **********
		,M390	("390",mod -> mod.getModel() == FiscalModelType.M390 
			|| mod.getModel() == FiscalModelType.M390_HF
			|| ( (mod.getModel() == FiscalModelType.M303_RG || mod.getModel() == FiscalModelType.M303_RS || mod.getModel() == FiscalModelType.M303 )
					&& (mod.isAraba() || mod.isBizkaia() || mod.isGipuzkoa()) 
					&& mod.isLastPeriod() )
		)
		
		// ********** MODELO 130 ********** 
		,M130	("130",mod -> mod.getModel() == FiscalModelType.M130)
		// ********** MODELO 131 ********** 
		,M131	("131",mod -> mod.getModel() == FiscalModelType.M131)
		// ********** MODELO 340 ********** 
		,M340	("340",mod -> mod.getModel() == FiscalModelType.M340)
		// ********** MODELO 347 ********** 
		,M347	("347",mod -> mod.getModel() == FiscalModelType.M347)
		// ********** MODELO 349 ********** 
		,M349	("349",mod -> mod.getModel() == FiscalModelType.M349)
		// ********** MODELO 180 ********** 
		,M180	("180",mod -> mod.getModel() == FiscalModelType.M180)
		// ********** MODELO 184 ********** 
		,M184	("184",mod -> mod.getModel() == FiscalModelType.M184)
		// ********** MODELO 190 ********** 
		,M190	("190",mod -> mod.getModel() == FiscalModelType.M190)
		// ********** MODELO 193 ********** 
		,M193	("193",mod -> mod.getModel() == FiscalModelType.M193)
		// ********** MODELO 200 ********** 
		,M200	("200",mod -> mod.getModel() == FiscalModelType.M200)
		// ********** MODELO 202 ********** 
		,M202	("202",mod -> mod.getModel() == FiscalModelType.M202)
		;
		
		private String name;
		private IFiscalModelTypeName accepter;
		
		private FiscalModelTypeName( String name, IFiscalModelTypeName getter) {
			this.name = name;
			this.accepter = getter;
		}
		public String getName() {
			return name;
		}
		private boolean accept(IFiscalModel mod) {
			return this.accepter.accept(mod);
		}
		private static String getName(IFiscalModel mod) {
			for (FiscalModelTypeName f : FiscalModelTypeName.values()) {
				if (f.accept(mod)) return f.getName();
			}
			return null;
		}
	}
	
	public static String getModelName(IFiscalModel fm) {
		String name = FiscalModelTypeName.getName(fm);
		return AonStringUtils.isNotBlank(name)?name:fm.getModel().getName();
	}
	
	public static String getPeriodDescription(IFiscalModel fm) {
		String description = "";
		if (fm.getPeriod() != null) {
			if (fm.getModel() == FiscalModelType.M202) {
				if (fm.getPeriod() == Period.T1)
					description = "1\u00BA Per.";
				if (fm.getPeriod() == Period.T2) 
					description = "2\u00BA Per.";
				if (fm.getPeriod() == Period.T3) 
					description = "3\u00BA Per.";				
			}
			else description = fm.getPeriod().getDescription();			
		}		
		return description;
	}
	
	/**
	 * @deprecated Use com.esferalia.aon.gwt.fiscal.client.model.AonFiscalModelHeader
	 */
	@Deprecated
	public static void paintHeaderTable(SimplePanel headerPanel, IFiscalModel fm) {
		Administration admon = (fm == null?Administration.COMMON_TERRITORY:fm.getAdministration());
		headerPanel.clear();
		headerPanel.setStyleName(AON.AON_CSS.aonWidthAll());
		
		FlexTable headerTable = new FlexTable();
		headerTable.getColumnFormatter().setWidth(0, "55px");
		headerTable.getColumnFormatter().setWidth(1, "70px");
		headerTable.getColumnFormatter().setWidth(2, "auto");
		headerTable.getColumnFormatter().setWidth(3, "75px");
		
		headerTable.setStyleName(AON.AON_CSS.aonFiscalModelTable());
		
		Label image = new Label("");
		image.setStyleName(getAdministrationImage(admon));
		
		headerTable.setWidget(0, 0, image);
		headerTable.getFlexCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderImage());
		headerTable.getFlexCellFormatter().setRowSpan(0, 0, 2);
		
		headerTable.setWidget(0, 1, new Label( FiscalModelUtils.getModelName(fm))); 
		headerTable.getFlexCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		headerTable.getFlexCellFormatter().addStyleName(0, 1, getAdministrationBG(admon));
		headerTable.getFlexCellFormatter().setRowSpan(0, 1, 2);
		
		
		headerTable.setWidget(0, 2, new Label(AON.MSG.fiscalModelDescriptionlong(fm.getModel())));
		headerTable.getFlexCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		headerTable.getFlexCellFormatter().addStyleName(0, 2, getAdministrationBG(admon));
		headerTable.getFlexCellFormatter().setRowSpan(0, 2, 2);
		
		headerTable.setWidget(0, 3, new Label(""+fm.getYear()));
		headerTable.getFlexCellFormatter().setStyleName(0, 3, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(0, 3, getAdministrationBG(admon));
		
		headerTable.setWidget(1, 0, new Label(getPeriodDescription(fm)));
		headerTable.getFlexCellFormatter().setStyleName(1, 0, AON.AON_CSS.aonFiscalModelTableHeaderModel());
		headerTable.getFlexCellFormatter().addStyleName(1, 0, getAdministrationBG(admon));

		headerPanel.setWidget(headerTable);
	}

	@Deprecated
	public static String getAdministrationBG(Administration admon) {
		if (admon == Administration.ALAVA) {
			return AON.AON_CSS.aonFiscalArabaBg();
		} else if (admon == Administration.BIZKAIA) {
			return AON.AON_CSS.aonFiscalBizkaiaBg();
		} else if (admon == Administration.GIPUZKOA) {
			return AON.AON_CSS.aonFiscalGipuzkoaBg();
		} else if (admon == Administration.NAVARRA) {
			return AON.AON_CSS.aonFiscalNavarraBg();
		} 
		return AON.AON_CSS.aonFiscalAeatBg();
	}
	
	@Deprecated
	public static String getAdministrationImage(Administration adm) {
		if (adm == Administration.ALAVA) {
			return AON.AON_CSS.aonArabaHeaderImage();
		} else if (adm == Administration.BIZKAIA) {
			return AON.AON_CSS.aonBizkaiaHeaderImage();
		} else if (adm == Administration.GIPUZKOA) {
			return AON.AON_CSS.aonGipuzkoaHeaderImage();
		} else if (adm == Administration.NAVARRA) {
			return AON.AON_CSS.aonNavarraHeaderImage();
		} 
		return AON.AON_CSS.aonAeatHeaderImage();
	}
	
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

	
	/**
	 * @deprecated Use fillPaymentInfo
	 */
	@Deprecated
	public static <T extends FiscalModel> void paintPaymentInfo(FlowPanel paymentInfo,T mod) {
		paymentInfo.clear();
		paymentInfo.setVisible(mod.isFinished() || mod.isSent());
		InlineLabel l1 = new InlineLabel(AON.MSG.result());
		l1.setStyleName(AON.AON_CSS.aonInnerLabel());
		paymentInfo.add(l1);
		InlineLabel l2 = new InlineLabel(AON.FMT.format(mod.getResult()));
		l2.setStyleName(AON.AON_CSS.aonInnerLabel());
		l2.addStyleName(AON.AON_CSS.aonBold());
		paymentInfo.add(l2);
		if (mod.getDeclarationType() != null) {
			InlineLabel l3 = new InlineLabel(mod.getDeclarationType().getDescription());
			l3.setStyleName(AON.AON_CSS.aonInnerLabel());
			l3.addStyleName(AON.AON_CSS.aonBold());
			paymentInfo.add(l3);
		}
		if (mod.getFinance() != null && mod.getFinance().getBankAccount() != null) {
			InlineLabel l4 = new InlineLabel(mod.getFinance().getBankAccount().getIban());
			l4.setStyleName(AON.AON_CSS.aonInnerLabel());
			l4.addStyleName(AON.AON_CSS.aonBold());
			paymentInfo.add(l4);
			
			InlineLabel l5 = new InlineLabel(mod.getFinance().getBankAlias());
			l5.setStyleName(AON.AON_CSS.aonInnerLabel());
			paymentInfo.add(l5);
		}
	}
	
	@Deprecated
	public static FlowPanel getAnchorPanel(IFiscalModel model, String label, String href) {
		FlowPanel p = new FlowPanel();
		p.setStyleName(AON.AON_CSS.aonPadding2());
		Anchor a = new Anchor(label,href,"_blank");
		a.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		a.addStyleName(FiscalModelUtils.getAdministrationIcon(model.getAdministration()));
		p.add(a);
		return p;
	}
	@Deprecated
	public static ImageResource getStatusImage(FiscalStatus status) {
		if (status == FiscalStatus.FINISHED) return AON.AON_RESOURCES.aonIconPointLightGreen();
		if (status == FiscalStatus.BATCHED) return AON.AON_RESOURCES.aonIconPointLightGreen();
		if (status == FiscalStatus.SENT) return AON.AON_RESOURCES.aonIconPointGreen();
		if (status == FiscalStatus.BLOCKED) return AON.AON_RESOURCES.aonIconPointRed();
		if (status  == FiscalStatus.CUSTOMER_CHECK) return AON.AON_RESOURCES.aonIconPointYellow();
		if (status  == FiscalStatus.MISSING)	return AON.AON_RESOURCES.aonIconQuestion();
		return AON.AON_RESOURCES.aonIconPointOrange();
	}
	@Deprecated
	public static String getStatusIconStyle(FiscalStatus status) {
		if (status == FiscalStatus.FINISHED)	return AON.AON_CSS.aonIconPointLightGreen();
		if (status == FiscalStatus.BATCHED)		return AON.AON_CSS.aonIconPointLightGreen();
		if (status  == FiscalStatus.BLOCKED )	return AON.AON_CSS.aonIconPointRed();
		if (status  == FiscalStatus.SENT )		return AON.AON_CSS.aonIconPointGreen();
		if (status  == FiscalStatus.CUSTOMER_CHECK) return AON.AON_CSS.aonIconPointYellow();
		if (status  == FiscalStatus.MISSING)	return AON.AON_CSS.aonIconQuestion();
		return AON.AON_CSS.aonIconPointOrange();
	}
	
//	private static String getStatusBckColor(FiscalStatus status) {
//		if (status == FiscalStatus.FINISHED)	return AON.AON_CSS.aonIconPointLightGreen();
//		if (status == FiscalStatus.BATCHED)		return AON.AON_CSS.aonIconPointLightGreen();
//		if (status  == FiscalStatus.BLOCKED )	return AON.AON_CSS.aonIconPointRed();
//		if (status  == FiscalStatus.SENT )		return AON.AON_CSS.aonIconPointGreen();
//		return AON.AON_CSS.aonIconPointOrange();
//	}
	@Deprecated
	public static String gettStatusBckColor(FiscalStatus status) {
		if (status == FiscalStatus.FINISHED)	return AON.AON_CSS.aonBgFinished();
		if (status == FiscalStatus.BATCHED)		return AON.AON_CSS.aonBgFinished();
		if (status  == FiscalStatus.BLOCKED )	return AON.AON_CSS.aonBgBlocked();
		if (status  == FiscalStatus.SENT )		return AON.AON_CSS.aonBgSent();
		if (status  == FiscalStatus.MISSING )	return AON.AON_CSS.aonBgMissing();
		return AON.AON_CSS.aonBgPending();
	}
	
	// 	------------------------------------------------------------------------	
	// 	------------------------------------------------------------------------	
	// 	------------------------------------------------------------------------	
	// 	------------------------------------------------------------------------	

	public static <T extends FiscalModel> void fillPaymentInfo(FlowPanel paymentInfo,T mod) {
		paymentInfo.clear();
		paymentInfo.setVisible(mod.isFinished() || mod.isSent());
		InlineLabel l1 = new InlineLabel(AON.MSG.result());
		l1.setStyleName(AON.CSS.aonInnerLabel());
		paymentInfo.add(l1);
		InlineLabel l2 = new InlineLabel(AON.FMT.format(mod.getResult()));
		l2.setStyleName(AON.CSS.aonInnerLabel());
		l2.addStyleName(AON.CSS.aonBold());
		paymentInfo.add(l2);
		if (mod.getDeclarationType() != null) {
			InlineLabel l3 = new InlineLabel(mod.getDeclarationType().getDescription());
			l3.setStyleName(AON.CSS.aonInnerLabel());
			l3.addStyleName(AON.CSS.aonBold());
			paymentInfo.add(l3);
		}
		if (mod.getFinance() != null && mod.getFinance().getBankAccount() != null) {
			InlineLabel l4 = new InlineLabel(mod.getFinance().getBankAccount().getIban());
			l4.setStyleName(AON.CSS.aonInnerLabel());
			l4.addStyleName(AON.CSS.aonBold());
			paymentInfo.add(l4);
			
			InlineLabel l5 = new InlineLabel(mod.getFinance().getBankAlias());
			l5.setStyleName(AON.CSS.aonInnerLabel());
			paymentInfo.add(l5);
		}
	}

	private static class FiscalStatusBackgroundRGB implements IFiscalStatusVisitor<String> {
		@Override public String visitPending() 	{return "LightGray";}
		@Override public String visitFinished() {return "#e3ffab";}
		@Override public String visitBatched() 	{return "#b8dc6f";}
		@Override public String visitBlocked() 	{return "#ff8080";}
		@Override public String visitSent() 	{return "#3EC946";}
		@Override public String visitMissing() 	{return "White";}
		@Override public String visitCustomerCheck() {return "LightYellow";}
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
	
}


