jPath = fullfile(matlabroot,'java','jarext');
cp = [fullfile(jPath,'jogl.jar') pathsep fullfile(jPath,'gluegen-rt.jar')];
cmd = ['javac -cp "' cp '" View3.java'];
system(cmd,'-echo');
javaaddpath(pwd);

% javaMethodEDT('main','HelloWorld','')