import { CreateComponent } from "../../../components/CreateComponent.js";
import { CONSTANT, CSS, MSG, TAG, EVENT, MATERIAL_ICONS } from "../../../environments/environments.js";
import { createDiv, setAttributes, setStyles } from "../../../services/utilsComponents.js";
import { AonAutosizeTextarea } from "../../../components/aon-autosize-textarea.js";
import { AonTextareaEditor } from "../../../components/aon-textarea-editor.js";
import { NewsEnums } from "../NewsEnums.js";
import {AonCategoryAdd} from "../../category/aon-category-add.js";


const createForm = (id, parent) => {
    const form = CreateComponent.createForm(id+"Form").element;
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

    buildCategory(parent, news);

    divC = createDiv({classes:[CSS.AON_COL_XS_12]})
    divC.appendTo(parent);
    CreateComponent.createAonSelect({
        attributes:{
            name:"scope",
            id:"scope",
            title:MSG.SCOPE,
            default:CONSTANT.TRUE,
            autocomplete: CONSTANT.OFF,
            required:true
        },
        events:{
            change: ({target}) => {
                news.setScope(target.getDetail());
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
            required:true
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
    
    descriptionEl.addEventListener(EVENT.KEYUP, ({target})=>{
        news.setDescription(target.value);
    });

    divC = createDiv({classes:[CSS.AON_COL_XS_6]})
    divC.appendTo(parent);
    CreateComponent.createAonDate({
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

    divC = createDiv({classes:[CSS.AON_COL_XS_6]})
    divC.appendTo(parent);
    CreateComponent.createAonDate({
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

    divC = createDiv({classes:[CSS.AON_COL_XS_12]})
    divC.appendTo(parent);
    CreateComponent.createAonSwitch(
      {
        attributes: {
          name: "active",
          id: MSG.ACTIVE,
          title: MSG.ACTIVE,
          checked: true,
        },
        events: {
          change: ({ target }) => {
            news.setActive(target.checked);
          },
        },
      },
      divC.element
    );


    news.setType("COMMUNICATION");

    // buildNews(parent, true);
  
}

/**
 * buildChannel
 */
const buildCategory = (parent, news) => {
    let divC = createDiv({classes:[CSS.AON_COL_XS_11]})
    divC.appendTo(parent);
    CreateComponent.createAonSelect({
        attributes:{
            name:"category",
            id:"category",
            title:"Canal",
            default:CONSTANT.TRUE,
            autocomplete: CONSTANT.OFF,
            required: true
        },
        events:{
            change: ({target}) => {
                news.setCategory(target.getDetail());
            }
        }
    }, divC.element);

    divC = createDiv({classes:[CSS.AON_COL_XS_1]})
    divC.appendTo(parent);
    const button = CreateComponent.createAonIconButton({
        attributes:{
            id:"categoryAdd",
            icon:MATERIAL_ICONS.OPEN_IN_NEW,
            title:`Crear canal`
        },
        events:{
            click: () =>{  openCategoryDialog() }
        }
    }, divC.element);

    setStyles(button.getButton(), {
        top:"12px",
        left:"-6px"
    })
}


const openCategoryDialog = () => {
    const aonNewsAdd = document.getElementById(NewsEnums.VIEWS_NEWS.AON_NEWS_ADD);
    const application = aonNewsAdd.getApplication();
    
    const dialog = application.getDialog();
    
    if (!application.isMobile()) 
        dialog.width = '40%';
    
    dialog.clear();
    dialog.setTitle("Canal");
        

    const aonCategoryAdd = new AonCategoryAdd();
    aonCategoryAdd.type = "ARTICLE";
    dialog.setContent(aonCategoryAdd);

    
    dialog.addSendAction(async()=>{
        application.startLoading();

        const success = await aonCategoryAdd.save();
        if(success) {
            aonNewsAdd.getCategorys();
            dialog.close();
        }

        application.stopLoading();
  
    }, MSG.CREATE)
        
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
//                         buildCategory(divNews);
//                     } else {
//                         document.getElementById('divCategory').remove();
//                     }
//                 }
//             }
//         }, divC);
//     }

//     if(checked){
//         buildCategory(divNews);
//     }
// }

// const buildCategory = (parent)=>{

//     const aonNewsAdd = document.getElementById(NewsEnums.VIEWS_NEWS.AON_NEWS_ADD);

//     const news = aonNewsAdd.news;

//     let divCategory = createDiv({classes:[CSS.AON_COL_XS_12]}).element
//     divCategory.style.marginTop = "8px";
//     divCategory.id = "divCategory";
//     parent.appendChild(divCategory);

//     CreateComponent.createAonSelect({
//         attributes:{
//             name:"category",
//             id:"category",
//             title:"Canal",
//             default:CONSTANT.TRUE,
//             autocomplete: CONSTANT.OFF
//         },
//         events:{
//             change: ({target}) => {
//                 news.setCategory(target.getDetail());
//             }
//         }
//     }, divCategory);

//     aonNewsAdd.getCategorys();
// }

const buildEditor = (parent, news) =>{
    const aonTextAreaEditor = setAttributes(new AonTextareaEditor(),{
        id:"aonTextAreaEditor",
        placeholder:MSG.CONTENT,
        required:true,
        // "bar-integrated": true
    });
    aonTextAreaEditor.style.height = "24em";

    aonTextAreaEditor.addEventListener(EVENT.KEYUP, ()=>{
        news.setContent(aonTextAreaEditor.value);
    });
    
    parent.appendChild(aonTextAreaEditor);
} 

export const NewsAddUtils = {
    createForm,
    buildFormGeneral,
    buildEditor
}