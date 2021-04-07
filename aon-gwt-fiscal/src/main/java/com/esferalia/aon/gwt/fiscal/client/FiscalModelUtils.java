package com.esferalia.aon.gwt.fiscal.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonStringUtils;
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
	
	public static FlowPanel getAnchorPanel(IFiscalModel model, String label, String href) {
		FlowPanel p = new FlowPanel();
		p.setStyleName(AON.AON_CSS.aonPadding2());
		Anchor a = new Anchor(label,href,"_blank");
		a.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		a.addStyleName(FiscalModelUtils.getAdministrationIcon(model.getAdministration()));
		p.add(a);
		return p;
	}
	
	public static ImageResource getStatusImage(FiscalStatus status) {
		if (status == FiscalStatus.FINISHED) return AON.AON_RESOURCES.aonIconPointLightGreen();
		if (status == FiscalStatus.BATCHED) return AON.AON_RESOURCES.aonIconPointLightGreen();
		if (status == FiscalStatus.SENT) return AON.AON_RESOURCES.aonIconPointGreen();
		if (status == FiscalStatus.BLOCKED) return AON.AON_RESOURCES.aonIconPointRed();
		if (status  == FiscalStatus.CUSTOMER_CHECK) return AON.AON_RESOURCES.aonIconPointYellow();
		if (status  == FiscalStatus.MISSING)	return AON.AON_RESOURCES.aonIconQuestion();
		return AON.AON_RESOURCES.aonIconPointOrange();
	}
	
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
	
	public static String gettStatusBckColor(FiscalStatus status) {
		if (status == FiscalStatus.FINISHED)	return AON.AON_CSS.aonBgFinished();
		if (status == FiscalStatus.BATCHED)		return AON.AON_CSS.aonBgFinished();
		if (status  == FiscalStatus.BLOCKED )	return AON.AON_CSS.aonBgBlocked();
		if (status  == FiscalStatus.SENT )		return AON.AON_CSS.aonBgSent();
		if (status  == FiscalStatus.MISSING )	return AON.AON_CSS.aonBgMissing();
		return AON.AON_CSS.aonBgPending();
	}
}


