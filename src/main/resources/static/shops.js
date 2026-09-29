document.addEventListener("DOMContentLoaded", () => {
    const guestMenu = document.getElementById("guestMenu");
    const memberMenu = document.getElementById("memberMenu");
    const nickname = document.getElementById("nickname");
    const logoutButton = document.getElementById("logoutButton");

    const accessToken = localStorage.getItem("accessToken");
    const savedNickname = localStorage.getItem("nickname");

    // 로그인 상태 표시
    if (accessToken) {
        guestMenu.style.display = "none";
        memberMenu.style.display = "flex";

        if (savedNickname) {
            nickname.textContent = savedNickname;
        }
    } else {
        guestMenu.style.display = "block";
        memberMenu.style.display = "none";
    }

    // 로그아웃
    if (logoutButton) {
        logoutButton.addEventListener("click", async () => {
            const token = localStorage.getItem("accessToken");

            try {
                if (token) {
                    const response = await fetch("/api/auth/logout", {
                        method: "POST",
                        headers: {
                            "Authorization": `Bearer ${token}`
                        }
                    });

                    if (!response.ok && response.status !== 401) {
                        console.error("로그아웃 실패:", response.status);
                    }
                }
            } catch (error) {
                console.error("로그아웃 요청 오류:", error);
            } finally {
                // 서버 요청 성공 여부와 관계없이 브라우저 토큰 제거
                localStorage.removeItem("accessToken");
                localStorage.removeItem("refreshToken");
                localStorage.removeItem("memberId");
                localStorage.removeItem("nickname");

                window.location.href = "/login";
            }
        });
    }
});