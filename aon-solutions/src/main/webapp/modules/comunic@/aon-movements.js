import {AonElement} from '../../components/AonElement.js';

export class AonMovements extends AonElement {

  get id() {
    return this.getAttribute('id');
  }

  set id(id) {
    this.setAttribute('id', id);
  }

	constructor () {
		super();
	}

	connectedCallback () {
    this.build();
 	}

 	build() {

  }
}
window.customElements.define('aon-movements', AonMovements);
