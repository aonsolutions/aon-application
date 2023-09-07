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
    this.TABLE_ROW = this.id + CONSTANT.ROW.initCap();
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
    let tr = this.getElement(this.TABLE_ROW + row);
    let td = this.createElement(TAG.TD);
    td.colSpan = colspan || '1';
    
    td.appendChild(elem);
    tr.appendChild(td);
    return td;
  }

  getCell(row, idx) {
    let tr =  this.getElement(this.TABLE_ROW + row);
    if(tr){
      let tds = [...tr.querySelectorAll("td")];
      if(tds && tds.length){
        return tds.find((x,i)=> i===idx);
      }
    }

    return null;
  }

  addRow() {
    this.rows = this.rows + 1;
    let tr = this.createElement(TAG.TR);
    tr.id = this.TABLE_ROW + this.rows;
    this.getElement(this.TABLE).appendChild(tr);
    return this.rows;
  }

  getRow(i) {
    return this.getElement(this.TABLE_ROW + i);
  }

  getRowsCount(){
    return this.querySelectorAll(`[id*='${this.TABLE_ROW}']`).length;
  }

  removeRow(row){
    let tr = this.getElement(this.TABLE_ROW + row);
    tr.parentNode.removeChild(tr);
    this.rows = this.rows - 1;

  }

  removeRows() {
    this.clear();
    this.initialize();
    this.build();
  }
}
if(!window.customElements.get(TAG.AON_BASIC_TABLE)){
  window.customElements.define(TAG.AON_BASIC_TABLE, AonBasicTable);
}
