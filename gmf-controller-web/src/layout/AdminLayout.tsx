import { BarChartOutlined, TeamOutlined } from '@ant-design/icons';
import { Layout, Menu, theme } from 'antd';
import { Outlet } from 'react-router-dom';

const { Content, Header, Sider } = Layout;

export default function AdminLayout() {
  const {
    token: { colorBgContainer }
  } = theme.useToken();

  return (
    <Layout className="admin-shell">
      <Sider breakpoint="lg" collapsedWidth="0">
        <div className="brand">Give Me Five</div>
        <Menu
          theme="dark"
          mode="inline"
          defaultSelectedKeys={['dashboard']}
          items={[
            { key: 'dashboard', icon: <BarChartOutlined />, label: '概览' },
            { key: 'users', icon: <TeamOutlined />, label: '用户' }
          ]}
        />
      </Sider>
      <Layout>
        <Header className="admin-header" style={{ background: colorBgContainer }}>
          管理后台
        </Header>
        <Content className="admin-content">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
}
