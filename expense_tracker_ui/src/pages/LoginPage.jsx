import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { login } from "../api/auth";
import { LogIn } from "lucide-react";
import s from "./LoginPage.module.css"; // keep your existing CSS module

export default function LoginPage(){
  const nav = useNavigate();
  const [f, setF]   = useState({ identifier: "", password: "" });
  const [t, setT]   = useState({ identifier:false, password:false });
  const [e, setE]   = useState({ identifier:"", password:"" });
  const [banner, setBanner] = useState("");
  const [show, setShow] = useState(false);
  const [loading, setLoading] = useState(false);

  const vIdentifier = (v)=> !v.trim() ? "Username or email is required" : "";
  const vPassword   = (v)=> !v ? "Password is required" : "";

  function change(field, val){
    setF((x)=>({ ...x, [field]: val }));
    setE((x)=>({ ...x, [field]: "" }));
    if (banner) setBanner("");
  }
  function blur(field){
    setT((x)=>({ ...x, [field]: true }));
    const msg = field === "identifier" ? vIdentifier(f.identifier) : vPassword(f.password);
    if (msg) setE((x)=>({ ...x, [field]: msg }));
  }

  function mapServerLoginError(err){
    const status = err?.status;
    const msg = (typeof err?.data === "string" ? err.data : (err?.data?.message || err?.message || "")).toLowerCase();

    // Wrong creds → show only under password (clean UX)
    if (status === 401 || /invalid|bad credentials|incorrect/.test(msg)) {
      return { password: "Incorrect username/email or password" };
    }

    // If backend returned a specific field problem (rare)
    if (status === 400 && /identifier/.test(msg)) {
      return { identifier: "Enter a valid username or email" };
    }

    return { _global: err?.message || "Login failed" };
  }

  async function handleSubmit(ev){
    ev.preventDefault();
    setBanner("");

    const errs = { identifier: vIdentifier(f.identifier), password: vPassword(f.password) };
    setE(errs); setT({ identifier:true, password:true });
    if (errs.identifier || errs.password) return;

    setLoading(true);
    try{
      await login({ identifier: f.identifier.trim(), password: f.password });
      // success → go to dashboard
      nav("/dashboard");
    }catch(err){
      const mapped = mapServerLoginError(err);
      if (mapped._global) setBanner(mapped._global);
      setE((prev)=>({ ...prev, ...mapped }));
    }finally{
      setLoading(false);
    }
  }

  return (
    <div className={s.page}>
      <div className={s.center}>
        <div className={s.card}>
          <div className={s.brand}>Expense&nbsp;Tracker</div>
          <h1 className={s.title}>Welcome back</h1>
          <p className={s.subtitle}>Sign in to continue</p>

          {banner && <div className={s.alert}>{banner}</div>}

          <form className={s.form} onSubmit={handleSubmit} noValidate>
            <div className={s.field}>
              <label className={s.label}>Username or Email</label>
              <input
                className={`${s.input} ${t.identifier && e.identifier ? s.inputError : ""}`}
                value={f.identifier}
                onChange={(ev)=>change("identifier", ev.target.value)}
                onBlur={()=>blur("identifier")}
                placeholder="yourhandle or you@domain.com"
                autoFocus
                required
              />
              {t.identifier && e.identifier ? <div className={s.helpErr}>{e.identifier}</div> : null}
            </div>

            <div className={s.field}>
              <label className={s.label}>Password</label>
              <div className={`${s.inputRow} ${t.password && e.password ? s.inputError : ""}`}>
                <input
                  className={s.inputBare}
                  type={show ? "text" : "password"}
                  value={f.password}
                  onChange={(ev)=>change("password", ev.target.value)}
                  onBlur={()=>blur("password")}
                  placeholder="••••••••"
                  required
                />
                <button type="button" className={s.eye} onClick={()=>setShow(!show)} aria-label={show?"Hide password":"Show password"}>
                  {show ? "🙈" : "👁️"}
                </button>
              </div>
              {t.password && e.password ? <div className={s.helpErr}>{e.password}</div> : null}
            </div>

            <button className={s.primary} disabled={loading}>
              <LogIn size={18}/> {loading ? "Signing in…" : "Login"}
            </button>

            <div className={s.meta}>
              New here? <Link className={s.link} to="/register">Create an account</Link>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
