export default function Alert({ children }) {
  return (
    <div
      style={{
        background: "#ffe0e0",
        border: "1px solid #ffc0c0",
        padding: 10,
        borderRadius: 8,
        marginBottom: 12
      }}
    >
      {children}
    </div>
  );
}
