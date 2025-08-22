import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import TopBar from "../components/Layout/TopBar";
import ExpenseForm from "./ExpenseForm";
import { getExpense, updateExpense, deleteExpense } from "../api/expenses";
import ConfirmModal from "../components/Modal/ConfirmModal";
import s from "./Scaffold.module.css";

export default function EditExpensePage(){
  const nav = useNavigate();
  const [sp] = useSearchParams();
  const id = sp.get("id");

  const [initial, setInitial] = useState(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState("");

  // delete confirm modal
  const [showConfirm, setShowConfirm] = useState(false);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    (async () => {
      try {
        const d = await getExpense(id);
        setInitial({
          dateOfExpense: d?.dateOfExpense || "",
          category: d?.category || "",
          amount: d?.amount,
          description: d?.description || "",
        });
      } catch (e) {
        setErr(e?.message || "Failed to load expense");
      } finally {
        setLoading(false);
      }
    })();
  }, [id]);

  async function handleSubmit(payload){
    await updateExpense(id, payload);
    nav("/expenses");
  }

  async function handleConfirmDelete(){
    setDeleting(true);
    try{
      await deleteExpense(id);
      nav("/expenses");
    } finally{
      setDeleting(false);
      setShowConfirm(false);
    }
  }

  return (
    <div className={s.page}>
      <TopBar onSearch={()=>{}}/>
      <div className={s.container}>
        <div className={s.header}>
          <h2 className={s.h2}>Edit Expense</h2>
        </div>

        {err && <div className={s.error}>{err}</div>}

        {loading ? (
          <div className={s.loading}>Loading…</div>
        ) : (
          <ExpenseForm
            mode="edit"
            initial={initial}
            onSubmit={handleSubmit}
            onBack={() => nav("/expenses")}
            // Instead of native confirm(), open modal:
            onDelete={() => setShowConfirm(true)}
          />
        )}
      </div>

      {/* Confirm delete modal */}
      <ConfirmModal
        open={showConfirm}
        title="Delete this expense?"
        message="Are you sure? This action cannot be undone."
        confirmText="Yes, delete"
        cancelText="Cancel"
        loading={deleting}
        onConfirm={handleConfirmDelete}
        onCancel={() => setShowConfirm(false)}
      />
    </div>
  );
}
