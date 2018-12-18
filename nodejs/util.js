

function init() {
    if (email) {
        result.innerHTML = 'Type your new password for user ' + email;
    }
}

var form = document.getElementById('reset-password-form');
form.addEventListener('submit', function (evt) {
    evt.preventDefault();
    resetPassword();
});

window.onload = init();

//decodeURIComponent
function getUrlParams() {
    var p = {};
    var match,
        pl = /\+/g, // Regex for replacing addition symbol with a space
        search = /([^&=]+)=?([^&]*)/g,
        decode = function (s) {
            return decodeURIComponent(s.replace(pl, " "));
        },
        query = window.location.search.substring(1);
    while (match = search.exec(query))
        p[decode(match[1])] = decode(match[2]);
    return p;
}

//app.js
(function(){
	$(document).ready(function(){
		userController.init(configConstants);
	});
}());

//userController.js
var userController = {
    data: {
      auth0Lock: null,
      config: null
    },
    uiElements: {
      loginButton: null,
      logoutButton: null,
    },
    init: function(config) {
      var that = this;
    },

    fn1: function (){
     
    }
}

window.addEventListener('load', function() {
    // your code here
});

//clone  prepend
var clone = this.uiElements.videoCardTemplate.clone().attr('id','video-' + i);
clone.find('source').attr('src', baseUrl + '/' + bucket + '/' + video.filename);
this.uiElements.videoList.prepend(clone);


function isAuthenticated() {
    // Check whether the current time is past the
    // access token's expiry time
    var expiresAt = JSON.parse(localStorage.getItem('expires_at'));
    return new Date().getTime() < expiresAt;
  }
 
function setSession(authResult) {
    // Set the time that the access token will expire at
    var expiresAt = JSON.stringify(
      authResult.expiresIn * 1000 + new Date().getTime()
    );
    localStorage.setItem('access_token', authResult.accessToken);
    localStorage.setItem('id_token', authResult.idToken);
    localStorage.setItem('expires_at', expiresAt);
  }

  $.ajaxSetup({
    'beforeSend': function(xhr) {
      console.log("set JQuery Authorization: Bearer xxxxx");
      xhr.setRequestHeader('Authorization', 'Bearer ' + id_token);
    }
  });
  
  var fd = new FormData();
  fd.append('key', data.key)
  fd.append('acl', 'private');
  fd.append('Content-Type', file.type);
  fd.append('AWSAccessKeyId', data.access_key);
  fd.append('policy', data.encoded_policy)
  fd.append('signature', data.signature);
  fd.append('file', file, file.name);

  $.ajax({
      url: data.upload_url,
      type: 'POST',
      data: fd,
      processData: false,
      contentType: false,
      xhr: this.progress,
      beforeSend: function (req) {
          req.setRequestHeader('Authorization', '');
      }
  }).done(function (response) {
      that.uiElements.uploadButtonContainer.show();
      that.uiElements.uploadProgressBar.hide();
      alert('Uploaded Finished');
  }).fail(function (response) {
      that.uiElements.uploadButtonContainer.show();
      that.uiElements.uploadProgressBar.hide();
      alert('Failed to upload');
  })
},
progress: function () {
  var xhr = $.ajaxSettings.xhr();
  xhr.upload.onprogress = function (evt) {
      var percentage = evt.loaded / evt.total * 100;
      $('#upload-progress').find('.progress-bar').css('width', percentage + '%');
  };
  return xhr;
}

//============================

crypto.randomBytes(20).toString('hex');


var key = event.Records[0].s3.object.key;
var sourceKey = decodeURIComponent(key.replace(/\+/g, " "));


var input = {
    email: email,
    lost: lost,
    password: password.value
};

lambda.invoke({
    FunctionName: 'sampleAuthResetPassword',
    Payload: JSON.stringify(input)
}, function (err, data) {
    if (err) console.log(err, err.stack);
    else {
        var output = JSON.parse(data.Payload);
        if (output.changed) {
            result.innerHTML = 'Password changed for user ' + email;
        } else {
            result.innerHTML = 'Password <b>not</b> changed.';
        }
    }
});


var mediaFile = document.getElementById('mediaFile');
var file = mediaFile.files[0];
var isPublic = document.getElementById('is-public');
var title = document.getElementById('title');
var description = document.getElementById('description');

var params = {
    Bucket: S3_BUCKET,
    Key: key,
    ContentType: file.type,
    Body: file,
    Metadata: {
        data: JSON.stringify({
            isPublic: isPublic.checked,
            title: title.value,
            description: description.value
        })
    }
};
//uploadToS3(params);
s3.putObject(params, function (err, data) {

});


//----------------------------//


// lib.js
function send(toList, fromEmail, subject, message) {
    async.waterfall([createMessage.bind(this, toList, fromEmail, subject, message), dispatch],
      function (err, result) {
        if (err) {
          console.log('Error sending email', err);
        } else {
          console.log('Email Sent Successfully', result);
        }
    });
  };
  
  module.exports = {
    send: send
  };

 var lib = require("./lib") 

// ES import/Export
import {inspect} from 'util';
import {main} from '../common.js';
import * as lib from './lib.js';


export function handler(event, context, callback) {
	console.log(`Reading options from event:\n${inspect(event, {depth: 5})}`);
}



console.log('DynamoDB Record: %j', record.dynamodb);
console.log('Reading options from event:\n', util.inspect(event, {
    depth: 5
}));


const contentType = 'image/jpeg';
const contentType = 'application/json';

exports.handler = async event => {
    const {
        imageUrl
    } = event;
    const body = await new Promise((resolve, reject) => {
        request.get(imageUrl, (error, _, body) => error ? reject(error) : resolve(body));
    });

    const image = await new Promise((resolve, reject) => {
        cv.readImage(body, (error, image) => error ? reject(error) : resolve(image));
    });

    return image;
}

var latestUploadDay = Object.keys(uploadDays).sort().pop();

var objKeys = Object.keys(image);

if ('isPublic' in image && 'uploadDay' in image) {
    var uploadDay = image.uploadDay.S;
    uploadDays[uploadDay] = true;
    console.log('Public content found for ' + uploadDay);
}

JSON.stringify(input)
JSON.parse(data.Payload);

function startsWith(text, prefix) {
    return (text.lastIndexOf(prefix, 0) === 0)
}

myArray.forEach((record) => {
    console.log(record.eventID);
    console.log(record.eventName);
});

myArray.push({
    identityId: item.identityId.S,
    objectKey: item.objectKey.S,
    thumbnailKey: item.thumbnailKey.S,
    uploadDate: item.uploadDate.S,
    title: item.title.S,
    description: item.description.S
});



var srcBucket = event.Records[0].s3.bucket.name;

//unescape
var srcKey = unescape(event.Records[0].s3.object.key);

var dstKey = srcKey.replace(/content/, 'thumbnail');
var identityId = srcKey.match(/.*\/content\/([^\/]*)/)[1];


var dstKey = srcKey.replace(/content/, 'thumbnail');

var identityId = srcKey.match(/.*\/content\/([^\/]*)/)[1];

var typeMatch = srcKey.match(/\.([^.]*)$/);
if (!typeMatch) {
    callback('Unable to infer image type for key ' + srcKey);
}
var imageType = typeMatch[1]; //jpg png


//waterfall, async.apply
async.waterfall([createBucketParams, getVideosFromBucket, async.apply(createList, encoding)],
function (err, result) {
  if (err) {
    callback(createErrorResponse(500, err, event.encoding));
  } else {
    if (result.urls.length > 0) {
      callback(null, result);
    } else {
      callback(createErrorResponse(404, 'no files for the given encoding were found', event.encoding));
    }
  }
});


// Asynchronously runs a given function X times
const asyncAll = (opts) => {
    let i = -1;
    const next = () => {
        i++;
        if (i === opts.times) {
            opts.done();
            return;
        }
        opts.fn(next, i);
    };
    next();
};

const load = (event, callback) => {
    const payload = event.event;
    asyncAll({
        times: event.iterations,
        fn: (next, i) => {
            payload.iteration = i;
            const lambdaParams = {
                FunctionName: event.function,
                InvocationType: 'Event',
                Payload: JSON.stringify(payload)
            };
            lambda.invoke(lambdaParams, (err, data) => next());
        },
        done: () => callback(null, 'Load test complete')
    });
};




// https, querystring, encodeURI

var https = require('https');
var querystring = require("querystring");


var params = querystring.stringify({
    value1: output
});


https.get(encodeURI(iftttMakerUrl) + '?' + params, function (res) {
    console.log('Got response: ' + res.statusCode);
    res.setEncoding('utf8');
    res.on('data', function (d) {
        console.log('Body: ' + d);
    });
    callback(null, res.statusCode);
}).on('error', function (e) {
    console.log("Got error: " + e.message);
    callback(e.message);
});


var post_options = {
    hostname: webhook_host,
    port: 443,
    path: webhook_path,
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        'Content-Length': Buffer.byteLength(post_data)
    }
};

var post_req = https.request(post_options, function (res) {
    res.setEncoding('utf8');
    res.on('data', function (chunk) {
        console.log('Response: ' + chunk);
    });
});

post_req.write(post_data);
post_req.end();


// exec process
var exec = require('child_process').exec;

var cmd = 'bin/ffprobe -v quiet -print_format json -show_format "/tmp/' + localFilename + '"';

exec(cmd, function (error, stdout, stderr) {
    if (error === null) {
        var metadataKey = sourceKey.split('.')[0] + '.json';
        saveMetadataToS3(stdout, sourceBucket, metadataKey, callback);
    } else {
        console.log(stderr);
        callback(error);
    }
});


// read from S3 to a local file
var fileStream = fs.createWriteStream('/tmp/' + localFilename);
var stream = s3.getObject({Bucket: sourceBucket, Key: sourceKey}).createReadStream().pipe(fileStream);

stream.on('error', function(error){
    callback(error);
});

stream.on('close', function(){
    console.log("downloaded file to /tmp/" +localFilename);
});


var downloadFunc = function (bucket, key) {
    return s3
    .getObject({
        Bucket,
        Key
    })
    .on('error', (error) => Promise.reject(`S3 Download Error: ${error}`))
    .createReadStream();
};

function downloadFile({bucket, key}) {
	return new Promise((resolve, reject) => {
		log(`Starting download: ${bucket} / ${key}`);
		downloadFunc(bucket, key)
			.on('end', () => {
				log('Download finished');
				resolve();
			})
			.on('error', reject)
			.pipe(fs.createWriteStream(tmp_file));
	});
}


export async function main(logger, callback) {
	let error = null;
	try {
		await downloadFile(sourceLocation);
		await checkM3u(download);
		await ffprobe();
		await ffmpeg(keyPrefix);
		await Promise.all([
			removeFile(download),
			uploadFiles(keyPrefix)
		]);
	} catch (e) {
		error = e;
    }
    
	callback(error);
}


// request with retry
function request(url, toPipe, retries = 0) {
	const options = parse(url);
	options.headers = {
		'User-Agent': 'node'
	};

	return new Promise((resolve, reject) => {
		const req = https.get(options, response => {
			const {statusCode} = response;

			if (statusCode < 200 || statusCode > 299) {
				if (statusCode === 302)
					return request(response.headers.location, toPipe);

				if (statusCode === 403 && retries < 3)
					return new Promise((resolve, reject) => {
						const tryCount = retries + 1;

						console.log(`Request failed, retrying ${tryCount} of 3`);

						setTimeout(
							() => {
								return request(url, toPipe, tryCount)
									.then(resolve)
									.catch(reject);
							},
							3e3
						);
					});

				return reject(new Error('Failed to load page, status code: ' + response.statusCode));
			}

			let body = '';

			if (toPipe)
				response.pipe(toPipe);
			else
				response.on('data', chunk => body += chunk);

			response.on('end', () => resolve(body));
		});

		req.on('error', reject);
	});
}

/*
curl --request GET \
      --url 'https://YOUR_AUTH0_DOMAIN/userinfo' \
      --header 'Authorization: Bearer {ACCESS_TOKEN}' \
      --header 'Content-Type: application/json'
*/

var options = {
    url: 'https://' + process.env.DOMAIN + '/userinfo',
    method: 'GET',
    'auth': {
      'bearer': access_token
    }
  };

  console.log("request options:" + util.inspect(options));
  request(options, function(error, response, body) {
    if (!error && response.statusCode === 200) {
      callback(null, body);
    } else {
      if (response.statusCode !== 200) {
        console.log(
          `statusCode: ${response.statusCode}`
        );
      }
      callback(error);
    }
  });



function createErrorResponse(code, message, encoding) {
    var response = {
      'statusCode': code,
      'headers' : {'Access-Control-Allow-Origin' : '*'},
      'body' : JSON.stringify({'code': code, 'messsage' : message, 'encoding' : encoding})
    }
  
    return response;
  }
  
  function createSuccessResponse(result) {
    var response = {
      'statusCode': 200,
      'headers' : {'Access-Control-Allow-Origin' : '*'},
      'body' : JSON.stringify(result)
    }
  
    return response;
  }



  // https
  let https = require('https');

  function makeRequests(event, iteration, callback){
    
    const req = https.request(event.options, (res) => {						
        let body = '';
        console.log('Status:', res.statusCode);
        res.setEncoding('utf8');
        res.on('data', (chunk) => body += chunk);
        res.on('end', () => {
            console.log('Successfully processed HTTPS response, iteration: ', iteration);
            
            if (res.headers['content-type'] === 'application/json') {
                console.log(JSON.parse(body));
            }
        });
    });
        
    return req;
}

// async.parallel
var tasks = [];
  for (var i = 0; i < 200; i++) {
    let n = i;
    tasks.push(function(cb) {
      var req = makeRequests(event, n, cb);
      req.end();
    });
  }

  async.parallel(tasks, function(err, results) {
    console.log("results:" + JSON.stringify(results));
    if (err) {
      callback(err);
    } else {
      callback(null, results);
    }
  });


function generateExpirationDate() {
    var currentDate = new Date();
    currentDate = currentDate.setDate(currentDate.getDate() + 1);
    return new Date(currentDate).toISOString();
  }


function base64encode (value) {
    return new Buffer(value).toString('base64');
}