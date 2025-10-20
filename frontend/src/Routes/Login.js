import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import styled from "styled-components";

function Login() {
  const navigate = useNavigate();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const res = await fetch("/api/user/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ username, password })
      });
      if (!res.ok) {
        const text = await res.text();
        throw new Error(text || "로그인 실패");
      }
      navigate("/");
    } catch (err) {
      setError(err.message || "로그인 중 오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container>
      <Card>
        <Title>로그인</Title>
        <form onSubmit={handleSubmit}>
          <Label>
            <Input
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="아이디"
              required
            />
          </Label>
          <Label>
            <Input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="비밀번호"
              required
            />
          </Label>
          {error && <ErrorText>{error}</ErrorText>}
          <Button type="submit" disabled={loading}>
            {loading ? "로그인 중..." : "로그인"}
          </Button>
        </form>
        <Sub>
          계정이 없으신가요? <Link to="/signup">회원가입</Link>
        </Sub>
      </Card>
    </Container>
  );
}

export default Login;

const Container = styled.div`
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, #f7f9fc 0%, #eef2f7 100%);
  padding: 24px;
`;

const Card = styled.div`
  width: 100%;
  max-width: 380px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 24px;
  box-shadow: 0 16px 48px rgba(0,0,0,0.06);
`;

const Title = styled.h2`
  margin: 0 0 16px 0;
  text-align: center;
  color: #0f172a;
`;

const Label = styled.label`
  display: block;
  margin-bottom: 12px;
  color: #1f2937;
  font-size: 0.95rem;
`;

const Input = styled.input`
  width: 100%;
  margin-top: 6px;
  padding: 10px 12px;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;

  &:focus {
    border-color: #4facfe;
    box-shadow: 0 0 0 3px rgba(79,172,254,0.15);
  }
`;

const Button = styled.button`
  width: 100%;
  margin-top: 8px;
  padding: 12px 14px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #4facfe, #00c3fe);
  color: #ffffff;
  font-weight: 700;
  cursor: pointer;
`;

const Sub = styled.div`
  margin-top: 12px;
  text-align: center;
  color: #64748b;
  font-size: 0.9rem;
`;

const ErrorText = styled.div`
  margin: 6px 0 0;
  color: #ef4444;
  font-size: 0.88rem;
  font-weight: 600;
`;


