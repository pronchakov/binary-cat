**For educational purpose only**

This is a very simple implementation of Servlet container.

#### To run using IDE:

Dependencies: ***IDE with maven, JDK 15***
1. Open project in your IDE
1. Run edu.servletcontainer.binarycat.Main class

#### To run using docker:

Dependencies: **docker**
1. Place ***.war*** file to 'deploy' folder
1. Install docker if you don't have it
1. Build application: ***docker run -it --rm --name binary-cat --user $(id -u):$(id -g) -v ~/.m2/repository:/tmp/.m2/repository -v $(pwd):/usr/src/binary-cat -w /usr/src/binary-cat maven:3.6.3-openjdk-15 mvn clean package -Dmaven.repo.local=/tmp/.m2/repository***
1. Build docker image: ***docker build -t binary-cat .*** 
1. Run docker container: ***docker run -it --rm --cpus=1 --memory=64m -p 8080:8080 binary-cat***
1. Use your browser or curl to invoke servlet from your ***.war*** file. Servlet container is listening on port 8080. 

#### Assignment:
1. Look at the code and understand what it does.
1. The code is as simple and straightforward as it could be. It has a bugs, spaghetti code, wrong exceptions handling, code duplicates, and etc. Find them and fix them.
1. As you may see, Binary Cat does not cover all Servlet specification :-) Improve it.