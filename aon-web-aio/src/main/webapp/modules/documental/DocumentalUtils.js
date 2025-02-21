import { AonDialog } from "../../components/aon-dialog.js";
import { AonSelect } from "../../components/aon-select.js";
import { MSG } from "../../environments/environments.js";
import { getCategories, getScopes, getTags, uploadFileDocumental } from "../../services/documentalService.js";
import { getReader } from "../../services/utils.js";
import { AonDocumental } from "./aon-documental.js";
// import { AonDocumental } from "./aon-documental.js";
import { ASESOR_TYPE_OPTION,
    ENTERPRISE_TYPE_OPTION, EMPLOYEE_TYPE_OPTION } from './DocumentalEnums.js';

export const uploadDocument = (file, data, success, error) => {
    if (file) {
        getReader(file).then(f => {
            const doc = {
                ...f,
                contentName: f.name,
                contentSize: f.size,
                category: data.category,
                tag: data.tag,
                scope: data.scope,
                type: data.type
            };
            uploadFileDocumental(doc)
                .then(r => success(file))
                .catch((e) => error(file, e));
        }).catch((e) => error(file, e));
    }
}
  

export const uploadDocuments = (el, files, dur) => {
    let d = new AonDialog();
    let rootPanel = document.getElementById("rootPanel");
    rootPanel.appendChild(d);
    d.clear();
    // if(isMobile()) d.width = '400px';
    d.setTitle(MSG.UPLOAD_FILE);
    d.setContent(uploadOption(dur));
    d.addAcceptAction(async() => {
      let arr = [];
      let data = {
          category: document.getElementById("aonDocumentalUploadCategory").value,
          scope: document.getElementById("aonDocumentalUploadScope").value,
          tag: document.getElementById("aonDocumentalUploadTag").value,
          type: document.getElementById("aonDocumentalUploadType").value
      }
      for await (let file of files) {
          let reader = await getReader(file).catch(()=>null);
          if(reader){
              let doc = await attach(reader, data);
              if(doc)
                arr.push(doc);
          }
      }
      el.value = null;
      if(arr.length>0){
        let aonComponent =  new AonDocumental();
        aonComponent.value = arr[0].id;
        rootPanel.innerHTML ="";
        rootPanel.appendChild(aonComponent);
      }
    });
    d.open();
}

export const uploadOption = (dur) => {
    let table = document.createElement('table');
    table.style.width = '100%';

    let tr2 = document.createElement('tr');
    table.appendChild(tr2);

    // CATEGORY
    let tdCategory = document.createElement('td');
    tdCategory.setAttribute('colspan', '1');

    let selCat = new AonSelect();
    selCat.id = "aonDocumentalUploadCategory";
    selCat.title = MSG.CATEGORY;
    tdCategory.appendChild(selCat);
    getCategories({domain: localStorage.getItem('aon_domain_id')}).then( categories => {
        selCat.options =  JSON.stringify(categories.map(c => {
          return {
            value: c.id,
            name: c.name
          }
        }));
    });

    tr2.appendChild(tdCategory);

    let tr3 = document.createElement('tr');
    table.appendChild(tr3);
    // SCOPE
    let tdScope = document.createElement('td');
    tdScope.setAttribute('colspan', '1');

    let selScp = new AonSelect();
    selScp.id = "aonDocumentalUploadScope";
    selScp.title = MSG.SCOPE;
    tdScope.appendChild(selScp);
    getScopes().then( scopes => {
        selScp.options = JSON.stringify(scopes.map(s => {
          return {
            value: s.id,
            name: s.name
          }
        }));
      });

    tr3.appendChild(tdScope);

    let tr4 = document.createElement('tr');
    table.appendChild(tr4);

    // TAG

    let tdTag = document.createElement('td');
    tdTag.setAttribute('colspan', '1');
    let selTag = new AonSelect();
    selTag.id = "aonDocumentalUploadTag";
    selTag.title = MSG.TAG;
    tdTag.appendChild(selTag);
    getTags({domain: localStorage.getItem('aon_domain_id')}).then( tags => {
        selTag.options = JSON.stringify(tags.map(t => {
          return {
            value: t.id,
            name: t.name
          }
        }));
    });

    tr4.appendChild(tdTag);

    let tr5 = document.createElement('tr');
    table.appendChild(tr5);

    // TYPE

    let tdType = document.createElement('td');
    tdType.setAttribute('colspan', '1');
    let selType = new AonSelect();
    selType.id = "aonDocumentalUploadType";
    selType.title = MSG.TYPE;

    let typeOptions = EMPLOYEE_TYPE_OPTION;
    if(dur.isDocumentalManager()) {
      typeOptions = ASESOR_TYPE_OPTION;
    } else if(dur.isDocumentalPortal()){
      typeOptions = ENTERPRISE_TYPE_OPTION;
    }
    selType.setOptions(typeOptions);

    tdType.appendChild(selType);
    tr5.appendChild(tdType);
    return table;
  }

const attach = async (reader, d) => {
    const data = {
      ...reader,
      contentName: reader.name,
      contentSize: reader.size,
      category: d.category,
      tag: d.tag,
      scope: d.scope,
      type: d.type
    };
    return await uploadFileDocumental(data).catch(e=>null);
}