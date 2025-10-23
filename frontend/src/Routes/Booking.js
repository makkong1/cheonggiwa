import { useEffect, useState, useMemo } from "react";
import styled from "styled-components";
import { Link } from "react-router-dom";
import { useAuth } from "../AuthContext";

function Booking() {
  const { user, loading: authLoading } = useAuth();
  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState("");
  const [showReviewModal, setShowReviewModal] = useState(false);
  const [selectedBooking, setSelectedBooking] = useState(null);
  const [reviewContent, setReviewContent] = useState("");
  const [reviewLoading, setReviewLoading] = useState(false);

  const load = () => {
    if (!user) return;
    
    setLoading(true);
    setErr("");
    fetch(`/api/booking/user/${user.id}`)
      .then(res => {
        if (!res.ok) throw new Error("예약 목록을 불러올 수 없습니다.");
        return res.json();
      })
      .then(data => setBookings(Array.isArray(data) ? data : []))
      .catch(e => setErr(e.message))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    if (user) {
      load();
    }
  }, [user]);

  const onCancel = async (id) => {
    if (!window.confirm("해당 예약을 취소하시겠습니까?")) return;
    alert(id);
    await fetch(`/api/booking/${id}`, { method: "DELETE" });
    load();
  };

  const onCheckIn = async (id) => {
    await fetch(`/api/booking/${id}/checkin`, { method: "POST" });
    load();
  };

  const onCheckOut = async (id) => {
    await fetch(`/api/booking/${id}/checkout`, { method: "POST" });
    load();
  };

  const onReview = (booking) => {
    setSelectedBooking(booking);
    setReviewContent("");
    setShowReviewModal(true);
  };

  const onSubmitReview = async () => {
    if (!reviewContent.trim()) {
      alert("리뷰 내용을 입력해주세요.");
      return;
    }

    setReviewLoading(true);
    try {
      const response = await fetch(`/api/review/${selectedBooking.roomId}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          roomId: selectedBooking.roomId,
          userId: user.id,
          content: reviewContent,
        }),
      });

      if (!response.ok) {
        throw new Error("리뷰 작성에 실패했습니다.");
      }

      alert("리뷰가 성공적으로 작성되었습니다.");
      setShowReviewModal(false);
      setReviewContent("");
      setSelectedBooking(null);
    } catch (error) {
      alert(error.message);
    } finally {
      setReviewLoading(false);
    }
  };

  const onCloseReviewModal = () => {
    setShowReviewModal(false);
    setReviewContent("");
    setSelectedBooking(null);
  };

  const sorted = useMemo(() => {
    return [...bookings].sort((a, b) => {
      const aDate = new Date(a.checkIn || a.startDate || 0).getTime();
      const bDate = new Date(b.checkIn || b.startDate || 0).getTime();
      return aDate - bDate;
    });
  }, [bookings]);

  // 로그인하지 않은 경우
  if (authLoading) {
    return (
      <Container>
        <StatusBox>로그인 상태를 확인하는 중...</StatusBox>
      </Container>
    );
  }

  if (!user) {
    return (
      <Container>
        <HeaderBar>
          <h1>내 예약</h1>
          <div>
            <NavLink to="/login">로그인</NavLink>
            <NavLink to="/">홈으로</NavLink>
          </div>
        </HeaderBar>
        <EmptyBox>로그인이 필요합니다.</EmptyBox>
      </Container>
    );
  }

  return (
    <Container>
      <HeaderBar>
        <h1>내 예약</h1>
        <div>
          <NavLink to="/">홈으로</NavLink>
        </div>
      </HeaderBar>

      {loading && <StatusBox>불러오는 중...</StatusBox>}
      {err && <ErrorBox>{err}</ErrorBox>}

      {!loading && !err && sorted.length === 0 && (
        <EmptyBox>예약 내역이 없습니다.</EmptyBox>
      )}

      <List>
        {sorted.map((bk) => {
          const id = bk.id ?? bk.bookingId;
          const roomName = bk.room?.roomName ?? bk.roomName ?? `객실 #${bk.roomId ?? "-"}`;
          const checkIn = bk.checkIn || bk.startDate;
          const checkOut = bk.checkOut || bk.endDate;
          const status = bk.status || bk.checkStatus || "UNKNOWN";

          const canCancel = status === "WAITING" || status === "CONFIRMED";
          const canCheckIn = status === "CONFIRMED";
          const canCheckOut = status === "CHECKED_IN";
          const canReview = status === "COMPLETED";

          return (
            <Card key={id}>
              <CardHeader>
                <Title>{roomName}</Title>
                <StatusBadge data-status={status}>{status}</StatusBadge>
              </CardHeader>

              <DetailRow>
                <Label>체크인</Label>
                <Value>{checkIn ? new Date(checkIn).toLocaleDateString() : "-"}</Value>
              </DetailRow>
              <DetailRow>
                <Label>체크아웃</Label>
                <Value>{checkOut ? new Date(checkOut).toLocaleDateString() : "-"}</Value>
              </DetailRow>
              <DetailRow>
                <Label>예약번호</Label>
                <Value>#{id}</Value>
              </DetailRow>

              <Actions>
                {canCancel && <ActionButton onClick={() => onCancel(id)}>예약 취소</ActionButton>}
                {canCheckIn && <PrimaryButton onClick={() => onCheckIn(id)}>체크인</PrimaryButton>}
                {canCheckOut && <PrimaryButton onClick={() => onCheckOut(id)}>체크아웃</PrimaryButton>}
                {canReview && <ReviewButton onClick={() => onReview(bk)}>리뷰 작성</ReviewButton>}
              </Actions>
            </Card>
          );
        })}
      </List>

      {/* 리뷰 작성 모달 */}
      {showReviewModal && (
        <ModalOverlay onClick={onCloseReviewModal}>
          <ModalContent onClick={(e) => e.stopPropagation()}>
            <ModalHeader>
              <h2>리뷰 작성</h2>
              <CloseButton onClick={onCloseReviewModal}>×</CloseButton>
            </ModalHeader>
            
            <ModalBody>
              <RoomInfo>
                <strong>{selectedBooking?.room?.roomName || selectedBooking?.roomName || `객실 #${selectedBooking?.roomId}`}</strong>
              </RoomInfo>
              
              <TextArea
                placeholder="숙소에 대한 솔직한 리뷰를 작성해주세요..."
                value={reviewContent}
                onChange={(e) => setReviewContent(e.target.value)}
                rows={6}
              />
            </ModalBody>
            
            <ModalFooter>
              <ModalButton onClick={onCloseReviewModal}>취소</ModalButton>
              <SubmitButton onClick={onSubmitReview} disabled={reviewLoading}>
                {reviewLoading ? "작성 중..." : "리뷰 작성"}
              </SubmitButton>
            </ModalFooter>
          </ModalContent>
        </ModalOverlay>
      )}
    </Container>
  );
}

export default Booking;

const Container = styled.div`
  padding: 20px;
  min-height: 100vh;
  background: linear-gradient(180deg, #f7f9fc 0%, #eef2f7 100%);
  font-family: "Arial", sans-serif;
`;

const HeaderBar = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;

  h1 {
    margin: 0;
    font-size: 1.6rem;
    color: #111827;
  }
`;

const NavLink = styled(Link)`
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: #ffffff;
  color: #111827;
  text-decoration: none;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  transition: transform 0.12s ease, box-shadow 0.12s ease;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 6px 16px rgba(0,0,0,0.08);
  }
`;

const StatusBox = styled.div`
  margin-top: 12px;
  padding: 12px 14px;
  background: #eff6ff;
  color: #1e40af;
  border: 1px solid #bfdbfe;
  border-radius: 10px;
`;

const ErrorBox = styled.div`
  margin-top: 12px;
  padding: 12px 14px;
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #fecaca;
  border-radius: 10px;
`;

const EmptyBox = styled.div`
  margin-top: 24px;
  padding: 20px;
  background: #ffffff;
  color: #6b7280;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  text-align: center;
`;

const List = styled.div`
  margin-top: 16px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
`;

const Card = styled.div`
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 16px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.04);
`;

const CardHeader = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
`;

const Title = styled.div`
  font-size: 1.05rem;
  color: #0f172a;
  font-weight: 700;
`;

const StatusBadge = styled.span`
  padding: 6px 10px;
  font-size: 0.8rem;
  border-radius: 999px;
  border: 1px solid #e5e7eb;
  color: #374151;
  background: #f9fafb;

  &[data-status="WAITING"] {
    color: #a16207;
    background: #fef3c7;
    border-color: #fde68a;
  }
  &[data-status="CONFIRMED"] {
    color: #065f46;
    background: #d1fae5;
    border-color: #a7f3d0;
  }
  &[data-status="CHECKED_IN"] {
    color: #1e40af;
    background: #dbeafe;
    border-color: #bfdbfe;
  }
  &[data-status="CANCELED"],
  &[data-status="CANCELLED"] {
    color: #991b1b;
    background: #fee2e2;
    border-color: #fecaca;
  }
`;

const DetailRow = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 0;
  border-bottom: 1px dashed #eef2f7;

  &:last-of-type {
    border-bottom: none;
    margin-bottom: 8px;
  }
`;

const Label = styled.div`
  color: #6b7280;
  font-size: 0.9rem;
`;

const Value = styled.div`
  color: #111827;
  font-weight: 600;
`;

const Actions = styled.div`
  display: flex;
  gap: 8px;
  margin-top: 10px;
`;

const ActionButton = styled.button`
  padding: 8px 12px;
  border: 1px solid #e5e7eb;
  background: #ffffff;
  color: #1f2937;
  border-radius: 10px;
  cursor: pointer;
  transition: transform 0.12s ease, box-shadow 0.12s ease;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 8px 18px rgba(0,0,0,0.08);
  }
`;

const PrimaryButton = styled.button`
  padding: 8px 12px;
  border: none;
  background: linear-gradient(135deg, #4facfe, #00c3fe);
  color: #ffffff;
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
`;

const ReviewButton = styled.button`
  padding: 8px 12px;
  border: 1px solid #10b981;
  background: #ffffff;
  color: #10b981;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 600;
  transition: transform 0.12s ease, box-shadow 0.12s ease, background 0.12s ease;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 8px 18px rgba(16, 185, 129, 0.15);
    background: #f0fdf4;
  }
`;

// 모달 스타일
const ModalOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
`;

const ModalContent = styled.div`
  background: #ffffff;
  border-radius: 16px;
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
`;

const ModalHeader = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px 16px;
  border-bottom: 1px solid #e5e7eb;

  h2 {
    margin: 0;
    font-size: 1.25rem;
    color: #111827;
    font-weight: 700;
  }
`;

const CloseButton = styled.button`
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #6b7280;
  cursor: pointer;
  padding: 4px;
  border-radius: 6px;
  transition: background 0.12s ease;

  &:hover {
    background: #f3f4f6;
  }
`;

const ModalBody = styled.div`
  padding: 20px 24px;
`;

const RoomInfo = styled.div`
  margin-bottom: 16px;
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
  color: #374151;
  font-size: 0.95rem;
`;

const TextArea = styled.textarea`
  width: 100%;
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.95rem;
  font-family: inherit;
  resize: vertical;
  min-height: 120px;
  transition: border-color 0.12s ease, box-shadow 0.12s ease;

  &:focus {
    outline: none;
    border-color: #3b82f6;
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  }

  &::placeholder {
    color: #9ca3af;
  }
`;

const ModalFooter = styled.div`
  display: flex;
  gap: 12px;
  padding: 16px 24px 20px;
  border-top: 1px solid #e5e7eb;
  justify-content: flex-end;
`;

const ModalButton = styled.button`
  padding: 10px 16px;
  border: 1px solid #d1d5db;
  background: #ffffff;
  color: #374151;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.12s ease, border-color 0.12s ease;

  &:hover {
    background: #f9fafb;
    border-color: #9ca3af;
  }
`;

const SubmitButton = styled.button`
  padding: 10px 16px;
  border: none;
  background: linear-gradient(135deg, #10b981, #059669);
  color: #ffffff;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: transform 0.12s ease, box-shadow 0.12s ease, filter 0.12s ease;

  &:hover:not(:disabled) {
    transform: translateY(-1px);
    box-shadow: 0 8px 18px rgba(16, 185, 129, 0.25);
    filter: brightness(1.05);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
`;