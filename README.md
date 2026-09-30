# Bets-R-Us Readme
REST API based sportsbook application 

System Requirements

Apache Maven 3.8.4 
Java version: 14.0.1, vendor: Oracle Corporation, runtime: [installdir]\jdk-14.0.1_windows-x64_bin\jdk-14.0.1

The system requires an installation of mongodb, an up to date release can be found at
https://www.mongodb.com/docs/manual/administration/install-community/


cd /d E:\Dev\bets-r-us

http://localhost:8080/swagger-ui.html#/

API key for the-odds-api.com
64ca26ada245a03d76408a313de48317

 https://api.the-odds-api.com/v4/sports/?apiKey=64ca26ada245a03d76408a313de48317

 https://api.the-odds-api.com/v4/sports/americanfootball_nfl/participants?apiKey=64ca26ada245a03d76408a313de48317
 
 https://api.the-odds-api.com/v4/sports/americanfootball_nfl/events?apiKey=64ca26ada245a03d76408a313de48317
 
  https://api.the-odds-api.com/v4/sports/americanfootball_nfl/event/aee7eae1849ebb103b9bf233e9741392/odds?apiKey=64ca26ada245a03d76408a313de48317&regions=us&markets=h2h%2Cspreads%2Ctotals&dateFormat=iso&oddsFormat=american&bookmakers=draftkings
  
  GET /v4/sports/{sport}/events/{eventId}/odds?apiKey={apiKey}&regions={regions}&markets={markets}&dateFormat={dateFormat}&oddsFormat={oddsFormat}

https://github.com/PJYankee/bets-r-us.git

docker compose up --build -d