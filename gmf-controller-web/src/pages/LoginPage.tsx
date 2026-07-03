import { QrcodeOutlined } from '@ant-design/icons';
import { Button, Card, Typography } from 'antd';

const { Title, Text } = Typography;

export default function LoginPage() {
  return (
    <main className="login-page">
      <Card className="login-panel">
        <QrcodeOutlined className="login-icon" />
        <Title level={3}>微信扫码登录</Title>
        <Text type="secondary">仅开发者账号可进入后台</Text>
        <Button type="primary" block className="login-button">
          获取登录二维码
        </Button>
      </Card>
    </main>
  );
}
