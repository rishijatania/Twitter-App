import axios from "axios";

const API_URL = "http://127.0.0.1:8080/api/auth/";

const register = (username, firstname, lastname, email, password) => {
  return axios.post(API_URL + "signup", {
    username,
    firstname,
    lastname,
    email,
    password,
  });
};

const login = async (username, password) => {
  const response = await axios.post(API_URL + "signin", {
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
