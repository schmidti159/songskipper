import { Box, Button, Card, CardActions, CardContent, Divider, Grid, IconButton, Typography } from "@mui/material";
import DeleteIcon from '@mui/icons-material/Delete';
import SaveIcon from '@mui/icons-material/Save';
import EditIcon from '@mui/icons-material/Edit';
import { Rule } from "../../common/types";
import RuleCondition from "./RuleCondition";
import AddCircleIcon from '@mui/icons-material/AddCircle';
import { useState } from "react";
import { api } from "../../api/api";


export type ConditionType = 'track' | 'artist' | 'album'

interface Condition {
  type: ConditionType
  initialExpression?: string
  expression?: string
}

function conditionChanged(condition: Condition) {
  return condition.initialExpression !== condition.expression
}
function toRule(conditions: Condition[], rule: Rule): Rule {
  return {
    id: rule.id,
    title: rule.title,
    titleExpression: conditions.filter(c => c.type === 'track')[0].expression,
    artistExpression: conditions.filter(c => c.type === 'artist')[0].expression,
    albumExpression: conditions.filter(c => c.type === 'album')[0].expression
  }
}
function initialConditions(rule: Rule): Condition[] {
  return [
    {
      type: 'track',
      initialExpression: rule.titleExpression,
      expression: rule.titleExpression
    }, {
      type: 'artist',
      initialExpression: rule.artistExpression,
      expression: rule.artistExpression
    }, {
      type: 'album',
      initialExpression: rule.albumExpression,
      expression: rule.albumExpression
    }
  ]
}
function mergeCondition(conditions: Condition[], type: ConditionType, newExpression: string) {
  const indexToUpdate = conditions.findIndex(c => c.type === type)
  conditions[indexToUpdate].expression = newExpression
  return conditions
}

interface RuleCardProps {
  rule: Rule
}

export default function RuleCard(props: RuleCardProps) {
  const rule = props.rule
  const [conditions, setConditions] = useState(initialConditions(rule))
  const [deleteRule] = api.useDeleteRuleByIdMutation()
  const [updateRule] = api.useUpdatedRuleMutation()

  return (
    <Grid item xs={12} md={6} lg={4}>
      <Card>
        <CardContent>
          <Box sx={{ display: 'flex' }}>
            <Typography variant="h6" component="p">{rule.title}</Typography>
            {
              conditions.some(condition => condition.expression == null) &&
              <>
                <IconButton aria-label="edit title" sx={{ marginLeft: 'auto' }}>
                  <EditIcon />
                </IconButton>
                <IconButton aria-label="add condition">
                  <AddCircleIcon />
                </IconButton>
              </>
            }
          </Box>
          {
            conditions
              .filter(condition => condition.expression != null)
              .map(condition => [
                <RuleCondition key={condition.type}
                  type={condition.type}
                  expression={condition.expression}
                  onChange={(expression) => setConditions(mergeCondition(conditions, condition.type, expression))} />])
              .reduce((prev, curr) => prev.length <= 1 ? curr : [...prev, <Divider key={curr[0].key + "_divider"} />, ...curr], [])
          }
        </CardContent>
        <CardActions>
          <Button startIcon={<DeleteIcon />} variant="text" sx={{ marginLeft: 'auto' }}
            onClick={() => deleteRule(rule.id)}>Delete</Button>
          <Button startIcon={<SaveIcon />} variant="contained"
            disabled={conditions.every(c => !conditionChanged(c))}
            onClick={() => updateRule(toRule(conditions, rule))}>Save</Button>
        </CardActions>
      </Card>
    </Grid>
  )
}

