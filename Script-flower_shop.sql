CREATE TABLE flowers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    value NUMERIC(10,2) NOT NULL,
    category VARCHAR(50) NOT NULL
);

INSERT INTO flowers (name, value, category) VALUES
    ('Rose', 100, 'Cut Flowers'),
    ('Tulip', 200, 'Cut Flowers'),
    ('Orchid', 35, 'House Plant');

select * from flowers; 


CREATE OR REPLACE FUNCTION search_flower(flower_name VARCHAR)
RETURNS TABLE(id INT, name VARCHAR, value NUMERIC, category VARCHAR) AS $$
BEGIN
    RETURN QUERY 
    SELECT flowers.id, flowers.name, flowers.value, flowers.category 
    FROM flowers 
    WHERE flowers.name ILIKE '%' || flower_name || '%';
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION delete_flower(flower_name VARCHAR)
RETURNS VOID AS $$
BEGIN
    DELETE FROM flowers WHERE name = flower_name;
END;
$$ LANGUAGE plpgsql;


SELECT add_flower('Lily', 150, 'Cut Flowers');

SELECT * FROM search_flower('Lily');

SELECT delete_flower('Lily');


CREATE OR REPLACE FUNCTION add_flower(flower_name VARCHAR, flower_value NUMERIC, flower_category VARCHAR)
RETURNS VOID AS $$
BEGIN
    INSERT INTO flowers (name, value, category) VALUES (flower_name, flower_value, flower_category);
END;
$$ LANGUAGE plpgsql;
