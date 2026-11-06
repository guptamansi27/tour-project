import { lazy, Suspense } from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import { SiteLayout } from './components/SiteLayout';
import AdminLayout from './components/AdminLayout';

const HomePage = lazy(() => import('./public-pages').then((module) => ({ default: module.HomePage })));
const PackagesPage = lazy(() => import('./public-pages').then((module) => ({ default: module.PackagesPage })));
const PackageDetailPage = lazy(() => import('./public-pages').then((module) => ({ default: module.PackageDetailPage })));
const DestinationPage = lazy(() => import('./public-pages').then((module) => ({ default: module.DestinationPage })));
const DestinationsPage = lazy(() => import('./public-pages').then((module) => ({ default: module.DestinationsPage })));
const LoginPage = lazy(() => import('./public-pages').then((module) => ({ default: module.LoginPage })));
const DashboardPage = lazy(() => import('./admin-pages').then((module) => ({ default: module.DashboardPage })));
const EnquiriesPage = lazy(() => import('./admin-pages').then((module) => ({ default: module.EnquiriesPage })));
const AdminPackagesPage = lazy(() => import('./admin-pages').then((module) => ({ default: module.AdminPackagesPage })));
const PackageEditorPage = lazy(() => import('./admin-pages').then((module) => ({ default: module.PackageEditorPage })));

function RouteLoading() {
  return <div className="route-loading" role="status"><span className="loading-mark" />Preparing your page…</div>;
}

export default function App() {
  return <Suspense fallback={<RouteLoading />}><Routes>
    <Route element={<SiteLayout />}>
      <Route index element={<HomePage />} />
      <Route path="packages" element={<PackagesPage />} />
      <Route path="packages/:slug" element={<PackageDetailPage />} />
      <Route path="destinations/:slug" element={<DestinationPage />} />
      <Route path="destinations" element={<DestinationsPage />} />
    </Route>
    <Route path="/admin/login" element={<LoginPage />} />
    <Route path="/admin" element={<AdminLayout />}>
      <Route index element={<DashboardPage />} />
      <Route path="enquiries" element={<EnquiriesPage />} />
      <Route path="packages" element={<AdminPackagesPage />} />
      <Route path="packages/new" element={<PackageEditorPage />} />
      <Route path="packages/:id" element={<PackageEditorPage />} />
    </Route>
    <Route path="*" element={<Navigate to="/" replace />} />
  </Routes></Suspense>;
}
