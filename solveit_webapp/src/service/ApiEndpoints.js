// BASE URL FOR PROD
export const BASE_URL = 'https://solveit-backend.onrender.com/api/v1';

// BASE URL FOR LOCAL
// export const BASE_URL = 'http://localhost:8080/api/v1';

export const API_ENDPOINTS = {
    LOGIN:  "/login",
    REGISTER: "/register",
    GET_USER_INFO:  "/user/info",
    UPDATE_USER_INFO: "/user/info",
    GET_FEEDS: "/users/questions/feeds",
    GET_FEEDS_RECENT: "/users/questions/recents",
    GET_FEEDS_POPULAR: "/users/questions/popular",
    LIKE_QUESTION: (questionId) => `/users/questions/upvote/${questionId}`,
    UNLIKE_QUESTION: (questionId) => `/users/questions/downvote/${questionId}`,
    UPDATE_ACCOUNT: "/income/current-month",
    CREATE_QUESTION: "/users/questions",
    UPDATE_QUESTION: (questionId) => `/users/questions/${questionId}`,
    DELETE_QUESTION: (questionId) => `/users/questions/${questionId}`,
    GET_QUESTION: (questionId) => `/users/questions/view/${questionId}`,
    GET_QUESTIONS_CURRENT_USER: "users/questions",
    ANSWERS_QUESTION: "/users/answers",


}