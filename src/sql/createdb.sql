Create TABLE Movies
(
  MovieId VARCHAR(300) NOT NULL,
  Title VARCHAR(300),
  ImdbMovieId VARCHAR(300),
  Year INTEGER,
  RTMovieId VARCHAR(300),
  RTACriticRating NUMBER,
  RTANumberOfReviews INTEGER,
  RTAAVGScore NUMBER,
  RTTCriticRating NUMBER,
  RTTNumberOfReviews INTEGER,
  RTTAVGScore NUMBER,
  RTAudienceRating NUMBER,
  RTAudienceNumberOfRating INTEGER,
  RTAudienceAVGScore NUMBER,
  PRIMARY KEY(MovieId)
);

Create TABLE Movie_genres
(
  MovieId VARCHAR(300) NOT NULL,
  Genres VARCHAR(300),
  PRIMARY KEY(MovieId, Genres),
  FOREIGN KEY (MovieId) REFERENCES Movies(MovieId) ON DELETE CASCADE 
);

Create TABLE Movie_directors
(
  MovieId VARCHAR(300) NOT NULL,
  DirectorId VARCHAR(300),
  DirectorName VARCHAR(300),
  PRIMARY KEY(MovieId, DirectorId),
  FOREIGN KEY (MovieId) REFERENCES Movies(MovieId) ON DELETE CASCADE
);

Create TABLE Movie_actors 
(
  MovieId VARCHAR(300) NOT NULL,
  ActorId VARCHAR(300),
  ActorName VARCHAR(300),
  Ranking INTEGER,
  PRIMARY KEY(MovieId, ActorId),
  FOREIGN KEY (MovieId) REFERENCES Movies(MovieId) ON DELETE CASCADE
);

Create TABLE Movie_countries 
(
  MovieId VARCHAR(300) NOT NULL,
  Country VARCHAR(300),
  PRIMARY KEY(MovieId),
  FOREIGN KEY (MovieId) REFERENCES Movies(MovieId) ON DELETE CASCADE
);

Create TABLE Movie_locations
(
  MovieId VARCHAR(300) NOT NULL,
  Country VARCHAR(300),
  States VARCHAR(300),
  City VARCHAR(300),
  FOREIGN KEY (MovieId) REFERENCES Movies(MovieId) ON DELETE CASCADE
);

Create TABLE Tags
(
  TagId VARCHAR(300),
  TagText VARCHAR(300),
  PRIMARY KEY(TagId)
);

Create TABLE Movie_tags
(
  MovieId VARCHAR(300),
  TagId VARCHAR(300),
  TagWeight INTEGER,
  PRIMARY KEY(MovieId, TagId),
  FOREIGN KEY (MovieId) REFERENCES Movies(MovieId) ON DELETE CASCADE,
  FOREIGN KEY (TagId) REFERENCES Tags(TagId) ON DELETE CASCADE
);

Create TABLE User_taggedmovies
(
  UserId VARCHAR(300),
  MovieId VARCHAR(300),
  TagId VARCHAR(300),
  Timestamp TIMESTAMP,
  PRIMARY KEY(UserId, MovieId, TagId),
  FOREIGN KEY (MovieId) REFERENCES Movies(MovieId) ON DELETE CASCADE,
  FOREIGN KEY (TagId) REFERENCES Tags(TagId) ON DELETE CASCADE
);

Create TABLE User_ratedmovies
(
  UserId VARCHAR(300),
  MovieId VARCHAR(300),
  Rating NUMBER,
  Timestamp TIMESTAMP,
  PRIMARY KEY(UserId, MovieId),
  FOREIGN KEY (MovieId) REFERENCES Movies(MovieId) ON DELETE CASCADE
);

CREATE INDEX genre_idx ON Movie_genres (Genres);
CREATE INDEX director_idx ON Movie_directors (DirectorId);
CREATE INDEX actor_idx ON Movie_actors (ActorId);
CREATE INDEX country_idx ON Movie_countries (country);
CREATE INDEX tagmovie_idx ON User_taggedmovies (Timestamp);
CREATE INDEX ratemovie_idx ON User_ratedmovies (Timestamp);

