# telephony-server

The application tracks incoming and outgoing calls and provides call status and log via HTTP server in the local Wi-Fi network.

## Application instructions

In order to run the calls tracking, please, give the Application the required permissions in runtime. 
If necessary, please also select TelephonyServer as the default Call Screening application.

To run the server, make sure the device is connected to a Wi-Fi network. You can ensure the connection state on the main application screen.
If it shows "Connected to WiFi", then you're good to go.
Otherwise, if you see the message "Please, connect to a WiFi network", please check your connection.

Once you're good to go, press the START TRACKING CALLS button. It will enable calls tracking and also it will start the HTTP server.
You can find the server address and port on the application screen. For example:  
Running server: 192.168.50.175:2040

After that you may send requests to the server from devices on the same network using the given address.

Once you want to stop calls tracking and the HTTP server, press STOP TRACKING CALLS button.

## HTTP server API documentation

`/`  

Returns server start date and the list of provided services with uris in json format.  
#### Example:
```{
  "services": [
    {
      "name": "status",
      "uri": "http://192.168.50.175:2040/status"
    },
    {
      "name": "log",
      "uri": "http://192.168.50.175:2040/log"
    }
  ],
  "start": "2022-11-02T18:57:43+0400"
}
```

</br>

`/status`  

Returns status of an ongiong call if any.  
#### Example:
```
{
    "ongoing": true,
    "contactName": "John",
    "number": "+995555000000"
}
```

#### Schema:
```
{
    "$schema": "https://json-schema.org/draft/2019-09/schema",
    "$id": "http://example.com/example.json",
    "type": "object",
    "default": {},
    "title": "Root Schema",
    "required": [
        "ongoing",
        "contactName",
        "number"
    ],
    "properties": {
        "ongoing": {
            "type": "boolean",
            "default": false,
            "title": "The ongoing Schema",
            "examples": [
                true
            ]
        },
        "contactName": {
            "type": "string",
            "default": "",
            "title": "The contactName Schema",
            "examples": [
                "John"
            ]
        },
        "number": {
            "type": "string",
            "default": "",
            "title": "The number Schema",
            "examples": [
                "+995555000000"
            ]
        }
    },
    "examples": [{
        "ongoing": true,
        "contactName": "John",
        "number": "+995555000000"
    }, { "ongoing":false }]
}
```

</br>

`/log`

Returns logged calls.  

Each call contains the following information:  
name - Name of the caller if known in the phone contacts. If unknown, this field won't be sent;  
duration - Duration of the call in seconds;  
number - Phone number of the caller;  
Beggining date - Date and time the call was started;  
timesQueried - How many time this call has been returned in the log response before.  

#### Example:
```
[
  {
    "name": "John",
    "duration": 12,
    "number": "+995555000000",
    "beginning": "2022-11-02T19:04:20+0400",
    "timesQueried": 0
  },
  {
    "name": "John",
    "duration": 26,
    "number": "+995555000000",
    "beginning": "2022-11-02T18:58:51+0400",
    "timesQueried": 0
  },
  {
    "duration": 6,
    "number": "+7678934",
    "beginning": "2022-11-02T12:24:48+0400",
    "timesQueried": 2
  },
  {
    "name": "Alice",
    "duration": 10,
    "number": "+5555555",
    "beginning": "2022-11-02T12:14:54+0400",
    "timesQueried": 4
  }
]
```
