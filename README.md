ct-client
=========

java client for bacapi service

java -jar client.jar execute {verb} {URL Endpoint} {apiKey} {secretKey}

The client can also post the create account command
java -jar client.jar account:create register.json

Example of submitting a test check
java -jar client.jar execute POST yourRequest.json https://www.crowdtrust.com/bacapi/test/checks 5f60d9d4382c +DdR/yTZLyF2VnmUall0XafC/Vp6jKeNSm1o7H8i068=
