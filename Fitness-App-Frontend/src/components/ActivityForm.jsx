import React from 'react'


const ActivityForm = ({onActivityAdded }) => {
    const[activity, setActivity]= useState({type:"RUNNING",duration :'',caloriesBurned:'',
      additionalMetrics:{}
    });
const handleSubmit = async (e)=>{
    e.preventDefauilt();
    try{
     await addActivity(activity);
     onActivityAdded();
     setActivity({type:"RUNNING",duration :'.',caloriesBurned:''})
    }catch(error){
  
    }
}
    return (
          <Box component="form" onSubmit={handleSubmit} sx={{ mb:4 }}>
      This Box renders as an HTML section element.
      <FormControl fullWidht sx={{mb: 2}}>
        <InputLabel>Activity Type</InputLabel>
        <Select
        value={activity.type}
        onChange={(e)=> setActivity({...activity, type: e.target.value})}
        ></Select>
        <MenuItem value="RUNNING">Running</MenuItem>
        <MenuItem value="CYCLING">Cycling</MenuItem>
        <MenuItem value="SWIMMING">Swimming</MenuItem>
        <MenuItem value="WALKING">WALKING</MenuItem>
      </FormControl>
      <TextField fullWidth
                 label="Duration (minutes)"
                 type="number"
                 sx={{ mb: 2 }}
                 value={activity.duration} 
                 onChange={(e)=> setActivity({...activity, duration: e.target.value})} 
      ></TextField>
      <TextField fullWidth
                 label="Calories Burned"
                 type="number"
                 sx={{ mb: 2 }}
                 value={activity.caloriesBurned} 
                 onChange={(e)=> setActivity({...activity, caloriesBurned: e.target.value})} 
      ></TextField>
      <Button type='submit' variant='contained'>  
        Add activity





        
      </Button>
    </Box>
    )
}

export default ActivityForm