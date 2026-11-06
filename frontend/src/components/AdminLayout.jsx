import { Link, NavLink, Outlet, useNavigate, useOutletContext } from 'react-router-dom';
import { LayoutDashboard, Map, MessageSquareText, LogOut, ArrowUpRight } from 'lucide-react';
import { useEffect, useState } from 'react';
import { toast } from 'react-toastify';
import { api } from '../api';

export default function AdminLayout() {
  const navigate = useNavigate();
  const [status, setStatus] = useState('checking');
  const [dashboard, setDashboard] = useState(null);
  useEffect(() => {
    let active = true;
    api.get('/admin/dashboard').then(({data}) => { if (active) { setDashboard(data); setStatus('ready'); } }).catch(error => {
      if (!active) return;
      if ([401,403].includes(error.response?.status)) navigate('/admin/login', {replace:true});
      else setStatus('offline');
    });
    return () => { active = false; };
  }, [navigate]);
  async function logout() {
    try { await api.post('/auth/logout'); } catch { /* Clear local view even if the API is temporarily unreachable. */ }
    toast.info('You have signed out.');
    navigate('/admin/login', { replace: true });
  }
  if(status==='checking')return <main className="admin-gate"><div className="skeleton-photo"/><p>Opening your studio…</p></main>;
  if(status==='offline')return <main className="admin-gate"><div className="empty panel"><strong>Your studio is temporarily unavailable.</strong><span>Check that the API and database are running, then try again.</span><button className="btn" onClick={()=>window.location.reload()}>Try again</button><button className="text-button" onClick={()=>navigate('/admin/login')}>Back to sign in</button></div></main>;
  return <div className="admin-shell">
    <aside className="sidebar">
      <Link className="brand" to="/admin">malabar<span>.</span>studio</Link>
      <span className="side-label">WORKSPACE</span>
      <NavLink end to="/admin"><LayoutDashboard size={17}/> Overview</NavLink>
      <NavLink to="/admin/enquiries"><MessageSquareText size={17}/> Enquiries</NavLink>
      <NavLink to="/admin/packages"><Map size={17}/> Journeys</NavLink>
      <div className="sidebar-bottom"><Link to="/">View public site <ArrowUpRight size={15}/></Link><button className="logout-button" onClick={logout}><LogOut size={16}/> Sign out</button></div>
    </aside>
    <main className="admin-main"><Outlet context={{dashboard}}/></main>
  </div>;
}
