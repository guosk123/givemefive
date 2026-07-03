import { Card, Col, Row, Statistic } from 'antd';

export default function DashboardPage() {
  return (
    <Row gutter={[16, 16]}>
      <Col xs={24} sm={12} lg={8}>
        <Card>
          <Statistic title="用户数" value={0} />
        </Card>
      </Col>
      <Col xs={24} sm={12} lg={8}>
        <Card>
          <Statistic title="今日活跃" value={0} />
        </Card>
      </Col>
      <Col xs={24} sm={12} lg={8}>
        <Card>
          <Statistic title="记录数" value={0} />
        </Card>
      </Col>
    </Row>
  );
}
