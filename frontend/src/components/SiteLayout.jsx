import { Link, NavLink, Outlet } from 'react-router-dom';
import { ArrowUpRight, Menu, X } from 'lucide-react';
import { useState } from 'react';

export function SiteLayout() {
  const [open, setOpen] = useState(false);
  return <div className="site-frame">
    <header className="site-nav-wrap">
      <div className="container site-nav">
        <Link className="brand" to="/" onClick={() => setOpen(false)}>malabar<span>.</span>trails</Link>
        <button className="menu-toggle" aria-label={open ? 'Close menu' : 'Open menu'} onClick={() => setOpen(!open)}>{open ? <X/> : <Menu/>}</button>
        <nav className={`nav-links ${open ? 'is-open' : ''}`}>
          <a href="/#about" onClick={() => setOpen(false)}>Our story</a>
          <Link to="/destinations" onClick={() => setOpen(false)}>Destinations</Link>
          <Link className="staff-link" to="/admin/login" onClick={() => setOpen(false)}>Staff login</Link>
          <Link className="btn nav-cta" to="/packages" onClick={() => setOpen(false)}>Plan your journey <ArrowUpRight size={15}/></Link>
        </nav>
      </div>
    </header>
    <Outlet/>
    <footer className="site-footer">
      <div className="container footer-row">
        <div><Link className="brand" to="/">malabar<span>.</span>trails</Link><p>Journeys, thoughtfully yours.</p></div>
        <div><strong>Speak with our Kochi team</strong><p>hello@malabartrails.example · +91 484 000 0000</p><Link to="/admin/login">Staff sign in</Link></div>
      </div>
      <div className="container footer-bottom"><span>© {new Date().getFullYear()} Malabar Trails, Kochi</span><span>Thoughtful journeys across South India</span></div>
    </footer>
  </div>;
}

export function PackageCard({ item }) {
  const words = `${item.title || ''} ${item.destination || ''} ${item.region || ''}`.toLowerCase();
  const fallback = words.includes('munnar') || words.includes('thekkady') ? '/images/munnar.svg'
    : words.includes('alleppey') || words.includes('alappuzha') ? '/images/alleppey.svg'
      : words.includes('wayanad') ? '/images/wayanad.svg' : '/images/kerala.svg';
  return <article className="package-card">
    <Link className="package-photo" to={`/packages/${item.slug}`}>
      <img src={item.coverImageUrl || fallback} onError={event => { event.currentTarget.onerror = null; event.currentTarget.src = fallback; }} referrerPolicy="no-referrer" alt={item.title} loading="lazy"/>
      <span className="photo-arrow"><ArrowUpRight size={18}/></span>
    </Link>
    <div className="package-copy">
      <span className="eyebrow">{item.region || item.destination || 'Kerala'}</span>
      <h3><Link to={`/packages/${item.slug}`}>{item.title}</Link></h3>
      <div className="package-meta"><span>{item.durationNights || item.nights || '—'} nights</span><span>{item.hotelCategory || 'Handpicked stays'}</span></div>
      <div className="card-foot"><span className="price">From ₹{Number(item.startingPrice || item.price || 0).toLocaleString('en-IN')} <small className="muted">/ person*</small></span><Link to={`/packages/${item.slug}`}>Explore <ArrowUpRight size={14}/></Link></div>
    </div>
  </article>;
}
