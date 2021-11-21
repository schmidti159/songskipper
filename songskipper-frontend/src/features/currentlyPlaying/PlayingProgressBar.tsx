import { Box, LinearProgress, Typography } from "@mui/material";
import React from "react";

interface PlayingProgressBarProps {
  isPaused?: boolean,
  progressMs: number,
  durationMs: number;
}

function formatTime(milliseconds: number) {
  const minutes = Math.floor(milliseconds / 1000 / 60);
  const seconds = Math.floor((milliseconds / 1000) - minutes * 60);
  const secondsString = (seconds < 10) ? "0" + seconds : seconds.toString();
  return minutes + ":" + secondsString;
}

export default function PlayingProgressBar(props: PlayingProgressBarProps) {

  const [progressMs, setProgressMs] = React.useState(0);
  React.useEffect(() => setProgressMs(props.progressMs), [props.progressMs]);
  React.useEffect(() => {
    const timer = setInterval(() => {
      setProgressMs((oldProgressMs) => {
        return props.isPaused ? oldProgressMs : oldProgressMs + 250;
      });
    }, 250);

    return () => {
      clearInterval(timer);
    };
  }, [props.isPaused]);
  return (
    <Box sx={{ display: 'flex', alignItems: 'center' }}>
      <Box sx={{ flexGrow: 1, mr: 1 }}>
        <LinearProgress variant="determinate" value={100 * progressMs / props.durationMs} />
      </Box>
      <Box sx={{ minWidth: 35 }}>
        <Typography variant="body2" color="text.secondary">{formatTime(progressMs)}/{formatTime(props.durationMs)}</Typography>
      </Box>
    </Box>
  );
}