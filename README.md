# Xml To Json

## What does it do

This application was designed to convert your XML to a pretty one JSON.

## How to launch

1. To start the application you must have docker.
2. Go to project folder.
3. Open terminal and type ```gradle build```. This command will create a docker 
image "xml-to-json".
4. To run this image type ```docker run -p 80:80 xml-to-json```. This command 
will run application at port 80.

## How to test

Converter wait a POST request at port 80. To send such request we can just use
```curl```.

Open terminal and type next command 
```
curl -X POST -H "Content-type: application-xml" -d @yourXml.xml http://localhost:80
```
In response you will receive converted JSON or "Not valid" message if your XML
is not valid.