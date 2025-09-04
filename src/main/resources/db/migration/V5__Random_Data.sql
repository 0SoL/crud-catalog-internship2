SET synchronous_commit = off;

INSERT INTO catalog (name, description, price)
SELECT
    'Product ' || gs.i,
    'Desc ' || substr(md5(random()::text), 1, 16),
    round( (10 + random()*990)::numeric, 2)
FROM generate_series(1, 3000000) AS gs(i);

RESET synchronous_commit;