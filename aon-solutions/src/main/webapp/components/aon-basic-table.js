import { AonElement } from "./AonElement.js";

import { CONSTANT, CSS, TAG } from '../environments/environments.js';
 
export class AonBasicTable extends AonElement {

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  TABLE;
  TABLE_ROW;

  rows;

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.clear();
    this.build();
  }

  initialize() {
    this.TABLE = this.id + CONSTANT.TABLE.initCap();
    this.TABLE_ROW = this.TABLE + CONSTANT.ROW.initCap();
    this.rows = 0;
  }

  build() {
    let table = this.createElement(TAG.TABLE);
    table.id = this.TABLE;
    table.className = CSS.AON_WIDTH_ALL;
    this.appendChild(table);
  }

  addCell(elem, colspan, row) {
    row = row || this.rows;
    colspan = colspan || 1;
    let tr = this.getElement(this.TABLE_ROW + row);
    let td = this.createElement(TAG.TD);
    td.colspan = colspan;
    td.appendChild(elem);
    tr.appendChild(td);
  }

  addRow() {
    rows = rows + 1;
    let tr = this.createElement(TAG.TR);
    tr.id = this.TABLE_ROW + this.rows;
    return this.rows;
  }

  removeRow(row){
    let tr = this.getElement(this.TABLE_ROW + row);
    tr.parentNode.removeChild(tr);
  }

  removeRows() {
    this.clear();
    this.build();
  }
}
if(!window.customElements.get(TAG.AON_BASIC_TABLE)){
  window.customElements.define(TAG.AON_BASIC_TABLE, AonBasicTable);
}
