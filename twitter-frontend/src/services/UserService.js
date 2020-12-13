import axios from "axios";
import authHeader from "./AuthHeader";

// const API_URL = "http://127.0.0.1:8080/api/user/";
// const MIDDLEWARE_URL = window._env_.REACT_APP_MIDDLEWARE_URL == undefined ? "http://localhost:5000/" : window._env_.REACT_APP_MIDDLEWARE_URL;
const MIDDLEWARE_URL = "/api/user"

const getUserProfile = () => {
  return axios.get(MIDDLEWARE_URL + "/profile", { headers: authHeader() });
};

const updateUserProfile = async (
  firstname,
  lastname,
  bio,
  proficPicChanged,
  avatar
) => {
  const formData = new FormData();
  const userForm = {
    firstname,
    lastname,
    bio,
    proficPicChanged,
  };
  if (proficPicChanged && avatar) {
    formData.append("profilePic", avatar);
  }
  formData.append("userForm", JSON.stringify(userForm));
  const accessToken = localStorage.getItem("accessToken");
  const config = {
    headers: {
      "Content-Type": "multipart/form-data",
      Authorization: `Bearer ${accessToken}`,
    },
  };
  const response = await axios.put(MIDDLEWARE_URL + "/profile", formData, config);
  return response.data;
};

const getFollowSuggestions = () => {
  return axios.get(MIDDLEWARE_URL + "/followSuggestions", { headers: authHeader() });
};

const unFollowUser = async (userName) => {
  const response = await axios.delete(MIDDLEWARE_URL + "/followers/" + userName, {
    headers: authHeader(),
  });
  return response.data;
};

const followUser = (username) => {
  return axios.post(
    MIDDLEWARE_URL + "/followers",
    { username },
    { headers: authHeader() }
  );
};

export {
  getUserProfile,
  updateUserProfile,
  getFollowSuggestions,
  followUser,
  unFollowUser,
};
