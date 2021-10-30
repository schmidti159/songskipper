
import Grid from '@mui/material/Grid';
import { api } from '../../api/api';
import { CircularProgress } from '@mui/material';
import { useSelector } from 'react-redux';
import { selectAllRules } from '../../api/rulesSlice';
import RuleCard from './RuleCard';


export default function SkipRulesPage() {
  const { isLoading } = api.useGetRulesQuery()
  const rules = useSelector(selectAllRules)
  console.log(rules)
  if (isLoading || rules.length === 0) {
    return <CircularProgress />
  } else {
    return (
      <Grid container spacing={3}>
        {rules.map(rule =>
          <RuleCard key={rule.id} rule={rule} />
        )}
      </Grid>
    )
  }
}