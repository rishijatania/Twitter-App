import axios from "axios";
import authHeader from "./AuthHeader";

const API_URL = "http://127.0.0.1:8081/api/tweet/";

const createTweet = async (content, tags, fileAttached, file) => {
  const formData = new FormData();
  const tweetForm = {
    content,
    tags,
    fileAttached,
  };
  if (fileAttached && file) {
    formData.append("file", file);
  }
  formData.append("tweetForm", JSON.stringify(tweetForm));
  const accessToken = localStorage.getItem("accessToken");
  const config = {
    headers: {
      "Content-Type": "multipart/form-data",
      Authorization: `Bearer ${accessToken}`,
    },
  };
  const response = await axios.post(API_URL, formData, config);
  return response.data;
};

const getUserTweets = () => {
  return axios.get(API_URL, { headers: authHeader() });
};

const getUserFeed = () => {
  return axios.get(API_URL + "feed", { headers: authHeader() });
};

const likeTweet = async (tweetId) => {
  const response = await axios.post(
    API_URL + tweetId + "/like",
    {},
    { headers: authHeader() }
  );
  return response.data;
};

const deleteTweet = async (tweetId) => {
  const response = await axios.delete(API_URL + tweetId, {
    headers: authHeader(),
  });
  return response.data;
};

const searchTweets = (searchTerm) => {
  return axios.get(API_URL + "search?text=" + searchTerm, {
    headers: authHeader(),
  });
};

const searchTweetsByTag = (searchTerm) => {
  return axios.get(API_URL + "searchTag?tag=%23" + searchTerm, {
    headers: authHeader(),
  });
};

const getTweetById = (tweetId) => {
  return axios.get(API_URL + tweetId, { headers: authHeader() });
};

const addComment = async (tweetId, comment) => {
  const response = await axios.post(
    API_URL + tweetId + "/comment",
    {
      text: comment,
    },
    { headers: authHeader() }
  );
  return response.data;
};

const deleteComment = async (tweetId, commentId) => {
  const response = await axios.delete(
    API_URL + tweetId + "/comment/" + commentId,
    {
      headers: authHeader(),
    }
  );
  return response.data;
};

export default {
  createTweet,
  getUserTweets,
  getUserFeed,
  likeTweet,
  deleteTweet,
  searchTweets,
  searchTweetsByTag,
  getTweetById,
  addComment,
  deleteComment,
};
