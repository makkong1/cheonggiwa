import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import styled from "styled-components";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { useTheme } from "../ThemeContext";

function Main() {
  const { theme, setTheme } = useTheme();
  const [rooms, setRooms] = useState([]);
  const [selectedRoom, setSelectedRoom] = useState(null);
  const [checkIn, setCheckIn] = useState(null);
  const [checkOut, setCheckOut] = useState(null);
  const [range, setRange] = useState({ startDate: null, endDate: null });
  const [blockedDays, setBlockedDays] = useState(new Set());
  const [availabilityLoadedFor, setAvailabilityLoadedFor] = useState(new Set());
  const [bookingMessage, setBookingMessage] = useState("");
  // const [bookDates, setBookDates] = useState([]);

  // auth states
  const [authError, setAuthError] = useState("");

    // 로그인/회원가입 모달 상태 관리
    const [showLoginModal, setShowLoginModal] = useState(false);
    const [showSignupModal, setShowSignupModal] = useState(false);
  
    // 로그인 관련 상태
    const [loginUsername, setLoginUsername] = useState("");
    const [loginPassword, setLoginPassword] = useState("");
  
    // 회원가입 관련 상태
    const [signupUsername, setSignupUsername] = useState("");
    const [signupPassword, setSignupPassword] = useState("");
    const [signupConfirm, setSignupConfirm] = useState("");
  
    // 로딩 상태 (버튼 비활성화용)
    const [authLoading, setAuthLoading] = useState(false);
  
    // 현재 로그인 유저 (테스트용 or 실제 props/context 연동)
    const [currentUser, setCurrentUser] = useState(null);

  // 전체 객실 목록 가져오기
  useEffect(() => {
    fetch("/api/room")
      .then((res) => res.json())
      .then((data) => setRooms(data))
      .catch((err) => console.error("객실 목록 로드 실패:", err));
  }, []);

  // try restore auth from localStorage
  useEffect(() => {
    const saved = localStorage.getItem("cheonggiwa_auth");
    if (saved) {
      try {
        const parsed = JSON.parse(saved);
        if (parsed && parsed.username) {
          setCurrentUser(parsed);
        }
      } catch {}
    }
  }, []);

  // 객실 클릭 시 상세정보 조회
  const handleRoomClick = (id) => {
    fetch(`/api/room/${id}`)
      .then((res) => res.json())
      .then((data) => {
        setSelectedRoom(data);
        console.log("객실 상태:", data.roomStatus);
        // 초기 가용성: 현재/다음 달 선조회
        const today = new Date();
        const monthsToLoad = [
          { y: today.getFullYear(), m: today.getMonth() + 1 },
          { y: today.getFullYear() + (today.getMonth() + 1 === 12 ? 1 : 0), m: ((today.getMonth() + 1) % 12) + 1 },
        ];
        monthsToLoad.forEach(({ y, m }) => preloadAvailability(data.id, y, m));
      })
      .catch((err) => console.error("객실 상세 로드 실패:", err));
  };

   const closeModal = () => {
    setSelectedRoom(null);
    setCheckIn(null);
    setCheckOut(null);
    setBookingMessage("");
  };

  // 모달 팝업 내부 BookingSection 앞쪽에 추가
  // const unavailableBookings = selectedRoom?.bookings
  //   ? selectedRoom.bookings.filter(
  //       b => b.status === "WAITING" || b.status === "CONFIRMED"
  //     )
  //   : [];

 // 예약 요청
  const handleBooking = () => {
    if (!currentUser) {
      setBookingMessage("로그인 후 예약이 가능합니다.");
      return;
    }
    const start = range.startDate || checkIn;
    const end = range.endDate || checkOut;
    if (!start || !end) {
      setBookingMessage("체크인/체크아웃 날짜를 선택해주세요.");
      return;
    }

    const fmt = (d) => d.toISOString().slice(0,10);
    const startStr = fmt(start);
    const endStr = fmt(end);

    fetch(`/api/booking`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        userId: currentUser?.id,      // 로그인 유저 ID
        roomId: selectedRoom.id,
        checkIn: startStr,
        checkOut: endStr
      })
    })
      .then((res) => {
        if (res.ok) {
          setBookingMessage("예약이 완료되었습니다!");
          // 예약 완료 후, UI 갱신
          setSelectedRoom(prev => ({
            ...prev,
            bookings: [...prev.bookings, { checkIn: startStr, checkOut: endStr, status: "WAITING" }]
          }));
        } else {
          setBookingMessage("예약 실패: 이미 예약된 날짜입니다.");
        }
      })
      .catch(() => setBookingMessage("서버 오류가 발생했습니다."));
  };

  // 가용성 API 선조회 및 blockedDates 구성
  const preloadAvailability = async (roomId, year, month) => {
    const key = `${roomId}:${year}-${String(month).padStart(2, "0")}`;
    if (availabilityLoadedFor.has(key)) return;
    try {
      const res = await fetch(`/api/booking/rooms/${roomId}/availability?year=${year}&month=${month}`);
      if (!res.ok) return;
      const data = await res.json();
      const newBlocked = new Set(blockedDays);
      // intervals [{start, end}] inclusive
      for (const it of data.blockedIntervals || []) {
        const start = new Date(it.start + "T00:00:00");
        const end = new Date(it.end + "T00:00:00");
        for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
          newBlocked.add(d.toISOString().slice(0,10));
        }
      }
      setBlockedDays(newBlocked);
      setAvailabilityLoadedFor(prev => new Set(prev).add(key));
    } catch {}
  };

  // auth handlers
  const handleLogin = async (e) => {
    e.preventDefault();
    setAuthError("");
    setAuthLoading(true);
    try {
      const res = await fetch("/api/user/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ username: loginUsername, password: loginPassword })
      });
      if (!res.ok) {
        const text = await res.text();
        throw new Error(text || "로그인 실패");
      }
      // fetch users to map username -> id
      const listRes = await fetch("/api/user");
      const users = listRes.ok ? await listRes.json() : [];
      const me = users.find(u => u.username === loginUsername) || { username: loginUsername };
      const auth = { id: me.id, username: me.username };
      setCurrentUser(auth);
      localStorage.setItem("cheonggiwa_auth", JSON.stringify(auth));
      setLoginPassword("");
    } catch (err) {
      setAuthError(err.message || "로그인 중 오류가 발생했습니다.");
    } finally {
      setAuthLoading(false);
    }
  };

  const handleSignup = async (e) => {
    e.preventDefault();
    setAuthError("");
    if (signupPassword !== signupConfirm) {
      setAuthError("비밀번호가 일치하지 않습니다.");
      return;
    }
    setAuthLoading(true);
    try {
      const res = await fetch("/api/user", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({ username: signupUsername, password: signupPassword })
      });
      if (!res.ok) {
        const text = await res.text();
        throw new Error(text || "회원가입 실패");
      }
      // auto-login convenience
      setLoginUsername(signupUsername);
      setSignupUsername("");
      setSignupPassword("");
      setSignupConfirm("");
    } catch (err) {
      setAuthError(err.message || "회원가입 중 오류가 발생했습니다.");
    } finally {
      setAuthLoading(false);
    }
  };

  const handleLogout = async () => {
    setAuthError("");
    try {
      await fetch("/api/user/logout", { method: "POST", credentials: "include" });
    } catch {}
    setCurrentUser(null);
    localStorage.removeItem("cheonggiwa_auth");
  };
  return (
    <Container>
      <TopBar>
        <Brand>Cheonggiwa</Brand>
        <Spacer />
        <ThemeToggle type="button" onClick={() => setTheme(theme === "dark" ? "light" : "dark")}
          aria-label={theme === "dark" ? "라이트 모드" : "다크 모드"}
          title={theme === "dark" ? "라이트 모드" : "다크 모드"}
        >
          {theme === "dark" ? "☀️" : "🌙"}
        </ThemeToggle>
        {!currentUser ? (
          <>
            <SmallButton type="button" onClick={() => setShowLoginModal(true)} style={{ marginRight: 8 }}>
              로그인
            </SmallButton>
            <SmallButton type="button" onClick={() => setShowSignupModal(true)}>
              회원가입
            </SmallButton>
            {/* 로그인 모달 */}
            {showLoginModal && (
              <ModalOverlay onClick={() => setShowLoginModal(false)}>
                <ModalContent onClick={e => e.stopPropagation()} style={{ minWidth: 290 }}>
                  <CloseButton onClick={() => setShowLoginModal(false)}>&times;</CloseButton>
                  <h3 style={{ marginBottom: 16 }}>로그인</h3>
                  <AuthInline>
                    <form onSubmit={handleLogin}>
                      <SmallInput
                        placeholder="아이디"
                        value={loginUsername}
                        onChange={(e) => setLoginUsername(e.target.value)}
                        required
                      />
                      <SmallInput
                        type="password"
                        placeholder="비밀번호"
                        value={loginPassword}
                        onChange={(e) => setLoginPassword(e.target.value)}
                        required
                      />
                      <SmallButton type="submit" disabled={authLoading}>로그인</SmallButton>
                    </form>
                  </AuthInline>
                </ModalContent>
              </ModalOverlay>
            )}
            {/* 회원가입 모달 */}
            {showSignupModal && (
              <ModalOverlay onClick={() => setShowSignupModal(false)}>
                <ModalContent onClick={e => e.stopPropagation()} style={{ minWidth: 320 }}>
                  <CloseButton onClick={() => setShowSignupModal(false)}>&times;</CloseButton>
                  <h3 style={{ marginBottom: 16 }}>회원가입</h3>
                  <AuthInline>
                    <form onSubmit={handleSignup}>
                      <SmallInput
                        placeholder="새 아이디"
                        value={signupUsername}
                        onChange={(e) => setSignupUsername(e.target.value)}
                        required
                      />
                      <SmallInput
                        type="password"
                        placeholder="새 비밀번호"
                        value={signupPassword}
                        onChange={(e) => setSignupPassword(e.target.value)}
                        required
                      />
                      <SmallInput
                        type="password"
                        placeholder="비밀번호 확인"
                        value={signupConfirm}
                        onChange={(e) => setSignupConfirm(e.target.value)}
                        required
                      />
                      <SmallButton type="submit" disabled={authLoading }>회원가입</SmallButton>
                    </form>
                  </AuthInline>
                </ModalContent>
              </ModalOverlay>
            )}
          </>
        ) : (
          <UserInline>
            <Avatar>{currentUser.username?.charAt(0)?.toUpperCase()}</Avatar>
            <Nick>{currentUser.username}</Nick>
            <NavLink to="/booking">예약내역</NavLink>
            <SmallButton type="button" onClick={handleLogout}>로그아웃</SmallButton>
          </UserInline>
        )}
      </TopBar>
      {authError && <AuthError>{authError}</AuthError>}
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
            <p><strong>상태:</strong> 
              {selectedRoom.roomStatus === "OCCUPIED" 
                ? "예약 중" 
                : "예약 가능"}
            </p>
            {selectedRoom.description && (
              <Description>{selectedRoom.description}</Description>
            )}

          {/* 예약 가능 시 UI */}
          {selectedRoom.roomStatus !== "OCCUPIED" && (
            <BookingSection>
              <div>
                <DatePicker
                  selected={checkIn}
                  onChange={(dates) => {
                    const [start, end] = dates;
                    setCheckIn(start);
                    setCheckOut(end);
                    setRange({ startDate: start, endDate: end });
                  }}
                  startDate={checkIn}
                  endDate={checkOut}
                  selectsRange
                  minDate={new Date()}
                  inline
                  excludeDates={[...blockedDays].map(s => new Date(s + "T00:00:00"))}
                  placeholderText="체크인/체크아웃 선택"
                />
              </div>
              <button onClick={handleBooking} disabled={!currentUser}>
                {currentUser ? "예약하기" : "로그인 후 예약"}
              </button>
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
  background: linear-gradient(180deg, var(--surface) 0%, var(--surface) 100%);

  h1 {
    margin: 0 0 16px 0;
    text-align: center;
    font-size: 1.8rem;
    letter-spacing: -0.2px;
    color: var(--text);
  }
`;

const TopBar = styled.div`
  display: flex;
  align-items: center;
  gap: 12px;
  background: var(--bg-elevated);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 10px 12px;
  margin-bottom: 16px;
`;

const Brand = styled.div`
  font-weight: 800;
  color: var(--text);
`;

const Spacer = styled.div`
  flex: 1;
`;

const AuthInline = styled.div`
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap; /* 줄바꿈 허용 */
  justify-content: center;

  form {
    display: flex;
    gap: 8px;
    align-items: center;
    flex-wrap: wrap;
  }

  /* --- 반응형 처리 --- */
  @media (max-width: 768px) {
    gap: 8px;
    flex-direction: column; /* 세로 정렬 */
    align-items: stretch;

    form {
      flex-direction: column;
      width: 100%;

      input,
      button {
        width: 100%; /* 버튼과 입력창을 한 줄로 */
      }
    }
  }

  @media (max-width: 480px) {
    gap: 6px;
    form {
      gap: 6px;
    }
  }
`;

const UserInline = styled.div`
  display: flex;
  gap: 10px;
  align-items: center;
`;

// const Divider = styled.div`
//   width: 1px;
//   height: 28px;
//   background: #e5e7eb;
// `;

const SmallInput = styled.input`
  padding: 8px 10px;
  border: 1px solid var(--border);
  border-radius: 8px;
  outline: none;
  font-size: 0.9rem;
`;

const SmallButton = styled.button`
  padding: 8px 12px;
  border: none;
  border-radius: 8px;
  background: linear-gradient(135deg, var(--accent), var(--accent-2));
  color: #fff;
  font-weight: 700;
  cursor: pointer;
`;

const NavLink = styled(Link)`
  padding: 8px 10px;
  background: var(--bg-elevated);
  border: 1px solid var(--border);
  border-radius: 8px;
  color: var(--text);
  text-decoration: none;
  font-weight: 700;
  transition: all 0.2s ease;

  &:hover {
    background: var(--surface);
    border-color: var(--accent);
    color: var(--accent);
  }
`;

const Avatar = styled.div`
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--accent), var(--accent-2));
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
`;

const Nick = styled.div`
  color: var(--text);
  font-weight: 700;
`;

const AuthError = styled.div`
  margin: 6px 0 10px;
  color: var(--error);
  text-align: center;
  font-weight: 700;
`;

const RoomGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
`;

const RoomCard = styled.div`
  background: var(--bg-elevated);
  border: 1px solid var(--border);
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
    color: var(--text);
  }

  p {
    color: var(--muted-2);
    font-size: 0.92rem;
    margin: 0;
  }
`;

const RoomImage = styled.div`
  width: 100%;
  height: 120px;
  background: linear-gradient(135deg, var(--accent), var(--accent-2));
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
  background: var(--bg-elevated);
  border-radius: 14px;
  padding: 24px;
  width: 250px;
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
    color: var(--text);
  }

  p {
    margin: 4px 0;
    color: var(--muted);
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
    color: var(--text);
    transform: scale(1.05);
  }
`;

const Description = styled.p`
  margin-top: 10px;
  color: var(--muted);
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
  border-bottom: 1px solid var(--border);

  p {
    margin: 0 0 6px 0;
    font-size: 0.92rem;
    color: var(--text);
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
    color: var(--text);
  }

  input {
    margin-left: 8px;
    padding: 8px 10px;
    border: 1px solid var(--border);
    border-radius: 8px;
    font-size: 0.92rem;
    outline: none;
    transition: border-color 0.2s ease, box-shadow 0.2s ease;

    &:focus {
      border-color: var(--accent);
      box-shadow: 0 0 0 3px rgba(79,172,254,0.2);
    }
  }

  button {
    padding: 10px 14px;
    border: none;
    background: linear-gradient(135deg, var(--accent), var(--accent-2));
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
  color: var(--error);
  font-size: 0.88rem;
  font-weight: 600;
`;

const ThemeToggle = styled.button`
  padding: 6px 10px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: var(--bg-elevated);
  color: var(--text);
  cursor: pointer;
`;