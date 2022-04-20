#!/bin/sh
set -e

java -Duser.country=MX -Duser.language=es -Djava.security.egd=file:/dev/./urandom -jar fineract-cn-otp-1.0.0.jar
