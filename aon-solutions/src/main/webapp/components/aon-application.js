import {AonElement} from './AonElement.js';

import './aon-toolbar.js';
import './aon-loader.js';
import './aon-icon.js';

export class AonApplication extends AonElement {
	SIDENAV;
	TOOLBAR;
	LOADER;
	CONTENT;

	selected;

  static get observedAttributes() {
    return ['title'];
  }

  get id() {
    return this.getAttribute('id');
  }

  set id(id) {
    this.setAttribute('id', id);
  }

  get title() {
    return this.getAttribute('title');
  }

  set title(title) {
    this.setAttribute('title', title);
  }

	get main() {
    return this.getAttribute('main');
  }

  set main(main) {
    this.setAttribute('main', main);
  }

  attributeChangedCallback(name, oldValue, newValue) {
		if('title' === name) {
			let toolbar = this.getElement(this.TOOLBAR);
			if(toolbar) toolbar.setAttribute('title', newValue);
		}
  }

	constructor () {
		super();
		this.SIDENAV = this.id + 'Sidenav';
		this.TOOLBAR = this.id + 'Toolbar';
		this.LOADER = this.id + 'Loader';
		this.CONTENT = this.id + 'Content';
	}

	connectedCallback () {
		this.build();
	}

 	build() {
		this.innerHTML = `
			<!-- AON APPLICATION TOOLBAR -->
			<aon-toolbar id="${this.TOOLBAR}" title="${this.getTitle()}"></aon-toolbar>

			<!-- AON APPLICATION LOADER -->
			<aon-loader id="${this.LOADER}"> </aon-loader>

			<!-- AON APPLICATION MENU (SIDENAV) -->
			<div id="${this.SIDENAV}" class="sidenav"></div>

			<!-- AON APPLICATION CONTENT -->
			<div id="${this.CONTENT}"></div>
		`;

    let toolbar = this.getElement(this.TOOLBAR);
    toolbar.toogleSidenav(() => this.toogleSidenav());

    let sidenav = this.getElement(this.SIDENAV);
    sidenav.style.width = this.isMobile() ? '0px' : '250px';

    let content = this.getElement(this.CONTENT);
		content.className = this.isMobile() ? 'aonMobileContent' : 'aonContent';
		content.style.marginLeft = this.isMobile() ? '0px' : "250px";

		if(this.hasAttribute('main')) {
			toolbar.style.display = 'none';
			sidenav.style.top = '60px';
			sidenav.style.height = 'calc(100vh - 61px)';
			content.style.height = 'calc(100vh - 61px)';
		}

	}

	startLoader() {
		this.getElement(this.LOADER).start();
	}

	stopLoader() {
		this.getElement(this.LOADER).stop();
	}

  toogleSidenav() {
    let sidenav = this.getElement(this.SIDENAV);
    let content = this.getElement(this.CONTENT);
    if(sidenav.style.width === "250px"){
			sidenav.style.width = "0px";
			content.style.marginLeft = "0px";
		} else {
			sidenav.style.width = "250px";
			content.style.marginLeft = this.isMobile() ? '0px' : '250px';
		}
  }

	closeSidenav() {
		let sidenav = this.getElement(this.SIDENAV);
		let content = this.getElement(this.CONTENT);
		sidenav.style.width = "0px";
		content.style.marginLeft = "0px";
	}

	addSidenavWidget(title, element) {
		let sidenav = this.getElement(this.SIDENAV);

		let div = this.createElement('div');
		div.style.paddingBottom = '25px';
		div.style.borderBottom = '1px solid #ebebeb';
		sidenav.appendChild(div);

		let sidenavTitle =  this.createElement('div');
		sidenavTitle.className = 'aonSidenavTitle';
		sidenavTitle.innerHTML = title;
		div.appendChild(sidenavTitle);

		let content = this.createElement('div');
		content.style.paddingLeft = '26px';
		content.appendChild(element);
		div.appendChild(content);
	}

	addSidenavWidgetHTML(title, html) {
		let sidenav = this.getElement(this.SIDENAV);

		let div = this.createElement('div');
		div.style.paddingBottom = '25px';
		div.style.borderBottom = '1px solid #ebebeb';
		sidenav.appendChild(div);

		let sidenavTitle =  this.createElement('div');
		sidenavTitle.className = 'aonSidenavTitle';
		sidenavTitle.innerHTML = title;
		div.appendChild(sidenavTitle);

		let content = this.createElement('div');
		content.style.paddingLeft = '26px';
		content.innerHTML = html;
		div.appendChild(content);
	}

	addSidenavOptions(title, options) {
		if(options && options.length > 0) {
    	let sidenav = this.getElement(this.SIDENAV);

			let div = this.createElement('div');
			div.style.paddingBottom = '25px';
			div.style.borderBottom = '1px solid #ebebeb';
    	sidenav.appendChild(div);

    	let sidenavTitle =  this.createElement('div');
    	sidenavTitle.className = 'aonSidenavTitle';
    	sidenavTitle.innerHTML = title;
			div.appendChild(sidenavTitle);

    	let ul =  this.createElement('ul');
    	ul.className = 'aonClip';
    	div.appendChild(ul);
    	options.forEach((option, i) => {
				if(!option.hidden){
      		let id = sidenav.id + option.name;
      		let li =  this.createElement('li');
      		li.id = id;
      		li.className = 'aonAppMenuSidenavList aonOpacity';
					ul.appendChild(li);

					let span =  this.createElement('span');
					span.className = 'aonMenuItemSpan';
					if(option.count && option.count > 0) {
						span.innerHTML = option.name + ' (' + option.count + ')';
						span.style.fontWeight = 'bold';
					} else span.innerHTML = option.name;

      		if(option.icon) {
        		let i =  this.createElement('i');
        		i.className = 'material-icons aonVerticalMiddle';
        		i.innerHTML = option.icon;
        		li.appendChild(i);
      		} else if(option.aonIcon) {
        		li.innerHTML = `<aon-icon id="${id + 'AonIcon' }" icon="${option.aonIcon.icon}" size="18px"></aon-icon>`;

						li.addEventListener('mouseover', () => {
							this.getElement(id + 'AonIcon').color = option.aonIcon.color;
						});

						li.addEventListener('mouseleave', () => {
							this.getElement(id + 'AonIcon').color = '#5f6368';
						});
      		} else if(option.img) {
        		let img =  this.createElement('img');
        		img.style.width = '18px';
        		img.src = option.img;
        		li.appendChild(img);
      		} else {
						span.style.marginLeft = '28px';
					}
      		li.appendChild(span);

      		li.addEventListener('mouseover', () => {
        		if(!this.selected || this.selected !== id)
          		li.style.backgroundColor = '#f1f1f1';
      		});

      		li.addEventListener('mouseleave', () => {
        		if(!this.selected || this.selected !== id)
          		li.style.backgroundColor = 'white';
      		});

      		li.addEventListener('click', () => {
      			document.querySelectorAll(`[id^='${sidenav.id}']`).forEach((el, i) => {
          		el.style.backgroundColor = 'transparent';
        		});
        		this.selected = id;
        		li.style.backgroundColor = '#ddd';
			    	let toolbar = this.getElement(this.TOOLBAR);
						toolbar.setAttribute('option', option.name);
        		option.fn();
						if(this.isMobile()) {
							this.closeSidenav();
						}
      		});
				}
    	});
		}
  }

  addOption(name, icon, fn) {
    this.addToolbarOption(name, icon, fn);
  }

  addToolbarOption(name, icon, fn) {
		this.getElement(this.TOOLBAR).addButton(name, icon, fn);
  }

	removeToolbarOption(name) {
		this.getElement(this.TOOLBAR).removeButton(name);
	}

	removeToolbarOptions() {
		this.getElement(this.TOOLBAR).removeButtons();
	}

  setContent(element){
		this.clearElement(this.CONTENT);
		this.getElement(this.CONTENT).appendChild(element);
  }

  setContentHTML(html){
		this.getElement(this.CONTENT).innerHTML = html;
  }

  getId() {
    return this.getAttribute('id');
  }

  getTitle() {
    return this.getAttribute('title');
  }
}
window.customElements.define('aon-application', AonApplication);
