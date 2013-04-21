A collection of useful Java utilities / code snippets.

The source can be obtained from
https://github.com/lucastan/libjava

Building
========
1. Install :
    - Apache ANT : http://ant.apache.org/
    - ANT-contrib: http://ant-contrib.sourceforge.net/

2. Do `ant dist` to build the entire distribution.

This will create a `dist-{ver}` dir with the following jar files:
- libjava-{ver}.jar : 
    Compiled classes only.

- libjava-{ver}-all.jar : 
    Compiled classes with all dependencies packaged in one jar.

- libjava-{ver}-test.jar : 
    Compiled test classes with test-specific dependencies.
    This requires `libjava-{ver}-all.jar` to be in the same dir to run.

Optional:

1. Do `ant test` to build and run the tests.
2. Do `ant clean` to clean up, that is, remove the `dist` and `class` dirs and
the distribution zip file.

Documentation
=============
Do `ant dist` or `ant doc` to build the documentation. 
Then open `dist-{ver}/doc/index.html` using your web browser.

Packages
========
Non-exhaustive list of package descriptions:

- j.algo :
    - UnionFind

- j.collections:
    - Queue and stack based on efficient circular array. 
    - Ordered pair (2-tuple)

- j.io :
    - IO streams and utility classes
    - line and column number text file reader.
    - HTTP chunked input/ouput stream.
    - FileUtil: read entire text files

- j.opt :
    - Command line parsing library based on Java annotations.
    - Powerful and easy to use and configure.
    - See https://github.com/lucastan/proxyServer/blob/master/src/j/net/proxy/ProxyServer.java 
    for a usage example.

- j.util :
    - Clipboard utilities
    - Shuffle native arrays
 
