#!/bin/bash

if test \! -x ./generate.sh
then
  echo "must be run from the resources directory"
  exit 1
fi
if test -z "$(command -v openssl)"
then
  echo "Need openssl."
  exit 1
fi

PASS="foobar"
VALIDITY_DAYS="3650"

echo "Generating root ca."
openssl req -new -x509 -days "$VALIDITY_DAYS" -keyout ca.key -out ca.pem -subj "/C=NO/CN=CA" -passout "pass:$PASS"

CERT=signed-cert
cat > conf.conf <<EOT
[v3_ca]
basicConstraints = CA:FALSE
keyUsage = digitalSignature, keyEncipherment
EOT
openssl genrsa -out "$CERT.key" 2048
openssl req -new -subj "/CN=$CERT" -key "$CERT.key" -out "$CERT.csr"
openssl x509 -req -CA ca.pem -CAkey ca.key -in "$CERT.csr" -out "$CERT.pem" -extensions client -extensions v3_ca -extfile ./conf.conf -days "$VALIDITY_DAYS" -CAcreateserial -passin "pass:$PASS"

rm -- *.key *.csr *.srl conf.conf
