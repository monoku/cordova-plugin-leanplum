var exec = require('cordova/exec');

exports.start = function(data, success, error) {
    const options = [];
    exec(success, error, "NSLeanplum", "start", options);
};
