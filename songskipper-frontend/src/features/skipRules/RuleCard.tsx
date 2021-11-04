import { Box, Button, Card, CardActions, CardContent, Divider, Grid, IconButton } from "@mui/material";
import DeleteIcon from '@mui/icons-material/Delete';
import SaveIcon from '@mui/icons-material/Save';
import { Rule } from "../../common/types";
import RuleCondition from "./RuleCondition";
import AddCircleIcon from '@mui/icons-material/AddCircle';
import { useState } from "react";
import { api } from "../../api/api";
import RuleTitle from "./RuleTitle";


export type ConditionType = 'track' | 'artist' | 'album'

export interface Condition {
  type: ConditionType
  initialExpression?: string
  expression?: string
  inChangeMode: boolean
}

function conditionChanged(condition: Condition) {
  return condition.initialExpression !== condition.expression
}
function initialConditions(rule: Rule): Condition[] {
  return [
    {
      type: 'track',
      initialExpression: rule.titleExpression,
      expression: rule.titleExpression,
      inChangeMode: false
    }, {
      type: 'artist',
      initialExpression: rule.artistExpression,
      expression: rule.artistExpression,
      inChangeMode: false
    }, {
      type: 'album',
      initialExpression: rule.albumExpression,
      expression: rule.albumExpression,
      inChangeMode: false
    }
  ]
}
function mergeCondition(conditions: Condition[], type: ConditionType, newExpression?: string) {
  const newConditions = [...conditions]
  newConditions[conditions.findIndex(c => c.type === type)].expression = newExpression
  return newConditions
}
function toggleChangeMode(conditions: Condition[], type: ConditionType) {
  const newConditions = [...conditions]
  const index = conditions.findIndex(c => c.type === type)
  newConditions[index].inChangeMode = !newConditions[index].inChangeMode
  return newConditions
}

function toRule(id: string, title: string, conditions: Condition[]): Rule {
  return {
    id: id,
    title: title,
    titleExpression: conditions.filter(c => c.type === 'track')[0].expression,
    artistExpression: conditions.filter(c => c.type === 'artist')[0].expression,
    albumExpression: conditions.filter(c => c.type === 'album')[0].expression
  }
}

interface RuleCardProps {
  rule: Rule
}

export default function RuleCard(props: RuleCardProps) {
  const rule = props.rule
  const [deleteRule] = api.useDeleteRuleByIdMutation()
  const [updateRule] = api.useUpdatedRuleMutation()

  const [conditions, setConditions] = useState(initialConditions(rule))
  const [title, setTitle] = useState(rule.title)
  const [titleInChangeMode, setTitleChangeMode] = useState(false)

  const save = () => {
    updateRule(toRule(rule.id, title, conditions));
    setTitleChangeMode(false)
    setConditions(conditions.map(condition => {
      return {
        ...condition,
        inChangeMode: false,
        initialExpression: condition.expression
      }
    }))
  }

  return (
    <Grid item xs={12} md={6} lg={4}>
      <Card>
        <CardContent>
          <Box sx={{ display: 'flex' }}>
            <RuleTitle title={title} initialTitle={rule.title} inChangeMode={titleInChangeMode}
              onChange={setTitle}
              toggleChangeMode={() => setTitleChangeMode(!titleInChangeMode)}
              onSave={save} />
            {
              conditions.some(condition => condition.expression == null) &&
              <IconButton aria-label="add condition">
                <AddCircleIcon />
              </IconButton>
            }
          </Box>
          {
            conditions
              .filter(condition => condition.expression != null)
              .map(condition => [
                <RuleCondition key={rule.id + "_" + condition.type}
                  condition={condition}
                  onChange={(expression) => setConditions(mergeCondition(conditions, condition.type, expression))}
                  toggleChangeMode={() => setConditions(toggleChangeMode(conditions, condition.type))}
                  onSave={save} />])
              .reduce((prev, curr) => prev.length <= 1 ? curr : [...prev, <Divider key={curr[0].key + "_divider"} />, ...curr], [])
          }
        </CardContent>
        <CardActions>
          <Button startIcon={<DeleteIcon />} variant="text" sx={{ marginLeft: 'auto' }}
            onClick={() => deleteRule(rule.id)}>Delete</Button> {/*TODO extra verification before deletion */}
          <Button startIcon={<SaveIcon />} variant="contained"
            disabled={conditions.every(c => !conditionChanged(c)) && title === rule.title}
            onClick={save}>Save</Button>
        </CardActions>
      </Card>
    </Grid>
  )
}

