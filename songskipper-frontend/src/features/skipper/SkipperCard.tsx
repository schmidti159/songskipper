import { Card, CardContent, Switch, Typography } from "@mui/material";
import { useEffect } from "react";
import { useAppDispatch, useAppSelector } from "../../app/hooks";
import { AppDispatch } from "../../app/store";
import { selectSkipperActive, skipperActive } from "./skipperSlice";

async function toggleSkipper(active: boolean, dispatch: AppDispatch) {
  const uri = active ? "/api/skipper/v1/stop" : "/api/skipper/v1/start"
  const response = await fetch(uri)
  if(response.ok) {
    dispatch(skipperActive(!active))
  } else {
    console.log("Could not fetch from "+uri+": "+response.status+" "+response.statusText)
  }
}

function updateStateFromServer(dispatch: AppDispatch) {
  (async () => {
    const uri = "api/skipper/v1/active"
    const response = await fetch(uri)
    if(response.ok) {
      const text = await response.text()
      dispatch(skipperActive(text === 'true'))
    } else {
      console.log("Could not fetch from "+uri+": "+response.status+" "+response.statusText)
    }
  })()
}

export default function SkipperCard() {
  const active = useAppSelector(selectSkipperActive);
  const dispatch = useAppDispatch();
  useEffect(() => updateStateFromServer(dispatch), [dispatch])
  return (
    <Card>
      <CardContent>
        <Typography variant="caption" component="p">Skipper Enabled</Typography>
        <Switch checked={active} onClick={() => toggleSkipper(active, dispatch)}/>
      </CardContent>
    </Card>
  )
}