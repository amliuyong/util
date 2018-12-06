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


//----------------------------//

console.log('DynamoDB Record: %j', record.dynamodb);
console.log('Reading options from event:\n', util.inspect(event, {
    depth: 5
}));

var latestUploadDay = Object.keys(uploadDays).sort().pop();

var objKeys = Object.keys(image);

if ('isPublic' in image  && 'uploadDay' in image) {
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
