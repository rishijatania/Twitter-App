import React from "react";
import { BrowserRouter, Switch, Route } from "react-router-dom";
import { ThemeProvider } from "@material-ui/core";

import SignUp from "./components/SignUp";
import SignIn from "./components/SignIn";
import theme from "./theme";

function App() {
  return (
    <BrowserRouter>
      <ThemeProvider theme={theme}>
        <Switch>
          <Route exact path={["/", "/signin"]} component={SignIn} />
          <Route exact path="/signup" component={SignUp} />
        </Switch>
      </ThemeProvider>
    </BrowserRouter>
  );
}

export default App;
