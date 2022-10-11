import { AonElement } from "./AonElement.js";
import { CONSTANT, CSS, EVENT, MATERIAL_ICONS, TAG } from "../environments/environments.js";
import { AonIconButton } from "./aon-icon-button.js";
import { AonCheckbox } from "./aon-checkbox.js";
import { AonDialogMenu } from "./aon-dialog-menu.js";

export class AonTable extends AonElement {
  columns;
  selected;

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
      let aonCheckbox = new AonCheckbox();
      aonCheckbox.id = "aonTableAllSelection";
      th.appendChild(aonCheckbox);
      header.appendChild(th);
      let ch = this.getElement("aonTableAllSelection");
      ch.addEventListener(EVENT.CHANGE, () => {
        document.querySelectorAll("aon-checkbox").forEach((item, i) => {
          if (item.value != ch.value) {
            let it = this.getElement(item.id + "Input");
            it.click();
          }
        });
      });
    }

  }

  addColumn(name, type, id, width) {
    if (this.hasAttribute("selectable")) {
      this.paintCheckboxHeader();
    }
    let header = this.getElement(this.getId() + "TableHeader");
    let th = this.createElement(TAG.TH);
    th.innerHTML = name;
    th.style.width = width;
    this.columns.push({ name, type, id, width });
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
    tr.style.cursor = "pointer";

    body.appendChild(tr);
    let checkBoxId = `aaa${body.children.length}`;
    if (this.hasAttribute("selectable")) {
      let tdCheckBox = this.createElement(TAG.TD);
      tdCheckBox.style.width = "5%";
      let aonCheckbox = new AonCheckbox();
      aonCheckbox.id = "aaa"+body.children.length;
      tdCheckBox.appendChild(aonCheckbox);
      tr.appendChild(tdCheckBox);
      let checkbox = this.getElement(`aaa${body.children.length}`);
      checkbox.addEventListener(EVENT.CHANGE, () => {
        if (checkbox.getValue()) {
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
    }

    this.columns.forEach((item, i) => {
      let td = this.createElement(TAG.TD);
      td.style.width = item.width;
      // if("number" === item.type) {
      //   td.style.textAlign = "right";
      //   td.style.paddingRight = "2%";
      // }
      let id = item.id;
      if ("option" === id && value[id]) {
        let aonIconB = new AonIconButton();
        aonIconB.id = this.getId()+"IconOption";
        aonIconB.icon = MATERIAL_ICONS.MORE_VERT;
        td.appendChild(aonIconB);
        td.addEventListener(EVENT.CLICK, () => this.getOptions(tr, td, value[id]));
      } else if("icon" === id && value[id]) {
        let icon = this.createElement(TAG.I);
        icon.id = this.getId() + "Icon";
        icon.className = value.icon_class || "material-icons";
        icon.innerHTML = value[id];
        icon.style.color = value.icon_color || "#5f6368";
        icon.title = value.icon_title;
        td.appendChild(icon);
      } else if(item.type && item.type ==="html") {
        td.appendChild(value[id])
        td.addEventListener(EVENT.CLICK, fn);
        if (contextMenu) {
          td.addEventListener("contextmenu", () => {
            
            let cb = this.getElement(checkBoxId + "Input");
            if(!cb.checked){
              this.deselectAll();
              cb.click();
            } 
          });
          td.addEventListener("contextmenu", contextMenu);
        }
      } else {
        td.innerHTML = value[id] !== undefined? value[id] : "";
        td.addEventListener(EVENT.CLICK, fn);
        if (contextMenu) {
          td.addEventListener("contextmenu", () => {
            
            let cb = this.getElement(checkBoxId + "Input");
            if(!cb.checked){
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
    this.selected = [];
    let body = this.getElement(this.getId() + "TableHeader");
    if (body) body.innerHTML = "";
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

}
if(!window.customElements.get('aon-table')){
  window.customElements.define("aon-table", AonTable);
}
