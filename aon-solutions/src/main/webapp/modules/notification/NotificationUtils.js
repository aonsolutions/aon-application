import { CreateComponent } from "../../components/CreateComponent.js";
import { COLORS, CONSTANT, CSS, MSG, TAG } from "../../environments/environments.js";
import { setStyles } from "../../services/utilsComponents.js";
import { firstLetters } from "../timecontrol/time-control/utils.js";
import { AonDateUtils } from "../utils/AonDateUtils.js";
import { NotificationEnums } from "./NotificationEnums.js";


const buildDesk = () => {
  let divParent = setStyles(document.createElement(TAG.DIV),{
    color: CSS.variable(COLORS.AON_GRAY),
    boxSizing: "border-box",
    margin: "0",
    minWidth: "0",
    width: "100%"
  });

  return divParent;
}

const buildRow = (res)=>{
  const aonNotification = document.getElementById(NotificationEnums.NOTIFICATION_IDS.AON_NOTIFICATION_DESK);
   
  const id = res.id;
  const row = document.createElement(TAG.DIV);
  row.className = "divRowHover";
  row.style = `
    margin: 0px;
    min-width: 0px;
    cursor: pointer;
    padding-top: 24px;
    padding-left: 40px;
    padding-right: 40px;
    display:block;
  `;

  row.appendChild( buildTitle(res) );

  row.appendChild( buildSubTitle(res) );

  const expandMore = "expand_more";
  const expandLess = "expand_less";
  row.onclick = () => {
    aonNotification.markReadNotification(res);

    const expandIcon = document.getElementById('notificationExpandIcon'+id);
  
    const subTitle = document.getElementById('notificationSubTitle'+id);

    const expand = expandIcon.innerText === expandMore;

    expandIcon.innerText = expand ? expandLess : expandMore;

    if(expand && res.source && res.source_id){
      const subTitleDiv = document.getElementById('notificationDivSubTitle'+id)
      subTitleDiv.appendChild(buildButtonMore(true, res));
    } else {
      buildButtonMore(false, res);
    }

    subTitle.style = getStyleExpand(expand);
  }
  

  return row;
}


const buildTitle = (res)=>{
  let div = document.createElement(TAG.DIV);
  div.style = `
    margin: 0px;
    min-width: 0px;
    display: flex;
    -webkit-box-pack: justify;
    justify-content: space-between;
  `;

  let campaignIcon = document.createElement(TAG.SPAN);
  campaignIcon.className = 'material-icons';
  campaignIcon.innerText = 'campaign';

  campaignIcon.style = `
    margin: 0px;
    min-width: 0px;
    display: flex;
    width: 26px;
    padding-top: 4px;
  `;
  div.appendChild(campaignIcon);

  let divTitle = document.createElement(TAG.DIV);
  divTitle.style = `
    margin: 0px;
    min-width: 0px;
    display: flex;
    -webkit-box-align: center;
    align-items: center;
    -webkit-box-flex: 1;
    flex-grow: 1;
  `;
  div.appendChild(divTitle);
  
  let label = document.createElement(TAG.DIV);
  label.innerText = res.title;
  label.style = `
    margin: 0px;
    min-width: 0px;
    font-weight: 500;
    font-size: 16px;
    line-height: 24px;
    color: rgb(112, 122, 138);
  `;
  divTitle.appendChild(label);
  
  let divDate = document.createElement(TAG.DIV);
  divDate.style =`
    margin: 0px;
    min-width: 0px;
    display: flex;
    padding-top: 4px;
    -webkit-box-pack: end;
    justify-content: flex-end;
    color: rgb(112, 122, 138);
  `;
  div.appendChild(divDate);

  let labelDate = document.createElement(TAG.DIV);
  labelDate.innerText = firstLetters(AonDateUtils.setFullDate(res.date)) + " " + AonDateUtils.setTime(res.date);
  labelDate.style = `
    margin: 0px 10px 0px 0px;
    min-width: 0px;
    font-weight: 400;
    font-size: 12px;
    line-height: 16px;
  `;
  divDate.appendChild(labelDate);

  let expandIcon = document.createElement(TAG.SPAN);
  expandIcon.id = "notificationExpandIcon"+res.id;
  expandIcon.className = 'material-icons';
  expandIcon.innerText = 'expand_more'; // expand_less
  expandIcon.style = `
    margin: 0px;
    min-width: 0px;
    color: rgb(112, 122, 138);
    font-size: 16px;
    fill: rgb(112, 122, 138);
    transform: rotate(180deg);
    transition: all 0.3s ease 0s;
    width: 1em;
    height: 1em;
  `;

  divDate.appendChild(expandIcon);

  return div;
}


const buildSubTitle = (res)=>{
  let div = document.createElement(TAG.DIV);
  div.style = `
    margin: 0px;
    min-width: 0px;
    border-bottom: 1px solid rgb(234, 236, 239);
    padding-bottom: 24px;
  `
  let divTwo = document.createElement(TAG.DIV);
  divTwo.id = "notificationDivSubTitle"+res.id;
  divTwo.style = `
    margin: 8px 0px 0px;
    min-width: 0px;
    font-size: 14px;
    line-height: 20px;
    color: rgb(112, 122, 138);
  `;
  div.appendChild(divTwo);

  let subTitle = document.createElement(TAG.DIV);
  subTitle.id = "notificationSubTitle"+res.id;
  subTitle.style = getStyleExpand(false);
  subTitle.innerHTML = res.body;
  divTwo.appendChild(subTitle);

  return div;
}

const getStyleExpand = (b) => {
  return  b ? 
  `
    margin: 8px 0px 0px;
    min-width: 0px;
    font-size: 14px;
    line-height: 20px;
    color: rgb(112, 122, 138);
  `
  :
  `
    margin: 0px;
    min-width: 0px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    font-size: 14px;
    line-height: 20px;
    color: rgb(112, 122, 138);
  `;
}

const buildButtonMore = (add, res) => {
  const id = "notificationSeeMore"+res.id;
  let btn = document.getElementById(id) || document.createElement(TAG.A);
  if(add){
    const aonNotification = document.getElementById(NotificationEnums.NOTIFICATION_IDS.AON_NOTIFICATION_DESK);
    btn.id = id;
    btn.className = CSS.AON_LINK;
    btn.innerText = "Ver más";
    btn.onclick = (ev) => {
      ev.preventDefault();
      ev.stopPropagation();
      aonNotification.goNotification(res);
    }
    return btn;
  } else {
    btn.remove();
  }
}

const buildDialogAdd = (idForm) =>{
    const form = CreateComponent.createForm(idForm);
    CreateComponent.createAonSelect({
      attributes:{
        name:"type",
        id:"type",
        title:MSG.TYPE,
        options: NotificationEnums.TYPE_USER,
        value: NotificationEnums.TYPE_USER[0].value
      },   
      events: {
        change: ({detail})=>{
          if(detail && detail.value) {
            hiddenElement(detail.value);
          }
        }
      },
    }, form);

    CreateComponent.createAonSelect({
      attributes:{
        name:"task_holder",
        id:"task_holder",
        title: MSG.EMPLOYEE
      }
    }, form);
    
    CreateComponent.createAonInput({
      attributes:{
        name:"email",
        id:"email",
        description:MSG.EMAIL,
        type:"text",
        hidden:true
      }
    }, form);

    CreateComponent.createAonInput({
      attributes:{
        name:"title", 
        id:"title", 
        description:MSG.TITLE,
        type:"text"
      }
    }, form);

    CreateComponent.createAonInput({
      attributes:{
        name:"body",
        id:"body",
        description:"Mensaje",
        type:"text"
      }
    }, form);

    return form;
}

const hiddenElement = (value)=> {
  let taskHolderEl = document.getElementById("task_holder");
  let emailEl = document.getElementById("email");
    if("employee"===value){
      taskHolderEl.hidden = false;
      emailEl.setAttribute(CONSTANT.HIDDEN, true);
    } else {
      taskHolderEl.hidden = true;
      emailEl.removeAttribute(CONSTANT.HIDDEN);
    }
}

// const buildTitleDesk = (res) => {
//   const title = res.title || "Sin asunto";
//   let divParent = setStyles(document.createElement(TAG.DIV), {
//     position: "relative",
//     padding: "5px"
//   });

//   let div = setStyles(document.createElement(TAG.DIV), {
//     top: "-4px",
//     position: "absolute",
//     left: "0",
//     right: "0",
//   });
//   divParent.appendChild(div);

//   let divFlex = setStyles(document.createElement(TAG.DIV), {
//     display: "flex",
//     whiteSpace: "nowrap",
//   });

//   div.appendChild(divFlex);

//   const divOne = setStyles(document.createElement(TAG.DIV), {
//     fontWeight: "550",
//     fontSize: "14px",
//     overflow: CONSTANT.HIDDEN,
//     textOverflow: "ellipsis",
//     color: CSS.variable(COLORS.AON_BLUE)
//   });
//   divOne.innerText = title;
//   divOne.title = title;

//   divFlex.appendChild(divOne);
  

//   return divParent;
// }

// const buildSubtitleDesk =({body}) => {

//   let divParent = setStyles(document.createElement(TAG.DIV), {
//     position: "relative",
//     padding:"11px"
//   });

//   //----------- DESCRIPTION

//   let div = setStyles(document.createElement(TAG.DIV), {
//     position: "absolute",
//     top: "6px",
//     left: "0",
//     right: "0",
//     whiteSpace: "nowrap",
//     textOverflow: "ellipsis",
//     overflow: "hidden",
//   });
//   divParent.appendChild(div);

//   let span = setStyles(document.createElement(TAG.SPAN), {
//     color:  CSS.variable(COLORS.AON_GRAY),
//     marginLeft: "3px",
//   });

//   span.innerHTML = body;
//   span.title = body.replace(/<[^>]+>|&nbsp;|\n/g, " ");
//   div.appendChild(span);
  
//   return divParent;
// }



export const NotificationUtils = {
    buildDesk,
    buildRow,
    buildDialogAdd
}