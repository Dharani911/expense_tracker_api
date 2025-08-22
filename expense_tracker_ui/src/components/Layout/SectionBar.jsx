import { useLocation, useNavigate } from "react-router-dom";
import "./SectionBar.css";

export default function SectionBar() {
  const nav = useNavigate();
  const { pathname } = useLocation();

  const isDash = pathname.startsWith("/dashboard");
  const isExp  = pathname.startsWith("/expenses");

  return (
    <div className="sectionbar">
      <div className="sectionbar-inner">
        <button
          className={`section-tab ${isDash ? "active" : ""}`}
          onClick={() => nav("/dashboard")}
        >
          Dashboard
        </button>
        <button
          className={`section-tab ${isExp ? "active" : ""}`}
          onClick={() => nav("/expenses")}
        >
          Expenses
        </button>
      </div>
    </div>
  );
}
