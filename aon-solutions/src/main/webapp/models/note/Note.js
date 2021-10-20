import { Domain } from "../Domain.js";

export class Note {
  id;
  domain;
  subject;
  date;
  owner;
  note;

  constructor(note) {
    if(note) {
      this.id          = note.id || undefined;
      this.domain      = note.domain || new Domain().getId();
      this.subject     = note.subject || "";
      this.note        = note.note || "";
      this.owner       = note.owner || undefined;
      this.date        = note.date && !note.date.includes("0001-01-01") ? new Date(note.date) : null;
    } else {
      this.id          = undefined;
      this.domain      = new Domain().getId(); 
      this.subject     = "";
      this.note        = undefined;
      this.owner       = undefined;
      this.date        = null;
    }
  }

  getId() {
    return this.id;
  }

  setId(id) {
    this.id = id;
  }

  getSubject() {
    return this.subject;
  }

  setSubject(subject) {
    this.subject = subject;
  }

  getDate() {
    return this.date;
  }

  setDate(date) {
    this.date = date;
  }

  getOwner() {
    return this.owner;
  }

  setOwner(owner) {
    this.owner = owner;
  }

  getNote() {
    return this.note;
  }

  setNote(note) {
    this.note = note;
  }

  getDomain() {
    return this.domain;
  }

  setDomain(domain) {
    this.domain = domain;
  }

}

