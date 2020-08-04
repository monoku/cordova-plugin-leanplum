## cordova-plugin-leanplum
Use Leanplum Plugin in Cordova projects

## Table of contents

- [Installation](#installation)
- [Usage](#usage)
- [Callbacks](#callbacks)
- [Leanplum SDK](#leanplum-sdk)

## The SDK methods implemented

- [x] [start](#start)
- [x] [setAppIdForDevelopmentMode](#set-leanplum-credentials-for-development)
- [x] [setAppIdForProductionMode](#set-leanplum-credentials-for-production)
- [x] [setUserId](#set-userid)
- [x] [track](#track-events)
- [x] [setVariables](#set-variables)
- [x] [getVariable](#get-a-variable)
- [x] [getVariables](#get-variables)
- [x] [setUserAttributes](#set-user-attributes)
- [ ] advance
- [ ] pauseState
- [ ] resumeState
- [ ] trackAllAppScreens
- [ ] setDeviceId
- [ ] inbox

## The SDK callbacks implemented
- [x] [onStartResponse](#onstartresponse)
- [x] [onValueChanged](#onvaluechanged)
- [x] [onVariablesChanged](#onvariableschanged)
- [ ] onFileReady
- [ ] onVariablesChangedAndNoDownloadsPending

## Installation

```bash
$ cordova plugin add https://github.com/monoku/cordova-plugin-leanplum.git --save
```

## Usage

### Set Leanplum credentials for development
```js

const { NSLeanplum } = window.cordova.plugins;

NSLeanplum.setAppIdForDevelopmentMode(<appId>, <setAppIdForProductionMode>, success, error);

```

### Set leanplum credentials for production
```js

const { NSLeanplum } = window.cordova.plugins;

NSLeanplum.setAppIdForProductionMode(<appId>, <setAppIdForProductionMode>, success, error);

```

### Set variables

```js

const { NSLeanplum } = window.cordova.plugins;

NSLeanplum.setVariables({
  stringVar: 'Some string variable',
  numVar: 1,
  boolVar: true,
  mapVar: {
    stringVal: 'some string val',
    'numVal:': 5,
  },
  listVar: [1, 2, 3],
});

```

### Start

```js

const { NSLeanplum } = window.cordova.plugins;

NSLeanplum.start();

```

### Set userId

```js

const { NSLeanplum } = window.cordova.plugins;

NSLeanplum.setUserId(id, success, error);

```
### Set user attributes

```js

const { NSLeanplum } = window.cordova.plugins;

NSLeanplum.setUserAttributes({
  name: 'My name',
  email: 'foo@example.com',
  age: 20,
});

```

### Track events

```js

const { NSLeanplum } = window.cordova.plugins;

NSLeanplum.track('Purchase', {itemCategory: 'Apparel', itemName: 'Shoes'});

```

### Get a variable
Get a specific variable
```js

const { NSLeanplum } = window.cordova.plugins;

NSLeanplum.getVariable('variable_name', (data) => {
  console.log(data.value) // variable_value
});

```

### Get variables
Get list of variables

```js

const { NSLeanplum } = window.cordova.plugins;

NSLeanplum.getVariables((variables) => {
  console.log(variables) // { stringVar: 'Some string variable', numVar: 1, ... }
});

```

## Callbacks
Because Leanplum variables and resources are retrieved from the server asynchronously after `start`, you need to wait for the values to be downloaded before using them in your code. The proper way to do this is to use one of these callbacks.

### onStartResponse
This callback is executed only when the start call finishes and all variables and files are returned from the Leanplum server. 

```js

const { NSLeanplum } = window.cordova.plugins;

NSLeanplum.onStartResponse(() => {
  console.log('start finished');
});

```

### onValueChanged
This callback is executed after start every time, but only after `forceContentUpdate` if a specific variable has changed.

```js

const { NSLeanplum } = window.cordova.plugins;

NSLeanplum.onValueChanged('variable_name', (data) => {
  console.log(data.value) // new_variable_value
});

```
### onVariablesChanged
This callback is executed after start every time, but only after `forceContentUpdate` if any variable has changed.
```js

const { NSLeanplum } = window.cordova.plugins;

NSLeanplum.onVariablesChanged((variables) => {
  console.log(variables) // { stringVar: 'Some string variable', numVar: 1, ... }
});
```

## Leanplum SDK

https://docs.leanplum.com/reference
