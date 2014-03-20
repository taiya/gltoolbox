#!/bin/bash

#--- OSX Matlab 2013a
# export MATLAB=/Applications/MATLAB_R2013a.app
# export MATLABJ="$MATLAB/java/jarext"
# export CLASSPATH="$MATLABJ/jogl.jar:$MATLABJ/gluegen-rt.jar:."

#--- OSX Matlab 2014a
export MATLAB=/Applications/MATLAB_R2014a.app
export MATLABJ="$MATLAB/java/jarext"
export CLASSPATH="$MATLABJ/jogl-all.jar:."

javac *.java
java -Djava.library.path=$MATLAB/bin/maci64 OpenGLCanvas
