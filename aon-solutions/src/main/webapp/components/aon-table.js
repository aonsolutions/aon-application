import { AonElement } from "./AonElement.js";
import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, TAG } from "../environments/environments.js";
import { AonIconButton } from "./aon-icon-button.js";
import { AonCheckbox } from "./aon-checkbox.js";
import { AonDialogMenu } from "./aon-dialog-menu.js";
import { AonIcon } from "./aon-icon.js";
import { AonDateUtils } from "../modules/utils/AonDateUtils.js";
import * as LS from "../services/localStorageService.js";
import { formatNumber } from "../services/utils.js";


export class AonTable extends AonElement {
  app;
  columns;
  selected;
  selectedAll;

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

  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    let aonDialogM = new AonDialogMenu();
    aonDialogM.id = this.getId()+"aonDialogAddOption";
    this.appendChild(aonDialogM);
    
    let table = this.createElement(TAG.TABLE);
    table.className = "aonTable";
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

    tbody.addEventListener("scroll", ({target}) => {
      const scrollTop = target.scrollTop;
      const offsetHeight = target.offsetHeight;
      const physicalSize = target.scrollHeight;
      const maxScrollPosition = physicalSize - offsetHeight;

      if (scrollTop >= maxScrollPosition) {
        this.dispatchEvent(new CustomEvent(EVENT.MORE));
      }
    });
  }

  initialize() {
    this.columns = [];
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
    let header = this.getElement(this.getId() + "TableHeader");
    if( !this.getElement(idCheckBox)){
      let th = this.createElement(TAG.TH);
      th.id = idCheckBox;
      th.style.width = "5%";
      header.appendChild(th);
      
      let aonCheckbox = new AonCheckbox();
      aonCheckbox.id = "aonTableAllSelection";
      th.appendChild(aonCheckbox);

      aonCheckbox.addEventListener(EVENT.CHANGE, () => {
        const checked = aonCheckbox.isChecked();
        this.selectedAll = checked;

        document.querySelectorAll("aon-checkbox")
        .forEach((item) => {
          if (item.isChecked() != checked) {
            let it = this.getElement(item.id + "Input");
            it.click();
          }
        });
      });
    }

  }

  addColumn(name, type, id, width, textAlign) {
    if (this.hasAttribute("selectable")) {
      this.paintCheckboxHeader();
    }
    let header = this.getElement(this.getId() + "TableHeader");
    let th = this.createElement(TAG.TH);
    th.innerHTML = name;
    th.style.width = width;
    this.columns.push({ name, type, id, width, textAlign });
    header.appendChild(th);
  }

  addColumnIcon({name, title, type, id, width}, fn) {
    let header = this.getElement(this.getId() + "TableHeader");
    let th = this.createElement(TAG.TH);
    let aonIconB = new AonIconButton();
    aonIconB.id = this.getId()+"Back";
    aonIconB.icon = name;
    aonIconB.title = title;
    aonIconB.noHover = "true";
    th.appendChild(aonIconB);
    th.style.width = width;
    this.columns.push({ name, type, id, width });
    header.appendChild(th);
    let iconBack = this.getElement(`${this.getId()}Back`);
    if(iconBack) {
      iconBack.firstChild.style.paddingTop = "15px";
      iconBack.addEventListener(EVENT.CLICK, e => fn(e))
    }
  }

  addRow(value, fn, contextMenu) {
    let body = this.getElement(this.getId() + "TableBody");
    if (!body) return true;
    let tr = this.createElement(TAG.TR);
    // tr.id = Math.random().toString(36).substring(7);
    tr.className ="aonTableTr";
    tr.style.cursor = "pointer";  
    if(this.getApp() && LS.isNewTheme()) {
      tr.addEventListener(EVENT.MOUSEOVER, () => {
        tr.style.backgroundColor = this.getApp().backgroundColor || '#eaf1fb'; 
      });

      tr.addEventListener(EVENT.MOUSELEAVE, () => {
        tr.style.backgroundColor = 'transparent'; 
      });
    }

    
    if(this.selectedColor){
      tr.addEventListener(EVENT.CLICK, () => this.addBackgroundTr(tr, "#d3e3fd"));
    }

    body.appendChild(tr);
    let checkBoxId = `aaa${body.children.length}`;
    if (this.hasAttribute("selectable")) {
      let tdCheckBox = this.createElement(TAG.TD);
      tdCheckBox.style.width = "5%";
      let aonCheckbox = new AonCheckbox();
      aonCheckbox.id = checkBoxId;
      aonCheckbox.addEventListener(EVENT.CHANGE, () => {
        if (aonCheckbox.isChecked()) {
          if(!this.selected.includes(value))
            this.selected.push(value);
          tr.style.backgroundColor = "aliceblue";
        } else {
          this.selected.forEach((item, i) => {
            if (item == value) {
              this.selected.splice(i, 1);
              tr.style.backgroundColor = "";
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
      td.style.width = item.width;
      td.style.textAlign = item.textAlign;

      let id = item.id;
      if ("option" === id && value[id]) {
        let aonIconB = new AonIconButton();
        aonIconB.id = this.getId()+"IconOption";
        aonIconB.icon = MATERIAL_ICONS.MORE_VERT;
        td.appendChild(aonIconB);
        td.addEventListener(EVENT.CLICK, () => this.getOptions(tr, td, value[id]));
      } else if("icon" === item.type && value[id]) {
        let icon = this.createElement(TAG.I);
        icon.id = this.getId() + "Icon";
        icon.className = value.icon_class || "material-icons";
        icon.innerHTML = value[id];
        icon.style.color = value[id + '_color'] || "#5f6368";
        icon.title = value.icon_title;
        td.appendChild(icon);
        if(value.fn){
          td.addEventListener(EVENT.CLICK, value.fn);
        }
      } else if("aonIcon" === item.type && value[id]) {
        let aonIcon = new AonIcon();
        aonIcon.id = this.getId()+ "AonIcon";
        aonIcon.icon = value[id];
        td.appendChild(aonIcon);
        if(value.fn){
          td.addEventListener(EVENT.CLICK, value.fn);
        }
      } else if(item.type && item.type === "list" ) {
        let list = value[id];
        let ulList = this.createElement(TAG.UL);
        ulList.style.marginTop = "6px";
        ulList.style.paddingLeft = "0px";
        ulList.style.listStyleType = "none";
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
        td.appendChild(value[id])
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
      } else if(item.type && item.type ==="date") {
        const dateRegex = /\d{2,4}\-\d{1,2}\-\d{1,2}(?:T.*)?/;
        const dateValue = value[id] !== undefined? value[id] : "";
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
      // else if(item.type && item.type ==="number") {
      //   let formatValue = formatNumber(value[id], 2, "EUR");
      //   td.innerHTML = formatValue;
      //   td.addEventListener(EVENT.CLICK, fn);
      //   if (contextMenu) {
      //     td.addEventListener("contextmenu", () => {
      //       let cb = this.getElement(checkBoxId + "Input");
      //       if(cb && !cb.checked){
      //         this.deselectAll();
      //         cb.click();
      //       } 
      //     });
      //     td.addEventListener("contextmenu", contextMenu);
      //   }
      // } 
      else {
        td.innerHTML = value[id] !== undefined? value[id] : "";
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
    let body = this.getElement(this.getId() + "TableBody");
    if (body) body.innerHTML = "";
  }

  removeColumns() {
    this.columns = [];
    this.clearSelected();
    let body = this.getElement(this.getId() + "TableHeader");
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

  empty(message) {
    message = message || 'No hay datos disponibles.';

    let body = this.getElement(this.getId() + "TableBody");
    if (!body) return true;

    let tr = this.createElement(TAG.TR);
    tr.style.textAlign = 'center';
    body.appendChild(tr);

    let td = this.createElement(TAG.TD);
    td.innerHTML = message;
    td.style.fontWeight = 'bold';
    tr.appendChild(td);
  }

  addBackgroundTr(tr, color){
    this.querySelectorAll(".aonTableTr")
    .forEach(el => {
      el.style.backgroundColor = "#ffffff";
    });

    if(tr){
      tr.style.backgroundColor = color;
    }
  }


  loading(b) {
    let body = this.getElement(this.TBODY);
    let id = this.TBODY+"Loading";
    let tr = this.getElement(id);
    if (b && !tr) {
      tr = this.createElement(TAG.TR);
      tr.id = id;
      tr.style.textAlign = 'center';
      tr.style.position  = 'relative';
      tr.style.top       = '-14px';
      tr.style.border    = 'none';
      body.appendChild(tr);

      let load = this.createElement(TAG.DIV);
      load.classList.add(CSS.AON_ICON_CONTAINER);
      load.style.right     = "0";
      load.style.left      = "0";
      tr.appendChild(load);

      let icon = this.createElement(TAG.I);
      icon.classList.add(CSS.AON_LOADER);
      load.appendChild(icon);

    } else if (!b && tr) {
      tr.remove();
    }
  }

  getApp() {
    return this.app;
  }

  setApp(app) {
    this.app = app;
  }

}
if(!window.customElements.get('aon-table')){
  window.customElements.define("aon-table", AonTable);
}
