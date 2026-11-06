UPDATE destinations SET cover_image_url = CASE slug
  WHEN 'munnar' THEN 'https://images.unsplash.com/photo-1593693397690-362cb9666fc2?auto=format&fit=crop&w=1800&q=88'
  WHEN 'wayanad' THEN 'https://images.unsplash.com/photo-1511497584788-876760111969?auto=format&fit=crop&w=1800&q=88'
  WHEN 'alappuzha' THEN 'https://images.unsplash.com/photo-1602301414682-03f4fc2f3c3f?auto=format&fit=crop&w=1800&q=88'
  ELSE cover_image_url
END;
