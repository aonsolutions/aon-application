import { AonDate } from "../../components/aon-date.js";
import { AonTextArea } from "../../components/aon-textarea.js";
import { COLORS, CONSTANT, CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from "../../environments/environments.js";
import { MONTHS } from "../../models/enums.js";
import { deleteNote, saveNote } from "../../services/noteService.js";
import { setTime } from "../../services/utils.js";
import { setStyles } from "../../services/utilsComponents.js";
import { dateCustomDayHour } from "../signin/time-control/utils.js";

/**
 * 
 * @param {HTMLElement} ul ul appendNote 
 * @param {Note} note class 
 */
export const appendNote = (ul, note) => {
    let li = document.createElement(TAG.LI);
    ul.insertBefore(li, ul.firstChild);
    
    const idRand =  Math.random().toString(36).substring(7);
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
    textArea.placeholder = "Escribe una nota";
    textArea.id = `notes${idRand}`;
    textArea.title  =  note.note || MSG.NOTE;
    textArea.NOT_BACKGROUND = true;

    div.appendChild(textArea);
    if(note.note) textArea.value = note.note;

    const toolbarLeft = document.getElementById(textArea.LEFT)
    toolbarLeft.style.width = "80%";
    
    //SET STYLE BTN RIGHT TOOLBAR
    setStyles(document.getElementById(textArea.RIGHT),{
        paddingRight : 0,
        // display : "none",
        alignSelf : "flex-start"
    });
    
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

    textArea.addToolbarOptionRight({
        id: MATERIAL_ICONS.MORE_VERT,
        icon: MATERIAL_ICONS.MORE_VERT,
        name: MSG.OPTIONS
    },(ev) => dialogMoreVert(ev, note, li, div, textArea.id));
    

    textArea.draggableEnable(); 

    div.addEventListener(EVENT.FOCUSOUT, async() => {
        const {id} = await saveNote(note);
        note.setId(id);
    });

    if(note.date && new Date(note.date).isValid()) appendDate(div, note.date, textArea.id);
}

const dialogMoreVert = (ev, note, li, divParent, textAreaId) => {
    ev.preventDefault();

    const rect = ev.target.getBoundingClientRect();
    const top  = rect.top + (ev.clientY - rect.top);
    const left = rect.left + (ev.clientX - rect.left) + 50;
    const idRand =  Math.random().toString(36).substring(7);
    
    let dialog = document.getElementById("DialogNote");
    dialog.clear();
    let content = dialog.getContent()
    content.style.width = "133px";
    let moreActions = [{
            id: MATERIAL_ICONS.NOTIFICATION_ADD,
            icon: MATERIAL_ICONS.NOTIFICATION_ADD,
            name: "Recordatorio",
            fn : () =>  {
                dialog.clear();
                content.style.width = "250px";
                dialog.setContentTitle("Recordatorio");
                const aonDate = new AonDate(); 
                aonDate.id = "date"+ idRand;
                aonDate.name = "date"+ idRand;
                aonDate.title =  MSG.DATE;
                aonDate.addEventListener(EVENT.CHANGE, ()=>{
                    if(aonDate.value){
                        note.setDate(aonDate.value);
                        saveNote(note);
                        appendDate(divParent, aonDate.value, textAreaId);
                        dialog.close();
                    }
                })

                let divContent = document.createElement(TAG.DIV);
                divContent.style.margin = "0 10px";
                divContent.appendChild(aonDate);
                dialog.setContent(divContent);

                if(note.date && new Date(note.date).isValid()) aonDate.setDate(note.date);
    
                dialog.openPosition({top, left: (left - 100) });
            }
        },
        {
            name: MSG.DELETE,
            icon: MATERIAL_ICONS.DELETE,
            id: MATERIAL_ICONS.DELETE,
            fn : () =>  {
                if(confirm("Estas seguro de eliminar la nota?")){
                    deleteNote(note);
                    li.remove();
                }
            }
        }
    ];	
    dialog.setMenuOptions(moreActions, top, left);
    dialog.open();
} 


const appendDate = (parent, date, textAreaId)=>{
    let label = document.getElementById("label"+textAreaId);
    if(!label){
        let div = setStyles(document.createElement(TAG.DIV),{
            display: "flex",
            alignItems: "center",
            height: "18px",
            justifyContent: "center",
            minWidth: "35px",
            padding:" 3px 5px",
            cursor: "pointer",
            "-webkit-justify-content": "center",
            "-webkit-align-items": "center",
        })
        div.id = "div"+textAreaId;
        parent.appendChild(div);
    
        let icon = setStyles(document.createElement(TAG.DIV),{ height: "28px",  opacity: "0.54", width: "17px" })    
        icon.innerHTML = `<span class="material-icons-outlined">schedule</span>`;
        div.appendChild(icon);
    
        label = setStyles(document.createElement(TAG.LABEL),{
            border: "1px solid transparent",
            textAlign: "center",
            fontSize: "11px",
            margin: "0 6px",
            padding: "1px",
            whiteSpace: "nowrap"
        });
        label.id = "label"+textAreaId;
        div.appendChild(label);
    }
    label.innerText = dateFormat(date);//"16 abr 2022, 20:00";  
}

const dateFormat = (d) => {
    const date = new Date(d);
    let day = dateCustomDayHour(d);
    //, ${setTime(d)}
    return day ? day : `${date.getDate()} ${MONTHS[date.getMonth()]} ${date.getFullYear()}`;
}