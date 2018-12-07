# Socidia
A nice cake

DEVELOPING ENVIRONMENT SETUP

VirtualBox: Ubuntu 14.04 VM

If ran into apt-get install issue
  most likely is relating to vi /etc/apt/sources.list
  https://www.velocihost.net/clients/knowledgebase/29/Fix-the-apt-get-install-error-Media-change-please-insert-the-disc-labeled--on-your-Linux-VPS.html

  comment out 'cdrom' line

  and make sure:

  deb http://archive.ubuntu.com/ubuntu trusty main universe restricted muliverse

Install: ssh
Command:
  'sudo apt-get update'
  'sudo apt-get upgrade'
  'sudo apt-get install openssh-server'

Install: pip
Command: 'sudo apt-get install python-pip'

This command install 2.4.5 for ubuntu 14.04
Install: python-psycopg2
Command: sudo apt-get install python-psycopg2

Install: pip
Command: 'sudo apt-get install python-pip'

Install: run this command first before pip install requirments.txt
Command: 'sudo apt install libpq-dev python-dev'

Install: requirement.txt
Command: sudo pip install -r requirments.txt

Alias Editing:
add to '~/.bashrc'
reload by 'source ~/.bashrc'
