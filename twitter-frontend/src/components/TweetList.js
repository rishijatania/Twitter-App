import React from "react";
import Paper from "@material-ui/core/Paper";
import { Divider } from "@material-ui/core";
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
  const classes = useStyles();

  return (
    <div className={classes.tweet}>
      {props.tweetData ? (
        props.tweetData.map((tweet) => (
          <Paper square key={tweet.id}>
            <Tweet
              tweet={tweet}
              selfTweets={props.selfTweets}
              onTweetChange={props.onTweetChange}
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
