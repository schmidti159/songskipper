
import Grid from '@mui/material/Grid';
import { api } from '../../api/api';
import { CircularProgress, Fab } from '@mui/material';
import { useSelector } from 'react-redux';
import { selectAllRules } from './rulesSlice';
import AddIcon from '@mui/icons-material/Add';
import RuleCard from './RuleCard';


export default function SkipRulesPage() {
  const { isLoading } = api.useGetRulesQuery()
  const [createRule] = api.useCreateRuleMutation()
  const rules = useSelector(selectAllRules)
  console.log(rules)
  if (isLoading) {
    return <CircularProgress />
  } else {
    return (
      <>
        <Grid container spacing={3}>
          {rules.map(rule =>
            <RuleCard key={rule.id} rule={rule} />
          )}
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
    )
  }
}