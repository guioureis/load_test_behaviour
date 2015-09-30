# Load tests for consumer behaviour API

The project was writen using Scala programming language. 
For the load test I used the stress tool Gatling.

## Setup development environment

Install VirtualBox and Vagrant in your machine (preferable Unix machines)

Inside project folder run:

`vagrant up`

This will provide a Linux virtual machine with the dependencies already installed.
Also, the project's folder will be synchronized with the folder '/vagrant' of virtual machine. It means that you can edit files from both places: your machine and also the virtual machine.

To access the virtual machine run:

`vagrant ssh`
`cd /vagrant`

## Setup the configuration

Edit the file `/vagrant/src/main/resources/application.conf`
Set the values for service base URL, username, password, etc.

## Running the Tests

Inside the /vagrant directory of virtual machine run the command:

`sbt`

### Preparing the hashes for tests

The class ClientTranslator is prepared to grab a cvs file with a sequence of CPFs and apply the hash algorith on it.
Configure the translator section of the `application.conf` file:

 Config | Description 
 -------- | -------- 
 cpf_input_file | a cvs file with a CPF per line. The base path is the project folder 
 hash_output_file | a cvs file that will be generated with the resultant hashs for the given CPF 
 provider | the provider that will be used on the hash 

Then run the following command inside `sbt` terminal

`gatling:testOnly *ClientTranslator*`

