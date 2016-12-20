1. Download selinum server from http://selenium-release.storage.googleapis.com/index.html
1.1 Start it java -jar 
2. Download selinum driver https://github.com/mozilla/geckodriver/releases
2.1 Put it to bin directory
3. Install nightwatch: npm install -g nightwatch
4. Prepare config and test
4.1 nightwatch.json, tests/test.js
4.2 start test: nightwatch tests/test.js
