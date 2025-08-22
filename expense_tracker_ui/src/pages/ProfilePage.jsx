import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import TopBar from "../components/Layout/TopBar";
import { getMyProfile, updateMyProfile, deactivateMyAccount } from "../api/user";
import { clearToken } from "../api/auth";
import { Save, ShieldAlert } from "lucide-react";
import s from "./ProfilePage.module.css";

export default function ProfilePage() {
  const nav = useNavigate();
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState("");
  const [ok, setOk] = useState("");
  const [profile, setProfile] = useState(null);
  const [name, setName] = useState("");
  const [saving, setSaving] = useState(false);

  // modal
  const [showConfirm, setShowConfirm] = useState(false);
  const [deactivating, setDeactivating] = useState(false);

  useEffect(() => {
    (async () => {
      try {
        const data = await getMyProfile();
        setProfile(data);
        setName(data?.name || "");
        setErr("");
      } catch (e) {
        setErr(e?.message || "Failed to load profile");
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  async function onSave(e) {
    e.preventDefault();
    if (!profile) return;
    setOk(""); setErr(""); setSaving(true);
    try {
      const updated = await updateMyProfile({ name: name.trim(), email: profile.email });
      setProfile(updated);
      setName(updated?.name || name);
      setOk("Profile updated.");
      setTimeout(() => setOk(""), 1500);
    } catch (e2) {
      setErr(e2?.message || "Failed to update profile");
    } finally {
      setSaving(false);
    }
  }

  async function confirmDeactivate() {
    setDeactivating(true);
    try {
      await deactivateMyAccount();          // backend soft-deactivate
    } catch {
      // ignore network errors; we'll still clear client session
    } finally {
      clearToken();                         // remove JWT locally
      setDeactivating(false);
      setShowConfirm(false);
      nav("/login");
    }
  }

  return (
    <div className={s.page}>
      <TopBar onSearch={() => {}} />
      <div className={s.container}>
        <div className={s.header}>
          <h2 className={s.h2}>My Profile</h2>
        </div>

        <div className={s.card}>
          {loading ? (
            <div className={s.skeleton} />
          ) : (
            <>
              {err && <div className={s.alert}>{err}</div>}
              {ok && <div className={s.success}>{ok}</div>}

              {/* Read-only */}
              <div className={s.grid}>
                <div className={s.block}>
                  <div className={s.label}>Username</div>
                  <div className={s.ro}>{profile?.username ?? "—"}</div>
                </div>
                <div className={s.block}>
                  <div className={s.label}>Email</div>
                  <div className={s.ro}>{profile?.email ?? "—"}</div>
                </div>
              </div>

              {/* Editable name */}
              <form onSubmit={onSave} className={s.form}>
                <div className={s.block}>
                  <label className={s.label}>Full name</label>
                  <input
                    className={s.input}
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    placeholder="Your name"
                    required
                  />
                </div>

                <div className={s.actions}>
                  <button className={s.primary} disabled={saving}>
                    <Save size={18} />
                    {saving ? "Saving…" : "Save changes"}
                  </button>

                  {/* Deactivate trigger */}
                  <button
                    type="button"
                    className={s.dangerGhost}
                    onClick={() => setShowConfirm(true)}
                  >
                    <ShieldAlert size={18} />
                    Deactivate account
                  </button>
                </div>
              </form>
            </>
          )}
        </div>
      </div>

      {/* Confirm Modal */}
      {showConfirm && (
        <div className={s.modalBackdrop} role="dialog" aria-modal="true">
          <div className={s.modal}>
            <h3 className={s.modalTitle}>Deactivate account?</h3>
            <p className={s.modalText}>
              Are you sure? <strong>This will permanently delete your account</strong>.
              You won’t be able to access your data after this action.
            </p>
            <div className={s.modalActions}>
              <button
                className={s.secondary}
                onClick={() => setShowConfirm(false)}
                disabled={deactivating}
              >
                Cancel
              </button>
              <button
                className={s.danger}
                onClick={confirmDeactivate}
                disabled={deactivating}
              >
                {deactivating ? "Deactivating…" : "Yes, deactivate"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
