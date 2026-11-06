ALTER TABLE travel_packages ADD COLUMN IF NOT EXISTS latitude DOUBLE PRECISION;
ALTER TABLE travel_packages ADD COLUMN IF NOT EXISTS longitude DOUBLE PRECISION;
ALTER TABLE travel_packages ADD COLUMN IF NOT EXISTS map_embed_url TEXT;
ALTER TABLE travel_packages ADD COLUMN IF NOT EXISTS hotel_details TEXT;
ALTER TABLE travel_packages ADD COLUMN IF NOT EXISTS availability_message TEXT;
CREATE TABLE IF NOT EXISTS package_media(id UUID PRIMARY KEY DEFAULT gen_random_uuid(), package_id UUID NOT NULL REFERENCES travel_packages(id) ON DELETE CASCADE, image_url TEXT NOT NULL, alt_text VARCHAR(240), sort_order INT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT now());
CREATE TABLE IF NOT EXISTS destinations(id UUID PRIMARY KEY DEFAULT gen_random_uuid(), slug VARCHAR(180) UNIQUE NOT NULL, name VARCHAR(160) NOT NULL, summary VARCHAR(800), description TEXT, cover_image_url TEXT, latitude DOUBLE PRECISION, longitude DOUBLE PRECISION, map_embed_url TEXT, seo_title VARCHAR(160), seo_description VARCHAR(320), status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED' CHECK(status IN ('DRAFT','PUBLISHED')));
CREATE INDEX IF NOT EXISTS package_media_package_sort_idx ON package_media(package_id, sort_order);
INSERT INTO destinations(slug,name,summary,description,cover_image_url,status) VALUES
('munnar','Munnar','Tea gardens, mountain air and slow mornings in the Western Ghats.','Munnar is made for unhurried walks through tea country, quiet viewpoints and warm, deeply local hospitality.','/images/munnar.svg','PUBLISHED'),
('wayanad','Wayanad','Forest trails, spice gardens and a gentler pace.','A green retreat of rainforest, wildlife and intimate stays.','/images/wayanad.svg','PUBLISHED'),
('alappuzha','Alappuzha','Backwaters, houseboats and coconut-fringed canals.','Follow Kerala’s waterways through village life and sunset-lit lagoons.','/images/alleppey.svg','PUBLISHED') ON CONFLICT (slug) DO NOTHING;
