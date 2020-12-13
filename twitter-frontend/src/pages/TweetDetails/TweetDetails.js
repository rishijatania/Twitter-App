import React, { useEffect, useState } from "react";
import { useHistory } from "react-router-dom";
import Card from "@material-ui/core/Card";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import CardMedia from "@material-ui/core/CardMedia";
import Avatar from "@material-ui/core/Avatar";
import Typography from "@material-ui/core/Typography";
import {
  CardActions,
  List,
  ListItem,
  ListItemAvatar,
  ListItemText,
} from "@material-ui/core";
import { FavoriteBorderOutlined } from "@material-ui/icons";
import IconButton from "@material-ui/core/IconButton";
import Badge from "@material-ui/core/Badge";
import DeleteOutlineOutlinedIcon from "@material-ui/icons/DeleteOutlineOutlined";
import { makeStyles, withStyles } from "@material-ui/core/styles";
import {
  likeTweet,
  deleteTweet,
  deleteComment,
  getTweetById,
  addComment,
} from "../../services/TweetService";
import { snackbarService } from "uno-material-ui";
import { Paper, Button, TextareaAutosize } from "@material-ui/core";

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
  addComment: {
    display: "flex",
    flexDirection: "column",
  },
  paper: {
    width: "100%",
    padding: "1rem 1rem",
    marginTop: "1rem",
    display: "flex",
  },
  textField: {
    paddingLeft: "1rem",
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
  addCommentAction: {
    display: "flex",
    alignItems: "center",
  },
  listPrimaryText: {
    fontWeight: "bold",
  },
  commentPaper: {
    width: "100%",
    padding: "1rem 1rem",
    marginTop: "1rem",
  },
}));

const TweetDetails = ({ match, location }) => {
  const [tweetDetails, setTweetDetails] = useState(null);
  const [comment, setComment] = useState("");
  const [loading, setLoading] = useState(false);
  const classes = useStyles();
  const history = useHistory();

  const handleLike = () => {
    likeTweet(tweetDetails.id).then(
      (response) => {
        window.location.reload(false);
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
    deleteTweet(tweetDetails.id).then(
      (response) => {
        snackbarService.showSnackbar("Tweet has been deleted");
        history.push("/u/");
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

  const handleDeleteComment = (commentId) => {
    deleteComment(tweetDetails.id, commentId).then(
      (response) => {
        snackbarService.showSnackbar("Comment has been deleted");
        history.push("/u/");
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

  const avatarUrl =
    tweetDetails && tweetDetails.user && tweetDetails.user.profilePicUrl
      ? tweetDetails.user.profilePicUrl
      : "/broken-image.jpg";

  useEffect(() => {
    getTweetById(match.params.tweetId).then((response) => {
      setTweetDetails(response.data);
    });
  }, [match.params.tweetId]);

  const handleAddComment = (e) => {
    e.preventDefault();
    if (!comment) {
      return snackbarService.showSnackbar("Reply something!", "error");
    }
    setLoading(true);
    addComment(tweetDetails.id, comment).then(
      (response) => {
        snackbarService.showSnackbar("Comment added to the tweet!");
        setLoading(false);
        window.location.reload(false);
      },
      (error) => {
        const errorMessage =
          (error.response &&
            error.response.data &&
            error.response.data.message) ||
          error.message ||
          error.toString();
        setLoading(false);
        snackbarService.showSnackbar(errorMessage, "error");
      }
    );
  };

  return (
    tweetDetails && (
      <>
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
              title={`${tweetDetails.user.firstname} ${tweetDetails.user.lastname} @${tweetDetails.user.username}`}
              classes={{ title: classes.listPrimaryText }}
            />
          </div>
          <CardContent className={classes.typography}>
            <Typography variant="body2" color="textSecondary" component="p">
              {tweetDetails.content}
            </Typography>
          </CardContent>
          {tweetDetails.file && (
            <CardMedia
              className={classes.media}
              image={tweetDetails.file.url}
              title={tweetDetails.file.fileName}
            />
          )}
          <CardActions className={classes.cardActions}>
            <IconButton aria-label="Like" onClick={handleLike}>
              <StyledBadge
                className="like"
                badgeContent={tweetDetails.likesCount}
              >
                <FavoriteBorderOutlined />
              </StyledBadge>
            </IconButton>
            {tweetDetails && (
              <IconButton aria-label="Like" onClick={handleDelete}>
                <StyledBadge className="like">
                  <DeleteOutlineOutlinedIcon />
                </StyledBadge>
              </IconButton>
            )}
          </CardActions>
        </Card>
        <Paper className={classes.paper}>
          <Avatar src={avatarUrl} />
          <form onSubmit={handleAddComment}>
            <div className="addComment">
              <TextareaAutosize
                cols="48"
                placeholder="Tweet your reply"
                type="text"
                value={comment}
                onChange={(e) => setComment(e.target.value)}
                className={classes.textField}
              />
              <div className="addCommentAction">
                <Button
                  size="small"
                  variant="outlined"
                  type="submit"
                  disabled={loading}
                >
                  Reply
                </Button>
              </div>
            </div>
          </form>
        </Paper>
        <Paper className={classes.commentPaper}>
          <List>
            {tweetDetails.comments &&
              tweetDetails.comments.map((tweetComment) => {
                return (
                  <ListItem key={tweetComment.id}>
                    <ListItemAvatar>
                      <Avatar
                        src={
                          tweetComment.user.profilePicUrl
                            ? tweetComment.user.profilePicUrl
                            : "/broken-image.jpg"
                        }
                      />
                    </ListItemAvatar>
                    <ListItemText
                      classes={{ primary: classes.listPrimaryText }}
                      primary={`${tweetComment.user.firstname} ${tweetComment.user.lastname} (@${tweetComment.user.username})`}
                      secondary={tweetComment.text}
                    />
                    <IconButton
                      aria-label="deleteComment"
                      onClick={() => handleDeleteComment(tweetComment.id)}
                    >
                      <DeleteOutlineOutlinedIcon />
                    </IconButton>
                  </ListItem>
                );
              })}
          </List>
        </Paper>
      </>
    )
  );
};

export default TweetDetails;
