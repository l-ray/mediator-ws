INSERT INTO categories ( id, term, category, hidden, deleted) VALUES
  (1,'ska','ska',0,0),
  (2,'metal','metal',0,0),
  (3,'rock','rock',0,0);

INSERT INTO term_weight (term, weight, deleted) VALUES
  ('club'    , 0.1,  0),
  ('bar'     , 0.1,  0),
  ('lounge'  , 0.1,  0),
  ('e.v.'    , 0.1,  0),
  ('Gelände' , 0.2,  0),
  ('alter'   , 0.1,  0),
  ('alte'    , 0.1,  0),
  ('altes'   , 0.1,  0),
  ('dresden' , 0.05, 0),
  ('live'    , 0.01, 0);

INSERT INTO user_rules (id, rule_type, rule_input, priority_change) VALUES
  (1, 0, '/Berlin/i',          0),
  (2, 0, '/Leipzig/i',         0),
  (3, 0, '/Mei.en/i',          0),
  (4, 1, 'theater',            0),
  (5, 1, 'radiosendung',       0),
  (6, 1, 'film',               -1000),
  (8, 0, '/G.rlitz/i',         0),
  (15, 2, '/zebra disco/',  0),
  (14, 2, '/D.beln/',       0),
  (16, 2, '/zebra/',        0),
  (19, 2, '/afterwork.purobeach/', 0);
