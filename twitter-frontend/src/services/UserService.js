import axios from "axios";
import authHeader from "./AuthHeader";

const API_URL = "http://127.0.0.1:8080/api/user/";

const getUserProfile = () => {
  return axios.get(API_URL + "profile", { headers: authHeader() });
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
  const response = await axios.put(API_URL + "profile", formData, config);
  return response.data;
};

const getFollowSuggestions = () => {
  return axios.get(API_URL + "followSuggestions", { headers: authHeader() });
};

const followUser = (username) => {
  return axios.post(
    API_URL + "followers",
    { username },
    { headers: authHeader() }
  );
};

export { getUserProfile, updateUserProfile, getFollowSuggestions, followUser };
