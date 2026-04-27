package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class AonFlexGrid extends FlowPanel {
	
	public AonFlexGrid(String[] columnWidths, String ... style) {
		this(columnWidths);
		for (String st : style) {
			addStyleName(st);
		}
	}
	
	public AonFlexGrid(String[] columnWidths) {
		setStyleName(AON.CSS.aonFlexGrid());
		AonCollectionUtils.stream(columnWidths)
			.reduce((a,b) -> a + " " + b)
			.ifPresent(s -> getElement().getStyle().setProperty("grid-template-columns", s));
	}
	
	public FlowPanel addCell(int colspan) {
		FlowPanel cell = new FlowPanel();
		cell.setStyleName(AON.CSS.aonFlexGridCell());
		if (colspan > 1) {
			cell.getElement().setAttribute("style", "grid-column: span " + colspan);
		}
		add(cell);
		return cell;
	}
	public FlowPanel addCell(int colspan, String ... style ) {
		FlowPanel cell = addCell(colspan);
		for (String st : style) {
			cell.addStyleName(st);
		}
		return cell;
	}
	public AonFlexGrid addCell( Widget widget, String ... style ) {
		return addCell(widget, 1, style);
	}
	public AonFlexGrid addCell( Widget widget, int colspan, String ... style ) {
		addCell(colspan, style).add(widget);
		return this;
	}
	
}
/*
				<!DOCTYPE html>
				<html lang="es">
				<head>
				  <meta charset="UTF-8">
				  <title>Grid 10×10 con colspan - HTML puro</title>
				  <style>
				    body {
				      font-family: monospace;
				      padding: 2rem;
				      background: #fff;
				    }
				
				    #grid {
				      display: grid;
				      grid-template-columns: repeat(10, 52px);
				      grid-template-rows: repeat(10, 52px);
				      border-left: 1px solid #ccc;
				      border-top: 1px solid #ccc;
				    }

				    .gcell {
				      border-right: 1px solid #ccc;
				      border-bottom: 1px solid #ccc;
				      display: flex;
				      align-items: center;
				      justify-content: center;
				      font-size: 11px;
				      color: #B5D4F4;
				      background: #185FA5;
				      user-select: none;
				      box-sizing: border-box;
				    }
				
				    .gcell:hover { background: #3B6D11; color: #C0DD97; }
				    .diag        { background: #993C1D; color: #F5C4B3; }

    .span3 {
      grid-column: span 3;
      background: #534AB7;
      color: #CECBF6;
      font-size: 12px;
    }
    .span3:hover { background: #3C3489; color: #EEEDFE; }
  </style>
</head>
<body>

  <div id="grid">
    <!-- Fila 0 -->
    <div class="gcell diag">0,0</div>
    <div class="gcell span3">(0,1) ×3</div>
    <!-- (0,2) y (0,3) eliminadas por el span -->
    <div class="gcell">0,4</div>
    <div class="gcell">0,5</div>
    <div class="gcell">0,6</div>
    <div class="gcell">0,7</div>
    <div class="gcell">0,8</div>
    <div class="gcell">0,9</div>

    <!-- Fila 1 -->
    <div class="gcell">1,0</div>
    <div class="gcell diag">1,1</div>
    <div class="gcell">1,2</div>
    <div class="gcell">1,3</div>
    <div class="gcell">1,4</div>
    <div class="gcell">1,5</div>
    <div class="gcell">1,6</div>
    <div class="gcell">1,7</div>
    <div class="gcell">1,8</div>
    <div class="gcell">1,9</div>

    <!-- Fila 2 -->
    <div class="gcell">2,0</div>
    <div class="gcell">2,1</div>
    <div class="gcell diag">2,2</div>
    <div class="gcell">2,3</div>
    <div class="gcell">2,4</div>
    <div class="gcell">2,5</div>
    <div class="gcell">2,6</div>
    <div class="gcell">2,7</div>
    <div class="gcell">2,8</div>
    <div class="gcell">2,9</div>

    <!-- Fila 3 -->
    <div class="gcell">3,0</div>
    <div class="gcell">3,1</div>
    <div class="gcell">3,2</div>
    <div class="gcell diag">3,3</div>
    <div class="gcell">3,4</div>
    <div class="gcell">3,5</div>
    <div class="gcell">3,6</div>
    <div class="gcell">3,7</div>
    <div class="gcell">3,8</div>
    <div class="gcell">3,9</div>

    <!-- Fila 4 -->
    <div class="gcell">4,0</div>
    <div class="gcell">4,1</div>
    <div class="gcell">4,2</div>
    <div class="gcell">4,3</div>
    <div class="gcell diag">4,4</div>
    <div class="gcell">4,5</div>
    <div class="gcell">4,6</div>
    <div class="gcell">4,7</div>
    <div class="gcell">4,8</div>
    <div class="gcell">4,9</div>

    <!-- Fila 5 -->
    <div class="gcell">5,0</div>
    <div class="gcell">5,1</div>
    <div class="gcell">5,2</div>
    <div class="gcell">5,3</div>
    <div class="gcell">5,4</div>
    <div class="gcell diag">5,5</div>
    <div class="gcell">5,6</div>
    <div class="gcell">5,7</div>
    <div class="gcell">5,8</div>
    <div class="gcell">5,9</div>

    <!-- Fila 6 -->
    <div class="gcell">6,0</div>
    <div class="gcell">6,1</div>
    <div class="gcell">6,2</div>
    <div class="gcell">6,3</div>
    <div class="gcell">6,4</div>
    <div class="gcell">6,5</div>
    <div class="gcell diag">6,6</div>
    <div class="gcell">6,7</div>
    <div class="gcell">6,8</div>
    <div class="gcell">6,9</div>

    <!-- Fila 7 -->
    <div class="gcell">7,0</div>
    <div class="gcell">7,1</div>
    <div class="gcell">7,2</div>
    <div class="gcell">7,3</div>
    <div class="gcell">7,4</div>
    <div class="gcell">7,5</div>
    <div class="gcell">7,6</div>
    <div class="gcell diag">7,7</div>
    <div class="gcell">7,8</div>
    <div class="gcell">7,9</div>

    <!-- Fila 8 -->
    <div class="gcell">8,0</div>
    <div class="gcell">8,1</div>
    <div class="gcell">8,2</div>
    <div class="gcell">8,3</div>
    <div class="gcell">8,4</div>
    <div class="gcell">8,5</div>
    <div class="gcell">8,6</div>
    <div class="gcell">8,7</div>
    <div class="gcell diag">8,8</div>
    <div class="gcell">8,9</div>

    <!-- Fila 9 -->
    <div class="gcell">9,0</div>
    <div class="gcell">9,1</div>
    <div class="gcell">9,2</div>
    <div class="gcell">9,3</div>
    <div class="gcell">9,4</div>
    <div class="gcell">9,5</div>
    <div class="gcell">9,6</div>
    <div class="gcell">9,7</div>
    <div class="gcell">9,8</div>
    <div class="gcell diag">9,9</div>
  </div>

</body>
</html>*/