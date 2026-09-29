-- =========================================================
-- FOODY DATABASE
-- MySQL 8.x
-- =========================================================


-- =========================================================
-- 1. DATABASE 생성
-- =========================================================

CREATE DATABASE IF NOT EXISTS foody
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE foody;


-- =========================================================
-- 2. 기존 TABLE 제거
-- FK 때문에 review → shop/member 순서로 삭제
-- =========================================================

DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS shop;
DROP TABLE IF EXISTS member;


-- =========================================================
-- 3. MEMBER TABLE
-- =========================================================

CREATE TABLE member (

    id BIGINT NOT NULL AUTO_INCREMENT,

    user_id VARCHAR(100) NOT NULL,

    password VARCHAR(200) NOT NULL,

    nickname VARCHAR(20) NOT NULL,

    updated_at DATETIME NOT NULL
        DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),

    UNIQUE KEY uk_member_user_id (user_id)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


-- =========================================================
-- 4. SHOP TABLE
-- =========================================================

CREATE TABLE shop (

    id BIGINT NOT NULL AUTO_INCREMENT,

    shop_name VARCHAR(100) NOT NULL,

    address VARCHAR(200) NOT NULL,

    view_count BIGINT NOT NULL DEFAULT 0,

    avg_rating INT NULL,

    created_at DATETIME NOT NULL
        DEFAULT CURRENT_TIMESTAMP,

    detail TEXT NULL,

    thumbnail VARCHAR(500) NULL,

    category VARCHAR(100) NOT NULL,

    PRIMARY KEY (id),

    UNIQUE KEY uk_shop_shop_name (shop_name),

    INDEX idx_shop_category (category),

    INDEX idx_shop_view_count (view_count),

    INDEX idx_shop_avg_rating (avg_rating)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


-- =========================================================
-- 5. REVIEW TABLE
-- =========================================================

CREATE TABLE review (

    id BIGINT NOT NULL AUTO_INCREMENT,

    rating INT NOT NULL,

    content TEXT NOT NULL,

    image_url VARCHAR(500) NULL,

    updated_at DATETIME NOT NULL
        DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    member_id BIGINT NOT NULL,

    shop_id BIGINT NOT NULL,

    PRIMARY KEY (id),

    INDEX idx_review_member_id (member_id),

    INDEX idx_review_shop_id (shop_id),

    CONSTRAINT fk_review_member
        FOREIGN KEY (member_id)
        REFERENCES member(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_review_shop
        FOREIGN KEY (shop_id)
        REFERENCES shop(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT chk_review_rating
        CHECK (rating BETWEEN 1 AND 5)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;


-- =========================================================
-- 6. SHOP 가상 데이터 150개 자동 생성
-- =========================================================

INSERT INTO shop
(
    shop_name,
    address,
    view_count,
    avg_rating,
    created_at,
    detail,
    thumbnail,
    category
)

WITH RECURSIVE numbers AS
(
    SELECT 1 AS n

    UNION ALL

    SELECT n + 1
    FROM numbers
    WHERE n < 150
)

SELECT

    -- =====================================================
    -- 가게 이름
    -- =====================================================

    CONCAT(
        CASE MOD(n - 1, 15)

            WHEN 0 THEN '서울밥상'
            WHEN 1 THEN '도쿄키친'
            WHEN 2 THEN '황금반점'
            WHEN 3 THEN '라비타'
            WHEN 4 THEN '카페소담'
            WHEN 5 THEN '불꽃고기'
            WHEN 6 THEN '청춘분식'
            WHEN 7 THEN '바삭치킨'
            WHEN 8 THEN '버거하우스'
            WHEN 9 THEN '달콤베이커리'
            WHEN 10 THEN '맛있는국밥'
            WHEN 11 THEN '스시마루'
            WHEN 12 THEN '마라공방'
            WHEN 13 THEN '파스타랩'
            WHEN 14 THEN '커피정원'

        END,

        ' ',

        LPAD(n, 3, '0'),

        '호점'
    ) AS shop_name,


    -- =====================================================
    -- 주소
    -- =====================================================

    CONCAT(

        CASE MOD(n - 1, 20)

            WHEN 0 THEN '서울특별시 강남구 역삼동 '
            WHEN 1 THEN '서울특별시 강남구 삼성동 '
            WHEN 2 THEN '서울특별시 마포구 서교동 '
            WHEN 3 THEN '서울특별시 마포구 연남동 '
            WHEN 4 THEN '서울특별시 송파구 잠실동 '
            WHEN 5 THEN '서울특별시 송파구 석촌동 '
            WHEN 6 THEN '서울특별시 성동구 성수동 '
            WHEN 7 THEN '서울특별시 종로구 익선동 '
            WHEN 8 THEN '서울특별시 용산구 한남동 '
            WHEN 9 THEN '서울특별시 용산구 이태원동 '
            WHEN 10 THEN '서울특별시 관악구 신림동 '
            WHEN 11 THEN '서울특별시 서대문구 신촌동 '
            WHEN 12 THEN '서울특별시 광진구 자양동 '
            WHEN 13 THEN '서울특별시 영등포구 여의도동 '
            WHEN 14 THEN '서울특별시 강서구 마곡동 '
            WHEN 15 THEN '서울특별시 노원구 상계동 '
            WHEN 16 THEN '서울특별시 동작구 사당동 '
            WHEN 17 THEN '서울특별시 서초구 서초동 '
            WHEN 18 THEN '서울특별시 중구 을지로 '
            WHEN 19 THEN '서울특별시 동대문구 회기동 '

        END,

        (10 + n),

        '-',

        (MOD(n * 7, 99) + 1)

    ) AS address,


    -- =====================================================
    -- 조회수
    -- 37 ~ 약 5000 사이
    -- =====================================================

    MOD(n * 173 + 97, 5000) + 37
        AS view_count,


    -- =====================================================
    -- 평균 평점
    -- 현재 컬럼이 INT라 1~5
    -- =====================================================

    MOD(n * 7, 5) + 1
        AS avg_rating,


    -- =====================================================
    -- 생성 날짜
    -- =====================================================

    DATE_SUB(
        NOW(),
        INTERVAL MOD(n * 3, 365) DAY
    ) AS created_at,


    -- =====================================================
    -- 상세 설명
    -- =====================================================

    CONCAT(

        CASE MOD(n - 1, 10)

            WHEN 0 THEN
                '정갈한 한식과 따뜻한 집밥 메뉴를 제공하는 가게입니다.'

            WHEN 1 THEN
                '신선한 재료로 만든 일식 메뉴를 즐길 수 있는 가게입니다.'

            WHEN 2 THEN
                '짜장면, 짬뽕, 탕수육 등 다양한 중식 메뉴를 제공합니다.'

            WHEN 3 THEN
                '파스타와 스테이크를 중심으로 다양한 양식 메뉴를 제공합니다.'

            WHEN 4 THEN
                '직접 내린 커피와 다양한 디저트를 즐길 수 있는 카페입니다.'

            WHEN 5 THEN
                '신선한 고기와 다양한 구이 메뉴를 즐길 수 있습니다.'

            WHEN 6 THEN
                '떡볶이, 튀김, 김밥 등 다양한 분식 메뉴를 제공합니다.'

            WHEN 7 THEN
                '바삭한 치킨과 다양한 사이드 메뉴를 판매합니다.'

            WHEN 8 THEN
                '두툼한 패티를 사용한 수제버거 전문점입니다.'

            WHEN 9 THEN
                '매일 직접 만든 빵과 디저트를 판매하는 베이커리입니다.'

        END,

        ' 테스트용 가상 가게 데이터 #',

        n

    ) AS detail,


    -- =====================================================
    -- 썸네일
    -- Picsum 테스트 이미지
    -- =====================================================

    CONCAT(
        'https://picsum.photos/seed/foody',
        n,
        '/600/400'
    ) AS thumbnail,


    -- =====================================================
    -- 카테고리
    -- =====================================================

    CASE MOD(n - 1, 10)

        WHEN 0 THEN '한식'
        WHEN 1 THEN '일식'
        WHEN 2 THEN '중식'
        WHEN 3 THEN '양식'
        WHEN 4 THEN '카페'
        WHEN 5 THEN '고기'
        WHEN 6 THEN '분식'
        WHEN 7 THEN '치킨'
        WHEN 8 THEN '패스트푸드'
        WHEN 9 THEN '디저트'

    END AS category


FROM numbers;


-- =========================================================
-- 7. 데이터 확인
-- =========================================================

SELECT COUNT(*) AS shop_count
FROM shop;


-- =========================================================
-- 8. 가게 전체 확인
-- =========================================================

SELECT *
FROM shop
ORDER BY id ASC;


-- =========================================================
-- 9. 카테고리별 개수 확인
-- =========================================================

SELECT
    category,
    COUNT(*) AS count
FROM shop
GROUP BY category
ORDER BY category;


-- =========================================================
-- 10. 조회수 높은 순
-- =========================================================

SELECT *
FROM shop
ORDER BY view_count DESC;


-- =========================================================
-- 11. 평점 높은 순
-- =========================================================

SELECT *
FROM shop
ORDER BY avg_rating DESC, view_count DESC;