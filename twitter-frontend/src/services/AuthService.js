import axios from "axios";

// const API_URL = "http://127.0.0.1:8080/api/auth/";
// const BACKEND_URL = window._env_.REACT_APP_BACKEND_URL == undefined ? "http://localhost:8080/" : window._env_.REACT_APP_BACKEND_URL;
const BACKEND_URL = "/api/auth/"

const register = (username, firstname, lastname, email, password) => {
	return axios.post(BACKEND_URL + "signup", {
		username,
		firstname,
		lastname,
		email,
		password,
	});
};

const login = async (username, password) => {
	const response = await axios.post(BACKEND_URL + "signin", {
		username,
		password,
	});
	if (response.data.accessToken) {
		localStorage.setItem("accessToken", response.data.accessToken);
	}
	return response.data;
};

const logout = () => {
	localStorage.removeItem("accessToken");
};

export { register, login, logout };
