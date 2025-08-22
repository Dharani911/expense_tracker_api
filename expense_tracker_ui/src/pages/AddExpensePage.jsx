import { useNavigate } from "react-router-dom";
import TopBar from "../components/Layout/TopBar";
import ExpenseForm from "./ExpenseForm";
import { createExpense } from "../api/expenses";
import s from "./Scaffold.module.css";

export default function AddExpensePage(){
  const nav = useNavigate();
  async function onSubmit(payload){ await createExpense(payload); nav("/expenses"); }

  return (
    <div className={s.page}>
      <TopBar onSearch={()=>{}}/>
      <div className={s.container}>
        <div className={s.header}>
          <h2 className={s.h2}>Add Expense</h2>
        </div>
        <ExpenseForm mode="create" onSubmit={onSubmit} onBack={()=>nav("/expenses")}/>
      </div>
    </div>
  );
}
