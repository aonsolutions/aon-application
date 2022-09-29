import { AonDialogMenu } from "../../components/aon-dialog-menu.js";
import { AonElement } from "../../components/AonElement.js";
import { Note } from "../../models/note/Note.js";
import { getNotes } from "../../services/noteService.js";
import { sortBy } from "../../services/utils.js";
import {NotesUtils} from "./NotesUtils.js"

export class AonNote extends AonElement {

  DIALOG;
  UL;
  constructor() {
    super();
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {
    this.id = "AonNote";
  }


  build(){
    this.paintView();
    this.buildNotes();
  }

  paintView(){
    this.paintDialog();
    this.paintContent();
  }

  paintDialog(){
    this.DIALOG = new AonDialogMenu();
    this.DIALOG.id = this.id+"Dialog";
    this.appendChild(this.DIALOG);
  }

  paintContent(){
    this.UL = document.createElement("ul");
    this.UL.id = this.id+"Ul";
    this.UL.style.listStyle = "none";
    this.UL.style.margin    = "0";
    this.UL.style.padding   = "0";

    this.appendChild(this.UL);
  }

  async buildNotes(){
    try {
      let notes = await getNotes();

      sortBy(notes, 'date', 'desc')
      .forEach(note=> this.addNote(note))
    } catch (error) {
      //alert(error);
    }
  }

  /**
   * 
   * @param {Object or null} note 
   */
  addNote(note){
    NotesUtils.addNote(this, note);
  }
  
}
window.customElements.define("aon-note", AonNote);
