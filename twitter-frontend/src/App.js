import React from "react";
import { BrowserRouter, Switch, Route, Redirect } from "react-router-dom";
import { ThemeProvider } from "@material-ui/core";
import { SnackbarContainer } from "uno-material-ui";

import SignUp from "./pages/SignUp/SignUp";
import SignIn from "./pages/SignIn/SignIn";
import Home from "./pages/Home/Home";
import theme from "./theme";

const authGuard = (Component) => () => {
  return localStorage.getItem("accessToken") ? (
    <Component />
  ) : (
    <Redirect to="/signin" />
  );
};

function App() {
  return (
    <BrowserRouter>
      <ThemeProvider theme={theme}>
        <SnackbarContainer />
        <Switch>
          <Route exact path={"/signin"} component={SignIn} />
          <Route exact path="/signup" component={SignUp} />
          <Route path="/home" render={authGuard(Home)}></Route>
          <Route exact path="/">
            <Redirect to="/home" />
          </Route>
        </Switch>
      </ThemeProvider>
    </BrowserRouter>
  );
}

export default App;
