#!/bin/sh

openssl req -new -x509 -sha256 -newkey rsa:4096 -passout pass:password -days 90 -keyout private.key -out CA.crt -subj "//CN=CA"
