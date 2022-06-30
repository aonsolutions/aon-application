import { AonDate } from "../../components/aon-date.js";
import { AonTextArea } from "../../components/aon-textarea.js";
import { COLORS, CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from "../../environments/environments.js";
import { MONTHS } from "../../models/enums.js";
import { deleteNote, saveNote } from "../../services/noteService.js";
import { addZero } from "../../services/utils.js";
import { setStyles } from "../../services/utilsComponents.js";
import { AonDateUtils } from "../utils/AonDateUtils.js";

/**
 * 
 * @param {HTMLElement} ul ul appendNote 
 * @param {Note} note class 
 */
export const appendNote = (ul, note) => {
    let li = document.createElement(TAG.LI);
    ul.insertBefore(li, ul.firstChild);
    
    const idRand = Math.random().toString(36).substring(7);
    let div = setStyles(document.createElement(TAG.DIV),{ borderRadius : "10px",  border : "1px solid #dadce0", margin : "3px 10px" });
    div.id ="div"+idRand;
    div.classList.add(CSS.NOTE_BOX_SHADOW);
    li.appendChild(div);

    let textArea = setStyles(new AonTextArea(),{
        boxShadow : "none",
        overflow : CONSTANT.HIDDEN,
        color : CSS.variable(COLORS.AON_GRAY),
        marginTop : 0
    });
    textArea.placeholder = MSG.WRITE_A_NOTE;
    textArea.id = `notes${idRand}`;
    textArea.title  =  note.note || MSG.NOTE;
    textArea.NOT_BACKGROUND = true;

    div.appendChild(textArea);
    if(note.note) textArea.value = note.note;

    const toolbarLeft = document.getElementById(textArea.LEFT)
    toolbarLeft.style.width = "80%";
    
    //SET STYLE BTN RIGHT TOOLBAR
    setStyles(document.getElementById(textArea.RIGHT),{ paddingRight : 0, alignSelf : "flex-start" });
    
    //CHANGE STYLE TOOLBAR
    setStyles(textArea.getToolbar(),{ borderBottom:"none", color :"black"});

    textArea.addEventListener(EVENT.INPUT, ()=> note.setNote(textArea.value) );

    let subject = setStyles(document.createElement(TAG.DIV),{ 
        width: "100%", 
        padding: "10px 3px",
        border:"none", 
        color : CSS.variable(COLORS.AON_GRAY),  
        fontWeight: "500"
    });
    subject.setAttribute("contentEditable", true);
    subject.setAttribute("placeholder", MSG.TITLE);
    subject.classList.add(CSS.CONTENT_EDITABLE, CSS.NO_FOCUS, CSS.TRANSITION_QUICK);
    textArea.addToolbarLeft(subject,()=>{});
    subject.addEventListener(EVENT.INPUT,({target})=> target.innerText.length <= 60 ? note.setSubject(target.innerText) : false);
    subject.addEventListener(EVENT.KEYPRESS,(ev)=> ev.target.innerText.length >= 60 ? ev.preventDefault() : true );
    if(note.subject) subject.innerText = note.subject;

    const textAreaId = textArea.id;
    textArea.addToolbarOptionRight({
        id: MATERIAL_ICONS.MORE_VERT+textAreaId,
        icon: MATERIAL_ICONS.MORE_VERT,
        name: MSG.OPTIONS
    },(ev) => dialogMoreVert(ev, li, note, textAreaId));
    

    textArea.draggableEnable(); 
    div.addEventListener(EVENT.FOCUSOUT, async() => {
        const {id} = await saveNote(note);
        note.setId(id);
    });

    if(note.date && new Date(note.date).isValid()) appendDate(note, textArea.id);

    if(!note.id)
        subject.focus();
}

const dialogMoreVert = (ev, li, note, textAreaId) => {
    ev.preventDefault();
    
    let dialog = document.getElementById("DialogNote");
    dialog.clear();
    let content = dialog.getContent()
    content.style.width = "133px";

    let moreActions = [{
            id: MATERIAL_ICONS.NOTIFICATION_ADD,
            icon: MATERIAL_ICONS.NOTIFICATION_ADD,
            name: MSG.REMINDER,
            fn : (e) => reminder(e, note, textAreaId)
        },
        {
            name: MSG.DELETE,
            icon: MATERIAL_ICONS.DELETE,
            id: MATERIAL_ICONS.DELETE,
            fn : () =>  {
                deleteNote(note);
                li.remove();
            }
        }
    ];	
    const rect = ev.target.getBoundingClientRect();
    const top  = rect.top + (ev.clientY - rect.top);
    const left = rect.left + (ev.clientX - rect.left) + 50;

    dialog.setMenuOptions(moreActions, top, left);
    dialog.open();
} 

const appendDate = (note, textAreaId)=>{
    const parent = document.getElementById(textAreaId).parentNode;
    if(parent){
        const date = note.date;
        let idDiv = "div"+textAreaId;
        let color = COLORS.AON_GRAY;
        let today = new Date().setHours(0,0,0,0);
        let newDate = new Date(date).setHours(0,0,0,0);
        if(today === newDate)    color = COLORS.ONLINE_GREEN;
        else if(today > newDate) color = COLORS.MATERIAL_RED;

        let divMain = document.getElementById(idDiv);
        if(divMain) divMain.remove();

        divMain = setStyles(document.createElement(TAG.DIV), { display:"flex", justifyContent:"end"});
        divMain.id = idDiv;

        divMain.setAttribute("tabindex",0);
        parent.appendChild(divMain);
    
        let div = setStyles(document.createElement(TAG.DIV),{ 
            height: "18px", 
            minWidth: "35px", 
            cursor: "pointer", 
            borderRadius:"7px",
            margin:"3px",
            border: `1px solid #E9E7E7`
        });
        divMain.appendChild(div);

        div.style.color = CSS.variable(color);

        let reloj = setStyles(document.createElement(TAG.SPAN),{ height: "28px", opacity: "0.54", fontSize: "16px"});
        reloj.classList.add(CONSTANT.MATERIAL_ICONS_OUTLINED);
        reloj.innerText = MATERIAL_ICONS.SCHEDULE;
        reloj.addEventListener(EVENT.CLICK, (ev)=> reminder(ev, note, textAreaId) );
        div.appendChild(reloj);
    
        let label = setStyles(document.createElement(TAG.LABEL),{ border: "1px solid transparent", fontSize: "11px", position:"relative", top:"-4px", cursor:"pointer"});
        label.classList.add(CSS.FIRST_LETTER_UPPER);
        label.id = "label"+textAreaId;
        label.addEventListener(EVENT.CLICK, (ev)=> reminder(ev, note, textAreaId) );
        div.appendChild(label);
        label.innerText = dateFormat(note.date);

        let iconX = setStyles(document.createElement(TAG.SPAN),{ height: "28px", opacity: "0.54", fontSize: "16px", display:"none"});
        iconX.classList.add(CONSTANT.MATERIAL_ICONS_OUTLINED);
        iconX.innerText = MATERIAL_ICONS.CLOSE;
        iconX.addEventListener(EVENT.CLICK, ()=> {
            note.setDate(null);
            saveNote(note);
            divMain.remove();
        });
        div.appendChild(iconX);

        div.addEventListener(EVENT.MOUSELEAVE,()=> iconX.style.display = "none" )
        div.addEventListener(EVENT.MOUSEOVER,()=> iconX.style.display = "contents" );
    }
}


const reminder = (ev, note, textAreaId) => {
    const dialog = document.getElementById("DialogNote");
    const rect = ev.target.getBoundingClientRect();
    const top  = rect.top + (ev.clientY - rect.top);
    const left = rect.left + (ev.clientX - rect.left) + 50;
    const content = dialog.getContent();

    const idRand =  Math.random().toString(36).substring(7);
    dialog.clear();
    content.style.width = "250px";
    dialog.setContentTitle(MSG.REMINDER);
    const aonDate = new AonDate(); 
    aonDate.id = "date"+ idRand;
    aonDate.name = "date"+ idRand;
    aonDate.title =  MSG.DATE;
    aonDate.addEventListener(EVENT.CHANGE, ()=>{
        if(aonDate.value){
            note.setDate(aonDate.value);
            saveNote(note);
            appendDate(note, textAreaId);
            dialog.close();
        }
    });
    let divContent = document.createElement(TAG.DIV);
    divContent.style.margin = "0 10px";
    divContent.appendChild(aonDate);
    dialog.setContent(divContent);

    if(note.date && new Date(note.date).isValid()) aonDate.setDate(note.date);

    dialog.openPosition({top, left: (left - 100) });
}

const dateFormat = (d) => {
    let day = AonDateUtils.lastThreeDayStr(d);
    const date = new Date(d);
    return day ? day : `${addZero(date.getDate(),2)} ${MONTHS[date.getMonth()]} ${date.getFullYear()}`;
}