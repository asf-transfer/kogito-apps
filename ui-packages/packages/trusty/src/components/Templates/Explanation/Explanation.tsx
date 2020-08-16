import React, { useCallback, useEffect, useState } from 'react';
import { useParams, useHistory, useLocation } from 'react-router-dom';
import queryString from 'query-string';
import {
  Button,
  Card,
  CardBody,
  CardHeader,
  Divider,
  Grid,
  GridItem,
  Modal,
  PageSection,
  Stack,
  StackItem,
  Title,
  Tooltip
} from '@patternfly/react-core';
import { HelpIcon } from '@patternfly/react-icons';
import Outcomes from '../../Organisms/Outcomes/Outcomes';
import InputDataBrowser from '../../Organisms/InputDataBrowser/InputDataBrowser';
import FeaturesScoreChart from '../../Organisms/FeaturesScoreChart/FeaturesScoreChart';
import FeaturesScoreTable from '../../Organisms/FeaturesScoreTable/FeaturesScoreTable';
import ExplanationSwitch from '../../Organisms/ExplanationSwitch/ExplanationSwitch';
import SkeletonGrid from '../../Molecules/SkeletonGrid/SkeletonGrid';
import SkeletonTornadoChart from '../../Molecules/SkeletonTornadoChart/SkeletonTornadoChart';
import SkeletonStripe from '../../Atoms/SkeletonStripe/SkeletonStripe';
import useFeaturesScores from './useFeaturesScores';
import useOutcomeDetail from './useOutcomeDetail';
import { ExecutionRouteParams, Outcome, RemoteData } from '../../../types';
import './Explanation.scss';

type ExplanationProps = {
  outcomes: RemoteData<Error, Outcome[]>;
};

const Explanation = ({ outcomes }: ExplanationProps) => {
  const { executionId } = useParams<ExecutionRouteParams>();
  const [outcomeData, setOutcomeData] = useState<Outcome | null>(null);
  const [outcomesList, setOutcomesList] = useState<Outcome[] | null>(null);
  const [outcomeId, setOutcomeId] = useState<string | null>(null);
  const outcomeDetail = useOutcomeDetail(executionId, outcomeId);
  const { featuresScores, topFeaturesScores } = useFeaturesScores(executionId);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const history = useHistory();
  const location = useLocation();

  const handleModalToggle = () => {
    setIsModalOpen(!isModalOpen);
  };

  const switchExplanation = useCallback(
    (selectedOutcomeId: string) => {
      history.push({
        search: `outcomeId=${selectedOutcomeId}`
      });
    },
    [history]
  );

  useEffect(() => {
    if (outcomes.status === 'SUCCESS') {
      const defaultOutcome = outcomes.data[0];
      setOutcomesList(outcomes.data);
      if (!outcomeId) {
        history.replace({
          search: `outcomeId=${defaultOutcome.outcomeId}`
        });
      }
    }
  }, [executionId, outcomeId, history, outcomes]);

  useEffect(() => {
    const query = queryString.parse(location.search);
    if (query.outcomeId && query.outcomeId.length) {
      setOutcomeId(query.outcomeId as string);
      if (outcomesList) {
        const outcome = outcomesList.find(
          item => item.outcomeId === query.outcomeId
        );
        setOutcomeData(outcome);
      }
    }
  }, [location.search, outcomesList]);

  return (
    <section className="explanation-view">
      {outcomesList == null && (
        <PageSection
          variant="light"
          className="explanation-view__section--outcome-selector"
        >
          <Divider className="explanation-view__section--outcome-selector__divider" />
          <SkeletonStripe customStyle={{ width: 400 }} />
        </PageSection>
      )}
      {outcomeId !== null && outcomesList !== null && outcomesList.length > 1 && (
        <PageSection
          variant="light"
          className="explanation-view__section--outcome-selector"
        >
          <Divider className="explanation-view__section--outcome-selector__divider" />
          <ExplanationSwitch
            currentExplanationId={outcomeId}
            onDecisionSelection={switchExplanation}
            outcomesList={outcomesList}
          />
        </PageSection>
      )}
      <PageSection variant="default" className="explanation-view__section">
        <div className="container">
          <Stack hasGutter>
            <StackItem>
              <Title headingLevel="h3" size="2xl">
                Outcome Details
              </Title>
            </StackItem>
            <StackItem>
              {outcomeData === null ? (
                <Card>
                  <CardBody>
                    <SkeletonGrid rowsNumber={2} colsNumber={2} />
                  </CardBody>
                </Card>
              ) : (
                <Outcomes outcomes={[outcomeData]} />
              )}
            </StackItem>
          </Stack>
        </div>
      </PageSection>
      <PageSection className="explanation-view__section">
        <div className="container">
          <Stack hasGutter>
            <StackItem>
              <Title headingLevel="h3" size="2xl">
                Explanation
              </Title>
            </StackItem>
            <StackItem>
              <Grid hasGutter>
                <GridItem span={8}>
                  <Card>
                    <CardHeader>
                      {topFeaturesScores.length ? (
                        <Title headingLevel="h4" size="xl">
                          Top Features Score Chart
                        </Title>
                      ) : (
                        <Title headingLevel="h4" size="xl">
                          Features Score Chart
                        </Title>
                      )}
                    </CardHeader>

                    <CardBody>
                      {featuresScores.status === 'LOADING' && (
                        <SkeletonTornadoChart valuesCount={10} height={400} />
                      )}
                      {featuresScores.status === 'SUCCESS' &&
                        topFeaturesScores.length === 0 && (
                          <div className="explanation-view__chart">
                            <FeaturesScoreChart
                              featuresScore={featuresScores.data}
                            />
                          </div>
                        )}
                      {featuresScores.status === 'SUCCESS' &&
                        topFeaturesScores.length > 0 && (
                          <>
                            <div className="explanation-view__chart">
                              <FeaturesScoreChart
                                featuresScore={topFeaturesScores}
                              />
                            </div>
                            <Button
                              variant="secondary"
                              type="button"
                              className="all-features-opener"
                              onClick={handleModalToggle}
                            >
                              View Complete Chart
                            </Button>
                            <Modal
                              width={'80%'}
                              title="All Features Score Chart"
                              isOpen={isModalOpen}
                              onClose={handleModalToggle}
                              actions={[
                                <Button key="close" onClick={handleModalToggle}>
                                  Close
                                </Button>
                              ]}
                            >
                              <FeaturesScoreChart
                                featuresScore={featuresScores.data}
                                large={true}
                              />
                            </Modal>
                          </>
                        )}
                    </CardBody>
                  </Card>
                </GridItem>
                <GridItem span={4}>
                  <Card className="explanation-view__score-table">
                    <CardHeader>
                      <Title headingLevel={'h4'} size={'lg'}>
                        Features Weight
                      </Title>
                    </CardHeader>
                    <CardBody>
                      {featuresScores.status === 'LOADING' && (
                        <SkeletonGrid colsNumber={2} rowsNumber={4} />
                      )}
                      {featuresScores.status === 'SUCCESS' &&
                        topFeaturesScores.length === 0 && (
                          <FeaturesScoreTable
                            featuresScore={featuresScores.data}
                          />
                        )}
                      {featuresScores.status === 'SUCCESS' &&
                        topFeaturesScores.length > 0 && (
                          <FeaturesScoreTable
                            featuresScore={topFeaturesScores}
                          />
                        )}
                    </CardBody>
                  </Card>
                </GridItem>
              </Grid>
            </StackItem>
          </Stack>
        </div>
      </PageSection>
      <PageSection className="explanation-view__section">
        <div className="container">
          <Stack hasGutter>
            <StackItem>
              <Title headingLevel="h3" size="2xl">
                <span>Outcome Influencing Inputs</span>
                <Tooltip
                  position="auto"
                  content={
                    <div>
                      This section displays all the input that contributed to
                      this specific decision outcome. They can include model
                      inputs (or a subset) or other sub-decisions
                    </div>
                  }
                >
                  <HelpIcon className="explanation-view__input-help" />
                </Tooltip>
              </Title>
            </StackItem>
            <StackItem>
              <InputDataBrowser inputData={outcomeDetail} />
            </StackItem>
          </Stack>
        </div>
      </PageSection>
    </section>
  );
};

export default Explanation;
