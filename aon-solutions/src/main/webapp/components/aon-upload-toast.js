import { CONSTANT, CSS, MATERIAL_ICONS, MSG, TAG } from '../environments/environments.js';
import { uploadDocument } from '../modules/documental/DocumentalUtils.js';
import { generateJobId, s3UploadInvoice, uploadInvoice2 } from '../modules/invoice/InvoiceUtils.js';
import {AonElement} from './AonElement.js';
import { AonCard } from './aon-card.js';
import { AonIcon } from './aon-icon.js';

export class AonUploadToast extends AonElement {

	files;
	JOB_ID;
	dur;
	invofoxConfiguration;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		this.build();
	}

	initialize() {
		this.id = this.id || 'aonUploadToast';
		this.JOB_ID = generateJobId();
	}

	build() {
		let card = new AonCard();
		card.id = this.id + 'Card';
		card.title = MSG.UPLOAD_FILE;		
		this.appendChild(card);
		card.addTitleButton(MSG.CLOSE, MATERIAL_ICONS.CLOSE, false, () => this.close());

		card.style.minHeight = '100px';
		card.style.width = '400px';
		card.style.position = 'absolute';
		card.style.zIndex = 3;
		card.style.bottom = '10px';
		card.style.right = '25px';
		

		let ul = this.createElement(TAG.UL);
		ul.id = this.id + 'List';
		ul.className = 'aonUl';
		// div.appendChild(ul)
		card.setContent(ul);
	}

	close() {
		this.parentNode.removeChild(this);
	}

	addFiles() {

	}

	addFile(type, file, data, fn) {
		let ul = this.getElement(this.id + 'List');

		let li = this.createElement(TAG.LI)
		li.className = 'aonLi';
		li.style.borderBottom = '0px';
		li.style.height = '0px';
		li.style.display = 'flex';
		li.style.justifyContent = 'space-between';
		ul.appendChild(li);

		let div = this.createDiv();
		li.appendChild(div);

		let icon = new AonIcon();
		icon.className = "aonAvatar";
		icon.icon = this.getTypeIcon(file.type);
		icon.size = "20";
		div.appendChild(icon);
		
		let span = this.createSpan()
		span.innerHTML = file.name;
		span.style.position = 'relative';
		span.style.top = '7px';
		div.appendChild(span);
		

		let loadDiv = this.createDiv();
		loadDiv.id = this.id + "LoadDiv";
		loadDiv.className = 'lds-ring';
		loadDiv.innerHTML = '<div></div><div></div><div></div><div></div>';
		li.appendChild(loadDiv);

		let okDiv = this.createElement(TAG.I);
		okDiv.id = this.id + "OkDiv";
		okDiv.className = CSS.MATERIAL_ICONS;
		okDiv.innerHTML = MATERIAL_ICONS.CHECK_CIRCLE;
		okDiv.style.display = 'none';
		okDiv.style.color = '#5cb85c';
		li.appendChild(okDiv);

		this.upload(type, file, data, () => {
			okDiv.style.display = 'block';
			loadDiv.style.display = 'none';
			if(fn) fn();
		}, () => {
			okDiv.style.display = 'block';
			okDiv.innerHTML = MATERIAL_ICONS.CANCEL;
			okDiv.style.color = 'red';
			loadDiv.style.display = 'none';
		});

	}	

	upload(type, file, data, success, error) {
		if("invoice" === type){
			if(this.isInvofox()) {
				s3UploadInvoice(file, this.JOB_ID, data, success, error);
			} else {
				uploadInvoice2(file, success, error);
			}
 		} else if("documental" === type) {
			uploadDocument(file, data, success, error);
		}

	}


	getTypeIcon(type) {
		if(type.includes('pdf')) {
		  return 'aon_pdf';
		} else if(type.includes('powerpoint') || type.includes('presentation')){
		  return 'aon_powerpoint'
		} else if(type.includes('excel') || type.includes('spreadsheet')){
		  return 'aon_excel'
		} else if(type.includes('word') || type.includes('text')){
		  return 'aon_word'
		} else if(type.includes('image')) {
		  return 'aon_image'
		} else {
		  return 'aon_file'
		}
	  }


	setDur(dur) {
		this.dur = dur;
	}

	getDur() {
		return this.dur;
	}
	
	isInvofox() {
		console.log(JSON.stringify(this.dur));
		console.log(this.dur.isInvofox());
		return this.dur.isInvofox();
	}

}
if(!window.customElements.get(TAG.AON_UPLOAD_TOAST)){
	window.customElements.define(TAG.AON_UPLOAD_TOAST,  AonUploadToast);
}
