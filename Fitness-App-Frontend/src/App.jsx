
import {Button} from "@mui/material";
import {useContext} from "react";
import { AuthContext } from "react-oauth2-code-pkce";
import { BrowserRouter as Router, Navigate, Route, Routes, useLocation } from "react-router-dom";
import { useDispatch } from "react-redux";
import { useEffect, useState } from "react";
import { setCredentials } from "./assets/store/authSlice";
import { useSelector } from "react-redux";
import { selectCurrentToken, selectCurrentUser } from "./assets/store/authSlice";
import { selectCurrentUserId } from "./assets/store/authSlice";

function App() {
  const { token, tokenData, logIn, logOut, isAuthenticated } = useContext(AuthContext);
  const dispatch= useDispatch();
   const [authReady, setAuthReady]= useState(false);
useEffect(()=>{
  if(token){
    dispatch(setCredentials({ token, user: tokenData,}));
    setAuthReady(true);
  }
},[token, tokenData, dispatch]);

  return (
    <Router>
     { !token?(
      <Button variant="contained" color="blue" onClick={()=>{
        logIn()
      }}> LOGIN
        </Button>):(
          <div>
            <pre>{JSON.stringify(tokenData, null, 2)}</pre>
          </div>
        )}
    </Router>
  )
}


export default App
