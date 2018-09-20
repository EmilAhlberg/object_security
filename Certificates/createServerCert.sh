#!/bin/sh

mkdir server

## import CA to server truststore
echo "ja" | keytool -import -file hospitalCA.crt -alias hospitalca -keystore servertruststore -storepass password

## generate keypair for server
keytool -genkeypair -keystore serverkeystore -alias server -keypass password -storepass password -dname "CN=Hospital"

## create CSR
keytool -certreq -file serverreq.crt -keystore serverkeystore -alias server -storepass password

## sign CSR using CA
openssl x509 -req -in serverreq.crt -CA hospitalCA.crt -CAkey private.key -CAcreateserial -out server.crt -passin pass:password

## import CA to server keystore
echo "ja" | keytool -import -file hospitalCA.crt -alias hospitalca -keystore serverkeystore -storepass password

## import signed server cert to server keystore
keytool -import -file server.crt -alias server -keystore serverkeystore -storepass password

rm serverreq.crt
rm server.crt
mv serverkeystore server
mv servertruststore server