import React, { useState } from "react";
import {
  TextareaAutosize,
  Paper,
  IconButton,
  Avatar,
  Fab,
} from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";

import { Photo } from "@material-ui/icons";

import TweetService from "../services/TweetService";
import { snackbarService } from "uno-material-ui";

const useStyles = makeStyles((theme) => ({
  NewTweet: {
    display: "flex",
    flexDirection: "column",
    flost: "left",
  },
  button: {
    backgroundColor: "rgba(29,161,242,1.00);",
    color: "white",
    textTransform: "capitalize",
    "&:hover": {
      backgroundColor: "rgba(29,161,242,1.00);",
    },
    marginLeft: "1rem",
  },
  btnDiv: {
    width: "4rem",
  },
  avatarDiv: {
    display: "block",
    flexDirection: "column",
    width: "10%",
    marginTop: "0.7rem",
    marginLeft: "0.7rem",
  },
  avatar: {
    width: "4rem",
    height: "4rem",
    float: "left",
    marginRight: "2rem",
  },
  textField: {
    boxSizing: "border-box",
    overflow: "hidden",
    lineHeight: "normal",
    width: "100%",
    fontWeight: 400,
    fontSize: "19px",
    color: "black",
    border: "none",
    backgroundColor: "transparent",
    outline: "none",
    resize: "none",
    marginBottom: "1.4rem",
    "&:focus": {
      outline: "none",
    },
  },
  bottomDiv: {
    display: "flex",
    alignItems: "center",
  },
  input: {
    display: "none",
  },
  icon: {
    color: "rgba(29,161,242,1.00)",
    height: "2rem",
    width: "2rem",
  },
  paper: {
    width: "100%",
    padding: "1rem 1rem",
  },
  tweetImage: {
    marginBottom: "1.5rem",
    marginTop: "0.5rem",
    objectFit: "cover",
    width: "500px",
    height: "285px",
  },
}));

const NewTweet = (props) => {
  const classes = useStyles();
  const [tweet, setTweet] = useState("");
  const [tweetImage, setTweetImage] = useState(null);
  const [tweetImageUrl, setTweetImageUrl] = useState(null);
  const [hasTweetImage, sethasTweetImage] = useState(false);
  const [loading, setLoading] = useState(false);

  const handletweetImageChange = (e) => {
    setTweetImage(e.target.files[0]);
    setTweetImageUrl(URL.createObjectURL(e.target.files[0]));
    sethasTweetImage(true);
  };

  const clearTweetForm = () => {
    setTweet("");
    setTweetImage(null);
    setTweetImageUrl(null);
    sethasTweetImage(false);
  };

  function handleFormSubmit(e) {
    e.preventDefault();
    setLoading(true);

    const tags = tweet.split(" ").filter((str) => str.startsWith("#"));

    TweetService.createTweet(tweet, tags, hasTweetImage, tweetImage).then(
      (response) => {
        setLoading(false);
        snackbarService.showSnackbar("Tweet posted successfully!");
        clearTweetForm();
      },
      (error) => {
        setLoading(false);
        const errorMessage =
          (error.response &&
            error.response.data &&
            error.response.data.message) ||
          error.message ||
          error.toString();
        snackbarService.showSnackbar(errorMessage, "error");
      }
    );
  }

  const avatarUrl =
    props.user && props.user.profilePicUrl
      ? props.user.profilePicUrl
      : "/broken-image.jpg";

  return (
    <Paper square className={classes.paper}>
      <Avatar className={classes.avatar} src={avatarUrl} />
      <form onSubmit={handleFormSubmit}>
        <div className={classes.NewTweet}>
          <TextareaAutosize
            value={tweet}
            onChange={(e) => setTweet(e.target.value)}
            className={classes.textField}
            placeholder="What's happening ?"
          />

          {tweetImage && tweetImageUrl && (
            <img
              className={classes.tweetImage}
              src={tweetImageUrl}
              alt="preview"
            />
          )}

          <div className={classes.bottomDiv}>
            <div>
              <input
                accept="image/*"
                className={classes.input}
                id="contained-button-file"
                multiple
                type="file"
                onChange={handletweetImageChange}
              />
              <label htmlFor="contained-button-file">
                <IconButton aria-label="upload picture" component="span">
                  <Photo className={classes.icon} />
                </IconButton>
              </label>
            </div>
            <Fab
              size="small"
              variant="extended"
              className={classes.button}
              aria-label="add"
              disabled={loading}
              type="submit"
            >
              <div className={classes.btnDiv}>
                <span>Tweet</span>
              </div>
            </Fab>
          </div>
        </div>
      </form>
    </Paper>
  );
};

export default NewTweet;
