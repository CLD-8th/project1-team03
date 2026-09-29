const loginForm = document.getElementById("loginForm");

const userIdInput = document.getElementById("userId");
const passwordInput = document.getElementById("password");

const loginButton = document.getElementById("loginButton");
const errorMessage = document.getElementById("errorMessage");


loginForm.addEventListener("submit", async (event) => {

    event.preventDefault();

    hideError();


    const userId = userIdInput.value.trim();
    const password = passwordInput.value;


    if (!userId || !password) {

        showError("아이디와 비밀번호를 입력해주세요.");

        return;
    }


    loginButton.disabled = true;
    loginButton.textContent = "로그인 중...";


    try {

        const response = await fetch("/api/auth/login", {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({
                userId: userId,
                password: password
            })

        });


        if (!response.ok) {

            showError("아이디 또는 비밀번호가 올바르지 않습니다.");

            return;
        }


        const data = await response.json();


        /*
         * 로그인 응답
         *
         * {
         *   accessToken,
         *   refreshToken,
         *   memberId,
         *   nickname
         * }
         */


        localStorage.setItem(
            "accessToken",
            data.accessToken
        );

        localStorage.setItem(
            "refreshToken",
            data.refreshToken
        );

        localStorage.setItem(
            "memberId",
            data.memberId
        );

        localStorage.setItem(
            "nickname",
            data.nickname
        );


        window.location.href = "/shops";


    } catch (error) {

        console.error(error);

        showError(
            "서버와 통신할 수 없습니다. 잠시 후 다시 시도해주세요."
        );


    } finally {

        loginButton.disabled = false;
        loginButton.textContent = "로그인";

    }

});


function showError(message) {

    errorMessage.textContent = message;

    errorMessage.classList.add("show");

}


function hideError() {

    errorMessage.textContent = "";

    errorMessage.classList.remove("show");

}