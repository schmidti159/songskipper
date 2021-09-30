import React from 'react';
import './App.css';
import PageFrame from './components/PageFrame';
import CurrentlyPlaying from './pages/CurrentlyPlaying';
import PlayLog from './pages/PlayLog';
import SkipRules from './pages/SkipRules';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import RuleIcon from '@mui/icons-material/Rule';
import HistoryIcon from '@mui/icons-material/History';



function App() {
  return (
    <PageFrame pages={[
      {
        title: "Currently Playing",
        icon: <PlayArrowIcon />,
        content: <CurrentlyPlaying/>
      },
      {
        title: "Skip Rules",
        icon: <RuleIcon />,
        content: <SkipRules/>
      },
      {
        title: "PlayLog",
        icon: <HistoryIcon />,
        content: <PlayLog/>
      }
    ]}/>
  );
}

export default App;
 