import './aon-toolbar.js';
import './aon-icon.js';

class AonApplication extends HTMLElement {

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
			let toolbar = document.getElementById(this.getId() + 'Toolbar');
			if(toolbar) toolbar.setAttribute('title', newValue);
		}
  }

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<!-- AON EXAMPLE TOOLBAR -->
			<aon-toolbar id="${this.getId() + 'Toolbar'}" title="${this.getTitle()}"></aon-toolbar>

			<!-- AON EXAMPLE MENU (SIDENAV) -->
			<div id="${this.getId() + 'Sidenav'}" class="sidenav">

			</div>

			<!-- AON CONTRAT@ CONTENT -->
			<div id="${this.getId() + 'Content'}" class="aonContent">

			</div>

		`;
    this.build();
 	}

 	build() {
    let toolbar = document.getElementById(this.getId() + 'Toolbar');
    toolbar.toogleSidenav(() => this.toogleSidenav());

    let sidenav = document.getElementById(this.getId() + 'Sidenav');
    sidenav.style.width = '250px';

    let content = document.getElementById(this.getId() + 'Content');
		content.style.marginLeft = "250px";

		if(this.hasAttribute('main')) {
			toolbar.style.display = 'none';
			sidenav.style.top = '60px';
			sidenav.style.height = 'calc(100vh - 61px)';
			content.style.height = 'calc(100vh - 61px)';
		}

	}

  toogleSidenav() {
    let sidenav = document.getElementById(this.getId() + 'Sidenav');
    let content = document.getElementById(this.getId() + 'Content');
    if(sidenav.style.width === "250px"){
			sidenav.style.width = "0px";
			content.style.marginLeft = "0px";
		} else {
			sidenav.style.width = "250px";
			content.style.marginLeft = "250px";
		}
  }

  addSidenavOptions(title, options) {
		if(options && options.length > 0) {
    	let sidenav = document.getElementById(this.getId() + 'Sidenav');

			let div = document.createElement('div');
			div.style.paddingBottom = '25px';
			div.style.borderBottom = '1px solid #ebebeb';
    	sidenav.appendChild(div);

    	let sidenavTitle = document.createElement('div');
    	sidenavTitle.className = 'aonSidenavTitle';
    	sidenavTitle.innerHTML = title;
			div.appendChild(sidenavTitle);

    	let ul = document.createElement('ul');
    	ul.className = 'aonClip';
    	div.appendChild(ul);
    	options.forEach((option, i) => {
      	let id = sidenav.id + option.name;
      	let li = document.createElement('li');
      	li.id = id;
      	li.className = 'aonAppMenuSidenavList aonOpacity';
				ul.appendChild(li);

				let span = document.createElement('span');
				span.className = 'aonMenuItemSpan';
				span.innerHTML = option.name;

      	if(option.icon) {
        	let i = document.createElement('i');
        	i.className = 'material-icons aonVerticalMiddle';
        	i.innerHTML = option.icon;
        	li.appendChild(i);
      	} else if(option.aonIcon) {
        	li.innerHTML = `<aon-icon id="${id + 'AonIcon' }" icon="${option.aonIcon.icon}" size="18px"></aon-icon>`;

					li.addEventListener('mouseover', () => {
						document.getElementById(id + 'AonIcon').color = option.aonIcon.color;
					});

					li.addEventListener('mouseleave', () => {
						document.getElementById(id + 'AonIcon').color = '#5f6368';
					});
      	} else if(option.img) {
        	let img = document.createElement('img');
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
          	li.style.backgroundColor = 'transparent';
      	});

      	li.addEventListener('click', () => {
      		document.querySelectorAll(`[id^='${sidenav.id}']`).forEach((el, i) => {
          	el.style.backgroundColor = 'transparent';
        	});
        	this.selected = id;
        	li.style.backgroundColor = '#ddd';
			    let toolbar = document.getElementById(this.getId() + 'Toolbar');
					toolbar.setAttribute('option', option.name);
        	option.fn();
      	});


    	});
		}
  }

  addOption(name, icon, fn) {
    this.addToolbarOption(name, icon, fn);
  }

  addToolbarOption(name, icon, fn) {
    let toolbar = document.getElementById(this.getId() + 'Toolbar');
    toolbar.addButton(name, icon, fn);
  }

	removeToolbarOption(name) {
		let toolbar = document.getElementById(this.getId() + 'Toolbar');
		toolbar.removeButton(name);
	}

	removeToolbarOptions() {
		let toolbar = document.getElementById(this.getId() + 'Toolbar');
		toolbar.removeButtons();
	}

  setContent(element){
    let content = document.getElementById(this.getId() + 'Content');
    content.innerHTML = '';
    content.appendChild(element);
  }

  setContentHTML(html){
    let content = document.getElementById(this.getId() + 'Content');
    content.innerHTML = html;
  }

  getId() {
    return this.getAttribute('id');
  }

  getTitle() {
    return this.getAttribute('title');
  }
}
window.customElements.define('aon-application', AonApplication);
