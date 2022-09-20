import { CreateComponent } from "../../../components/CreateComponent.js";
import { CONSTANT, CSS, MSG, EVENT, MATERIAL_ICONS } from "../../../environments/environments.js";
import { createDiv, setAttributes, setStyles } from "../../../services/utilsComponents.js";
import { getAttach, deleteAttach } from '../../../services/fileService.js';
import { getReader } from '../../../services/utils.js';
import { AonAutosizeTextarea } from "../../../components/aon-autosize-textarea.js";
import { AonTextareaEditor } from "../../../components/aon-textarea-editor.js";
import { NewsEnums } from "../NewsEnums.js";
import {AonCategoryAdd} from "../../category/aon-category-add.js";
import { AonUpload } from "../../../components/aon-upload.js";



const createForm = (id, parent) => {
    const form = CreateComponent.createForm(id+"Form");
    parent.appendChild(form);

    const className = parent.isMobile() ? CSS.AON_MOBILE_SUB_CONTENT : CSS.AON_SUB_CONTENT;
    const divParent = createDiv({id: id+"Div", classes:[className]}).element;
    form.appendChild(divParent);

    let div;
    div = createDiv({classes:[CSS.AON_COL_XS_12, CSS.AON_COL_MD_5]});
    div.appendTo(divParent);
    const cardOne = CreateComponent.createAonCard({id: id+"cardOne", title:MSG.NOTIFICATION}, div);

    div = createDiv({classes:[CSS.AON_COL_SM_12, CSS.AON_COL_MD_7]});
    div.appendTo(divParent);
    const cardTwo = CreateComponent.createAonCard({id: id+"cardTwo", title:MSG.CONTENT}, div);

    return {
        cardOne,
        cardTwo
    }
}

const buildFormGeneral = (parent, news) => {
    let divC;

    buildChannel(parent, news);

    divC = createDiv({classes:[CSS.AON_COL_XS_12]})
    divC.appendTo(parent);
    const scope = CreateComponent.createAonSelect({
        attributes:{
            name:"scope",
            id:"scope",
            title:MSG.SCOPE,
            default:CONSTANT.TRUE,
            autocomplete: CONSTANT.OFF,
            required:true
        },
        events:{
            change: () => {
                news.setScope(scope.value ? scope.getDetail(): undefined);
            }
        }
    }, divC.element);


    divC = createDiv({classes:[CSS.AON_COL_XS_12]})
    divC.appendTo(parent);
    CreateComponent.createAonInput({
        attributes:{
            name:"title",
            id:"title",
            description:MSG.TITLE,
            required:true,
            value: news.getTitle() || ""
        },
        events:{
            keyup: ({target}) => {
                news.setTitle(target.value);
            }
        }
    }, divC.element);

    divC = createDiv({classes:[CSS.AON_COL_XS_12]})
    divC.appendTo(parent);
    const descriptionEl =  setAttributes(new AonAutosizeTextarea(),{
        name:"description",
        id: "description",
        title: MSG.DESCRIPTION
    });
    divC.appendChild(descriptionEl);

    descriptionEl.value = news.getDescription() || "";;
    
    descriptionEl.addEventListener(EVENT.KEYUP, ({target})=>{
        news.setDescription(target.value);
    });

    divC = createDiv({classes:[CSS.AON_COL_XS_8]})
    divC.appendTo(parent);
    const initDate = CreateComponent.createAonDate({
        attributes:{
            name:"init_date", 
            id:"init_date", 
            title:MSG.START_DATE
        },
        events:{
            change: ({target}) => {
                news.setInitDate(target.value);
            }
        }
    }, divC.element);


    if(news.getInitDate()){
        initDate.value = new Date(news.getInitDate())
    }

    divC = createDiv({classes:[CSS.AON_COL_XS_4]})
    divC.appendTo(parent);
    const initDateTime = CreateComponent.createAonInput({
        attributes:{
            name:"init_date_time",
            id:"init_date_time",
            description:MSG.HOUR,
            type:"time"
        }, events:{
            change: ({target}) => {
                news.setInitDateTime(target.value);
            }
        }
    }, divC.element);


    if(news.getInitDateTime()){
        initDateTime.value = news.getInitDateTime();
    }

    divC = createDiv({classes:[CSS.AON_COL_XS_8]})
    divC.appendTo(parent);
    const endDate = CreateComponent.createAonDate({
        attributes:{
            name:"end_date", 
            id:"end_date", 
            title:MSG.END_DATE
        },
        events:{
            change: ({target}) => {
                news.setEndDate(target.value);
            }
        }
    }, divC.element);

    if(news.getEndDate()){
        endDate.value = new Date(news.getEndDate())
    }

    divC = createDiv({classes:[CSS.AON_COL_XS_4]})
    divC.appendTo(parent);
    const endDateTime = CreateComponent.createAonInput({
        attributes:{
            name:"end_date_time",
            id:"end_date_time",
            description:MSG.HOUR,
            type:"time"
        }, events:{
            change: ({target}) => {
              news.setEndDateTime(target.value);
            }
        }
    }, divC.element);


    if(news.getEndDateTime()){
        endDateTime.value = news.getEndDateTime();
    }


    divC = createDiv({classes:[CSS.AON_COL_XS_12]})
    divC.appendTo(parent);
    CreateComponent.createAonSwitch(
      {
        attributes: {
          name: "active",
          id: MSG.ACTIVE,
          title: MSG.ACTIVE,
          checked: news.getActive(),
        },
        events: {
          change: ({ target }) => {
            news.setActive(target.checked);
          },
        },
      },
      divC.element
    );

    // AonUpload
    buildUnloadFile(parent, news);

    news.setType("COMMUNICATION");

    // buildNews(parent, true);
}

const buildUnloadFile = (parent, news)=>{
    const aonNewsAdd = document.getElementById(NewsEnums.VIEWS_NEWS.AON_NEWS_ADD);
    
    let divC = createDiv({classes:[CSS.AON_COL_XS_12], styles: {marginTop:"12px"}});
    divC.appendTo(parent);

    let aonUpload = new AonUpload();
    aonUpload.id = 'NewsUpload';
    aonUpload.setMessage(MSG.ATTACH_FILES_DRAGGING_DROPPING_BACKGROUND);
    aonUpload.setDeleteMessage(MSG.DELETE_BACKGROUND_CONFIRM);
    divC.appendChild(aonUpload);

    const attachType = "registry";

    if(news.getRattach()){

        let filter = {
          id: news.getRattach(),
          attachType
        };
    
        getAttach(filter).then(attach => {
            if(attach && attach.id){
                aonUpload.setAttach(attach);
            }
        });
      }
  
    
    aonUpload.addEventListener(EVENT.UPLOAD, ({detail}) => {
   
        if(!news.getRattach()){
            getReader(detail).then( async(f) => {
       
                f.attachType = attachType;
    
                news.setAttach(f);
            });
        }
    });
    
    aonUpload.addEventListener(EVENT.DELETE, async () => {
        if(news.getId()){

            const id = news.getRattach();
            if(id){
                news.setRattach(null);

                await aonNewsAdd.save(false);
                await deleteAttach({attachType, id});
            }
        }
    });
}

/**
 * buildChannel
 */
const buildChannel = (parent, news) => {
    let divC = createDiv({classes:[CSS.AON_COL_XS_11]})
    divC.appendTo(parent);

    let divB = createDiv({classes:[CSS.AON_COL_XS_1]})
    divB.appendTo(parent);

    let category = null;
    let button   = null;


    const setIconButton = (btn, edit) => {
        btn.icon = edit ? MATERIAL_ICONS.EDIT : MATERIAL_ICONS.OPEN_IN_NEW; 
        btn.title = edit ? MSG.EDIT : MSG.CREATE; 
    }

    category = CreateComponent.createAonSelect({
        attributes:{
            name:"category",
            id:"category",
            title:"Canal",
            required: true,
            default:CONSTANT.TRUE,
            autocomplete: CONSTANT.OFF
        },
        events:{
            change: () => {
                const categoryData = category.value ? category.getDetail(): undefined;
                news.setCategory(categoryData);

                setIconButton(button, categoryData);
            }
        }
    });

    button = CreateComponent.createAonIconButton({
        attributes:{
            id:"categoryAdd",
            icon:MATERIAL_ICONS.OPEN_IN_NEW,
            title:`Crear canal`
        },
        events:{
            click: () =>{  openCategoryDialog(category.getDetail()) }
        }
    });

    setIconButton(button, news.getCategory());

    divC.appendChild(category);

    divB.appendChild(button);

    setStyles(button.getButton(), {
        top:"12px",
        left:"-6px"
    })
}


const openCategoryDialog = (category) => {

    const categoryExist = category && category.id;

    const aonNewsAdd = document.getElementById(NewsEnums.VIEWS_NEWS.AON_NEWS_ADD);
    const application = aonNewsAdd.getApplication();
    
    const dialog = application.getDialog();
    
    if (!application.isMobile()) 
        dialog.width = '40%';
    
    dialog.clear();
    dialog.setTitle("Canal");
        
    const aonCategoryAdd = new AonCategoryAdd();
    if(category && category.id){
        aonCategoryAdd.data = category;
    }
    aonCategoryAdd.type = "ARTICLE";
    dialog.setContent(aonCategoryAdd);

    if(categoryExist){
        const btnRemove = dialog.addCancelAction(() =>{
            application.confirmDialog(MSG.DELETE, MSG.DELETE_CONFIRM+` ${category.name}`, async()=>{
                try {
                    const success = await aonCategoryAdd.delete();
                    if(success){
                        const categoryEl = document.getElementById("category");

                        if(categoryEl && category.id ==categoryEl.value){
                            categoryEl.clear();
                        }
 
                        aonNewsAdd.getCategorys();
                        dialog.close();
                    }
                } catch (error) {
                    application.showError(error);
                }
            });
        }, false);

        btnRemove.innerHTML = MSG.DELETE;
    }
    
    dialog.addSendAction(async()=>{
        application.startLoading();

        const success = await aonCategoryAdd.save();
        if(success) {
            aonNewsAdd.getCategorys();
            dialog.close();
        }

        application.stopLoading();
  
    }, MSG.SAVE);


        
    dialog.open();
}

// const buildNews = (parent, checked= false) => {
//     const aonNewsAdd = document.getElementById(NewsEnums.VIEWS_NEWS.AON_NEWS_ADD);

//     const news = aonNewsAdd.news;

//     const id = "divNews";

//     if(!document.getElementById(id)){
//         const divNews  = createDiv({}).element;
//         divNews.id = "divNews";
//         parent.appendChild(divNews);
    
//         let divC = createDiv({classes:[CSS.AON_COL_XS_12]}).element;
//         divC.style.margin = "10px auto";
//         divNews.appendChild(divC);
//         CreateComponent.createAonSwitch({
//             attributes:{
//                 name:"rss",
//                 id:"rss",
//                 title: "Publicar RSS", 
//                 checked
//             }, 
//             events:{
//                 change: ({target}) => {
//                     const isChecked = target.checked;
//                     news.setRss(isChecked);
//                     if(checked){
//                         buildChannel(divNews);
//                     } else {
//                         document.getElementById('divCategory').remove();
//                     }
//                 }
//             }
//         }, divC);
//     }

//     if(checked){
//         buildChannel(divNews);
//     }
// }

// const buildChannel = (parent)=>{

//     const aonNewsAdd = document.getElementById(NewsEnums.VIEWS_NEWS.AON_NEWS_ADD);

//     const news = aonNewsAdd.news;

//     let divCategory = createDiv({classes:[CSS.AON_COL_XS_12]}).element
//     divCategory.style.marginTop = "8px";
//     divCategory.id = "divCategory";
//     parent.appendChild(divCategory);

//     const category = CreateComponent.createAonSelect({
//         attributes:{
//             name:"category",
//             id:"category",
//             title:"Canal",
//             default:CONSTANT.TRUE,
//             autocomplete: CONSTANT.OFF
//         },
//         events:{
//             change: ({target}) => {
//                 news.setCategory(category.getDetail());
//             }
//         }
//     }, divCategory);

//     aonNewsAdd.getCategorys();
// }

const buildEditor = (parent, news, aonNewsAdd) =>{
    const aonTextAreaEditor = setAttributes(new AonTextareaEditor(),{
        id:"aonTextAreaEditor",
        placeholder:MSG.WRITE_A_DESCRIPTION,
        required:true,
        "text-box-min-height":"24em",
        'has-fullscreen-mode': true
    });

    aonTextAreaEditor.elementFilter = {
        undo: false,
        redo: false,
        font: !aonTextAreaEditor.isMobile(),
        fontSize: !aonTextAreaEditor.isMobile(),
        bold: true,
        italic: true,
        underline: true,
        color: true,
        backgroundColor: !aonTextAreaEditor.isMobile(),
        alignment: true,
        orderedList: !aonTextAreaEditor.isMobile(),
        unorderedList: !aonTextAreaEditor.isMobile(),
        indent: !aonTextAreaEditor.isMobile(),
        outdent: !aonTextAreaEditor.isMobile(),
        removeFormat: true,
        strikethrough: true,
        quote: !aonTextAreaEditor.isMobile(),
        hyperlink: true,
        attachment: true,
        editorMode: !aonTextAreaEditor.isMobile()
    };

    if ((navigator.userAgent.indexOf('Firefox') !== -1)) {
        aonTextAreaEditor.textBoxHeight = "24em";
    }

    aonTextAreaEditor.addEventListener(EVENT.INPUT, ()=>{
        news.setContent(aonTextAreaEditor.value);
    });
    
    parent.appendChild(aonTextAreaEditor);

    if(news.getContent()){
        aonTextAreaEditor.value = news.getContent();
    }

    aonTextAreaEditor.addEventListener("save", ()=> {
        aonNewsAdd.save(false);
    })
}

export const NewsAddUtils = {
    createForm,
    buildFormGeneral,
    buildEditor
}