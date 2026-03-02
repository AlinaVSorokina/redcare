GitHub Repository Scoring Service

A Spring Boot (Java 21) REST API that retrieves GitHub repositories based on language and creation date, calculates a custom score, and returns ranked results.
The scoring algorithm is based on repository popularity (stars and forks) and freshness (last push date).

API

Request

/scoring?language=laguageName&creationDate=date 

language: If the language is not set or does not exist, this value will not be used in the search.

date: should be in YYYY-MM-DD format. if not set or is in the wrong format "bad request" will be returned

Response has json format

{
    "repoId": id,  // long
    "score": 15,  // int
    "fullName": repo name, // string
    "htmlUrl": "repo url, // string
    "language": repo language // string
},

Scoring Algorithm

The score combines popularity and freshness:
Popularity: logarithmic weighting of stars and forks
Freshness: exponential decay based on days since last push

score = (log(1 + stars) + 0.8 * log(1 + forks)) * 2 ^ (-daysSinceLastPush / 365)
