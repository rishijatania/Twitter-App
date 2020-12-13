import React, { useEffect, useState } from "react";
import { Switch, Route, Link } from "react-router-dom";

import AppBar from "@material-ui/core/AppBar";
import CssBaseline from "@material-ui/core/CssBaseline";
import Divider from "@material-ui/core/Divider";
import Drawer from "@material-ui/core/Drawer";
import Hidden from "@material-ui/core/Hidden";
import IconButton from "@material-ui/core/IconButton";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import ListItemText from "@material-ui/core/ListItemText";
import Toolbar from "@material-ui/core/Toolbar";
import Typography from "@material-ui/core/Typography";
import SvgIcon from "@material-ui/core/SvgIcon";
import Grid from "@material-ui/core/Grid";
import { makeStyles, useTheme } from "@material-ui/core/styles";

import MenuIcon from "@material-ui/icons/Menu";
import HomeIcon from "@material-ui/icons/Home";
import TwitterIcon from "@material-ui/icons/Twitter";
import ProfileIcon from "@material-ui/icons/PersonOutlineOutlined";

import Home from "../Home/Home";
import Explore from "../Explore/Explore";
import Profile from "../Profile/Profile";
import EditProfile from "../Profile/EditProfile";
import WhoToFollow from "../WhoToFollow/WhoToFollow";
import { getUserProfile } from "../../services/UserService";
import TweetDetails from "../TweetDetails/TweetDetails";

import { snackbarService } from "uno-material-ui";

const drawerWidth = 240;

const useStyles = makeStyles((theme) => ({
  root: {
    display: "flex",
  },
  drawer: {
    [theme.breakpoints.up("sm")]: {
      width: drawerWidth,
      flexShrink: 0,
    },
  },
  appBar: {
    background: "white",
    [theme.breakpoints.up("sm")]: {
      width: `calc(100% - ${drawerWidth}px)`,
      marginLeft: drawerWidth,
    },
  },
  menuButton: {
    marginRight: theme.spacing(2),
    [theme.breakpoints.up("sm")]: {
      display: "none",
    },
  },
  // necessary for content to be below app bar
  toolbar: theme.mixins.toolbar,
  drawerPaper: {
    width: drawerWidth,
  },
  content: {
    flexGrow: 1,
    padding: theme.spacing(1),
    width: "100%",
    height: "100vh",
  },
  listItem: {
    "&:hover, &:active, &.active": {
      backgroundColor: "rgb(206,233,234)",
      color: "rgba(29,161,242,1.00)",
      "& .MuiSvgIcon-root": {
        color: "rgba(29,161,242,1.00)",
      },
      "& .MuiTypography-root": {
        color: "rgba(29,161,242,1.00)",
      },
    },
  },
  link: {
    textDecoration: "none",
    textDecorationColor: "none",
    "& .MuiTypography-root": {
      fontWeight: "bold",
      fontSize: "19px",
      color: "black",
    },
    "&:hover, &:active, &.active": {
      "& .MuiTypography-root": {
        fontWeight: "bold",
        fontSize: "19px",
        color: "rgba(29,161,242,1.00)",
        "&:focus, &.active": {
          color: "rgba(29,161,242,1.00)",
        },
      },
      "& .MuiSvgIcon-root": {
        color: "rgba(29,161,242,1.00)",
      },
    },
  },
  icons: {
    width: "2.5rem",
    height: "1.75rem",
    color: "rgb(20, 23, 26);",
    "&:hover, &:active, &.active": {
      color: "rgba(29,161,242,1.00)",
    },
  },
  twitterIcon: {
    color: "#00acee",
  },
  svg: {
    display: "flex",
    flexBasis: "auto",
    flexDirection: "column",
    flexShrink: 0,
    width: "2.5rem",
    marginLeft: "0.1rem",
    height: "1.75rem",
    position: "relative",
  },
}));

const HashTag = (props) => {
  return (
    <SvgIcon {...props}>
      <path d="M20.585 9.468c.66 0 1.2-.538 1.2-1.2 0-.662-.54-1.2-1.2-1.2h-3.22l.31-3.547c.027-.318-.07-.63-.277-.875-.206-.245-.495-.396-.822-.425-.65-.035-1.235.432-1.293 1.093l-.326 3.754H9.9l.308-3.545c.06-.658-.43-1.242-1.097-1.302-.665-.05-1.235.43-1.293 1.092l-.325 3.754h-3.33c-.663 0-1.2.538-1.2 1.2 0 .662.538 1.2 1.2 1.2h3.122l-.44 5.064H3.416c-.662 0-1.2.54-1.2 1.2s.538 1.202 1.2 1.202h3.22l-.31 3.548c-.057.657.432 1.24 1.09 1.3l.106.005c.626 0 1.14-.472 1.195-1.098l.327-3.753H14.1l-.308 3.544c-.06.658.43 1.242 1.09 1.302l.106.005c.617 0 1.142-.482 1.195-1.098l.325-3.753h3.33c.66 0 1.2-.54 1.2-1.2s-.54-1.202-1.2-1.202h-3.122l.44-5.064h3.43zm-5.838 0l-.44 5.063H9.253l.44-5.062h5.055z"></path>
    </SvgIcon>
  );
};

function Dashboard(props) {
  const { window } = props;
  const classes = useStyles();
  const theme = useTheme();

  const [mobileOpen, setMobileOpen] = useState(false);
  const [value, setValue] = useState("Home");
  const [user, setUser] = useState(null);

  useEffect(() => {
    getUpdatedUserProfile();
  }, []);

  const handleDrawerToggle = () => {
    setMobileOpen(!mobileOpen);
  };

  const getUpdatedUserProfile = () => {
    getUserProfile().then(
      (response) => {
        setUser(response.data);
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

  const drawer = (
    <div>
      <div className={classes.toolbar} />
      <Divider />
      <List>
        <ListItem
          button
          className={classes.listItem}
          component={Link}
          to="/u/home"
          onClick={() => setValue("Home")}
        >
          <ListItemIcon>
            <HomeIcon className={classes.icons} />
          </ListItemIcon>
          <ListItemText primary="Home" />
        </ListItem>

        <ListItem
          button
          className={classes.listItem}
          component={Link}
          to="/u/explore"
          onClick={() => setValue("Explore")}
        >
          <ListItemIcon>
            <HashTag className={classes.icons} />
          </ListItemIcon>
          <ListItemText primary="Explore" />
        </ListItem>

        <ListItem
          button
          className={classes.listItem}
          component={Link}
          to="/u/profile"
          onClick={() => setValue("Profile")}
        >
          <ListItemIcon>
            <ProfileIcon className={classes.icons} />
          </ListItemIcon>
          <ListItemText primary="Profile" />
        </ListItem>
      </List>
    </div>
  );

  const container =
    window !== undefined ? () => window().document.body : undefined;

  return (
    <div className={classes.root}>
      <CssBaseline />
      <AppBar position="fixed" className={classes.appBar}>
        <Toolbar>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            edge="start"
            onClick={handleDrawerToggle}
            className={classes.menuButton}
          >
            <MenuIcon />
          </IconButton>
          <TwitterIcon fontSize="large" className={classes.twitterIcon} />
          <Typography variant="h6" noWrap>
            {value}
          </Typography>
        </Toolbar>
      </AppBar>
      <nav className={classes.drawer} aria-label="mailbox folders">
        {/* The implementation can be swapped with js to avoid SEO duplication of links. */}
        <Hidden smUp implementation="css">
          <Drawer
            container={container}
            variant="temporary"
            anchor={theme.direction === "rtl" ? "right" : "left"}
            open={mobileOpen}
            onClose={handleDrawerToggle}
            classes={{
              paper: classes.drawerPaper,
            }}
            ModalProps={{
              keepMounted: true, // Better open performance on mobile.
            }}
          >
            {drawer}
          </Drawer>
        </Hidden>
        <Hidden xsDown implementation="css">
          <Drawer
            classes={{
              paper: classes.drawerPaper,
            }}
            variant="permanent"
            open
          >
            {drawer}
          </Drawer>
        </Hidden>
      </nav>
      <main className={classes.content}>
        <div className={classes.toolbar} />
        <Grid container spacing={1}>
          <Grid className={classes.content} item md={9}>
            <Switch>
              <Route exact path="/u">
                <Home user={user} />
              </Route>
              <Route path="/u/home">
                <Home user={user} />
              </Route>
              <Route path="/u/explore" component={Explore} />
              <Route path="/u/profile">
                <Profile user={user} />
              </Route>
              <Route path="/u/settings/profile">
                <EditProfile handleUserUpdate={getUpdatedUserProfile} />
              </Route>
              <Route path="/u/tweet/:tweetId" component={TweetDetails} />
            </Switch>
          </Grid>
          <Hidden xsDown>
            <Grid item md={3}>
              <WhoToFollow handleUserUpdate={getUpdatedUserProfile} />
            </Grid>
          </Hidden>
        </Grid>
      </main>
    </div>
  );
}

export default Dashboard;
