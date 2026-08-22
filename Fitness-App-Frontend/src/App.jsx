
import {Button} from "@mui/material";
import {useContext} from "react";
import { AuthContext } from "react-oauth2-code-pkce";
import { BrowserRouter as Router, Navigate, Route, Routes, useLocation } from "react-router-dom";
import { useDispatch } from "react-redux";
import { useEffect, useState } from "react";
import { setCredentials } from "./store/authSlice";
import { useSelector } from "react-redux";
import { selectCurrentToken, selectCurrentUser } from "./store/authSlice";
import ActivityForm from "./components/ActivityForm";
import ActivityList from "./components/ActivityList";
import Box from "@mui/material/Box";
import ActivityDetail from "./components/ActivityDetail";
import Typography from "@mui/material/Typography";
const ActivitiesPage=()=>{
    return  (<Box component="section" sx={{ p: 2, border: '1px dashed grey' }}>
      
      <ActivityForm  onActivityAdded ={()=> window.location.reload()}/>
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
   <Box
    sx={{
        minHeight: '100vh',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        padding: 2
    }}
>
    <Box
        sx={{
            width: '100%',
            maxWidth: 450,
            backgroundColor: 'white',
            borderRadius: 4,
            padding: 5,
            textAlign: 'center',
            boxShadow: 6
        }}
    >
        <Typography
            variant="h3"
            fontWeight="bold"
            gutterBottom
        >
            🏋️ Fitness App
        </Typography>

        <Typography
            variant="h6"
            color="text.secondary"
            sx={{ mb: 2 }}
        >
            Welcome to your fitness journey!
        </Typography>

        <Typography
            variant="body1"
            color="text.secondary"
            sx={{ mb: 4 }}
        >
            Track your activities, monitor your progress,
            and stay motivated to achieve your fitness goals.
        </Typography>

        <Button
            variant="contained"
            size="large"
            fullWidth
            onClick={logIn}
            sx={{
                py: 1.5,
                fontSize: '1rem',
                fontWeight: 'bold',
                borderRadius: 2
            }}
        >
            Login to Access Your Activities
        </Button>

        <Typography
            variant="body2"
            color="text.secondary"
            sx={{ mt: 3 }}
        >
            Please login to access and manage your activities.
        </Typography>
    </Box>
</Box>):(
         
  <Box component="section" sx={{ p: 2, border: '1px dashed grey' }}>
    <Button variant="contained" color="blue" onClick={()=>{
        logOut()
      }}> LOGOUT
        </Button>
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
