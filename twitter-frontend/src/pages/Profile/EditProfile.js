import React, { useState, useEffect } from "react";
import { useHistory } from "react-router-dom";

import { makeStyles } from "@material-ui/core/styles";
import {
  Grid,
  Paper,
  TextField,
  Button,
  IconButton,
  Avatar,
  Box,
} from "@material-ui/core";

import { CameraAlt } from "@material-ui/icons";

import { snackbarService } from "uno-material-ui";

import { getUserProfile, updateUserProfile } from "../../services/UserService";

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
    height: "100%",
  },
  form: {
    width: "100%",
    marginTop: "6rem",
  },
  paper: {
    flexGrow: 1,
  },
  avatarBox: {
    position: "relative",
    bottom: "1rem",
    top: "0rem",
    marginLeft: "0.7rem",
  },
  avatar: {
    border: "4px solid white",
    height: "8rem",
    width: "8rem",
    marginTop: "0rem",
  },
  input: {
    display: "none",
  },
  darkArea: {
    backgroundColor: "rgb(204, 214, 221)",
    height: "12rem",
    marginTop: "1rem",
    opacity: "0.75",
  },
  icon: {
    color: "rgba(29,161,242,1.00)",
    height: "2rem",
    width: "2rem",
  },
  btnDiv: {
    textAlign: "center",
  },
  btn: {
    backgroundColor: "rgba(29,161,242,1.00)",
    color: "white",
    fontWeight: "bold",
    marginTop: "0.7rem",
    marginRight: "1rem",
    textTransform: "capitalize",
    "&:hover": {
      backgroundColor: "rgba(29,161,242,1.00)",
    },
    "&:focus": {
      backgroundColor: "rgba(29,161,242,1.00)",
    },
  },
  textField: {
    width: "90%",
    marginLeft: "5%",
    height: "15%",
  },
  camera: {
    marginLeft: "50%",
    marginTop: "4rem",
  },
}));

const EditProfile = () => {
  const classes = useStyles();
  const history = useHistory();

  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [bio, setBio] = useState("");
  const [avatar, setAvatar] = useState(null);
  const [avatarUrl, setAvatarUrl] = useState("/broken-image.jpg");
  const [avatarChanged, setAvatarChanged] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    getUserProfile().then(
      (response) => {
        setFirstName(response.data.firstname);
        setLastName(response.data.lastname);
        setBio(response.data.bio);
        setAvatarUrl(response.data.profilePicUrl);
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
  }, []);

  const handleAvatarChange = (e) => {
    setAvatar(e.target.files[0]);
    setAvatarUrl(URL.createObjectURL(e.target.files[0]));
    setAvatarChanged(true);
  };

  function handleFormSubmit(e) {
    e.preventDefault();
    setLoading(true);

    updateUserProfile(firstName, lastName, bio, avatarChanged, avatar).then(
      (response) => {
        setLoading(false);
        snackbarService.showSnackbar("Profile updated successfully!");
        history.push("/u/profile");
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

  return (
    <Grid container className={classes.root}>
      <Paper className={classes.paper}>
        <form onSubmit={handleFormSubmit}>
          <Grid className={classes.darkArea} item>
            <label>
              <IconButton
                className={classes.camera}
                aria-label="upload picture"
                component="span"
              >
                <CameraAlt className={classes.icon} />
              </IconButton>
            </label>
            <div className={classes.avatarBox}>
              <Box>
                <label htmlFor="contained-button-file">
                  <Avatar className={classes.avatar} src={avatarUrl}>
                    <IconButton aria-label="upload picture" component="span">
                      <CameraAlt className={classes.icon} />
                    </IconButton>
                  </Avatar>
                </label>
                <input
                  accept="image/*"
                  className={classes.input}
                  id="contained-button-file"
                  multiple
                  type="file"
                  onChange={handleAvatarChange}
                />
              </Box>
            </div>
          </Grid>
          <Grid className={classes.form} container spacing={2}>
            <Grid item xs={12}>
              <TextField
                autoFocus
                autoComplete="firstname"
                name="firstname"
                variant="filled"
                value={firstName}
                required
                className={classes.textField}
                id="firstname"
                label="First Name"
                onChange={(e) => setFirstName(e.target.value)}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                variant="filled"
                required
                className={classes.textField}
                id="lastname"
                label="Last Name"
                name="lastname"
                autoComplete="lastname"
                value={lastName}
                onChange={(e) => setLastName(e.target.value)}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                className={classes.textField}
                name="bio"
                variant="filled"
                multiline
                id="bio"
                label="Bio"
                autoComplete="bio"
                value={bio}
                onChange={(e) => setBio(e.target.value)}
              />
            </Grid>
          </Grid>
          <div className={classes.btnDiv}>
            <Button
              type="submit"
              variant="contained"
              color="primary"
              className={classes.btn}
              disabled={loading}
            >
              Save
            </Button>
          </div>
        </form>
      </Paper>
    </Grid>
  );
};

export default EditProfile;
