import { useEffect, useState } from "react";
import styled from "styled-components";

function User() {
  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);

  useEffect(() => {
    fetch("/api/user")
      .then((res) => res.json())
      .then((data) => setUsers(data))
      .catch((err) => console.error(err));
  }, []);

  const handleUserClick = (id) => {
    fetch(`/api/user/${id}`)
      .then((res) => res.json())
      .then((data) => setSelectedUser(data))
      .catch((err) => console.error(err));
  };

  const closeModal = () => setSelectedUser(null);

  return (
    <Container>
      <h1>회원 목록</h1>
      <UserGrid>
        {users.map((user) => (
          <UserCard key={user.id} onClick={() => handleUserClick(user.id)}>
            <UserAvatar bgColor="red">{user.username.charAt(0).toUpperCase()}</UserAvatar>
          </UserCard>
        ))}
      </UserGrid>

      {/* 모달 팝업 */}
      {selectedUser && (
        <ModalOverlay onClick={closeModal}>
          <ModalContent onClick={(e) => e.stopPropagation()}>
            <CloseButton onClick={closeModal}>&times;</CloseButton>

            <h2>회원 상세 정보</h2>
            <p><strong>ID:</strong> {selectedUser.id}</p>
            <p><strong>이름:</strong> {selectedUser.username}</p>

            {/* ✅ 예약 정보가 있을 때만 출력 */}
            {selectedUser.bookings && selectedUser.bookings.length > 0 ? (
              <>
                <h3 style={{ marginTop: '16px' }}>예약 내역</h3>
                <ul style={{ listStyle: 'none', padding: 0}}>
                  {selectedUser.bookings.map((booking) => (
                    <li key={booking.id} style={{ marginBottom: '10px', borderBottom: '1px solid #ddd', paddingBottom: '8px' }}>
                      <p><strong>객실명:</strong> {booking.roomName}</p>
                      <p><strong>체크인:</strong> {booking.checkIn}</p>
                      <p><strong>체크아웃:</strong> {booking.checkOut}</p>
                      <p><strong>상태:</strong> {booking.checkStatus}</p>
                    </li>
                  ))}
                </ul>
              </>
            ) : (
              <p style={{ marginTop: '16px', color: '#888' }}>예약 내역이 없습니다.</p>
            )}
          </ModalContent>
        </ModalOverlay>
      )}
    </Container>
  );
}

export default User;

/* styled-components */
const Container = styled.div`
  padding: 20px;
  font-family: Arial, sans-serif;
`;

const UserGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
  gap: 16px;
`;

const UserCard = styled.div`
  background: #f8f9fa;
  border-radius: 10px;
  padding: 16px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  }
`;

const UserAvatar = styled.div`
  background: ${props => props.bgColor || 'green'};
  color: white;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-size: 18px;
  margin: 0 auto;
`;

/* 모달 스타일 */
const ModalOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
`;

const ModalContent = styled.div`
  background: white;
  max-height: 70vh;      
  overflow-y: auto;        
  border-radius: 10px;
  padding: 24px;
  width: 300px;
  position: relative;
  text-align: center;
`;

const CloseButton = styled.span`
  position: absolute;
  top: 8px;
  right: 12px;
  font-size: 24px;
  font-weight: bold;
  cursor: pointer;
`;
