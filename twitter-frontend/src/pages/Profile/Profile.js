import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";

import { makeStyles } from "@material-ui/core/styles";
import { Button, Paper, Avatar, Box } from "@material-ui/core";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";

import { Lock } from "@material-ui/icons";

import { getUserTweets } from "../../services/TweetService";
import TweetList from "../../components/TweetList";
import { snackbarService } from "uno-material-ui";

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
    width: "100%",
    top: "0rem",
    marginBottom: "0rem",
    background: "white",
    color: "gray",
  },
  paper: {
    backgroundColor: "rgb(204, 214, 221)",
    height: "12rem",
    top: "0rem",
    marginTop: "0rem",
  },
  horizontalDiv: {
    display: "flex",
    flexDirection: "row",
  },
  btn: {
    border: "1.5px solid rgba(29,161,242,1.00)",
    color: "rgba(29,161,242,1.00)",
    fontWeight: "bold",
    borderRadius: "1.5rem",
    marginTop: "0.7rem",
    marginRight: "1rem",
    textTransform: "capitalize",
  },
  lock: {
    height: "1.5rem",
    color: "black",
  },
  avatarBox: {
    position: "relative",
    bottom: "1rem",
    top: "8rem",
    marginLeft: "0.7rem",
  },
  avatar: {
    border: "4px solid white",
    height: "8rem",
    width: "8rem",
    justifyContent: "center",
  },
  name: {
    color: "black",
    font: "inherit",
    fontWeight: "bold",
    fontSize: "20px",
  },
  followersDiv: {
    display: "flex",
    flexDirection: "row",
    marginBottom: "1rem",
  },
  followers: {
    textDecoration: "none",
    font: "inherit",
    color: "grey",
    fontWeight: "100px",
    fontSize: "15px",
  },
  tweet: {
    flexGrow: 1,
    width: "100%",
  },
}));

const Profile = (props) => {
  const classes = useStyles();

  const [tweets, setTweets] = useState([]);

  const refreshTweetsList = () => {
    getUserTweets().then(
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

  const avatarUrl =
    props.user && props.user.profilePicUrl
      ? props.user.profilePicUrl
      : "/broken-image.jpg";

  return (
    <Grid container>
      <Paper className={classes.root}>
        <Grid item xs={12}>
          <Paper className={classes.paper}>
            {" "}
            <div className={classes.avatarBox}>
              <Box>
                <Avatar className={classes.avatar} src={avatarUrl}></Avatar>
              </Box>
            </div>
          </Paper>
        </Grid>
        <Grid style={{ marginLeft: "1rem" }} item xs={12}>
          <div
            style={{ justifyContent: "space-between" }}
            className={classes.horizontalDiv}
          >
            <div></div>
            <Button
              className={classes.btn}
              component={Link}
              to="/u/settings/profile"
            >
              <span>Edit profile</span>
            </Button>
          </div>
          <div style={{ marginBottom: "1rem" }}>
            <div
              style={{ marginTop: "1rem" }}
              className={classes.horizontalDiv}
            >
              <Typography className={classes.name} variant="h6" id="name">
                {props.user && `${props.user.firstname} ${props.user.lastname}`}
              </Typography>
              <Lock className={classes.lock} />
            </div>
            <span>
              <Typography id="username">
                <small>{props.user && `@${props.user.username}`}</small>
              </Typography>
            </span>
          </div>
          <div style={{ marginBottom: "1rem" }}>
            <span>
              <Typography id="bio">
                {props.user && props.user.bio ? props.user.bio : "Bio"}
              </Typography>
            </span>
          </div>
          <div className={classes.followersDiv}>
            <Typography className={classes.followers}>
              {props.user && `${props.user.followersCount} Followers`}
            </Typography>
            <div style={{ width: "2.5rem" }}></div>
            <Typography className={classes.followers}>
              {props.user && `${props.user.followingCount} Following`}
            </Typography>
          </div>
        </Grid>
      </Paper>
      <div className={classes.tweet}>
        <TweetList
          tweetData={tweets}
          onTweetChange={refreshTweetsList}
          selfTweets={true}
        />
      </div>
    </Grid>
  );
};

export default Profile;
