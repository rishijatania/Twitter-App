import React, { useState, useEffect } from "react";
import Paper from "@material-ui/core/Paper";
import { Divider } from "@material-ui/core";
import { snackbarService } from "uno-material-ui";
import Tweet from "../components/Tweet";
import { makeStyles } from "@material-ui/core/styles";

const useStyles = makeStyles((theme) => ({
  tweet: {
    marginTop: "3.5rem",
    [theme.breakpoints.up("md")]: {
      marginTop: "2.5rem",
    },
  },
}));

const TweetList = (props) => {
  const [tweets, setTweets] = useState([]);
  const classes = useStyles();

  const refreshTweetsList = () => {
    props.tweetApi().then(
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
    <div className={classes.tweet}>
      {tweets ? (
        tweets.map((tweet) => (
          <Paper square key={tweet.id}>
            <Tweet
              tweet={tweet}
              selfTweets={props.selfTweets}
              onTweetChange={refreshTweetsList}
            />
            <Divider />
          </Paper>
        ))
      ) : (
        <div>No tweets</div>
      )}
    </div>
  );
};

export default TweetList;
