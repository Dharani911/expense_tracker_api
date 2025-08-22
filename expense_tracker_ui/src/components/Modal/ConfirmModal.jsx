import s from "./ConfirmModal.module.css";

export default function ConfirmModal({
  open,
  title = "Are you sure?",
  message,
  confirmText = "Confirm",
  cancelText = "Cancel",
  loading = false,
  onConfirm,
  onCancel,
}) {
  if (!open) return null;
  return (
    <div className={s.backdrop} role="dialog" aria-modal="true">
      <div className={s.modal}>
        <h3 className={s.title}>{title}</h3>
        {message ? <p className={s.text}>{message}</p> : null}
        <div className={s.actions}>
          <button className={s.secondary} onClick={onCancel} disabled={loading}>
            {cancelText}
          </button>
          <button className={s.danger} onClick={onConfirm} disabled={loading}>
            {loading ? "Working…" : confirmText}
          </button>
        </div>
      </div>
    </div>
  );
}
