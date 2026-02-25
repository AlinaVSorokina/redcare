GitHub Repository Scoring Service

A Spring Boot (Java 21) REST API that retrieves GitHub repositories based on language and creation date, calculates a custom score, and returns ranked results.
The scoring algorithm is based on repository popularity (stars and forks) and freshness (last push date).

Scoring Algorithm

The score combines popularity and freshness:
Popularity: logarithmic weighting of stars and forks
Freshness: exponential decay based on days since last push

score = (log(1 + stars) + 0.8 * log(1 + forks)) * 2 ^ (-daysSinceLastPush / 365)
