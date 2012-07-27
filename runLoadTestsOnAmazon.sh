#!/bin/bash

ec2-run-instances ami-4f4c8a26 -g sshable -k default2.keypair -t t1.micro


while [ -z "$instance" ]
do
instance=`ec2-describe-instances -F instance-state-name=running`
fields=( $instance )
echo "Waiting for EC2 instance to start"
sleep 5
done
echo ${fields[7]}

echo "Host ec2" > ~/.ssh/config
echo "HostName ${fields[7]}" >> ~/.ssh/config
echo "User ec2-user" >> ~/.ssh/config
echo "IdentityFile ~/.ssh/default2.pem" >> ~/.ssh/config
echo "StrictHostKeyChecking no" >> ~/.ssh/config

sleep 120
# ssh ec2 "\"rm -r ./LoadTestingFramework\""
ssh ec2 "sudo yum -y install git"
ssh ec2 "git clone https://github.com/appfolio/LoadTestingFramework.git"
ssh ec2 "cd LoadTestingFramework;java Starter"

ec2-terminate-instances ${fields[5]}