import React, { useEffect, useState } from "react";

import { makeStyles } from "@material-ui/core/styles";
import {
  List,
  ListItem,
  Avatar,
  ListItemText,
  ListItemAvatar,
  Typography,
  Divider,
  Button,
} from "@material-ui/core";

import { getFollowSuggestions, followUser } from "../../services/UserService";

import { snackbarService } from "uno-material-ui";

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
    marginLeft: theme.spacing(1),
    backgroundColor: "rgb(204, 214, 221)",
    borderRadius: "15px",
  },
  btn: {
    border: "1.5px solid rgba(29,161,242,1.00)",
    color: "rgba(29,161,242,1.00)",
    fontWeight: "bold",
    borderRadius: "1.5rem",
    textTransform: "capitalize",
  },
}));

const WhoToFollow = () => {
  const classes = useStyles();

  const [followSuggestions, setFollowSuggestions] = useState(null);

  useEffect(() => {
    getFollowSuggestions().then(
      (response) => {
        setFollowSuggestions(response.data.mappedResults);
      },
      (error) => {
        const errorMessage =
          (error.response &&
            error.response.data &&
            error.response.data.message) ||
          error.message ||
          error.toString();
        console.log(errorMessage);
      }
    );
  }, []);

  const handleFollow = (username) => {
    followUser(username).then(
      (response) => {
        snackbarService.showSnackbar(response.data.message);
        window.location.reload(false);
      },
      (error) => {
        const errorMessage =
          (error.response &&
            error.response.data &&
            error.response.data.message) ||
          error.message ||
          error.toString();
        console.log(errorMessage);
      }
    );
  };

  return (
    <div className={classes.root}>
      <List>
        <ListItem>
          <Typography className={classes.subtitle} variant="h6">
            Who to follow
          </Typography>
        </ListItem>
        <Divider />
        {followSuggestions &&
          followSuggestions.map((user) => {
            return (
              <ListItem key={user.id}>
                <ListItemAvatar>
                  <Avatar
                    src={
                      user.profilePicUrl
                        ? user.profilePicUrl
                        : "/broken-image.jpg"
                    }
                  />
                </ListItemAvatar>
                <ListItemText
                  primary={`${user.firstname} ${user.lastname}`}
                  secondary={`@${user.username}`}
                />
                <Button
                  className={classes.btn}
                  onClick={() => handleFollow(user.username)}
                >
                  Follow
                </Button>
              </ListItem>
            );
          })}
      </List>
    </div>
  );
};

export default WhoToFollow;
