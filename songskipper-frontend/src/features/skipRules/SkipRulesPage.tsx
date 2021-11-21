
import AddIcon from '@mui/icons-material/Add';
import { CircularProgress, Fab } from '@mui/material';
import Grid from '@mui/material/Grid';
import { useSelector } from 'react-redux';
import { rulesApi } from '../../api/rulesApi';
import RuleCard from './RuleCard';
import { selectAllRules } from './rulesSlice';


export default function SkipRulesPage() {
  const { isLoading } = rulesApi.useGetRulesQuery();
  const [createRule] = rulesApi.useCreateRuleMutation();
  const rules = useSelector(selectAllRules);

  if (isLoading) {
    return <CircularProgress />;
  } else {
    return (
      <>
        <Grid container spacing={3}>
          {rules.map(rule => (
            <Grid item xs={12} md={6} lg={4} key={rule.id}>
              <RuleCard rule={rule} />
            </Grid>
          ))}
        </Grid>

        <Fab color="primary" aria-label="add rule"
          onClick={() => createRule({
            id: 'new-id',
            title: 'Rule'
          })}
          sx={{
            position: "fixed",
            bottom: (theme) => theme.spacing(2),
            right: (theme) => theme.spacing(2)
          }}>
          <AddIcon />
        </Fab>
      </>
    );
  }
}