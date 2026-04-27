INSERT INTO rewards (username, points) VALUES ('auth0|1234567890', 100);
INSERT INTO rewards (username, points) VALUES ('auth0|0987654321', 250);

INSERT INTO applications (name, client_id, is_active, owner_id) 
VALUES ('test-app', 'test-client-id', TRUE, 'auth0|1234567890');
