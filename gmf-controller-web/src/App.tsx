import { Navigate, RouterProvider, createBrowserRouter } from 'react-router-dom';
import AdminLayout from './layout/AdminLayout';
import DashboardPage from './pages/DashboardPage';
import LoginPage from './pages/LoginPage';

const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginPage />
  },
  {
    path: '/',
    element: <AdminLayout />,
    children: [
      {
        index: true,
        element: <DashboardPage />
      }
    ]
  },
  {
    path: '*',
    element: <Navigate to="/" replace />
  }
]);

export default function App() {
  return <RouterProvider router={router} />;
}
