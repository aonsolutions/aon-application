import { post, get, remove } from "./request.js";
import * as LS from "./localStorageService.js";
import { API_URL } from "../environments/environments.js";
import { sortBy } from "./utils.js";

export const saveNote = async (data) => {
	let notes = await getNotes();
    if(data.id) notes = notes.filter(nt => nt.id !=data.id);
    notes.push(data);
    setNotes(notes);
}
export const deleteNote = async (data) => {
    let notes = await getNotes();
    if(data.id) notes = notes.filter(nt => nt.id !=data.id);
    setNotes(notes);
};

export const getNotes = async () => new Promise((resolve)=>{
    let notes = JSON.parse(LS.get("notes") ? LS.get("notes") : "[]");
   
    resolve(  sortBy(notes, 'date', 'desc') );
});

const setNotes = (notes)=> LS.set("notes", JSON.stringify(notes));