ALTER TABLE consultation_categories
    ADD COLUMN parent_id BIGINT REFERENCES consultation_categories(id),
    ADD COLUMN depth SMALLINT NOT NULL DEFAULT 1,
    ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT now();

CREATE INDEX idx_consultation_categories_parent_id ON consultation_categories(parent_id);
CREATE INDEX idx_consultation_categories_depth ON consultation_categories(depth);

UPDATE tickets SET category_id = NULL;

DELETE FROM consultation_categories;

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth) VALUES
    ('ORDER_PAYMENT', '주문/결제', NULL, true, 1, NULL, 1),
    ('DELIVERY', '배송', NULL, true, 2, NULL, 1),
    ('EXCHANGE_RETURN_REFUND', '교환/반품/환불', NULL, true, 3, NULL, 1),
    ('PRODUCT_SERVICE', '상품/서비스', NULL, true, 4, NULL, 1),
    ('MEMBER_ACCOUNT', '회원/계정', NULL, true, 5, NULL, 1),
    ('COMPLAINT', '불만/클레임', NULL, true, 6, NULL, 1),
    ('ETC', '기타', NULL, true, 7, NULL, 1);

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'ORDER', '주문', NULL, true, 1, id, 2 FROM consultation_categories WHERE code = 'ORDER_PAYMENT';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'PAYMENT', '결제', NULL, true, 2, id, 2 FROM consultation_categories WHERE code = 'ORDER_PAYMENT';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'ORDER_CANCEL', '주문 취소', NULL, true, 1, id, 3 FROM consultation_categories WHERE code = 'ORDER';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'ORDER_CHANGE', '주문 변경', NULL, true, 2, id, 3 FROM consultation_categories WHERE code = 'ORDER';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'PAYMENT_FAIL', '결제 실패', NULL, true, 1, id, 3 FROM consultation_categories WHERE code = 'PAYMENT';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'PAYMENT_RECEIPT', '결제 영수증', NULL, true, 2, id, 3 FROM consultation_categories WHERE code = 'PAYMENT';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'DELIVERY_STATUS', '배송 상태', NULL, true, 1, id, 2 FROM consultation_categories WHERE code = 'DELIVERY';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'DELIVERY_ISSUE', '배송 문제', NULL, true, 2, id, 2 FROM consultation_categories WHERE code = 'DELIVERY';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'DELIVERY_DELAY', '배송 지연', NULL, true, 1, id, 3 FROM consultation_categories WHERE code = 'DELIVERY_STATUS';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'DELIVERY_TRACKING', '배송 조회', NULL, true, 2, id, 3 FROM consultation_categories WHERE code = 'DELIVERY_STATUS';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'DELIVERY_LOST', '배송 분실', NULL, true, 1, id, 3 FROM consultation_categories WHERE code = 'DELIVERY_ISSUE';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'DELIVERY_WRONG', '오배송', NULL, true, 2, id, 3 FROM consultation_categories WHERE code = 'DELIVERY_ISSUE';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'RETURN', '반품', NULL, true, 1, id, 2 FROM consultation_categories WHERE code = 'EXCHANGE_RETURN_REFUND';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'EXCHANGE', '교환', NULL, true, 2, id, 2 FROM consultation_categories WHERE code = 'EXCHANGE_RETURN_REFUND';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'RETURN_REQUEST', '반품 신청', NULL, true, 1, id, 3 FROM consultation_categories WHERE code = 'RETURN';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'RETURN_STATUS', '반품 진행', NULL, true, 2, id, 3 FROM consultation_categories WHERE code = 'RETURN';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'EXCHANGE_REQUEST', '교환 신청', NULL, true, 1, id, 3 FROM consultation_categories WHERE code = 'EXCHANGE';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'REFUND_STATUS', '환불 진행', NULL, true, 2, id, 3 FROM consultation_categories WHERE code = 'EXCHANGE';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'PRODUCT', '상품', NULL, true, 1, id, 2 FROM consultation_categories WHERE code = 'PRODUCT_SERVICE';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'SERVICE', '서비스', NULL, true, 2, id, 2 FROM consultation_categories WHERE code = 'PRODUCT_SERVICE';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'PRODUCT_DEFECT', '상품 불량', NULL, true, 1, id, 3 FROM consultation_categories WHERE code = 'PRODUCT';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'PRODUCT_INFO', '상품 문의', NULL, true, 2, id, 3 FROM consultation_categories WHERE code = 'PRODUCT';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'SERVICE_USAGE', '서비스 이용', NULL, true, 1, id, 3 FROM consultation_categories WHERE code = 'SERVICE';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'SERVICE_ERROR', '서비스 오류', NULL, true, 2, id, 3 FROM consultation_categories WHERE code = 'SERVICE';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'ACCOUNT', '계정', NULL, true, 1, id, 2 FROM consultation_categories WHERE code = 'MEMBER_ACCOUNT';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'MEMBER', '회원', NULL, true, 2, id, 2 FROM consultation_categories WHERE code = 'MEMBER_ACCOUNT';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'LOGIN_ISSUE', '로그인 문제', NULL, true, 1, id, 3 FROM consultation_categories WHERE code = 'ACCOUNT';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'PASSWORD_RESET', '비밀번호 재설정', NULL, true, 2, id, 3 FROM consultation_categories WHERE code = 'ACCOUNT';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'MEMBER_JOIN', '회원 가입', NULL, true, 1, id, 3 FROM consultation_categories WHERE code = 'MEMBER';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'MEMBER_WITHDRAW', '회원 탈퇴', NULL, true, 2, id, 3 FROM consultation_categories WHERE code = 'MEMBER';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'SERVICE_COMPLAINT', '서비스 불만', NULL, true, 1, id, 2 FROM consultation_categories WHERE code = 'COMPLAINT';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'POLICY_COMPLAINT', '정책 불만', NULL, true, 2, id, 2 FROM consultation_categories WHERE code = 'COMPLAINT';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'COMPLAINT_AGENT', '상담원 불만', NULL, true, 1, id, 3 FROM consultation_categories WHERE code = 'SERVICE_COMPLAINT';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'COMPLAINT_RESPONSE', '응대 지연', NULL, true, 2, id, 3 FROM consultation_categories WHERE code = 'SERVICE_COMPLAINT';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'COMPLAINT_POLICY', '정책 불만', NULL, true, 1, id, 3 FROM consultation_categories WHERE code = 'POLICY_COMPLAINT';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'COMPLAINT_REFUND', '환불 불만', NULL, true, 2, id, 3 FROM consultation_categories WHERE code = 'POLICY_COMPLAINT';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'GENERAL', '일반 문의', NULL, true, 1, id, 2 FROM consultation_categories WHERE code = 'ETC';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'PARTNERSHIP', '제휴/협력', NULL, true, 2, id, 2 FROM consultation_categories WHERE code = 'ETC';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'OTHER', '기타 문의', NULL, true, 1, id, 3 FROM consultation_categories WHERE code = 'GENERAL';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'SUGGESTION', '개선 제안', NULL, true, 2, id, 3 FROM consultation_categories WHERE code = 'GENERAL';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'PARTNERSHIP_INQUIRY', '제휴 문의', NULL, true, 1, id, 3 FROM consultation_categories WHERE code = 'PARTNERSHIP';

INSERT INTO consultation_categories (code, name, description, active, sort_order, parent_id, depth)
SELECT 'BULK_ORDER', '대량 주문', NULL, true, 2, id, 3 FROM consultation_categories WHERE code = 'PARTNERSHIP';

UPDATE tickets SET category_id = (SELECT id FROM consultation_categories WHERE code = 'LOGIN_ISSUE')
WHERE id = 1;

UPDATE tickets SET category_id = (SELECT id FROM consultation_categories WHERE code = 'PAYMENT_RECEIPT')
WHERE id = 2;

UPDATE tickets SET category_id = (SELECT id FROM consultation_categories WHERE code = 'SERVICE_ERROR')
WHERE id = 3;
