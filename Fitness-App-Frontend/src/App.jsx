
import {Button} from "@mui/material";
import {useContext} from "react";
import { AuthContext } from "react-oauth2-code-pkce";
import { BrowserRouter as Router, Navigate, Route, Routes, useLocation } from "react-router-dom";
import { useDispatch } from "react-redux";
import { useEffect, useState } from "react";
import { setCredentials } from "./store/authSlice";
import { useSelector } from "react-redux";
import { selectCurrentToken, selectCurrentUser } from "./store/authSlice";
import { selectCurrentUserId } from "./store/authSlice";
import ActivityForm from "./components/ActivityForm";
import ActivityList from "./components/ActivityList";
import Box from "@mui/material/Box";
import ActivityDetail from "./components/ActivityDetail";
const ActivitiesPage=()=>{
    return  (<Box component="section" sx={{ p: 2, border: '1px dashed grey' }}>
      <ActivityForm  onActivitiesAdded={()=> window.location.reload()}/>
      <ActivityList/>
      </Box>);
}
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
          // <div>
          //   <pre>{JSON.stringify(tokenData, null, 2)}</pre>
          //    <pre>{JSON.stringify(token, null, 2)}</pre>
          // </div>
  <Box component="section" sx={{ p: 2, border: '1px dashed grey' }}>
      <Routes>
          <Route path="/activities" element={<ActivitiesPage/>}/>
          <Route path="/activities/:id" element={<ActivityDetail/>}/>
          <Route path="/" element={token?<Navigate to ="/activities" replace/>:<div>Please log in</div>}/>
        </Routes>
    </Box>

        )}
    </Router>
  )
}


export default App
