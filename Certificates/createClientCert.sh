#!/bin/sh
echo "\nCreating new certificate\n"
read -p "First name: " name
read -p "Last name: " lname
read -p "SSN-number: " ssn
read -s -p "Password: " pass
echo "\n"
mkdir $ssn

## import CA to client truststore
echo "ja" | keytool -import -file hospitalCA.crt -alias hospitalca -keystore clienttruststore -storepass $pass

## generate keypair for client
keytool -genkeypair -keystore clientkeystore -alias client -keypass $pass -storepass $pass -dname "CN=$ssn"

## create CSR
keytool -certreq -file certreq.crt -keystore clientkeystore -alias client -storepass $pass

## sign CSR using CA
openssl x509 -req -in certreq.crt -CA hospitalCA.crt -CAkey private.key -CAcreateserial -out client.crt -passin pass:password

## import CA to client keystore
echo "ja" | keytool -import -file hospitalCA.crt -alias hospitalca -keystore clientkeystore -storepass $pass

## import signed client cert to client keystore
keytool -import -file client.crt -alias client -keystore clientkeystore -storepass $pass

rm certreq.crt
rm client.crt
mv clientkeystore $ssn
mv clienttruststore $ssn
echo "\nNew user certificate created"