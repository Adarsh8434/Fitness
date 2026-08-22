import React from 'react'
import { useState,useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Card from '@mui/material/Card';
import Typography from '@mui/material/Typography';
import axios from 'axios';
import CardContent from '@mui/material/CardContent';
import Grid  from '@mui/material/Grid';
import { getActivities } from  '../services/api';
const ActivityList=()=>{
    const[activities,setActivities]=useState([]);
    const navigate=useNavigate();
    const fetchActivities=async ()=>{
    try{
           const response = await getActivities();
           
          setActivities(response.data);
    }catch(error) {console.log(error);}
    };
    useEffect(()=>{
        fetchActivities();
    },[]);
return (
    <Grid container spacing={2}>
    {activities.map((activity) => (
        <Grid container spacing = {{xs:2 , md:3}}columns={{xs:4 , md:3}}>
            <Card
                sx={{
                    cursor: 'pointer',
                    padding: 2
                }}
                onClick={() =>
                    navigate(`/activities/${activity.id}`)
                }
            >
               <CardContent> 
                <Typography variant="h6">
                    {activity.type}
                </Typography>

                <Typography>
                    Duration: {activity.duration}
                </Typography>

                <Typography>
                    Calories: {activity.caloriesBurned}
                </Typography>
                </CardContent>
            </Card>
        </Grid>
    ))}
</Grid>
);
};
//     return (
//         <Box
//             container
//             spacing={2}
//             sx={{
//                 display: 'grid',
//                 gridTemplateColumns: {
//                     xs: '1fr',
//                     md: 'repeat(4, 1fr)'
//                 },
//                 gap: 2
//             }}
//         >
//             {activities.map((activity) => (
//                 <Card
//                     key={activity.id}
//                     sx={{
//                         cursor: 'pointer',
//                         padding: 2
//                     }}
//                     onClick={() =>
//                         navigate(`/activities/${activity.id}`)
//                     }
//                 >
//                     <Typography variant="h6">
//                         {activity.type}
//                     </Typography>

//                     <Typography>
//                         Duration: {activity.duration}
//                     </Typography>

//                     <Typography>
//                         Calories: {activity.caloriesBurned}
//                     </Typography>
//                 </Card>
//             ))}
//         </Box>
//     );
// };

export default ActivityList;