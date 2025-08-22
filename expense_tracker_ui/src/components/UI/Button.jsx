export default function Button({ children, ...props }) {
  return (
    <button
      {...props}
      style={{
        display: "flex",
        alignItems: "center",
        gap: 8,
        margin: "16px auto 8px",
        padding: "10px 16px",
        borderRadius: 12,
        border: "1px solid #ddd",
        cursor: "pointer",
        background: "white"
      }}
    >
      {children}
    </button>
  );
}
