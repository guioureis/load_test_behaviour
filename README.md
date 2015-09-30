# Load tests for consumer behaviour API

The project was writen using Scala programming language. 
For the load test I used the stress tool Gatling.

## Setup development environment

Install VirtualBox and Vagrant in your machine (preferable Unix machines)

Inside project folder run:

'vagrant up'

This will provide a Linux virtual machine with the dependencies already installed.
Also, the project's folder will be synchronized with the folder '/vagrant' of virtual machine. It means that you can edit files from both places: your machine and also the virtual machine.

To access the virtual machine run:

'vagrant ssh' 
'cd /vagrant'

## Running the Tests

Inside the /vagrant directory of virtual machine run the command:

'sbt'

### Preparing the hashes for tests



