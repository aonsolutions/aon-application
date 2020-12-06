import {AonElement} from './AonElement.js';

export class AonDialogMenu extends AonElement {

	DIALOG;
	CONTENT;

	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	constructor () {
		super();
		this.DIALOG = this.id + 'DialogMenu';
		this.CONTENT = this.DIALOG + 'Content';
	}

	connectedCallback () {
		this.innerHTML = `
		<div id="${this.DIALOG}" class="aonDialog">
			<div id="${this.CONTENT}" class="aonDialogContent">

			</div>
		</div>
		`;
		this.build();
  }

	build() {
		let dialog = this.getElement(this.DIALOG);
		let content = this.getElement(this.CONTENT);

		dialog.style.backgroundColor = 'transparent';
		dialog.style.paddingTop = '0px';

		content.style.position = 'absolute';
	  content.style.width = '200px';
		content.style.padding = '0px';

		dialog.onclick = (event) => {
			if (event.target === dialog) {
				this.close();
  		}
		}
	}

	open(){
    let dialog = this.getElement(this.DIALOG);
		dialog.style.display = 'block';
	}

	close() {
		let dialog = this.getElement(this.DIALOG);
		dialog.style.display = 'none';
	}

	setMenuOptions(options, top, left) {
		let dialog = this.getElement(this.DIALOG);
		let content = this.getElement(this.CONTENT);

  		content.style.top = top + 'px' || '90px';
		content.style.left = (left > (dialog.offsetWidth/2) ? left - 180 : left)+'px' ;
		
		content.innerHTML = '';
		let ul = document.createElement('ul');
		content.appendChild(ul);
		options.forEach((item, i) => {
			let li = document.createElement('li');
			li.className = 'aonAppLi';
			li.style.padding = '10px';
			li.style.cursor = 'pointer';
			ul.appendChild(li);

			let ic = document.createElement('i');
			ic.className = 'material-icons';
			ic.style.verticalAlign = 'middle';
			ic.innerHTML = item.icon;
			li.appendChild(ic);

			let span = document.createElement('span');
			span.style.marginLeft = '5px';
			span.innerHTML = item.name;
			li.appendChild(span);
			li.addEventListener('click', () => {
				this.close();
				item.fn();
			});
		});
	}
}

window.customElements.define('aon-dialog-menu', AonDialogMenu);
