import React from "react";
import { Link } from "react-router-dom";
import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import CardMedia from "@material-ui/core/CardMedia";
import Avatar from "@material-ui/core/Avatar";
import Typography from "@material-ui/core/Typography";
import CardActions from "@material-ui/core/CardActions";
import {
  ChatBubbleOutlineOutlined,
  FavoriteBorderOutlined,
} from "@material-ui/icons";
import IconButton from "@material-ui/core/IconButton";
import Badge from "@material-ui/core/Badge";
import DeleteOutlineOutlinedIcon from "@material-ui/icons/DeleteOutlineOutlined";
import { makeStyles, withStyles } from "@material-ui/core/styles";
import { likeTweet, deleteTweet } from "../services/TweetService";
import { snackbarService } from "uno-material-ui";

const StyledBadge = withStyles((theme) => ({
  badge: {
    right: -7,
    top: 13,
    border: `2px solid ${theme.palette.background.paper}`,
    padding: "0 4px",
    color: "red",
  },
}))(Badge);

const useStyles = makeStyles((theme) => ({
  root: {
    maxWidth: "relative",
  },
  cardheader: {
    flexShrink: 1,
    display: "flex",
    flexDirection: "row",
  },
  media: {
    marginBottom: "1.5rem",
    marginTop: "0.5rem",
    marginLeft: "1rem",
    objectFit: "cover",
    width: "500px",
    height: "285px",
  },
  listPrimaryText: {
    fontWeight: "bold",
  },
}));

const Tweet = (props) => {
  const classes = useStyles();

  const avatarUrl =
    props.tweet.user && props.tweet.user.profilePicUrl
      ? props.tweet.user.profilePicUrl
      : "/broken-image.jpg";

  const handleLike = () => {
    likeTweet(props.tweet.id).then(
      (response) => {
        props.onTweetChange();
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

  const handleDelete = () => {
    deleteTweet(props.tweet.id).then(
      (response) => {
        snackbarService.showSnackbar(response.message);
        props.onTweetChange();
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

  return (
    <Card className={classes.root}>
      <div className={classes.cardheader}>
        <CardHeader
          avatar={
            <Avatar
              aria-label="user-avatar"
              src={avatarUrl}
              className={classes.avatar}
            ></Avatar>
          }
          title={`${props.tweet.user.firstname} ${props.tweet.user.lastname} (@${props.tweet.user.username})`}
          classes={{ title: classes.listPrimaryText }}
        />
      </div>
      <CardContent className={classes.typography}>
        <Typography variant="body2" color="textSecondary" component="p">
          {props.tweet.content}
        </Typography>
      </CardContent>
      {props.tweet.file && (
        <CardMedia
          className={classes.media}
          image={props.tweet.file.url}
          title={props.tweet.file.fileName}
        />
      )}
      <CardActions className={classes.cardActions}>
        <IconButton
          aria-label="Comment"
          component={Link}
          to={`/u/tweet/${props.tweet.id}`}
        >
          <StyledBadge
            className="chat"
            badgeContent={props.tweet.commentsCount}
          >
            <ChatBubbleOutlineOutlined />
          </StyledBadge>
        </IconButton>
        <IconButton aria-label="Like" onClick={handleLike}>
          <StyledBadge className="like" badgeContent={props.tweet.likesCount}>
            <FavoriteBorderOutlined />
          </StyledBadge>
        </IconButton>
        {props.selfTweets && (
          <IconButton aria-label="Like" onClick={handleDelete}>
            <StyledBadge className="like">
              <DeleteOutlineOutlinedIcon />
            </StyledBadge>
          </IconButton>
        )}
      </CardActions>
    </Card>
  );
};

export default Tweet;
