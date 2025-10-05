import { useEffect, useState } from "react";
import styled from "styled-components";

function Main() {
  const [rooms, setRooms] = useState([]);
  const [selectedRoom, setSelectedRoom] = useState(null);
  const [checkIn, setCheckIn] = useState("");
  const [checkOut, setCheckOut] = useState("");
  const [bookingMessage, setBookingMessage] = useState("");

  // 전체 객실 목록 가져오기
  useEffect(() => {
    fetch("/api/room")
      .then((res) => res.json())
      .then((data) => setRooms(data))
      .catch((err) => console.error("객실 목록 로드 실패:", err));
  }, []);

  // 객실 클릭 시 상세정보 조회
  const handleRoomClick = (id) => {
    fetch(`/api/room/${id}`)
      .then((res) => res.json())
      .then((data) => setSelectedRoom(data))
      .catch((err) => console.error("객실 상세 로드 실패:", err));
  };

   const closeModal = () => {
    setSelectedRoom(null);
    setCheckIn("");
    setCheckOut("");
    setBookingMessage("");
  };

  // 예약 요청
  const handleBooking = () => {
    if (!checkIn || !checkOut) {
      setBookingMessage("체크인/체크아웃 날짜를 선택해주세요.");
      return;
    }

    fetch(`/api/booking`, {
      method: "POST",
    })
      .then((res) => {
        if (res.ok) {
          setBookingMessage("예약이 완료되었습니다!");
        } else {
          setBookingMessage("이미 예약된 날짜일 수 있습니다.");
        }
      })
      .catch(() => setBookingMessage("서버 오류가 발생했습니다."));
  };

  return (
    <Container>
      <h1>객실 목록</h1>
      <RoomGrid>
        {rooms.map((room) => (
          <RoomCard key={room.id} onClick={() => handleRoomClick(room.id)}>
            <RoomImage />
            <h3>{room.roomName}</h3>
            <p>₩ {room.price.toLocaleString()}</p>
          </RoomCard>
        ))}
      </RoomGrid>

      {/* 모달 팝업 */}
      {selectedRoom && (
        <ModalOverlay onClick={closeModal}>
          <ModalContent onClick={(e) => e.stopPropagation()}>
            <CloseButton onClick={closeModal}>&times;</CloseButton>
            <h2>{selectedRoom.roomName}</h2>
            <p><strong>가격:</strong> ₩{selectedRoom.price.toLocaleString()}</p>
            <p><strong>상태:</strong> {selectedRoom.status || "예약 가능"}</p>
            {selectedRoom.description && (
              <Description>{selectedRoom.description}</Description>
            )}

            {/* 예약 가능 시 UI */}
            {(!selectedRoom.status || selectedRoom.status === "예약 가능") && (
              <BookingSection>
                <label>
                  체크인:
                  <input type="date" value={checkIn} onChange={(e) => setCheckIn(e.target.value)} />
                </label>
                <label>
                  체크아웃:
                  <input type="date" value={checkOut} onChange={(e) => setCheckOut(e.target.value)} />
                </label>
                <button onClick={handleBooking}>예약하기</button>
                {bookingMessage && <BookingMessage>{bookingMessage}</BookingMessage>}
              </BookingSection>
            )}

            {/* 리뷰 영역 */}
            {selectedRoom.reviews && selectedRoom.reviews.length > 0 && (
              <ReviewsContainer>
                <h3>리뷰</h3>
                {selectedRoom.reviews.map((review) => (
                  <Review key={review.id}>
                    <p>{review.username}</p>
                    <p>{review.content}</p>
                    <small>{new Date(review.createdAt).toLocaleString()}</small>
                  </Review>
                ))}
              </ReviewsContainer>
            )}

          </ModalContent>
        </ModalOverlay>
      )}
    </Container>
  );
}

export default Main;

/* styled-components */
const Container = styled.div`
  padding: 20px;
  font-family: "Arial", sans-serif;
`;

const RoomGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
`;

const RoomCard = styled.div`
  background: #f8f9fa;
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  text-align: center;
  transition: transform 0.2s ease, box-shadow 0.2s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 15px rgba(0,0,0,0.15);
  }

  h3 {
    margin: 10px 0 6px;
  }

  p {
    color: #555;
    font-size: 0.9rem;
  }
`;

const RoomImage = styled.div`
  width: 100%;
  height: 120px;
  background: linear-gradient(135deg, #4facfe, #00f2fe);
  border-radius: 10px;
`;

/* 모달 */
const ModalOverlay = styled.div`
  position: fixed;
  top: 0; left: 0;
  width: 100%; height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
`;

const ModalContent = styled.div`
  background: white;
  border-radius: 10px;
  padding: 24px;
  width: 400px;
  max-height: 70vh;
  overflow: hidden;  /* 모달 전체는 hidden */
  position: relative;
  text-align: center;
  display: flex;
  flex-direction: column;
`;

const CloseButton = styled.span`
  position: absolute;
  top: 10px; right: 14px;
  font-size: 24px;
  cursor: pointer;
`;

const Description = styled.p`
  margin-top: 10px;
  color: #333;
  line-height: 1.5;
`;

/* 리뷰 영역 */
const ReviewsContainer = styled.div`
  margin-top: 16px;
  text-align: left;
  flex: 1;               /* 남은 공간 채우기 */
  overflow-y: auto;      /* 스크롤 */
  padding-right: 8px;    /* 스크롤 겹침 방지 */
`;

const Review = styled.div`
  padding: 8px 0;
  border-bottom: 1px solid #eee;

  p {
    margin: 0px 0px 4px 0px;
    font-size: 0.9rem;
    border-bottom: 1px solid #eee;
  }

  small {
    color: #999;
    font-size: 0.75rem;
  }
`;
const BookingSection = styled.div`
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;

  label {
    display: flex;
    justify-content: space-between;
    font-size: 0.9rem;
  }

  input {
    margin-left: 8px;
  }

  button {
    padding: 6px 12px;
    border: none;
    background-color: #4facfe;
    color: white;
    border-radius: 6px;
    cursor: pointer;
    font-weight: bold;

    &:hover {
      background-color: #00f2fe;
    }
  }
`;

const BookingMessage = styled.div`
  margin-top: 8px;
  color: red;
  font-size: 0.85rem;
`;
