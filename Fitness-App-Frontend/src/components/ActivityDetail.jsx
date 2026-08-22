import { Divider, Typography } from '@mui/material';
import React from 'react'
import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { getActivityDetails } from '../services/api';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Box from '@mui/material/Box';
const ActivityDetail =() =>{
     const {id}= useParams();
       const [activity, setActivity] = useState(null);
       const[recommendation,setRecommendation] = useState(null);

       useEffect(() => {
         const fetchActivityDetails = async () => {
           try {
             const response = await getActivityDetails(id);
             setActivity(response.data);
             setRecommendation(response.data.recommendation);
           } catch (error) {
             console.error('Error fetching activity details:', error);
           }
       }
       fetchActivityDetails();
    },[id]);
   if(!activity){
    return <Typography> Loading...</Typography>
   }
    return(
      <Box sx= {{maxWidth: 800, mx: 'auto', p:2}}>
        <Card>
          <CardContent>
            <Typography variant="h5" gutterBottom> Activity Details</Typography>
            <Typography>Type: {activity.type}</Typography>
            <Typography>Duration: {activity.duration}</Typography>
            <Typography>Calories Burned: {activity.caloriesBurned}</Typography>
            <Typography>Date:{new Date(activity.createdAt).toLocaleDateString()}</Typography> 
            
          </CardContent>
        </Card>
        {recommendation && (
          <Card sx={{ mt: 2 }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                AI Recommendation
              </Typography>
               <Typography variant="h5">
                Analysis</Typography>
                <Typography
                paragraph> {activity.recommendation} </Typography>
                <Divider sx={{my:2}}/>

              <Typography variant="h6">Improvements</Typography>
              {
                activity?.improvements?.map((improvement, index) => (
                  <Typography key={index} paragraph> {activity.improvements }   </Typography>
                ))
              }
              <Divider sx={{my:2}}/>
              <Typography variant="h6">Suggestions</Typography>
              {activity?.suggestions?.map((suggestion, index) => (
                <Typography key={index} paragraph>
                  {suggestion}
                </Typography>
              ))}
              <Divider sx={{my:2}}/>
              <Typography variant="h6">Safety Guidelines</Typography>
              {activity?.safety?.map((safety, index) => (
                <Typography key={index} paragraph> {safety} </Typography>
              ))}
            </CardContent>
          </Card>
        )}
      </Box>
    )
}
export default ActivityDetail;