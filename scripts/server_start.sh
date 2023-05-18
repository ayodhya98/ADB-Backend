#!/usr/bin/env bash
cd /home/ubuntu/server
sudo java -jar -Dserver.port=80 \
    *.jar > ./application.log 2>&1 &

