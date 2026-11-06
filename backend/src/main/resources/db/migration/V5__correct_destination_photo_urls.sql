UPDATE destinations SET cover_image_url = CASE slug
  WHEN 'wayanad' THEN 'https://images.unsplash.com/photo-1579497172605-cd1fb35bd919?auto=format&fit=crop&w=1800&q=88'
  WHEN 'alappuzha' THEN 'https://images.unsplash.com/photo-1593693401060-9fc28cf9e368?auto=format&fit=crop&w=1800&q=88'
  ELSE cover_image_url
END;
