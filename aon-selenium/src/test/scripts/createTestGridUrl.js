var webdriver = require('selenium-webdriver');
var AWS = require("aws-sdk");

var PROJECT_ARN = "arn:aws:devicefarm:us-west-2:083580179390:testgrid-project:05a8aa9e-c70b-4bb4-9c74-fe1bb30416c9";
var devicefarm = new AWS.DeviceFarm({ region: "us-west-2" });

// Get the endpoint to create a new session
devicefarm.createTestGridUrl({
	projectArn: PROJECT_ARN,
        expiresInSeconds: 600
},
function(err, data) {
  if (err) console.log(err, err.stack); 	// an error occurred
  else     process.stdout.write(data.url);	// successful response
});




