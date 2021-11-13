import { Box, Button, Card, CardActions, CardContent, Divider } from "@mui/material";
import DeleteIcon from '@mui/icons-material/Delete';
import SaveIcon from '@mui/icons-material/Save';
import { Condition, ConditionType, Rule } from "../../common/types";
import RuleCondition from "./RuleCondition";
import { useState } from "react";
import RuleTitle from "./RuleTitle";
import AddConditionButton from "./AddConditionButton";
import ConfirmDeletionDialog from "./ConfirmDeletionDialog";
import { rulesApi } from '../../api/rulesApi';

function conditionChanged(condition: Condition) {
  return condition.initialExpression !== condition.expression;
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
  ];
}
function mergeCondition(conditions: Condition[], type: ConditionType, newExpression?: string) {
  const newConditions = [...conditions];
  newConditions[conditions.findIndex(c => c.type === type)].expression = newExpression;
  return newConditions;
}
function initializeCondition(conditions: Condition[], type: ConditionType) {
  const newConditions = [...conditions];
  const index = conditions.findIndex(c => c.type === type);
  newConditions[index].expression = 'g::bi';
  if (newConditions[index].initialExpression == null) {
    newConditions[index].initialExpression = 'g::bi';
  }
  return newConditions;
}
function clearCondition(conditions: Condition[], type: ConditionType) {
  const newConditions = [...conditions];
  const index = conditions.findIndex(c => c.type === type);
  newConditions[index].expression = undefined;
  if (newConditions[index].initialExpression === 'g::bi') {
    newConditions[index].initialExpression = undefined;
  }
  return newConditions;
}
function toggleChangeMode(conditions: Condition[], type: ConditionType) {
  const newConditions = [...conditions];
  const index = conditions.findIndex(c => c.type === type);
  newConditions[index].inChangeMode = !newConditions[index].inChangeMode;
  return newConditions;
}

function toRule(id: string, title: string, conditions: Condition[]): Rule {
  return {
    id: id,
    title: title,
    titleExpression: conditions.filter(c => c.type === 'track')[0].expression,
    artistExpression: conditions.filter(c => c.type === 'artist')[0].expression,
    albumExpression: conditions.filter(c => c.type === 'album')[0].expression
  };
}

interface RuleCardProps {
  rule: Rule;
}

export default function RuleCard(props: RuleCardProps) {
  const rule = props.rule;
  const [deleteRule] = rulesApi.useDeleteRuleByIdMutation();
  const [updateRule] = rulesApi.useUpdateRuleMutation();

  const [conditions, setConditions] = useState(initialConditions(rule));
  const [title, setTitle] = useState(rule.title);
  const [titleInChangeMode, setTitleChangeMode] = useState(false);

  const [confirmDeletionDialogOpen, setConfirmDeletionDialogOpen] = useState(false);

  const save = () => {
    updateRule(toRule(rule.id, title, conditions));
    setTitleChangeMode(false);
    setConditions(conditions.map(condition => {
      return {
        ...condition,
        inChangeMode: false,
        initialExpression: condition.expression
      };
    }));
  };
  const missingTypes = conditions.filter(condition => condition.expression == null)
    .map(condition => condition.type);
  const enabledConditions = conditions.filter(condition => condition.expression != null);

  return (
    <Card>
      <CardContent>
        <Box sx={{ display: 'flex' }}>
          <RuleTitle title={title} initialTitle={rule.title} inChangeMode={titleInChangeMode}
            onChange={setTitle}
            toggleChangeMode={() => setTitleChangeMode(!titleInChangeMode)}
            onSave={save} />
          {
            missingTypes.length > 0 &&
            <AddConditionButton possibleTypes={missingTypes}
              onNewCondition={(type) => setConditions(initializeCondition(conditions, type))} />
          }
        </Box>
        {
          enabledConditions.map(condition => [
            <RuleCondition key={rule.id + "_" + condition.type}
              condition={condition}
              onChange={(expression) => setConditions(mergeCondition(conditions, condition.type, expression))}
              onDelete={() => setConditions(clearCondition(conditions, condition.type))}
              toggleChangeMode={() => setConditions(toggleChangeMode(conditions, condition.type))}
              onSave={save} />])
            .reduce((prev, curr) => prev.length < 1 ? curr : [...prev, <Divider key={curr[0].key + "_divider"} />, ...curr], [])
        }
      </CardContent>
      <CardActions>
        <Button startIcon={<DeleteIcon />} variant="text" sx={{ marginLeft: 'auto' }}
          onClick={() => setConfirmDeletionDialogOpen(true)}>Delete</Button>
        <Button startIcon={<SaveIcon />} variant="contained"
          disabled={conditions.every(c => !conditionChanged(c)) && title === rule.title}
          onClick={save}>Save</Button>
      </CardActions>
      <ConfirmDeletionDialog open={confirmDeletionDialogOpen} onCancel={() => setConfirmDeletionDialogOpen(false)}
        onConfirm={() => deleteRule(rule.id)} />
    </Card>
  );
}

