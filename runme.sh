#!/bin/bash
rm -rf *.png
export MATLAB=/Applications/MATLAB_R2013a.app
export MATLABJ="$MATLAB/java/jarext"
export CLASSPATH="$MATLABJ/jogl.jar:$MATLABJ/gluegen-rt.jar:."
javac *.java
java -Djava.library.path=$MATLAB/bin/maci64 OpenGLCanvas
open *.png
