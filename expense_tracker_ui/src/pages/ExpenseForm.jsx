import { useEffect, useMemo, useRef, useState } from "react";
import { CalendarDays, ChevronDown, Coins, MessageSquare, Save, ArrowLeft, Trash2 } from "lucide-react";
import { getCategories } from "../api/expenses";
import s from "./ExpenseForm.module.css";

const DDMMYYYY = /^(\d{2})\/(\d{2})\/(\d{4})$/;
const toISO=(d)=>{ const m=DDMMYYYY.exec(d||""); return m?`${m[3]}-${m[2]}-${m[1]}`:""; }
const isoToday = ()=> new Date().toISOString().slice(0,10);
const toDDMMYYYY=(iso)=>{ if(!iso) return ""; const [y,m,d]=iso.split("-"); return `${d}/${m}/${y}`; }

export default function ExpenseForm({ initial, mode="create", onSubmit, onBack, onDelete }){
  const [cats,setCats]=useState([]); const [loading,setLoading]=useState(false); const [err,setErr]=useState("");
  const [f,setF]=useState({
    dateOfExpense: initial?.dateOfExpense ? (DDMMYYYY.test(initial.dateOfExpense)?initial.dateOfExpense:toDDMMYYYY(initial.dateOfExpense)) : toDDMMYYYY(isoToday()),
    category: (initial?.category||"").toUpperCase(),
    amount: initial?.amount ?? "",
    description: initial?.description || ""
  });

  useEffect(()=>{ (async()=>{ try{ setCats(await getCategories()); }catch{ setCats([]); } })(); },[]);

  const dateRef = useRef(null);
  function openPicker(){ dateRef.current?.showPicker?.(); if(!dateRef.current?.showPicker) dateRef.current?.click?.(); }
  function onNativeChange(e){ setF(p=>({...p, dateOfExpense: toDDMMYYYY(e.target.value)})); }

  async function submit(e){
    e.preventDefault(); setErr("");

    if(!DDMMYYYY.test(f.dateOfExpense)) return setErr("Please use dd/MM/yyyy.");
    if(!f.category) return setErr("Category is required.");

    const amt = Number(f.amount);
    if(!(amt>=0.01)) return setErr("Amount must be greater than 0.");
    if(f.description && f.description.length>1000) return setErr("Description must be 1000 characters or less.");

    setLoading(true);
    try{
      await onSubmit({
        // ⬇️ send ISO to backend, keep UI dd/MM/yyyy
        dateOfExpense: toISO(f.dateOfExpense),
        category: String(f.category).toUpperCase(),
        amount: Math.round(amt*100)/100,
        description: (f.description||"").trim(),
      });
    }catch(e2){
      setErr(e2?.message || "Failed to save");
    }finally{
      setLoading(false);
    }
  }

  return (
    <form className={s.form} onSubmit={submit}>
      {err && <div className={s.alert}>{err}</div>}

      <div className={s.grid}>
        <label className={s.block}>
          <span className={s.label}>Date *</span>
          <div className={s.inputRow}>
            <input className={s.inputBare} placeholder="dd/MM/yyyy" value={f.dateOfExpense} onChange={e=>setF({...f,dateOfExpense:e.target.value})}/>
            <input type="date" ref={dateRef} value={toISO(f.dateOfExpense)||""} onChange={onNativeChange} style={{position:"absolute",opacity:0,pointerEvents:"none",width:0,height:0}} aria-hidden tabIndex={-1}/>
            <button className={s.iconBtn} type="button" onClick={openPicker}><CalendarDays size={18}/></button>
          </div>
        </label>

        <label className={s.block}>
          <span className={s.label}>Category *</span>
          <div className={s.inputRow}>
            <select className={`${s.inputBare} ${s.select}`} value={f.category} onChange={e=>setF({...f,category:e.target.value.toUpperCase()})}>
              {!f.category && <option value="">Select…</option>}
              {cats.map(c=><option key={c} value={c}>{c}</option>)}
            </select>
            <span className={s.iconTrail}><ChevronDown size={18}/></span>
          </div>
        </label>

        <label className={s.block}>
          <span className={s.label}>Amount *</span>
          <div className={s.inputRow}>
            <input className={s.inputBare} type="number" step="0.01" min="0.01" max="9999999999.99" placeholder="0.00" value={f.amount} onChange={e=>setF({...f,amount:e.target.value})}/>
            <span className={s.iconTrail}><Coins size={18}/></span>
          </div>
        </label>

        <label className={`${s.block} ${s.full}`}>
          <span className={s.label}>Description</span>
          <textarea className={`${s.input} ${s.textarea}`} rows={3} maxLength={1000} value={f.description} onChange={e=>setF({...f,description:e.target.value})} placeholder="Add a note (optional)"/>
        </label>
      </div>

      <div className={s.actions}>
        <button className={s.primary} disabled={loading}><Save size={18}/> {mode==="edit"?(loading?"Updating…":"Update"):(loading?"Saving…":"Save")}</button>
        <button type="button" className={s.secondary} onClick={onBack}><ArrowLeft size={18}/> Back</button>
        {mode==="edit" && onDelete && (
          <button type="button" className={s.danger} onClick={onDelete}><Trash2 size={18}/> Delete</button>
        )}
      </div>
    </form>
  );
}
