import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import TopBar from "../components/Layout/TopBar";
import { listExpenses } from "../api/expenses";
import { Pencil, Plus } from "lucide-react";
import s from "./Expenses.module.css";

export default function ExpensesPage() {
  const nav = useNavigate();
  const [query, setQuery] = useState("");
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState("");

  useEffect(() => {
    (async () => {
      try {
        const data = await listExpenses();
        const arr = Array.isArray(data) ? data : [];
        // sort by dateOfExpense (newest first)
        arr.sort((a, b) => new Date(b.dateOfExpense) - new Date(a.dateOfExpense));
        setItems(arr);
      } catch (e) {
        setErr(e?.message || "Failed to load expenses");
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  const filtered = useMemo(() => {
    if (!query) return items;
    const t = query.toLowerCase();
    return items.filter((x) =>
      [x.category, x.description, x.amount?.toString(), x.dateOfExpense]
        .filter(Boolean)
        .some((v) => String(v).toLowerCase().includes(t))
    );
  }, [items, query]);

  return (
    <div className={s.page}>
      <TopBar onSearch={setQuery} />
      <div className={s.headerRow}>
        <h2 className={s.h2}>Expenses</h2>
        <button className={s.primary} onClick={() => nav("/expenses/new")}>
          <Plus size={18} />
          Add Expense
        </button>
      </div>

      <main className={s.main}>
        {loading && <div className={s.empty}>Loading…</div>}
        {err && <div className={s.error}>{err}</div>}
        {!loading && !err && filtered.length === 0 && (
          <div className={s.empty}>No expenses found.</div>
        )}

        <ul className={s.list}>
          {filtered.map((x) => (
            <li key={x.id} className={s.card}>
              <div className={s.left}>
                <div className={s.date}>{formatDate(x.dateOfExpense)}</div>
                <div className={s.badge}>{x.category}</div>
              </div>

              <div className={s.mid}>
                <div className={s.title}>{x.description || "—"}</div>
              </div>

              <div className={s.right}>
                <div className={s.amount}>{formatMoney(x.amount)}</div>
                <button
                  className={s.icon}
                  onClick={() => nav(`/expenses/edit?id=${x.id}`)}
                  title="Edit"
                >
                  <Pencil size={18} />
                </button>
              </div>
            </li>
          ))}
        </ul>
      </main>
    </div>
  );
}

function formatMoney(v) {
  if (v == null) return "€0.00";
  const n = Number(v);
  if (Number.isNaN(n)) return String(v);
  return new Intl.NumberFormat(undefined, {
    style: "currency",
    currency: "EUR",
    minimumFractionDigits: 2,
  }).format(n);
}

function formatDate(s) {
  // accepts "yyyy-MM-dd" or "dd/MM/yyyy"
  if (!s) return "—";
  if (/^\d{4}-\d{2}-\d{2}$/.test(s)) {
    const [y, m, d] = s.split("-");
    return `${d}/${m}/${y}`;
  }
  return s;
}
