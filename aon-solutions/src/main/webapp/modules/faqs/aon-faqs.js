import {AonElement} from '../../components/AonElement.js';
import '../../components/aon-application.js';

class AonFaqs extends AonElement {

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<aon-application id="aonFaqs" title="FAQs"></aon-application>
		`;
		this.build();
 	}

 	build() {
		let aonFaqs = document.getElementById('aonFaqs');

		aonFaqs.addToolbarOption('Add', 'add', () => {this.loadCreate()});

		let options = [
			{
				name: 'Listar FAQs',
				icon: 'view_list',
				fn: () => this.loadIndex()
			},
			{
				name: 'Crear FAQ',
				icon: 'add_box',
				fn: () => this.loadCreate()
			}
		];
		aonFaqs.addSidenavOptions('Acciones', options);
		this.loadIndex();
	}

	loadIndex() {
		let aonFaqs = document.getElementById('aonFaqs');
		aonFaqs.setContentHTML('<iframe src="../../aon-suite/public/faqs/index.html" style="width:100%;height:100%;border:none;"></iframe>');
	}

	loadCreate() {
		let aonFaqs = document.getElementById('aonFaqs');
		aonFaqs.setContentHTML('<iframe src="../../aon-suite/public/faqs/create.html" style="width:100%;height:100%;border:none;"></iframe>');
	}

	loadShow() {
		let aonFaqs = document.getElementById('aonFaqs');
		aonFaqs.setContentHTML('<iframe src="../../aon-suite/public/faqs/show.html" style="width:100%;height:100%;border:none;"></iframe>');
	}

}
window.customElements.define('aon-faqs', AonFaqs);
