import { AonBasicTable } from "../../../components/aon-basic-table";
import { AonElement } from "../../../components/AonElement";
import { AonToolbar } from "../../../components/aon-toolbar.js";
import { createCard, createDate, createInput, createSelect } from "../../../components/CreateComponent";
import { CONSTANT, EVENT, MSG, TAG } from "../../../environments/environments";
import { RecordData } from "../../../models/registry/RecordData";
import { CommercialRegistryCode, RecordDataType } from "../../../models/registry/RecordDataEnums";
import { deleteRecordData, saveRecordData } from "../../../services/registryService";
import { ToolbarType } from "../../../models/enums";

import * as ACTION from '../../actions.js';

export class AonRecordData extends AonElement {

    recordData;

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
        this.id = this.id || 'aonRecordDataId';
        this.RECORD_DATA_TOOLBAR = this.id + "Toolbar";
        this.RECORD_DATA_CARD = this.id + "Card";
        this.RECORD_DATA_TABLE = this.id + "Table";
        this.RECORD_DATA_DESCRIPTION = this.id + "Description";
        this.RECORD_DATA_CREATION_DATE = this.id + "CreationDate";
        this.RECORD_DATA_RECORD_DATE = this.id + "RecordDate";
        this.RECORD_DATA_NOTARY = this.id + "Notary";
        this.RECORD_DATA_PROTOCOL_NUMBER = this.id + "ProtocolNumber";
        this.RECORD_DATA_INSCRIPTION = this.id + "Inscription";
        this.RECORD_DATA_TOMO = this.id + "Tomo";
        this.RECORD_DATA_SECTION = this.id + "Section";
        this.RECORD_DATA_FOLIO = this.id + "Folio";
        this.RECORD_DATA_HOJA = this.id + "Hoja";
        this.RECORD_DATA_TYPE = this.id + "Type";
        this.RECORD_DATA_IRUS = this.id + "Irus";
        this.RECORD_DATA_COMMERCIAL_REGISTRY_CODE = this.id + "CommercialRegistryCode";
    }

    build() {
        this.buildToolbar();
 
        let div = this.createElement(TAG.DIV);
        div.style.display = 'flex';
        div.style.flexWrap = 'wrap';
        this.appendChild(div);
        this.buildRegistralCard(div);
    }

    buildToolbar() {
        let toolbar = new AonToolbar();
        toolbar.id = this.RECORD_DATA_TOOLBAR;
        toolbar.type = ToolbarType.SECONDARY;
        toolbar.title = 'Datos Registrales';
        this.appendChild(toolbar);
        if(this.getRecordData().getId()) 
            toolbar.addButton2(ACTION.DELETE, () => this.remove());
        toolbar.addButton2(ACTION.SAVE, () => this.save());
        toolbar.addButton2(ACTION.BACK, () => this.back());
    }

    buildRegistralCard(parent){
        let card = createCard(this.RECORD_DATA_CARD, MSG.REGISTRATION_DATA, parent);
        card.style.width = '50%';

        let div = this.createDiv();
        card.setContent(div);

        let table = new AonBasicTable();
        table.id = this.RECORD_DATA_TABLE;
        div.appendChild(table);

        table.addRow();

        let type = createSelect(this.RECORD_DATA_TYPE, MSG.TYPE);
        type.setAlias("value", "description");
        type.autocomplete = true;
        type.value = this.getRecordData().getType().value;
        type.setOptions(RecordDataType.toArray());
		type.addEventListener(EVENT.SELECT, () => {
            alert(type.value);
            alert(JSON.stringify(RecordDataType.safeValueOf(type.value)));

			this.getRecordData().setType(RecordDataType.safeValueOf(type.value));
		});
        table.addCell(type, 2);

        let description = createInput(this.RECORD_DATA_DESCRIPTION, MSG.DESCRIPTION);
        description.value = this.getRecordData().getDescription();
        description.addEventListener(EVENT.CHANGE, () => {
            this.getRecordData().setDescription(description.value);
        });
        table.addCell(description, 2);

        table.addRow();


        let creationDate = createDate(this.RECORD_DATA_CREATION_DATE, MSG.CREATION_DATE);
        creationDate.setDate(this.getRecordData().getCreationDate());
        creationDate.addEventListener(EVENT.CHANGE, () => {
            this.getRecordData().setCreationDate(creationDate.getDateValue());
            if(this.autosave) this.save();
        });
        table.addCell(creationDate, 2);

        let recordDate = createDate(this.RECORD_DATA_RECORD_DATE, MSG.REGISTRATION_DATE);
        recordDate.setDate(this.getRecordData().getRecordDate());
        recordDate.addEventListener(EVENT.CHANGE, () => {
            this.getRecordData().setRecordDate(recordDate.getDateValue());
            if(this.autosave) this.save();
        });
        table.addCell(recordDate, 2);

        table.addRow();
        
        let notary = createInput(this.RECORD_DATA_NOTARY, MSG.NOTARY);
        notary.value = this.getRecordData().getNotary();
        notary.addEventListener(EVENT.CHANGE, () => {
            this.getRecordData().setNotary(notary.value);
        });
        table.addCell(notary, 4);

        table.addRow();

        let protocol = createInput(this.RECORD_DATA_PROTOCOL_NUMBER, MSG.PROTOCOL);
        protocol.value = this.getRecordData().getNumber();
        protocol.addEventListener(EVENT.CHANGE, () => {
            this.getRecordData().setNumber(protocol.value);
        });
        table.addCell(protocol, 1);

        let inscription = createInput(this.RECORD_DATA_INSCRIPTION, MSG.INSCRIPTION);
        inscription.value = this.getRecordData().getRegistration();
        inscription.addEventListener(EVENT.CHANGE, () => {
            this.getRecordData().setRegistration(inscription.value);
        })
        table.addCell(inscription, 3);

        table.addRow();

        let tomo = createInput(this.RECORD_DATA_TOMO, MSG.VOLUME);
        tomo.value = this.getRecordData().getVolume();
        tomo.addEventListener(EVENT.CHANGE, () => {
            this.getRecordData().setVolume(tomo.value);
        });
        table.addCell(tomo, 1);

        let section = createInput(this.RECORD_DATA_SECTION, MSG.SECTION);
        section.value = this.getRecordData().getSection();
        section.addEventListener(EVENT.CHANGE, () => {
            this.getRecordData().setSection(section.value);
        });
        table.addCell(section, 1);

        let folio = createInput(this.RECORD_DATA_FOLIO, MSG.FOLIO);
        folio.value = this.getRecordData().getPage();
        folio.addEventListener(EVENT.CHANGE, () => {
            this.getRecordData().setPage(folio.value);
        });
        table.addCell(folio, 1);

        let hoja = createInput(this.RECORD_DATA_HOJA, MSG.SHEET);
        hoja.value = this.getRecordData().getSheet();
        hoja.addEventListener(EVENT.CHANGE, () => {
            this.getRecordData().setSheet(hoja.value);
        });
        table.addCell(hoja, 1);		

        table.addRow();

        let commercialRegistryCode = createSelect(this.RECORD_DATA_COMMERCIAL_REGISTRY_CODE, MSG.COMMERCIAL_REGISTRY_CODE);
        commercialRegistryCode.setAlias("value", "description");       
        commercialRegistryCode.autocomplete = true;
        if(this.getRecordData().getCommercialRegistryCode()) 
            commercialRegistryCode.value = this.getRecordData().getCommercialRegistryCode().value;
        commercialRegistryCode.setOptions(CommercialRegistryCode.toArray());
		commercialRegistryCode.addEventListener(EVENT.SELECT, () => {
			this.getRecordData().setCommercialRegistryCode(CommercialRegistryCode.safeValueOf(commercialRegistryCode.value));
		});
        table.addCell(commercialRegistryCode, 2);

        let irus = createInput(this.RECORD_DATA_IRUS, MSG.IRUS);
        irus.value = this.getRecordData().getIrus();
        irus.addEventListener(EVENT.CHANGE, () => {
            this.getRecordData().setIrus(irus.value);
        });
        table.addCell(irus, 2);
    }


    getRecordData() {
        return this.recordData;  
    }

    setRecordData(recordData) {
        this.recordData = recordData;
    }

    save() {
        saveRecordData(this.getRecordData().toJSON()).then(() => {
   			this.showToast({
				type: 'success',
	 			message: 'Datos Guardados Correctamente'
	 		});
        }).catch(error => {
   	 		this.showToast(error);
        });
    }

    remove() {
        deleteRecordData(this.getRecordData().getId()).then(() => {
   			this.showToast({
                type: 'success',
     			message: 'Datos Eliminados Correctamente'
     		});
            this.back(this.getRecordData().getId());
        }).catch(error => {
   	 		this.showToast(error);
        });
    }
} 

if(!window.customElements.get(TAG.AON_RECORD_DATA)){
    window.customElements.define(TAG.AON_RECORD_DATA, AonRecordData);
}