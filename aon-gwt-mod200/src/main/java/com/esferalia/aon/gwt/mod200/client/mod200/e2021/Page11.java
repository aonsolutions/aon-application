// LIQUIDACION (IV): OTRAS DEDUCCIONES, CUOTA LIQUIDA POSITIVA
package com.esferalia.aon.gwt.mod200.client.mod200.e2021;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.mod200.client.mod200.e2021.Model2002021.Model200PageCallback;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN082Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN1040Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN1041Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN565Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN565_1Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN565_2Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN584Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN585Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN588Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN590Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Key;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class Page11 extends PageAbs {

	private static final String[] HEADERS_1 = new String[] {	
			null,
			AON.MSG.pendingDeduction(),
			AON.MSG.current(),
			AON.MSG.futurePending()
	};
	
	private static final String[] HEADERS_3 = new String[] {
			null,
			AON.MSG.deductionTaxablebase(),
			"Importe generado/pendiente al principio del periodo",
			AON.MSG.liquiMsg31(),
			AON.MSG.pendingAmount()
	};
	
	private static final String[] HEADERS_588 = new String[] {	
			"Deducciones para incentivar determinadas actividades (***)",
			AON.MSG.pendingDeduction(),
			AON.MSG.current(),
			AON.MSG.futurePending()
	};
	
	private static final String[] HEADERS_082 = new String[] {
			"Deducciones I+D+i excluidas de l\u00EDmite. Opci\u00F3n art. 39.2 LIS (**)",
			AON.MSG.pendingDeduction(),
			AON.MSG.reducedDeduction(),
			AON.MSG.current(),
			"Importe abonado por insuficiencia de cuota",
			"Deducci\u00F3n resto del grupo"
	};
	
	private static final String FOOTER_1 = "(*) S\u00F3lo debe cumplimentarse si tiene deducciones pendientes de aplicar correspondientes a un per\u00EDodo impositivo anterior iniciado en 2021.";
	private static final String FOOTER_588_1 = "(***) Excepto deducciones por producciones cinematogr\u00E1ficas extranjeras (art. 36.2 LIS) que se declaran en las casillas [01039] de la p\u00E1g. 14 y, en su caso, en la casilla [01042] de la p\u00E1g. 14 bis.";
	private static final String FOOTER_588_2 = "(****) Programas cuya vigencia se inicia a partir de 2022: S\u00F3lo debe cumplimentarse esta fila si la entidad tiene un per\u00EDodo impositivo que no coincida con el a\u00F1o natural y ha realizado gastos con derecho a deducci\u00F3n a partir de 2022.";
	private static final String FOOTER_082 = "(**) Entre otros requisitos, ser\u00E1 necesario que transcurra, al menos, uno a\u00F1o desde la finalizaci\u00F3n del per\u00EDodo impositivo en que se gener\u00F3 la deducci\u00F3n, sin que la misma haya sido objeto de aplicaci\u00F3n.";
	
	private FlowPanel filmPanel;

	public Page11( Model200PageCallback callback ) {
		super(callback);
	}
	
	@Override
	protected void initializeTable() {
		
		basePanel.clear();	
		
		basePanel.add(getTitle(AON.MSG.otherDeductions()));
		
		FlexTable table = addTable();
		
		int row = 0;
		for (final Mod2002021Key key : Mod2002021Constants.LIQUIDATION_IV_KEYS) {
			row = paintKey(table,key,row);
			if (callback.getMod200Object().isVisible(key)) {
				if (key == Mod2002021Key.BN585) {
					row = paintKeyBreakdownLink(table, row, Mod2002021Key.BN585, Mod2002021BN585Key.values(), HEADERS_1, FOOTER_1);
				} 
				if (key == Mod2002021Key.BN584) {
					row = paintKeyBreakdownLink(table, row, Mod2002021Key.BN584, Mod2002021BN584Key.values(), HEADERS_1, FOOTER_1);
				} 
				if (key == Mod2002021Key.BN588) {
					row = paintKeyBreakdownLink(table,row,Mod2002021Key.BN588,Mod2002021BN588Key.values(), HEADERS_588, FOOTER_1, FOOTER_588_1, FOOTER_588_2);
				} 
				if (key == Mod2002021Key.BN082) {
					row = paintKeyBreakdownLink(table, row, Mod2002021Key.BN082, Mod2002021BN082Key.values(), HEADERS_082, FOOTER_1, FOOTER_082);
				}
				if (key == Mod2002021Key.BN565) {
					row = paintKeyBreakdownLinkBN565(table, row);
				}
				if (key == Mod2002021Key.BN590) {
					row = paintKeyBreakdownLink(table,row,Mod2002021Key.BN590,Mod2002021BN590Key.values(),HEADERS_1, FOOTER_1);
				}
				if (key == Mod2002021Key.BN1040) {
					row = paintKeyBreakdownLink(table,row,Mod2002021Key.BN1040,Mod2002021BN1040Key.values(),HEADERS_3, FOOTER_1);
				}
				if (key == Mod2002021Key.BN1041) {
					row = paintKeyBreakdownLink(table,row,Mod2002021Key.BN1041,Mod2002021BN1041Key.values(),HEADERS_3, FOOTER_1);
				}				
				if (key == Mod2002021Key.BN1039) {
					FlexTable table2 = new FlexTable();
					table2.setWidth("100%");
					table2.setCellSpacing(0);
					table2.getColumnFormatter().setWidth(1, "150px");
					table2.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());					
					paintKey(table2,Mod2002021Key.BN1039M,0);
					table.setWidget(row, 0, table2);
					row++;
				}				
				if (key == Mod2002021Key.BN2314) {
					FlexTable table2 = new FlexTable();
					table2.setWidth("100%");
					table2.setCellSpacing(0);
				   	table2.getColumnFormatter().setWidth(1, "150px");
					table2.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());					
					paintKey(table2,Mod2002021Key.BN2314M,0);
					table.setWidget(row, 0, table2);
					row++;
				}
				
			}
		}
		
		// Información adicional producciones cinematográficas españolas y espectáculos en vivo
		filmPanel = new FlowPanel();		
		filmPanel.addStyleName(AON.CSS.aonPaddingBottom());
		basePanel.add(filmPanel);
		paintFilmPanel();
		
	}
	
	private void paintFilmPanel() {
		
		otherInputs.clear();
		filmPanel.clear();
		
		filmPanel.add(getTitle("Informaci\u00F3n adicional producciones cinematogr\u00E1ficas espa\u00F1olas y espect\u00E1culos en vivo"));
		paintLabel(filmPanel, "Los contribuyentes que participen en la financiaci\u00F3n de producciones cinematogr\u00E1ficas espa\u00F1olas y espect\u00E1culos en vivo de artes esc\u00E9nicas y musicales (arts. 36.1 y 3 LIS y art. 39.7 LIS) consignar\u00E1n, a continuaci\u00F3n, el NIF del contribuyente que realiza la producci\u00F3n o espect\u00E1culo.", false);
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.setWidth("30%");
		tab.addStyleName(AON.CSS.aonBlockCenter());
		filmPanel.add(tab);
		
		tab.addRow()
			.addCell( new Label("NIF Contribuyente"),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth150())			
			.addCell( new Label(""),AON.CSS.aonBold(),AON.CSS.aonBorderBottom(),AON.CSS.aonWidth20());
		
		for (int i = 0; i < callback.getMod200Object().getMod200().getFilmProductions().size(); i++) {
			final int idx = i;
			
			AonDocumentTextBox document = new AonDocumentTextBox();
			document.setMaxLength(9);
			document.setValue(callback.getMod200Object().getMod200().getFilmProductions().get(idx));
			document.addValueChangeHandler(event -> {
				callback.getMod200Object().getMod200().getFilmProductions().set(idx, document.getValue());
				callback.markAsDirty();
			});
			otherInputs.add(document);
			
			// Boton borrar linea
			AonTableButton deleteButton = new AonTableButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(event -> {
				callback.getMod200Object().getMod200().getFilmProductions().remove(idx);
				paintFilmPanel();
				callback.markAsDirty();
			});
			otherInputs.add(deleteButton);

			tab.addRow()
				.addCell(document)
				.addCell(deleteButton);
			
		}
		
		// Botón añadir 
		AonTableButton addButton = new AonTableButton(AON.MSG.newAction(),AON.CSS.aonIconAdd());
		addButton.addClickHandler(event -> {
			callback.getMod200Object().getMod200().getFilmProductions().add(new String());
			paintFilmPanel();
		});
		tab.addRow().addCell(addButton);
		otherInputs.add(addButton);

	}
	
	private int paintKeyBreakdownLinkBN565(final FlexTable tab, int row) {
		
		final int boxRow = row-1;
		final int boxCell = 1;
		
		FlowPanel panel  = (FlowPanel) tab.getWidget( boxRow , boxCell );
		panel.addStyleName(AON.AON_CSS.aonNowrap());
		
		Button breakdown = new Button();		
		breakdown.setStyleName(AON.AON_CSS.aonIconModel());
		breakdown.addStyleName(AON.AON_CSS.aonBorderNone());
		breakdown.addStyleName(AON.AON_CSS.aonCursorPointer());
		breakdown.addStyleName(AON.AON_CSS.aonMarginRight());
		breakdown.setTitle(AON.MSG.breakdown());		 
		panel.insert(breakdown,0);
		
		final FlowPanel container = new FlowPanel();		
		container.addStyleName(AON.CSS.aonPaddingBottom());
		container.setVisible(false);
		final String backgroundColor = "#E0FFFF";
		
		String[] headers = new String[] {
				null,
				AON.MSG.pendingDeduction(),
				AON.MSG.current(),
				AON.MSG.futurePending()
		};
		
		headers[0] = "DONACIONES DE CARACTER GENERAL";
		paintKeyBreakdownLinkContainer(container, Mod2002021Key.BN565, Mod2002021BN565_1Key.values(), headers, FOOTER_1);
		headers[0] = "DONACIONES PARA ACTIVIDADES PRIORITARIAS DE MECENAZGO Y OTRAS CON DERECHO A DEDUCCION INCREMENTADA";
		paintKeyBreakdownLinkContainer(container, Mod2002021Key.BN565, Mod2002021BN565_2Key.values(), headers, FOOTER_1);
		headers[0] = "TOTAL DEDUCCIONES A ENTIDADES SIN FINES DE LUCRO (LEY 49/2002)";
		headers[2] = "";
		paintKeyBreakdownLinkContainer(container, Mod2002021Key.BN565, Mod2002021BN565Key.values(), headers, null);
		
		FlexTable table = new FlexTable();
		table.addStyleName(AON.CSS.aonPaddingTop());
		addHeaderCell(table, 0, 0, "BASE DE LA DEDUCCION POR DONACIONES A ENTIDADES SIN FINES DE LUCRO DEL PERIODO IMPOSITIVO");
		table.getFlexCellFormatter().setColSpan(0, 0, 2);
		paintKey(table, Mod2002021Key.BN974, 1);		
		container.add(table);
				
		tab.setWidget(row, 0, container);
		tab.getFlexCellFormatter().setColSpan(row, 0, tab.getCellCount(boxRow)); 
		
		breakdown.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				container.setVisible( !container.isVisible() );
				for ( int i = 0 ; i < tab.getCellCount(boxRow); i++) {
					tab.getCellFormatter().getElement(boxRow , i).getStyle().setBackgroundColor(
							container.isVisible()?backgroundColor:"#FFFFFF");	
				}
				container.getElement().getStyle().setBackgroundColor(
						container.isVisible()?backgroundColor:"#FFFFFF");
			}
			
		});
		
		return ++row;
	}
	
	private void paintKeyBreakdownLinkContainer(FlowPanel container, Mod2002021Key breakdownKey, IMod200KeysProvider[] keysProvider, String[] headers, String footernote) {
		
		FlexTable tableDetail = getFlexTable(container, headers);
		tableDetail.addStyleName(AON.CSS.aonPaddingTop());
		
		int r = 1;
		int col = 0;		
		for (IMod200KeysProvider key : keysProvider) {
			boolean paintDesc = true;
			col = 1;
			for (final IMod200Key k : key.getKeys() ) {
				// La casilla del desglose no la pintamos porque sino se duplicarian
				// las casillas en la pantalla y tal y como está montado ahora el 
				// repintado de todas las casillas, al recalcular solo se pinta una de las 
				// casillas, si ambas casillas son la misma
				if (k != null && k != breakdownKey) {
					if (paintDesc) {
						Label desc = new Label(key.getDescription());			
						tableDetail.setWidget(r, 0, desc);
						desc.setStyleName(AON.AON_CSS.aonMarginLeft());
						if ("Total".equals(key.getDescription()))
							desc.addStyleName(AON.AON_CSS.aonBold());
						tableDetail.getFlexCellFormatter().setStyleName(r, 0, AON.AON_CSS.aonFiscalBorderBottom());
						paintDesc = false;
					}
					paintKeyField(tableDetail, k, r, col, 9, false);					
				}
				++col;
			}
			if (!paintDesc)
				++r;
		}
		
		// Notas al pie
		if (footernote != null)
			paintFooterNote(container, footernote);
		
	}
	
}
