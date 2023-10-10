import { AonElement } from '../components/AonElement.js';
import { CONSTANT, MATERIAL_ICONS, TAG, EVENT, CSS } from '../environments/environments.js';
import * as LS from '../services/localStorageService.js';

export class AonAppMenu extends AonElement {

    get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

    app;
    
	connectedCallback () {
		this.initialize();
		this.build();
    }

	initialize() {
        this.id = this.id || 'aonAppMenu';
	}

    build() {
        let div = this.createElement(TAG.DIV);
        div.innerHTML = this.getApp().title;
        div.style.fontWeight = 'bold';
        div.style.fontSize = '20px';
        div.style.marginLeft = '10px';
        div.style.marginTop = '10px';
        div.style.color = this.getApp().color;
        this.appendChild(div);
        let data = {
            id: "Prueba",
            title:"Prueba",
            name:"Prueba",
            color: this.getApp().color
        };

        let options = [{
            id: 'Prueba1',
            name: 'Prueba1',
            icon: MATERIAL_ICONS.THUNDERSTORM
          },{
            id: 'Prueba2',
            name: 'Prueba2',
            icon: MATERIAL_ICONS.THUNDERSTORM
          }]
        this.addSidenavOptions(data, options);
    }

    getApp() {
        return this.app;
    }

    setApp(app) {
        this.app = app;
    }

    addSidenavOptions(data, options, newButton) {
        this.addSidenavOptionsTitle(data, newButton);
        this.addSidenavOptionsList(data, options);
      }

    addSidenavOptionsTitle(data, newButton) {
        let div = this.createElement(TAG.DIV);
        div.id = this.id + data.id;
        div.style.paddingBottom = "10px";
        this.appendChild(div);
    
        if (newButton && !this.isMobile()) {
          let addButton = this.createElement(TAG.DIV);
          addButton.style.marginTop = "-15px";
          addButton.style.right = "0px";
          addButton.style.position = "absolute";
          let aonIconButton = new AonIconButton();
          aonIconButton.icon = "add";
          aonIconButton.id = div.id + "NewButton";
          addButton.appendChild(aonIconButton);
          div.appendChild(addButton);
          this.getElement(div.id + "NewButton").addEventListener(EVENT.CLICK, newButton);
        }
    
        let sidenavTitle = this.createElement(TAG.DIV);
        sidenavTitle.className = LS.isNewTheme() ? "aonSidenavTitleBeta" : "aonSidenavTitle";
        sidenavTitle.id = "aonSidenavTitle"+data.id;
        sidenavTitle.title = data.name;
        sidenavTitle.style.cursor = "pointer";
        sidenavTitle.style.userSelect = "none";
        sidenavTitle.style.marginLeft = LS.isNewTheme() ? "10px": "2px";
    
        let arrowTitleSpan = this.createElement(TAG.SPAN);
        arrowTitleSpan.className = CSS.AON_SIDENAV_TITLE_ARROW;
        arrowTitleSpan.style.borderColor = data.color;
    
        let arrowTitle = this.createElement("i");
        arrowTitle.innerHTML = MATERIAL_ICONS.EXPAND_LESS;
        arrowTitle.className = "material-icons aonVerticalMiddle";
        sidenavTitle.appendChild(LS.isNewTheme() ? arrowTitleSpan : arrowTitle);
    
        sidenavTitle.addEventListener(EVENT.CLICK, ()=>{
          const ul = div.querySelector("ul");
          if(ul){
            ul.classList.toggle(CSS.ELEMENT_HIDDEN);
            if(ul.classList.contains(CSS.ELEMENT_HIDDEN)){
              arrowTitle.innerHTML = MATERIAL_ICONS.EXPAND_MORE;
              sidenavTitle.style.marginBottom = "0";
            } else {
              arrowTitle.innerHTML = MATERIAL_ICONS.EXPAND_LESS;
              sidenavTitle.style.marginBottom = "10px";
            }
          }
        });  
    
        let span = this.createElement(TAG.SPAN);
        span.innerHTML = data.name.toUpperCase();
        sidenavTitle.appendChild(span);
    
        div.appendChild(sidenavTitle);
        return div;
      }

      addSidenavOptionsList(data, options) {
        if(data && data.id){
          let div = this.getElement(this.id + data.id);
          if(div){
            const idUl = div.id + "List";
            let ul =  this.getElement(idUl); 
            if(!ul){
              ul = this.createElement(TAG.UL);
              ul.id =idUl;
              ul.classList.add(CSS.AON_UL);
              ul.classList.add(CSS.AON_CLIP);
              div.appendChild(ul);
            }
            options.forEach((option, i) => {
              this.addSidenavOptionsListValue(data, option, ul);
            });
            return ul;
          }
        }
        return null;
      }


      addSidenavOptionsListValue(data, option, ul) {
        let sidenavId = this.isMobile() ? this.MOBILE_SIDENAV_CONTENT: this.SIDENAV;
        ul = ul || this.getElement(sidenavId + data.id + "List");
        if (!option.hidden && ul) {
          let id = sidenavId + (option.id || Math.random().toString(36).substring(7));
          let li = this.createElement(TAG.LI);
          li.id = id;
          li.title = option.name;
    
          li.className = LS.isNewTheme() 
              ? "aonAppMenuSidenavList aonOpacity"
              : "aonAppMenuSidenavList aonOpacity sidenavHover";  
          
          if(LS.isNewTheme() ) {
            li.addEventListener(EVENT.MOUSEOVER, () => {
                        li.style.backgroundColor = data.backgroundColor || '#eaf1fb';
                    });
    
                    li.addEventListener(EVENT.MOUSELEAVE, () => {
                        li.style.backgroundColor = 'transparent';
                    });
          }
    
    
          ul.appendChild(li);
          if(option.options) {
            li.style.paddingLeft = '6px';
            let arrow = this.createElement(TAG.I);
            arrow.className = "material-icons aonVerticalMiddle";
            arrow.innerHTML = MATERIAL_ICONS.ARROW_RIGHT;
            li.appendChild(arrow);
            let newLi =  this.createElement(TAG.LI);
            newLi.id = id + 'Options';
            newLi.appendChild(this.buildSidenavSubOptions(data, option.options));
            newLi.style.transition = "opacity 1s ease-out";
            this.hiddenElement(newLi, true);
            ul.appendChild(newLi);
            if(option.clickable) {
              arrow.addEventListener(EVENT.CLICK, (e => {
                e.preventDefault();
                arrow.innerHTML = arrow.innerHTML === MATERIAL_ICONS.ARROW_RIGHT ? MATERIAL_ICONS.ARROW_DROP_DOWN : MATERIAL_ICONS.ARROW_RIGHT;
                this.hiddenElement(newLi, arrow.innerHTML === MATERIAL_ICONS.ARROW_RIGHT);
              }));
            } else {
              li.addEventListener(EVENT.CLICK,() => {
                arrow.innerHTML = arrow.innerHTML === MATERIAL_ICONS.ARROW_RIGHT ? MATERIAL_ICONS.ARROW_DROP_DOWN : MATERIAL_ICONS.ARROW_RIGHT;
                this.hiddenElement(newLi, arrow.innerHTML === MATERIAL_ICONS.ARROW_RIGHT);
              });
            } 
          }
    
          let span = this.createElement(TAG.SPAN);
          span.className = "aonMenuItemSpan";
          span.title =  option.name;
          if (option.count) {
            span.innerHTML = option.name + " (" + option.count + ")";
            span.style.fontWeight = "bold";
          } else span.innerHTML = option.name;
    
          if (option.icon) {
            let i = this.createElement(TAG.I);
            let iconClass = "material-icons";
            if(data.color) i.style.color = data.color;
            if(option.icon_color) i.style.color = option.icon_color;
            if(option.icon_class) iconClass = option.icon_class;
            i.className = `${iconClass} aonVerticalMiddle`;
            i.innerHTML = option.icon;
            li.appendChild(i);
          } else if (option.aonIcon) {
            let ai = new AonIcon();
            ai.id    = id + "AonIcon";
            ai.icon  = option.aonIcon.icon;
            ai.size  = "18px";
            li.appendChild(ai);
            if(data.color) ai.color = data.color;
            li.addEventListener(EVENT.MOUSEOVER, () => {
              this.getElement(id + "AonIcon").color = data.color || option.aonIcon.color;
            });
    
            li.addEventListener(EVENT.MOUSELEAVE, () => {
              this.getElement(id + "AonIcon").color = data.color || "#5f6368";
            });
          } else if (option.img) {
            let img = this.createElement(TAG.IMG);
            if(option.style) {
              img.className = option.style;
              span.style.paddingLeft = '20px'
            } else img.style.width = '18px';
            img.src = option.img;
            li.appendChild(img);
          } else {
            span.style.marginLeft = '28px';
          }
          li.appendChild(span);
    
          // li.addEventListener(EVENT.MOUSEOVER, () => {
          //   if (!this.selected || this.selected !== id)
          //     li.style.backgroundColor = "#f1f1f1";
          // });
    
          // li.addEventListener(EVENT.MOUSELEAVE, () => {
          //   if (!this.selected || this.selected !== id)
          //     li.style.backgroundColor = "white";
          // });
    
          if (option.actions) {
            let actionDiv = this.createElement(TAG.SPAN);
            actionDiv.style.display = "none";
            li.appendChild(actionDiv);
            li.addEventListener(EVENT.MOUSEOVER, () =>  actionDiv.style.display = "contents");
    
            li.addEventListener(EVENT.MOUSELEAVE, () =>  actionDiv.style.display = "none");
    
            option.actions.forEach((item, i) => {
              let button = this.createElement(TAG.SPAN);
              button.style.right = i * 30 + "px";
              button.style.position = "absolute";
              let aonIconButton = new AonIconButton();
              aonIconButton.noHover = true;
              aonIconButton.icon = item.icon;
              aonIconButton.id = li.id + item.id;
              button.appendChild(aonIconButton);
              actionDiv.appendChild(button);
              let b = aonIconButton.getButton();
              b.style.height = "30px";
              b.style.minWidth = "30px";
              b.style.width = "30px";
              let ic = aonIconButton.getIcon();
              ic.style.fontSize = "1.3rem";
              aonIconButton.addEventListener(EVENT.CLICK, (ev)=>{
                ev.stopPropagation();
                item.action(ev)
              });
            });
          }
    
          if(!option.options || option.clickable){
            li.addEventListener(EVENT.CLICK, () => {
              // ul
              this.querySelectorAll(`[id^='${sidenavId}'] li`).forEach((el) => {
                if (el.id !== sidenavId)
                  el.style.removeProperty("background-color");
                  el.style.removeProperty("font-weight");
              });
              li.style.fontWeight= "bold"
              if(!LS.isNewTheme())
                li.style.backgroundColor = "#d3e3fd";
    
              this.selected = id;
              let toolbar = this.getElement(this.TOOLBAR);
              if(toolbar) toolbar.setAttribute("option", option.name);
              if(option.fn){
                let count = 0;
                if(li.querySelector("span")) count = li.querySelector("span").dataset.count;
                option.fn(count);
              } 
              this.dispatchEvent(new CustomEvent(EVENT.SELECT_OPTION, { detail: option }));
              if (this.isMobile()) {
                this.closeMobileSidenav();
              }
            });
          }
        }
      }

}
if(!window.customElements.get(TAG.AON_APP_MENU)){
	window.customElements.define(TAG.AON_APP_MENU, AonAppMenu);
}
