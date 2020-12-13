import React, { useState } from "react";
import SearchBar from "material-ui-search-bar";
import { snackbarService } from "uno-material-ui";
import { makeStyles } from "@material-ui/core/styles";
import Grid from "@material-ui/core/Grid";
import AppBar from "@material-ui/core/AppBar";
import Tabs from "@material-ui/core/Tabs";
import Tab from "@material-ui/core/Tab";
import { searchTweets, searchTweetsByTag } from "../../services/TweetService";
import TweetList from "../../components/TweetList";

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
    position: "static",
    background: "white",
    color: "gray",
    marginTop: "1rem",
  },
  tab: {
    fontWeight: 800,
    "&:active": {
      backgroundColor: "rgba(29,161,242,1.00)",
      color: "white",
    },
    "&:focus": {
      color: "rgba(29,161,242,1.00)",
    },
  },
}));

const Explore = () => {
  const classes = useStyles();
  const [searchValue, setSearchValue] = useState("");
  const [value, setValue] = useState(0);
  const [tab, setTab] = useState("tweets");

  const [tweetSearchResult, setTweetSearchResult] = useState("");
  const [tagSearchResult, setTagSearchResult] = useState("");

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  const handleSearch = () => {
    if (!searchValue) {
      return snackbarService.showSnackbar("Enter something to search", "error");
    }
    searchTweets(searchValue).then((response) => {
      setTweetSearchResult(response.data.tweets);
    });
    searchTweetsByTag(searchValue).then((response) => {
      setTagSearchResult(response.data.tweets);
    });
  };

  return (
    <div>
      <Grid container>
        <Grid item xs={12}>
          <SearchBar
            value={searchValue}
            onChange={(newValue) => setSearchValue(newValue)}
            onRequestSearch={() => handleSearch()}
          />
          <AppBar elevation={1} component="nav" className={classes.root}>
            <Tabs
              variant="fullWidth"
              value={value}
              indicatorColor="primary"
              onChange={handleChange}
              centered
            >
              <Tab
                label="Tweets"
                onClick={() => setTab("tweets")}
                className={classes.tab}
              />
              <Tab
                label="Tags"
                onClick={() => setTab("tags")}
                className={classes.tab}
              />
            </Tabs>
          </AppBar>
        </Grid>
        <Grid item xs={12}>
          {tab === "tweets" && (
            <TweetList
              tweetData={tweetSearchResult}
              selfTweets={false}
              onTweetChange={handleSearch}
            />
          )}
          {tab === "tags" && (
            <TweetList
              tweetData={tagSearchResult}
              selfTweets={false}
              onTweetChange={handleSearch}
            />
          )}
        </Grid>
      </Grid>
    </div>
  );
};

export default Explore;
