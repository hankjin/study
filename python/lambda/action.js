console.log('Loading function');

exports.handler = function(event, context) {
    console.log(JSON.stringify(event, null, 2));

    // Get Data from SNS Message
    var data = event.Records[0].Sns.Message;
    var message = JSON.parse(data);

    // Connect to S3
    var AWS = require('aws-sdk');
    AWS.config.update({region: message.Region});
    var S3 = new AWS.S3();
    var params = ({Bucket: message.Bucket, Key: message.Key, Body: message.Body});
    var info = "put object " + message.Key + " to bucket " + message.Bucket + " with " + message.Body;

    // Put Message to S3
    S3.putObject(params, function(err, data) {
        if (err) {
            console.log("Fail to " + info + ". error:"+err);
        }
        else {
            console.log("Succeed to " + info);
        }
    context.done(err);
    });
}
