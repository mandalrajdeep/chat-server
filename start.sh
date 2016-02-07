#/bin/bash

echo $$>>pid.txt 

ant > log-$(date +%F_%R)
