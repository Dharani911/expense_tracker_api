import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { register as registerApi } from "../api/auth";
import { UserPlus } from "lucide-react";
import s from "./RegisterPage.module.css";

const PASS_RE = /^(?=\S+$)(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[^\w\s]).{8,}$/;

export default function RegisterPage() {
  const navigate = useNavigate();

  const [f, setF] = useState({ username: "", name: "", email: "", password: "" });
  const [t, setT] = useState({ username:false, name:false, email:false, password:false });
  const [e, setE] = useState({ username:"", name:"", email:"", password:"" });
  const [globalErr, setGlobalErr] = useState("");
  const [show, setShow] = useState(false);
  const [loading, setLoading] = useState(false);

  // validators
  const vUsername = (v)=> !v.trim() ? "Username is required"
    : (v.trim().length < 3 ? "Min 3 characters"
    : (v.trim().length > 30 ? "Max 30 characters" : ""));
  const vName     = (v)=> !v.trim() ? "Full name is required"
    : (v.trim().length > 100 ? "Max 100 characters" : "");
  const vEmail    = (v)=> !v.trim() ? "Email is required"
    : (!/^\S+@\S+\.\S+$/.test(v) ? "Enter a valid email"
    : (v.length > 500 ? "Max 500 characters" : ""));
  const vPassword = (v)=> !v ? "Password is required"
    : (!PASS_RE.test(v) ? "Min 8 chars, with upper, lower, digit & special; no spaces" : "");

  function change(field, val){
    const next = field === "email" ? val.toLowerCase() : val;
    setF((x)=>({ ...x, [field]: next }));
    // clear field error immediately
    if (e[field]) setE((x)=>({ ...x, [field]: "" }));
    if (globalErr) setGlobalErr("");
  }

  function blur(field){
    setT((tt)=>({ ...tt, [field]: true }));
    const localErr =
      field==="username" ? vUsername(f.username) :
      field==="name"     ? vName(f.name) :
      field==="email"    ? vEmail(f.email) :
                           vPassword(f.password);
    if (localErr) setE((x)=>({ ...x, [field]: localErr }));
  }

  function validateAll(){
    const errs = {
      username: vUsername(f.username),
      name:     vName(f.name),
      email:    vEmail(f.email),
      password: vPassword(f.password),
    };
    setE(errs);
    setT({ username:true, name:true, email:true, password:true });
    return !errs.username && !errs.name && !errs.email && !errs.password;
  }

  function mapServerRegisterError(err) {
    const status = err?.status;
    const data   = err?.data;
    const msg    = (typeof data === "string" ? data : (data?.message || err?.message || "")).trim();
    const m      = msg.toLowerCase();

    // explicit messages from backend
    if (status === 400) {
      if (m.includes("username") && m.includes("already") && m.includes("use")) {
        return { username: "Username already in use." };
      }
      if (m.includes("email") && m.includes("already") && m.includes("use")) {
        return { email: "Email already in use." };
      }
    }

    // optional future shape: { fields: { username: "...", email: "..." } }
    if (status === 400 && data && data.fields) {
      const out = {};
      if (data.fields.username) out.username = data.fields.username;
      if (data.fields.email)    out.email    = data.fields.email;
      if (data.fields.name)     out.name     = data.fields.name;
      if (data.fields.password) out.password = data.fields.password;
      if (Object.keys(out).length) return out;
    }

    if (/upper.*lower.*digit.*special/.test(m)) {
      return { password: "Min 8 chars, with upper, lower, digit & special; no spaces" };
    }
    return { _global: msg || "Registration failed" };
  }

  async function handleSubmit(ev){
    ev.preventDefault();
    setGlobalErr("");

    if (!validateAll()) return;

    setLoading(true);
    try{
      await registerApi({
        username: f.username.trim(),
        name:     f.name.trim(),
        email:    f.email.trim(),
        password: f.password,
      });
      navigate("/dashboard"); // or "/login" if you prefer
    }catch(err){
      const mapped = mapServerRegisterError(err);
      if (mapped._global) setGlobalErr(mapped._global);
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
          <h1 className={s.title}>Create account</h1>
          <p className={s.subtitle}>Join and start tracking</p>

          {globalErr && <div className={s.alert}>{globalErr}</div>}

          <form className={s.form} onSubmit={handleSubmit} noValidate>
            <div className={s.field}>
              <label className={s.label}>User name</label>
              <input
                className={`${s.input} ${t.username && e.username ? s.inputError : ""}`}
                value={f.username}
                onChange={(ev)=>change("username", ev.target.value)}
                onBlur={()=>blur("username")}
                placeholder="yourhandle"
                required
              />
              {t.username && e.username ? <div className={s.helpErr}>{e.username}</div> : null}
            </div>

            <div className={s.field}>
              <label className={s.label}>Full name</label>
              <input
                className={`${s.input} ${t.name && e.name ? s.inputError : ""}`}
                value={f.name}
                onChange={(ev)=>change("name", ev.target.value)}
                onBlur={()=>blur("name")}
                placeholder="Jane Doe"
                required
              />
              {t.name && e.name ? <div className={s.helpErr}>{e.name}</div> : null}
            </div>

            <div className={s.field}>
              <label className={s.label}>Email</label>
              <input
                type="email"
                className={`${s.input} ${t.email && e.email ? s.inputError : ""}`}
                value={f.email}
                onChange={(ev)=>change("email", ev.target.value)}
                onBlur={()=>blur("email")}
                placeholder="you@domain.com"
                required
              />
              {t.email && e.email ? <div className={s.helpErr}>{e.email}</div> : null}
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
                  placeholder="Min 8 chars, upper/lower/digit/special"
                  required
                />
                <button
                  type="button"
                  className={s.eye}
                  onClick={()=>setShow(!show)}
                  aria-label={show ? "Hide password" : "Show password"}
                >
                  {show ? "🙈" : "👁️"}
                </button>
              </div>
              {t.password && e.password ? <div className={s.helpErr}>{e.password}</div> : null}
            </div>

            <button className={s.primary} disabled={loading}>
              <UserPlus size={18}/> {loading ? "Creating…" : "Register"}
            </button>

            <div className={s.meta}>
              Already have an account? <Link className={s.link} to="/login">Login</Link>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
