**For educational purpose only**

This is a very simple implementation of Servlet container.

Dependencies: **docker**

To run:
1. Place ***.war*** file to 'deploy' folder
1. Install docker if you don't have it
1. Build application: ***docker run -it --rm --name binary-cat -v ~/.m2/repository:/root/.m2/repository -v "$(pwd)":/usr/src/binary-cat -w /usr/src/binary-cat maven:3.6.3-openjdk-15 mvn clean package***
1. Build docker image: ***docker build -t binary-cat .*** 
1. Run docker container: ***docker run -it --rm --cpus=1 --memory=64m -p 8080:8080 binary-cat***
1. Use your browser or curl to invoke servlet from your ***.war*** file. Servlet container is listening on port 8080. 

Mission:
1. Look at the code and understand what it does.
1. The code is as simple and straightforward as it could be. It has a bugs, spaghetti code, wrong exceptions handling, code duplicates, and etc. Find them and fix them.
1. As you may see, Binary Cat does not cover all Servlet specification :-) Improve it.