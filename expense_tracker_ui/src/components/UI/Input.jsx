export default function Input({ label, rightAdornment, error, ...props }) {
  return (
    <label style={{ display: "block", marginBottom: 12 }}>
      {label ? <div style={{ marginBottom: 6 }}>{label}</div> : null}
      <div style={{
        display: "flex",
        alignItems: "center",
        gap: 8,
        background: "white",
        border: "1px solid #d7d7d7",
        borderRadius: 12,
        padding: "0 10px"
      }}>
        <input
          {...props}
          style={{
            flex: 1,
            padding: 12,
            border: "none",
            outline: "none",
            borderRadius: 12
          }}
        />
        {rightAdornment}
      </div>
      {error ? (
        <div style={{ color: "crimson", marginTop: 6, fontSize: 12 }}>{error}</div>
      ) : null}
    </label>
  );
}
