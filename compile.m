function success = compile()
jPath = fullfile(matlabroot,'java','jarext');
cp = [fullfile(jPath,'jogl.jar') pathsep fullfile(jPath,'gluegen-rt.jar')];
cmd = ['javac -cp "' cp '" View3.java'];
retval = system(cmd,'-echo');
success = (retval==0);
javaaddpath(pwd);
