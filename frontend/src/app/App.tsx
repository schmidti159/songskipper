import React from 'react';
import './App.css';
import PageFrame from '../features/frame/PageFrame';
import CurrentlyPlayingPage from '../features/currentlyPlaying/CurrentlyPlayingPage';
import PlayLog from '../features/playlog/PlayLogPage';
import SkipRules from '../features/skipRules/SkipRulesPage';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import RuleIcon from '@mui/icons-material/Rule';
import HistoryIcon from '@mui/icons-material/History';
import { useAppDispatch } from './hooks'
import connectWebsocket from '../api/WebSocketSubscriber';


function App() {
  connectWebsocket(useAppDispatch());
  return (
      <PageFrame pages={[
        {
          title: "Currently Playing",
          icon: <PlayArrowIcon />,
          content: <CurrentlyPlayingPage/>
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
 