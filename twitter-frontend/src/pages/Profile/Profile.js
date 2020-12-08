import React from "react";
import { useHistory, Link } from "react-router-dom";
import { makeStyles } from "@material-ui/core/styles";
import {
  Tab,
  Tabs,
  Button,
  Paper,
  Avatar,
  Box,
  Hidden,
  Divider,
} from "@material-ui/core";
import Grid from "@material-ui/core/Grid";
import Typography from "@material-ui/core/Typography";
import {
  Lock,
  KeyboardBackspace,
  CalendarToday,
  NavigateBefore,
  NavigateNext,
} from "@material-ui/icons";

const useStyles = makeStyles((theme) => ({
  root: {
    flexGrow: 1,
    width: "100%",
    top: "0rem",
    marginBottom: "0rem",
    background: "white",
    color: "gray",
  },
  tab: {
    minWidth: "100px",
    textTransform: "capitalize",
    borderBottomColor: "rgb(29, 161, 242)",
    "&:hover": {
      backgroundColor: "rgb(206,233,234)",
      color: "rgba(29,161,242,1.00)",
    },
    "&:focus": {
      backgroundColor: "rgb(206,233,234)",
      color: "rgba(29,161,242,1.00)",
    },
  },
  tabs: {
    flexGrow: 1,
    display: "flex",
    fontSize: "15px",
    width: "100%",
    "& .MuiTabs-indicator": {
      backgroundColor: "rgba(29,161,242,1.00)",
    },
    [theme.breakpoints.down("xs")]: {
      minWidth: "80px",
    },
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
  div1: {
    display: "flex",
    flexDirection: "row",
    marginTop: "1rem",
  },
  backArrow: {
    color: "rgba(29,161,242,1.00)",
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
  nameTypo: {
    color: "black",
    font: "inherit",
    fontWeight: "bold",
    fontSize: "20px",
  },
  linksDiv: {
    display: "flex",
    flexDirection: "row",
    marginBottom: "1rem",
  },
  links: {
    textDecoration: "none",
    font: "inherit",
    color: "grey",
    fontWeight: "100px",
    fontSize: "15px",
  },
}));

const Profile = (props) => {
  const classes = useStyles();
  const history = useHistory();
  const [value, setValue] = React.useState(0);
  const [tab, setTab] = React.useState("Tweets");
  const [editProfile, setEditProfile] = React.useState(false);

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };
  const handleNextTab = () => {
    let newValue = value;
    if (newValue !== 3) {
      newValue = newValue + 1;
      setValue(newValue);
    }
  };

  const handleBackTab = () => {
    let newValue = value;
    if (newValue !== 0) {
      newValue = newValue - 1;
      setValue(newValue);
    }
  };

  const openProfileEditor = () => {
    setEditProfile(true);
  };

  return (
    <Grid container xs={12}>
      <Grid item xs={12}>
        <Paper className={classes.paper}>
          {" "}
          <div className={classes.avatarBox}>
            <Box>
              <Avatar className={classes.avatar}>Profile Image</Avatar>
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
          <Button onClick={openProfileEditor} className={classes.btn}>
            <span>Edit profile</span>
          </Button>
        </div>
        <div style={{ marginBottom: "1rem" }}>
          <div style={{ marginTop: "1rem" }} className={classes.horizontalDiv}>
            <Typography className={classes.nameTypo} variant="h6" id="name">
              Emi Chukwuka
            </Typography>
            <Lock />
          </div>
          <span>
            <Typography id="username">
              <small>@emi_chukwuka</small>
            </Typography>
          </span>
        </div>
        <div style={{ marginBottom: "1rem" }}>
          <span>
            <Typography id="status">My status</Typography>
          </span>
        </div>
        <div style={{ marginBottom: "1rem" }}>
          <div className={classes.horizontalDiv}>
            <CalendarToday fontSize="small" />
            <div style={{ width: "0.8rem" }}></div>
            <Typography id="date-joined">Date Joined</Typography>
          </div>
        </div>
        <div className={classes.linksDiv}>
          <Link className={classes.links} to="/following">
            Following
          </Link>
          <div style={{ width: "2.5rem" }}></div>
          <Link className={classes.links} to="/followers">
            Followers
          </Link>
        </div>
      </Grid>
    </Grid>
  );
};

export default Profile;
