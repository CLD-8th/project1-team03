document.addEventListener("DOMContentLoaded", () => {

    const accessToken =
        localStorage.getItem("accessToken");

    const reviewFormArea =
        document.getElementById("reviewFormArea");

    const loginReviewMessage =
        document.getElementById("loginReviewMessage");

    const reviewForm =
        document.getElementById("reviewForm");


    // 로그인 상태에 따라 리뷰 작성 폼 표시
    if (accessToken) {

        reviewFormArea.style.display = "block";
        loginReviewMessage.style.display = "none";

    } else {

        reviewFormArea.style.display = "none";
        loginReviewMessage.style.display = "block";
    }


    // 기존 리뷰 불러오기
    loadReviews();


    // 리뷰 등록 이벤트
    if (reviewForm) {

        reviewForm.addEventListener(
            "submit",
            submitReview
        );
    }
});


async function submitReview(event) {

    event.preventDefault();


    const accessToken =
        localStorage.getItem("accessToken");


    if (!accessToken) {

        alert("로그인이 필요합니다.");

        window.location.href = "/login";

        return;
    }


    const rating =
        Number(
            document
                .getElementById("rating")
                .value
        );


    const content =
        document
            .getElementById("reviewContent")
            .value
            .trim();


    const imageInput =
        document.getElementById("reviewImage");


    if (!content) {

        alert("리뷰 내용을 입력해주세요.");

        return;
    }


    let imageUrl = null;


    try {

        /*
         * 이미지가 있으면 먼저 업로드
         */
        if (
            imageInput.files
            &&
            imageInput.files.length > 0
        ) {

            const formData =
                new FormData();


            formData.append(
                "image",
                imageInput.files[0]
            );


            const imageResponse =
                await fetch(
                    "/api/reviews/images",
                    {
                        method: "POST",

                        headers: {
                            "Authorization":
                                `Bearer ${accessToken}`
                        },

                        body: formData
                    }
                );


            if (imageResponse.status === 401) {

                alert("로그인이 만료되었습니다.");

                window.location.href = "/login";

                return;
            }


            if (!imageResponse.ok) {

                throw new Error(
                    `사진 업로드 실패: ${imageResponse.status}`
                );
            }


            const imageData =
                await imageResponse.json();


            imageUrl =
                imageData.imageUrl;
        }


        /*
         * 리뷰 등록
         */
        const response =
            await fetch(
                `/api/shops/${shopId}/reviews`,
                {
                    method: "POST",

                    headers: {

                        "Content-Type":
                            "application/json",

                        "Authorization":
                            `Bearer ${accessToken}`
                    },

                    body: JSON.stringify({

                        rating: rating,

                        content: content,

                        imageUrl: imageUrl
                    })
                }
            );


        if (response.status === 401) {

            alert("로그인이 필요합니다.");

            window.location.href = "/login";

            return;
        }


        if (!response.ok) {

            const errorText =
                await response.text();


            console.error(
                "리뷰 등록 실패:",
                response.status,
                errorText
            );


            throw new Error(
                `리뷰 등록 실패: ${response.status}`
            );
        }


        alert("리뷰가 등록되었습니다.");


        /*
         * 리뷰 등록 직후 페이지 새로고침
         *
         * 서버에서 shop.avg_rating을
         * 실제 리뷰 평균으로 변경했기 때문에
         * 페이지를 다시 불러오면
         * 새 평균 별점이 바로 표시된다.
         */
        window.location.reload();


    } catch (error) {

        console.error(error);

        alert(
            "리뷰 등록 중 오류가 발생했습니다."
        );
    }
}


async function loadReviews() {

    const reviewList =
        document.getElementById("reviewList");


    try {

        const response =
            await fetch(
                `/api/shops/${shopId}/reviews`
            );


        if (!response.ok) {

            throw new Error(
                `리뷰 조회 실패: ${response.status}`
            );
        }


        const reviews =
            await response.json();


        if (reviews.length === 0) {

            reviewList.innerHTML = `
                <p class="empty-review">
                    아직 작성된 리뷰가 없습니다.
                </p>
            `;

            return;
        }


        reviewList.innerHTML =
            reviews
                .map(review => {

                    const stars =
                        "★".repeat(
                            review.rating
                        )
                        +
                        "☆".repeat(
                            5 - review.rating
                        );


                    const image =
                        review.imageUrl
                            ? `
                                <img
                                    src="${review.imageUrl}"
                                    alt="리뷰 이미지"
                                    class="review-image"
                                >
                            `
                            : "";


                    return `
                        <article class="review-item">

                            <div class="review-top">

                                <span class="review-rating">
                                    ${stars}
                                </span>

                                <span class="review-nickname">
                                    ${escapeHtml(review.nickname)}
                                </span>

                            </div>

                            <p class="review-content">
                                ${escapeHtml(review.content)}
                            </p>

                            ${image}

                        </article>
                    `;
                })
                .join("");


    } catch (error) {

        console.error(
            "리뷰 조회 오류:",
            error
        );


        reviewList.innerHTML = `
            <p class="empty-review">
                리뷰를 불러오지 못했습니다.
            </p>
        `;
    }
}


function escapeHtml(text) {

    const div =
        document.createElement("div");

    div.textContent =
        text ?? "";

    return div.innerHTML;
}