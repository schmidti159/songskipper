import './App.css';
import PageFrame from '../features/frame/PageFrame';
import CurrentlyPlayingPage from '../features/currentlyPlaying/CurrentlyPlayingPage';
import PlayLog from '../features/playlog/PlayLogPage';
import SkipRules from '../features/skipRules/SkipRulesPage';
import PlayArrowIcon from '@mui/icons-material/PlayArrow';
import RuleIcon from '@mui/icons-material/Rule';
import HistoryIcon from '@mui/icons-material/History';
import { Route, Switch } from 'react-router-dom';
import { ThemeProvider } from '@mui/material';
import { theme } from './theme';

function App() {
  return (
    <ThemeProvider theme={theme}>
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
          title: "PlayLog",
          icon: <HistoryIcon />
        }
      ]}>
        <Switch>
          <Route path="/rules">
            <SkipRules />
          </Route>
          <Route path="/log">
            <PlayLog />
          </Route>
          <Route path="/">
            <CurrentlyPlayingPage />
          </Route>
        </Switch>
      </PageFrame>
    </ThemeProvider>
  );
}

export default App;