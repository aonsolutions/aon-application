import { AonElement } from "./AonElement.js";
import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, TAG } from "../environments/environments.js";
import { AonIconButton } from "./aon-icon-button.js";
import { AonCheckbox } from "./aon-checkbox.js";
import { AonDialogMenu } from "./aon-dialog-menu.js";
import { AonIcon } from "./aon-icon.js";
import { AonDateUtils } from "../modules/utils/AonDateUtils.js";
import * as LS from "../services/localStorageService.js";

export class AonTable extends AonElement {
  columns;
  selected;
  selectedTr;
  selectedAll;

  checkFetch;
  isFetchingData;
  
  TABLE;
  THEADER;
  TBODY;

  get id() {
    return this.getAttribute(CONSTANT.ID);
  }

  set id(id) {
    this.setAttribute(CONSTANT.ID, id);
  }

  get selectable() {
    return this.getAttribute("selectable");
  }

  set selectable(selectable) {
    this.setAttribute("selectable", selectable);
  }

  constructor(checkFetch) {
    super();
    this.checkFetch = checkFetch;
    this.isFetchingData = false;
  }

  connectedCallback() {
    this.initialize();
    let aonDialogM = new AonDialogMenu();
    aonDialogM.id = this.getId()+"aonDialogAddOption";
    this.appendChild(aonDialogM);
    
    let table = this.createElement(TAG.TABLE);
    table.className = "aonTable";
    table.id = this.TABLE;
    this.appendChild(table);

    let thead = this.createElement("thead");
    table.appendChild(thead);
    let tr = this.createElement(TAG.TR);
    tr.id = this.THEADER;
    thead.appendChild(tr);

    let tbody = this.createElement("tbody");
    tbody.id = this.TBODY;
    table.appendChild(tbody);

    if (this.hasAttribute("selectable")){
      this.paintCheckboxHeader();
    }
/*
    if (
      localStorage.getItem("aon_solutions") === undefined ||
      localStorage.getItem("aon_solutions") === null
    ) {
      if (localStorage.getItem("aon_application_top") == 138) {
        tbody.style.height = `calc(100vh - 186px)`;
      } else if (localStorage.getItem("aon_application_top") == 102) {
        tbody.style.height = `calc(100vh - 150px)`;
      } else if (localStorage.getItem("aon_application_top") == 130) {
        tbody.style.height = `calc(100vh - 178px)`;
      }
    }
*/
    if(this.checkFetch){
      tbody.addEventListener("scroll", ({target}) => {
        const scrollTop = target.scrollTop;
        const offsetHeight = target.offsetHeight;
        const physicalSize = target.scrollHeight;
        const maxScrollPosition = physicalSize - offsetHeight;
  
        if (scrollTop >= maxScrollPosition && !this.isFetchingData) {
          this.isFetchingData = true;
          this.dispatchEvent(new CustomEvent(EVENT.MORE));
        }
      });
    } else {
      tbody.addEventListener("scroll", ({target}) => {
        const scrollTop = target.scrollTop;
        const offsetHeight = target.offsetHeight;
        const physicalSize = target.scrollHeight;
        const maxScrollPosition = physicalSize - offsetHeight;
  
        if (scrollTop >= maxScrollPosition ) {
          this.dispatchEvent(new CustomEvent(EVENT.MORE));
        }
      });
    }
  }

  setFetchingData(fetching){
    this.isFetchingData = fetching;
  } 

  initialize() {
    this.columns = [];
    this.TABLE = this.id + "Table";
    this.THEADER = this.id + "TableHeader";
    this.TBODY = this.id + "TableBody";
    this.selected = [];
    this.selectedAll = false;
  }

  deselectAll() {
    document.querySelectorAll("aon-checkbox").forEach((item, i) => {
      if (item.getValue()) {
        let it = this.getElement(item.id + "Input");
        it.click();
      }
    });
  }

  addButton(icon, fn){
  }

  paintCheckboxHeader(){
    const idCheckBox = this.getId()+"checkboxHeader";
    let header = this.getElement(this.THEADER);
    if( !this.getElement(idCheckBox)){
      let th = this.createElement(TAG.TH);
      th.id = idCheckBox;
      header.appendChild(th);
      
      let aonCheckbox = new AonCheckbox();
      aonCheckbox.id = "aonTableAllSelection";
      th.appendChild(aonCheckbox);

      aonCheckbox.addEventListener(EVENT.CHANGE, () => {
        const checked = aonCheckbox.isChecked();
        this.selectedAll = checked;

        document.querySelectorAll("aon-checkbox").forEach((item) => {
          if (item.isChecked() != checked) {
            let it = this.getElement(item.id + "Input");
            it.click();
          }
        });
      });
    }
  }

  addColumnObject(column) {
    this.addColumn(column.name, column.type, column.id, column.width, column.textAlign);
  }

  addColumn(name, type, id, width, textAlign) {
    if (this.hasAttribute("selectable")) {
      this.paintCheckboxHeader();
    }
    let header = this.getElement(this.THEADER);
    let th = this.createElement(TAG.TH);
    th.innerHTML = name;
    this.columns.push({ name, type, id, width, textAlign });

    header.appendChild(th);
  }

  addColumnIcon({name, title, type, id, width}, fn) {
    let header = this.getElement(this.THEADER);
    let th = this.createElement(TAG.TH);
    let aonIconB = new AonIconButton();
    aonIconB.id = this.getId()+"Back";
    aonIconB.icon = name;
    aonIconB.title = title;
    aonIconB.noHover = "true";
    th.appendChild(aonIconB);
    this.columns.push({ name, type, id, width });
    header.appendChild(th);
    let iconBack = this.getElement(`${this.getId()}Back`);
    if(iconBack) {
      iconBack.addEventListener(EVENT.CLICK, e => fn(e))
    }
  }

  addRow(value, fn, contextMenu) {
    let body = this.getElement(this.TBODY);
    if (!body) return true;
    let tr = this.createElement(TAG.TR);
    // tr.id = Math.random().toString(36).substring(7);
    tr.className ="aonTableTr";

    if(this.selectedColor){
      tr.addEventListener(EVENT.CLICK, () => this.addBackgroundTr(tr, "#d3e3fd"));
    }

    body.appendChild(tr);
    let checkBoxId = `aaa${body.children.length}`;
    if (this.hasAttribute("selectable")) {
      let tdCheckBox = this.createElement(TAG.TD);
      let aonCheckbox = new AonCheckbox();
      aonCheckbox.id = checkBoxId;
      aonCheckbox.addEventListener(EVENT.CHANGE, () => {
        if (aonCheckbox.isChecked()) {
          if(!this.selected.includes(value))
            this.selected.push(value);
          tr.className = "aonTableTr aonTableTrChecked";
        } else {
          tr.classList.remove("aonTableTrChecked");
          this.selected.forEach((item, i) => {
            if (item == value) {
              this.selected.splice(i, 1);
            }
          });
        }
        this.dispatchEvent(new CustomEvent(EVENT.SELECT));
      });
      tdCheckBox.appendChild(aonCheckbox);
      tr.appendChild(tdCheckBox);
    }

    this.columns.forEach((item, i) => {
      let td = this.createElement(TAG.TD);
      let id = item.id;
      td.setAttribute('data-label', item.name);

      if ("option" === id && value[id]) {
        let aonIconB = new AonIconButton();
        aonIconB.id = this.getId()+"IconOption";
        aonIconB.icon = MATERIAL_ICONS.MORE_VERT;
        td.appendChild(aonIconB);
        td.addEventListener(EVENT.CLICK, () => this.getOptions(tr, td, value[id]));
      } 
      // else if("icon" === item.type && value[id]) {
      //   let icon = this.createElement(TAG.I);
      //   icon.id = this.getId() + "Icon";
      //   icon.className = value.icon_class || "material-icons";
      //   icon.innerHTML = value[id];
      //   icon.title = value.icon_title;
      //   td.appendChild(icon);
      //   if(value.fn){
      //     td.addEventListener(EVENT.CLICK, value.fn);
      //   }
      // } else if("aonIcon" === item.type && value[id]) {
      //   let aonIcon = new AonIcon();
      //   aonIcon.id = this.getId()+ "AonIcon";
      //   aonIcon.icon = value[id];
      //   td.appendChild(aonIcon);
      //   if(value.fn){
      //     td.addEventListener(EVENT.CLICK, value.fn);
      //   }
      // }
      else if("icon" === item.type && value[id] || "aonIcon" === item.type && value[id]) {
        let aonIcon = new AonIcon();
        aonIcon.id = this.getId()+ "AonIcon";
        aonIcon.icon = value[id];
        if(value.icon_title)
          aonIcon.title = value.icon_title;
        td.appendChild(aonIcon);
        if(value.fn){
          td.addEventListener(EVENT.CLICK, value.fn);
        }
      } else if("icons" === item.type && value[id]) {
        let span = this.createSpan(this.getId() + 'Icons');
        value[id].forEach((icon,i) => {
          let icon2 = new AonIcon();
          icon2.id = this.getId() + "Icon" + i;
          icon2.icon = icon.icon;
          icon2.title = icon.title;
          if(icon.icon === "qr_code_2"){
            icon2.color = icon.color;
          }
          // let icon2 = this.createElement(TAG.I);
          // icon2.id = this.getId() + "Icon" + i;
          // icon2.className = icon.class || "material-icons";
          // icon2.classList.add("aonTableRowIcon2");
          // icon2.innerHTML = icon.icon;
          // icon2.title = icon.title;
          if(icon.fn) icon2.addEventListener(EVENT.CLICK, icon.fn);
          span.appendChild(icon2);
        });
        td.appendChild(span);
      } else if(item.type && item.type === "list" ) {
        let list = value[id];
        let ulList = this.createElement(TAG.UL);
        td.appendChild(ulList);
        td.addEventListener(EVENT.CLICK, fn);

        list.forEach(d => {
          let iconHtml = d.icon ?  `<i class="material-icons aonVerticalMiddle">${d.icon}</i>`: "*";
          let li = this.createElement(TAG.LI);
          li.innerHTML = iconHtml+" "+d.name;
          if(d.fn){
            li.addEventListener(EVENT.CLICK, d.fn);
          }
          ulList.appendChild(li);
        });
      } else if(item.type && item.type ==="html") {
        td.appendChild(value[id]);
        td.title = td.textContent.trim();
        td.addEventListener(EVENT.CLICK, fn);
        if (contextMenu) {
          td.addEventListener("contextmenu", () => {
            let cb = this.getElement(checkBoxId + "Input");
            if(cb && !cb.checked){
              this.deselectAll();
              cb.click();
            } 
          });
          td.addEventListener("contextmenu", contextMenu);
        }
      } else if(item.type && (item.type ==="date" || item.type ==="creation_date")) {
        const dateRegex = /\d{2,4}\-\d{1,2}\-\d{1,2}(?:T.*)?/;
        const dateValue = value[id] !== undefined ? value[id] : "";
        let val = "";
        try {
          if (dateValue && (dateValue instanceof Date)) {
            val = AonDateUtils.formatDate(dateValue);
          } else if (dateValue && dateRegex.test(dateValue)) {
            let date = new Date(dateValue);
            val = AonDateUtils.formatDate(date);
          } else {
            val = dateValue;
          }
        } catch (error) {
          val = dateValue;
        }
        td.innerHTML = val;
        td.title = val;
        td.addEventListener(EVENT.CLICK, fn);
        if (contextMenu) {
          td.addEventListener("contextmenu", () => {
            let cb = this.getElement(checkBoxId + "Input");
            if(cb && !cb.checked){
              this.deselectAll();
              cb.click();
            } 
          });
          td.addEventListener("contextmenu", contextMenu);
        }
      } 
      else if(item.type && item.type ==="number") {
        td.innerHTML = value[id] !== undefined? value[id] : "";
        td.title = value[id] !== undefined ? value[id] : "";
        // td.style.textAlign = "right";
        // if(value[id] !== undefined && value[id].includes('-')){
        //   td.style.color = "green";
        // }
        td.addEventListener(EVENT.CLICK, fn);
      } else {
        td.innerHTML = value[id] !== undefined ? value[id] : "";
        td.title = value[id] !== undefined ? value[id] : "";
        td.addEventListener(EVENT.CLICK, fn);
        if (contextMenu) {
          td.addEventListener("contextmenu", () => {
            let cb = this.getElement(checkBoxId + "Input");
            if(cb && !cb.checked){
              this.deselectAll();
              cb.click();
            } 
          });
          td.addEventListener("contextmenu", contextMenu);
        }
      }
      tr.appendChild(td);
    });

    return tr;
  }

  removeRows() {
    let body = this.getElement(this.TBODY);
    if (body) body.innerHTML = "";
  }

  removeColumns() {
    this.columns = [];
    this.clearSelected();
    let body = this.getElement(this.THEADER);
    if (body) body.innerHTML = "";
  }

  clearSelected(){
    this.selected = [];
  }

  getId() {
    return this.getAttribute("id");
  }

  getOptions(tr, td, options) {
    const top = td.getBoundingClientRect().top;
    const left = td.getBoundingClientRect().left;
    let d = this.getElement(this.getId() + "aonDialogAddOption");
    options = options.map(({ icon, aonIcon, name, fn }) => {
      return {
        aonIcon,
        icon,
        name,
        fn: () => fn(tr),
      };
    });

    d.setMenuOptions(options, top, left);
    d.open();
  }

  empty(message = 'No hay datos disponibles.') {
    let table = this.getElement(this.TABLE);
    if (!table) return;
    
    let body = this.getElement(this.TBODY);
    if (!body) {
      body = document.createElement("tbody");
      table.appendChild(body);
    } else {
      body.innerHTML = ""; 
    }

    let columnsCount = table.querySelectorAll("th").length || 1;
    let tr = document.createElement("tr");
    let td = document.createElement("td");

    td.colSpan = columnsCount;
    td.textContent = message;
    
    tr.appendChild(td);
    body.appendChild(tr);
  }

  addBackgroundTr(tr, color){
    if(tr && this.selectedTr !== tr){
      this.selectedTr = tr;
    } else this.selectedTr = undefined;
  }

  loading(b) {
    let body = this.getElement(this.TBODY);
    let id = this.TBODY+"Loading";
    let tr = this.getElement(id);
    if (b && !tr) {
      tr = this.createElement(TAG.TR);
      tr.id = id;
      body.appendChild(tr);

      let load = this.createElement(TAG.DIV);
      load.classList.add(CSS.AON_ICON_CONTAINER);
      tr.appendChild(load);

      let icon = this.createElement(TAG.I);
      icon.classList.add(CSS.AON_LOADER);
      load.appendChild(icon);

    } else if (!b && tr) {
      tr.remove();
    }
  }
}
if(!window.customElements.get('aon-table')){
  window.customElements.define("aon-table", AonTable);
}
