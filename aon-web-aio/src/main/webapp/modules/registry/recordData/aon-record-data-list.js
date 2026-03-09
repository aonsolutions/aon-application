import { AonElement } from '../../../components/AonElement.js';
import { AonTable } from '../../../components/aon-table.js';
import { AonRecordData } from './aon-record-data.js';
import { getRegistry } from '../../../services/service.js';
import { CONSTANT, EVENT, MSG, TAG, MATERIAL_ICONS } from '../../../environments/environments.js';
import { RecordData } from '../../../models/registry/RecordData.js';
import { RecordDataType } from '../../../models/registry/RecordDataEnums.js';

export class AonRecordDataList extends AonElement {

    TABLE;
    registryId;
    domain;
    array;

    get id() {
        return this.getAttribute(CONSTANT.ID);
    }

    set id(id) {
        this.setAttribute(CONSTANT.ID, id);
    }

    constructor() {
        super();
    }

    connectedCallback() {
        this.initialize();
        this.build();
    }

    initialize() {
        this.TABLE = new AonTable();
        this.TABLE.id = (this.id || 'aonRecordDataList') + 'Table';
        this.appendChild(this.TABLE);
    }

    build() {
        this.TABLE.addColumn(MSG.CREATION_DATE, 'string',   'creationDate', '20%');
        this.TABLE.addColumn(MSG.DESCRIPTION,   'string',   'description',   '40%');
        this.TABLE.addColumn(MSG.NOTARY,        'string',   'notary',        '25%');
        this.TABLE.addColumn(MSG.PROTOCOL,      'string',   'number',        '15%');

        this.TABLE.addColumnIcon({ title: MSG.ADD+" dato registral", name: MATERIAL_ICONS.ADD, type: "string", width: "5%", id: "option"}, 
            () => this.openRecordData({domain: this.domain, registry: this.registryId, 
                type: this.array.length === 0 ? RecordDataType.INCORPORATION.name : RecordDataType.OTHER_REGISTRATIONS.name}));
    
        this.load();
    }

    load() {
        if (this.array.length === 0) {
            this.TABLE.empty();
            return;
        }
        this.array.forEach(item => {
            this.TABLE.addRow(item, () => this.openRecordData(item));
        });
    }

    openRecordData(item) {
        let parent = this.parentNode;

        let aonRecordData = new AonRecordData();
        aonRecordData.id = (this.id || 'aonRecordDataList') + 'Detail';
        aonRecordData.setRecordData(new RecordData(item));
        aonRecordData.back = (removedId) => this.backToList(parent, this.array, this.registryId, this.domain, removedId);

        this.clearElement(parent);

        parent.appendChild(aonRecordData);
    }

    backToList(parent, array, registryId, domain, removedId) {
		let recordDataList = new AonRecordDataList();
		recordDataList.array = removedId ? array.filter(f => f.id != removedId) : array;
		recordDataList.registryId = registryId;
		recordDataList.domain = domain;
        
        this.clearElement(parent);  
        parent.appendChild(recordDataList);
    }
}

if (!window.customElements.get(TAG.AON_RECORD_DATA_LIST)) {
    window.customElements.define(TAG.AON_RECORD_DATA_LIST, AonRecordDataList);
}
