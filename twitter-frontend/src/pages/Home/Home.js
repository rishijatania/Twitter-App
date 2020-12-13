import React, { useState, useEffect } from "react";
import NewTweet from "../../components/NewTweet";
import TweetList from "../../components/TweetList";

import { getUserFeed } from "../../services/TweetService";
import { snackbarService } from "uno-material-ui";

const Home = (props) => {
  const [tweets, setTweets] = useState([]);

  const refreshTweetsList = () => {
    getUserFeed().then(
      (response) => {
        setTweets(response.data.tweets);
      },
      (error) => {
        const errorMessage =
          (error.response &&
            error.response.data &&
            error.response.data.message) ||
          error.message ||
          error.toString();
        snackbarService.showSnackbar(errorMessage, "error");
      }
    );
  };

  useEffect(() => {
    refreshTweetsList();
  }, []);

  return (
    <div>
      <NewTweet user={props.user} />
      <TweetList
        tweetData={tweets}
        selfTweets={false}
        onTweetChange={refreshTweetsList}
      />
    </div>
  );
};

export default Home;
