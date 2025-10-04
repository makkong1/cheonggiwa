import { useEffect, useState } from "react";
import "./App.css";

function App() {
  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null); // 선택한 회원

  useEffect(() => {
    fetch("/api/user")
      .then((res) => res.json())
      .then((data) => setUsers(data))
      .catch((err) => console.error(err));
  }, []);

  // 회원 클릭 시 단건 조회
  const handleUserClick = (id) => {
    fetch(`/api/user/${id}`)
      .then((res) => res.json())
      .then((data) => setSelectedUser(data))
      .catch((err) => console.error(err));
  };

  return (
    <div className="container">
      <h1>회원 목록</h1>
      <div className="user-grid">
        {users.map(user => (
          <div className="user-card" key={user.id} onClick={() => handleUserClick(user.id)}>
            <div className="user-avatar">{user.username.charAt(0).toUpperCase()}</div>
            <div className="user-info">
              <h3>{user.username}</h3>
              <p>예약 건수: 0</p>
            </div>
          </div>
        ))}
      </div>

      {selectedUser && (
        <div className="user-detail">
          <h2>회원 상세 정보</h2>
          <p>ID: {selectedUser.id}</p>
          <p>이름: {selectedUser.username}</p>
          {/* 추후 예약 정보 등 추가 가능 */}
        </div>
      )}
    </div>
  );
}

export default App;