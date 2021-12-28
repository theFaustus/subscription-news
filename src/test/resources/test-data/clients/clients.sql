--Clean up
truncate clients cascade;

--Insert test data
INSERT INTO public.clients (id, created_at, updated_at, email_address) VALUES ('d90c971c-4a48-45cc-9301-d9dd072ac14d', '2022-12-20 17:06:48.230205', '2022-12-20 17:06:48.230205', 'peter@gmail.com');
