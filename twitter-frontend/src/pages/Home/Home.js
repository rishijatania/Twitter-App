import React from "react";
import NewTweet from "../../components/NewTweet";
import TweetList from "../../components/TweetList";

import { getUserFeed } from "../../services/TweetService";

const Home = (props) => {
  return (
    <div>
      <NewTweet user={props.user} />
      <TweetList tweetApi={getUserFeed} selfTweets={false} />
    </div>
  );
};

export default Home;
