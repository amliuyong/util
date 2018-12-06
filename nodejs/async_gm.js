var async = require('async');
var gm = require('gm').subClass({
  imageMagick: true
}); // Enable ImageMagick integration.
var util = require('util');

async.waterfall([
  function download(next) {
    // Download the image from S3 into a buffer.
    s3.getObject({
        Bucket: srcBucket,
        Key: srcKey
      },
      next);
  },
  function tranform(response, next) {
    gm(response.Body).size(function(err, size) {
      // Infer the scaling factor to avoid stretching the image unnaturally.
      var scalingFactor = Math.min(
        MAX_WIDTH / size.width,
        MAX_HEIGHT / size.height
      );
      var width = scalingFactor * size.width;
      var height = scalingFactor * size.height;

      // Transform the image buffer in memory.
      this.resize(width, height)
        .toBuffer(imageType, function(err, buffer) {
          if (err) {
            next(err);
          } else {
            next(null, response.ContentType, response.Metadata.data,
              buffer);
          }
        });
    });
  },
  function upload(contentType, metadata, data, next) {
    // Stream the transformed image to a different S3 bucket.
    s3.putObject({
      Bucket: dstBucket,
      Key: dstKey,
      Body: data,
      ContentType: contentType
    }, function(err, buffer) {
      if (err) {
        next(err);
      } else {
        next(null, metadata);
      }
    });
  },
  function index(metadata, next) {
    // adds metadata do DynamoDB
    var json_metadata = JSON.parse(metadata);
    var params = {
      TableName: DDB_TABLE,
      Item: {
        identityId: {
          S: identityId
        },
        objectKey: {
          S: srcKey
        },
        thumbnailKey: {
          S: dstKey
        },
        isPublic: {
          BOOL: json_metadata.isPublic
        },
        uploadDate: {
          S: eventTime
        },
        uploadDay: {
          S: eventTime.substr(0, 10)
        },
        title: {
          S: json_metadata.title
        },
        description: {
          S: json_metadata.description
        }
      }
    };
    dynamodb.putItem(params, next);
  }
], function(err) {
  if (err) console.log(err, err.stack);
  else console.log('Ok');
});
