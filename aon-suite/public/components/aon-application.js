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

  attributeChangedCallback(name, oldValue, newValue) {
    if('title' === name) {
      let toolbar = document.getElementById(this.getId() + 'Toolbar');

      if (toolbar !== null) {
        toolbar.setAttribute('title', newValue);
      }
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
			<div id="${this.getId() + 'Sidenav'}" class="sidenav" style="top:41px;">

			</div>

			<!-- AON CONTRAT@ CONTENT -->
			<div id="${this.getId() + 'Content'}" class="aonContent" style="height:calc(100vh - 41px);">

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
    let sidenav = document.getElementById(this.getId() + 'Sidenav');

		let div = document.createElement('div');
		div.style.paddingBottom = '25px';
		div.style.borderBottom = '1px solid #ebebeb';

    let sidenavTitle = document.createElement('div');
    sidenavTitle.className = 'aonSidenavTitle';
    sidenavTitle.innerHTML = title;
		div.appendChild(sidenavTitle);

    let ul = document.createElement('ul');
    ul.className = 'aonClip listUnstyled';

    options.forEach((option, i) => {
      let id = sidenav.id + option.name;
      let li = document.createElement('li');
      li.id = id;
      li.className = 'aonAppMenuSidenavList aonOpacity';

      if(!this.selected && option.default) {
        this.selected = id;
        li.style.backgroundColor = '#ddd';
      }

      if(option.icon) {
        let i = document.createElement('i');
        i.className = 'material-icons aonVerticalMiddle';
        i.innerHTML = option.icon;
        li.appendChild(i);
      } else if(option.aonIcon) {
        li.innerHTML = `<aon-icon icon="${option.aonIcon.icon}" color="${option.aonIcon.color}" size="18px"></aon-icon>`;
      } else if(option.img) {
        let img = document.createElement('img');
        img.style.width = '18px';
        img.src = option.img;
        li.appendChild(img);
      }

      let span = document.createElement('span');
      span.className = 'aonMenuItemSpan';
      span.innerHTML = option.name;
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
        option.fn();
      });

      ul.appendChild(li);
    });
    div.appendChild(ul);
    sidenav.appendChild(div);
  }

  addOption(name, icon, fn) {
    this.addToolbarOption(name, icon, fn);
  }

  addToolbarOption(name, icon, fn, title = null) {
    let toolbar = document.getElementById(this.getId() + 'Toolbar');
    toolbar.addButton(name, icon, fn, title);
  }

  removeToolbarOptions(names = null) {
    let toolbar = document.getElementById(this.getId() + 'Toolbar');

    if (names === null) {
      toolbar.removeButtons();
    } else {
      names.map((name) => {
        toolbar.removeButton(name);
      });
    }
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
