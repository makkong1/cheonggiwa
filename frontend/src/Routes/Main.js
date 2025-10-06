import { useEffect, useState } from "react";
import styled from "styled-components";
import DatePicker from "react-datepicker";

function Main() {
  const [rooms, setRooms] = useState([]);
  const [selectedRoom, setSelectedRoom] = useState(null);
  const [checkIn, setCheckIn] = useState("");
  const [checkOut, setCheckOut] = useState("");
  const [bookingMessage, setBookingMessage] = useState("");
  const [bookDates, setBookDates] = useState([]);

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

  // 모달 팝업 내부 BookingSection 앞쪽에 추가
  const unavailableBookings = selectedRoom?.bookings
    ? selectedRoom.bookings.filter(
        b => b.status === "WAITING" || b.status === "CONFIRMED"
      )
    : [];

 // 예약 요청
  const handleBooking = () => {
    if (!checkIn || !checkOut) {
      setBookingMessage("체크인/체크아웃 날짜를 선택해주세요.");
      return;
    }

    const conflict = unavailableBookings.some(b =>
      new Date(checkIn) < new Date(b.checkOut) &&
      new Date(checkOut) > new Date(b.checkIn)
    );

    if (conflict) {
      setBookingMessage("이미 예약된 날짜입니다.");
      return;
    }

    fetch(`/api/booking`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        userId: 25,      // 실제 로그인 유저 ID
        roomId: selectedRoom.id,
        checkIn,
        checkOut
      })
    })
      .then((res) => {
        if (res.ok) {
          setBookingMessage("예약이 완료되었습니다!");
          // 예약 완료 후, UI 갱신
          setSelectedRoom(prev => ({
            ...prev,
            bookings: [...prev.bookings, { checkIn, checkOut, status: "WAITING" }]
          }));
        } else {
          setBookingMessage("예약 실패: 이미 예약된 날짜입니다.");
        }
      })
      .catch(() => setBookingMessage("서버 오류가 발생했습니다."));
  };
  return (
    <Container>
      <h1><a href="/booking">예약내역으로</a></h1>
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
            <p><strong>가격:</strong> ₩{selectedRoom.price}</p>
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
  min-height: 100vh;
  background: linear-gradient(180deg, #f7f9fc 0%, #eef2f7 100%);

  h1 {
    margin: 0 0 16px 0;
    text-align: center;
    font-size: 1.8rem;
    letter-spacing: -0.2px;
    color: #1f2937;
  }
`;

const RoomGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
`;

const RoomCard = styled.div`
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 16px;
  cursor: pointer;
  text-align: center;
  transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0,0,0,0.08);
    border-color: #d1d5db;
  }

  h3 {
    margin: 12px 0 6px;
    font-size: 1.05rem;
    color: #111827;
  }

  p {
    color: #6b7280;
    font-size: 0.92rem;
    margin: 0;
  }
`;

const RoomImage = styled.div`
  width: 100%;
  height: 120px;
  background: linear-gradient(135deg, #4facfe, #00f2fe);
  border-radius: 12px;
  box-shadow: inset 0 0 0 1px rgba(255,255,255,0.2);
`;

/* 모달 */
const ModalOverlay = styled.div`
  position: fixed;
  top: 0; left: 0;
  width: 100%; height: 100%;
  background: rgba(17,24,39,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
  backdrop-filter: blur(2px);
`;

const ModalContent = styled.div`
  background: #ffffff;
  border-radius: 14px;
  padding: 24px;
  width: 420px;
  max-width: calc(100vw - 32px);
  max-height: 72vh;
  overflow: hidden;  /* 모달 전체는 hidden */
  position: relative;
  text-align: center;
  display: flex;
  flex-direction: column;
  box-shadow: 0 16px 48px rgba(0,0,0,0.12);

  h2 {
    margin: 0 0 8px 0;
    font-size: 1.3rem;
    color: #0f172a;
  }

  p {
    margin: 4px 0;
    color: #334155;
  }
`;

const CloseButton = styled.span`
  position: absolute;
  top: 10px; right: 14px;
  font-size: 24px;
  cursor: pointer;
  color: #64748b;
  line-height: 1;

  &:hover {
    color: #0f172a;
    transform: scale(1.05);
  }
`;

const Description = styled.p`
  margin-top: 10px;
  color: #374151;
  line-height: 1.5;
`;

 /* 리뷰 영역 */
const ReviewsContainer = styled.div`
  margin-top: 16px;
  text-align: left;
  flex: 1;               /* 남은 공간 채우기 */
  overflow-y: auto;      /* 스크롤 */
  padding-right: 8px;    /* 스크롤 겹침 방지 */

  /* 스크롤바 스타일 (웹킷) */
  &::-webkit-scrollbar {
    width: 8px;
  }
  &::-webkit-scrollbar-thumb {
    background: #cbd5e1;
    border-radius: 8px;
  }
`;

const Review = styled.div`
  padding: 10px 0;
  border-bottom: 1px solid #eef2f7;

  p {
    margin: 0 0 6px 0;
    font-size: 0.92rem;
    color: #1f2937;
  }

  small {
    color: #94a3b8;
    font-size: 0.78rem;
  }
`;

const BookingSection = styled.div`
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;

  label {
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 0.92rem;
    color: #1f2937;
  }

  input {
    margin-left: 8px;
    padding: 8px 10px;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    font-size: 0.92rem;
    outline: none;
    transition: border-color 0.2s ease, box-shadow 0.2s ease;

    &:focus {
      border-color: #4facfe;
      box-shadow: 0 0 0 3px rgba(79,172,254,0.2);
    }
  }

  button {
    padding: 10px 14px;
    border: none;
    background: linear-gradient(135deg, #4facfe, #00c3fe);
    color: white;
    border-radius: 10px;
    cursor: pointer;
    font-weight: 700;
    letter-spacing: 0.2px;
    transition: transform 0.12s ease, box-shadow 0.12s ease, filter 0.12s ease;

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 8px 18px rgba(0,0,0,0.12);
      filter: brightness(1.02);
    }

    &:active {
      transform: translateY(0);
      box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    }
  }
`;

const BookingMessage = styled.div`
  margin-top: 6px;
  color: #ef4444;
  font-size: 0.88rem;
  font-weight: 600;
`;