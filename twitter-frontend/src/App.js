import React from "react";
import { BrowserRouter, Switch, Route, Redirect } from "react-router-dom";
import { ThemeProvider } from "@material-ui/core";
import { SnackbarContainer } from "uno-material-ui";

import SignUp from "./pages/SignUp/SignUp";
import SignIn from "./pages/SignIn/SignIn";
import Dashboard from "./pages/Dashboard/Dashboard";
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
          <Route path="/u" render={authGuard(Dashboard)}></Route>
          <Route exact path="/">
            <Redirect to="/u/home" />
          </Route>
        </Switch>
      </ThemeProvider>
    </BrowserRouter>
  );
}

export default App;
