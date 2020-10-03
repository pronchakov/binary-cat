**For educational purpose only**

This is a very simple implementation of Servlet container.

Dependencies: **docker**

To run:
1. Place ***.war*** file to 'deploy' folder
2. Install docker if you don't have it
3. Build docker image: ***docker build -t binary-cat .*** 
4. Run docker container: ***docker run -it --rm -p 8080:8080 binary-cat***
5. Use your broswer or curl to invoke servlet from your ***.war*** file 

Mission:
1. Look at the code and understand what it does.
2. The code is as simple and straightforward as it could be. It has a bugs, spaghetti code, wrong exceptions handling, code duplicates, and etc. Find them and fix them.
3. As you may see, Binary Cat does not cover all Servlet specification :-) Improve it.