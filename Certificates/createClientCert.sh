#!/bin/sh
echo "\nCreating new certificate\n"
read -p "Name: " name
read -s -p "Password: " pass
echo "\n"
mkdir $name

## import CA to truststore
echo "ja" | keytool -import -file CA.crt -alias ca -keystore truststore -storepass $pass

## generate keypair
keytool -genkeypair -keystore keystore -alias $name -keypass $pass -storepass $pass -dname "CN=$ssn"

## create CSR
keytool -certreq -file certreq.crt -keystore keystore -alias $name -storepass $pass

## sign CSR using CA
openssl x509 -req -in certreq.crt -CA CA.crt -CAkey private.key -CAcreateserial -out $name.crt -passin pass:password

## import CA to keystore
echo "ja" | keytool -import -file CA.crt -alias ca -keystore keystore -storepass $pass

## import signed cert to keystore
keytool -import -file $name.crt -alias $name -keystore keystore -storepass $pass

rm certreq.crt
rm $name.crt
mv keystore $name
mv truststore $name
echo "\nNew user certificate created"