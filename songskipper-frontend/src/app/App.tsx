import HistoryIcon from '@mui/icons-material/History';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import RuleIcon from '@mui/icons-material/Rule';
import { Route, Routes } from 'react-router-dom';
import CurrentlyPlayingPage from '../features/currentlyPlaying/CurrentlyPlayingPage';
import PageFrame from '../features/frame/PageFrame';
import PlayLog from '../features/playlog/PlayLogPage';
import SkipRules from '../features/skipRules/SkipRulesPage';

function App() {
  return (
    <PageFrame links={[
      {
        path: "/",
        title: "Currently Playing",
        icon: <PlayArrowIcon />
      },
      {
        path: "/rules",
        title: "Skip Rules",
        icon: <RuleIcon />
      },
      {
        path: "/log",
        title: "Playlog",
        icon: <HistoryIcon />
      }
    ]}>
      <Routes>
        <Route path="/rules">
          <SkipRules />
        </Route>
        <Route path="/log">
          <PlayLog />
        </Route>
        <Route path="/">
          <CurrentlyPlayingPage />
        </Route>
      </Routes>
    </PageFrame>
  );
}

export default App;