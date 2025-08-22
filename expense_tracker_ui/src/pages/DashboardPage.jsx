import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import TopBar from "../components/Layout/TopBar";
import SectionBar from "../components/Layout/SectionBar";
import { listExpenses } from "../api/expenses";
import { CalendarDays, Receipt, PiggyBank } from "lucide-react";
import {
  ResponsiveContainer,
  AreaChart, Area, XAxis, YAxis, Tooltip, CartesianGrid,
  PieChart, Pie, Cell, Legend
} from "recharts";
import s from "./DashboardPage.module.css";

/** Robust LocalDate parser: accepts yyyy-MM-dd OR dd/MM/yyyy */
function parseLocalDate(d) {
  if (!d) return null;
  if (typeof d !== "string") {
    try { return new Date(d); } catch { return null; }
  }
  if (d.includes("-")) {
    // yyyy-MM-dd
    const [y, m, day] = d.split("-").map(Number);
    if (!y || !m || !day) return null;
    return new Date(y, m - 1, day);
  }
  if (d.includes("/")) {
    // dd/MM/yyyy
    const [day, m, y] = d.split("/").map(Number);
    if (!y || !m || !day) return null;
    return new Date(y, m - 1, day);
  }
  // fallback
  const maybe = new Date(d);
  return isNaN(maybe) ? null : maybe;
}

function ymKey(date) {
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, "0");
  return `${y}-${m}`;
}
function ddmmyyyy(date) {
  const d = String(date.getDate()).padStart(2, "0");
  const m = String(date.getMonth() + 1).padStart(2, "0");
  const y = date.getFullYear();
  return `${d}/${m}/${y}`;
}

const CATS = [
  "#35b6b0", "#7dd3fc", "#60a5fa", "#a78bfa", "#f472b6",
  "#fca5a5", "#fbbf24", "#34d399", "#93c5fd", "#f59e0b"
];

export default function DashboardPage() {
  const nav = useNavigate();
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState("");
  const [expenses, setExpenses] = useState([]);

  useEffect(() => {
    let mounted = true;
    (async () => {
      setLoading(true);
      setErr("");
      try {
        const data = await listExpenses();
        const sorted = (data || []).slice().sort((a, b) => {
          const da = parseLocalDate(a.dateOfExpense)?.getTime() ?? 0;
          const db = parseLocalDate(b.dateOfExpense)?.getTime() ?? 0;
          return db - da;
        });
        if (mounted) setExpenses(sorted);
      } catch (e) {
        if (mounted) setErr(e?.message || "Failed to load expenses");
      } finally {
        if (mounted) setLoading(false);
      }
    })();
    return () => { mounted = false; };
  }, []);

  // ---- compute summaries & chart data ----
  const now = new Date();
  const todayKey = now.toDateString();
  const monthKeyNow = ymKey(now);

  const {
    totalToday, totalMonth, recent, byMonthSeries, byCategorySeries
  } = useMemo(() => {
    let _totalToday = 0;
    let _totalMonth = 0;

    // last 6 months keys (including this month)
    const months = [];
    for (let i = 5; i >= 0; i--) {
      const d = new Date(now.getFullYear(), now.getMonth() - i, 1);
      months.push(ymKey(d));
    }
    const monthSums = Object.fromEntries(months.map(k => [k, 0]));

    const catSums = {};

    const rec = [];

    for (const ex of expenses) {
      const dt = parseLocalDate(ex.dateOfExpense);
      if (!dt) continue;
      const ts = dt.toDateString();
      const my = ymKey(dt);
      const amt = Number(ex.amount || 0);

      if (ts === todayKey) _totalToday += amt;
      if (my === monthKeyNow) _totalMonth += amt;

      if (my in monthSums) monthSums[my] += amt;

      const cat = String(ex.category || "Uncategorized");
      catSums[cat] = (catSums[cat] || 0) + amt;
    }

    for (let i = 0; i < Math.min(5, expenses.length); i++) rec.push(expenses[i]);

    const byMonthSeries = months.map(k => ({
      month: k,
      total: monthSums[k]
    }));

    const byCategorySeries = Object.entries(catSums)
      .sort((a, b) => b[1] - a[1])
      .map(([name, value]) => ({ name, value }));

    return {
      totalToday: _totalToday,
      totalMonth: _totalMonth,
      recent: rec,
      byMonthSeries,
      byCategorySeries
    };
  }, [expenses]);

  return (
    <div className={s.page}>
      <TopBar />
      <main className={s.container}>
        <header className={s.header}>
          <div>
            <h1 className={s.h1}>Dashboard</h1>
            <p className={s.sub}>A quick glance at your spending</p>
          </div>
          <button className={s.primary} onClick={() => nav("/expenses/new")}>
            <Receipt size={18}/> Add Expense
          </button>
        </header>

        {err && <div className={s.alert}>{err}</div>}

        {/* KPI cards */}
        <section className={s.grid}>
          <div className={s.card}>
            <div className={s.cardHead}>
              <PiggyBank size={18}/> <span>This Month</span>
            </div>
            <div className={s.big}>
              {totalMonth.toLocaleString(undefined, { style: "currency", currency: "EUR" })}
            </div>
            <div className={s.dim}>Total spent in {monthKeyNow}</div>
          </div>
          <div className={s.card}>
            <div className={s.cardHead}>
              <CalendarDays size={18}/> <span>Today</span>
            </div>
            <div className={s.big}>
              {totalToday.toLocaleString(undefined, { style: "currency", currency: "EUR" })}
            </div>
            <div className={s.dim}>So far today</div>
          </div>
          <div className={s.card}>
            <div className={s.cardHead}>
              <Receipt size={18}/> <span>Records</span>
            </div>
            <div className={s.big}>{expenses.length}</div>
            <div className={s.dim}>Total expenses</div>
          </div>
        </section>

        {/* Charts */}
        <section className={s.charts}>
          <div className={s.chartCard}>
            <div className={s.chartHead}>Last 6 months</div>
            <div className={s.chartWrap}>
              <ResponsiveContainer width="100%" height={260}>
                <AreaChart data={byMonthSeries} margin={{ left: 8, right: 8, top: 10, bottom: 0 }}>
                  <defs>
                    <linearGradient id="gradSpend" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#35b6b0" stopOpacity={0.9}/>
                      <stop offset="95%" stopColor="#35b6b0" stopOpacity={0.05}/>
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" opacity={0.15}/>
                  <XAxis dataKey="month" tick={{ fontSize: 12 }} />
                  <YAxis tick={{ fontSize: 12 }} />
                  <Tooltip formatter={(v)=>v.toLocaleString(undefined,{style:"currency",currency:"EUR"})}/>
                  <Area type="monotone" dataKey="total" stroke="#35b6b0" fill="url(#gradSpend)" strokeWidth={2} />
                </AreaChart>
              </ResponsiveContainer>
            </div>
          </div>

          <div className={s.chartCard}>
            <div className={s.chartHead}>By category</div>
            <div className={s.chartWrap}>
              <ResponsiveContainer width="100%" height={260}>
                <PieChart>
                  <Pie
                    data={byCategorySeries}
                    dataKey="value"
                    nameKey="name"
                    outerRadius={90}
                    innerRadius={50}
                    paddingAngle={2}
                  >
                    {byCategorySeries.map((_, i) => (
                      <Cell key={i} fill={CATS[i % CATS.length]} />
                    ))}
                  </Pie>
                  <Legend wrapperStyle={{ fontSize: 12 }} />
                  <Tooltip formatter={(v)=>v.toLocaleString(undefined,{style:"currency",currency:"EUR"})}/>
                </PieChart>
              </ResponsiveContainer>
            </div>
          </div>
        </section>

        {/* Recent list */}
        <section className={s.panel}>
          <div className={s.panelHead}>
            <h2 className={s.h2}>Recent expenses</h2>
            <button className={s.linkBtn} onClick={() => nav("/expenses")}>View all</button>
          </div>

          {loading ? (
            <div className={s.skeletonList}>
              <div className={s.skelRow} />
              <div className={s.skelRow} />
              <div className={s.skelRow} />
            </div>
          ) : (
            <ul className={s.list}>
              {recent.map((ex) => {
                const dt = parseLocalDate(ex.dateOfExpense) || new Date();
                return (
                  <li key={ex.id} className={s.row} onClick={() => nav(`/expenses/${ex.id}/edit`)}>
                    <div className={s.cellCat}>{ex.category}</div>
                    <div className={s.cellDesc}>{ex.description || "—"}</div>
                    <div className={s.cellDate}>{ddmmyyyy(dt)}</div>
                    <div className={s.cellAmt}>
                      {Number(ex.amount || 0).toLocaleString(undefined,{style:"currency",currency:"EUR"})}
                    </div>
                  </li>
                );
              })}
              {recent.length === 0 && !loading && <li className={s.empty}>No expenses yet.</li>}
            </ul>
          )}
        </section>
      </main>
    </div>
  );
}
