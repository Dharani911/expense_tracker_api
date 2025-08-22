import { useNavigate, useLocation } from "react-router-dom";
import { LogOut, User2, Search } from "lucide-react";
import { logout } from "../../api/auth";
import s from "./TopBar.module.css";

export default function TopBar({ onSearch }) {
  const nav = useNavigate();
  const { pathname } = useLocation();

  async function onLogout() {
    try { await logout(); } finally { nav("/login"); }
  }

  return (
    <header className={s.bar}>
      <div className={s.left}>
        <div className={s.search}>
          <Search size={16}/>
          <input
            placeholder="Search…"
            onChange={(e)=>onSearch?.(e.target.value)}
          />
        </div>
      </div>

      <div className={s.title} onClick={()=>nav("/dashboard")}>
        Expense&nbsp;Tracker
      </div>

      <div className={s.right}>
        <button className={s.ghost} onClick={()=>nav("/dashboard")} data-active={pathname.startsWith("/dashboard")}>Dashboard</button>
        <button className={s.ghost} onClick={()=>nav("/expenses")} data-active={pathname.startsWith("/expenses")}>Expenses</button>
        <button className={s.icon} onClick={()=>nav("/profile")} title="Profile"><User2 size={20}/></button>
        <button className={s.logout} onClick={onLogout}><LogOut size={18}/> Logout</button>
      </div>
    </header>
  );
}
